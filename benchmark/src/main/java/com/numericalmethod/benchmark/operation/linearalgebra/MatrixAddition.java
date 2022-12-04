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
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import dev.nm.algebra.linear.matrix.doubles.operation.MatrixFactory;
import dev.nm.algebra.linear.matrix.doubles.operation.MatrixUtils;
import dev.nm.stat.random.rng.univariate.RandomNumberGenerator;
import dev.nm.stat.random.rng.univariate.uniform.UniformRNG;
import java.util.ArrayList;
import java.util.List;

/**
 * Runtime benchmark tests of matrix addition.
 *
 * @author Song Lin
 */
public class MatrixAddition implements BenchmarkOperation {

    private final int nRows;
    private final int nCols;

    public MatrixAddition(int nRows, int nCols) {
        this.nRows = nRows;
        this.nCols = nCols;
    }

    @Override
    public String getTitle() {
        return String.format("matrix addition (%dx%d)", nRows, nCols);
    }

    @Override
    public List<Argument> getArguments() {
        RandomNumberGenerator rng = new UniformRNG();

        DenseMatrix A = MatrixFactory.randomDenseMatrix(nRows, nCols, rng);
        DenseMatrix B = MatrixFactory.randomDenseMatrix(nRows, nCols, rng);

        List<Argument> matrices = new ArrayList<Argument>(2);
        matrices.add(dense(MatrixUtils.to2DArray(A)));
        matrices.add(dense(MatrixUtils.to2DArray(B)));
        return matrices;
    }
}
