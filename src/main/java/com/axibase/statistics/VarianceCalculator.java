package com.axibase.statistics;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Class implements statical methods to calculate variance and standard deviation of a data set.
 * Instead of the data set itself these methods use the StatisticalSummary object which
 * knows how to get basic statistics of the data set: number of elements,
 * sum of elements and sum of squares of elements.
 */
public class VarianceCalculator {

    /**
     * The method returns the rounded variance of some data set managed by an engine.
     * To perform necessary operations over data set the provided engine is used.
     * The second argument specifies which variance - sample or population will be calculated.
     * The third argument determines the rounding strategy.
     */
    public static BigDecimal variance(BasicStatistics engine, boolean sample, MathContext varianceContext) {
        if (engine.getN() == 0) {
            return null;
        }
        if (engine.getN() == 1) {
            return BigDecimal.ZERO;
        }
        BigDecimal sumOfSquares = engine.getSumsq();
        BigDecimal squareOfSum = engine.getSum().pow(2);
        BigDecimal N = BigDecimal.valueOf(engine.getN());
        BigDecimal numerator = N.multiply(sumOfSquares).subtract(squareOfSum);
        BigDecimal denominator = sample ? N.multiply(N.subtract(BigDecimal.ONE)) : N.pow(2);

        return numerator.divide(denominator, varianceContext);
    }

    /**
     * The method returns the rounded standard deviation of some data set managed by an engine.
     * To perform necessary operations over data set the provided engine is used.
     * The second argument specifies which variance - sample or population will be calculated.
     * The third argument determines the rounding strategy.
     */
    public static BigDecimal stdDev(BasicStatistics engine, boolean sample, MathContext stDevContext) {
        if (engine.getN() == 0) {
            return null;
        }
        if (engine.getN() == 1) {
            return BigDecimal.ZERO;
        }

        if (stDevContext.getPrecision() == 0) {
            stDevContext = new MathContext(0, RoundingMode.UNNECESSARY);
        }

        if (stDevContext.getRoundingMode() == RoundingMode.UNNECESSARY) {
            // TODO handle exception
            return SquareRoot.babylonian(variance(engine, sample, stDevContext), stDevContext);
        }

        // compute numerator and denominator of the sample variance
        BigDecimal sumOfSquares = engine.getSumsq();
        BigDecimal squareOfSum = engine.getSum().pow(2);
        BigDecimal N = BigDecimal.valueOf(engine.getN());
        BigDecimal numerator = N.multiply(sumOfSquares).subtract(squareOfSum);
        BigDecimal denominator = sample ? N.multiply(N.subtract(BigDecimal.ONE)) : N.pow(2);

        // get rounded variance
        int stdPrecision = stDevContext.getPrecision();
        int variancePrecision = 2 * stdPrecision + 2;
        MathContext varianceContext = new MathContext(variancePrecision, RoundingMode.DOWN);
        BigDecimal roundedVariance = numerator.divide(denominator, varianceContext);

        // get rounded standard deviation with one more digit of precision
        MathContext roundingContext = new MathContext(stdPrecision + 1, RoundingMode.DOWN);
        BigDecimal stDev = SquareRoot.babylonian(roundedVariance, roundingContext);

        // if we lucky to find exact standard deviation we return it
        boolean isExact = (stDev.pow(2).multiply(denominator).compareTo(numerator) == 0);
        if (isExact) {
            return stDev.round(stDevContext);
        }

        // the exact standard deviation is slightly (less than 1 ulp) bigger then stDev, so we can do rounding
        switch (stDevContext.getRoundingMode()) {

            case UP:
            case CEILING:
                roundingContext = new MathContext(stdPrecision, RoundingMode.DOWN);
                return stDev.round(roundingContext).add(BigDecimal.ONE).round(roundingContext);

            case DOWN:
            case FLOOR:
                return stDev.round(stDevContext);

            case HALF_UP:
            case HALF_DOWN:
            case HALF_EVEN:
                roundingContext = new MathContext(stdPrecision, RoundingMode.HALF_UP);
                return stDev.round(roundingContext);

            case UNNECESSARY:
                // this case was handled earlier

            default:
                String msg = "Unsupported rounding mode: " + stDevContext.getRoundingMode();
                throw new IllegalArgumentException(msg);
        }
    }

}
