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
import dev.nm.algebra.linear.matrix.doubles.factorization.eigen.Eigen;
import dev.nm.algebra.linear.matrix.doubles.factorization.eigen.EigenProperty;
import dev.nm.algebra.linear.matrix.doubles.factorization.eigen.EigenDecomposition;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import dev.nm.algebra.linear.vector.doubles.Vector;
import dev.nm.algebra.linear.vector.doubles.dense.DenseVector;
import dev.nm.algebra.linear.vector.doubles.operation.VectorFactory;
import dev.nm.algebra.linear.matrix.doubles.operation.MatrixFactory;

public class scsCone {

    /**
     * It defines a class of any Cartesian product of the following primitive
     * cones
     */
    int zero;
    /* dimension of Zero Cones */
    int free;
    /* dimension of free Cones */
    int pos;
    /* dimension of positive orthant cones */

    /**
     * Second-order Cone
     */
    int[] q;
    /* dimension of each second-order cone */
    int q_size;
    /* number of primitive second-order cones */
    /**
     * Positive Semidefinite Cone
     */
    int[] s;
    /* array of SD constraints */
    int s_size;
    /* length of SD array */
    /**
     * Exponential Cone, Dual Exponential Cone
     */
    int ep;
    /* number of primal exponential cone triples */
    int ed;
    /* number of dual exponential cone triples */
    /**
     * Power Cone, dual Power Cone
     */
    float[] p;
    /* array of power cone params, must be \in [-1, 1],
                    negative values are interpreted as specifying the
                    dual cone */
    int p_size;

    /* number of (primal and dual) power cone triples */

    public scsCone(int zz, int freec, int poss, int[] qq, int qq_size,
            int[] ss, int ss_size, int epp, int edd, float[] pp, int pp_size) {

        zero = zz;
        free = freec;
        pos = poss;
        q = qq;
        q_size = qq_size;
        s = ss;
        s_size = ss_size;
        ep = epp;
        ed = edd;
        p = pp;
        p_size = pp_size;
    }

    public Vector dualProjection(Vector p) {

        /**
         * Vectors to store the projections
         */
        Vector zeroProj = null;
        Vector freeProj = null;
        Vector posProj = null;
        Vector soProj = null;
        Vector psProj = null;
        Vector exProj = null;
        Vector exDualProj = null;

        /**
         * Zero Cone - dual is Free Cone
         * Nothing needed to be done
         */
        if (zero > 0) {
            zeroProj = VectorFactory.subVector(p, 1, zero);
        }

        /**
         * Free Cone - dual is Zero Cone
         */
        if (free > 0) {
            freeProj = VectorFactory.subVector(p, zero + 1, zero + free);
            freeProj = freeProj.ZERO();
        }

        /**
         * Non-negative Orthant - self-dual Cone
         */
        if (pos > 0) {
            posProj = VectorFactory.subVector(p, zero + free + 1, zero + free + pos);
            for (int a = 1; a <= posProj.size(); ++a) {
                if (posProj.get(a) < 0) {
                    posProj.set(a, 0);
                }
            }
        }

        /**
         * Second-order cone
         */
        int len = 0;
        if (q_size > 0) {

            Vector[] proj = new Vector[q_size];
            for (int i = 0; i < q_size; i++) {
                proj[i] = VectorFactory.subVector(p, zero + free + pos + 1 + len,
                        zero + free + pos + q[i] + len);
                proj[i] = soConeProj(proj[i]);
                len += q[i];
            }
            for (int i = 0; i < q_size; i++) {
                soProj = VectorFactory.concat(soProj, proj[i]);
            }
            assert (soProj.size() == len);
        }

        /**
         * Semidefinite Cone - self dual
         */
        if (s_size > 0) {
            psProj = VectorFactory.subVector(p, zero + free + pos + len + 1,
                    zero + free + pos + len + s_size * s_size);
            Matrix A = new DenseMatrix(s_size, s_size);

            for (int i = 1; i <= s_size; i++) {
                for (int j = 1; j <= s_size; j++) {
                    A.set(i, j, psProj.get((i - 1) * s_size + j));
                }
            }
            /**
             * Eigenvalue Decomposition of Matrix
             */
            EigenDecomposition a = new EigenDecomposition(A, 1e-3);

            Eigen b = new Eigen(A, Eigen.Method.QR, 1e-3);
            double[] eigenvalues = b.getRealEigenvalues();

            Matrix matrixProj = new DenseMatrix(s_size, s_size);
            for (double val : eigenvalues) {
                /**
                 * Leave out those with negative eigenvalues
                 */
                if (val > 0) {
                    EigenProperty property = b.getProperty(val);
                    Vector eigenVec = property.eigenVector();

                    Matrix row = MatrixFactory.cbind(eigenVec);
                    Matrix col = MatrixFactory.rbind(eigenVec);
                    matrixProj.add(row.multiply(col));
                }
            }
            for (int i = 1; i <= s_size; i++) {
                for (int j = 1; j <= s_size; j++) {
                    psProj.set((i - 1) * s_size + j, matrixProj.get(i, j));
                }
            }
        }

        /**
         * Exponential Cone
         * dual is Exponential Dual Cone
         */
        if (ep > 0) {
            for (int i = 0; i < ep; i++) {
                Vector proj = VectorFactory.subVector(p, zero + free + pos + len + s_size * s_size + 1 + i * 3,
                        zero + free + pos + len + s_size * s_size + (i + 1) * 3);
                proj = expDualConeProj(proj);
                exProj = VectorFactory.concat(exProj, proj);
            }
        }

        /**
         * Exponential Dual Cone
         * Dual is Exponential Cone
         */
        if (ed > 0) {
            for (int i = 0; i < ed; i++) {
                Vector proj = VectorFactory.subVector(p, zero + free + pos + len + s_size * s_size + ep * 3 + 1 + i * 3,
                        zero + free + pos + len + s_size * s_size + ep * 3 + (i + 1) * 3);
                proj = expConeProj(proj);
                exDualProj = VectorFactory.concat(exDualProj, proj);
            }
        }

        return VectorFactory.concat(zeroProj, freeProj, posProj,
                soProj, psProj, exProj, exDualProj);
    }

    private Vector expDualConeProj(Vector expDual) {
        /**
         * To be added
         */
        return expDual;
    }

    /**
     * Projection on a single Second-Order Cone
     */
    private Vector soConeProj(Vector so) {
        int length = so.size();
        double s = VectorFactory.subVector(so, 1, 1).get(1);
        Vector temp_v = VectorFactory.subVector(so, 2, length);
        double v_norm = temp_v.norm();
        Vector res = new DenseVector(length);
        if (v_norm <= -s) {
            res = so.ZERO();
        } else if (v_norm <= s) {
            res = so.scaled(1);
        } else if (v_norm >= Math.abs(s)) {
            Vector norm_v = new DenseVector(1, v_norm);
            temp_v = VectorFactory.concat(norm_v, temp_v);
            res = temp_v.scaled(0.5 * (1 + s / v_norm));
        }
        return res;
    }

    private Vector expConeProj(Vector exp) {
        double dim_f = exp.get(1);
        double dim_s = exp.get(2);
        double dim_t = exp.get(3);

        Vector proj2 = exp;

        if ((dim_f < 0 && -dim_f * Math.exp(dim_s / dim_t) <= Math.exp(1) * dim_t)
                || (dim_f == 0 && dim_s >= 0 && dim_t >= 0)) {
            proj2 = proj2.scaled(1);
        } else if ((dim_s < 0 && dim_s * Math.exp(dim_f / dim_t) >= dim_t)
                || (dim_f >= 0 && dim_s == 0 && dim_t <= 0)) {
            proj2 = proj2.ZERO();
        } else if (dim_f < 0 && dim_t < 0) {
            proj2 = new DenseVector(dim_f, 0, Math.max(0, dim_t));
        }
        return proj2;
    }

    public scsCone(scsCone other) {
        zero = other.zero;
        free = other.free;
        pos = other.pos;
        q = other.q;
        q_size = other.q_size;
        s = other.s;
        s_size = other.s_size;
        ep = other.ep;
        ed = other.ed;
        p = other.p;
        p_size = other.p_size;
    }

}
