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
import com.numericalmethod.benchmark.implementation.datatype.*;
import com.numericalmethod.benchmark.implementation.library.LibraryInfo;
import com.numericalmethod.benchmark.operation.linearalgebra.*;
import com.numericalmethod.benchmark.operation.optimization.*;
import dev.nm.algebra.linear.matrix.doubles.Matrix;
import dev.nm.algebra.linear.matrix.doubles.factorization.eigen.EigenDecomposition;
import dev.nm.algebra.linear.matrix.doubles.factorization.qr.QR;
import dev.nm.algebra.linear.matrix.doubles.factorization.svd.SVD;
import dev.nm.algebra.linear.matrix.doubles.factorization.triangle.LU;
import dev.nm.algebra.linear.matrix.doubles.factorization.triangle.cholesky.Chol;
import dev.nm.algebra.linear.matrix.doubles.linearsystem.LUSolver;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.triangle.*;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.sparse.CSRSparseMatrix;
import dev.nm.algebra.linear.matrix.doubles.operation.Inverse;
import dev.nm.algebra.linear.matrix.doubles.operation.MatrixMeasure;
import dev.nm.algebra.linear.vector.doubles.Vector;
import dev.nm.algebra.linear.vector.doubles.dense.DenseVector;
import dev.nm.solver.IterativeSolution;
import dev.nm.solver.multivariate.constrained.ConstrainedMinimizer;
//import dev.nm.solver.multivariate.constrained.SubProblemMinimizer;
import dev.nm.solver.multivariate.constrained.convex.sdp.pathfollowing.CentralPath;
import dev.nm.solver.multivariate.constrained.convex.sdp.pathfollowing.PrimalDualPathFollowingMinimizer;
import dev.nm.solver.multivariate.constrained.convex.sdp.problem.SDPDualProblem;
import dev.nm.solver.multivariate.constrained.convex.sdp.socp.interiorpoint.PrimalDualInteriorPointMinimizer;
import dev.nm.solver.multivariate.constrained.convex.sdp.socp.interiorpoint.PrimalDualSolution;
import dev.nm.solver.multivariate.constrained.convex.sdp.socp.problem.SOCPDualProblem;

/**
 *
 * @author Ken Yiu
 */
public class SuanShuImplementation extends AbstractImplementation {

    public SuanShuImplementation() {

        addExecutable(CholeskyDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Chol chol = new Chol(matrix(arguments[0]));
                Object L = chol.L();
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                EigenDecomposition eigen = new EigenDecomposition(matrix(arguments[0]));
                Matrix D = eigen.D();
                Matrix Q = eigen.Q();
            }
        });
        addExecutable(LUDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                LU lu = new LU(matrix(arguments[0]));
                Matrix L = lu.L();
                Matrix U = lu.U();
                Matrix P = lu.P();
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
                double B = MatrixMeasure.det(matrix(arguments[0]));
            }
        });
        addExecutable(MatrixInverse.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix B = new Inverse(matrix(arguments[0]), 0.);
            }
        });
        addExecutable(MatrixInverseSPD.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix B = new Inverse(matrix(arguments[0]), 0.); // same class for SPD inverse
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
                Matrix B = matrix(arguments[0]).scaled(MatrixScale.SCALAR);
            }
        });
        addExecutable(MatrixTranspose.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                Matrix B = matrix(arguments[0]).t();
            }
        });
        addExecutable(QRDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                QR qr = new QR(matrix(arguments[0]));
                Matrix Q = qr.Q();
                Matrix R = qr.R();
            }
        });
        addExecutable(SingularValueDecomposition.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                SVD svd = new SVD(matrix(arguments[0]), true);
                Matrix D = svd.D();
                Matrix U = svd.U();
                Matrix V = svd.V();
            }
        });
        addExecutable(SolvingEquality.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) {
                // solve by LU
                Matrix A = matrix(arguments[0]);
                Matrix B = matrix(arguments[1]);
                LUSolver solver = new LUSolver();
                Matrix X = solver.solve(A, B);
            }
        });
        addExecutable(SolvingSOCP.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) throws Exception {
                SOCPDualProblem problem = new SOCPDualProblem(
                        vector(arguments[1]), // b
                        new Matrix[]{matrix(arguments[0])}, // A
                        new Vector[]{vector(arguments[2])}); // c
                PrimalDualInteriorPointMinimizer socp
                        = new PrimalDualInteriorPointMinimizer(1e-6, 100);
                PrimalDualInteriorPointMinimizer.Solution soln1;
                soln1 = socp.solve(problem);
                soln1.search();
                Vector z = soln1.minimizer().y;
            }
        });
        addExecutable(SolvingSparseSOCP.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) throws Exception {
                SOCPDualProblem problem = new SOCPDualProblem(
                        vector(arguments[1]), // b
                        new Matrix[]{matrix(arguments[0])}, // A
                        new Vector[]{vector(arguments[2])}); // c
                PrimalDualInteriorPointMinimizer socp
                        = new PrimalDualInteriorPointMinimizer(1e-6, 100);
                PrimalDualInteriorPointMinimizer.Solution soln1;
                soln1 = socp.solve(problem);
                soln1.search();
                Vector z = soln1.minimizer().y;
            }
        });
        addExecutable(SolvingSDP.class, new AbstractExecutable() {
            @Override
            public void execute(Object[] arguments) throws Exception {
                SDPDualProblem problem = new SDPDualProblem(
                        vector(arguments[1]), // b
                        new SymmetricMatrix(matrix(arguments[2])), // C
                        new SymmetricMatrix[]{new SymmetricMatrix(matrix(arguments[0]))}); // A[]

                PrimalDualPathFollowingMinimizer pdpf = new PrimalDualPathFollowingMinimizer(1e-6);
                PrimalDualPathFollowingMinimizer.Solution soln = pdpf.solve(problem);
                CentralPath path = soln.search();
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        return new LibraryInfo("suanshu", "3.3.0");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    DenseMatrixArgument A = (DenseMatrixArgument) argument;
                    return new DenseMatrix(A.get1dArray(), A.nRows(), A.nCols());
                }

                if (argument instanceof SparseMatrixArgument) {
                    SparseMatrixArgument sparse = (SparseMatrixArgument) argument;
                    return new CSRSparseMatrix(
                            sparse.nRows(),
                            sparse.nCols(),
                            sparse.getRowIndices(),
                            sparse.getColumnIndices(),
                            sparse.getValues());
                }

                if (argument instanceof DenseVectorArgument) {
                    return new DenseVector(((DenseVectorArgument) argument).getArray());
                }

                throw new ConversionNotSupported(argument);
            }
        };
    }

    private Matrix matrix(Object matrix) {
        return (Matrix) matrix;
    }

    private Vector vector(Object vector) {
        return (Vector) vector;
    }
}
