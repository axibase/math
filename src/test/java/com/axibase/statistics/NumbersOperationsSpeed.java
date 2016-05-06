package com.axibase.statistics;

import java.math.BigDecimal;
import java.util.List;

/**
 * test
 */
public class NumbersOperationsSpeed {

    long doubleMinTime = 0;
    long doubleSumTime = 0;
    long decimalMinTime = 0;
    long decimalSumTime = 0;
    long axiMinTime = 0;

    public static void main(String[] args) {
        NumbersOperationsSpeed tester = new NumbersOperationsSpeed();
        tester.testMinSpeed(0, 3, 10000000);
        //tester.testSpeed(3, 2, 10000000);
    }

    private void testMinSpeed(int integerDigits, int fractionalDigits, int length) {

        long start;
        System.out.format("Speed test for %d numbers. Each number has %d digits before the decimal point and %d fractional digits %n",
                length, integerDigits, fractionalDigits);

        List<String> numbers = BigDecimalGenerator.generateUniformStr(integerDigits, fractionalDigits, length);

        start = System.currentTimeMillis();
        AxibaseDecimal[] axiDecimals = BigDecimalGenerator.toAxibaseDecimal(numbers);
        System.out.println("AxibaseDecimals creation: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        BigDecimal[] decimals = BigDecimalGenerator.toBigDecimal(numbers);
        System.out.println("BigDecimals creation: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        double[] doubles = BigDecimalGenerator.toDouble(numbers);
        System.out.println("Doubles creation: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        Double minDouble = min(doubles);
        doubleMinTime = System.currentTimeMillis() - start;
        System.out.println(minDouble);

        start = System.currentTimeMillis();
        AxibaseDecimal minAxi = min(axiDecimals);
        axiMinTime = System.currentTimeMillis() - start;
        System.out.println(minAxi);

        start = System.currentTimeMillis();
        BigDecimal minDecimal = min(decimals);
        decimalMinTime = System.currentTimeMillis() - start;
        System.out.println(minDecimal);

        System.out.println("");
        System.out.format("BD/double: %.2f%n", (double) decimalMinTime / doubleMinTime);
        System.out.format("AD/double: %.2f%n", (double) axiMinTime / doubleMinTime);
    }

    private void testSpeed(int integerDigits, int fractionalDigits, int length) {

        System.out.format("Speed test for %d numbers. Each number has %d digits before the decimal point and %d fractional digits %n",
                length, integerDigits, fractionalDigits);

        // run test 10 times for different arrays
        for (int i = 0; i < 10; i++) {

            System.out.format("Iteration %d%n", i);
            List<String> numbers = BigDecimalGenerator.generateUniformStr(integerDigits, fractionalDigits, length);
            BigDecimal[] decimals = BigDecimalGenerator.toBigDecimal(numbers);
            double[] doubles = BigDecimalGenerator.toDouble(numbers);
            System.out.println("Numbers are generated.");

//            for (int j = 0; j < 3; j++) {
//                BigDecimal minDecimal = min(decimals);
//                BigDecimal sumDecimal = sum(decimals);
//                Double minDouble = min(doubles);
//                Double sumDouble = sum(doubles);
//                decimals[0] = decimals[0].add(sumDecimal).add(minDecimal);
//                doubles[0] += minDouble;
//            }

            if ((i & 1) == 0) {
                long start = System.currentTimeMillis();
                BigDecimal minDecimal = min(decimals);
                long finish = System.currentTimeMillis();
                decimalMinTime += (finish - start);
                System.out.println(minDecimal);
                decimals[0] = decimals[0].add(minDecimal);

                start = System.currentTimeMillis();
                Double minDouble = min(doubles);
                finish = System.currentTimeMillis();
                doubleMinTime += (finish - start);
                System.out.println(minDouble);
                doubles[0] += minDouble;

                start = System.currentTimeMillis();
                BigDecimal sumDecimal = sum(decimals);
                finish = System.currentTimeMillis();
                decimalSumTime += (finish - start);
                System.out.println(sumDecimal);
                decimals[0] = decimals[0].add(BigDecimal.ONE);

                start = System.currentTimeMillis();
                Double sumDouble = sum(doubles);
                finish = System.currentTimeMillis();
                doubleSumTime += (finish - start);
                System.out.println(sumDouble);
                doubles[0] += 1;

            } else {
                long start = System.currentTimeMillis();
                Double minDouble = min(doubles);
                long finish = System.currentTimeMillis();
                doubleMinTime += (finish - start);
                System.out.println(minDouble);
                doubles[0] += minDouble;

                start = System.currentTimeMillis();
                BigDecimal minDecimal = min(decimals);
                finish = System.currentTimeMillis();
                decimalMinTime += (finish - start);
                System.out.println(minDecimal);
                decimals[0] = decimals[0].add(minDecimal);

                start = System.currentTimeMillis();
                Double sumDouble = sum(doubles);
                finish = System.currentTimeMillis();
                doubleSumTime += (finish - start);
                System.out.println(sumDouble);
                doubles[0] += 1;

                start = System.currentTimeMillis();
                BigDecimal sumDecimal = sum(decimals);
                finish = System.currentTimeMillis();
                decimalSumTime += (finish - start);
                System.out.println(sumDecimal);
                decimals[0] = decimals[0].add(BigDecimal.ONE);
            }
        }

        System.out.println("");
        System.out.format("min: %.2f%n", (double) decimalMinTime / doubleMinTime);

        System.out.println("");
        System.out.format("sum: %.2f%n", (double) decimalSumTime / doubleSumTime);

    }

    private BigDecimal min(BigDecimal[] array) {
        BigDecimal min = array[0];
        for (int i = 0; i < array.length; i++) {
            BigDecimal val = array[i];
            if (min.compareTo(val) > 0) {
                min = val;
            }
        }
        return min;
    }

    private double min(double[] array) {
        double min = array[0];
        for (int i = 0; i < array.length; i++) {
            double val = array[i];
            if (min > val) {
                min = val;
            }
        }
        return min;
    }

    private AxibaseDecimal min(AxibaseDecimal[] array) {
        AxibaseDecimal min = array[0];
        for (int i = 0; i < array.length; i++) {
            AxibaseDecimal val = array[i];
            if (min.compareTo(val) > 0) {
                min = val;
            }
        }
        return min;
    }

    private BigDecimal sum(BigDecimal[] array) {
        BigDecimal sum = array[0];
        for (int i = 0; i < array.length; i++) {
            sum = sum.add(array[i]);
        }
        return sum;
    }

    private double sum(double[] array) {
        double sum = array[0];
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

}
