package com.axibase.statistics;

import java.math.*;
import java.util.ArrayList;

/**
 * Several algorithms of square root for BigDecimal numbers are implemented.
 * 1. Coupled Newton Iteration
 * (Frans Lelieveld, http://iteror.org/big/Retro/blog/sec/archive20070915_295396.html)
 * 2. Halley's
 * (http://www.mathpath.org/Algor/squareroot/algor.square.root.halley.htm)
 * 3. Babilonian method
 * (Wiki, http://www.codeproject.com/Articles/69941/Best-Square-Root-Method-Algorithm-Function-Precisi)
 * 4. Reciprocal square root
 * (http://drops.dagstuhl.de/opus/volltexte/2008/1435/pdf/08021.ZimmermannPaul.ExtAbstract.1435.pdf)
 * 5. Karatsuba Square Root
 * (https://hal.inria.fr/inria-00072854/document)
 * 6. Only substractions
 * (http://www.afjarvis.staff.shef.ac.uk/maths/jarvisspec02.pdf)
 * 7. Use Math.sqrt for double recursively.
 */
public class SquareRoot {

    // TODO first approximation:
    // 1. with double sqrt
    // 2.

    static final BigDecimal TWO = new BigDecimal("2");
    static final BigInteger TEN = new BigInteger("10");
    static final double SQRT_10 = 3.162277660168379332;


    /**
     * This method performs the fast Square Root by Coupled Newton Iteration
     * algorithm by Arnold Schonhage,
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

        BigDecimal x = estimateSqrt(number);                                 // an estimation of sqrt(number)
        BigDecimal v = BigDecimal.ONE.divide(TWO.multiply(x), mathContext);  // ~ 1/(2x)





        // TODO check here, because double uses 53 (not 63) bits for significand
        // Initial precision is that of double numbers 2^63/2 ~ 4E18
        int BITS = 62;                              // 63 - 1 an even number of number bits
        int nInit = 16;                             // precision seems 16 to 18 digits
        MathContext nMC = new MathContext(18, RoundingMode.HALF_DOWN); // set precision of nMC to 18 digits

        // Iteration variables, for the square root x and the reciprocal v
        BigDecimal x = null, e = null;              // initial x:  x0 ~ sqrt(squarD)
        BigDecimal v = null, g = null;              // initial v:  v0 = 1/(2*x)

        // Estimate the square root with the foremost 62 bits of squarD
        BigInteger bi = number.unscaledValue();     // bi and scale are a tandem
        int biLen = bi.bitLength();
        int shift = Math.max(0, biLen - BITS + (biLen % 2 == 0 ? 0 : 1));   // even shift..
        bi = bi.shiftRight(shift);                  // ..floors to 62 or 63 bit BigInteger

        double root = Math.sqrt(bi.doubleValue());
        BigDecimal halfBack = new BigDecimal(BigInteger.ONE.shiftLeft(shift/2));

        int scale = number.scale();
        if(scale % 2 == 1)                          // add half scales of the root to odds..
            root *= SQRT_10;                          // 5 -> 2, -5 -> -3 need half a scale more..
        scale = (int)Math.floor(scale/2.);          // ..where 100 -> 10 shifts the scale

        // Initial x - use double root - multiply by halfBack to unshift - set new scale
        x = new BigDecimal(root, nMC);
        x = x.multiply(halfBack, nMC);                          // x0 ~ sqrt()
        if(scale != 0)
            x = x.movePointLeft(scale);

        if(precision < nInit)                 // for prec 15 root x0 must surely be OK
            return x.round(mathContext);        // return small prec roots without iterations TODO check here

        // Initial v - the reciprocal
        v = BigDecimal.ONE.divide(TWO.multiply(x), nMC);        // v0 = 1/(2*x)


        // Collect iteration precisions beforehand
        ArrayList<Integer> nPrecs = new ArrayList<Integer>();

        assert nInit > 3 : "Never ending loop!";                // assume nInit = 16 <= prec

        // Let m be the exact digits precision in an earlier! loop
        for(int m = precision + 1; m > nInit; m = m/2 + (m > 100 ? 1 : 2))
            nPrecs.add(m);


        // The loop of "Square Root by Coupled Newton Iteration" for simpletons
        for(int i = nPrecs.size() - 1; i > -1; i--)
        {
            // Increase precision - next iteration supplies n exact digits
            nMC = new MathContext(nPrecs.get(i), (i%2 == 1) ? RoundingMode.HALF_UP :
                    RoundingMode.HALF_DOWN);

            // Next x                                                 // e = d - x^2
            e = number.subtract(x.multiply(x, nMC), nMC);
            if(i != 0)
                x = x.add(e.multiply(v, nMC));                          // x += e*v     ~ sqrt()
            else
            {
                x = x.add(e.multiply(v, mathContext), mathContext);               // root x is ready!
                break;
            }

            // Next v                                                 // g = 1 - 2*x*v
            g = BigDecimal.ONE.subtract(TWO.multiply(x).multiply(v, nMC));

            v = v.add(g.multiply(v, nMC));                            // v += g*v     ~ 1/2/sqrt()
        }

        return x;                        // return sqrt(squarD) with precision of mathContext
    }

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
        BigDecimal shiftedSqrtEstimation = new BigDecimal(Math.sqrt(shiftedDoubleEstimation));

        return shiftedSqrtEstimation.movePointRight(shift / 2);
    }

    /**
     * Original code from
     * https://github.com/rizar/matrix/blob/master/src/com/github/rizar/bigdecimalmath/BigDecimalMath.java
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

}
