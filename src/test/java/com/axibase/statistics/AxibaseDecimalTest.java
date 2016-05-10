package com.axibase.statistics;

import org.junit.Assert;
import org.junit.Test;

/**
 * test
 */
public class AxibaseDecimalTest {

    @Test
    public void testConstructor() {

        AxibaseDecimal value = new AxibaseDecimal("1234000");
        Assert.assertEquals(1234000, value.getSignificand());
        Assert.assertEquals(0, value.getExponent());

        value = new AxibaseDecimal("-1234000.00");
        Assert.assertEquals(-123400000, value.getSignificand());
        Assert.assertEquals(-2, value.getExponent());

        value = new AxibaseDecimal("-123.4000");
        Assert.assertEquals(-1234000, value.getSignificand());
        Assert.assertEquals(-4, value.getExponent());

        value = new AxibaseDecimal("-123.04");
        Assert.assertEquals(-12304, value.getSignificand());
        Assert.assertEquals(-2, value.getExponent());

        value = new AxibaseDecimal("0");
        Assert.assertEquals(0, value.getSignificand());
        Assert.assertEquals(0, value.getExponent());

        value = new AxibaseDecimal("0.0");
        Assert.assertEquals(0, value.getSignificand());
        Assert.assertEquals(-1, value.getExponent());

        value = new AxibaseDecimal("2010.0000");
        Assert.assertEquals(20100000, value.getSignificand());
        Assert.assertEquals(-4, value.getExponent());

        value = new AxibaseDecimal("10.00001");
        Assert.assertEquals(1000001, value.getSignificand());
        Assert.assertEquals(-5, value.getExponent());

        value = new AxibaseDecimal("-10.02000");
        Assert.assertEquals(-1002000, value.getSignificand());
        Assert.assertEquals(-5, value.getExponent());

        value = new AxibaseDecimal("-12");
        Assert.assertEquals(-12, value.getSignificand());
        Assert.assertEquals(-0, value.getExponent());

    }

}