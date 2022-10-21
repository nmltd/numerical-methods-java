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

import com.numericalmethod.benchmark.implementation.converter.ArgumentConverter;
import com.numericalmethod.benchmark.implementation.converter.ConversionNotSupported;
import com.numericalmethod.benchmark.implementation.datatype.Argument;
import com.numericalmethod.benchmark.implementation.datatype.DenseMatrixArgument;
import com.numericalmethod.benchmark.implementation.library.LibraryInfo;
import com.numericalmethod.benchmark.operation.linearalgebra.*;
import java.util.logging.Level;
import no.uib.cipr.matrix.*;
import static no.uib.cipr.matrix.SymmDenseEVD.*;

/**
 *
 * @author Ken Yiu
 */
public class MtjImplementation extends AbstractImplementation {

    static {
        // mute the mtj logger
        java.util.logging.Logger.getLogger("com.github.fommil").setLevel(Level.OFF);
        /*
         * This is tricky fix for MTJ API. If other operations are performed before multiplication,
         * multiplication, when performed, will throw exception. So, before any execution, a small
         * multiplication is done here.
         */
        Matrix A = new DenseMatrix(1, 1);
        A = A.mult(A, A);
    }

    public MtjImplementation() {
        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // Note: MTJ doesn't support getL() (throw UnsupportedOperationException)
//                Object L = DenseCholesky.factorize(matrix(arguments[0])).getL();
                Object U = DenseCholesky.factorize(matrix(arguments[0])).getU();
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                try {
                    SymmDenseEVD eigen = factorize(matrix(arguments[0]));
                    double[] eignevalues = eigen.getEigenvalues();
                    Matrix V = eigen.getEigenvectors();
                } catch (NotConvergedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                int matrixSize = matrix(arguments[0]).numRows();
                DenseLU lu = new DenseLU(matrixSize, matrixSize);
                lu.factor((DenseMatrix) matrix(arguments[0]));
                Object L = lu.getL();
                Object U = lu.getU();
                int[] pivots = lu.getPivots();
            }
        });
        addExecutable(MatrixAddition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix C = matrix(arguments[0]).add(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixDeterminant.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // no suitable method to compute determinant; EVD is used instead
                try {
                    EVD evd = EVD.factorize(matrix(arguments[0]));
                    double[] realValues = evd.getRealEigenvalues();
                    double[] imaginaryValues = evd.getImaginaryEigenvalues();
                    double real = 1.;
                    double imaginary = 0.;
                    for (int i = 0; i < matrix(arguments[0]).numRows(); ++i) {
                        double temp = real;
                        real = real * realValues[i] - imaginary * imaginaryValues[i];
                        imaginary = temp * imaginaryValues[i] + imaginary * realValues[i];
                    }
                    double B = real;
                } catch (Exception ex) {
                    System.out.println("Mtj SVD error");
                }
            }
        });
        addExecutable(MatrixInverse.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix A = matrix(arguments[0]);
                Matrix B = new DenseMatrix(A.numRows(), A.numColumns());
                matrix(arguments[0]).solve(
                    no.uib.cipr.matrix.Matrices.identity(A.numRows()), B);
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // use Cholesky decomposition to get the inverse
                int matrixSize = matrix(arguments[0]).numRows();
                DenseCholesky chol = new DenseCholesky(matrixSize, false);
                LowerSPDDenseMatrix l = new LowerSPDDenseMatrix(matrix(arguments[0]));
                chol.factor(l);
                no.uib.cipr.matrix.Matrix result = chol.solve(
                    no.uib.cipr.matrix.Matrices.identity(matrixSize));
            }
        });
        addExecutable(MatrixMultiplication.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix A = matrix(arguments[0]);
                Matrix B = matrix(arguments[1]);
                Matrix C = new DenseMatrix(A.numRows(), A.numColumns());
                C = A.mult(B, C);
            }
        });
        addExecutable(MatrixScale.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix B = matrix(arguments[0]).scale(MatrixScale.SCALAR);
            }
        });
        addExecutable(MatrixTranspose.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix B = matrix(arguments[0]).transpose();
            }
        });
        addExecutable(QRDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix A = matrix(arguments[0]);
                int nRows = A.numRows();
                int nCols = A.numColumns();
                QR qr = new no.uib.cipr.matrix.QR(nRows, nCols);
                qr.factor((DenseMatrix) A);
                Object Q = qr.getQ();
                Object R = qr.getR();
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                try {
                    Matrix A = matrix(arguments[0]);
                    int nRows = A.numRows();
                    int nCols = A.numColumns();
                    SVD svd = new no.uib.cipr.matrix.SVD(nRows, nCols);
                    svd.factor((DenseMatrix) A);
                    double[] S = svd.getS();
                    Matrix U = svd.getU();
                    Matrix V = svd.getVt();
                } catch (NotConvergedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix A = matrix(arguments[0]);
                Matrix B = matrix(arguments[1]);
                int nRows = matrix(arguments[0]).numRows();
                Matrix solution = new DenseMatrix(nRows, nRows);
                A.solve(B, solution);
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("mtj", "1.0.4");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    return new DenseMatrix(((DenseMatrixArgument) argument).get2dArray());
                }

                throw new ConversionNotSupported(argument);
            }
        };
    }

    private Matrix matrix(Object matrix) {
        return (Matrix) matrix;
    }
}
