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

import com.numericalmethod.benchmark.implementation.ApacheCommonsImplementation;
import com.numericalmethod.benchmark.implementation.ColtImplementation;
import com.numericalmethod.benchmark.implementation.EjmlImplementation;
import com.numericalmethod.benchmark.implementation.Implementation;
import com.numericalmethod.benchmark.implementation.JamaImplementation;
import com.numericalmethod.benchmark.implementation.JblasImplementation;
import com.numericalmethod.benchmark.implementation.JlapackImplementation;
import com.numericalmethod.benchmark.implementation.MtjImplementation;
import com.numericalmethod.benchmark.implementation.OjalgoImplementation;
import com.numericalmethod.benchmark.implementation.ParallelColtImplementation;
import com.numericalmethod.benchmark.implementation.RImplementation;
import com.numericalmethod.benchmark.implementation.SuanShuImplementation;
import com.numericalmethod.benchmark.implementation.UjmpImplementation;
import com.numericalmethod.benchmark.implementation.library.LibraryImplementation;

/**
 *
 * @author Ken Yiu
 */
public enum EvaluatedLinearAlgebraLibrary implements LibraryImplementation {

    SUANSHU(new SuanShuImplementation()),
    //    R(new RImplementation()),
    APACHE_COMMONS(new ApacheCommonsImplementation()),
    COLT(new ColtImplementation()),
    PARALLEL_COLT(new ParallelColtImplementation()),
    EJML(new EjmlImplementation()),
    JAMA(new JamaImplementation()),
    JBLAS(new JblasImplementation()),
    JLAPACK(new JlapackImplementation()),
    //    LA4J(new La4jImplementation()), // too slow
    MTJ(new MtjImplementation()),
    OJALGO(new OjalgoImplementation()),
    UJMP(new UjmpImplementation())
    ;
    private final Implementation implementation;

    private EvaluatedLinearAlgebraLibrary(Implementation implementation) {
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
