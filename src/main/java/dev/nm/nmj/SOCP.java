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

import java.util.List;
import java.util.Arrays;

import dev.nm.algebra.linear.matrix.doubles.Matrix;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import dev.nm.algebra.linear.vector.doubles.Vector;
import dev.nm.algebra.linear.vector.doubles.dense.DenseVector;
import dev.nm.solver.IterativeSolution;
import dev.nm.solver.multivariate.constrained.convex.sdp.socp.interiorpoint.PrimalDualInteriorPointMinimizer;
import dev.nm.solver.multivariate.constrained.convex.sdp.socp.interiorpoint.PrimalDualSolution;
import dev.nm.solver.multivariate.constrained.convex.sdp.socp.problem.SOCPGeneralConstraint;
import dev.nm.solver.multivariate.constrained.convex.sdp.socp.problem.SOCPGeneralProblem;

public final class SOCP {

    /**
     * The objective function.
     */
    public static final Vector f = new DenseVector(1., 0., 0., 0., 0.);
    /**
     * The A's in the conic constraints.
     */
    public static final Matrix A1t = new DenseMatrix(new double[][]{
        {0, -1, 0, 1, 0},
        {0, 0, 1, 0, -1}
    });
    public static final Matrix A2t = new DenseMatrix(new double[][]{
        {0, 0.5, 0, 0, 0},
        {0, 0, 1, 0, 0}
    });
    public static final Matrix A3t = new DenseMatrix(new double[][]{
        {0, 0, 0, -0.7071, -0.7071},
        {0, 0, 0, -0.3536, 0.3536}
    });
    /**
     * The b's in the conic constraints.
     */
    public static final Vector b1 = f;
    public static final Vector b2 = f.ZERO();
    public static final Vector b3 = f.ZERO();
    /**
     * The c's in the conic constraints.
     */
    public static final Vector c1 = new DenseVector(2);//zero
    public static final Vector c2 = new DenseVector(-0.5, 0.);
    public static final Vector c3 = new DenseVector(4.2426, -0.7071);
    /**
     * The d's in the conic constraints.
     */
    public static final double[] d = new double[]{0., 1, 1};
    private static final List<SOCPGeneralConstraint> constraints = Arrays.asList(
            new SOCPGeneralConstraint(A1t.t(), c1, b1, d[0]),
            new SOCPGeneralConstraint(A2t.t(), c2, b2, d[1]),
            new SOCPGeneralConstraint(A3t.t(), c3, b3, d[2]));
    /**
     * The SOCP problem to be solved.
     */
    public static final SOCPGeneralProblem problem = new SOCPGeneralProblem(f, constraints);

    private SOCP() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("This class demonstrates how to solve Second-order cone programming "
                + "problems in Java using SuanShu.");
        solveSOCP_0010(problem, 0.00001, 20);
        solveSOCP_0020(problem, 0.00001, 20);
    }

    /**
     * Uses interior point method to solve the given problem from the given
     * starting point.
     *
     * @param problem      the dual SDP problem that is to be solved
     * @param epsilon      the precision of the computed result
     * @param maxIteration the maximum number of iterations
     * @throws Exception if an error occurs solving the problem
     */
    public static void solveSOCP_0010(SOCPGeneralProblem problem, double epsilon, int maxIteration)
            throws Exception {
        /**
         * initial guess of solution
         */
        Vector x0 = new DenseVector(1, 0, 0, 0.1, 0, 0, 0.1, 0, 0);
        Vector s0 = new DenseVector(3.7, 1, -3.5, 1, 0.25, 0.5, 1, -0.35355, -0.1767);
        Vector y0 = new DenseVector(-3.7, -1.5, -0.5, -2.5, -4);
        PrimalDualSolution soln0 = new PrimalDualSolution(x0, s0, y0);
        PrimalDualInteriorPointMinimizer socp = new PrimalDualInteriorPointMinimizer(epsilon, maxIteration);
        IterativeSolution<PrimalDualSolution> soln = socp.solve(problem);
        soln.search(soln0);
        System.out.println("minimizer: " + soln.minimizer().y);
    }

    /**
     * Uses interior point method to solve the given problem from the initials
     * as in SDPT3.
     * Users do not provide initial guess.
     *
     * @param problem      the dual SDP problem that is to be solved
     * @param epsilon      the precision of the computed result
     * @param maxIteration the maximum number of iterations
     * @throws Exception if an error occurs solving the problem
     */
    public static void solveSOCP_0020(SOCPGeneralProblem problem, double epsilon, int maxIteration)
            throws Exception {
        PrimalDualInteriorPointMinimizer socp = new PrimalDualInteriorPointMinimizer(epsilon, maxIteration);
        IterativeSolution<PrimalDualSolution> soln = socp.solve(problem);
        soln.search();
        System.out.println("minimizer: " + soln.minimizer().y);
    }
}
