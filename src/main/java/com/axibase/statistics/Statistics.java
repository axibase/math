package com.axibase.statistics;

/**
 * Extends org.apache.commons.math3.stat.descriptive.StatisticSummary,
 * but change return value to Number and add getPercentile() method.
 */
public interface Statistics {
    Number getMax();
    Number getMean();
    Number getMin();
    long getN();
    Number getPercentile(double p);
    Number getStandardDeviation();
    Number getSum();
    Number getVariance();
    void addValue(Number value);
}
