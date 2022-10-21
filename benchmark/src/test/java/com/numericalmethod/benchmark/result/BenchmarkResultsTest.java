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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Ken Yiu
 */
public class BenchmarkResultsTest {

    @Test
    public void testSaveAsCsv() throws Exception {
        File testFile = File.createTempFile("test", "results.csv");
        String filePath = testFile.getCanonicalPath();
        BenchmarkResults results = new BenchmarkResults(Arrays.asList("API A", "API B"));

        Map<String, Double> result = new HashMap<String, Double>();
        result.put("API A", 2.);
        result.put("API B", 12.);

        results.addOperationBenchmarkResult("dummy operation", result);

        results.saveAsCsv(filePath);

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line = br.readLine();
        assertEquals("operation,API A,API B", line);

        line = br.readLine();
        assertEquals("dummy operation,2.0,12.0", line);

        line = br.readLine();
        assertEquals(null, line);

        br.close();

        testFile.deleteOnExit();
    }
}