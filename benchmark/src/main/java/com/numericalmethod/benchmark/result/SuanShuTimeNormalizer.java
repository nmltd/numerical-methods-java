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

import com.numericalmethod.benchmark.main.EvaluatedLinearAlgebraLibrary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ken Yiu
 */
public class SuanShuTimeNormalizer implements ResultProcessor {

    private final ValueAverager averager;

    public SuanShuTimeNormalizer() {
        this(0);
    }

    public SuanShuTimeNormalizer(int nDiscard) {
        this.averager = new ValueAverager(nDiscard); // discard the max and min values
    }

    public Map<String, Double> process(
        Map<String, List<Double>> results) {

        Map<String, Double> means = averager.process(results);

        return normalize(means);
    }

    private Map<String, Double> normalize(
        Map<String, Double> results) {

        Map<String, Double> times = new HashMap<String, Double>(results);

        // time used by suanshu
        if (!times.containsKey(EvaluatedLinearAlgebraLibrary.SUANSHU.toString())) {
            throw new RuntimeException("time for suanshu execution does not exist");
        }
        double ssTime = times.get(EvaluatedLinearAlgebraLibrary.SUANSHU.toString());

        // normalize times
        for (Map.Entry<String, Double> entry : times.entrySet()) {
            double normalized = entry.getValue() / ssTime;
            entry.setValue(normalized);
        }

        return times;
    }
}
