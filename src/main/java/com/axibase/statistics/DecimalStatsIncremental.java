package com.axibase.statistics;

import java.math.BigDecimal;

/**
 * Analog of SummaryStatistics class from
 * the org.apache.commons.math3.stat.descriptive package,
 * but uses BigDecimals instead of doubles.
 * The code is refactoring of the open source Apache's code.
 */
public class DecimalStatsIncremental {

    private boolean computed;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal mean;
    private BigDecimal median;
    private BigDecimal variance;
    private BigDecimal std;

    /**
     * Add value to the dataset,
     * and update statistics - max, min, mean, median (?),
     * variance and standard deviation.
     */
    public void addAndUpdate(BigDecimal value) {
//        if (windowSize == INFINITE_WINDOW || getN() < windowSize) {
//            updateStatistics(value, BigDecimal.ZERO);
//            ra.addElement(value);
//        } else {
//            updateStatistics(value, ra.getElement(0));
//            ra.addElementRolling(value);
//        }
    }

    private void updateStatistics(BigDecimal newValue, BigDecimal discardedValue) {

    }



}
