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
package dev.nm.nmj;

//import com.csvreader.CsvReader;
import dev.nm.algebra.linear.matrix.doubles.Matrix;
import dev.nm.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import dev.nm.algebra.linear.vector.doubles.Vector;
import dev.nm.algebra.linear.vector.doubles.dense.DenseVector;
import dev.nm.stat.random.rng.RNGUtils;
import dev.nm.stat.test.timeseries.adf.AugmentedDickeyFuller;
import dev.nm.stat.test.timeseries.portmanteau.LjungBox;
import dev.nm.stat.timeseries.datastructure.multivariate.MultivariateGenericTimeTimeSeries;
import dev.nm.stat.timeseries.datastructure.multivariate.realtime.inttime.MultivariateIntTimeTimeSeries;
import dev.nm.stat.timeseries.datastructure.multivariate.realtime.inttime.MultivariateSimpleTimeSeries;
import dev.nm.stat.timeseries.datastructure.univariate.GenericTimeTimeSeries;
import dev.nm.stat.timeseries.datastructure.univariate.realtime.inttime.IntTimeTimeSeries;
import dev.nm.stat.timeseries.datastructure.univariate.realtime.inttime.SimpleTimeSeries;
import dev.nm.stat.timeseries.linear.multivariate.arima.VARIMAModel;
import dev.nm.stat.timeseries.linear.multivariate.arima.VARIMASim;
import dev.nm.stat.timeseries.linear.multivariate.stationaryprocess.arma.VARFit;
import dev.nm.stat.timeseries.linear.multivariate.stationaryprocess.arma.VARLinearRepresentation;
import dev.nm.stat.timeseries.linear.multivariate.stationaryprocess.arma.VARMAAutoCorrelation;
import dev.nm.stat.timeseries.linear.multivariate.stationaryprocess.arma.VARMAAutoCovariance;
import dev.nm.stat.timeseries.linear.multivariate.stationaryprocess.arma.VARMAForecastOneStep;
import dev.nm.stat.timeseries.linear.multivariate.stationaryprocess.arma.VARMAModel;
import dev.nm.stat.timeseries.linear.multivariate.stationaryprocess.arma.VARModel;
import dev.nm.stat.timeseries.linear.multivariate.stationaryprocess.arma.VARXModel;
import dev.nm.stat.timeseries.linear.multivariate.stationaryprocess.arma.VECMLongrun;
import dev.nm.stat.timeseries.linear.multivariate.stationaryprocess.arma.VMAModel;
import dev.nm.stat.timeseries.linear.univariate.arima.ARIMAForecast;
import dev.nm.stat.timeseries.linear.univariate.arima.ARIMAForecastMultiStep;
import dev.nm.stat.timeseries.linear.univariate.arima.ARIMAModel;
import dev.nm.stat.timeseries.linear.univariate.arima.ARIMASim;
import dev.nm.stat.timeseries.linear.univariate.arima.AutoARIMAFit;
import dev.nm.stat.timeseries.linear.univariate.sample.SampleAutoCorrelation;
import dev.nm.stat.timeseries.linear.univariate.sample.SampleAutoCovariance;
import dev.nm.stat.timeseries.linear.univariate.sample.SamplePartialAutoCorrelation;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.AdditiveModel;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.MADecomposition;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.MultiplicativeModel;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.arma.ARMAForecast;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.arma.ARMAForecastOneStep;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.arma.ARMAModel;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.arma.ARModel;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.arma.AutoCorrelation;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.arma.AutoCovariance;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.arma.ConditionalSumOfSquares;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.arma.LinearRepresentation;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.arma.MAModel;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.armagarch.ARMAGARCHFit;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.armagarch.ARMAGARCHModel;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.garch.GARCHFit;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.garch.GARCHModel;
import dev.nm.stat.timeseries.linear.univariate.stationaryprocess.garch.GARCHSim;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TimeSeries {

    public static ArrayList<String[]> readcsv(String path) {
        ArrayList<String[]> csvFileList = new ArrayList<String[]>();
//        try {
//            String csvFilePath = path;
//            CsvReader reader = new CsvReader(csvFilePath, ',', Charset.forName("UTF-8"));
//            reader.readHeaders();
//
//            while (reader.readRecord()) {
////                System.out.println(reader.getRawRecord());
//                csvFileList.add(reader.getValues());
//            }
//            reader.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return csvFileList;
    }

    public static String[] getdata(ArrayList<String[]> csv, int column) {
        String[] result = new String[csv.size()];
        for (int row = 0; row < csv.size(); row++) {
            result[row] = csv.get(row)[column];
        }
        return result;
    }

    public static double[] strtodouble(String[] raw) {
        double[] result = new double[raw.length];
        for (int i = 0; i < raw.length; i++) {
            result[i] = Double.parseDouble(raw[i]);
        }
        return result;
    }

    public static Date[] strtodate(String[] raw) {
        Date[] result = new Date[raw.length];
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < raw.length; i++) {
                result[i] = format.parse(raw[i]);
            }
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void sp_daily() {

        try {
            // read the s&p500 daily data
            ArrayList<String[]> csvFileList = new ArrayList<String[]>();
            String csvFilePath = "TimeSeriesData/sp_daily.csv";
//            CsvReader reader = new CsvReader(csvFilePath, ',', Charset.forName("UTF-8"));
//            reader.readHeaders();
//
//            while (reader.readRecord()) {
////                System.out.println(reader.getRawRecord());
//                csvFileList.add(reader.getValues());
//            }
//            reader.close();

            String[] date0 = new String[csvFileList.size()];
            String[] price0 = new String[csvFileList.size()];
            String[] volume0 = new String[csvFileList.size()];

            for (int row = 0; row < csvFileList.size(); row++) {
                date0[row] = csvFileList.get(row)[0];
                price0[row] = csvFileList.get(row)[4];
                volume0[row] = csvFileList.get(row)[6];
            }

            // change the date from string to timestamp, and price and volume to double
            Date[] date = new Date[date0.length];
            double[] price = new double[price0.length];
            double[] volume = new double[volume0.length];

            DateFormat strtodate = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < date.length; i++) {
                date[i] = strtodate.parse(date0[i]);
                price[i] = Double.parseDouble(price0[i]);
                volume[i] = Double.parseDouble(volume0[i]);
            }

            double[][] price_volume = new double[price.length][2];
            for (int j = 0; j < price.length; j++) {
                price_volume[j][0] = price[j];
                price_volume[j][1] = volume[j];
            }

            // generate the univariate time series and multivariate time series
            GenericTimeTimeSeries ts = new GenericTimeTimeSeries(date, price);
            MultivariateGenericTimeTimeSeries ts_multi = new MultivariateGenericTimeTimeSeries(date, price_volume);

            Date time = (Date) ts.time(1);
            System.out.println(time);
            System.out.println(ts.get(time));
            System.out.println(ts_multi.dimension());

            // get the daily simple return
            double[] price_0 = ts.toArray();
            double[] diff = ts.diff(1).toArray();

            double[] simple_return = new double[diff.length];
            Date[] ret_time = new Date[diff.length];

            for (int i = 0; i < diff.length; i++) {
                ret_time[i] = date[i + 1];
                simple_return[i] = diff[i] / price_0[i];
            }

            // get the daily log return
            double[] log_price = new double[price.length];
            for (int i = 0; i < price.length; i++) {
                log_price[i] = Math.log(price[i]);
            }

            double[] log_return = new double[price.length - 1];
            for (int i = 1; i < price.length; i++) {
                log_return[i - 1] = log_price[i] - log_price[i - 1];
            }

            GenericTimeTimeSeries return_ts = new GenericTimeTimeSeries(ret_time, log_return);

            System.out.println(return_ts.get(1));
            System.out.println(return_ts.time(1));

            // test whether the simple daily return is white noise
            LjungBox lb_test = new LjungBox(log_return, 14, 0);

            System.out.printf("The null hypothesis is %s%n", lb_test.getNullHypothesis());
            System.out.printf("The alternative hypothesis is %s%n", lb_test.getAlternativeHypothesis());
            System.out.printf("The test statistic is %f%n", lb_test.statistics());
            System.out.printf("The p-value is %f%n", lb_test.pValue());

            SimpleTimeSeries xt_return = new SimpleTimeSeries(log_return);
            // construct a ar(2) model for the simple return
            ConditionalSumOfSquares css_ar2 = new ConditionalSumOfSquares(log_return, 2, 0, 0);
            ARMAModel m_ar2 = css_ar2.getARMAModel();
            System.out.printf("the AR(2) model is %s%n", m_ar2);

            ARMAForecast forecast_ar2 = new ARMAForecast(xt_return, m_ar2);
            System.out.println("The forecasat for the AR(2) model is");
            for (int j = 0; j < 10; ++j) {
                System.out.println(forecast_ar2.next());
            }

            // construct a ma(2) model for the simple return
            ConditionalSumOfSquares css_ma2 = new ConditionalSumOfSquares(log_return, 0, 0, 2);
            ARMAModel m_ma2 = css_ma2.getARMAModel();
            System.out.printf("the MA(2) model is %s%n", m_ma2);

            ARMAForecast forecast_ma2 = new ARMAForecast(xt_return, m_ma2);
            System.out.println("The forecasat for the MA(2) model is");
            for (int j = 0; j < 10; ++j) {
                System.out.println(forecast_ma2.next());
            }

            // construct an arima model for the simple return using auto fit
            AutoARIMAFit fit_arima = new AutoARIMAFit(log_return);
            System.out.printf("the optimal ARIMA model by AIC: %s%n", fit_arima.optimalModelByAIC());
            System.out.printf("the optimal ARIMA model by AICc: %s%n", fit_arima.optimalModelByAICC());
            ARIMAModel m1 = fit_arima.optimalModelByAIC();
            ARIMAModel m2 = fit_arima.optimalModelByAICC();
            System.out.printf("the optimal ARIMA model by AIC: %s%n", fit_arima.optimalModelByAIC());
            System.out.printf("the optimal ARIMA model by AICc: %s%n", fit_arima.optimalModelByAICC());
            ARMAModel m3 = m1.getARMA();

            // test for model adequacy
            ConditionalSumOfSquares css_arma = new ConditionalSumOfSquares(log_return, 1, 0, 1);
            ARMAModel m_arma = css_arma.getARMAModel();
            System.out.printf("The ARMA(1,1) model is %s%n", m_arma);

            ARMAForecast forecast_arma = new ARMAForecast(xt_return, m_arma);
            System.out.println("The forecast for the ARMA(1,1) model is");
            for (int j = 0; j < 10; ++j) {
                System.out.println(forecast_arma.next());
            }

            ARMAForecastOneStep xt_hat = new ARMAForecastOneStep(log_return, m_arma);
            double[] residuals = new double[log_return.length];
            for (int t = 0; t < log_return.length; ++t) {
                residuals[t] = log_return[t] - xt_hat.xHat(t);
            }

            // Fit the GARCH model
            GARCHFit garch_fit = new GARCHFit(residuals, 1, 1);
            GARCHModel garch = garch_fit.getModel();
            System.out.printf("the GARCH(1,1) model is: %s%n", garch);

            // fit the garch model using ARMAGARCHFit
            ARMAGARCHFit arma_garch_fit = new ARMAGARCHFit(log_return, 1, 1, 1, 1);
            ARMAGARCHModel arma_garch = arma_garch_fit.getARMAGARCHModel();
            ARMAModel arma11 = arma_garch.getARMAModel();
            GARCHModel garch11 = arma_garch.getGARCHModel();
            System.out.printf("the ARMA(1,1) model is: %s%n", arma11);
            System.out.printf("the GARCH(1,1) model is: %s%n", garch11);

            // calculate the acf for the residuals
            SimpleTimeSeries xt = new SimpleTimeSeries(residuals);
            SampleAutoCorrelation acf_residuals = new SampleAutoCorrelation(xt);
            for (int i = 1; i < 20; i++) {
                System.out.printf("The acf of the residuals at lag %d is: %f%n", i, acf_residuals.evaluate(i));
            }

            // test for model adequacy
            LjungBox adequacy_test = new LjungBox(residuals, 3, 2);
            System.out.printf("The test statistic is %f%n", adequacy_test.statistics());
            System.out.printf("The p-value is %f%n", adequacy_test.pValue());

            System.out.println("test the armaforecastonestep");

            for (int t = 0; t < 10; ++t) {
                System.out.println(xt_hat.xHat(t));
            }

            System.out.println("test the ARMAForecast");
            ARIMAForecast arima_fore = new ARIMAForecast(xt, m_arma);
//            System.out.println(arima_fore.next());
//            System.out.println(arima_fore.next());
            List<ARIMAForecast.Forecast> list_fore = arima_fore.next(10);
            for (int j = 0; j < 10; ++j) {
                System.out.println(list_fore.get(j));
            }

            // ADF test for Sp500 prices
            System.out.printf("test for the unit root");
            AugmentedDickeyFuller test = new AugmentedDickeyFuller(price);

            System.out.println(test.getAlternativeHypothesis());
            System.out.println(test.getNullHypothesis());
            System.out.printf("the p-value for the test is: %f%n", test.pValue());
            System.out.printf("the statistics for the test is: %f%n", test.statistics());

            // ARIMA model for sp500
            AutoARIMAFit fit_price1 = new AutoARIMAFit(price);

            ARIMAModel arima1 = fit_price1.optimalModelByAIC();
            ARIMAModel arima2 = fit_price1.optimalModelByAICC();

//            System.out.println(arima1.d());
            System.out.println("The optimal model for S&P500 based on AIC is");
            System.out.println(arima1);
            System.out.println("The optimal model for S&P500 based on AICc is");
            System.out.println(arima2);

            // Forecasting
            IntTimeTimeSeries xt_price = new SimpleTimeSeries(price);
            ARIMAForecast forecast_sp500 = new ARIMAForecast(xt_price, arima1);
            System.out.println("The first 10 forecasts for sp500 using ARIMA(2,1,1) model is");
            for (int i = 0; i < 10; i++) {
                System.out.println(forecast_sp500.next());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public static void ap_daily() {
//        String ap = "TimeSeriesData/AAPL_daily.csv";
//        ArrayList<String[]> csv_apm = readcsv(ap);
//        double[] ap_price = strtodouble(getdata(csv_apm, 4));
//
//        double[] log_price = new double[ap_price.length];
//        for (int i = 0; i < ap_price.length; i++) {
//            log_price[i] = Math.log(ap_price[i]);
//        }
//
//        double[] log_returns = new double[ap_price.length - 1];
//        for (int i = 1; i < ap_price.length; i++) {
//            log_returns[i - 1] = log_price[i] - log_price[i - 1];
//        }
//
//        System.out.println(log_returns[1250]);
//
//    }
    public static void sp_ap_monthly() {
        // get monthly sp and apple price
        String sp_0 = "TimeSeriesData/sp_monthly.csv";
        String ap_0 = "TimeSeriesData/AAPL_monthly.csv";
        ArrayList<String[]> csv_spm = readcsv(sp_0);
        ArrayList<String[]> csv_apm = readcsv(ap_0);
//        Date[] date = strtodate(getdata(csv_spm, 0));
        double[] sp_price = strtodouble(getdata(csv_spm, 4));
        double[] ap_price = strtodouble(getdata(csv_apm, 4));

        IntTimeTimeSeries sp_ts = new SimpleTimeSeries(sp_price);
        IntTimeTimeSeries ap_ts = new SimpleTimeSeries(ap_price);

        // calculate the acvf and the acf and pacf for monthly price
        SampleAutoCovariance sp_acvf = new SampleAutoCovariance(sp_ts);
        SampleAutoCorrelation sp_acf = new SampleAutoCorrelation(sp_ts);
        SamplePartialAutoCorrelation sp_pacf = new SamplePartialAutoCorrelation(sp_ts);
        for (int i = 1; i < 25; i++) {
//            System.out.printf("The acvf at lag %d is: %f%n", i, sp_acvf.evaluate(i));
//            System.out.printf("The acf at lag %d is: %f%n", i, sp_acf.evaluate(i));
            System.out.printf("The pacf at lag %d is: %f%n", i, sp_pacf.evaluate(i));
        }

        // calculate the log return of sp500
        double[] log_price_sp = new double[sp_price.length];
        for (int i = 0; i < sp_price.length; i++) {
            log_price_sp[i] = Math.log(sp_price[i]);
        }

        double[] sp_return = new double[sp_price.length - 1];
        for (int i = 1; i < sp_price.length; i++) {
            sp_return[i - 1] = log_price_sp[i] - log_price_sp[i - 1];
        }

        // calculate the log return of apple
        double[] log_price_ap = new double[ap_price.length];
        for (int i = 0; i < ap_price.length; i++) {
            log_price_ap[i] = Math.log(ap_price[i]);
        }

        double[] ap_return = new double[ap_price.length - 1];
        for (int i = 1; i < ap_price.length; i++) {
            ap_return[i - 1] = log_price_ap[i] - log_price_ap[i - 1];
        }

        // construct the matrix for multivariate time series
        double[][] log_return = new double[ap_return.length][2];
        for (int i = 0; i < ap_return.length; i++) {
            log_return[i] = new double[]{sp_return[i], ap_return[i]};
        }

        // generate time series
        MultivariateIntTimeTimeSeries xt = new MultivariateSimpleTimeSeries(log_return);

        // fit var(1)
        VARFit instance = new VARFit(xt, 1);
        System.out.println("the estimated phi for var(1) is");
        System.out.println(instance.AR(1));

//        VECM vecm9 = new VECM(mu,
//                pi, gamma,
//                psi, sigma
//        );
    }

    public static void decompose_sp_monthly() {
        // read the monthly sp500 data
        String sp_0 = "TimeSeriesData/sp_monthly.csv";
        ArrayList<String[]> csv_spm = readcsv(sp_0);
        double[] sp_price = strtodouble(getdata(csv_spm, 4));

        // decompose the sp500
        MADecomposition ma_decom_sp = new MADecomposition(sp_price, 12);
        System.out.printf("the random component in the timeseries is: %s%n", Arrays.toString(ma_decom_sp.getRandom()));
        System.out.printf("the seasonal component in the timeseries is: %s%n", Arrays.toString(ma_decom_sp.getSeasonal()));
        System.out.printf("the trend component in the timeseries is: %s%n", Arrays.toString(ma_decom_sp.getTrend()));
    }

    public static void AR() {

        // define an ar(1) model
        ARModel ar1 = new ARModel(
                new double[]{0.6}
        );

        AutoCovariance acvf1 = new AutoCovariance(ar1);
        for (int i = 1; i < 10; i++) {
            System.out.printf("the acvf of the AR(1) model at lag%d: %f%n", i, acvf1.evaluate(i));
        }

        AutoCorrelation acf1 = new AutoCorrelation(ar1, 10);
        for (int i = 0; i < 10; i++) {
            System.out.printf("the acf of the AR(1) model at lag%d: %f%n", i, acf1.evaluate(i));
        }

        // define an ar(2) model
        ARModel ar2 = new ARModel(
                new double[]{1.2, -0.35}
        );

        AutoCovariance acvf2 = new AutoCovariance(ar2);
        for (int i = 1; i < 10; i++) {
            System.out.printf("the acvf of the AR(2) model at lag%d: %f%n", i, acvf2.evaluate(i));
        }
        AutoCorrelation acf2 = new AutoCorrelation(ar2, 10);
        for (int i = 0; i < 10; i++) {
            System.out.printf("the acf of the AR(2) model at lag%d: %f%n", i, acf2.evaluate(i));
        }

    }

    public static void MA() {
        // define an ma(1) model
        MAModel ma1 = new MAModel(
                new double[]{0.8}
        );

        AutoCovariance acvf1 = new AutoCovariance(ma1);
        for (int i = 1; i < 10; i++) {
            System.out.printf("the acvf of the MA(1) model at lag%d: %f%n", i, acvf1.evaluate(i));
        }

        AutoCorrelation acf1 = new AutoCorrelation(ma1, 10);
        for (int i = 0; i < 10; i++) {
            System.out.printf("the acf of the MA(1) model at lag%d: %f%n", i, acf1.evaluate(i));
        }

        // define an ma(2) model
        MAModel ma2 = new MAModel(
                new double[]{-0.2, 0.01}
        );

        AutoCovariance acvf2 = new AutoCovariance(ma2);
        for (int i = 1; i < 10; i++) {
            System.out.printf("the acvf of the MA(2) model at lag%d: %f%n", i, acvf2.evaluate(i));
        }

        AutoCorrelation acf2 = new AutoCorrelation(ma2, 10);
        for (int i = 0; i < 10; i++) {
            System.out.printf("the acf of the MA(2) model at lag%d: %f%n", i, acf2.evaluate(i));
        }
    }

    public static void ARMA() {

        // define an ARMA(1,1) model
        ARMAModel arma11 = new ARMAModel(
                new double[]{0.2},
                new double[]{1.1});

        AutoCovariance acvf1 = new AutoCovariance(arma11);
        for (int i = 1; i < 10; i++) {
            System.out.printf("the acvf of the ARMA(1,1) model at lag%d: %f%n", i, acvf1.evaluate(i));
        }

        AutoCorrelation acf1 = new AutoCorrelation(arma11, 10);
        for (int i = 0; i < 10; i++) {
            System.out.printf("the acf of the ARMA(1,1) model at lag%d: %f%n", i, acf1.evaluate(i));
        }

        // get the linear representation for model 1
        LinearRepresentation rep = new LinearRepresentation(arma11);
        for (int i = 1; i < 10; i++) {
            System.out.printf("The coefficient of the linear representation at lag %d is %f%n", i, rep.AR(i));
        }

        // define an ARMA(2,3) model
        ARMAModel arma23 = new ARMAModel(
                new double[]{0.6, -0.23},
                new double[]{0.1, 0.2, 0.4});

        AutoCovariance acvf2 = new AutoCovariance(arma23);
        for (int i = 1; i < 10; i++) {
            System.out.printf("the acvf of the ARMA(2,3) model at lag%d: %f%n", i, acvf2.evaluate(i));
        }

        AutoCorrelation acf2 = new AutoCorrelation(arma11, 10);
        for (int i = 0; i < 10; i++) {
            System.out.printf("the acf of the ARMA(2,3) model at lag%d: %f%n", i, acf2.evaluate(i));
        }

//        IntTimeTimeSeries xt = new SimpleTimeSeries(new double[]{5.704, 0.727, 1.041, 0.942, 0.555, -1.102, -0.585, 0.090, -0.648, 0.325});
//        ARMAForecastOneStep instance = new ARMAForecastOneStep(xt, arma);
//        for (int i = 0; i <= 10; ++i) {
//            System.out.printf("x^: %f (%f)%n", instance.xHat(i), instance.var(i));
//        }
//        ARMAForecastMultiStep instance_multi = new ARMAForecastMultiStep(xt, arma, 3);
//        Vector xHat = instance_multi.allForecasts();
//        Vector var = instance_multi.allMSEs();
//        System.out.printf("x^= %f (%f)%n", xHat.get(1), var.get(1));
//        System.out.printf("x^= %f (%f)%n", xHat.get(2), var.get(2));
//        System.out.printf("x^= %f (%f)%n", xHat.get(3), var.get(3));
    }

    public static void acf_pacf() {

        IntTimeTimeSeries xt = new SimpleTimeSeries(new double[]{4.704, 0.727, 1.041, 1.942, 0.555, -1.102, -0.585, 0.090, -0.648, 0.325});
        SampleAutoCovariance acvf = new SampleAutoCovariance(xt);
        SampleAutoCorrelation acf = new SampleAutoCorrelation(xt);
        SamplePartialAutoCorrelation pacf = new SamplePartialAutoCorrelation(xt);

        for (int i = 1; i < 5; i++) {
            System.out.printf("The acvf at lag %d is: %f%n", i, acvf.evaluate(i));
            System.out.printf("The acf at lag %d is: %f%n", i, acf.evaluate(i));
            System.out.printf("The pacf at lag %d is: %f%n", i, pacf.evaluate(i));
        }
    }

    public static void decompose() {
        double[] xt = new double[200];
        ARIMASim arima_sim = new ARIMASim(new ARIMAModel(new double[]{}, 0, new double[]{0.6}));
        for (int i = 0; i < 200; i++) {
            xt[i] = arima_sim.nextDouble();
        }

        AdditiveModel additive_model = new AdditiveModel(
                new double[]{
                    1, 3, 5, 7, 9, 11
                }, new double[]{
                    0, 1, 0, 1, 0, 1
                }, xt
        );
        System.out.println(additive_model.toString());

        MultiplicativeModel multiplicative_model = new MultiplicativeModel(
                new double[]{
                    1, 3, 5, 7, 9, 11
                }, new double[]{
                    0, 1, 0, 1, 0, 1
                }, xt
        );
        System.out.println(multiplicative_model.toString());

        MADecomposition ma_decom = new MADecomposition(xt, 1, 1);
        System.out.printf("the random component in the timeseries is: %s%n", Arrays.toString(ma_decom.getRandom()));
        System.out.printf("the seasonal component in the timeseries is: %s%n", Arrays.toString(ma_decom.getSeasonal()));
        System.out.printf("the trend component in the timeseries is: %s%n", Arrays.toString(ma_decom.getTrend()));
    }

    public static void GARCH() {
        GARCHModel arch = new GARCHModel(0.2, new double[]{0.212}, new double[]{});
        System.out.println(arch);

        GARCHModel garch = new GARCHModel(0.2, new double[]{0.212}, new double[]{0.106});
        GARCHSim sim = new GARCHSim(garch);
        double[] GARCHTimeSeries = RNGUtils.nextN(sim, 1000);
        GARCHFit fit = new GARCHFit(GARCHTimeSeries, 1, 1);
        GARCHModel garch_fit = fit.getModel();
        System.out.printf("the GARCH(1,1) model: %s%n", garch_fit);
    }

    public static void fit_uniARMA() {
        IntTimeTimeSeries x = new SimpleTimeSeries(new double[]{-1.1, 0.514, 0.116, -0.845, 0.872, -0.467, -0.977, -1.699, -1.228, -1.093});
        double[] xt = x.toArray();
        ConditionalSumOfSquares fit1 = new ConditionalSumOfSquares(
                xt,
                1, 0, 1);
        ARMAModel arma = fit1.getARMAModel();
        System.out.printf("the ARMA(1,1) model: %s%n", arma);

    }

    public static void sim_uniARIMA() {
        ARIMASim arima_sim = new ARIMASim(new ARIMAModel(new double[]{0.3, -0.02}, 1, new double[]{0.6}));
        RNGUtils.nextN(arima_sim, 10);
        System.out.printf("Example ARIMA time series: %s%n",
                Arrays.toString(RNGUtils.nextN(arima_sim, 10)));
    }

    public static void uniARIMA() {
        ARIMAModel arima = new ARIMAModel(new double[]{0.3, -0.02}, 1, new double[]{0.6});
        IntTimeTimeSeries xt = new SimpleTimeSeries(new double[]{5.704, 0.727, 1.041, 0.942, 0.555, -1.102, -0.585, 0.090, -0.648, 0.325});
        ARIMAForecastMultiStep instance_multi = new ARIMAForecastMultiStep(xt, arima, 4);
        Vector xHat = instance_multi.allForecasts();
        Vector var = instance_multi.allMSEs();
        System.out.printf("x^= %f (%f)%n", xHat.get(1), var.get(1));
        System.out.printf("x^= %f (%f)%n", xHat.get(2), var.get(2));
        System.out.printf("x^= %f (%f)%n", xHat.get(3), var.get(3));
    }

    public static void fit_uniARIMA() {
        double[] xt = new double[]{5.704, 0.727, 1.041, 0.942, 0.555, -1.102, -0.585, 0.090, -0.648, 0.325};
        AutoARIMAFit fit_arima = new AutoARIMAFit(xt);
        System.out.printf("the optimal ARIMA model by AIC: %s%n", fit_arima.optimalModelByAIC());
        System.out.printf("the optimal ARIMA model by AICc: %s%n", fit_arima.optimalModelByAICC());
    }

    public static void fit_arima_residual_garch() {
        IntTimeTimeSeries x = new SimpleTimeSeries(new double[]{5.704, 0.727, 1.041, 0.942, 0.555, -1.102, -0.585, 0.090, -0.648, 0.325});
        double[] xt = x.toArray();
        ConditionalSumOfSquares fit1 = new ConditionalSumOfSquares(
                xt,
                1, 0, 1);
        ARMAModel arma = fit1.getARMAModel();
        System.out.printf("the ARMA(1,1) model: %s%n", arma);
        ARMAForecastOneStep xt_hat = new ARMAForecastOneStep(x, arma);
        double[] residuals = new double[xt.length];
        for (int t = 0; t < xt.length; ++t) {
            residuals[t] = xt[t] - xt_hat.xHat(t);
        }
        GARCHFit fit2 = new GARCHFit(residuals, 1, 1);
        GARCHModel garch = fit2.getModel();
        System.out.printf("the GARCH(1,1) model: %s%n", garch);

        ARMAGARCHFit armagarch_fit = new ARMAGARCHFit(new double[]{5.704, 0.727, 1.041, 0.942, 0.555, -1.102, -0.585, 0.090, -0.648, 0.325}, 1, 1, 1, 1);
        System.out.printf("The one step ahead forecast of X in ARMA model is %s%n", Arrays.toString(armagarch_fit.getMeanForecast(1)));
        System.out.printf("The one step ahead forecast of conditional variance h in GARCH model is %s%n", Arrays.toString(armagarch_fit.getVarForecast(1)));
    }

    public static void multi_VAR() {
        MultivariateIntTimeTimeSeries X_T = new MultivariateSimpleTimeSeries(
                new double[][]{
                    {-1.875, 1.693},
                    {-2.518, -0.03},
                    {-3.002, -1.057},
                    {-2.454, -1.038},
                    {-1.119, -1.086},
                    {-0.72, -0.455},
                    {-2.738, 0.962},
                    {-2.565, 1.992},
                    {-4.603, 2.434},
                    {-2.689, 2.118}
                });

        VARFit instance = new VARFit(X_T, 1);
        System.out.println("the estimated phi for var(1) is");
        System.out.println(instance.AR(1));

        Matrix[] PHI = new Matrix[1];
        PHI[0] = new DenseMatrix(
                new double[][]{
                    {0.7, 0.12},
                    {0.31, 0.6}
                });

        VARModel var = new VARModel(PHI);
        VARMAAutoCovariance acvf = new VARMAAutoCovariance(var, 10);
        VARMAAutoCorrelation acf = new VARMAAutoCorrelation(var, 10);
        for (int i = 0; i < 10; i++) {
            System.out.printf("The acvf for the VAR model ar lag %d is:", i);
            System.out.println(acvf.evaluate(i));
            System.out.printf("The acf for the VAR model at lag %d is:", i);
            System.out.println(acf.evaluate(i));
        }
    }

    public static void multi_VMA() {
        Matrix[] THETA = new Matrix[1];
        THETA[0] = new DenseMatrix(
                new double[][]{
                    {0.5, 0.6},
                    {-0.7, 0.8}
                });
        VMAModel vma = new VMAModel(THETA);
        VARMAAutoCorrelation acf = new VARMAAutoCorrelation(vma, 10);
        for (int i = 0; i < 10; i++) {
            System.out.printf("The acf for the VMA model at lag %d is: ", i);
            System.out.println(acf.evaluate(i));
        }
    }

    public static void multi_VARMA() {
        Matrix PHI = new DenseMatrix(
                new double[][]{
                    {0.7, 0},
                    {0, 0.6}
                });

        Matrix THETA = new DenseMatrix(
                new double[][]{
                    {0.5, 0.6},
                    {-0.7, 0.8}
                });

        VARMAModel multiARMA = new VARMAModel(
                new Matrix[]{PHI},
                new Matrix[]{THETA});

        MultivariateIntTimeTimeSeries X_T = new MultivariateSimpleTimeSeries(
                new double[][]{
                    {-1.875, 1.693},
                    {-2.518, -0.03},
                    {-3.002, -1.057},
                    {-2.454, -1.038},
                    {-1.119, -1.086},
                    {-0.72, -0.455},
                    {-2.738, 0.962},
                    {-2.565, 1.992},
                    {-4.603, 2.434},
                    {-2.689, 2.118}
                });

        VARLinearRepresentation linear_varma = new VARLinearRepresentation(multiARMA);
        System.out.println("The first AR coefficient of the linear representation is:");
        System.out.println(linear_varma.AR(1));

        VARMAForecastOneStep instance = new VARMAForecastOneStep(X_T, multiARMA);

        int T = X_T.size();
        for (int t = 0; t <= T; t++) {
            Vector xTHat = instance.xHat(t + 1);
            Matrix V = instance.covariance(t);
            System.out.printf("Predictor at time %d: %s, covariance of errors: %s%n",
                    t, xTHat, V);
        }

    }

    public static void multi_VARIMA() {
        Matrix[] PHI = new Matrix[4];
        PHI[0] = new DenseMatrix(new DenseVector(new double[]{0.3}));
        PHI[1] = new DenseMatrix(new DenseVector(new double[]{-0.2}));
        PHI[2] = new DenseMatrix(new DenseVector(new double[]{0.05}));
        PHI[3] = new DenseMatrix(new DenseVector(new double[]{0.04}));

        Matrix[] THETA = new Matrix[2];
        THETA[0] = new DenseMatrix(new DenseVector(new double[]{0.2}));
        THETA[1] = new DenseMatrix(new DenseVector(new double[]{0.5}));

        int integration = 1;

        VARIMAModel varima = new VARIMAModel(PHI, integration, THETA);
        VARIMASim instance = new VARIMASim(varima);
        System.out.println("the sitimulated ARIMA process is ");
        for (int i = 0; i < 10; i++) {
            System.out.println(instance.nextVector()[0]);
        }
    }

    public static void multi_VEC() {
        Vector mu = new DenseVector(new double[]{1.33, 2.12});
        Matrix[] phi = new Matrix[]{
            new DenseMatrix(new double[][]{
                {0.2, 0.3},
                {0., 0.4}}),
            new DenseMatrix(new double[][]{
                {0.1, 0.2},
                {0.3, 0.1}})
        };

        VARModel var = new VARModel(mu, phi);

        // generate a sample
        VARIMASim sim = new VARIMASim(var);
        sim.seed(1234567831L);
        final int N = 5000;
        double[][] ts = new double[N][];
        for (int i = 0; i < N; ++i) {
            ts[i] = sim.nextVector();
        }
        MultivariateIntTimeTimeSeries mts = new MultivariateSimpleTimeSeries(ts);
        // fit a VAR model
        VARModel fitted = new VARFit(mts, 2);
        System.out.println("mu:");
        System.out.println(fitted.mu());
        System.out.println("phi0");
        System.out.println(fitted.AR(1));
        System.out.println("phi1");
        System.out.println(fitted.AR(2));
        // convert a VAR model into a VECM model
        VARXModel varx = new VARXModel(fitted.mu(),
                fitted.phi(),
                new DenseMatrix(2, 2).ZERO(),
                fitted.sigma());
        VECMLongrun vecm = new VECMLongrun(varx);
        System.out.println("vecm:");
        System.out.println(vecm.mu());
        System.out.println(vecm.p());
        System.out.println(vecm.gamma(1));
        System.out.println(vecm.gamma(2));
        System.out.println(vecm.psi());
        System.out.println(vecm.sigma());
    }

    public static void adf() {
        //ADFFiniteSampleDistribution dis1 = new ADFFiniteSampleDistribution(100, TrendType.NO_CONSTANT);
        //System.out.printf("The cumulative probability for an ADF sample distribution of 100 samples at the test statistic -1.95 is: %f%n", dis1.cdf(-1.95));
        //ADFFiniteSampleDistribution dis2 = new ADFFiniteSampleDistribution(100, TrendType.CONSTANT);
        //System.out.printf("The cumulative probability for an ADF sample distribution of 100 samples at the test statistic -2.89 is: %f%n", dis2.cdf(-2.89));
        //ADFFiniteSampleDistribution dis3 = new ADFFiniteSampleDistribution(100, TrendType.CONSTANT_TIME);
        //System.out.printf("The cumulative probability for an ADF sample distribution of 100 samples at the test statistic -3.45 is: %f%n", dis3.cdf(-3.45));

        AugmentedDickeyFuller test = new AugmentedDickeyFuller(new double[]{5.704, 0.727, 1.041, 0.942, 0.555, -1.102, -0.585, 0.090, -0.648, 0.325});

        System.out.println(test.getAlternativeHypothesis());
        System.out.println(test.getNullHypothesis());
        System.out.printf("the p-value for the test is: %f%n", test.pValue());
        System.out.printf("the statistics for the test is: %f%n", test.statistics());
    }

    public static void ljungbox() {
        IntTimeTimeSeries x = new SimpleTimeSeries(new double[]{5.704, 0.727, 1.041, 0.942, 0.555, -1.102, -0.585, 0.090, -0.648, 0.325});
        double[] xt = x.toArray();
        ConditionalSumOfSquares fit1 = new ConditionalSumOfSquares(
                xt,
                1, 0, 1);
        ARMAModel arma = fit1.getARMAModel();
        System.out.printf("the ARMA(1,1) model: %s%n", arma);
        ARMAForecastOneStep xt_hat = new ARMAForecastOneStep(x, arma);
        double[] residuals = new double[xt.length];
        for (int t = 0; t < xt.length; ++t) {
            residuals[t] = xt[t] - xt_hat.xHat(t);
        }
        LjungBox lb_test = new LjungBox(residuals, 9, 5);
        System.out.printf("The null hypothesis is %s%n", lb_test.getNullHypothesis());
        System.out.printf("The alternative hypothesis is %s%n", lb_test.getAlternativeHypothesis());
        System.out.printf("The test statistic is %f%n", lb_test.statistics());
        System.out.printf("The p-value is %f%n", lb_test.pValue());
    }

    public static void main(String[] args) {
        String curDir = System.getProperty("user.dir");
        System.out.println(curDir);
//        sp_daily();
        sp_ap_monthly();
//        decompose_sp_monthly();
//        AR();
//        MA();
//        ARMA();
//        acf_pacf();
//        decompose();
//        GARCH();
//        uniARMA();
//        sim_uniARIMA();
//        fit_uniARMA();
//        uniARIMA();
//        fit_uniARIMA();

//        fit_arima_residual_garch();
//        multi_VAR();
//        multi_VMA();
//        multi_VARMA();
//        multi_VARIMA();
//        multi_VEC();
//        adf();
//        ljungbox();
    }
}
