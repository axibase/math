package com.axibase.statistics;

import java.math.*;
import java.util.ArrayList;

/**
 * Several algorithms of square root for BigDecimal numbers are implemented.

 * 1. Coupled Newton Iteration
 * (Frans Lelieveld, http://iteror.org/big/Retro/blog/sec/archive20070915_295396.html)

 * 2. Halley's
 * (http://www.mathpath.org/Algor/squareroot/algor.square.root.halley.htm)
 *
 * 3. Babylonian method
 * (Wiki, http://www.codeproject.com/Articles/69941/Best-Square-Root-Method-Algorithm-Function-Precisi)
 *
 * 4. Reciprocal square root
 * (http://drops.dagstuhl.de/opus/volltexte/2008/1435/pdf/08021.ZimmermannPaul.ExtAbstract.1435.pdf)
 * 5. Karatsuba Square Root, this algorithm is used in GMP library
 * (https://hal.inria.fr/inria-00072854/document)
 * 6. Only subtractions (view K.P.Kohas paper in the Quant magazine)
 * (http://www.afjarvis.staff.shef.ac.uk/maths/jarvisspec02.pdf)
 * 7. Use Math.sqrt for double recursively.
 * 8. Exact sqrt (then it is possible).
 * 9. Sqrt as in jscience library.
 * http://jscience.org/api/org/jscience/mathematics/number/Real.html
 * 10. Dutka 1971.
 * 11. Ito 1971.
 */
public class SquareRoot {

    // TODO first approximation:
    // 1. with double sqrt
    // 2. fast approximation from Wikipedia
    // 3. with Quake trick - look in Lomont fast_InvSqrt paper
    // and more in http://www.phailed.me/2014/10/0x5f400000-understanding-fast-inverse-sqrt-the-easyish-way/

    static final BigDecimal TWO = new BigDecimal("2");
    static final double SQRT_10 = 3.162277660168379332d;


    /**
     * This method performs the fast Square Root by Coupled Newton Iteration
     * algorithm by Arnold Schonhage, from the book "Pi, unleashed" by Jörg Arndt.
     * //TODO Link in the book is incorrect. Try to find correct one!
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
        BigDecimal y = BigDecimal.ONE.divide(TWO.multiply(x), mathContext);  // ~ 1/(2x)
        return x;
    }

    public static BigDecimal babylonian(BigDecimal number) {
        BigDecimal  x = estimateSqrt(number);
        return null;
    }


    /**
     * Calculates big integer sqrt, such that
     * sqrt^2 <= number < (sqrt + 1) ^ 2
     */
    public static BigInteger babylonian(BigInteger number) {

        return null;
    }



    /**
     * Returns estimation of sqrt of provided number with use of the Math.sqrt(double) function.
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
     * Frans Lelieveld, http://iteror.org/big/Retro/blog/sec/archive20070915_295396.html.
     * Returns estimation of sqrt of provided number.
     */
    protected static BigDecimal doubleApproximationOriginal(BigDecimal squarD) {

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
