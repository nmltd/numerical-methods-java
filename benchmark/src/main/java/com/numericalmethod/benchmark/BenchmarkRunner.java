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

import com.numericalmethod.benchmark.time.StopWatch;
import com.numericalmethod.benchmark.time.formatter.MillisFormatter;
import com.numericalmethod.benchmark.time.formatter.PeriodFormatter;
import com.numericalmethod.suanshu.number.DoubleUtils;
import com.numericalmethod.suanshu.stats.descriptive.moment.Mean;
import com.numericalmethod.suanshu.stats.descriptive.moment.Variance;
import java.util.List;

/**
 *
 * @author Ken Yiu
 */
public class BenchmarkRunner {

    public static interface RunnableOperation {

        /**
         * Does something before each run (but not timed).
         */
        void preRun() throws Exception;

        void run() throws Exception;

        /**
         * Does something after each run (but not timed).
         */
        void postRun() throws Exception;
    }
    private final int nRuns;
    private final PeriodFormatter formatter;
    private final boolean showMessage;

    public BenchmarkRunner(int nRuns) {
        this(nRuns, new MillisFormatter(), false);
    }

    public BenchmarkRunner(int nRuns, boolean showMessage) {
        this(nRuns, new MillisFormatter(), showMessage);
    }

    public BenchmarkRunner(int nRuns, PeriodFormatter formatter, boolean showMessage) {
        this.nRuns = nRuns;
        this.formatter = formatter;
        this.showMessage = showMessage;
    }

    public List<Double> run(String operationName, RunnableOperation operation) {
        message("running %s for %d times...%n", operationName, nRuns);
        double[] timeTaken = new double[nRuns];

        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < nRuns; ++i) {
            try {
                operation.preRun();

                stopWatch.start();

                operation.run();

                stopWatch.stop();

                operation.postRun();

                timeTaken[i] = stopWatch.value();
                message("%d) %s%n", i + 1, formatter.format(timeTaken[i]));
            } catch (Exception ex) {
                timeTaken[i] = Double.NaN;
                message("%d) exception thrown (%s)%n", i + 1, ex);
                throw new RuntimeException(
                    String.format("exception thrown when running operation '%s' in run %d",
                                  operationName, i),
                    ex);
            }
        }

        double average = new Mean(timeTaken).value();
        double stdev = new Variance(timeTaken).standardDeviation();

        System.out.printf("%s takes %s +/- %s%n",
                          operationName,
                          formatter.format(average),
                          formatter.format(stdev));

        return DoubleUtils.doubleArray2List(timeTaken);
    }

    private void message(String format, Object... args) {
        if (!showMessage) {
            return;
        }

        System.out.printf(format, args);
    }
}
