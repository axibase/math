package com.axibase.statistics;

import java.math.*;

/**
 * Several algorithms of square root for BigDecimal numbers are implemented.
 * *1. Coupled Newton Iteration (for decimals)
 * *(Frans Lelieveld, http://iteror.org/big/Retro/blog/sec/archive20070915_295396.html)
 *
 * *2. Halley's
 * *(http://www.mathpath.org/Algor/squareroot/algor.square.root.halley.htm)
 *
 * 3. Babylonian method (for integers and decimals)
 * (Wiki, http://www.codeproject.com/Articles/69941/Best-Square-Root-Method-Algorithm-Function-Precisi)
 *
 * *4. Reciprocal square root (for decimals)
 * *(http://drops.dagstuhl.de/opus/volltexte/2008/1435/pdf/08021.ZimmermannPaul.ExtAbstract.1435.pdf)
 *
 * *5. Karatsuba Square Root, this algorithm is used in GMP library
 * *(https://hal.inria.fr/inria-00072854/document)
 *
 * *6. Only subtractions
 * *(http://www.afjarvis.staff.shef.ac.uk/maths/jarvisspec02.pdf)
 *
 * *7. Sqrt as in jscience library.
 * *http://jscience.org/api/org/jscience/mathematics/number/Real.html
 *
 * *8. Dutka 1971 - can't be used because we have no initial solution of the Pell's equation.
 *
 * *9. Ito 1971.
 *
 * *10. Digit by digit calculation in base 2 and 256 (1 byte) - view
 * *Wikipedia,
 * *http://web.archive.org/web/20120306040058/http://medialab.freaknet.org/martin/src/sqrt/sqrt.c,
 * *http://atoms.alife.co.uk/sqrt/SquareRoot.java,
 * *http://www.embedded.com/electronics-blogs/programmer-s-toolbox/4219659/Integer-Square-Roots
 */
public class SquareRoot {

    // TODO first approximation:
    // 1. with double sqrt
    // 2. fast approximation from Wikipedia
    // 3. with Quake trick - look in Lomont fast_InvSqrt paper
    // and more in http://www.phailed.me/2014/10/0x5f400000-understanding-fast-inverse-sqrt-the-easyish-way/
    // 4. Half of binary length.

    static final BigDecimal TWO = new BigDecimal("2");
    static final double SQRT_10 = 3.162277660168379332d;


    /**
     * This method implements Coupled Newton Iteration algorithm by Arnold Schonhage,
     * from the book "Pi, unleashed" by Jörg Arndt.
     *
     * @param number        number to get the root from
     * @param mathContext   precision and rounding mode
     *
     * @return the root of the argument number
     *
     * @throws ArithmeticException       if the argument number is negative
     * @throws IllegalArgumentException  if mathContext has precision 0
     */
    public static BigDecimal coupledNewton(BigDecimal number, MathContext mathContext) {

        int precision = mathContext.getPrecision();           // the requested precision = the number of significand figures
        if(precision == 0) {
            throw new IllegalArgumentException("\nMost roots won't have infinite precision.");
        }

        // Get initial values to start iterations x -> sqrt(number), y -> 1/(2sqrt(number))
        BigDecimal x = estimateSqrt(number);                                 // an estimation of sqrt(number)
        BigDecimal y = BigDecimal.ONE.divide(TWO.multiply(x), mathContext);  // = 1/(2x)

        do {
            BigDecimal discrepancy = number.subtract(x.multiply(x));
            x = x.add(discrepancy.multiply(y));
            y = y.multiply(TWO).subtract(y.multiply(x.multiply(y)));
        } while (false);
        return x;
    }

    /**
     * Returns big decimal sqrt s.t. sqrt**2 - number < tolerance
     */
    public static BigDecimal coupledNewton(BigDecimal number, BigDecimal tolerance) {

        // Get initial values to start iterations x -> sqrt(number), y -> 1/(2sqrt(number))
        BigDecimal x = estimateSqrt(number);                         // an estimation of sqrt(number)
        MathContext mathContext = new MathContext(1, RoundingMode.HALF_UP);
        BigDecimal y = BigDecimal.ONE.divide(TWO.multiply(x), mathContext);       // = 1/(2x)
        BigDecimal discrepancy;

        do {
            discrepancy = x.multiply(x).subtract(number);
            x = x.subtract(discrepancy.multiply(y));
            y = y.multiply(TWO).subtract(y.multiply(x.multiply(y)));
        } while (discrepancy.abs().compareTo(tolerance) > 0);
        return x;
    }


    public static BigDecimal babylonian(BigDecimal number) {
        BigDecimal  x = estimateSqrt(number);
        return null;
    }


    /**
     * Calculates big integer sqrt, such that
     * sqrt^2 <= number < (sqrt + 1) ^ 2.
     * Calculation uses Babylonian (= Newton-Raphson) iteration method.
     * This method is implemented in
     * org.jscience.mathematics.number.LargeInteger.sqrt().
     */
    public static BigInteger babylonian(BigInteger number) {

        if (number.signum() == -1) {
            throw new ArithmeticException("\nSquare root of a negative number: " + number);
        }

        if (number.equals(BigInteger.ZERO)) {
            return number;
        }

        // Get rough estimation of sqrt.
        BigInteger sqrt = estimateIntSqrt(number);

        while (true) {
            BigInteger newEstimation = sqrt.add(number.divide(sqrt)).shiftRight(1);
            if (newEstimation.equals(sqrt) || newEstimation.subtract(sqrt).equals(BigInteger.ONE)) {
                if (newEstimation.multiply(newEstimation).compareTo(number) <= 0) {
                    return newEstimation;
                }
                return sqrt;
            }
            sqrt = newEstimation;
        }
    }


    /**
     * Returns rough estimation of an big integer square root,
     * as suggested in Wikipedia
     * https://en.wikipedia.org/wiki/Methods_of_computing_square_roots
     * for binary numbers.
     * If binary length of number is 2n, the method returns 2**n.
     * If binary length of number is 2n + 1, the method returns 2**(n + 1).
     */
    public static BigInteger estimateIntSqrt(BigInteger number) {
        int length = number.bitLength();
        int n = (length >> 1) + (length & 1);
        return BigInteger.ONE.shiftLeft(n - 1);
    }

    /**
     * Returns rough estimation of an big integer square root,
     * as it is implemented in
     * org.jscience.mathematics.number.LargeInteger.sqrt().
     */
    public static BigInteger estimateIntSqrtJScienceImpl(BigInteger number) {
        int length = number.bitLength();
        int n = (length >> 1) + (length & 1);
        return number.shiftRight(n);
    }

    /**
     * Returns estimation of sqrt of provided number with use of the Math.sqrt(double) function.
     * It is my own.
     */
    protected static BigDecimal estimateSqrt(BigDecimal number) {

        if (number.signum() == -1) {
            throw new ArithmeticException("\nSquare root of a negative number: " + number);
        }

        // use number of integer digits of provided big decimal as shift, and make it even
        int shift = number.precision() - number.scale();
        shift -= 15;
        shift += (shift % 2 == 0) ? 0 : 1;
        //shift = 0;

        double shiftedDoubleEstimation = number.movePointLeft(shift).doubleValue();
        // TODO explore if math context can be useful here
        BigDecimal shiftedSqrtEstimation = new BigDecimal(Math.sqrt(shiftedDoubleEstimation));

        return shiftedSqrtEstimation.movePointRight(shift / 2);
    }

    /**
     * Returns estimation of sqrt of provided number.
     * Frans Lelieveld, http://iteror.org/big/Retro/blog/sec/archive20070915_295396.html.
     */
    protected static BigDecimal estimateSqrtLelieveld(BigDecimal squarD) {

        // Initial precision is that of double numbers 2^63/2 ~ 4E18
        int BITS = 62; // 63-1 an even number of number bits
        int nInit = 16; // precision seems 16 to 18 digits
        MathContext nMC = new MathContext(18, RoundingMode.HALF_UP);

        // Iteration variables, for the square root x and the reciprocal v
        BigDecimal x = null, e = null; // initial x: x0 ~ sqrt()
        BigDecimal v = null, g = null; // initial v: v0 = 1/(2*x)

        // Estimate the square root with the foremost 62 bits of squarD
        BigInteger bi = squarD.unscaledValue(); // bi and scale are a tandem
        int biLen = bi.bitLength();
        int shift = Math.max(0, biLen - BITS + (biLen % 2 == 0 ? 0 : 1)); // even
        // shift..
        bi = bi.shiftRight(shift); // ..floors to 62 or 63 bit BigInteger

        double root = Math.sqrt(bi.doubleValue());
        BigDecimal halfBack = new BigDecimal(
                BigInteger.ONE.shiftLeft(shift / 2));

        int scale = squarD.scale();
        if (scale % 2 == 1) // add half scales of the root to odds..
            root *= SQRT_10; // 5 -> 2, -5 -> -3 need half a scale more..
        scale = (int) Math.floor(scale / 2.); // ..where 100 -> 10 shifts the
        // scale

        // Initial x - use double root - multiply by halfBack to unshift - set
        // new scale
        x = new BigDecimal(root, nMC);
        x = x.multiply(halfBack, nMC); // x0 ~ sqrt()
        if (scale != 0)
            x = x.movePointLeft(scale);

        return x;
    }


    protected static int estimateAccuracy(BigDecimal number, BigDecimal sqrtApproximatioin, RoundingMode rounding) {
        return 0;
    }

}
