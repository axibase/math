package com.axibase.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * The class has methods to calculate square roots of BigDecimal numbers
 * and integer square roots of BigInteger numbers.
 */
public class SquareRoot {

    /**
     * Let X be a BigDecimal number provided as the first argument of the method.
     * Let Z be the exact value of the square root of X.
     * The method returns rounded value of Z.
     * The rounding strategy is specified by the second method's argument.
     * If Z has decimal representation of finite length it is used for rounding.
     * For example,  the number 2 has an infinite decimal representation 1.99999999...,
     * but for rounding finite representation 2 will be used.
     * So for the context
     * <pre>
     *     context = new MathContext(1, RoundingMode.DOWN);
     * </pre>
     * the number babylonian(new BigDecimal("4"), context) will be 2.
     */
    public static BigDecimal babylonian(BigDecimal number, MathContext sqrtContext) {

        int numberPrecision;

        // Round number to drop excess digits.
        if (sqrtContext.getPrecision() == 0) {
            sqrtContext = new MathContext(0, RoundingMode.UNNECESSARY);
        }
        if (sqrtContext.getRoundingMode() == RoundingMode.UNNECESSARY) {
            numberPrecision = number.stripTrailingZeros().precision();
        } else {
            numberPrecision = 2 * sqrtContext.getPrecision() - 1;
        }
        BigDecimal roundedNumber = convert(number, numberPrecision);

        // Let exact square root of the number is exactSqrt.
        // Then:
        // sqrt <= exactSqrt <= sqrt + 1
        // To make correct rounding we should compare exactSqrt with sqrt, sqrt + 0.5 and sqrt + 1.
        BigInteger intSqrt = babylonian(roundedNumber.unscaledValue());
        BigDecimal sqrt = new BigDecimal(intSqrt, roundedNumber.scale() / 2);

        BigDecimal sqrtHalf = new BigDecimal(intSqrt.multiply(BigInteger.TEN).add(new BigInteger("5")), (roundedNumber.scale() / 2) + 1 );

        BigInteger intSqrtPlus = intSqrt.add(BigInteger.ONE);
        BigDecimal sqrtPlus = new BigDecimal(intSqrtPlus, roundedNumber.scale() / 2).round(sqrtContext);

        switch (sqrtContext.getRoundingMode()) {

            case UP:
            case CEILING:
                BigInteger squared = intSqrt.multiply(intSqrt);
                if (new BigDecimal(squared, roundedNumber.scale()).compareTo(number) == 0) {
                    return sqrt;
                }
                return sqrtPlus;

            case DOWN:
            case FLOOR:
                squared = intSqrtPlus.multiply(intSqrtPlus);
                if (new BigDecimal(squared, roundedNumber.scale()).compareTo(number) == 0) {
                    return sqrtPlus;
                }
                return sqrt;

            case HALF_UP:
                BigDecimal square = sqrtHalf.multiply(sqrtHalf);
                if (number.compareTo(square) < 0) {
                    return sqrt;
                }
                return sqrtPlus;

            case HALF_DOWN:
                square = sqrtHalf.multiply(sqrtHalf);
                if (number.compareTo(square) <= 0) {
                    return sqrt;
                }
                return sqrtPlus;

            case HALF_EVEN:
                square = sqrtHalf.multiply(sqrtHalf);
                switch (number.compareTo(square)) {
                    case -1:
                        return sqrt;
                    case 0:
                        if (intSqrt.getLowestSetBit() > 0) {
                            return sqrt;
                        }
                        return sqrtPlus;
                    case 1:
                        return sqrtPlus;
                }

            case UNNECESSARY:
                squared = intSqrt.multiply(intSqrt);
                if (new BigDecimal(squared, roundedNumber.scale()).compareTo(number) == 0) {
                    return sqrt;
                }
                squared = squared.add(new BigInteger("2").multiply(intSqrt)).add(BigInteger.ONE);
                if (new BigDecimal(squared, roundedNumber.scale()).compareTo(number) == 0) {
                    return sqrtPlus;
                }
                String msg = "The square root of the number has infinitely many digits, so finite precision and rounding mode are mandatory.";
                throw new ArithmeticException(msg);

            default:
                msg = "Unknown rounding mode: " + sqrtContext.getRoundingMode();
                throw new IllegalArgumentException(msg);
        }
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

    protected static BigDecimal convert(String str, int figures) {
        return convert(new BigDecimal(str), figures);
    }

    /**
     * Let X be a  big decimal number provided as the first argument,
     * and N be an integer number - the second argument.
     *
     * The method returns a big decimal number Z
     * with special representation: Z = S * 10^(-scale).
     * Z is rounding of X with RoundingMode.DOWN
     * and precision N or N + 1.
     * The precision is number of digits of unscaled value S
     * and it is chosen equal to N or to N + 1,
     * so that the scale is even.
     */
    protected static BigDecimal convert(BigDecimal number, int figures) {
        int scale = number.scale() + figures - number.precision();
        scale += (scale & 1);
        return number.setScale(scale, RoundingMode.DOWN);
    }
}
