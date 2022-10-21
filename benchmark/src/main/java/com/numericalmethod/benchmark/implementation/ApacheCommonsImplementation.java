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
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author Ken Yiu
 */
public class ApacheCommonsImplementation extends AbstractImplementation {

    public ApacheCommonsImplementation() {
        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                org.apache.commons.math3.linear.CholeskyDecomposition chol =
                    new org.apache.commons.math3.linear.CholeskyDecomposition(matrix(arguments[0]));
                Object L = chol.getL();
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                org.apache.commons.math3.linear.EigenDecomposition eigen =
                    new org.apache.commons.math3.linear.EigenDecomposition(matrix(arguments[0]));
                Object D = eigen.getD();
                Object V = eigen.getV();
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                org.apache.commons.math3.linear.LUDecomposition lu =
                    new org.apache.commons.math3.linear.LUDecomposition(matrix(arguments[0]));
                Object L = lu.getL();
                Object U = lu.getU();
                Object P = lu.getP();
            }
        });
        addExecutable(MatrixAddition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                RealMatrix C = matrix(arguments[0]).add(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixDeterminant.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                org.apache.commons.math3.linear.LUDecomposition LU =
                    new org.apache.commons.math3.linear.LUDecomposition(matrix(arguments[0]));
                double B = LU.getDeterminant();
            }
        });
        addExecutable(MatrixInverse.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                org.apache.commons.math3.linear.LUDecomposition LU =
                    new org.apache.commons.math3.linear.LUDecomposition(matrix(arguments[0]));
                RealMatrix result = LU.getSolver().getInverse();
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // use Cholesky decomposition to get the inverse
                org.apache.commons.math3.linear.CholeskyDecomposition chol =
                    new org.apache.commons.math3.linear.CholeskyDecomposition(matrix(arguments[0]));
                RealMatrix result = chol.getSolver().getInverse();
            }
        });
        addExecutable(MatrixMultiplication.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                RealMatrix C = matrix(arguments[0]).multiply(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixScale.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                RealMatrix B = matrix(arguments[0]).scalarMultiply(0.5);
            }
        });
        addExecutable(MatrixTranspose.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                RealMatrix B = matrix(arguments[0]).transpose();
            }
        });
        addExecutable(QRDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                org.apache.commons.math3.linear.QRDecomposition qr =
                    new org.apache.commons.math3.linear.QRDecomposition(matrix(arguments[0]));
                Object Q = qr.getQ();
                Object R = qr.getR();
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                org.apache.commons.math3.linear.SingularValueDecomposition svd =
                    new org.apache.commons.math3.linear.SingularValueDecomposition(matrix(arguments[0]));
                Object S = svd.getS();
                Object U = svd.getU();
                Object V = svd.getV();
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                org.apache.commons.math3.linear.LUDecomposition lu =
                    new org.apache.commons.math3.linear.LUDecomposition(matrix(arguments[0]));
                RealMatrix solution = lu.getSolver().solve(matrix(arguments[1]));
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("apache-commons", "3.5");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    return new Array2DRowRealMatrix(((DenseMatrixArgument) argument).get2dArray());
                }

                throw new ConversionNotSupported(argument);
            }
        };
    }

    private RealMatrix matrix(Object matrix) {
        return (RealMatrix) matrix;
    }
}
