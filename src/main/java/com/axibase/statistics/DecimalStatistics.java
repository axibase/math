package com.axibase.statistics;

import java.util.List;

/**
 * Analog of org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
 * but uses BigDecimals instead of doubles.
 * implementation will follow to implementation of
 * org.apache.commons.math3.stat.descriptive.Statistics
 * view
 *
 */
public class DecimalStatistics implements Statistics {

    protected int windowSize = INFINITE_WINDOW;

    /** Store data values. */
    protected ResizableDecimalArray values = new ResizableDecimalArray();

    /** Number of digits after decimal point. */
    private int precision = DEFAULT_PRECISION;

    public static final int INFINITE_WINDOW = -1;
    public static final int DEFAULT_PRECISION = 10;

    public DecimalStatistics() {
    }

    public DecimalStatistics(List<? extends Number> values) {
        if (values != null) {
            //this.values = new ResizableDecimalArray(values);
        }
    }

    @Override
    public Number getMax() {
        return null;
    }

    @Override
    public Number getMean() {
        return null;
    }

    @Override
    public Number getMin() {
        return null;
    }

    @Override
    public long getN() {
        return 0;
    }

    @Override
    public Number getPercentile(double p) {
        return null;
    }

    @Override
    public Number getStandardDeviation() {
        return null;
    }

    @Override
    public Number getSum() {
        return null;
    }

    @Override
    public Number getVariance() {
        return null;
    }

    @Override
    public void addValue(Number value) {

    }
}
