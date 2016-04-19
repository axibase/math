package com.axibase.statistics;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.Assert.*;

/**
 * Created by mikhail on 19.04.16.
 */
public class SquareRootTest {

    @Test
    public void testBabylonian() throws Exception {

        // test numbers 0, ..., 4000
        for (int i = 0; i < 100001; i++) {
            BigInteger sqrt = new BigInteger(new Integer(i).toString());
            testNumber(sqrt);
        }

        // do random test of big numbers with given number of digits (places)
        for (int places = 10; places < 5001; places++) {
            for (int counter = 0; counter < 100; counter++) {
                BigInteger number = new BigInteger(BigDecimalGenerator.generateInteger(places));
                testNumber(number);
            }
        }
    }

    private static void testNumber(BigInteger number) {

        BigInteger sqrt = SquareRoot.babylonian(number);

        BigInteger floor = sqrt.multiply(sqrt);
        BigInteger ceiling = floor.add(sqrt.shiftLeft(1).add(BigInteger.ONE));

        String msg1 = "SquareRoot.babylonian(BigInteger number) returns sqrt s.t.: sqrt**2 > number, number = ";
        String msg2 = "SquareRoot.babylonian(BigInteger number) returns sqrt s.t.: (sqrt + 1)**2 <= number, number = ";

        Assert.assertTrue(msg1 + number, number.compareTo(floor) >= 0);
        Assert.assertTrue(msg2 + number, number.compareTo(ceiling) < 0);
    }

    @Test
    public void testAllDec() throws Exception {

        int integerPlaces = 500;
        int fractionalPlaces = 500;
        int sampleSize = 1;

        for (int before = 0; before < integerPlaces; before++) {
            for (int after = 0; after < fractionalPlaces; after++) {
                for (int counter = 0; counter < sampleSize; counter++) {

                    String str = BigDecimalGenerator.generateDecimal(before, after);
                    if (!str.equals("")) {

                        BigDecimal exactSqrt = new BigDecimal(str);
                        BigDecimal number = exactSqrt.multiply(exactSqrt);
                        // TODO is that precision correct?
                        MathContext sqrtContext = new MathContext(exactSqrt.precision() + 1, RoundingMode.HALF_UP);

                        BigDecimal sqrt = SquareRoot.babylonian(number, sqrtContext);
                        BigDecimal rounded = exactSqrt.round(sqrtContext);
                        String msg = "SquareRootDev.babylonian error for: " + exactSqrt.toPlainString();

                        Assert.assertTrue(rounded.compareTo(sqrt) == 0);
                    }
                }
            }
        }
    }



}