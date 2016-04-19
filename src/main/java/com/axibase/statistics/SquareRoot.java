package com.axibase.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 */
public class SquareRoot {

    public static BigDecimal babylonian(BigDecimal number, MathContext sqrtContext) {

        int sqrtPrecision = sqrtContext.getPrecision();
        int numberPrecision = 2 * sqrtPrecision + 3;    // may be 2x + 5 ???

        BigDecimal roundedNumber = convert(number, numberPrecision);
        //ScaledInteger scaledInteger = convert(number, numberPrecision);

        BigInteger intSqrt = babylonian(roundedNumber.unscaledValue());

        BigDecimal result = new BigDecimal(intSqrt, roundedNumber.scale() / 2);

        return result.round(sqrtContext);
    }



    /**
     * The method returns an integer square root of a number.
     * So the method returns x, such that
     * x^2 <= number < (x + 1) ^ 2.
     * Calculation uses Babylonian (= Newton-Raphson) iteration method
     * as described in
     * <a href="https://en.wikipedia.org/wiki/Integer_square_root">Integer_square_root, Wikipedia </a>
     * The same method is implemented in
     * <a href="http://jscience.org/api/org/jscience/mathematics/number/LargeInteger.html#sqrt()">JScience library</a>
     */
    public static BigInteger babylonian(BigInteger number) {

        if (number.equals(BigInteger.ZERO)) {
            return number;
        }

        if (number.signum() == -1) {
            throw new ArithmeticException("Square root of a negative number: " + number);
        }

        // Get rough estimation of sqrt.
        BigInteger sqrt = estimateIntSqrt(number);

        while (true) {
            BigInteger newEstimation = sqrt.add(number.divide(sqrt)).shiftRight(1);
            if (newEstimation.subtract(sqrt).abs().compareTo(BigInteger.ONE) <= 0) {
                if (newEstimation.multiply(newEstimation).compareTo(number) <= 0) {
                    return newEstimation;
                }
                return newEstimation.subtract(BigInteger.ONE);
            }
//            if (newEstimation.equals(sqrt) || newEstimation.subtract(sqrt).equals(BigInteger.ONE)) {
//                if (newEstimation.multiply(newEstimation).compareTo(number) <= 0) {
//                    return newEstimation;
//                }
//                return sqrt;
//            }
            sqrt = newEstimation;
        }
    }

    /**
     * Returns rough estimation of an integer square root,
     * as suggested in
     * <a href = "https://en.wikipedia.org/wiki/Methods_of_computing_square_roots">Wikipedia</a>
     * for binary numbers.
     * If binary length of a number is 2n, the method returns 2**n.
     * If binary length of a number is 2n + 1, the method returns 2**(n + 1).
     */
    public static BigInteger estimateIntSqrt(BigInteger number) {
        int length = number.bitLength();
        int n = (length >> 1) + (length & 1);
        return BigInteger.ONE.shiftLeft(n - 1);
    }

    public static BigDecimal convert(String str, int figures) {
        return convert(new BigDecimal(str), figures);
    }

    /**
     * Let X be provided big decimal number,
     * and N be integer number.
     *
     * The method returns big decimal number Z
     * with its representation: Z = S * 10^(-2n).
     *
     * Number of digits in unscaled value S of Z equals
     * to N or N + 1.
     * Z is rounding of X with RoundingMode.HALF_UP
     * and precision N (or N + 1).
     */
    public static BigDecimal convert(BigDecimal number, int figures) {

        int decimalExp = number.scale();

        number = number.movePointRight(decimalExp);

        assert number.scale() == 0;

        int diff = figures - number.precision();
        if (diff >= 0) {
            number = number.movePointRight(diff);
            decimalExp += diff;
            if (decimalExp % 2 == 1) {
                number = number.movePointRight(1);
                decimalExp++;
            }
        } else {
            if ((decimalExp + diff) % 2 == 1) {
                figures++;
                diff++;
            }
            MathContext context = new MathContext(figures, RoundingMode.HALF_UP);
            number = number.round(context);
            number = number.movePointRight(diff);
            decimalExp += diff;
        }

        //return new ScaledInteger(number.unscaledValue(), decimalExp);
        return new BigDecimal(number.unscaledValue(), decimalExp);
    }


    /**
     * Container for BigDecimal.
     */
    static class ScaledInteger {
        BigInteger number;
        int scale;

        public ScaledInteger(BigInteger number, int scale) {
            this.number = number;
            this.scale = scale;
        }

        public BigInteger getNumber() {
            return number;
        }

        public int getScale() {
            return scale;
        }
    }







}
