package com.axibase.math.stat.descriptive;

import java.math.BigDecimal;

/**
 * The interface enumerates some basic statistics used to calculate more complicated ones.
 * The classes DescriptiveStatistics and SummaryStatistics implement this interface in different ways.
 * The DescriptiveStatistics recalculates an statistics for stored data from scratch each time
 * the appropriate method is called.
 * The SummaryStatistics just updates stored values of statistics for each new data point.
 * The VarianceCalculator can use any implementation of this interface as an "engine" to calculate data variance.
 * So this interface is introduced to let the VarianceCalculator make its job with the same code for
 * both DescriptiveStatistics and SummaryStatistics.
 */
public interface BasicStatistics {
    long getN();
    BigDecimal getMax();
    BigDecimal getMin();
    BigDecimal getSum();
    BigDecimal getSumsq();
}
