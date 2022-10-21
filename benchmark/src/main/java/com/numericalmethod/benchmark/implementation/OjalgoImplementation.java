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
import com.numericalmethod.benchmark.operation.linearalgebra.CholeskyDecomposition;
import com.numericalmethod.benchmark.operation.linearalgebra.LUDecomposition;
import com.numericalmethod.benchmark.operation.linearalgebra.QRDecomposition;
import com.numericalmethod.benchmark.operation.linearalgebra.SingularValueDecomposition;
import com.numericalmethod.benchmark.operation.linearalgebra.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.ojalgo.OjAlgoUtils;
import org.ojalgo.machine.VirtualMachine;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.decomposition.*;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.netio.BasicLogger;

/**
 *
 * @author Ken Yiu
 */
public class OjalgoImplementation extends AbstractImplementation {

    static {
        // a little hack here to make ojalgo silent
        BasicLogger.DEBUG = new BasicLogger.PrintStreamAppender(new PrintStream(new ByteArrayOutputStream(2048)));
        VirtualMachine vm = OjAlgoUtils.ENVIRONMENT;
//        System.out.println("the machine configuration detected by 'ojalgo' = " + vm);
    }

    public OjalgoImplementation() {
        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Cholesky<Double> chol = Cholesky.makePrimitive();
                chol.compute(matrix(arguments[0]));
                MatrixStore<Double> L = chol.getL();
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Eigenvalue<Double> eigen = Eigenvalue.makePrimitive(true);
                eigen.compute(matrix(arguments[0]), false);
                MatrixStore<Double> D = eigen.getD();
                MatrixStore<Double> V = eigen.getV();
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LU<Double> lu = LU.makePrimitive();
                lu.compute(matrix(arguments[0]));
                MatrixStore<Double> L = lu.getL();
                MatrixStore<Double> U = lu.getU();
                int[] pivots = lu.getPivotOrder();
            }
        });
        addExecutable(MatrixAddition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                PrimitiveMatrix C = matrix(arguments[0]).add(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixDeterminant.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                double B = matrix(arguments[0]).getDeterminant().doubleValue();
            }
        });
        addExecutable(MatrixInverse.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                PrimitiveMatrix B = matrix(arguments[0]).invert();
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // use Cholesky decomposition to get the inverse
                Cholesky<Double> chol = Cholesky.makePrimitive();
                chol.compute(matrix(arguments[0]));
                MatrixStore<Double> result = chol.getInverse();
            }
        });
        addExecutable(MatrixMultiplication.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                PrimitiveMatrix C = matrix(arguments[0]).multiply(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixScale.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                PrimitiveMatrix B = matrix(arguments[0]).multiply(MatrixScale.SCALAR);
            }
        });
        addExecutable(MatrixTranspose.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                PrimitiveMatrix B = matrix(arguments[0]).transpose();
            }
        });
        addExecutable(QRDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                QR<Double> qr = QR.makePrimitive();
                qr.compute(matrix(arguments[0]));
                MatrixStore<Double> Q = qr.getQ();
                MatrixStore<Double> R = qr.getR();
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                SingularValue<Double> svd = SingularValue.makePrimitive();
                svd.compute(matrix(arguments[0]));
                MatrixStore<Double> D = svd.getD();
                MatrixStore<Double> U = svd.getQ1();
                MatrixStore<Double> V = svd.getQ2();
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                PrimitiveMatrix solution = matrix(arguments[0]).solve(matrix(arguments[1]));
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("ojalgo", "38.2");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    return PrimitiveMatrix.FACTORY.rows(
                        ((DenseMatrixArgument) argument).get2dArray());
                }

                throw new ConversionNotSupported(argument);
            }
        };
    }

    private PrimitiveMatrix matrix(Object matrix) {
        return (PrimitiveMatrix) matrix;
    }
}
