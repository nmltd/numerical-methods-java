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
public class DenseMatrixArgument implements Argument {

    private final double[][] data2d;
    /*
     * Note: Some APIs prefers 1D array at matrix construction. This duplicated data in 1D
     * representation is just to provide an option for those APIs to choose a better way to
     * construct matrices. Otherwise, the benchmarking (which also counts the matrix construction
     * time) would favor the APIs that use 2D arrays (which is unfair).
     */
    private final double[] data1d; // 1D representation of the data
    private final int nRows;
    private final int nCols;

    public DenseMatrixArgument(double[][] data) {
        this.data2d = data;
        this.nRows = data.length;
        this.nCols = data[0].length;

        this.data1d = new double[nRows * nCols];
        for (int i = 0, p = 0; i < nRows; ++i, p += nCols) {
            System.arraycopy(data[i], 0, data1d, p, nCols);
        }
    }

    public double[][] get2dArray() {
        return data2d;
    }

    public double[] get1dArray() {
        return data1d;
    }

    /**
     * @return the nRows
     */
    public int nRows() {
        return nRows;
    }

    /**
     * @return the nCols
     */
    public int nCols() {
        return nCols;
    }

    public DenseMatrixArgument deepCopy() {
        double[][] copy = new double[data2d.length][];

        for (int i = 0; i < data2d.length; ++i) {
            copy[i] = data2d[i].clone();
        }

        return new DenseMatrixArgument(copy);
    }
}
