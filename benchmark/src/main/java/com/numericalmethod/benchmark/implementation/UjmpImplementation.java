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
import org.ujmp.core.Matrix;
import org.ujmp.core.doublematrix.DoubleMatrix2D;
import org.ujmp.core.doublematrix.impl.DefaultDenseDoubleMatrix2D;
import org.ujmp.core.util.UJMPSettings;

/**
 *
 * @author Ken Yiu
 */
public class UjmpImplementation extends AbstractImplementation {

    static {
        /*
         * Note: For matrix size > 100, UJMP delegates SVD decomposition to the built-in OjAlgo
         * classes. However, we are also linking to a newer version of OjAlgo API whose method names
         * have been changed. So, the method call will fail with a NoSuchMethodException.
         *
         * As a remedy, we change the default setting to use the internal MTJ classes instead.
         */
        UJMPSettings.getInstance().setUseOjalgo(false);
        UJMPSettings.getInstance().setUseMTJ(true);
    }

    public UjmpImplementation() {

        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix L = matrix(arguments[0]).chol();
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix[] VD = matrix(arguments[0]).eigSymm();
                Matrix V = VD[0];
                Matrix D = VD[1];
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix[] results = matrix(arguments[0]).lu();
                Matrix L = results[0];
                Matrix U = results[1];
                Matrix P = results[2];
            }
        });
        addExecutable(MatrixAddition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix C = matrix(arguments[0]).plus(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixDeterminant.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                double B = matrix(arguments[0]).det();
            }
        });
        addExecutable(MatrixInverse.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix B = matrix(arguments[0]).inv();
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix result = matrix(arguments[0]).invSPD();
            }
        });
        addExecutable(MatrixMultiplication.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix C = matrix(arguments[0]).mtimes(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixScale.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix B = matrix(arguments[0]).times(MatrixScale.SCALAR);
            }
        });
        addExecutable(MatrixTranspose.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix B = matrix(arguments[0]);
                Matrix result = B.transpose();
            }
        });
        addExecutable(QRDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix[] results = matrix(arguments[0]).qr();
                Matrix Q = results[0];
                Matrix R = results[1];
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix[] results = matrix(arguments[0]).svd();
                Matrix U = results[0];
                Matrix S = results[1];
                Matrix V = results[2];
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // bug: the solution seems wrong
                Matrix solution = matrix(arguments[0]).solve(matrix(arguments[1]));
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("ujmp", "0.3.0");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    return newMatrixFrom2dArray(((DenseMatrixArgument) argument).get2dArray());
                }

                throw new ConversionNotSupported(argument);
            }

            public DoubleMatrix2D newMatrixFrom2dArray(double[][] A) {
                int nRows = A.length;
                int nCols = A[0].length;
                double[] values = new double[nRows * nCols];
                for (int i = 0, pos = 0; i < nRows; ++i, pos += nCols) {
                    System.arraycopy(A[i], 0, values, pos, nCols);
                }
                return new DefaultDenseDoubleMatrix2D(values, nRows, nCols);
            }
        };
    }

    private DoubleMatrix2D matrix(Object matrix) {
        return (DoubleMatrix2D) matrix;
    }
}
