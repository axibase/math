package com.axibase.statistics;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.ONE;
import static org.junit.Assert.*;

/**
 * test
 */
public class PercentileCalculatorTest {

    BigDecimal[]  ticket_2353 = new BigDecimal[] {
            new BigDecimal("0.02"),
            new BigDecimal("0.02"),
            new BigDecimal("0.02"),
            new BigDecimal("0.02"),
            new BigDecimal("0.3"),
            new BigDecimal("0.3"),
            new BigDecimal("0.3"),
    };

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
        PercentileCalculator percentileCalculator = new PercentileCalculator(digits);
        BigDecimal value = percentileCalculator.evaluate("0");
        //Assert.assertEquals(value.compareTo(new BigDecimal("0")), 0);
        value = percentileCalculator.evaluate("1");
        //Assert.assertEquals(value.compareTo(new BigDecimal("9")), 0);
        value = percentileCalculator.evaluate("0.3");
        //Assert.assertEquals(value.compareTo(new BigDecimal("2.3")), 0);
        value = percentileCalculator.evaluate("0.5");
        //Assert.assertEquals(value.compareTo(new BigDecimal("4.5")), 0);

        percentileCalculator = new PercentileCalculator(ticket_2353);
        System.out.println("50 getPercentile: " + percentileCalculator.evaluate(new BigDecimal("0.5")));
    }

    @Test
    public void testEqualValues() throws Exception {
        BigDecimal[] units = new BigDecimal[] {
                ONE, ONE, ONE, ONE, ONE,
                ONE, ONE, ONE, ONE, ONE,
                ONE, ONE, ONE, ONE, ONE,
                ONE, ONE, ONE, ONE, ONE
        };
        PercentileCalculator percentileCalculator = new PercentileCalculator(units);
        BigDecimal value = percentileCalculator.evaluate("0.456782654356735482164591273654623");
        Assert.assertEquals(value.compareTo(ONE), 0);
        System.out.println();
    }

    @Test
    public void testSelect() throws Exception {
        PercentileCalculator percentileCalculator = new PercentileCalculator(digits);
        BigDecimal value = percentileCalculator.select(0);
        Assert.assertEquals(value.compareTo(ZERO), 0);
        value = percentileCalculator.select(7);
        Assert.assertEquals(value.intValue(), 3);
        value = percentileCalculator.select(19);
        Assert.assertEquals(value.intValue(), 9);
        try {
            percentileCalculator.select(20);
            fail("The call of select(k) with index k out of bounds should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }
}