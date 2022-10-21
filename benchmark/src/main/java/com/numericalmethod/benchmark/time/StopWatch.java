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
package com.numericalmethod.benchmark.time;

import com.numericalmethod.benchmark.time.formatter.PeriodFormatter;
import com.numericalmethod.benchmark.time.formatter.TimeUnitFormatter;

/**
 *
 * @author Ken Yiu
 */
public class StopWatch {

    private static PeriodFormatter PERIOD_FORMATTER = new TimeUnitFormatter();
    private long startInstant = 0;
    private long stopInstant = 0;

    public void start() {
        startInstant = System.currentTimeMillis();
    }

    public void stop() {
        stopInstant = System.currentTimeMillis();
    }

    public long value() {
        return stopInstant - startInstant;
    }

    @Override
    public String toString() {
        return PERIOD_FORMATTER.format(value());
    }
}
