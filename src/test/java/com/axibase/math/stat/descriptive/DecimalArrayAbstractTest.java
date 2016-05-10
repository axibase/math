package com.axibase.math.stat.descriptive;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;


/**
 * This class contains test cases for the ResizableDecimalArray.
 */
public class DecimalArrayAbstractTest {

    protected ResizableDecimalArray da = null;

    // Array used to test rolling
    protected ResizableDecimalArray ra = null;

    @Test
    public void testAdd1000() {

        for (int i = 0; i < 1000; i++) {
            da.addElement(BigDecimal.valueOf(i));
        }

        Assert.assertEquals(
                "Number of elements should be equal to 1000 after adding 1000 values",
                1000,
                da.getNumElements());

        Assert.assertEquals(
                "The element at the 56th index should be 56",
                BigDecimal.valueOf(56),
                da.getElement(56));

    }

    @Test
    public void testGetValues() {
        double[] controlArray = { 2.0, 4.0, 6.0 };

        da.addElement(BigDecimal.valueOf(2.0));
        da.addElement(BigDecimal.valueOf(4.0));
        da.addElement(BigDecimal.valueOf(6.0));
        BigDecimal[] testArray = da.getElements();

        for (int i = 0; i < da.getNumElements(); i++) {
            Assert.assertEquals(
                    "The testArray values should equal the controlArray values, index i: "
                            + i
                            + " does not match",
                    testArray[i],
                    BigDecimal.valueOf(controlArray[i]));
        }

    }

    @Test
    public void testAddElementRolling() {
        ra.addElement(BigDecimal.valueOf(0.5));
        ra.addElement(BigDecimal.valueOf(1.0));
        ra.addElement(BigDecimal.valueOf(1.0));
        ra.addElement(BigDecimal.valueOf(1.0));
        ra.addElement(BigDecimal.valueOf(1.0));
        ra.addElement(BigDecimal.valueOf(1.0));
        ra.addElementRolling(BigDecimal.valueOf(2.0));

        Assert.assertEquals(
                "There should be 6 elements in the eda",
                6,
                ra.getNumElements());
        /*
        Assert.assertEquals(
                "The getMax element should be 2.0",
                BigDecimal.valueOf(2.0),
                StatUtils.getMax(ra.getElements()));
        Assert.assertEquals(
                "The getMin element should be 1.0",
                1.0,
                StatUtils.getMin(ra.getElements()),
                Double.MIN_VALUE);
        */

        for (int i = 0; i < 1024; i++) {
            ra.addElementRolling(BigDecimal.valueOf(i));
        }

        Assert.assertEquals(
                "We just inserted 1024 rolling elements, num elements should still be 6",
                6,
                ra.getNumElements());
    }

    /*
    @Test
    public void testMinMax() {
        da.addElement(2.0);
        da.addElement(22.0);
        da.addElement(-2.0);
        da.addElement(21.0);
        da.addElement(22.0);
        da.addElement(42.0);
        da.addElement(62.0);
        da.addElement(22.0);
        da.addElement(122.0);
        da.addElement(1212.0);

        Assert.assertEquals("Min should be -2.0", -2.0, StatUtils.getMin(da.getElements()), Double.MIN_VALUE);
        Assert.assertEquals(
                "Max should be 1212.0",
                1212.0,
                StatUtils.getMax(da.getElements()),
                Double.MIN_VALUE);
    }
    */

}
