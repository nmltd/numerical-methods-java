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
package com.numericalmethod.benchmark.main;

import com.numericalmethod.benchmark.Benchmark;
import com.numericalmethod.benchmark.operation.linearalgebra.*;
import com.numericalmethod.benchmark.operation.optimization.*;
import com.numericalmethod.benchmark.result.BenchmarkResults;
import com.numericalmethod.benchmark.result.SuanShuTimeNormalizer;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Runs the benchmark for a collection of common operations.
 *
 * @author Ken Yiu
 */
public class Main {

    public static void main(String[] args) throws IOException {
        int nRuns = 1;
        if (args.length >= 1) {
            nRuns = Integer.parseInt(args[0]);
        }
        System.out.println("nRuns = " + nRuns);
        boolean detailMode = false;
        if (args.length >= 2) {
            detailMode = Boolean.parseBoolean(args[1]);
        }
        System.out.println("detailMode = " + detailMode);

        Main main = new Main(detailMode);
        main.run(nRuns);
    }

    private final boolean detailMode;

    public Main() {
        this(false);
    }

    public Main(boolean detailMode) {
        this.detailMode = detailMode;
    }

    public void run(int nRuns) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        saveResults(benchmarkLinearAlgebraOperations(nRuns), "linearalgebra", timestamp);
        saveResults(benchmarkOptimizationOperations(nRuns), "optimization", timestamp);
    }

    private void saveResults(BenchmarkResults results, String category, String timestamp) throws IOException {
        String fileName = String.format("record/%s/record-%s.csv", category, timestamp);
        results.saveAsCsv(fileName);
    }

    private BenchmarkResults benchmarkLinearAlgebraOperations(int nRuns) {
        Benchmark benchmark = new Benchmark(nRuns, new SuanShuTimeNormalizer(2), detailMode);

        for (EvaluatedLinearAlgebraLibrary library : EvaluatedLinearAlgebraLibrary.values()) {
            benchmark.addImplementation(library.getImplementation());
        }

        benchmark.addOperation(new MatrixTranspose(5000, 5000));
        benchmark.addOperation(new MatrixAddition(5000, 5000));
        benchmark.addOperation(new MatrixScale(5000, 5000));
        benchmark.addOperation(new MatrixMultiplication(2000, 2000, 2000));
        benchmark.addOperation(new MatrixDeterminant(2000));
        benchmark.addOperation(new MatrixInverse(2000));
        benchmark.addOperation(new MatrixInverseSPD(2000));
        benchmark.addOperation(new CholeskyDecomposition(5000));
        benchmark.addOperation(new LUDecomposition(2000));
        benchmark.addOperation(new QRDecomposition(2000, 1000));
        benchmark.addOperation(new EigenDecompositionSymmetric(2000));
        benchmark.addOperation(new SingularValueDecomposition(2000, 2000));
        benchmark.addOperation(new SolvingEquality(2000));

        BenchmarkResults results = benchmark.run();
        return results;
    }

    private BenchmarkResults benchmarkOptimizationOperations(int nRuns) {
        Benchmark benchmark = new Benchmark(nRuns, new SuanShuTimeNormalizer(2), detailMode);

        for (EvaluatedOptimizationLibrary library : EvaluatedOptimizationLibrary.values()) {
            benchmark.addImplementation(library.getImplementation());
        }

        benchmark.addOperation(new SolvingSOCP(200));
        benchmark.addOperation(new SolvingSOCP(400));
        benchmark.addOperation(new SolvingSOCP(800));
        benchmark.addOperation(new SolvingSparseSOCP(200));
        benchmark.addOperation(new SolvingSparseSOCP(400));
        benchmark.addOperation(new SolvingSparseSOCP(800));
        benchmark.addOperation(new SolvingSDP(20));
        benchmark.addOperation(new SolvingSDP(40));
//        benchmark.addOperation(new SolvingSDP(80)); // takes too long time

        BenchmarkResults results = benchmark.run();
        return results;
    }

}
