package com.axibase.statistics;

import java.util.BitSet;

/**
 * copy-pasted from ts_compression project
 */
public class AxibaseDecimal implements Comparable<AxibaseDecimal> {

    private long significand;
    private int exponent;

    public AxibaseDecimal() {
    }

    public AxibaseDecimal(String str) {

        char[] chars = str.toCharArray();
        int lastIndex = chars.length - 1;

        int digitIndex = lastIndex;

        int sign = 1;

        for (int charIndex = lastIndex; charIndex >= 0; charIndex--) {

            char c = chars[charIndex];

            if ('1' <= c && c <= '9') {
                chars[digitIndex--] = c;
                continue;
            }

            if (c == '0') {
                if (digitIndex < lastIndex) {
                    chars[digitIndex--] = c;
                } else {
                    exponent++;
                }
                continue;
            }

            if (c == '.') {
                exponent = digitIndex - lastIndex;
                continue;
            }

            if (c == '-') {
                sign = -1;
            }


//            switch (c) {
//                case '1':
//                case '2':
//                case '3':
//                case '4':
//                case '5':
//                case '6':
//                case '7':
//                case '8':
//                case '9':
//                    chars[digitIndex--] = c;
//                    break;
//                case '0':
//                    if (digitIndex < lastIndex) {
//                        chars[digitIndex--] = c;
//                    } else {
//                        exponent++;
//                    }
//                    break;
//                case '.':
//                    exponent = digitIndex - lastIndex;
//                    break;
//                case '-':
//                    sign = -1;
//                    break;
//                default:
//                    throw new IllegalArgumentException();
//            }
        }

        if (lastIndex > digitIndex) {
            significand = sign * Long.valueOf(new String(chars, digitIndex + 1, lastIndex - digitIndex));
        } else {
            significand = 0L;
            exponent = 0;
        }
    }

    public void parse1(String str) {

        char[] chars = str.toCharArray();
        int lastIndex = chars.length - 1;

        int digitIndex = lastIndex;

        int sign = 1;

        for (int charIndex = lastIndex; charIndex >= 0; charIndex--) {

            char c = chars[charIndex];

            switch (c) {
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    chars[digitIndex--] = c;
                    break;
                case '0':
                    if (digitIndex < lastIndex) {
                        chars[digitIndex--] = c;
                    } else {
                        exponent++;
                    }
                    break;
                case '.':
                    exponent = digitIndex - lastIndex;
                    break;
                case '-':
                    sign = -1;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        if (lastIndex > digitIndex) {
            significand = sign * Long.valueOf(new String(chars, digitIndex + 1, lastIndex - digitIndex));
        } else {
            significand = 0L;
            exponent = 0;
        }
    }

//    public void parse2(String str) {
//
//        char[] chars = str.toCharArray();
//        int lastIndex = chars.length - 1;
//        char c;
//
//        while (lastIndex >= 0 && (c = chars[lastIndex--]) == '0') {
//            exponent++;
//        }
//
//        if ('1' <= c && c <= '9') {
//
//        }
//
//        if (c == '.') {
//
//        }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//        int digitIndex = lastIndex;
//        int pointIndex;
//
//        int sign = 1;
//
//        for (int charIndex = lastIndex; charIndex >= 0; charIndex--) {
//
//            char c = chars[charIndex];
//
//            switch (c) {
//                case '1':
//                case '2':
//                case '3':
//                case '4':
//                case '5':
//                case '6':
//                case '7':
//                case '8':
//                case '9':
//                    chars[digitIndex--] = c;
//                    break;
//                case '0':
//                    if (digitIndex < lastIndex) {
//                        chars[digitIndex--] = c;
//                    } else {
//                        exponent++;
//                    }
//                    break;
//                case '.':
//                    exponent = digitIndex - lastIndex;
//                    break;
//                case '-':
//                    sign = -1;
//                    break;
//                default:
//                    throw new IllegalArgumentException();
//            }
//        }
//
//        if (lastIndex > digitIndex) {
//            significand = sign * Long.valueOf(new String(chars, digitIndex + 1, lastIndex - digitIndex));
//        } else {
//            significand = 0L;
//            exponent = 0;
//        }
//    }

    /**
     * Convert string to AxibaseDecimal. Use dot character as delimiter: 23.45
     * @param str string for conversion.
     *            The method extracts significand and exponent from the string and produces normalized AxibaseDecimal.
     *            So significand (a long number) is chosen so that it is not divisible by 10.
     */
    public void parse(String str) {

        char[] chars = str.toCharArray();
        int lastIndex = chars.length - 1;

        int digitIndex = lastIndex;

        int sign = 1;
        exponent = 0;
        char c;

        for (int charIndex = lastIndex; charIndex >= 0; charIndex--) {

            c = chars[charIndex];
            switch (c) {
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    chars[digitIndex--] = c;
                    break;
                case '0':
                    if (digitIndex < lastIndex) {
                        chars[digitIndex--] = c;
                    } else {
                        exponent++;
                    }
                    break;
                case '.':
                    exponent = digitIndex - lastIndex;
                    break;
                case '-':
                    sign = -1;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        if (lastIndex > digitIndex) {
            significand = sign * Long.valueOf(new String(chars, digitIndex + 1, lastIndex - digitIndex));
        } else {
            significand = 0L;
            exponent = 0;
        }

//        // TODO check for an AxibaseDecimalRepresentationException
//        String[] splitted = str.split("\\.");
//        if (splitted.length == 1) {
//            significand = Long.parseLong(str);
//            exponent = 0;
//        } else {
//            significand = Long.parseLong(splitted[0] + splitted[1]);
//            exponent = -splitted[1].length();
//        }
//        normalize();
    }

    public AxibaseDecimal(long significant, int exponent) {
        this.significand = significant;
        this.exponent = exponent;
    }

    /**
     * Return true if provided object is an AxibaseDecimal object, and it represents the same decimal number as this.
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AxibaseDecimal)) { return false; }
        AxibaseDecimal that = (AxibaseDecimal) obj;
        AxibaseDecimal thisCopy = new AxibaseDecimal(this.significand, this.exponent);
        AxibaseDecimal thatCopy = new AxibaseDecimal(that.significand, that.exponent);
        thisCopy.normalize();
        thatCopy.normalize();
        return thisCopy.getSignificand() == thatCopy.getSignificand() && thisCopy.getExponent() == thatCopy.getExponent();
    }

    @Override
    public int hashCode() {
        AxibaseDecimal copy = new AxibaseDecimal(this.getSignificand(), this.getExponent());
        copy.normalize();
        return copy.toString().hashCode();
    }


    /**
     * Converts AxibaseDecimal to double.
     * Often it is impossible do such conversion, and result is wrong.
     */
    public double toDouble() {
        double value = significand;
        if (exponent >= 0) {
            for (int i = 0; i < exponent; i++) {
                value *= 10;
            }
        } else {
            for (int i = 0; i > exponent; i--) {
                value /= 10;
            }
        }
        return value;
    }

    /**
     * The method doesn't change this number, but change its representation.
     * It trims trailing zeroes of significand and changes exponent appropriately.
     */
    public void normalize() {
        if (significand == 0) {
            exponent = 0;
        } else {
            while (significand % 10 == 0) {
                significand = significand / 10;
                exponent++;
            }
        }
    }


    /**
     * Changes significand and exponent of this AxibaseDecimal to provided values.
     */
    public void change(long significand, int exponent) {
        this.significand = significand;
        this.exponent = exponent;
    }

    public void changeSignificand(long significand) {
        this.significand = significand;
    }

    public void changeExponent(int exponent) {
        this.exponent = exponent;
    }

    public BitSet toBits() {
        return BitSet.valueOf(new long[] {Double.doubleToRawLongBits(toDouble())});
    }

    public long getSignificand() {
        return significand;
    }

    public int getExponent() {
        return exponent;
    }

    @Override
    public int compareTo(AxibaseDecimal that) {

        if (this.exponent == that.exponent) {
            return compareLongs(this.significand, that.significand);
        }
        if (this.exponent < that.exponent) {
            return compare(this.significand, that.significand, that.exponent - this.exponent);
        }
        return -compare(that.significand, this.significand, this.exponent - that.exponent);
    }

    private int compare(long x, long ySignif, int yExp) {

        switch (Long.signum(ySignif)) {
            case -1:
                if (x >= 0 || yExp > 18 || ySignif < negativeBoundaries[yExp]) {
                    return 1;
                }
                break;
            case 0:
                return Long.signum(x);
            case 1:
                if (x <= 0 || yExp > 18 || ySignif > positiveBoundaries[yExp]) {
                    return -1;
                }
                break;
        }
        return compareLongs(x, ySignif * powersOfTen[yExp]);
    }

    private int compareLongs(long x, long y) {
        return (x < y) ? -1 : (x == y) ? 0 : 1;
    }

    /**
     * Return canonical string representation of given AxibaseDecimal.
     * Implementation is important because the method is used io create hash code.
     */
    @Override
    public String toString() {
        if (significand == 0) { return "0"; }
        String digits = String.valueOf(significand);
        if (exponent > 0) {
            digits += zeroes(exponent);
        }
        if (exponent < 0) {
            String signStr = significand < 0 ? "-" : "";
            if (significand < 0) {
                digits = digits.substring(1);
            }
            int pos = digits.length() + exponent;
            if (pos > 0) {
                digits = signStr + digits.substring(0, pos) + "." + digits.substring(pos, digits.length());
            } else {
                digits = signStr + "0." + zeroes(-pos) + digits;
            }
        }
        return digits;
    }

    /**
     * @param numOfZeroes number of zeroes in string
     * @return string of zeroes: "00...0"
     */
    private static String zeroes(int numOfZeroes) {
        String zeroes = "";
        for (int i = 0; i < numOfZeroes; i++) {
            zeroes = zeroes + "0";
        }
        return zeroes;
    }

    private static final long[] powersOfTen = new long[19];
    private static final long[] positiveBoundaries = new long[19];
    private static final long[] negativeBoundaries = new long[19];

    static {
        // initialize static arrays
        long powerOfTen = 1;
        for (int i = 0; i < 19; i++) {
            powersOfTen[i] = powerOfTen;
            positiveBoundaries[i] = Long.MAX_VALUE / powerOfTen;
            negativeBoundaries[i] = Long.MIN_VALUE / powerOfTen;
            powerOfTen *= 10;
        }
    }

}
