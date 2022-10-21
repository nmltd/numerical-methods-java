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
package com.numericalmethod.benchmark.implementation.datatype;

/**
 *
 * @author Ken Yiu
 */
public class SparseMatrixArgument implements Argument {

    private final int nRows;
    private final int nCols;
    private final int[] rowIndices;
    private final int[] columnIndices;
    private final double[] values;

    public SparseMatrixArgument(
        int nRows, int nCols, int[] rowIndices, int[] columnIndices, double[] values) {
        this.nRows = nRows;
        this.nCols = nCols;
        this.rowIndices = rowIndices;
        this.columnIndices = columnIndices;
        this.values = values;
    }

    public int nRows() {
        return nRows;
    }

    public int nCols() {
        return nCols;
    }

    public int[] getRowIndices() {
        return rowIndices;
    }

    public int[] getColumnIndices() {
        return columnIndices;
    }

    public double[] getValues() {
        return values;
    }

    public SparseMatrixArgument deepCopy() {
        return new SparseMatrixArgument(
            nRows, nCols, rowIndices.clone(), columnIndices.clone(), values.clone());
    }
}
