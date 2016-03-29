package com.axibase.statistics;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.axibase.statistics.SquareRoot.doubleApproximationOriginal;
import static com.axibase.statistics.SquareRoot.estimateSqrt;
import static org.junit.Assert.*;

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

            BigDecimal fransEstimation = doubleApproximationOriginal(decimals[i]);
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

                BigDecimal fransEstimation = doubleApproximationOriginal(number);
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



}