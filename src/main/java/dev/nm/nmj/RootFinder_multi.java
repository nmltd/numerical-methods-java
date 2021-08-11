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
package dev.nm.nmj;

import dev.nm.algebra.linear.vector.doubles.Vector;
import dev.nm.algebra.linear.vector.doubles.dense.DenseVector;
import dev.nm.analysis.function.rn2r1.RealScalarFunction;
import dev.nm.analysis.root.multivariate.NewtonSystemRoot;
import dev.nm.analysis.root.univariate.NoRootFoundException;

public class RootFinder_multi {

    public static void multi_equations() {
        RealScalarFunction F = new RealScalarFunction() {
            @Override
            public int dimensionOfDomain() {
                return 4;
            }

            @Override
            public Double evaluate(Vector vector) {
                double x1 = vector.get(1);
                double x2 = vector.get(2);
                double x3 = vector.get(3);
                double x4 = vector.get(4);
                return 2 * x1 * x1 + Math.log(x2) - Math.exp(x3) + Math.sqrt(x4);
            }

            @Override
            public int dimensionOfRange() {
                return 1;
            }
        };

        System.out.printf("The dimemsion of domain of the function is %d%n", F.dimensionOfDomain());
        System.out.printf("The dimension of range of the function is %d%n", F.dimensionOfRange());

    }

    public static void find2roots() {

//        BivariateRealFunction F1 = new AbstractBivariateRealFunction() {
//            @Override
//            public double evaluate(double x, double y) {
//                return 3 * x + y * y - 12;
//            }
//        };
//
//        BivariateRealFunction F2 = new AbstractBivariateRealFunction() {
//            @Override
//            public double evaluate(double x, double y) {
//                return x * x + y - 4;
//            }
//        };
//
//        BivariateRealFunction[] F = new BivariateRealFunction[2];
//        F[0] = F1;
//        F[1] = F2;
        RealScalarFunction F1 = new RealScalarFunction() {

            @Override
            public int dimensionOfDomain() {
                return 2;
            }

            @Override
            public Double evaluate(Vector vector) {
                double x = vector.get(1);
                double y = vector.get(2);
                return 3 * x + y * y - 12;
            }

            @Override
            public int dimensionOfRange() {
                return 1;
            }
        };

        RealScalarFunction F2 = new RealScalarFunction() {
            @Override
            public int dimensionOfDomain() {
                return 2;
            }

            @Override
            public Double evaluate(Vector vector) {
                double x = vector.get(1);
                double y = vector.get(2);
                return x * x + y - 4;
            }

            @Override
            public int dimensionOfRange() {
                return 1;
            }
        };

        RealScalarFunction[] F = new RealScalarFunction[2];
        F[0] = F1;
        F[1] = F2;

        double accuracy = 1E-9;

        NewtonSystemRoot solver = new NewtonSystemRoot(accuracy, 10);
        DenseVector guess = new DenseVector(new double[]{0, 0});
        try {
            double x = solver.solve(F, guess).get(1);
            double y = solver.solve(F, guess).get(2);
            System.out.printf("x = %f, y = %f%n", x, y);
        } catch (NoRootFoundException e) {
            System.out.println("Exception thrown  :" + e);
        }
    }

    public static void find3roots() {
//        TrivariateRealFunction F1 = new AbstractTrivariateRealFunction() {
//            @Override
//            public double evaluate(double x, double y, double z) {
//                return Math.pow(x, 2) + Math.pow(y, 3) - z - 6;
//            }
//        };
//
//        TrivariateRealFunction F2 = new AbstractTrivariateRealFunction() {
//            @Override
//            public double evaluate(double x, double y, double z) {
//                return 2 * x + 9 * y - z - 17;
//            }
//        };
//
//        TrivariateRealFunction F3 = new AbstractTrivariateRealFunction() {
//            @Override
//            public double evaluate(double x, double y, double z) {
//                return Math.pow(x, 4) + 5 * y + 6 * z - 29;
//            }
//        };
//
//        TrivariateRealFunction[] F = new TrivariateRealFunction[3];

        RealScalarFunction F1 = new RealScalarFunction() {
            @Override
            public int dimensionOfDomain() {
                return 3;
            }

            @Override
            public Double evaluate(Vector vector) {
                double x = vector.get(1);
                double y = vector.get(2);
                double z = vector.get(3);
                return Math.pow(x, 2) + Math.pow(y, 3) - z - 6;
            }

            @Override
            public int dimensionOfRange() {
                return 1;
            }
        };

        RealScalarFunction F2 = new RealScalarFunction() {
            @Override
            public int dimensionOfDomain() {
                return 3;
            }

            @Override
            public Double evaluate(Vector vector) {
                double x = vector.get(1);
                double y = vector.get(2);
                double z = vector.get(3);
                return 2 * x + 9 * y - z - 17;
            }

            @Override
            public int dimensionOfRange() {
                return 1;
            }
        };

        RealScalarFunction F3 = new RealScalarFunction() {

            @Override
            public int dimensionOfDomain() {
                return 3;
            }

            @Override
            public Double evaluate(Vector vector) {
                double x = vector.get(1);
                double y = vector.get(2);
                double z = vector.get(3);
                return Math.pow(x, 4) + 5 * y + 6 * z - 29;
            }

            @Override
            public int dimensionOfRange() {
                return 1;
            }
        };

        RealScalarFunction[] F = new RealScalarFunction[3];

        F[0] = F1;
        F[1] = F2;
        F[2] = F3;

        double accuracy = 1E-9;

        NewtonSystemRoot solver = new NewtonSystemRoot(accuracy, 1000);
        DenseVector guess = new DenseVector(new double[]{-1, -2, 2});
        try {
            double x = solver.solve(F, guess).get(1);
            double y = solver.solve(F, guess).get(2);
            double z = solver.solve(F, guess).get(3);
            System.out.printf("x = %f, y = %f, z = %f%n", x, y, z);
        } catch (NoRootFoundException e) {
            System.out.println("Exception thrown  :" + e);
        }
    }

    public static void main(String[] args) {
        multi_equations();
        find2roots();
        find3roots();
    }
}
