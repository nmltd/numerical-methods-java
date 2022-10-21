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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author Ken Yiu
 */
public class BenchmarkResults {

    private static final String OPERATION_COLUMN_LABEL = "operation";
    private final List<OperationRecord> records = new ArrayList<OperationRecord>();
    private final List<String> libraryNames;

    public BenchmarkResults(List<String> libraryNames) {
        this.libraryNames = new ArrayList<String>(libraryNames);
    }

    public void addOperationBenchmarkResult(String operationTitle, Map<String, Double> values) {
        records.add(new OperationRecord(operationTitle, values));
    }

    private static class OperationRecord {

        private final String operationTitle;
        private final Map<String, Double> meanValues;

        OperationRecord(String operationTitle, Map<String, Double> meanValues) {
            this.operationTitle = operationTitle;
            this.meanValues = meanValues;
        }
    }

    public void saveAsCsv(String filePath) throws IOException {
        System.out.printf("saving results to %s...%n", filePath);
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // make sure the folders exist
        FileWriter writer = new FileWriter(file);
        CsvMapWriter mapWriter = new CsvMapWriter(writer, CsvPreference.STANDARD_PREFERENCE);

        String[] headers = makeHeaders();
        mapWriter.writeHeader(headers);

        for (OperationRecord record : records) {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put(OPERATION_COLUMN_LABEL, record.operationTitle);
            row.putAll(record.meanValues);

            mapWriter.write(row, headers);
        }

        mapWriter.close();
    }

    private String[] makeHeaders() {
        List<String> headers = new ArrayList<String>();
        headers.add(OPERATION_COLUMN_LABEL);
        headers.addAll(libraryNames);

        return headers.toArray(new String[0]);
    }
}
