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
import dev.nm.algebra.linear.matrix.doubles.matrixtype.sparse.CSRSparseMatrix;
import dev.nm.algebra.linear.matrix.doubles.operation.MatrixUtils;
import java.util.Arrays;
import java.util.List;
import org.rosuda.REngine.JRI.JRIEngine;
import org.rosuda.REngine.*;

/**
 *
 * @author Ken Yiu
 */
public class RImplementation extends AbstractImplementation {

    /**
     * This engine enables a faster conversion to R matrix.
     */
    private static final REngine rEngine = newREngine();
    private final REXP environment; // stands for the R-environment

    private static REngine newREngine() {
        REngine rEngine;
        try {
            rEngine = new JRIEngine();
            rEngine.close(); // TODO: why can we still use it after close()?
        } catch (REngineException ex) {
            throw new RuntimeException("failed to create REngine", ex);
        }
        return rEngine;
    }

    private abstract class RExecutable implements Executable {

        private final List<String> requiredLibraries;

        abstract void execute();

        RExecutable(String... requiredLibraries) {
            this.requiredLibraries = Arrays.asList(requiredLibraries);
        }

        @Override
        public void preExecute() {
            loadLibraries();
        }

        @Override
        public void preEachExecute() {
            // do nothing;
        }

        @Override
        public void execute(Object[] arguments) {
            assignVariables(arguments);

            execute();
        }

        @Override
        public void postEachExecute() {
            cleanup();
        }

        @Override
        public void postExecute() {
            unloadLibraries();
        }

        private void loadLibraries() {
            for (String library : requiredLibraries) {
                loadLibrary(library);
            }
        }

        private void unloadLibraries() {
            for (String library : requiredLibraries) {
                unloadLibrary(library);
            }
        }
    }

    public RImplementation() {
        try {
            this.environment = rEngine.newEnvironment(null, true); // null for no parent;
        } catch (Exception ex) {
            throw new RuntimeException("failed to create new R environment", ex);
        }

        addExecutable(CholeskyDecomposition.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("y <- chol(A0)");
            }
        });
        addExecutable(EigenDecompositionSymmetric.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("y<-eigen(A0, symmetric=TRUE)");
            }
        });
        addExecutable(LUDecomposition.class, new RExecutable("Matrix") {
            /*
             * LU decomposition is built in 'Matrix' library (not in the base library).
             */
            @Override
            public void execute() {
                evaluate("y<-lu(A0)");
            }
        });
        addExecutable(MatrixAddition.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("C <- A0 + A1");
            }
        });
        addExecutable(MatrixDeterminant.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("B <- det(A0)");
            }
        });
        addExecutable(MatrixInverse.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("B <- solve(A0)");
            }
        });
        addExecutable(MatrixInverseSPD.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("result<-chol2inv(chol(A0))");
            }
        });
        addExecutable(MatrixMultiplication.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("C <- A0 %*% A1");
            }
        });
        addExecutable(MatrixScale.class, new RExecutable() {
            @Override
            public void execute() {
                assign("alpha", MatrixScale.SCALAR);
                evaluate("B <- alpha * A0");
            }
        });
        addExecutable(MatrixTranspose.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("B <- t(A0)");
            }
        });
        addExecutable(QRDecomposition.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("y <- qr(A0)");
            }
        });
        addExecutable(SingularValueDecomposition.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("y <- svd(A0)");
            }
        });
        addExecutable(SolvingEquality.class, new RExecutable() {
            @Override
            public void execute() {
                evaluate("solution <- solve(A0, A1)");
            }
        });
        addExecutable(SolvingSOCP.class, new RExecutable("CLSOCP") {
            @Override
            public void execute() {
                evaluate("solution<-socp(A0,A1,A2,dim(A0)[2], use_sparse=FALSE)");
            }
        });
        /*
         * CLSOCP does not consider the feasible of the solution, therefore it is replaced with
         * DWD package.
         */
//        addExecutable(SolvingSparseSOCP.class, new RExecutable("CLSOCP") {
//            public void execute() {
//                evaluate("solution<-socp(A0,A1,A2,dim(A0)[2], use_sparse=FALSE)");
//            }
//        });
        addExecutable(SolvingSparseSOCP.class, new RExecutable("DWD") {
            @Override
            public void execute() {
                // A0=A, A1=b, A2=c
                evaluate("AColNorm<-rep(0,dim(A0)[2]);for(i in 1:dim(A0)[2]) AColNorm[i]<-sum(A0[,i]^2)^0.5");
                evaluate("term<-(1+A1)/(1+AColNorm[1:dim(A1)[1]]);xi<-max(1,max(term))");
                evaluate("ARowNorm<-rep(0,dim(A0)[1]);for(i in 1:dim(A0)[1]) ARowNorm[i]<-sum(A0[i,]^2)^0.5");
                evaluate("normMax<-max(sum(A2^2)^0.5,max(ARowNorm))");
                evaluate("eta<-max(1,(1+normMax)/dim(A0)[2]^0.5)");
                evaluate("x0<-c(xi,rep(0,dim(A0)[2]-1));s0<-c(eta,rep(0,dim(A0)[2]-1));y0<-rep(0,dim(A0)[1])");
                evaluate("A<-list(A0);C<-list(matrix(A2,ncol=1));b<-matrix(A1,ncol=1)");
                evaluate("si<-list(matrix(c(dim(A0)[2]),1,1));block<-list(type=\"q\",size=si)");
                evaluate("X0<-list(matrix(x0,ncol=1));S0<-list(matrix(s0,ncol=1));Y0<-matrix(y0,ncol=1)");
                evaluate("solution<-sqlp(blk=block,At=A,C=C,b=b,X0=X0,y0=Y0,Z0=S0)");
            }
        });
        addExecutable(SolvingSDP.class, new RExecutable("Rcsdp") {
            @Override
            public void execute() {
                evaluate("control=list(printlevel=0)"); // suppress output from Rcsdp
                evaluate("solution<-csdp(list(A2),list(list(A0)),A1,list(type=c(\"s\"),size=c(dim(A2)[1])),control)");
            }
        });
    }

    @Override
    public LibraryInfo getLibraryInfo() {
        /*
         * For R-3.0.0 or R-3.0.2, JRI fails to load the 'Matrix' library; also,
         * some operations fail to return valid results, e.g., Cholesky factorization,
         * eigen decomposition, LU decomposition, matrix determinant, matrix inverse,
         * SPD matrix inverse, QR decomposition, SVD factorization and solving
         * (equal) linear systems.
         *
         * For R-2.14, everything works well with JRI.
         */
        return new LibraryInfo("R", "2.14");
    }

    @Override
    public ArgumentConverter getArgumentConverter() {
        return new ArgumentConverter() {
            @Override
            public Object convert(Argument argument) {
                if (argument instanceof DenseMatrixArgument) {
                    return REXP.createDoubleMatrix(((DenseMatrixArgument) argument).get2dArray());
                }

                if (argument instanceof DenseVectorArgument) {
                    double[] data = ((DenseVectorArgument) argument).getArray();
                    double[][] columnMatrix = new double[data.length][1];
                    for (int i = 0; i < data.length; ++i) {
                        columnMatrix[i][0] = data[i];
                    }

                    return REXP.createDoubleMatrix(columnMatrix);
                }

                if (argument instanceof SparseMatrixArgument) {
                    SparseMatrixArgument sparse = (SparseMatrixArgument) argument;
                    CSRSparseMatrix A = new CSRSparseMatrix(
                        sparse.nRows(),
                        sparse.nCols(),
                        sparse.getRowIndices(),
                        sparse.getColumnIndices(),
                        sparse.getValues());
                    double[][] array2d = MatrixUtils.to2DArray(A);
                    return REXP.createDoubleMatrix(array2d);
                }

                throw new ConversionNotSupported(argument);
            }
        };
    }

    private void assignVariables(Object[] arguments) {
        for (int i = 0; i < arguments.length; ++i) {
            String variableName = String.format("A%d", i);
            try {
                // assign the arguments into R environment
                rEngine.assign(variableName, (REXP) arguments[i]);
            } catch (Exception ex) {
                throw new RuntimeException(
                    String.format("failed to assign argument to '%s'", variableName), ex);
            }
        }
    }

    private void cleanup() {
        /*
         * Note: Since Java's garbage collection does not synchronize with R engine, calling "rm"
         * alone in R does not guarantee the object reference in Java to be cleaned up. Therefore,
         * the safe way is to first call System.gc() in Java, then call "rm" in R.
         *
         * (There is no such documentation in rJava or JRI homepage. These are just our own
         * hypothesis based on observation on memory usage without concrete proof.)
         */
        System.gc();
        evaluate("rm(list=ls(all.names=TRUE))");
    }

    public double asDouble(String rVariable) {
        try {
            return evaluate(rVariable).asDouble();
        } catch (REXPMismatchException ex) {
            throw new RuntimeException(
                String.format("failed to evaluate R variable '%s'", rVariable), ex);
        }
    }

    public REngine getRengine() {
        return rEngine;
    }

    public REXP getEnvironment() {
        return environment;
    }

    public void assign(String variableName, double value) {
        try {
            rEngine.assign(variableName, new double[]{value});
        } catch (REngineException ex) {
            throw new RuntimeException(
                String.format("failed to assign value to '%s'", variableName), ex);
        }
    }

    public REXP evaluate(String rCommand) {
        try {
            return rEngine.parseAndEval(rCommand, environment, true);
        } catch (REngineException ex) {
            throw new RuntimeException(ex);
        } catch (REXPMismatchException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Loads a R library. That is,
     * <pre>
     * {@code
     * library(libraryName)
     * }
     * </pre>
     *
     * Note:
     * <ul>
     * <li>
     * Some matrix operations require to load 'Matrix' library in R. For example, LU
     * decomposition. Note that loading 'Matrix' library will significantly
     * increases the execution time (2 ~ 10 times longer) for almost all operations.
     * From our observation, unloading the loaded library (nor creating a new environment) does not
     * really resolve the performance degradation issue caused by library loading.
     * </li>
     * <li>
     * Please make sure you have installed those required libraries in R's folder (not user's home
     * nor documents). Otherwise, JRI is not able to load those libraries.
     * </li>
     * </ul>
     *
     * @param libraryName the name of the library
     */
    public void loadLibrary(String libraryName) {
        String command = String.format("library(%s)", libraryName);
        if (evaluate(command).isNull()) { // should return a list of attached packages
            throw new RuntimeException(
                String.format("failed to load R library '%s'", libraryName));
        }
    }

    /**
     * Unloads a R library. That is,
     * <pre>
     * {@code
     * detach("package:libraryName", unload=TRUE)
     * }
     * </pre>
     *
     * @param libraryName the name of the library
     */
    public void unloadLibrary(String libraryName) {
        String command = String.format("detach(\"package:%s\", unload=TRUE)", libraryName);
        if (!evaluate(command).isNull()) { // successful detach returns NULL
            throw new RuntimeException(
                String.format("failed to unload R library '%s'", libraryName));
        }
    }
}
