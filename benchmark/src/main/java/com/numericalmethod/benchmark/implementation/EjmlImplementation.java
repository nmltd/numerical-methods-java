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
import org.ejml.alg.dense.decomposition.eig.SwitchingEigenDecomposition;
import org.ejml.data.*;
import static org.ejml.factory.DecompositionFactory.*;
import static org.ejml.factory.LinearSolverFactory.*;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CovarianceOps;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author Ken Yiu
 */
public class EjmlImplementation extends AbstractImplementation {

    public EjmlImplementation() {
        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                int matrixSize = matrix(arguments[0]).getMatrix().getNumRows();
                org.ejml.interfaces.decomposition.CholeskyDecomposition<DenseMatrix64F> chol
                    = chol(matrixSize, true);
                chol.decompose(matrix(arguments[0]).getMatrix());
                Matrix64F L = chol.getT(null);
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                int matrixSize = matrix(arguments[0]).getMatrix().getNumRows();
                SwitchingEigenDecomposition eigen = new SwitchingEigenDecomposition(matrixSize);
                eigen.decompose(matrix(arguments[0]).getMatrix());
                Complex64F[] eigenvalues = new Complex64F[eigen.getNumberOfEigenvalues()];
                DenseMatrix64F[] eigenvectors = new DenseMatrix64F[eigen.getNumberOfEigenvalues()];
                for (int i = 0; i < eigenvalues.length; ++i) {
                    eigenvalues[i] = eigen.getEigenvalue(i);
                    eigenvectors[i] = eigen.getEigenVector(i);
                }
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                int matrixSize = matrix(arguments[0]).getMatrix().getNumRows();
                org.ejml.interfaces.decomposition.LUDecomposition<DenseMatrix64F> lu
                    = lu(matrixSize, matrixSize);
                lu.decompose(matrix(arguments[0]).getMatrix());
                Matrix64F L = lu.getLower(null);
                Matrix64F U = lu.getUpper(null);
                Matrix64F P = lu.getPivot(null);
            }
        });
        addExecutable(MatrixAddition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                SimpleMatrix C = matrix(arguments[0]).plus(matrix(arguments[1]));
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
                SimpleMatrix B = matrix(arguments[0]).invert();
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // the input matrix is a covariance matrix
                int matrixSize = matrix(arguments[0]).getMatrix().getNumRows();
                SimpleMatrix result = new SimpleMatrix(matrixSize, matrixSize);
                CovarianceOps.invert(matrix(arguments[0]).getMatrix(), result.getMatrix());
            }
        });
        addExecutable(MatrixMultiplication.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                SimpleMatrix C = matrix(arguments[0]).mult(matrix(arguments[1]));
            }
        });
        addExecutable(MatrixScale.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                SimpleMatrix B = matrix(arguments[0]).scale(MatrixScale.SCALAR);
            }
        });
        addExecutable(MatrixTranspose.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                SimpleMatrix B = matrix(arguments[0]).transpose();
            }
        });
        addExecutable(QRDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                int matrixSize = matrix(arguments[0]).getMatrix().getNumRows();
                org.ejml.interfaces.decomposition.QRDecomposition<DenseMatrix64F> qr
                    = qr(matrixSize, matrixSize);
                qr.decompose(matrix(arguments[0]).getMatrix());
                Matrix64F Q = qr.getQ(null, true);
                Matrix64F R = qr.getR(null, true);
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                int matrixSize = matrix(arguments[0]).getMatrix().getNumRows();
                org.ejml.interfaces.decomposition.SingularValueDecomposition<DenseMatrix64F> svd
                    = svd(matrixSize, matrixSize, true, true, true);
                svd.decompose(matrix(arguments[0]).getMatrix());
                Matrix64F W = svd.getW(null);
                Matrix64F U = svd.getU(null, false);
                Matrix64F V = svd.getV(null, false);
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                int matrixSize = matrix(arguments[0]).getMatrix().getNumRows();
                LinearSolver<DenseMatrix64F> solver = linear(matrixSize);
                SimpleMatrix solution = new SimpleMatrix(matrixSize, matrixSize);
                solver.setA(matrix(arguments[0]).getMatrix());
                solver.solve(matrix(arguments[1]).getMatrix(), solution.getMatrix());
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("ejml", "0.25");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    return new SimpleMatrix(((DenseMatrixArgument) argument).get2dArray());
                }

                throw new ConversionNotSupported(argument);
            }
        };
    }

    private SimpleMatrix matrix(Object matrix) {
        return (SimpleMatrix) matrix;
    }
}
