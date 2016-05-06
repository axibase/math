package com.axibase.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Generates big decimal numbers.
 */
public class BigDecimalGenerator {

    public static String generateDigit() {
        Random generator = new Random();
        return String.valueOf(generator.nextInt(10));
    }

    public static String generateNonzeroDigit() {
        Random generator = new Random();
        return String.valueOf(generator.nextInt(9) + 1);
    }

    /**
     * Generates a random positive integer number with exactly n decimal digits.
     * This integer can has a lot of digits so it can overflow java.Long type.
     * @param n The number of digits in the generated number.
     */
    public static String generateInteger(int n) {
        if (n <= 0) {
            return "";
        }
        Random generator = new Random();
        StringBuilder str = new StringBuilder(n);
        str.append(generator.nextInt(9) + 1);
        for (int i = 1; i < n; i++) {
            str.append(generator.nextInt(10));
        }
        return str.toString();
    }

    /**
     * Generates a random positive integer number with exactly n decimal digits,
     * and with non-zero last digit.
     * This integer can has a lot of digits so it can overflow java.Long type.
     * @param n The number of digits in the generated number.
     */
    public static String generateIntegerWithNonzeroLastDigit(int n) {
        if (n <= 0) {
            return "";
        }
        Random generator = new Random();
        StringBuilder str = new StringBuilder(n);
        str.append(generator.nextInt(9) + 1);
        for (int i = 1; i < n - 1; i++) {
            str.append(generator.nextInt(10));
        }
        if (n > 1) {
            str.append(generator.nextInt(9) + 1);
        }
        return str.toString();
    }

    /**
     * Generates a random positive decimal number with exactly n digits
     * before decimal point and m digits after.
     * This integer can has a lot of digits so it can overflow java.Double type.
     */
    public static String generateDecimal(int before, int after) {
        if (before < 0 || after < 0) {
            return "";
        }
        if (before == 0 && after == 0) {
            return "0";
        }
        StringBuilder str = new StringBuilder(before + after + 2);
        if (after == 0) {
            return generateInteger(before);
        }
        if (before == 0) {
            str.append("0.");
            str.append(generateIntegerWithNonzeroLastDigit(after));
            return str.toString();
        }
        str.append(generateInteger(before));
        str.append(".");
        str.append(generateIntegerWithNonzeroLastDigit(after));
        return str.toString();
    }

    /**
     * Generates a random positive decimal number with exactly n digits
     * before decimal point and m digits after.
     * This integer can has a lot of digits so it can overflow java.Double type.
     */
    public static String generateDecimal(int limit) {
        if (limit < 1) {
            return "";
        }
        Random generator = new Random();
        int after = generator.nextInt(limit);
        int before = generator.nextInt(limit);
        return generateDecimal(before, after);
    }

    public static void main(String[] args) {
        System.out.println(generateDecimal(0, 0));
        System.out.println(generateDecimal(1, 0));
        System.out.println(generateDecimal(0, 1));
        System.out.println(generateDecimal(1, 1));
        System.out.println(generateDecimal(3, 4));
        System.out.println(generateDecimal(30, 0));
        System.out.println(generateDecimal(0, 20));
        System.out.println(generateDecimal(17, 17));
        System.out.println(generateDecimal(60));
    }

    public static List<BigDecimal> generateList() {
        return generateList(100, 100, 10);
    }

    public static List<BigDecimal> generateList(int integer, int fractional, int repeat) {
        List<BigDecimal> decimals = new ArrayList<>(integer * fractional * repeat);
        for (int before = 0; before < integer; before++) {
            for (int after = 0; after < fractional; after++) {
                for (int counter = 0; counter < repeat; counter++) {
                    String str = BigDecimalGenerator.generateDecimal(before, after);
                    if (!str.equals("")) {
                        decimals.add(new BigDecimal(str));
                    }
                }
            }
        }
        return decimals;
    }

    public static List<String> generateStringsList(int integer, int fractional, int repeat) {
        List<String> strs = new ArrayList<>(integer * fractional * repeat);
        for (int before = 0; before < integer; before++) {
            for (int after = 0; after < fractional; after++) {
                for (int counter = 0; counter < repeat; counter++) {
                    String str = BigDecimalGenerator.generateDecimal(before, after);
                    if (!str.equals("")) {
                        strs.add(str);
                    }
                }
            }
        }
        return strs;
    }

    public static List<String> generateUniformStr(int integer, int fractional, int length) {
        List<String> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(generateDecimal(integer, fractional));
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

    public static AxibaseDecimal[] toAxibaseDecimal(List<String> numbers) {
        AxibaseDecimal[] decimals = new AxibaseDecimal[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            decimals[i] = new AxibaseDecimal(numbers.get(i));
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
