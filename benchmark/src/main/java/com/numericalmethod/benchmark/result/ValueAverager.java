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

import static com.numericalmethod.suanshu.number.DoubleUtils.collection2DoubleArray;
import com.numericalmethod.suanshu.stats.descriptive.moment.Mean;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ken Yiu
 */
public class ValueAverager implements ResultProcessor {

    private final int nDiscard;

    public ValueAverager() {
        this(0);
    }

    public ValueAverager(int nDiscard) {
        this.nDiscard = nDiscard;
    }

    public Map<String, Double> process(
        Map<String, List<Double>> results) {
        Map<String, Double> meanValues = new HashMap<String, Double>();
        for (Map.Entry<String, List<Double>> entry : results.entrySet()) {

            List<Double> values = entry.getValue();

            if (nDiscard != 0) {
                Collections.sort(values);

                // discard only when there are enough values
                for (int i = 0; i < nDiscard && values.size() > 2; ++i) {
                    values.remove(0); // remove the n smallest
                    values.remove(values.size() - 1);
                }
            }

            double mean = new Mean(collection2DoubleArray(values)).value();
            meanValues.put(entry.getKey(), mean);
        }

        return meanValues;
    }
}
