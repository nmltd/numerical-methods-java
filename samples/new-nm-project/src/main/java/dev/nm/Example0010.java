/*
 * Copyright (c) NM LTD.
 * https://www.nm.dev/
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
package dev.nm;

import dev.nm.algebra.linear.matrix.doubles.Matrix;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;

/**
 *
 * @author haksunli
 */
public class Example0010 {

    public static void main(String[] args) {
        Example0010 example0010 = new Example0010();
        example0010.function1();
    }

    public void function1() {
        System.out.println("Hello World!");
    }

    public int function2() {
        Matrix A = new DenseMatrix(new double[][]{{1, 2}, {3, 4}});
        return A.nCols();
    }

}
