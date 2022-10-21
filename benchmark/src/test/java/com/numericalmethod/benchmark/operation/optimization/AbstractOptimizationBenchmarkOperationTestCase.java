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

import com.numericalmethod.benchmark.implementation.Implementation;
import com.numericalmethod.benchmark.main.EvaluatedOptimizationLibrary;
import com.numericalmethod.benchmark.operation.AbstractBenchmarkOperationTestCase;
import java.util.List;
import org.junit.Ignore;

/**
 *
 * @author Ken Yiu
 */
@Ignore("this is just a testcase template")
public abstract class AbstractOptimizationBenchmarkOperationTestCase
    extends AbstractBenchmarkOperationTestCase {

    protected void addAllImplementations(List<Implementation> implementations) {
        for (EvaluatedOptimizationLibrary library : EvaluatedOptimizationLibrary.values()) {
            implementations.add(library.getImplementation());
        }
    }
}
