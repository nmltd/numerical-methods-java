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
package com.numericalmethod.benchmark.operation.linearalgebra;

import com.numericalmethod.benchmark.implementation.datatype.Argument;
import static com.numericalmethod.benchmark.implementation.datatype.Arguments.dense;
import com.numericalmethod.benchmark.operation.BenchmarkOperation;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.operation.MatrixFactory;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.operation.MatrixUtils;
import com.numericalmethod.suanshu.stats.random.rng.univariate.RandomNumberGenerator;
import com.numericalmethod.suanshu.stats.random.rng.univariate.uniform.UniformRNG;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ken Yiu
 */
public class MatrixMultiplication implements BenchmarkOperation {

    private final int nRows;
    private final int nCols1;
    private final int nCols2;

    public MatrixMultiplication(int nRows, int nCols1, int nCols2) {
        this.nRows = nRows;
        this.nCols1 = nCols1;
        this.nCols2 = nCols2;
    }

    @Override
    public String getTitle() {
        return String.format("matrix multiplication (%dx%d)x(%dx%d)",
                             nRows, nCols1, nCols1, nCols2);
    }

    @Override
    public List<Argument> getArguments() {
        RandomNumberGenerator rng = new UniformRNG();

        DenseMatrix A = MatrixFactory.randomDenseMatrix(nRows, nCols1, rng);
        DenseMatrix B = MatrixFactory.randomDenseMatrix(nCols1, nCols2, rng);

        List<Argument> matrices = new ArrayList<Argument>(2);
        matrices.add(dense(MatrixUtils.to2DArray(A)));
        matrices.add(dense(MatrixUtils.to2DArray(B)));
        return matrices;
    }
}
