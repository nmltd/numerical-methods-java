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
import static com.numericalmethod.benchmark.implementation.datatype.Arguments.dense;
import static com.numericalmethod.benchmark.implementation.datatype.Arguments.vector;
import com.numericalmethod.benchmark.operation.BenchmarkOperation;
import dev.nm.algebra.linear.matrix.doubles.Matrix;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.triangle.SymmetricMatrix;
import dev.nm.algebra.linear.matrix.doubles.operation.MatrixFactory;
import dev.nm.algebra.linear.matrix.doubles.operation.MatrixUtils;
import dev.nm.algebra.linear.vector.doubles.Vector;
import dev.nm.algebra.linear.vector.doubles.dense.DenseVector;
import dev.nm.stat.random.rng.univariate.RandomNumberGenerator;
import dev.nm.stat.random.rng.univariate.uniform.UniformRNG;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hao Ding
 */
public class SolvingSDP implements BenchmarkOperation {

    private final int problemSize;

    public SolvingSDP(int problemSize) {
        this.problemSize = problemSize;
    }

    public String getTitle() {
        return String.format("solving SDP (%d variables, %d constraints)", problemSize, 1);
    }

    public List<Argument> getArguments() {
        RandomNumberGenerator rng = new UniformRNG();
        Matrix C0 = MatrixFactory.randomDenseMatrix(problemSize, problemSize, rng);
        SymmetricMatrix C = new SymmetricMatrix(C0.multiply(C0.t())); // C=C0*C0t, so that C is semi positive definite
        Matrix A0 = MatrixFactory.randomDenseMatrix(problemSize, problemSize, rng);
        SymmetricMatrix A = new SymmetricMatrix(A0.multiply(A0.t())); // A=A0*A0t, so that A is semi positive definite
        for (int i = 1; i <= problemSize; i++) {
            C.set(i, i, problemSize * 100); // increases the diagonal entries of C, so that C is strictly positive definite
            A.set(i, i, problemSize * 100); // increases the diagonal entries of A, so that A is strictly positive definite
        }
        Vector b = new DenseVector(rng.nextDouble()); // dimension of b is 1, because there is only one constraint

        List<Argument> matrices = new ArrayList<Argument>();
        matrices.add(dense(MatrixUtils.to2DArray(A)));
        matrices.add(vector(b.toArray()));
        matrices.add(dense(MatrixUtils.to2DArray(C)));
        return matrices;
    }
}
