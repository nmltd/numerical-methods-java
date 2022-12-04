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
import dev.nm.algebra.linear.matrix.doubles.Matrix;
import dev.nm.algebra.linear.matrix.doubles.operation.MatrixFactory;
import dev.nm.algebra.linear.matrix.doubles.operation.MatrixUtils;
import dev.nm.stat.random.rng.univariate.RandomNumberGenerator;
import dev.nm.stat.random.rng.univariate.uniform.UniformRNG;
import java.util.ArrayList;
import java.util.List;

/**
 * Runtime benchmark tests of eigen value decomposition of symmetric matrices.
 *
 * @author Song Lin
 */
public class EigenDecompositionSymmetric implements BenchmarkOperation {

    private final int matrixSize;

    public EigenDecompositionSymmetric(int matrixSize) {
        this.matrixSize = matrixSize;
    }

    @Override
    public String getTitle() {
        return String.format("symmetric eigen value decomposition (%dx%d)", matrixSize, matrixSize);
    }

    @Override
    public List<Argument> getArguments() {
        RandomNumberGenerator rng = new UniformRNG();
        Matrix S = MatrixFactory.randomSymmetricMatrix(matrixSize, rng);

        List<Argument> matrices = new ArrayList<Argument>(1);
        matrices.add(dense(MatrixUtils.to2DArray(S)));
        return matrices;
    }
}
