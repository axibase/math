package com.axibase.statistics;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 */
public interface BasicStatistics {
    long getN();
    BigDecimal max();
    BigDecimal min();
    BigDecimal sum();
    BigDecimal sumOfSquares();
}
