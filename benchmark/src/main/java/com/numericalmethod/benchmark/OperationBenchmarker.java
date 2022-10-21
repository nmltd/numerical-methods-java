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

import com.numericalmethod.benchmark.BenchmarkRunner.RunnableOperation;
import com.numericalmethod.benchmark.implementation.Implementation;
import com.numericalmethod.benchmark.implementation.Implementation.Executable;
import com.numericalmethod.benchmark.implementation.converter.ArgumentConverter;
import com.numericalmethod.benchmark.implementation.datatype.Argument;
import com.numericalmethod.benchmark.implementation.library.LibraryInfo;
import com.numericalmethod.benchmark.operation.BenchmarkOperation;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ken Yiu
 */
public class OperationBenchmarker {

    private static final Logger LOGGER =
        Logger.getLogger(OperationBenchmarker.class.getSimpleName());
    private final BenchmarkRunner benchmarkRunner;

    public OperationBenchmarker(BenchmarkRunner benchmarkRunner) {
        this.benchmarkRunner = benchmarkRunner;
    }

    public Map<String, List<Double>> run(
        Collection<Implementation> implementations,
        BenchmarkOperation operation) {
        Map<String, List<Double>> results = new HashMap<String, List<Double>>();

        final List<Argument> arguments = operation.getArguments();
        for (Implementation implementation : implementations) {
            try {
                String libraryName = formatLibraryName(implementation.getLibraryInfo());
                final Executable executable = implementation.getExecutable(operation.getClass());

                executable.preExecute();

                final ArgumentConverter converter = implementation.getArgumentConverter();
                final List<Argument> cloned = cloneArguments(arguments);
                List<Double> timeTaken = benchmarkRunner.run(
                    formatOperationNameForLibrary(operation.getTitle(), libraryName),
                    new RunnableOperation() {
                    @Override
                    public void preRun() throws Exception {
                        executable.preEachExecute();
                    }

                    @Override
                    public void run() throws Exception {
                        // matrix creation is also timed (some APIs do calculation in constructors)
                        Object[] converted = new Object[cloned.size()];
                        for (int i = 0; i < cloned.size(); ++i) {
                            converted[i] = converter.convert(cloned.get(i));
                        }

                        // execute the operation with the converted matrices
                        executable.execute(converted);
                    }

                    @Override
                    public void postRun() throws Exception {
                        executable.postEachExecute();
                    }
                });

                executable.postExecute();

                results.put(libraryName, timeTaken);
            } catch (Throwable th) {
                LOGGER.log(
                    Level.WARNING,
                    String.format(
                    "operation '%s' from library '%s' throwed exception during execution",
                    operation.getTitle(), implementation.getLibraryInfo()),
                    th);
            }
        }

        return results;
    }

    private static List<Argument> cloneArguments(List<Argument> inputs) {
        final List<Argument> clonedInputs = new ArrayList<Argument>(inputs.size());
        for (Argument input : inputs) {
            Argument cloned = input.deepCopy();
            clonedInputs.add(cloned);
        }

        return clonedInputs;
    }

    private static String formatOperationNameForLibrary(String operationName, String libraryName) {
        return String.format("%s(%s)", operationName, libraryName);
    }

    private static String formatLibraryName(LibraryInfo libraryInfo) {
        return String.format("%s-%s", libraryInfo.getName(), libraryInfo.getVersion());
    }
}
