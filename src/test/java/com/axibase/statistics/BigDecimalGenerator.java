package com.axibase.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * For testing, generates big decimal numbers.
 */
public class BigDecimalGenerator {

    private static final char[] decimalDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] nonzeroDigits = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static char generateDigit(Random generator) {
        return decimalDigits[generator.nextInt(10)];
    }

    public static char generateNonzeroDigit(Random generator) {
        return nonzeroDigits[generator.nextInt(9)];
    }

    /**
     * Generates a random positive integer number with exactly n decimal decimalDigits.
     * This integer can has a lot of decimalDigits so it can overflow java.Long type.
     * @param n The number of decimalDigits in the generated number.
     */
    public static char[] generateInteger(int n, Random generator) {
        if (n <= 0) {
            return new char[0];
        }
        char[] digits = new char[n];
        digits[0] = nonzeroDigits[generator.nextInt(9)];
        for (int i = 1; i < n; i++) {
            digits[i] = decimalDigits[generator.nextInt(10)];
        }
        return digits;
    }

    /**
     * Generates a random positive integer number with exactly n decimal decimalDigits,
     * and with non-zero last digit.
     * This integer can has a lot of decimalDigits so it can overflow java.Long type.
     * @param n The number of decimalDigits in the generated number.
     */
    public static char[] generateIntegerWithNonzeroLastDigit(int n, Random generator) {
        if (n <= 0) {
            return new char[0];
        }
        char[] digits = new char[n];
        int lastIndex = n - 1;
        digits[0] = nonzeroDigits[generator.nextInt(9)];
        for (int i = 1; i < lastIndex; i++) {
            digits[i] = decimalDigits[generator.nextInt(10)];
        }
        digits[lastIndex] = nonzeroDigits[generator.nextInt(9)];
        return digits;
    }

    /**
     * Generates a random positive decimal number with exactly n decimalDigits
     * before decimal point and m decimalDigits after.
     * This integer can has a lot of decimalDigits so it can overflow java.Double type.
     */
    public static String generateDecimal(int before, int after, Random generator) {
        if (before < 0 || after < 0) {
            return "";
        }
        if (before == 0 && after == 0) {
            return "0";
        }
        if (after == 0) {
            return new String(generateInteger(before, generator));
        }
        StringBuilder str = new StringBuilder(before + after + 2);
        if (before == 0) {
            str.append("0.");
            str.append(generateIntegerWithNonzeroLastDigit(after, generator));
            return str.toString();
        }
        str.append(generateInteger(before, generator));
        str.append(".");
        str.append(generateIntegerWithNonzeroLastDigit(after, generator));
        return str.toString();
    }

    /**
     * Generates a random positive decimal number with exactly n decimalDigits
     * before decimal point and m decimalDigits after.
     * This integer can has a lot of decimalDigits so it can overflow java.Double type.
     */
    public static String generateSignedDecimal(int before, int after, Random generator) {

        if (before < 0 || after < 0) {
            return "";
        }
        StringBuilder str = new StringBuilder(before + after + 3);
        if (generator.nextBoolean()) {
            str.append('-');
        }
        if (before == 0 && after == 0) {
            return str.append('0').toString();
        }
        if (after == 0) {
            return str.append(generateInteger(before, generator)).toString();
        }
        if (before == 0) {
            str.append("0.");
            str.append(generateIntegerWithNonzeroLastDigit(after, generator));
            return str.toString();
        }
        str.append(generateInteger(before, generator));
        str.append(".");
        str.append(generateIntegerWithNonzeroLastDigit(after, generator));
        return str.toString();
    }

    /**
     * Generates a random positive decimal number with no more then given limit
     * integer and fractional decimalDigits.
     */
    public static String generateDecimal(int limit) {
        if (limit < 1) {
            return "";
        }
        Random generator = new Random();
        int after = generator.nextInt(limit);
        int before = generator.nextInt(limit);
        return generateDecimal(before, after, generator);
    }

    public static void main(String[] args) {
        Random generator = new Random();
        System.out.println(generateDecimal(0, 0, generator));
        System.out.println(generateDecimal(1, 0, generator));
        System.out.println(generateDecimal(0, 1, generator));
        System.out.println(generateDecimal(1, 1, generator));
        System.out.println(generateDecimal(3, 4, generator));
        System.out.println(generateDecimal(30, 0, generator));
        System.out.println(generateDecimal(0, 20, generator));
        System.out.println(generateDecimal(17, 17, generator));
        System.out.println(generateDecimal(60));
    }

    public static List<BigDecimal> generateList() {
        return generateList(100, 100, 10);
    }

    public static List<BigDecimal> generateList(int integer, int fractional, int repeat) {
        Random generator = new Random();
        List<BigDecimal> decimals = new ArrayList<>(integer * fractional * repeat);
        for (int before = 0; before < integer; before++) {
            for (int after = 0; after < fractional; after++) {
                for (int counter = 0; counter < repeat; counter++) {
                    String str = BigDecimalGenerator.generateDecimal(before, after, generator);
                    if (!str.equals("")) {
                        decimals.add(new BigDecimal(str));
                    }
                }
            }
        }
        return decimals;
    }

    public static List<String> generateStringsList(int integer, int fractional, int repeat) {
        Random generator = new Random();
        List<String> strs = new ArrayList<>(integer * fractional * repeat);
        for (int before = 0; before < integer; before++) {
            for (int after = 0; after < fractional; after++) {
                for (int counter = 0; counter < repeat; counter++) {
                    String str = BigDecimalGenerator.generateDecimal(before, after, generator);
                    if (!str.equals("")) {
                        strs.add(str);
                    }
                }
            }
        }
        return strs;
    }

    public static List<String> generateUniformStr(int integer, int fractional, int length) {
        Random generator = new Random();
        List<String> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(generateDecimal(integer, fractional, generator));
        }
        return list;
    }

    public static List<String> generateRandomStr(int integer, int fractional, int length) {
        List<String> list = new ArrayList<>(length);
        Random generator = new Random();
        for (int i = 0; i < length; i++) {
            int before = generator.nextInt(integer);
            int after = generator.nextInt(fractional);
            list.add(generateSignedDecimal(before, after, generator));
        }
        return list;
    }

    public static BigDecimal[] toBigDecimal(List<String> numbers) {
        BigDecimal[] decimals = new BigDecimal[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            decimals[i] = new BigDecimal(numbers.get(i));
        }
        return decimals;
    }

    public static double[] toDouble(List<String> numbers) {
        double[] doubles = new double[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            doubles[i] = Double.parseDouble(numbers.get(i));
        }
        return doubles;
    }

}
