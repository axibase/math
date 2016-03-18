package com.axibase.statistics;

import java.math.BigDecimal;
import java.util.List;

/**
 * Analog of org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
 * but uses BigDecimals instead of doubles.
 * implementation follows to the implementation of
 * org.apache.commons.math3.stat.descriptive.Statistics
 */
public class DecimalStatistics implements Statistics {

    private static final int INFINITE_WINDOW = -1;
    private int windowSize = INFINITE_WINDOW;

    /** Store data values. */
    protected ResizableDecimalArray ra = new ResizableDecimalArray();


    public DecimalStatistics() {
    }

    public DecimalStatistics(BigDecimal[] values) {
        if (values != null) {
            //this.values = new ResizableDecimalArray(values);
        }
    }


    @Override
    public void addValue(Number value) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Number getElement(int index) {
        return null;
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
    public int getWindowSize() {
        return 0;
    }

    @Override
    public void removeMostRecentValue() {

    }

    @Override
    public Number replaceMostRecentValue(Number number) {
        return null;
    }

    @Override
    public void setWindowSize(int windowSize) {

    }

}
