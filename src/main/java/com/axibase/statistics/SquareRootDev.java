package com.axibase.statistics;

import java.math.*;
import java.util.ArrayList;

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
 * 7. Sqrt as in jscience library.
 * http://jscience.org/api/org/jscience/mathematics/number/Real.html
 *
 * 8. Dutka 1971 - can't be used because we have no initial solution of the Pell's equation.
 *
 * *9. Ito 1971.
 *
 * *10. Digit by digit calculation in base 2 and 256 (1 byte) - view
 * *Wikipedia,
 * *http://web.archive.org/web/20120306040058/http://medialab.freaknet.org/martin/src/sqrt/sqrt.c,
 * *http://atoms.alife.co.uk/sqrt/SquareRoot.java,
 * *http://www.embedded.com/electronics-blogs/programmer-s-toolbox/4219659/Integer-Square-Roots
 */
public class SquareRootDev {

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

        if (number.compareTo(BigDecimal.ZERO) == 0) {
            return number;
        }

        if(mathContext.getPrecision() == 0) {
            throw new IllegalArgumentException("\nMost roots won't have infinite precision.");
        }

        int precision = mathContext.getPrecision() + 2;
        int currentPrecision = 16;
        MathContext currentContext = new MathContext(currentPrecision, RoundingMode.HALF_UP);
        // Get initial values to start iterations
        BigDecimal x = estimateSqrt(number);                                                   // estimate sqrt(number)
        BigDecimal y = BigDecimal.ONE.divide(TWO.multiply(x), currentContext);                 // = 1/(2x)
        BigDecimal newX;

        while(true) {
            currentContext = new MathContext(currentPrecision, RoundingMode.HALF_UP);

            BigDecimal discrepancy = number.subtract(x.multiply(x, currentContext), currentContext);
            newX = x.add(discrepancy.multiply(y, currentContext), currentContext);
            if ((currentPrecision >> 2) > precision) {
                return x.round(mathContext);
            } else {
                x = newX;
            }
            y = y.multiply(TWO).multiply(BigDecimal.ONE.subtract(x.multiply(y, currentContext)), currentContext);
            currentPrecision <<= 1;
        }
    }

    public static void coupledNewtonExploration(BigDecimal number, MathContext mathContext) {

        int precision = mathContext.getPrecision() + 2;
        int currentPrecision = 14;
        MathContext currentContext = new MathContext(currentPrecision, RoundingMode.HALF_UP);
        // Get initial values to start iterations
        BigDecimal x = estimateSqrt(number);                                                     // estimate sqrt(number)
        BigDecimal y = BigDecimal.ONE.divide(TWO.multiply(x), currentContext);                 // = 1/(2x)
        System.out.println(x.toPlainString());
        System.out.println(y.toPlainString());
        System.out.println("---------------------------------------------");

        BigDecimal newX;


        for (int i = 0; i < 6; i++) {
            currentContext = new MathContext(currentPrecision, RoundingMode.HALF_UP);
            BigDecimal discrepancy = number.subtract(x.multiply(x, currentContext), currentContext);
            x = x.add(discrepancy.multiply(y, currentContext), currentContext);
            y = y.multiply(TWO).multiply(BigDecimal.ONE.subtract(x.multiply(y, currentContext)), currentContext);
            currentPrecision *= 2;
            System.out.println(x.toPlainString());
            System.out.println(y.toPlainString());
            System.out.println("---------------------------------------------");
        }
    }

    /**
     * Returns big decimal sqrt s.t. sqrt**2 - number < tolerance
     */
    public static BigDecimal coupledNewton(BigDecimal number, BigDecimal tolerance) {

        BigDecimal x = estimateSqrt(number);
        MathContext mathContext = new MathContext(16, RoundingMode.HALF_UP);
        BigDecimal y = BigDecimal.ONE.divide(TWO.multiply(x), mathContext);
        BigDecimal discrepancy;
        do {
            discrepancy = x.multiply(x).subtract(number);
            x = x.subtract(discrepancy.multiply(y));
            y = y.multiply(TWO).subtract(y.multiply(x.multiply(y)));
        } while (discrepancy.abs().compareTo(tolerance) > 0);
        return x;
    }


    /**
     * Let 'sqrt' be exact square root of a number and 'approx'
     * is its approximation by this method.
     * Then round(sqrt - approx, mathContext) == 0.
     */
    public static BigDecimal babylonian(BigDecimal number, MathContext mathContext) {

        MathContext stricterMC= new MathContext(mathContext.getPrecision() + 2, mathContext.getRoundingMode());

        BigDecimal x = BigDecimal.ZERO;
        BigDecimal newX = estimateSqrt(number).round(stricterMC);

        while (x.compareTo(newX) != 0)  {
            x = newX;
            newX = number.divide(x, stricterMC).add(x).divide(TWO).round(stricterMC);
        }
        return x.round(mathContext);
    }

    public static BigDecimal babylonianByInteger(BigDecimal number, MathContext sqrtContext) {

        int sqrtPrecision = sqrtContext.getPrecision();
        int numberPrecition = 2 * sqrtPrecision + 3;    // may be 2x + 5 ???

        ScaledInteger scaledInteger = convert(number, numberPrecition);

        BigInteger intSqrt = babylonian(scaledInteger.getNumber());

        BigDecimal result = new BigDecimal(intSqrt, scaledInteger.getScale() / 2);

        return result.round(sqrtContext);
    }

    /**
     * Calculates big integer sqrt, such that
     * sqrt^2 <= number < (sqrt + 1) ^ 2.
     * Calculation uses Babylonian (= Newton-Raphson) iteration method.
     * This method is implemented in
     * org.jscience.mathematics.number.LargeInteger.sqrt().
     */
    public static BigInteger babylonian(BigInteger number) {

        if (number.equals(BigInteger.ZERO)) {
            return number;
        }

        if (number.signum() == -1) {
            throw new ArithmeticException("\nSquare root of a negative number: " + number);
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

//        if (number.signum() == -1) {
//            throw new ArithmeticException("\nSquare root of a negative number: " + number);
//        }

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

    /**
     * @author Frans Lelieveld
     * @version Java 5, 28 September 2007
     */
    /**
     * Returns the correctly rounded square root of a positive BigDecimal.
     * The algorithm for taking the square root of a BigDecimal is most
     * critical for the speed of your application. This method performs the fast
     * Square Root by Coupled Newton Iteration algorithm by Timm Ahrendt,
     * from the book "Pi, unleashed" by Jörg Arndt in a neat loop.
     *
     * @param squarD   number to get the root from (called "d" in the book)
     * @param rootMC   precision and rounding mode (for the last root "x")
     *
     * @return the root of the argument number
     *
     * @throws ArithmeticException       if the argument number is negative
     * @throws IllegalArgumentException  if rootMC has precision 0
     */

    public static BigDecimal bigSqrt(BigDecimal squarD, MathContext rootMC)
    {
        // Static constants - perhaps initialize in class Vladimir!
        // BigDecimal TWO = new BigDecimal(2);
        // double SQRT_10 = 3.162277660168379332;


        // General number and precision checking
        int sign = squarD.signum();
        if(sign == -1)
            throw new ArithmeticException("\nSquare root of a negative number: " + squarD);
        else if(sign == 0)
            return squarD.round(rootMC);

        int prec = rootMC.getPrecision();           // the requested precision
        if(prec == 0)
            throw new IllegalArgumentException("\nMost roots won't have infinite precision = 0");

        // Initial precision is that of double numbers 2^63/2 ~ 4E18
        int BITS = 62;                              // 63-1 an even number of number bits
        int nInit = 16;                             // precision seems 16 to 18 digits
        MathContext nMC = new MathContext(18, RoundingMode.HALF_DOWN);


        // Iteration variables, for the square root x and the reciprocal v
        BigDecimal x = null, e = null;              // initial x:  x0 ~ sqrt()
        BigDecimal v = null, g = null;              // initial v:  v0 = 1/(2*x)

        // Estimate the square root with the foremost 62 bits of squarD
        BigInteger bi = squarD.unscaledValue();     // bi and scale are a tandem
        int biLen = bi.bitLength();
        int shift = Math.max(0, biLen - BITS + (biLen%2 == 0 ? 0 : 1));   // even shift..
        bi = bi.shiftRight(shift);                  // ..floors to 62 or 63 bit BigInteger

        double root = Math.sqrt(bi.doubleValue());
        BigDecimal halfBack = new BigDecimal(BigInteger.ONE.shiftLeft(shift/2));

        int scale = squarD.scale();
        if(scale % 2 == 1)                          // add half scales of the root to odds..
            root *= SQRT_10;                          // 5 -> 2, -5 -> -3 need half a scale more..
        scale = (int)Math.floor(scale/2.);          // ..where 100 -> 10 shifts the scale

        // Initial x - use double root - multiply by halfBack to unshift - set new scale
        x = new BigDecimal(root, nMC);
        x = x.multiply(halfBack, nMC);                          // x0 ~ sqrt()
        if(scale != 0)
            x = x.movePointLeft(scale);

        if(prec < nInit)                 // for prec 15 root x0 must surely be OK
            return x.round(rootMC);        // return small prec roots without iterations

        // Initial v - the reciprocal
        v = BigDecimal.ONE.divide(TWO.multiply(x), nMC);        // v0 = 1/(2*x)


        // Collect iteration precisions beforehand
        ArrayList<Integer> nPrecs = new ArrayList<Integer>();

        assert nInit > 3 : "Never ending loop!";                // assume nInit = 16 <= prec

        // Let m be the exact digits precision in an earlier! loop
        for(int m = prec+1; m > nInit; m = m/2 + (m > 100 ? 1 : 2))
            nPrecs.add(m);


        // The loop of "Square Root by Coupled Newton Iteration" for simpletons
        for(int i = nPrecs.size()-1; i > -1; i--)
        {
            // Increase precision - next iteration supplies n exact digits
            nMC = new MathContext(nPrecs.get(i), (i%2 == 1) ? RoundingMode.HALF_UP :
                    RoundingMode.HALF_DOWN);

            // Next x                                                 // e = d - x^2
            e = squarD.subtract(x.multiply(x, nMC), nMC);
            if(i != 0)
                x = x.add(e.multiply(v, nMC));                          // x += e*v     ~ sqrt()
            else
            {
                x = x.add(e.multiply(v, rootMC), rootMC);               // root x is ready!
                break;
            }

            // Next v                                                 // g = 1 - 2*x*v
            g = BigDecimal.ONE.subtract(TWO.multiply(x).multiply(v, nMC));

            v = v.add(g.multiply(v, nMC));                            // v += g*v     ~ 1/2/sqrt()
        }

        return x;                        // return sqrt(squarD) with precision of rootMC
    }

    public static ScaledInteger convert(String str, int figures) {
        return convert(new BigDecimal(str), figures);
    }

    public static ScaledInteger convert(BigDecimal number, int figures) {

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
            if (((decimalExp + diff) & 1) == 1) {
                figures++;
                diff++;
            }
            MathContext context = new MathContext(figures, RoundingMode.HALF_UP);
            number = number.round(context);
            number = number.movePointRight(diff);
            decimalExp += diff;
        }

        return new ScaledInteger(number.unscaledValue(), decimalExp);
    }

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
