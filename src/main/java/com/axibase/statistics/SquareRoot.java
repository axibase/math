package com.axibase.statistics;

import java.math.*;
import java.util.ArrayList;

 /**
 * @author Frans Lelieveld, http://iteror.org/big/Retro/blog/sec/archive20070915_295396.html
 * @version Java 5, 28 September 2007
 */
public class SquareRoot {

     static final BigDecimal TWO = new BigDecimal(2);
     static final double SQRT_10 = 3.162277660168379332;

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
     public static BigDecimal bigSqrt(BigDecimal squarD, MathContext rootMC) {

         // General number and precision checking
         int sign = squarD.signum();
         if(sign == -1)
             throw new ArithmeticException("\nSquare root of a negative number: " + squarD);
         else if(sign == 0)
             return squarD.round(rootMC);

         int prec = rootMC.getPrecision();           // the requested precision = the number of significand figures
         if(prec == 0)
             throw new IllegalArgumentException("\nMost roots won't have infinite precision = 0");

         // TODO check here, because double uses 53 (not 63) bits for significand
         // Initial precision is that of double numbers 2^63/2 ~ 4E18
         int BITS = 62;                              // 63 - 1 an even number of number bits
         int nInit = 16;                             // precision seems 16 to 18 digits
         MathContext nMC = new MathContext(18, RoundingMode.HALF_DOWN); // set precision of nMC to 18 digits

         // Iteration variables, for the square root x and the reciprocal v
         BigDecimal x = null, e = null;              // initial x:  x0 ~ sqrt(squarD)
         BigDecimal v = null, g = null;              // initial v:  v0 = 1/(2*x)

         // Estimate the square root with the foremost 62 bits of squarD
         BigInteger bi = squarD.unscaledValue();     // bi and scale are a tandem
         int biLen = bi.bitLength();
         int shift = Math.max(0, biLen - BITS + (biLen % 2 == 0 ? 0 : 1));   // even shift..
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
             return x.round(rootMC);        // return small prec roots without iterations TODO check here

         // Initial v - the reciprocal
         v = BigDecimal.ONE.divide(TWO.multiply(x), nMC);        // v0 = 1/(2*x)


         // Collect iteration precisions beforehand
         ArrayList<Integer> nPrecs = new ArrayList<Integer>();

         assert nInit > 3 : "Never ending loop!";                // assume nInit = 16 <= prec

         // Let m be the exact digits precision in an earlier! loop
         for(int m = prec + 1; m > nInit; m = m/2 + (m > 100 ? 1 : 2))
             nPrecs.add(m);


         // The loop of "Square Root by Coupled Newton Iteration" for simpletons
         for(int i = nPrecs.size() - 1; i > -1; i--)
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
}
