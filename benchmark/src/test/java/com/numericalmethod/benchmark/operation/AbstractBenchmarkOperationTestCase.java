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
package com.numericalmethod.benchmark.operation;

import com.numericalmethod.benchmark.BenchmarkRunner;
import com.numericalmethod.benchmark.OperationBenchmarker;
import com.numericalmethod.benchmark.implementation.Implementation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Ken Yiu
 */
@Ignore("this is just a testcase template")
public abstract class AbstractBenchmarkOperationTestCase {

    private final List<Implementation> implementations =
        new ArrayList<Implementation>();

    protected abstract void addAllImplementations(List<Implementation> implementations);

    protected abstract BenchmarkOperation newInstance();

    @Before
    public void addAllImplementations() {
        addAllImplementations(implementations);
    }

    @Test
    public void test() {
        BenchmarkOperation operation = newInstance();

        int nRuns = 2; // run 2 times to make sure repetition works fine
        OperationBenchmarker benchmarker =
            new OperationBenchmarker(new BenchmarkRunner(nRuns));

        Map<String, List<Double>> results = benchmarker.run(implementations, operation);

        assertNotNull(results);
    }
}
