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

import cern.colt.function.tdouble.DoubleDoubleFunction;
import cern.colt.function.tdouble.DoubleFunction;
import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.algo.decomposition.*;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import com.numericalmethod.benchmark.implementation.converter.ArgumentConverter;
import com.numericalmethod.benchmark.implementation.converter.ConversionNotSupported;
import com.numericalmethod.benchmark.implementation.datatype.Argument;
import com.numericalmethod.benchmark.implementation.datatype.DenseMatrixArgument;
import com.numericalmethod.benchmark.implementation.library.LibraryInfo;
import com.numericalmethod.benchmark.operation.linearalgebra.*;

/**
 *
 * @author Ken Yiu
 */
public class ParallelColtImplementation extends AbstractImplementation {

    public ParallelColtImplementation() {
        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DenseDoubleAlgebra pColtAlgebra = new DenseDoubleAlgebra();
                DenseDoubleCholeskyDecomposition chol = pColtAlgebra.chol(matrix(arguments[0]));
                Object L = chol.getL();
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DenseDoubleAlgebra pColtAlgebra = new DenseDoubleAlgebra();
                DenseDoubleEigenvalueDecomposition eigen = pColtAlgebra.eig(matrix(arguments[0]));
                DoubleMatrix2D D = eigen.getD();
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DenseDoubleLUDecompositionQuick lu = new DenseDoubleLUDecompositionQuick();
                lu.decompose(matrix(arguments[0]));
                DoubleMatrix2D L = lu.getL();
                DoubleMatrix2D U = lu.getU();
                int[] pivots = lu.getPivot();
            }
        });
        addExecutable(MatrixAddition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                cern.colt.matrix.tdouble.DoubleMatrix2D C = matrix(arguments[0]).copy();
                C.assign(matrix(arguments[1]), new DoubleDoubleFunction() {
                    @Override
                    public double apply(double x, double y) {
                        return x + y;
                    }
                });
            }
        });
        addExecutable(MatrixDeterminant.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DenseDoubleAlgebra pColtAlgebra = new DenseDoubleAlgebra();
                double B = pColtAlgebra.det(matrix(arguments[0]));
            }
        });
        addExecutable(MatrixInverse.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DenseDoubleAlgebra pColtAlgebra = new DenseDoubleAlgebra();
                DoubleMatrix2D B = pColtAlgebra.inverse(matrix(arguments[0]));
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // use Cholesky decomposition to get the inverse
                int matrixSize = matrix(arguments[0]).rows();
                DenseDoubleAlgebra pColtAlgebra = new DenseDoubleAlgebra();
                DenseDoubleCholeskyDecomposition chol = pColtAlgebra.chol(matrix(arguments[0]));
                DoubleMatrix2D result =
                    DoubleFactory2D.dense.identity(matrixSize);
                chol.solve(result);
            }
        });
        addExecutable(MatrixMultiplication.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix2D C = new DenseDoubleMatrix2D(
                    matrix(arguments[0]).rows(), matrix(arguments[1]).columns());
                matrix(arguments[0]).zMult(matrix(arguments[1]), C);
            }
        });
        addExecutable(MatrixScale.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix2D B = matrix(arguments[0]).copy();
                B.assign(new DoubleFunction() {
                    @Override
                    public double apply(double d) {
                        return MatrixScale.SCALAR * d;
                    }
                });
            }
        });
        addExecutable(MatrixTranspose.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DenseDoubleAlgebra pColtAlgebra = new DenseDoubleAlgebra();
                DoubleMatrix2D B = pColtAlgebra.transpose(matrix(arguments[0])).copy();
            }
        });
        addExecutable(QRDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DenseDoubleAlgebra pColtAlgebra = new DenseDoubleAlgebra();
                DenseDoubleQRDecomposition qr = pColtAlgebra.qr(matrix(arguments[0]));
                DoubleMatrix2D Q = qr.getQ(true);
                DoubleMatrix2D R = qr.getR(true);
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DenseDoubleAlgebra pColtAlgebra = new DenseDoubleAlgebra();
                DenseDoubleSingularValueDecomposition svd = pColtAlgebra.svd(matrix(arguments[0]));
                DoubleMatrix2D S = svd.getS();
                DoubleMatrix2D U = svd.getU();
                DoubleMatrix2D V = svd.getV();
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DenseDoubleAlgebra pColtAlgebra = new DenseDoubleAlgebra();
                DoubleMatrix2D solution =
                    pColtAlgebra.solve(matrix(arguments[0]), matrix(arguments[1]));
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("parallelcolt", "0.10.1");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    return new DenseDoubleMatrix2D(((DenseMatrixArgument) argument).get2dArray());
                }

                throw new ConversionNotSupported(argument);
            }
        };
    }

    private DoubleMatrix2D matrix(Object matrix) {
        return (DoubleMatrix2D) matrix;
    }
}
