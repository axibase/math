package com.axibase.statistics;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import static com.axibase.statistics.SquareRoot.TWO;
import static com.axibase.statistics.SquareRoot.estimateSqrtLelieveld;
import static com.axibase.statistics.SquareRoot.estimateSqrt;

/**
 * test
 */
public class SquareRootTest {

    String[] strings = {"0",
            "1",
            "4",
            "9",
            "10000000000000000", // 10^16
            "8100000000000000000000", // 81 * 10^20
            "121000000000000000000000000000000", // 121 * 10^30
            "1210000000000000000000000000000000000000000", // 121 * 10^40
            "1543456953465123654037645628562864375123654356534629365923",
            "44242425252526374885450349876045867765309458673490586749058",
            "63653265376734672638726425348267354892567421543862737687312564624358367462645392348354245613547126534",
            "636532653767346726387264253482673548925674215438627376873125646243583674626453923483542456135471265323",
            "0.01",
            "0.0004",
            "0.000000000000000000000121", // 121 * 10^(-24)
            "2",
            "3",
            "12345678901234567890",
            "0.34569876524342561618732367285",
            "0.0000000010000001001010000201021020201002312102012001021032024332140235412343401223423435400012345",
            "100"};

    BigDecimal[] decimals = new BigDecimal[strings.length];

    @Before
    public void init() {
        for (int i = 0; i < strings.length; i++) {
            decimals[i] = new BigDecimal(strings[i]);
        }
    }

    @Test
    public void testEstimateSqrtFixed() {
        for (int i = 0; i < strings.length; i++) {
            System.out.println("\n--------------------------------");
            System.out.println("Number:  " + strings[i]);

            BigDecimal myEstimation = estimateSqrt(decimals[i]);
            BigDecimal myError = decimals[i].subtract(myEstimation.multiply(myEstimation));
            System.out.println("My error:    " + myError.toPlainString());

            BigDecimal fransEstimation = estimateSqrtLelieveld(decimals[i]);
            BigDecimal fransDeviation = decimals[i].subtract(fransEstimation.multiply(fransEstimation));
            System.out.println("Frans error: " + fransDeviation.toPlainString());
        }
    }

    @Test
    public void testEstimateSqrtRandom() {
        MathContext mathContext = new MathContext(3);
        BigDecimal difference = BigDecimal.ZERO;
        BigDecimal worstDifference = BigDecimal.ZERO;
        boolean myBest = true;
        for (int n = 1; n < 150; n++) {
            System.out.println("Digits limit: " + n);
            for (int i = 0; i < 10000; i++) {
                //System.out.println("\n--------------------------------");
                BigDecimal number = new BigDecimal(BigDecimalGenerator.generateDecimal(n));
                if (number.equals(BigDecimal.ZERO)) {
                    continue;
                }
                //System.out.println("Number:  " + number.toPlainString());

                BigDecimal myEstimation = estimateSqrt(number);
                BigDecimal myError = number.subtract(myEstimation.multiply(myEstimation)).abs();
                myError = myError.divide(number, mathContext);

                BigDecimal fransEstimation = estimateSqrtLelieveld(number);
                BigDecimal fransError = number.subtract(fransEstimation.multiply(fransEstimation)).abs();
                fransError = fransError.divide(number, mathContext);

                difference = difference.add(myError).subtract(fransError);
            }
            if (difference.compareTo(BigDecimal.ZERO) > 0) {
                worstDifference = difference;
                myBest = false;
            }
            System.out.println("My error - France's: " + difference.toPlainString());
            difference = BigDecimal.ZERO;
        }
        if (!myBest) {
            System.out.println("Sometimes France do better: " + worstDifference);
        }
    }


    @Test
    public void testEstimateIntSqrt() throws Exception {

        BigInteger number = new BigInteger("0", 2);
        BigInteger sqrt = SquareRoot.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("0", 2), sqrt);

        number = new BigInteger("1", 2);
        sqrt = SquareRoot.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("10", 2);
        sqrt = SquareRoot.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("11", 2);
        sqrt = SquareRoot.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("100", 2);
        sqrt = SquareRoot.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("10", 2), sqrt);

        number = new BigInteger("101", 2);
        sqrt = SquareRoot.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("10", 2), sqrt);

        number = new BigInteger("1001101", 2);
        sqrt = SquareRoot.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("1000", 2), sqrt);

        number = new BigInteger("11010101", 2);
        sqrt = SquareRoot.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("1000", 2), sqrt);

    }

    @Test
    public void testEstimateIntSqrtJScienceImpl() throws Exception {

        BigInteger number = new BigInteger("0", 2);
        BigInteger sqrt = SquareRoot.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("0", 2), sqrt);

        number = new BigInteger("1", 2);
        sqrt = SquareRoot.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("0", 2), sqrt);

        number = new BigInteger("10", 2);
        sqrt = SquareRoot.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("11", 2);
        sqrt = SquareRoot.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("100", 2);
        sqrt = SquareRoot.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("101", 2);
        sqrt = SquareRoot.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("1001101", 2);
        sqrt = SquareRoot.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("100", 2), sqrt);

        number = new BigInteger("1010101", 2);
        sqrt = SquareRoot.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("101", 2), sqrt);

        number = new BigInteger("11010101", 2);
        sqrt = SquareRoot.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("1101", 2), sqrt);

    }

    @Test
    public void testAllDec() throws Exception {

        List<BigDecimal> sqrts = BigDecimalGenerator.generateList(100, 100, 10);

        for (BigDecimal exactSqrt : sqrts) {

            BigDecimal number = exactSqrt.multiply(exactSqrt);

            MathContext mathContext = new MathContext(exactSqrt.precision() + 1, RoundingMode.HALF_UP);

            //BigDecimal sqrt = SquareRoot.babylonian(number, mathContext);
            //BigDecimal sqrtFrans = SquareRoot.bigSqrt(number, mathContext);
            BigDecimal sqrtCoupled = SquareRoot.coupledNewton(number, mathContext);

            //System.out.println(exactSqrt.toPlainString());
            //System.out.println(sqrt.toPlainString());
            //System.out.println(sqrtFrans.toPlainString());

            //if (exactSqrt.compareTo(sqrt) != 0) {
            //    System.out.println("sqrt error for: " + exactSqrt.toPlainString());
            //}
            //if (exactSqrt.compareTo(sqrtFrans) != 0) {
            //    System.out.println("sqrtFrans error for: " + exactSqrt.toPlainString());
            //}
            if (exactSqrt.compareTo(sqrtCoupled) != 0) {
                System.out.println("sqrtCoupled error for: " + exactSqrt.toPlainString());
            }

        }
    }

        @Test
    public void testBabylonianInt() throws Exception {

        BigInteger number;
        BigInteger sqrt;
        BigInteger sqrtSquared;
        BigInteger sqrtPlusSquared;

        number = new BigInteger("-1");
        try {
            sqrt = SquareRoot.babylonian(number);
            Assert.fail("ArithmeticException should be thrown by SquareRoot.babylonian(BigInteger) method for negative argument.");
        } catch (ArithmeticException ex) {
        }

        // test numbers 0, ..., 4000
        for (int i = 0; i < 4001; i++) {
            number = new BigInteger(new Integer(i).toString());
            testBabylonianIntegerNumber(number);
        }

        // do random test of big numbers with given number of digits (places)
        for (int places = 10; places < 200; places++) {
            for (int counter = 0; counter < 1000; counter++) {
                number = new BigInteger(BigDecimalGenerator.generateInteger(places));
                testBabylonianIntegerNumber(number);
            }
        }

        number = new BigInteger("64592137654307265023476527674650237456023746501374650234765034765023476503476503476502387465304765645192364528354192364592365492836754192364519236542939");
        sqrt = SquareRoot.babylonian(number);
        sqrtSquared = sqrt.multiply(sqrt);
        sqrtPlusSquared = sqrtSquared.add(sqrt.shiftLeft(1).add(BigInteger.ONE));
        Assert.assertTrue(number.compareTo(sqrtSquared) >= 0);
        Assert.assertTrue(number.compareTo(sqrtPlusSquared) < 0);
    }

    private static void testBabylonianIntegerNumber(BigInteger number) {

        BigInteger sqrt;
        BigInteger sqrtSquared;
        BigInteger sqrtPlusSquared;
        String msg1 = "SquareRoot.babylonian(BigInteger number) returns sqrt s.t.: sqrt**2 > number, number = ";
        String msg2 = "SquareRoot.babylonian(BigInteger number) returns sqrt s.t.: (sqrt + 1)**2 <= number, number = ";


        sqrt = SquareRoot.babylonian(number);

        sqrtSquared = sqrt.multiply(sqrt);
        Assert.assertTrue(msg1 + number, number.compareTo(sqrtSquared) >= 0);

        sqrtPlusSquared = sqrtSquared.add(sqrt.shiftLeft(1).add(BigInteger.ONE));
        Assert.assertTrue(msg2 + number, number.compareTo(sqrtPlusSquared) < 0);
    }

    @Test
    public void testCoupledNewtonDec() throws Exception {
        String str = "317688.513712171";
        //String str = "0.01";
        BigDecimal exactSqrt = new BigDecimal(str);
        BigDecimal number = exactSqrt.multiply(exactSqrt);
        System.out.println(str);
        System.out.println(BigDecimal.ONE.divide(SquareRoot.TWO.multiply(exactSqrt), new MathContext(200, RoundingMode.HALF_UP)).toPlainString());
        System.out.println("-----------------------------");

        MathContext mathContext = new MathContext(exactSqrt.precision() + 1, RoundingMode.HALF_UP);

        SquareRoot.coupledNewtonExploration(number, mathContext);
        //BigDecimal sqrtFrans = SquareRoot.bigSqrt(number, mathContext);


    }

    @Test
    public void testCoupledNewton() throws Exception {

        String[] sqrts =   {"0", "1", "2", "3", "4", "10", "13", "101", "237", "1346750",
                "23764501287354601287560824765018247650812476510876",
                "24765012384756103247563847560238745682034756083247650183746580134756083476510746513764510473456334576",
                "1.2", "3.74", "11.04506077", "0.00000000342001000000000012", "0.0000000000000000200000202000001",
                "0.00000000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000001",
                "3624591236459342937646.762547126450124765027465012476502476504756014756014765014765014576107456137454",
                "263525467216354736456346592843567829174563219.2634596246350489275642734659812435698254367208734663491"};

        for (int i = 0; i < sqrts.length; i++) {
            testSqureRoot(new BigDecimal(sqrts[i]));
        }

        for (int i = 0; i < 1000; i++) {
            Random generator = new Random();
            int integerPlaces = generator.nextInt(300);
            int fractionalPlaces = generator.nextInt(300);
            String sqrt = BigDecimalGenerator.generateDecimal(integerPlaces, fractionalPlaces);
            if (!sqrt.equals("")) {
                testSqureRoot(new BigDecimal(sqrt));
            }
        }
    }

    private static void testSqureRoot(BigDecimal sqrt) {
        BigDecimal number = sqrt.multiply(sqrt);
        int toleranceScale = number.scale() + 1;
        BigDecimal tolerance = new BigDecimal(new BigInteger("1"), toleranceScale);
        System.out.println(number.toPlainString());
        System.out.println(sqrt.multiply(sqrt).toPlainString());
        System.out.println("-----------------------------");
        Assert.assertTrue(number.subtract(sqrt.multiply(sqrt)).abs().compareTo(tolerance) < 0);
    }

    @Test
    public void testSpeed() {
        List<BigDecimal> decimals = BigDecimalGenerator.generateList();
        MathContext mathContext = new MathContext(20, RoundingMode.HALF_UP);

        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRoot.bigSqrt(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Frans: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRoot.babylonian(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Babylonian: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRoot.bigSqrt(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Frans: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRoot.babylonian(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Babylonian: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRoot.bigSqrt(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Frans: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRoot.babylonian(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Babylonian: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRoot.bigSqrt(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Frans: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRoot.babylonian(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Babylonian: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRoot.bigSqrt(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Frans: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRoot.babylonian(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Babylonian: " + (endTime - startTime) + " ms");

    }

}