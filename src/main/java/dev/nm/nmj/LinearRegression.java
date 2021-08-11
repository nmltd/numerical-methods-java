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

import dev.nm.algebra.linear.matrix.doubles.Matrix;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import dev.nm.algebra.linear.vector.doubles.Vector;
import dev.nm.algebra.linear.vector.doubles.dense.DenseVector;
import dev.nm.stat.regression.linear.LMBeta;
import dev.nm.stat.regression.linear.LMProblem;
import dev.nm.stat.regression.linear.glm.GLMProblem;
import dev.nm.stat.regression.linear.glm.GeneralizedLinearModel;
import dev.nm.stat.regression.linear.glm.distribution.GLMFamily;
import dev.nm.stat.regression.linear.glm.distribution.GLMPoisson;
import dev.nm.stat.regression.linear.glm.quasi.GeneralizedLinearModelQuasiFamily;
import dev.nm.stat.regression.linear.glm.quasi.QuasiGLMProblem;
import dev.nm.stat.regression.linear.glm.quasi.family.QuasiFamily;
import dev.nm.stat.regression.linear.glm.quasi.family.QuasiGamma;
import dev.nm.stat.regression.linear.logistic.LogisticRegression;
import dev.nm.stat.regression.linear.ols.OLSRegression;
import dev.nm.stat.regression.linear.ols.OLSResiduals;
import dev.nm.stat.regression.linear.residualanalysis.LMDiagnostics;
import dev.nm.stat.regression.linear.residualanalysis.LMInformationCriteria;

public class LinearRegression {

    public LinearRegression() {

    }

    public void olsEqual() {
        Matrix x
                = new DenseMatrix(new double[][]{
            {1.28, 2.54, 5.42},
            {5.22, 9.11, 1.10},
            {2.30, 2.25, 12.24},
            {12.18, 8.18, 24.28},
            {8.24, 10.10, 2.28}
        });

        Vector y
                = new DenseVector(new double[]{5.43, 0.258, 3.1416, 14.2857, 23.33});

//		Matrix x = new DenseMatrix(new double[][]{{80, 100, 120, 140, 160, 180, 200, 220, 240, 260}});
//		Vector y =  new DenseVector(new double[]{70, 65, 90, 95, 110, 115, 120, 140, 155, 150});
        boolean intercept = true;

        LMProblem problem = new LMProblem(y, x, intercept);
//		System.out.println(problem);

        OLSRegression ols = new OLSRegression(problem);

        OLSResiduals olsResiduals = ols.residuals();
//
        System.out.printf("beta hat: %s,%nstderr: %s,%nt: %s,%nresiduals: %s%n",
                ols.beta().betaHat(), ols.beta().stderr(), ols.beta().t(),
                olsResiduals.residuals());
        System.out.printf("R2: %f, AR2: %f, standard error: %f, f: %f%n",
                olsResiduals.R2(), olsResiduals.AR2(), olsResiduals.stderr(),
                olsResiduals.Fstat());
        System.out.printf("fitted values: %s%n", olsResiduals.fitted());
        System.out.printf("leverage: %s%n", olsResiduals.leverage());
        System.out.printf("standardized residuals: %s%n", olsResiduals.standardized());
        System.out.printf("studentized residuals: %s%n", olsResiduals.studentized());
        System.out.printf("sum of squared residuals: %s%n", olsResiduals.RSS());
        System.out.printf("total sum of squares: %f%n", olsResiduals.TSS());
        System.out.println();
        LMDiagnostics olsDiagnostics = ols.diagnostics();
        System.out.printf("DFFITS (Welsch and Kuh measure): %s%n", olsDiagnostics.DFFITS());
        System.out.printf("Hadi: %s%n", olsDiagnostics.Hadi());
        System.out.printf("Cook distance: %s%n", olsDiagnostics.cookDistances());
        LMInformationCriteria olsInformationCriteria = ols.informationCriteria();
        System.out.printf("Akaike information criterion: %f%n", olsInformationCriteria.AIC());
        System.out.printf("Bayesian information criterion: %f%n", olsInformationCriteria.BIC());
    }

    public void olsWeighted() {
        Matrix x
                = new DenseMatrix(new double[][]{
            {1.28, 2.54, 5.42},
            {5.22, 9.11, 1.10},
            {2.30, 2.25, 12.24},
            {12.18, 8.18, 24.28},
            {8.24, 10.10, 2.28}
        });

        Vector y
                = new DenseVector(new double[]{5.43, 0.258, 3.1416, 14.2857, 23.33});
        Vector w = new DenseVector(new double[]{0.2, 0.4, 0.1, 0.3, 0.1});
        LMProblem problem = new LMProblem(y, x, true, w);
        OLSRegression ols = new OLSRegression(problem);

        OLSResiduals olsResiduals = ols.residuals();
//
        System.out.printf("beta hat: %s,%nstderr: %s,%nt: %s,%nresiduals: %s%n",
                ols.beta().betaHat(), ols.beta().stderr(), ols.beta().t(),
                olsResiduals.residuals());
        System.out.printf("R2: %f, AR2: %f, standard error: %f, f: %f%n",
                olsResiduals.R2(), olsResiduals.AR2(), olsResiduals.stderr(),
                olsResiduals.Fstat());
        System.out.printf("fitted values: %s%n", olsResiduals.fitted());
        System.out.printf("leverage: %s%n", olsResiduals.leverage());
        System.out.printf("standardized residuals: %s%n", olsResiduals.standardized());
        System.out.printf("studentized residuals: %s%n", olsResiduals.studentized());
        System.out.printf("sum of squared residuals: %s%n", olsResiduals.RSS());
        System.out.printf("total sum of squares: %f%n", olsResiduals.TSS());
        System.out.println();
        LMDiagnostics olsDiagnostics = ols.diagnostics();
        System.out.printf("DFFITS (Welsch and Kuh measure): %s%n", olsDiagnostics.DFFITS());
        System.out.printf("Hadi: %s%n", olsDiagnostics.Hadi());
        System.out.printf("Cook distance: %s%n", olsDiagnostics.cookDistances());
        LMInformationCriteria olsInformationCriteria = ols.informationCriteria();
        System.out.printf("Akaike information criterion: %f%n", olsInformationCriteria.AIC());
        System.out.printf("Bayesian information criterion: %f%n", olsInformationCriteria.BIC());
    }

    public void glm() {
        Matrix x
                = new DenseMatrix(new double[][]{
            {1.52, 2.11},
            {3.22, 4.32},
            {4.32, 1.23},
            {10.1034, 8.43},
            {12.1, 7.31}
        });

        Vector y = new DenseVector(new double[]{2, 1, 4, 5, 7});
        GLMProblem problem = new GLMProblem(y, x, true, new GLMFamily(new GLMPoisson()));
        GeneralizedLinearModel glm = new GeneralizedLinearModel(problem);
        System.out.printf("beta hat: %s,%nstderr: %s,%nt: %s%n",
                glm.beta().betaHat(), glm.beta().stderr(), glm.beta().t());
        System.out.printf("fitted values: %s%n", glm.residuals().fitted());
        System.out.printf("deviance residuals: %s%n", glm.residuals().devianceResiduals());
        System.out.printf("deviance: %f, overdispersion: %f, AIC: %f%n",
                glm.residuals().deviance(), glm.residuals().overdispersion(), glm.AIC());

    }

    public void quasiGLM() {
        Matrix x
                = new DenseMatrix(new double[][]{
            {1.52, 2.11},
            {3.22, 4.32},
            {4.32, 1.23},
            {10.1034, 8.43},
            {12.1, 7.31}
        });

        Vector y = new DenseVector(new double[]{2, 1, 4, 5, 7});
        GeneralizedLinearModelQuasiFamily quasi = new GeneralizedLinearModelQuasiFamily(
                new QuasiGLMProblem(y, x, true, new QuasiFamily(new QuasiGamma())));
        System.out.printf("beta hat: %s,%nstderr: %s,%nt: %s%n",
                quasi.beta().betaHat(), quasi.beta().stderr(), quasi.beta().t());
        System.out.printf("fitted values: %s%n", quasi.residuals().fitted());
        System.out.printf("deviance residuals: %s%n", quasi.residuals().devianceResiduals());
        System.out.printf("deviance: %f, overdispersion: %f%n",
                quasi.residuals().deviance(), quasi.residuals().overdispersion());
    }

    public void logistic() {

        Matrix x
                = new DenseMatrix(new double[][]{
            {1.52},
            {3.22},
            {4.32},
            {10.1034},
            {12.1}
        });

        Vector y = new DenseVector(new double[]{0, 1, 0, 1, 1});

        LMProblem problem = new LMProblem(y, x, true);

        LogisticRegression logistic = new LogisticRegression(problem);
        System.out.printf("beta hat: %s,%nstderr: %s,%nt: %s%n",
                logistic.beta().betaHat(), logistic.beta().stderr(), logistic.beta().t());
        System.out.printf("fitted values: %s%n", logistic.residuals().fitted());
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        LinearRegression lr = new LinearRegression();
        //lr.olsEqual();
        //lr.olsWeighted();
        lr.glm();
        lr.quasiGLM();
        lr.logistic();
    }

}
