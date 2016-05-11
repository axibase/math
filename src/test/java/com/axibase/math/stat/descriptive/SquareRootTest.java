/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axibase.math.stat.descriptive;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

/**
 * test
 */
public class SquareRootTest {

    String[] numStrs = {
            // 99225 = 315^2
            "99225000000000000000000000000000000000000000000000000000000000001"
    };

    @Test
    public void testBabylonianInt() throws Exception {

        // test numbers 0, ..., 4000
        for (int i = 0; i < 100001; i++) {
            BigInteger sqrt = new BigInteger(new Integer(i).toString());
            testIntNumber(sqrt);
        }

        // do random test of big numbers with given number of digits (places)
        Random generator = new Random();
        for (int places = 10; places < 501; places++) {
            for (int counter = 0; counter < 100; counter++) {
                BigInteger number = new BigInteger(BigDecimalGenerator.generateInteger(places, generator).toString());
                testIntNumber(number);
            }
        }
    }

    private static void testIntNumber(BigInteger number) {

        BigInteger sqrt = SquareRoot.babylonian(number);

        BigInteger floor = sqrt.multiply(sqrt);
        BigInteger ceiling = floor.add(sqrt.shiftLeft(1).add(BigInteger.ONE));

        String msg1 = "SquareRoot.babylonian(BigInteger number) returns sqrt s.t.: sqrt**2 > number, number = ";
        String msg2 = "SquareRoot.babylonian(BigInteger number) returns sqrt s.t.: (sqrt + 1)**2 <= number, number = ";

        Assert.assertTrue(msg1 + number, number.compareTo(floor) >= 0);
        Assert.assertTrue(msg2 + number, number.compareTo(ceiling) < 0);
    }

    @Test
    public void testRandomBabylonianDec() throws Exception {

        // the total number of roots to compute equals to the product
        // of these 4 numbers multiplied by 5 if all rounding modes are tested!
        int integerPlaces = 1000;
        int fractionalPlaces = 7000;
        int sampleSize = 10;
        int numOfContexts = 10;
        boolean testAllRoundingModes = true;
        RoundingMode[] roundingModes = {RoundingMode.UP, RoundingMode.DOWN, RoundingMode.HALF_UP, RoundingMode.HALF_DOWN, RoundingMode.HALF_EVEN};

        Random generator = new Random();

        for (int before = 0; before < integerPlaces; before++) {
            System.out.println("Before = " + before + " ");
            for (int after = 0; after < fractionalPlaces; after++) {
                if (after == 2000) {
                    System.out.println("After = 2000 ");
                }
                for (int sampleCounter = 0; sampleCounter < sampleSize; sampleCounter++) {

                    String str = BigDecimalGenerator.generateDecimal(before, after, generator);
                    if (!str.equals("")) {

                        BigDecimal exactSqrt = new BigDecimal(str);
                        BigDecimal number = exactSqrt.multiply(exactSqrt);

                        // test exact context
                        MathContext sqrtContext = new MathContext(exactSqrt.precision(), RoundingMode.UNNECESSARY);
                        BigDecimal sqrt = SquareRoot.babylonian(number, sqrtContext);
                        BigDecimal rounded = exactSqrt.round(sqrtContext);
                        if (rounded.compareTo(sqrt) != 0) {
                            String msg = "Error for: \n exact sqrt = " + exactSqrt.toPlainString() +
                                    "\n precision = " + sqrtContext.getPrecision() +
                                    "\n rounding mode = " + sqrtContext.getRoundingMode() +
                                    "\n the result is not equal to rounding of the exact sqrt.";
                            System.out.println(msg);
                        }

                        for (int contextCounter = 0; contextCounter < numOfContexts; contextCounter++) {

                            int precision = generator.nextInt(exactSqrt.precision() + 10) + 1;

                            for (RoundingMode roundingMode : roundingModes) {

                                sqrtContext = new MathContext(precision, roundingMode);
                                sqrt = SquareRoot.babylonian(number, sqrtContext);
                                rounded = exactSqrt.round(sqrtContext);
                                if (rounded.compareTo(sqrt) != 0) {
                                    String msg = "Error for: \n exact sqrt = " + exactSqrt.toPlainString() +
                                            "\n precision = " + sqrtContext.getPrecision() +
                                            "\n rounding mode = " + sqrtContext.getRoundingMode() +
                                            "\n the result is not equal to rounding of the exact sqrt.";
                                    System.out.println(msg);
                                }
                                if (exactSqrt.compareTo(BigDecimal.ZERO) != 0 && sqrt.precision() != sqrtContext.getPrecision()) {
                                    String msg = "Error for: \n exact sqrt = " + exactSqrt.toPlainString() +
                                            "\n precision = " + sqrtContext.getPrecision() +
                                            "\n rounding mode = " + sqrtContext.getRoundingMode() +
                                            "\n result wrong precision = " + sqrt.precision();
                                    System.out.println(msg);
                                }
                                //Assert.assertTrue(msg, rounded.compareTo(sqrt) == 0);
                                if (!testAllRoundingModes) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void testDecNumber(BigDecimal exactSqrt, MathContext sqrtContext) {
        BigDecimal number = exactSqrt.multiply(exactSqrt);
        BigDecimal sqrt = SquareRoot.babylonian(number, sqrtContext);
        BigDecimal rounded = exactSqrt.round(sqrtContext);
        String msg = "Error for: \n exact sqrt = " + exactSqrt.toPlainString() +
                "\n precision = " + sqrtContext.getPrecision() +
                "\n rounding mode = " + sqrtContext.getRoundingMode();
        Assert.assertTrue(msg, rounded.compareTo(sqrt) == 0);
    }

    @Test
    public void testConvert() throws Exception {

        String msg = "Wrong answer for ";

        String str = "0.00123450";
        BigDecimal result = SquareRoot.convert(str, 3);
        Assert.assertEquals(1235, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 6, result.scale());

        str = "0.00123450";
        result = SquareRoot.convert(str, 4);
        Assert.assertEquals(1235, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 6, result.scale());

        str = "0.0123450";
        result = SquareRoot.convert(str, 3);
        Assert.assertEquals(123, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 4, result.scale());

        str = "0.0123450";
        result = SquareRoot.convert(str, 4);
        Assert.assertEquals(12345, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 6, result.scale());

        str = "0.01";
        result = SquareRoot.convert(str, 3);
        Assert.assertEquals(100, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 4, result.scale());

        str = "41.00001";
        result = SquareRoot.convert(str, 3);
        Assert.assertEquals(4100, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 2, result.scale());

        str = "41.0051";
        result = SquareRoot.convert(str, 3);
        Assert.assertEquals(4101, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 2, result.scale());

        str = "4.1";
        result = SquareRoot.convert(str, 4);
        Assert.assertEquals(41000, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 4, result.scale());

        str = "5324.123044";
        result = SquareRoot.convert(str, 3);
        Assert.assertEquals(5324, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 0, result.scale());

        str = "5324.123045";
        result = SquareRoot.convert(str, 1);
        Assert.assertEquals(53, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, -2, result.scale());

        str = "5354.123046";
        result = SquareRoot.convert(str, 2);
        Assert.assertEquals(54, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, -2, result.scale());

        str = "5354.123046";
        result = SquareRoot.convert(str, 1);
        Assert.assertEquals(54, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, -2, result.scale());

        str = "53254.123045";
        result = SquareRoot.convert(str, 1);
        Assert.assertEquals(5, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, -4, result.scale());


        BigDecimal number = new BigDecimal("1230000");
        number.setScale(-3);
        result = SquareRoot.convert(number, 7);
        Assert.assertEquals(1230000, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 0, result.scale());
        result = SquareRoot.convert(number, 3);
        Assert.assertEquals(123, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, -4, result.scale());
        result = SquareRoot.convert(number, 4);
        Assert.assertEquals(12300, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, -2, result.scale());
        result = SquareRoot.convert(number, 8);
        Assert.assertEquals(123000000, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 2, result.scale());
        result = SquareRoot.convert(number, 9);
        Assert.assertEquals(123000000, result.unscaledValue().intValue());
        Assert.assertEquals(msg + str, 2, result.scale());
    }

    @Test
    public void main() {
        //MathContext sqrtContext = new MathContext(2, RoundingMode.HALF_DOWN);
        //BigDecimal exactSqrt = new BigDecimal("0.725046");

        //MathContext sqrtContext = new MathContext(1, RoundingMode.UP);
        //BigDecimal exactSqrt = new BigDecimal("0.19999947743546688333183052787213063415106300743277342814428459779598500653045333870910352445");

        MathContext sqrtContext = new MathContext(1, RoundingMode.HALF_UP);
        BigDecimal exactSqrt = new BigDecimal("0.55");

        testDecNumber(exactSqrt, sqrtContext);

        BigDecimal number = new BigDecimal(new BigInteger("4"), -100).add(BigDecimal.ONE);
        sqrtContext = new MathContext(1, RoundingMode.UP);
        System.out.println(SquareRoot.babylonian(number, sqrtContext));

        number = new BigDecimal(new BigInteger("4"), -100).subtract(BigDecimal.ONE);
        sqrtContext = new MathContext(1, RoundingMode.DOWN);
        System.out.println(SquareRoot.babylonian(number, sqrtContext));

        number = new BigDecimal(new BigInteger("9"), -100).subtract(BigDecimal.ONE);
        sqrtContext = new MathContext(1, RoundingMode.DOWN);
        System.out.println(SquareRoot.babylonian(number, sqrtContext));

    }

}