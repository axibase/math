package com.axibase.math.stat.descriptive;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Computes statistics for a stream of data values added using the addValue method.
 *
 * Analog of the SummaryStatistics class from
 * the org.apache.commons.math3.stat.descriptive package,
 * but uses BigDecimals instead of doubles.
 *
 * This class should be used to calculate statistics for a stream of data.
 * To recalculate statistics for new data value add it with the {@link #addValue(BigDecimal)} method.
 * To clear all statistics and be ready for a new stream use the {@link #clear()} method.
 *
 * As the data values are not stored, some statistics (percentile and select) can not be calculated.
  */
public class SummaryStatistics implements StatisticalSummary {

    private static final BigDecimal TWO = new BigDecimal("2");

    private static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(16, RoundingMode.HALF_UP);
    private MathContext mathContext = DEFAULT_MATH_CONTEXT;

    /**
     * Construct an instance with default MathContext.
     */
    public SummaryStatistics() {
    }

    public SummaryStatistics(MathContext mathContext) {
        this.mathContext = mathContext;
    }

    /* values counter */
    private  long n = 0;
    private BigDecimal min = null;
    private BigDecimal max = null;
    private BigDecimal sum = null;
    private BigDecimal sumOfSquares = null;

    /**
     * Update statistics with provided value from the data stream.
     */
    public void addValue(BigDecimal value) {
        if (n == 0) {
            min = value;
            max = value;
            sum = value;
            sumOfSquares = value.pow(2);
        } else {
            min = value.min(min);
            max = value.max(max);
            sum = sum.add(value);
            sumOfSquares = sumOfSquares.add(value.pow(2));
        }
        n++;
    }

    /**
     * Clear all statistics.
     */
    public void clear() {
        n = 0;
        min = null;
        max = null;
        sum = null;
        sumOfSquares = null;
    }

    /**
     * Returns the number of values.
     */
    public long getN() {
        return n;
    }

    /**
     * Returns the sum of the values.
     * If there are no values return null.
     */
    public BigDecimal getSum() {
        if (n == 0) {
            return null;
        }
        return sum;
    }

    /**
     * Returns the sum of the squares of the values.
     * Null is returned if there are no values.
     */
    public BigDecimal getSumsq() {
        if (n == 0) {
            return null;
        }
        return sumOfSquares;
    }

    @Override
    public BigDecimal getMean() {
        return getMean(mathContext);
    }

    /**
     * Returns the getMean of the values.
     * Null is returned if there are no values.
     */
    public BigDecimal getMean(MathContext meanContext) {
        if (n == 0) {
            return null;
        }
        return sum.divide(BigDecimal.valueOf(n), meanContext);
    }

    @Override
    public BigDecimal getStandardDeviation() {
        return getStandardDeviation(mathContext);
    }

    /**
     * Returns the sample standard deviation of the values.
     * Null is returned if there are no values,
     * and 0 if there is a single value.
     */
    public BigDecimal getStandardDeviation(MathContext stDevContext) {
        return VarianceCalculator.stdDev(this, true, stDevContext);
    }

    @Override
    public BigDecimal getPopulationStandardDeviation() {
        return getPopulationStandardDeviation(mathContext);
    }

    /**
     * Returns the population standard deviation of the values.
     * Null is returned if there are no values,
     * and 0 if there is a single value.
     */
    public BigDecimal getPopulationStandardDeviation(MathContext stDevContext) {
        return VarianceCalculator.stdDev(this, false, stDevContext);
    }


    @Override
    public BigDecimal getVariance() {
        return getVariance(mathContext);
    }

    /**
     * Returns the (sample) variance of the available values.
     *
     * <p>This method returns the bias-corrected sample variance (using {@code n - 1} in
     * the denominator).  Use {@link #getPopulationVariance} for the non-bias-corrected
     * population variance.</p>
     *
     * Null is returned if there are no values.
     */
    public BigDecimal getVariance(MathContext varianceContext) {
        return VarianceCalculator.variance(this, true, varianceContext);
    }

    @Override
    public BigDecimal getPopulationVariance() {
        return getPopulationVariance(mathContext);
    }

    /**
     * Returns the <a href="http://en.wikibooks.org/wiki/Statistics/Summary/Variance">
     * population variance</a> of the values that have been added.
     *
     * Null is returned if there are no values.
     */
    public BigDecimal getPopulationVariance(MathContext varianceContext) {
        return VarianceCalculator.variance(this, false, varianceContext);    }

    /**
     * Returns the maximum of the values.
     */
    public BigDecimal getMax() {
        return max;
    }

    /**
     * Returns the minimum of the values.
     */
    public BigDecimal getMin() {
        return min;
    }
}
