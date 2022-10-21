/*
 * Copyright (c) Numerical Method Inc.
 * http://www.numericalmethod.com/
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
package com.numericalmethod.benchmark.implementation;

import com.numericalmethod.benchmark.implementation.JlapackImplementation.LapackMatrix;
import com.numericalmethod.benchmark.implementation.converter.ArgumentConverter;
import com.numericalmethod.benchmark.implementation.converter.ConversionNotSupported;
import com.numericalmethod.benchmark.implementation.datatype.Argument;
import com.numericalmethod.benchmark.implementation.datatype.DenseMatrixArgument;
import com.numericalmethod.benchmark.implementation.library.LibraryInfo;
import com.numericalmethod.benchmark.operation.linearalgebra.*;
import org.netlib.blas.DGEMM;
import org.netlib.lapack.*;
import org.netlib.util.intW;

/**
 *
 * @author Song Lin
 */
public class JlapackImplementation extends AbstractImplementation {

    public static class LapackMatrix {

        private final double[][] data;

        public LapackMatrix(double[][] data) {
            this.data = data;
        }

        public LapackMatrix(int nRows, int nCols) {
            this.data = new double[nRows][nCols];
        }

        public double get(int row, int col) {
            return data[row][col];
        }

        public void set(int row, int col, double value) {
            data[row][col] = value;
        }

        public int nRows() {
            return data.length;
        }

        public int nCols() {
            return data[0].length;
        }

        public LapackMatrix copy() {
            int row = nRows();
            int col = nCols();
            double[][] copy = new double[row][col];
            for (int i = 0; i < row; ++i) {
                for (int j = 0; j < col; ++j) {
                    copy[i][j] = data[i][j];
                }
            }
            return new LapackMatrix(copy);
        }

        public LapackMatrix zero() {
            return new LapackMatrix(nRows(), nCols());
        }

        public LapackMatrix one() {
            LapackMatrix one = zero();
            int diag = Math.min(nRows(), nCols());
            for (int i = 0; i < diag; ++i) {
                one.set(i, i, 1.);
            }
            return one;
        }
    }

    public JlapackImplementation() {
        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LapackMatrix L = matrix(arguments[0]).copy(); // make a copy
                intW info = new intW(0);

                DPOTRF.DPOTRF(
                    "L", // indicator to compute lower triangular matrix
                    L.nRows(), // size of the (square) matrix
                    L.data, // the input matrix
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("Cholesky decomposition is unsuccessful!");
                }
                if (info.val > 0) {
                    throw new IllegalArgumentException("Cholesky decomposition failed; "
                        + "the matrix must be positive definite!");
                }

                /*
                 * Note: LAPACK only deals with either LOWER or UPPER triangular; the other side
                 * must be filled with zero.
                 */
                clearUpper(L);
            }

            /**
             * {@link org.jblas.Decompose#clearLower}
             */
            private void clearUpper(LapackMatrix L) {
                for (int i = 0; i < L.nRows(); ++i) {
                    for (int j = i + 1; j < L.nCols(); ++j) {
                        L.set(i, j, 0.);
                    }
                }
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LapackMatrix Q = matrix(arguments[0]).copy(); // make a copy
                int size = Q.nRows();
                double[] D = new double[size];
                intW info = new intW(0);
                int lwork = 2 * size * size + 6 * size + 1; // see the LAPACK documentation
                int liwork = 5 * size + 3; // see the LAPACK documentation

                DSYEVD.DSYEVD( // eigen decomposition by divide-and-conquer approach
                    "V", // computes both eigenvalues and eigenvectors
                    "U", // symmetric matrix, "U" or "L" does not matter
                    size, // the size
                    Q.data, // the orthogonal matrix (eigenvectors)
                    D, // the eigenvalues in ascending order
                    new double[lwork], // lapack workspace
                    lwork, // lapack workspace
                    new int[liwork], // lapack workspace
                    liwork, // lapack workspace
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("Eigen decomposition is unsuccessful!");
                }
                if (info.val > 0) { // see the LAPACK documentation
                    throw new IllegalArgumentException(
                        String.format("Eigen decomposition failed on submatrix (%d, %d) -> (%d, %d)",
                                      info.val / (size + 1), info.val / (size + 1),
                                      info.val % (size + 1), info.val % (size + 1)));
                }
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LapackMatrix A = matrix(arguments[0]).copy(); // make a copy
                int minSize = Math.min(A.nRows(), A.nCols());
                int[] pivotIndex = new int[minSize];
                intW info = new intW(0);

                DGETRF.DGETRF(
                    A.nRows(), // number of rows
                    A.nCols(), // number of cols
                    A.data, // the input matrix
                    pivotIndex, // pivot indices
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("LU decomposition is unsuccessful!");
                }
                if (info.val > 0) {
                    throw new IllegalArgumentException("LU decomposition failed; "
                        + "the matrix is singular!");
                }

                // LAPACK doesn't output L, U, P
                LapackMatrix L = new LapackMatrix(A.nRows(), minSize);
                LapackMatrix U = new LapackMatrix(minSize, A.nCols());
                decomposeLowerUpper(A, L, U);

                LapackMatrix P = new LapackMatrix(minSize, minSize);
                decomposePivot(P, pivotIndex);
            }

            /**
             * {@link org.jblas.Decompose#decomposeLowerUpper}
             */
            private void decomposeLowerUpper(LapackMatrix A, LapackMatrix L, LapackMatrix U) {
                for (int i = 0; i < A.nRows(); ++i) {
                    for (int j = 0; j < A.nCols(); ++j) {
                        if (i < j) {
                            U.set(i, j, A.get(i, j));
                        } else if (i == j) {
                            U.set(i, i, A.get(i, i));
                            L.set(i, i, 1.);
                        } else {
                            L.set(i, j, A.get(i, j));
                        }
                    }
                }
            }

            /**
             * {@link org.jblas.util.Permutations#permutationDoubleMatrixFromPivotIndices}
             */
            private void decomposePivot(LapackMatrix P, int[] ipiv) {
                int n = ipiv.length;
                int size = P.nRows();
                int indices[] = new int[size];
                for (int i = 0; i < size; i++) {
                    indices[i] = i;
                }

                for (int i = 0; i < n; i++) {
                    int j = ipiv[i] - 1;
                    int t = indices[i];
                    indices[i] = indices[j];
                    indices[j] = t;
                }

                for (int i = 0; i < size; i++) {
                    P.set(indices[i], i, 1.);
                }
            }
        });
        addExecutable(MatrixAddition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LapackMatrix C = matrix(arguments[0]).copy();
                /*
                 * Important note: LAPACK does not directly support matrix addition; using DGEMM()
                 * may be slower due to additional multiplication with identity matrix.
                 */
                DGEMM.DGEMM(
                    "N", // no transpose of the first matrix
                    "N", // no transpose on identity
                    C.nRows(), // number of rows
                    C.nCols(), // number of cols
                    C.nCols(), // the other dimension
                    1., // no scaling
                    matrix(arguments[1]).data, // input matrix
                    C.one().data, // identity matrix
                    1., // no scaling
                    C.data // the second matrix
                    );
            }
        });
        addExecutable(MatrixDeterminant.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // Use LU decomposition
                LapackMatrix A = matrix(arguments[0]).copy(); // make a copy
                int minSize = Math.min(A.nRows(), A.nCols());
                int[] pivotIndex = new int[minSize];
                intW info = new intW(0);

                DGETRF.DGETRF(
                    A.nRows(), // number of rows
                    A.nCols(), // number of cols
                    A.data, // the input matrix
                    pivotIndex, // pivot indices
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("LU decomposition is unsuccessful!");
                }
                if (info.val > 0) {
                    throw new IllegalArgumentException("LU decomposition failed; "
                        + "the matrix is singular!");
                }

                /*
                 * LAPACK does not directly calculate matrix determinant; the determinant is
                 * calculated as the product of the diagonal entries of A.
                 */
                double B = determinantFromLU(A);
            }

            private double determinantFromLU(LapackMatrix A) {
                double B = 1.;
                for (int i = 0; i < A.nRows(); ++i) {
                    B *= A.get(i, i);
                }
                return B;
            }
        });
        addExecutable(MatrixInverse.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // General inverse from LU decomposition
                // LAPACK requires to do the decomposition first and then the inverse
                LapackMatrix B = matrix(arguments[0]).copy(); // make a copy
                int size = B.nRows();
                int[] pivotIndex = new int[size];
                intW info = new intW(0);
                int NB = ILAENV.ILAENV(1, "DGETRI", " ", size, -1, -1, -1); // number of blocks
                int lwork = size * NB;

                DGETRF.DGETRF( // First, calculate the LU decomposition
                    size, // number of rows
                    size, // number of cols
                    B.data, // the input matrix
                    pivotIndex, // pivot indices
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("LU decomposition is unsuccessful!");
                }
                if (info.val > 0) {
                    throw new IllegalArgumentException("LU decomposition failed; "
                        + "the matrix is singular!");
                }

                DGETRI.DGETRI( // Then, do the inverse
                    size, // matrix size
                    B.data, // the input matrix
                    pivotIndex, // pivot indices
                    new double[lwork], // lapack workspace
                    lwork, // lapack workspace
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("Inverse is unsuccessful!");
                }
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // Use the Cholesky decomposition
                // LAPACK requires to do the decomposition first and then the inverse
                LapackMatrix result = matrix(arguments[0]).copy(); // make a copy
                intW info = new intW(0);

                DPOTRF.DPOTRF(
                    "L", // indicator to compute lower triangular matrix
                    result.nRows(), // size of the (square) matrix
                    result.data, // the input matrix
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("Cholesky decomposition is unsuccessful!");
                }
                if (info.val > 0) {
                    throw new IllegalArgumentException("Cholesky decomposition failed; "
                        + "the matrix must be positive definite!");
                }

                DPOTRI.DPOTRI(
                    "L", // indicator to compute lower triangular matrix
                    result.nRows(), // size of the (square) matrix
                    result.data, // the input matrix
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("Cholesky decomposition is unsuccessful!");
                }
                if (info.val > 0) {
                    throw new IllegalArgumentException("Cholesky decomposition failed; "
                        + "the matrix must be positive definite!");
                }

                /*
                 * Note: LAPACK only computes either LOWER or UPPER triangular of the inverse
                 * matrix; the other side must be filled symmetrically
                 */
                fillUpper(result);
            }

            public void fillUpper(LapackMatrix L) {
                for (int i = 0; i < L.nRows(); ++i) {
                    for (int j = i + 1; j < L.nCols(); ++j) {
                        L.set(i, j, L.get(j, i));
                    }
                }
            }
        });
        addExecutable(MatrixMultiplication.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LapackMatrix C = new LapackMatrix(matrix(arguments[0]).nRows(), matrix(arguments[1]).nCols());
                DGEMM.DGEMM(
                    "N", // no transpose on first matrix
                    "N", // no transpose on second matrix
                    C.nRows(), // number of rows of the result matrix
                    C.nCols(), // number of cols of the result matrix
                    matrix(arguments[0]).nCols(), // the other dimension
                    1., // scale is 1.
                    matrix(arguments[0]).data, // the first matrix
                    matrix(arguments[1]).data, // the second matrix
                    0., // no offset
                    C.data // the result matrix
                    );
            }
        });
        addExecutable(MatrixScale.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LapackMatrix B = matrix(arguments[0]).copy(); // make a copy
                DGEMM.DGEMM(
                    "N", // no transpose on first matrix
                    "N", // no transpose on second matrix
                    B.nRows(), // number of rows of the result matrix
                    B.nCols(), // number of cols of the result matrix
                    matrix(arguments[0]).nCols(), // the other dimension
                    0., // no matrix multiplication
                    matrix(arguments[0]).data, // no matrix multiplication
                    matrix(arguments[0]).data, // no matrix multiplication
                    MatrixScale.SCALAR, // scaling
                    B.data // the result matrix
                    );
            }
        });
        addExecutable(MatrixTranspose.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LapackMatrix B = matrix(arguments[0]).zero();
                /*
                 * Important note: LAPACK does not directly support matrix transpose; using DGEMM()
                 * may be slower due to additional multiplication with identity matrix.
                 */
                DGEMM.DGEMM(
                    "T", // transpose of the input
                    "N", // no transpose on identity
                    B.nRows(), // number of rows
                    B.nCols(), // number of cols
                    B.nCols(), // the other dimension
                    1., // no scaling
                    matrix(arguments[0]).data, // input matrix
                    B.one().data, // identity matrix
                    0., // no offset
                    B.data // the result matrix
                    );
            }
        });
        addExecutable(QRDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LapackMatrix A = matrix(arguments[0]).copy(); // make a copy
                int row = A.nRows();
                int col = A.nCols();
                int k = Math.min(row, col);
                double[] tau = new double[k];
                intW info = new intW(0);
                int NB = ILAENV.ILAENV(1, "DGEQRF", "", row, col, -1, -1); // number of blocks
                int lwork = col * NB; // see the LAPACK documentation

                /**
                 * Important note: R seems to use another subroutine to do the followings
                 * (calculating the factors 'tau'), i.e., DGEQP3, which involves a column pivoting
                 * to the input matrix.
                 * However, LAPACK does not have the subsequent subroutine to get Q after the
                 * pivoting QR (or QP).
                 */
                DGEQRF.DGEQRF( // QR factorization, to get 'tau' (the factors) and R
                    row, // number of rows
                    col, // number of columns
                    A.data, // the output containing R
                    tau, // the scalar factors (output for next step)
                    new double[lwork], // lapack workspace 
                    lwork, // lapack workspace
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("QR decomposition is unsuccessful!");
                }

                LapackMatrix R = A.copy();
                // LAPACK does not directly output R; instead, the upper triangular 
                // matrix of the result is R, while the lower part should be set to 0
                clearLower(R);

                NB = ILAENV.ILAENV(1, "DORGQR", "", row, row, k, -1); // number of blocks
                lwork = col * NB; // see the LAPACK documentation

                DORGQR.DORGQR( // the second step of QR factorization, to get Q
                    row, // number of rows of Q
                    col, // number of columns of Q
                    k, // number of elementary reflectors
                    A.data, // the results containing Q
                    tau, // the scalar factors
                    new double[lwork], // lapack workspace
                    lwork, // lapack workspace
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("QR decomposition is unsuccessful!");
                }
                
                LapackMatrix Q = A; // Q is the output from DORGQR
            }

            public void clearLower(LapackMatrix R) {
                for (int j = 0; j < R.nCols(); j++) {
                    for (int i = j + 1; i < R.nRows(); i++) {
                        R.set(i, j, 0.);
                    }
                }
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                int row = matrix(arguments[0]).nRows();
                int col = matrix(arguments[0]).nCols();
                int minSize = Math.min(row, col);
                LapackMatrix U = new LapackMatrix(row, row);
                double[] S = new double[minSize];
                LapackMatrix V = new LapackMatrix(col, col);
                intW info = new intW(0);
                int lwork = 7 * minSize * minSize + 4 * Math.max(row, col); // see the LAPACK documentation
                int iwork = 8 * minSize;

                DGESDD.DGESDD( // using divide-and-conquer approach, faster but requires more memory
                    "A", // computes all
                    row, // number of the rows
                    col, // number of the columns
                    matrix(arguments[0]).copy().data, // make a copy to the input matrix
                    S, // the eigenvalues
                    U.data, // U
                    V.data, // the transpose of V
                    new double[lwork], // lapack workspace
                    lwork, // lapack workspace
                    new int[iwork], // lapack workspace
                    info // error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException(
                        "Singular value decomposition is unsuccessful!");
                }
                if (info.val > 0) {
                    throw new IllegalArgumentException(
                        "Singular value decomposition not converged!");
                }
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LapackMatrix solution = matrix(arguments[1]).copy(); // make a copy
                intW info = new intW(0);

                DGESV.DGESV( // the LHS matrix must be square
                    solution.nRows(), // number of rows of the solution
                    solution.nCols(), // number of columns of the solution
                    matrix(arguments[0]).copy().data, // make a copy to the LHS matrix
                    new int[solution.nRows()], // the pivot indices
                    solution.data, // the solution matrix
                    info // the error flag
                    );

                if (info.val < 0) {
                    throw new IllegalArgumentException("Solve linear system is unsuccessful!");
                }
                if (info.val > 0) {
                    throw new IllegalArgumentException("Solve linear system failed;"
                        + "the left hand side matrix is singular");
                }
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("jlapack", "0.8");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    return new LapackMatrix(((DenseMatrixArgument) argument).get2dArray());
                }

                throw new ConversionNotSupported(argument);
            }
        };
    }

    private LapackMatrix matrix(Object matrix) {
        return (LapackMatrix) matrix;
    }
}
