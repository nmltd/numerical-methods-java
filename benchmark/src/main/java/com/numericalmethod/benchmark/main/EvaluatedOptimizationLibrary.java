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
package com.numericalmethod.benchmark.main;

import com.numericalmethod.benchmark.implementation.Implementation;
import com.numericalmethod.benchmark.implementation.RImplementation;
import com.numericalmethod.benchmark.implementation.NMDev;
import com.numericalmethod.benchmark.implementation.library.LibraryImplementation;

/**
 *
 * @author Ken Yiu
 */
public enum EvaluatedOptimizationLibrary implements LibraryImplementation {

    SUANSHU(new NMDev()),
//    R(new RImplementation())
    ;
    private final Implementation implementation;

    private EvaluatedOptimizationLibrary(Implementation implementation) {
        this.implementation = implementation;
    }

    @Override
    public Implementation getImplementation() {
        return implementation;
    }

    @Override
    public String toString() {
        return implementation.getLibraryInfo().toString();
    }
}
