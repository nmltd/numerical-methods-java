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
package com.numericalmethod.benchmark.operation.optimization;

import com.numericalmethod.benchmark.implementation.datatype.Argument;
import static com.numericalmethod.benchmark.implementation.datatype.Arguments.sparse;
import static com.numericalmethod.benchmark.implementation.datatype.Arguments.vector;
import com.numericalmethod.benchmark.operation.BenchmarkOperation;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.Matrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.sparse.CSRSparseMatrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.sparse.SparseMatrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.operation.MatrixFactory;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.Vector;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense.DenseVector;
import com.numericalmethod.suanshu.stats.random.rng.univariate.RandomLongGenerator;
import com.numericalmethod.suanshu.stats.random.rng.univariate.RandomNumberGenerator;
import com.numericalmethod.suanshu.stats.random.rng.univariate.uniform.UniformRNG;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hao Ding
 */
public class SolvingSparseSOCP implements BenchmarkOperation {

    private final int problemSize;

    public SolvingSparseSOCP(int problemSize) {
        this.problemSize = problemSize;
    }

    public String getTitle() {
        return String.format("solving sparse SOCP (%d variables, %d constraints)",
                             problemSize, 2 * problemSize);
    }

    public List<Argument> getArguments() {
        // generates A, b and c so that there is always a solution
        RandomLongGenerator rng = new UniformRNG();
        CSRSparseMatrix A = MatrixFactory.randomCSRSparseMatrix(
            problemSize, 2 * problemSize, 2 * problemSize * problemSize / 10, rng);
        Vector x = generateSOCVector();
        Vector s = generateSOCVector();
        Vector y = MatrixFactory.randomCSRSparseMatrix(
            problemSize, 1, problemSize / 10, rng).getColumn(1);
        Vector b = A.multiply(x); // b=Ax
        Vector c = A.t().multiply(y).add(s); // c=Aty+c
        List<Argument> matrices = new ArrayList<Argument>();
        List<SparseMatrix.Entry> entrylist = A.getEntrytList();
        int[] rowIndex = new int[entrylist.size()];
        int[] colIndex = new int[entrylist.size()];
        double[] values = new double[entrylist.size()];
        for (int i = 0; i < entrylist.size(); i++) {
            rowIndex[i] = entrylist.get(i).coordinates.i;
            colIndex[i] = entrylist.get(i).coordinates.j;
            values[i] = entrylist.get(i).value;
        }
        matrices.add(sparse(A.nRows(), A.nCols(), rowIndex, colIndex, values));
        matrices.add(vector(b.toArray()));
        matrices.add(vector(c.toArray()));
        return matrices;
    }

    /**
     * Generates a vector x, so that it is in the second order cone.
     */
    private Vector generateSOCVector() {
        RandomNumberGenerator rng = new UniformRNG();
        Matrix A = MatrixFactory.randomDenseMatrix(2 * problemSize - 1, 1, rng);
        Vector a = A.getColumn(1);
        Vector x = new DenseVector(2 * problemSize);
        for (int i = 1; i <= 2 * problemSize - 1; i++) {
            x.set(i + 1, a.get(i));
        }
        x.set(1, a.norm() * 2); // x(1)=2*||x(2:n)||_2, => x(1)>2*||x(2:n)||_2
        return x;
    }
}
