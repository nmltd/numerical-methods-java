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
import org.jblas.*;
import static org.jblas.Solve.*;
import static org.jblas.util.Logger.*;

/**
 *
 * @author Ken Yiu
 */
public class JblasImplementation extends AbstractImplementation {

    static {
        // mute the jblas logger
        getLogger().setLevel(ERROR);
    }

    public JblasImplementation() {
        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix L = org.jblas.Decompose.cholesky(matrix(arguments[0]));
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix A = matrix(arguments[0]);
                DoubleMatrix[] VD = Eigen.symmetricEigenvectors(A);
                DoubleMatrix V = VD[0];
                DoubleMatrix D = VD[1];
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Decompose.LUDecomposition<DoubleMatrix> lu = Decompose.lu(matrix(arguments[0]));
                DoubleMatrix L = lu.l;
                DoubleMatrix U = lu.u;
                DoubleMatrix P = lu.p;
            }
        });
        addExecutable(MatrixAddition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix C = matrix(arguments[0]).add(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixDeterminant.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                double B = Decompose.lu(matrix(arguments[0])).u.diag().prod();
            }
        });
        addExecutable(MatrixInverse.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix B = Solve.solve(
                    matrix(arguments[0]),
                    DoubleMatrix.eye(matrix(arguments[0]).rows));
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // solver for SPD matrix
                int matrixSize = matrix(arguments[0]).rows;
                DoubleMatrix result = solvePositive(
                    matrix(arguments[0]),
                    DoubleMatrix.eye(matrixSize));
            }
        });
        addExecutable(MatrixMultiplication.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix C = matrix(arguments[0]).mmul(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixScale.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix B = matrix(arguments[0]).mmul(MatrixScale.SCALAR);
            }
        });
        addExecutable(MatrixTranspose.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix B = matrix(arguments[0]).transpose();
            }
        });
        addExecutable(QRDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Decompose.QRDecomposition<DoubleMatrix> qr = Decompose.qr(matrix(arguments[0]));
                DoubleMatrix Q = qr.q;
                DoubleMatrix R = qr.r;
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix[] results = Singular.fullSVD(matrix(arguments[0]));
                DoubleMatrix U = results[0];
                DoubleMatrix S = results[1];
                DoubleMatrix V = results[2];
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix solution = org.jblas.Solve.solve(
                    matrix(arguments[0]), matrix(arguments[1]));
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("jblas", "1.2.4");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    return new DoubleMatrix(((DenseMatrixArgument) argument).get2dArray());
                }

                throw new ConversionNotSupported(argument);
            }
        };
    }

    private DoubleMatrix matrix(Object matrix) {
        return (DoubleMatrix) matrix;
    }
}
