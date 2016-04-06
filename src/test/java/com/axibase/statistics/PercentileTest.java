package com.axibase.statistics;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.ONE;
import static org.junit.Assert.*;

/**
 * Created by mikhail on 05.04.16.
 */
public class PercentileTest {

    // each of digits 0, ..., 9 is used twice
    BigDecimal[] digits = new BigDecimal[] {
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
        Percentile percentile = new Percentile(digits);
        BigDecimal value = percentile.evaluate("0");
        Assert.assertEquals(value.compareTo(new BigDecimal("0")), 0);
        value = percentile.evaluate("1");
        Assert.assertEquals(value.compareTo(new BigDecimal("9")), 0);
        value = percentile.evaluate("0.3");
        Assert.assertEquals(value.compareTo(new BigDecimal("2.3")), 0);
        value = percentile.evaluate("0.5");
        Assert.assertEquals(value.compareTo(new BigDecimal("4.5")), 0);
    }

    @Test
    public void testEqualValues() throws Exception {
        BigDecimal[] units = new BigDecimal[] {
                ONE, ONE, ONE, ONE, ONE,
                ONE, ONE, ONE, ONE, ONE,
                ONE, ONE, ONE, ONE, ONE,
                ONE, ONE, ONE, ONE, ONE
        };
        Percentile percentile = new Percentile(units);
        BigDecimal value = percentile.evaluate("0.456782654356735482164591273654623");
        Assert.assertEquals(value.compareTo(ONE), 0);
        System.out.println();
    }

    @Test
    public void testSelect() throws Exception {
        Percentile percentile = new Percentile(digits);
        BigDecimal value = percentile.select(0);
        Assert.assertEquals(value.compareTo(ZERO), 0);
        value = percentile.select(7);
        Assert.assertEquals(value.intValue(), 3);
        value = percentile.select(19);
        Assert.assertEquals(value.intValue(), 9);
        try {
            percentile.select(20);
            fail("The call of select(k) with index k out of bounds should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }
}