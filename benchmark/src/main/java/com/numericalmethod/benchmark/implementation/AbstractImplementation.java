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
package com.numericalmethod.benchmark.implementation;

import com.numericalmethod.benchmark.operation.BenchmarkOperation;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author Ken Yiu
 */
public abstract class AbstractImplementation implements Implementation {

    private static final Logger LOGGER =
        Logger.getLogger(AbstractImplementation.class.getSimpleName());
    private final Map<Class<? extends BenchmarkOperation>, Executable> operationExecutable =
        new HashMap<Class<? extends BenchmarkOperation>, Executable>();

    /**
     * This method is for implementation class to register implementation for a given operation in
     * its constructor.
     *
     * @param operationClass the class of the operation
     * @param executable     the implementation of the operation
     */
    protected void addExecutable(
        Class<? extends BenchmarkOperation> operationClass,
        Executable executable) {
        Executable original = operationExecutable.put(operationClass, executable);
        if (original != null) {
            LOGGER.warning(
                String.format("overriding the original operation '%s' of implementation '%s'",
                              operationClass.getSimpleName(),
                              this.getClass().getSimpleName()));
        }
    }

    @Override
    public Executable getExecutable(Class<? extends BenchmarkOperation> operationClass) {
        Executable executable = operationExecutable.get(operationClass);
        if (executable == null) {
            throw new RuntimeException(
                String.format("no implementation for operation '%s' in '%s'",
                              operationClass.getSimpleName(),
                              this.getClass().getSimpleName()));
        }

        return executable;
    }
}
