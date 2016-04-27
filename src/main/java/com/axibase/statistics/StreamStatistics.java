package com.axibase.statistics;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Analog of SummaryStatistics class from
 * the org.apache.commons.math3.stat.descriptive package,
 * but uses BigDecimals instead of doubles.
 *
 * This class should be used to calculate statistics for a stream of data.
 * To recalculate statistics for new data value add it with the {@link #addValue(BigDecimal)} method.
 * To clear all statistics and be ready for a new stream use the {@link #clear()} method.
 *
 * As the data values are not stored, some statistics (percentile and select) can not be calculated.
  */
public class StreamStatistics implements Statistics {

    private static final BigDecimal TWO = new BigDecimal("2");

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
    public BigDecimal sum() {
        if (n == 0) {
            return null;
        }
        return sum;
    }

    /**
     * Returns the sum of the squares of the values.
     * Null is returned if there are no values.
     */
    public BigDecimal sumOfSquares() {
        if (n == 0) {
            return null;
        }
        return sumOfSquares;
    }

    /**
     * Returns the mean of the values.
     * Null is returned if there are no values.
     */
    public BigDecimal mean(MathContext meanContext) {
        if (n == 0) {
            return null;
        }
        return sum.divide(BigDecimal.valueOf(n), meanContext);
    }

    /**
     * Returns the sample standard deviation of the values.
     * Null is returned if there are no values,
     * and 0 if there is a single value.
     */
    public BigDecimal sampleStdDev(MathContext stDevContext) {
        return VarianceCalculator.stdDev(this, true, stDevContext);
    }

    /**
     * Returns the population standard deviation of the values.
     * Null is returned if there are no values,
     * and 0 if there is a single value.
     */
    public BigDecimal populationStdDev(MathContext stDevContext) {
        return VarianceCalculator.stdDev(this, false, stDevContext);
    }

    /**
     * Returns the (sample) variance of the available values.
     *
     * <p>This method returns the bias-corrected sample variance (using {@code n - 1} in
     * the denominator).  Use {@link #populationVariance} for the non-bias-corrected
     * population variance.</p>
     *
     * Null is returned if there are no values.
     */
    public BigDecimal sampleVariance(MathContext varianceContext) {
        return VarianceCalculator.variance(this, true, varianceContext);
    }

    /**
     * Returns the <a href="http://en.wikibooks.org/wiki/Statistics/Summary/Variance">
     * population variance</a> of the values that have been added.
     *
     * Null is returned if there are no values.
     */
    public BigDecimal populationVariance(MathContext varianceContext) {
        return VarianceCalculator.variance(this, false, varianceContext);    }

    /**
     * Returns the maximum of the values.
     */
    public BigDecimal max() {
        return max;
    }

    /**
     * Returns the minimum of the values.
     */
    public BigDecimal min() {
        return min;
    }
}
