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
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.sparse.CSRSparseMatrix;
import dev.nm.algebra.linear.vector.doubles.Vector;
import dev.nm.algebra.linear.vector.doubles.dense.DenseVector;

public class scsTest {

    public static void main(String[] args) throws Exception {

        /**
         * Test case 1
         */
        int n = 5;
        int m = 9;

        Vector c = new DenseVector(1.0, 0.0, 0.0, 0.0, 0.0);
        Vector b = new DenseVector(.0, .0, .0, 1, -0.5, 0, 1, 4.2426, -0.7071);

        /** Initial Guesses
         *
         */
        Vector s = new DenseVector(3.7, 1, -3.5, 1, 0.25, 0.5, 1, -0.35355, -0.1767);
        Vector y = new DenseVector(1, 0, 0, 0.1, 0, 0, 0.1, 0, 0);
        Vector x = new DenseVector(3.7, 1.5, 0.5, 2.5, 4);

        Matrix A = new DenseMatrix(new double[][]{
            {-1, 0, 0, 0, 0}, {0, 1, 0, -1, 0}, {0, 0, -1, 0, 1},
            {0, 0, 0, 0, 0}, {0, -0.5, 0, 0, 0}, {0, 0, -1, 0, 0},
            {0, 0, 0, 0, 0}, {0, 0, 0, 0.7071, 0.7071}, {0, 0, 0, 0.3536, -0.3536}
        });
        int zero = 0;
        int free = 0;
        int pos = 0;
        int[] q = new int[]{3, 3, 3};
        int q_size = 3;
        int ss[] = null;
        int s_size = 0;
        int ep = 0;
        int ed = 0;
        float p[] = null;
        int p_size = 0;

        CSRSparseMatrix AA = new CSRSparseMatrix(A);
        scsCone K = new scsCone(zero, free, pos, q, q_size, ss, s_size, ep, ed, p, p_size);
        scsSolver solver = new scsSolver(c, b, s, x, y, AA, n, m, K);
        long startTime = System.nanoTime();
        Vector[] solutions = solver.getSolution(1e-3);
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("Total execution time in Java in millis: " + elapsedTime / 1000000);
        for (int i = 0; i < solutions.length; i++) {
            System.out.println(solutions[i]);
        }

        /**
         * Test case 2
         *
         *
         *
         * ProcessCBF getData = new ProcessCBF();
         *
         * int n = 6991;
         * int m = 6994;
         *
         * Matrix B = getData.constructB(6991, 5992);
         * Vector D = getData.constructD(5992);
         *
         * Matrix A = null;
         * Vector b = null;
         * Matrix Bi;
         * Matrix Ci = new DenseMatrix(1,6991);
         *
         * Vector bi = new DenseVector(1,0);
         * Vector Di;
         *
         * for(int i = 0; i<1002;i++){
         * if (i == 0){
         * Bi = MatrixFactory.subMatrix(B,1,6991,1,2994);
         * Bi = Bi.scaled(-1);
         * A = MatrixFactory.rbind(Ci,Bi.t());
         *
         * Di = VectorFactory.subVector(D,1,2994);
         * b = VectorFactory.concat(bi,Di);
         * } else if (i == 1001){
         * Bi = MatrixFactory.subMatrix(B,1,6991,3995,5992);
         * Bi = Bi.scaled(-1);
         * A = MatrixFactory.rbind(A,Ci,Bi.t());
         *
         * Di = VectorFactory.subVector(D,3995,5992);
         * b = VectorFactory.concat(b,bi, Di);
         * } else {
         * Bi = MatrixFactory.subMatrix(B,1,6991,2994+i,2994+i);
         * Bi = Bi.scaled(-1);
         * A = MatrixFactory.rbind(A,Bi.t(),Ci);
         *
         * Di = VectorFactory.subVector(D,2994+i,2994+i);
         * b = VectorFactory.concat(b,Di,bi);
         * }
         * }
         *
         * CSRSparseMatrix AA = new CSRSparseMatrix(A);
         * b = new SparseVector(b);
         * Vector c = new DenseVector(n, 0);
         * c.set(4993,1);
         *
         * //Initial Guesses
         * Vector x = new DenseVector(n,0);
         * for(int i = 1; i<=n; i++)
         * x.set(i,Math.random());
         *
         * Vector s = A.multiply(x).minus(b);
         * Vector y = new DenseVector(m,0.2);
         *
         * int[] qq = new int[1002];
         * for(int i = 1; i<1001; i++){
         * qq[i] = 2;
         * }
         * qq[0] = 2995;
         * qq[1001] = 1999;
         * scsCone K = new scsCone(0, 0, 0,qq ,
         * 1002, null, 0, 0,0, null, 0);
         *
         * scsSolver solver = new scsSolver(c, b, s, x, y, AA, n, m, K);
         *
         * /**
         * Test Case 3
         *
         *
         * int n = 50;
         * int m = 260;
         *
         * Vector b = new DenseVector(m,0);
         * for(int i = 1; i<=m/2;i++)
         * b.set(2*i-1, Math.random());
         *
         * Matrix AA = new DenseMatrix(m,n);
         * for(int row = 1; row<=m/2; row++){
         * for(int col = 1; col<=n;col++)
         * AA.set(2*row, col, -Math.random());
         * }
         *
         * CSRSparseMatrix A = new CSRSparseMatrix(m, n);
         *
         * int qq[] = new int[m/2];
         * for(int i = 0; i<qq.length;i++)
         * qq[i] = 2;
         *
         * Vector c = new DenseVector(n);
         * c.set(2,1);
         *
         * Vector x = new DenseVector(n,0);
         * for(int i = 1; i<=n; i++)
         * x.set(i,Math.random());
         *
         * Vector s = A.multiply(x).minus(b);
         * Vector y = new DenseVector(m,0.2);
         *
         * scsCone K = new scsCone(0, 0, 0, qq ,m/2
         * , null, 0, 0,0, null, 0);
         * scsSolver solver = new scsSolver(c, b, s, x, y, A, n, m, K);
         *
         * //List<SOCPGeneralConstraint> constraints = getData.getConstraints();
         * System.out.println("Problem Constructed Successfully!");
         * long startTime = System.nanoTime();
         * Vector[] solutions = solver.getSolution(1e-3);
         * long elapsedTime = System.nanoTime() - startTime;
         * for(int i = 0; i<solutions.length; i++)
         * System.out.println(solutions[i]);
         * System.out.println("Total execution time in Java in s: " +
         * elapsedTime/1000000000);
         */
    }
}
