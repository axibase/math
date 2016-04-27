package com.axibase.statistics;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Common methods of DecimalStatistics and DecimalStatisticsIncremental.
 */
public interface Statistics extends BasicStatistics {

    void addValue(BigDecimal value);
    void clear();

    BigDecimal mean(MathContext meanContext);

    BigDecimal sampleVariance(MathContext varianceContext);
    BigDecimal populationVariance(MathContext varianceContext);

    BigDecimal sampleStdDev(MathContext stdDevContext);
    BigDecimal populationStdDev(MathContext stdDevContext);

}
