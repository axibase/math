package com.axibase.statistics;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * test
 */
public class StoredStatisticsTest {

    BigDecimal[]  ticket_2353 = new BigDecimal[] {
            new BigDecimal("0.02"),
            new BigDecimal("0.02"),
            new BigDecimal("0.02"),
            new BigDecimal("0.02"),
            new BigDecimal("0.3"),
            new BigDecimal("0.3"),
            new BigDecimal("0.3"),
    };


    @Test
    public void testPercentile() throws Exception {
        StoredStatistics calculator = new StoredStatistics(ticket_2353);
        System.out.println("50 percentile = " + calculator.percentile(new BigDecimal("0.5")));
        System.out.println("60 percentile = " + calculator.percentile(new BigDecimal("0.6")));
        System.out.println("70 percentile = " + calculator.percentile(new BigDecimal("0.7")));
    }
}