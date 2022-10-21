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
package com.numericalmethod.benchmark;

import com.numericalmethod.benchmark.implementation.Implementation;
import com.numericalmethod.benchmark.operation.BenchmarkOperation;
import com.numericalmethod.benchmark.result.*;
import java.util.*;

/**
 *
 * @author Ken Yiu
 */
public class Benchmark {

    private final List<Implementation> implementations = new ArrayList<Implementation>();
    private final List<BenchmarkOperation> operations = new ArrayList<BenchmarkOperation>();
    private final OperationBenchmarker benchmarker;
    private final ResultProcessor processor;

    public Benchmark(int nRuns) {
        this(nRuns, new ValueAverager(), false);
    }

    public Benchmark(int nRuns, ResultProcessor processor, boolean showMessage) {
        this.benchmarker = new OperationBenchmarker(new BenchmarkRunner(nRuns, showMessage));
        this.processor = processor;
    }

    public void addImplementation(Implementation implementation) {
        implementations.add(implementation);
    }

    public void addOperation(BenchmarkOperation operation) {
        operations.add(operation);
    }

    public BenchmarkResults run() {
        checkAllOperationsImplemented(); // fail-fast checking for the availability of operations

        BenchmarkResults results = new BenchmarkResults(getAllImplementationNames());

        for (BenchmarkOperation operation : operations) {
            results.addOperationBenchmarkResult(
                operation.getTitle(),
                runOperation(operation));
        }

        return results;
    }

    private void checkAllOperationsImplemented() {
        for (BenchmarkOperation operation : operations) {
            for (Implementation implementation : implementations) {
                if (implementation.getExecutable(operation.getClass()) == null) {
                    throw new RuntimeException(
                        String.format("the implementation of operation '%s' is missing from '%s'",
                                      operation.getClass().getSimpleName(),
                                      implementation.getClass().getSimpleName()));
                }
            }
        }
    }

    private List<String> getAllImplementationNames() {
        List<String> apiNames = new ArrayList<String>(implementations.size());
        for (Implementation implementation : implementations) {
            apiNames.add(implementation.getLibraryInfo().toString());
        }
        return apiNames;
    }

    private Map<String, Double> runOperation(BenchmarkOperation operation) {
        Map<String, List<Double>> results = benchmarker.run(implementations, operation);
        return processor.process(results);
    }
}
