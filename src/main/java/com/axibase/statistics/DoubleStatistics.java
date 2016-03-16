package com.axibase.statistics;

import java.util.List;

/**
 * This class is a simple wrapper around
 * org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
 * in order to implement the Statistics interface.
 */
public class DoubleStatistics implements Statistics {

    private org.apache.commons.math3.stat.descriptive.DescriptiveStatistics ds;

    public DoubleStatistics() {
        ds = new org.apache.commons.math3.stat.descriptive.DescriptiveStatistics();
    }

    /**
     * Convert provided values to Doubles and create instance.
     */
    public DoubleStatistics(List<? extends Number> values) {
        double[] doubleArray = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            doubleArray[i] = values.get(i).doubleValue();
        }
        ds = new org.apache.commons.math3.stat.descriptive.DescriptiveStatistics(doubleArray);
    }

    @Override
    public void addValue(Number value) {
        ds.addValue(value.doubleValue());
    }

    @Override
    public Number getMax() {
        return ds.getMax();
    }

    @Override
    public Number getMean() {
        return ds.getMean();
    }

    @Override
    public Number getMin() {
        return ds.getMin();
    }

    @Override
    public long getN() {
        return ds.getN();
    }

    @Override
    public Number getPercentile(double p) {
        return ds.getPercentile(p);
    }

    @Override
    public Number getStandardDeviation() {
        return ds.getStandardDeviation();
    }

    @Override
    public Number getSum() {
        return ds.getSum();
    }

    @Override
    public Number getVariance() {
        return ds.getVariance();
    }
}
