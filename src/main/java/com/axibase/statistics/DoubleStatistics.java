package com.axibase.statistics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This class is a simple wrapper around
 * org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
 * in order to implement the Statistics interface.
 */
public class DoubleStatistics implements Statistics<Double> {

    private DescriptiveStatistics ds;

    /** Construct a DoubleStatistics instance with an infinite window. */
    public DoubleStatistics() {
        ds = new DescriptiveStatistics();
    }

    /**
     * Construct a DoubleStatistics instance with an infinite window
     * and provided initial data.
     */
    public DoubleStatistics(double[] values) {
        ds = new DescriptiveStatistics(values);
    }

    /**
     * Construct a DoubleStatistics instance with the specified window size.
     */
    public DoubleStatistics(int windowSize) {
        ds = new DescriptiveStatistics(windowSize);
    }

    @Override
    public void addValue(Double value) {
        ds.addValue(value);
    }

    @Override
    public void clear() {
        ds.clear();
    }

    @Override
    public Double getElement(int index) {
        return ds.getElement(index);
    }

    @Override
    public Double getMax() {
        return ds.getMax();
    }

    @Override
    public Double getMean() {
        return ds.getMean();
    }

    @Override
    public Double getMin() {
        return ds.getMin();
    }

    @Override
    public long getN() {
        return ds.getN();
    }

    @Override
    public double getPercentile(double p) {
        return ds.getPercentile(p);
    }

    @Override
    public double getStandardDeviation() {
        return ds.getStandardDeviation();
    }

    @Override
    public Double getSum() {
        return ds.getSum();
    }

    @Override
    public double getVariance() {
        return ds.getVariance();
    }

    @Override
    public int getWindowSize() {
        return ds.getWindowSize();
    }

    @Override
    public void removeMostRecentValue() {
        ds.removeMostRecentValue();
    }

    @Override
    public Double replaceMostRecentValue(Double number) {
        return ds.replaceMostRecentValue(number.doubleValue());
    }

    @Override
    public void setWindowSize(int windowSize) {
        ds.setWindowSize(windowSize);
    }
}
