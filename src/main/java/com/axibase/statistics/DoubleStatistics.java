package com.axibase.statistics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This class is a simple wrapper around
 * org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
 * in order to implement the Statistics interface.
 */
public class DoubleStatistics {

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


    public void addValue(Double value) {
        ds.addValue(value);
    }


    public void clear() {
        ds.clear();
    }


    public Double getElement(int index) {
        return ds.getElement(index);
    }


    public Double getMax() {
        return ds.getMax();
    }


    public Double getMean() {
        return ds.getMean();
    }


    public Double getMin() {
        return ds.getMin();
    }


    public long getN() {
        return ds.getN();
    }


    public double getPercentile(double p) {
        return ds.getPercentile(p);
    }


    public double getStandardDeviation() {
        return ds.getStandardDeviation();
    }


    public Double getSum() {
        return ds.getSum();
    }


    public double getVariance() {
        return ds.getVariance();
    }


    public int getWindowSize() {
        return ds.getWindowSize();
    }


    public void removeMostRecentValue() {
        ds.removeMostRecentValue();
    }


    public Double replaceMostRecentValue(Double number) {
        return ds.replaceMostRecentValue(number.doubleValue());
    }


    public void setWindowSize(int windowSize) {
        ds.setWindowSize(windowSize);
    }
}
