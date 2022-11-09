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
import cern.colt.function.DoubleFunction;
import cern.colt.function.DoubleDoubleFunction;
import cern.jet.math.Functions;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
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
class my_class implements DoubleFunction {
    double sca;
    public my_class(double s)
    {sca = s;}
    @Override
    public double apply(double d){
        return sca * d;
    }
}

public class ColtImplementation extends AbstractImplementation {

    

    public ColtImplementation() {
        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                cern.colt.matrix.linalg.CholeskyDecomposition chol =
                    new cern.colt.matrix.linalg.CholeskyDecomposition(matrix(arguments[0]));
                DoubleMatrix2D L = chol.getL();
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                cern.colt.matrix.linalg.EigenvalueDecomposition eigen =
                    new cern.colt.matrix.linalg.EigenvalueDecomposition(matrix(arguments[0]));
                DoubleMatrix2D D = eigen.getD();
                DoubleMatrix2D V = eigen.getV();
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                cern.colt.matrix.linalg.LUDecompositionQuick lu =
                    new cern.colt.matrix.linalg.LUDecompositionQuick();
                lu.decompose(matrix(arguments[0]));
                DoubleMatrix2D L = lu.getL();
                DoubleMatrix2D U = lu.getU();
                int[] pivots = lu.getPivot(); // permutation matrix is NOT provided
            }
        });
        addExecutable(MatrixAddition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix2D C = matrix(arguments[0]).copy();
                // Functions ff = Functions.functions;
                C.assign(matrix(arguments[1]), Functions.plus);
            }
        });
        addExecutable(MatrixDeterminant.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Algebra coltAlgebra = new Algebra();
                double B = coltAlgebra.det(matrix(arguments[0]));
            }
        });
        addExecutable(MatrixInverse.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Algebra coltAlgebra = new Algebra();
                DoubleMatrix2D B = coltAlgebra.inverse(matrix(arguments[0]));
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // use Cholesky decomposition to get the inverse
                cern.colt.matrix.linalg.CholeskyDecomposition chol =
                    new cern.colt.matrix.linalg.CholeskyDecomposition(matrix(arguments[0]));
                DoubleMatrix2D result = chol.solve(
                    DoubleFactory2D.dense.identity(matrix(arguments[0]).rows()));
            }
        });
        addExecutable(MatrixMultiplication.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Algebra coltAlgebra = new Algebra();
                DoubleMatrix2D C = coltAlgebra.mult(matrix(arguments[0]), matrix(arguments[1]));
            }
        });

        addExecutable(MatrixScale.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                DoubleMatrix2D B = matrix(arguments[0]).copy();
<<<<<<< Updated upstream
                DoubleFunction my_obj = new my_class(MatrixScale.SCALAR);
=======
                my_class my_obj = new my_class(MatrixScale.SCALAR);
>>>>>>> Stashed changes
                B.assign(my_obj);
                // B.assign(Functions.abs {
                //     @Override
                //     public double apply(double d) {
                //         return MatrixScale.SCALAR * d;
                //     }
                // });
            }
        });
        addExecutable(MatrixTranspose.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Algebra coltAlgebra = new Algebra();
                DoubleMatrix2D B = coltAlgebra.transpose(matrix(arguments[0])).copy();
            }
        });
        addExecutable(QRDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                cern.colt.matrix.linalg.QRDecomposition qr =
                    new cern.colt.matrix.linalg.QRDecomposition(matrix(arguments[0]));
                DoubleMatrix2D Q = qr.getQ();
                DoubleMatrix2D R = qr.getR();
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                cern.colt.matrix.linalg.SingularValueDecomposition svd =
                    new cern.colt.matrix.linalg.SingularValueDecomposition(matrix(arguments[0]));
                DoubleMatrix2D S = svd.getS();
                DoubleMatrix2D U = svd.getU();
                DoubleMatrix2D V = svd.getV();
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Algebra coltAlgebra = new Algebra();
                DoubleMatrix2D solution =
                    coltAlgebra.solve(matrix(arguments[0]), matrix(arguments[1]));
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("colt", "1.2.0");
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
