package com.axibase.statistics;

import java.math.BigDecimal;

/**
 * The interface enumerates some basic statistics used to calculate more complicated ones.
 */
public interface BasicStatistics {
    long getN();
    BigDecimal max();
    BigDecimal min();
    BigDecimal sum();
    BigDecimal sumOfSquares();
}
