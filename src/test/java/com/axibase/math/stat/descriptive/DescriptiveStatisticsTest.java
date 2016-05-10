package com.axibase.math.stat.descriptive;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * test
 */
public class DescriptiveStatisticsTest {

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
        DescriptiveStatistics calculator = new DescriptiveStatistics(ticket_2353);
        System.out.println("50 getPercentile = " + calculator.getPercentile(new BigDecimal("0.5")));
        System.out.println("60 getPercentile = " + calculator.getPercentile(new BigDecimal("0.6")));
        System.out.println("70 getPercentile = " + calculator.getPercentile(new BigDecimal("0.7")));
    }
}