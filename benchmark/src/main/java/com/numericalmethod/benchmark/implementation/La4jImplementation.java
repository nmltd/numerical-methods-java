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
import org.la4j.LinearAlgebra;
import org.la4j.Matrix;
import org.la4j.decomposition.*;
import org.la4j.inversion.MatrixInverter;
import org.la4j.matrix.dense.Basic2DMatrix;

/**
 *
 * @author Ken Yiu
 */
public class La4jImplementation extends AbstractImplementation {

    public La4jImplementation() {
        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                CholeskyDecompositor chol = new CholeskyDecompositor(matrix(arguments[0]));
                Matrix[] results = chol.decompose();
                Matrix L = results[0];
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) { // too slow
                EigenDecompositor eigen = new EigenDecompositor(matrix(arguments[0]));
                Matrix[] VD = eigen.decompose();
                Object V = VD[0];
                Object D = VD[1];
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LUDecompositor lu = new LUDecompositor(matrix(arguments[0]));
                Matrix[] results = lu.decompose();
                Matrix L = results[0];
                Matrix U = results[1];
                Matrix P = results[2];
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
                double B = matrix(arguments[0]).determinant();
            }
        });
        addExecutable(MatrixInverse.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                MatrixInverter inverter
                    = matrix(arguments[0]).withInverter(LinearAlgebra.GAUSS_JORDAN);
                Matrix B = inverter.inverse();
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // no suitable inverse for SPD matrix
                MatrixInverter inverter
                    = matrix(arguments[0]).withInverter(LinearAlgebra.GAUSS_JORDAN);
                Matrix result = inverter.inverse();
            }
        });
        addExecutable(MatrixMultiplication.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix C = matrix(arguments[0]).multiply(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixScale.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix B = matrix(arguments[0]).multiply(MatrixScale.SCALAR);
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
                QRDecompositor qr = new QRDecompositor(matrix(arguments[0]));
                Matrix[] results = qr.decompose();
                Matrix Q = results[0];
                Matrix R = results[1];
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                SingularValueDecompositor svd = new SingularValueDecompositor(matrix(arguments[0]));
                Matrix[] results = svd.decompose();
                Matrix U = results[0];
                Matrix S = results[1];
                Matrix V = results[2];
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                MatrixInverter inverter
                    = matrix(arguments[0]).withInverter(LinearAlgebra.GAUSS_JORDAN);
                Basic2DMatrix solution
                    = (Basic2DMatrix) inverter.inverse().multiply(matrix(arguments[1]));
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("la4j", "0.5.5");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    return new Basic2DMatrix(((DenseMatrixArgument) argument).get2dArray());
                }

                throw new ConversionNotSupported(argument);
            }
        };
    }

    private Basic2DMatrix matrix(Object matrix) {
        return (Basic2DMatrix) matrix;
    }
}
