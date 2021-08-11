/*
 * Copyright (c) NM LTD.
 * https://www.nm.dev/
 * 
 * THIS SOFTWARE IS LICENSED, NOT SOLD.
 * 
 * YOU MAY USE THIS SOFTWARE ONLY AS DESCRIBED IN THE LICENSE.
 * IF YOU ARE NOT AWARE OF AND/OR DO NOT AGREE TO THE TERMS OF THE LICENSE,
 * DO NOT USE THIS SOFTWARE.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITH NO WARRANTY WHATSOEVER,
 * EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION,
 * ANY WARRANTIES OF ACCURACY, ACCESSIBILITY, COMPLETENESS,
 * FITNESS FOR A PARTICULAR PURPOSE, MERCHANTABILITY, NON-INFRINGEMENT, 
 * TITLE AND USEFULNESS.
 * 
 * IN NO EVENT AND UNDER NO LEGAL THEORY,
 * WHETHER IN ACTION, CONTRACT, NEGLIGENCE, TORT, OR OTHERWISE,
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIMS, DAMAGES OR OTHER LIABILITIES,
 * ARISING AS A RESULT OF USING OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dev.nm.nmj;

import dev.nm.algebra.linear.matrix.doubles.Matrix;
import dev.nm.algebra.linear.matrix.doubles.linearsystem.LSProblem;
import dev.nm.algebra.linear.matrix.doubles.linearsystem.LUSolver;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.diagonal.DiagonalMatrix;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.sparse.CSRSparseMatrix;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.sparse.SparseMatrix;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.sparse.SparseVector;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.sparse.solver.iterative.ConvergenceFailure;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.sparse.solver.iterative.nonstationary.ConjugateGradientSolver;
import dev.nm.algebra.linear.matrix.doubles.operation.Inverse;
import dev.nm.algebra.linear.matrix.doubles.operation.MatrixFactory;
import dev.nm.algebra.linear.vector.doubles.Vector;
import dev.nm.algebra.linear.vector.doubles.dense.DenseVector;
import dev.nm.algebra.linear.vector.doubles.operation.VectorFactory;
import dev.nm.misc.algorithm.iterative.monitor.IterationMonitor;
import dev.nm.misc.algorithm.iterative.monitor.VectorMonitor;
import dev.nm.misc.algorithm.iterative.tolerance.AbsoluteTolerance;
import dev.nm.misc.algorithm.iterative.tolerance.Tolerance;

public class scsSolver {

    /**
     * Objective function cTx, bTy
     */
    private Vector c;
    private Vector b;

    /**
     * Solutions
     */
    private Vector s;
    private Vector x;
    private Vector y;

    /**
     *
     */
    private CSRSparseMatrix A;

    /**
     * Dimension of x, r, c
     */
    private int N;

    /**
     * Dimension of y, s, b
     */
    private int M;

    /**
     * Cone K
     * Important: K can be any Cartesian product of the following primitive
     * cones
     */
    private scsCone K;
    private int iteration_count = 0;

    public scsSolver(Vector cc, Vector bb, Vector ss, Vector xx, Vector yy,
            CSRSparseMatrix AA, int n, int m, scsCone KK) {
        c = cc;
        b = bb;
        s = ss;
        x = xx;
        y = yy;
        A = AA;
        N = n;
        M = m;
        K = new scsCone(KK);
    }

    /**
     * Initial preparation to generate u,v,Q
     */
    private Vector prep_u(double tau) {
        Vector tauu = new DenseVector(1, tau);
        Vector u = VectorFactory.concat(x, y, tauu);

        return u;
    }

    private Vector prep_v(double kappa) {
        Vector kap = new DenseVector(1, kappa);
        /**
         * Dual variable r
         */
        Vector r = new DenseVector(N, 0);
        Vector v = VectorFactory.concat(r, s, kap);

        return v;
    }

    private SparseMatrix prep_M() {
        SparseMatrix At = A.t();
        SparseMatrix zero_n = new CSRSparseMatrix(N, N);
        SparseMatrix zero_m = new CSRSparseMatrix(M, M);
        SparseMatrix M_up = MatrixFactory.cbind(zero_n, At);
        SparseMatrix M_down = MatrixFactory.cbind(A.scaled(-1), zero_m);
        SparseMatrix MM = MatrixFactory.rbind(M_up, M_down);
        Matrix I = new DiagonalMatrix(M + N);
        for (int i = 1; i <= M + N; i++) {
            I.set(i, i, 1);
        }
        /**
         * M is skew symmetric
         */
        SparseMatrix II = new CSRSparseMatrix(I);
        SparseMatrix MM_I = new CSRSparseMatrix(MM.add(II));
        return MM_I;
    }

    /**
     * Parameter indicating different types of cones
     * default is 0 = Second order cone
     * 1 = Non-negative orthant
     * 2 = Positive semi-definite cone
     * 3 = Exponential cone
     */

    private double[] Iteration(double precision) throws ConvergenceFailure {
        double tau = 1;
        double kappa = 0;
        Vector u = prep_u(tau);
        Vector v = prep_v(kappa);

        Vector u_prime;
        SparseVector h = new SparseVector(VectorFactory.concat(c, b));
        /**
         * ADMM method to update our solution
         */

        /** Warm Start */
        SparseMatrix MM = prep_M();
        Inverse mInv = new Inverse(MM, 1e-7);
        Vector cache = mInv.multiply(h);
        double scale = h.innerProduct(cache);

        Vector subu_prime, step2, step2_x, step2_y, step2_tau;

        double third = tau + kappa, scales;
        Vector u_third = new DenseVector(1, third);

        LUSolver directSol = new LUSolver();
        Tolerance t = new AbsoluteTolerance(1e-3);
        ConjugateGradientSolver indirectSol = new ConjugateGradientSolver(20, t);
        LSProblem prob;
        Vector subuv;

        /**
         * Termination criterion:
         * ||Ax + s - b|| <= e(1+||b||), ||Ay + c|| <= e(1+||c||), |cx + by| <=
         * e(1+|cx|+|by|)
         */
        Vector x_tau, y_tau, s_tau;
        Vector pk = A.multiply(x).add(s.minus(b));
        Vector dk = A.t().multiply(y).add(c);
        double gk = c.innerProduct(x) + b.innerProduct(y);
        IterationMonitor<Vector> monitor = new VectorMonitor();

        /** Start iteration */
        while (pk.norm() > precision * (1 + b.norm()) || dk.norm() > precision * (1 + c.norm())
                || Math.abs(gk) > precision * (1 + Math.abs(c.innerProduct(x)) + Math.abs(b.innerProduct(y)))) {
            /**
             * Update Step 1: Solving linear system
             * Using LDL to solve
             */

            third = u.get(N + M + 1) + v.get(N + M + 1);
            subu_prime = cache.scaled(-third / (1 + scale));
            subuv = VectorFactory.subVector(u.add(v), 1, N + M);
            prob = new LSProblem(MM, subuv);
            subuv = directSol.solve(prob);
            //subuv = indirectSol.solve(prob, monitor).search(subuv);
            subu_prime = subu_prime.add(subuv);
            scales = h.innerProduct(subuv);
            subu_prime = subu_prime.add(cache.scaled(-scales / (1 + scale)));
            u_third.set(1, third + subu_prime.innerProduct(h));
            u_prime = VectorFactory.concat(subu_prime, u_third);

            /**
             * Update Step 2: Projection onto Cone
             * u(k+1) = projection of (u_prime - v) onto C
             * C = R^n x K* x R+
             */
            step2 = u_prime.minus(v);
            step2_x = VectorFactory.subVector(step2, 1, N);
            step2_y = VectorFactory.subVector(step2, N + 1, N + M);
            step2_tau = VectorFactory.subVector(step2, N + M + 1, N + M + 1);
            if (step2_tau.get(1) < 0) {
                step2_tau.set(1, 0);
            }
            step2_y = K.dualProjection(step2_y);
            u = VectorFactory.concat(step2_x, step2_y, step2_tau);

            /**
             * Update Step 3: Simple vector computation
             */
            v = (v.minus(u_prime)).add(u);

            /**
             * Compute new error after updating
             */
            double ss = u.get(N + M + 1);
            /** If tau > 0, Update error, else continue iteration */
            if (ss > 0) {
                x = VectorFactory.subVector(u, 1, N);
                y = VectorFactory.subVector(u, N + 1, N + M);
                s = VectorFactory.subVector(v, N + 1, M + N);
                x_tau = x.scaled(1 / ss);
                y_tau = y.scaled(1 / ss);
                s_tau = s.scaled(1 / ss);
                pk = A.multiply(x_tau).add(s_tau.minus(b));
                dk = A.t().multiply(y_tau).add(c);
                gk = c.innerProduct(x_tau) + b.innerProduct(y_tau);
            }

            iteration_count += 1;
        }

        double final_tau = u.get(M + N + 1);
        double final_kappa = v.get(M + N + 1);
        double[] certs = new double[]{final_tau, final_kappa};

        return certs;
    }

    public Vector[] getSolution(double precision) throws ConvergenceFailure {
        double[] certificates = Iteration(precision);

        if (certificates[0] > 0 && certificates[1] == 0) {
            Vector[] solutions = new Vector[]{x, y, s};
            for (int i = 0; i < solutions.length; i++) {
                solutions[i] = solutions[i].scaled(1 / certificates[0]);
                System.out.println("The primal-dual solution exists, return solution");
                return solutions;
            }
        } else if (certificates[0] == 0 && certificates[1] >= 0) {
            if (b.innerProduct(y) < 0 && c.innerProduct(x) < 0) {
                System.out.println("Primal and dual infeasible, return certificate of infeasibility");
                Vector[] cert = new Vector[]{y.scaled(1 / b.innerProduct(y)), x.scaled(-1 / c.innerProduct(x))};
                return cert;
            } else if (b.innerProduct(y) < 0) {
                System.out.println("Primal infeasible, return certificate of infeasibility");
                Vector[] cert = new Vector[]{y.scaled(1 / b.innerProduct(y))};
                return cert;
            } else if (c.innerProduct(x) < 0) {
                System.out.println("Dual infeasible, return certificate of infeasibility");
                Vector[] cert = new Vector[]{x.scaled(-1 / c.innerProduct(x))};
                return cert;
            } else {
                System.out.println("Nothing can be concluded about the original problem, but zero is always a solution");
                Vector cert = new DenseVector(certificates[0], certificates[1]);
                return new Vector[]{cert};
            }
        } else {
            System.out.println("Wrong value for tao and kappa");
            return null;
        }
        return null;
    }

    public int getIteration_count() {
        return iteration_count;
    }

    private Vector indirect(LUSolver sol, LSProblem p) {
        return null;
    }

    private Vector direct(ConjugateGradientSolver sol, LSProblem p) {
        return null;
    }
}
