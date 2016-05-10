package com.axibase.statistics;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * The interface extends BasicStatistics by common univariate statistics.
 * It is an analog of the StatisticalSummary interface from
 * the org.apache.commons.math3.stat.descriptive package for BigDecimal numbers.
 * It is implemented by the DescriptiveStatistics and SummaryStatistics classes.
 * As the methods of this class may return a decimal number with infinite number of digits,
 * so the MathContext should be used for rounding.
 * If a method hasn't a MathContext argument, then implementation will use its own MathContext.
 */
public interface StatisticalSummary extends BasicStatistics {

    BigDecimal getMean();
    BigDecimal getMean(MathContext meanContext);

    BigDecimal getVariance();
    BigDecimal getVariance(MathContext varianceContext);
    BigDecimal getPopulationVariance();
    BigDecimal getPopulationVariance(MathContext varianceContext);

    BigDecimal getStandardDeviation();
    BigDecimal getStandardDeviation(MathContext stdDevContext);
    BigDecimal getPopulationStandardDeviation();
    BigDecimal getPopulationStandardDeviation(MathContext stdDevContext);

}
