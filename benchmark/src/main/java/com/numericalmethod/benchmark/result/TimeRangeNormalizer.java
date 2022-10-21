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
package com.numericalmethod.benchmark.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ken Yiu
 */
public class TimeRangeNormalizer implements ResultProcessor {

    private final ValueAverager averager = new ValueAverager();

    public Map<String, Double> process(
        Map<String, List<Double>> results) {
        Map<String, Double> means = averager.process(results);

        return normalize(means);
    }

    private Map<String, Double> normalize(
        Map<String, Double> results) {

        Map<String, Double> times = new HashMap<String, Double>(results);

        // find min/max time
        double minTime = Double.POSITIVE_INFINITY;
        double maxTime = Double.NEGATIVE_INFINITY;
        for (Map.Entry<String, Double> entry : results.entrySet()) {
            double time = entry.getValue();

            if (time < minTime) {
                minTime = time;
            }

            if (time > maxTime) {
                maxTime = time;
            }
        }

        // normalize times
        double timeRange = maxTime - minTime;
        for (Map.Entry<String, Double> entry : times.entrySet()) {
            double normalized = (entry.getValue() - minTime) / timeRange;
            entry.setValue(normalized);
        }

        return times;
    }
}
