package com.axibase.statistics;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by mikhail on 05.04.16.
 */
public class PercentileTest {

    BigDecimal[] values = new BigDecimal[] {
            new BigDecimal("4"),
            new BigDecimal("2"),
            new BigDecimal("7"),
            new BigDecimal("1"),
            new BigDecimal("9"),
            new BigDecimal("8"),
            new BigDecimal("3"),
            new BigDecimal("6"),
            new BigDecimal("5"),
            new BigDecimal("0"),
    };

    @Test
    public void testEvaluate() throws Exception {
        Percentile percentile = new Percentile(values);
        System.out.println(percentile.evaluate("1.0"));
    }

    @Test
    public void testSelect() throws Exception {
        Percentile percentile = new Percentile(values);
        System.out.println(percentile.select(9));
    }
}