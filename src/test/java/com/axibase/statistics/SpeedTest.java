package com.axibase.statistics;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * test
 */
public class SpeedTest {

    Map<String, Long> decimalTime;
    Map<String, Long> doubleTime;
    BigDecimal HALF = new BigDecimal("0.5");

    public SpeedTest() {
        clear();
    }

    public static void main(String[] args) {
        SpeedTest tester = new SpeedTest();
        tester.testSpeed(3, 2, 10000000);
    }

    private void testSpeed(int integerDigits, int fractionalDigits, int length) {

        System.out.format("Speed test for %d numbers. Each number has %d digits before the decimal point and %d fractional digits %n",
                length, integerDigits, fractionalDigits);

        MathContext rounding = new MathContext(integerDigits + fractionalDigits, RoundingMode.HALF_UP);

        // run test 10 times for different arrays
        for (int i = 0; i < 10; i++) {
            System.out.format("Iteration %d%n", i);
            List<String> numbers = BigDecimalGenerator.generateUniformStr(integerDigits, fractionalDigits, length);
            BigDecimal[] decimals = BigDecimalGenerator.toBigDecimal(numbers);
            double[] doubles = BigDecimalGenerator.toDouble(numbers);
            System.out.println("Numbers are generated.");

            // measure time to construct StatisticalSummary objects
            long start = System.currentTimeMillis();
            DescriptiveStatistics stat1 = new DescriptiveStatistics(decimals);
            long finish = System.currentTimeMillis();
            long duration = finish - start;
            long time = decimalTime.get("creation");
            decimalTime.put("creation", time + duration);

            start = System.currentTimeMillis();
            org.apache.commons.math3.stat.descriptive.DescriptiveStatistics stat2 = new org.apache.commons.math3.stat.descriptive.DescriptiveStatistics(doubles);
            finish = System.currentTimeMillis();
            duration = finish - start;
            time = doubleTime.get("creation");
            doubleTime.put("creation", time + duration);

            numbers = null;
            decimals = null;
            doubles = null;

            // warm up
            // for (int j = 0; j < 3; j++) {
            //     testDecimalsSpeed(stat1, rounding, false);
            //     testDoubleSpeed(stat2, false);
            // }
            if ((i & 1) == 1) {
                System.out.println("Decimals first.");
                testDecimalsSpeed(stat1, rounding, true);
                testDoubleSpeed(stat2, true);
            } else {
                System.out.println("Doubles first.");
                testDoubleSpeed(stat2, true);
                testDecimalsSpeed(stat1, rounding, true);
            }
        }
        printResults();
    }

    private void testDecimalsSpeed(DescriptiveStatistics stat, MathContext rounding, boolean writeResults) {

        long start = System.currentTimeMillis();
        stat.getMax();
        long finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = decimalTime.get("max");
            decimalTime.put("max", time + duration);
        }

        start = System.currentTimeMillis();
        stat.getMin();
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = decimalTime.get("min");
            decimalTime.put("min", time + duration);
        }
        start = System.currentTimeMillis();
        stat.getMean(rounding);
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = decimalTime.get("mean");
            decimalTime.put("mean", time + duration);
        }
        start = System.currentTimeMillis();
        stat.getPercentile(HALF);
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = decimalTime.get("percentile");
            decimalTime.put("percentile", time + duration);
        }
        start = System.currentTimeMillis();
        stat.getVariance(rounding);
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = decimalTime.get("variance");
            decimalTime.put("variance", time + duration);
        }
        start = System.currentTimeMillis();
        stat.getStandardDeviation(rounding);
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = decimalTime.get("standardDeviation");
            decimalTime.put("standardDeviation", time + duration);
        }
        start = System.currentTimeMillis();
        stat.getSum();
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = decimalTime.get("sum");
            decimalTime.put("sum", time + duration);
        }
    }


    private void testDoubleSpeed(org.apache.commons.math3.stat.descriptive.DescriptiveStatistics stat, boolean writeResults) {

        long start = System.currentTimeMillis();
        stat.getMax();
        long finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = doubleTime.get("max");
            doubleTime.put("max", time + duration);
        }

        start = System.currentTimeMillis();
        stat.getMin();
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = doubleTime.get("min");
            doubleTime.put("min", time + duration);
        }
        start = System.currentTimeMillis();
        stat.getMean();
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = doubleTime.get("mean");
            doubleTime.put("mean", time + duration);
        }
        start = System.currentTimeMillis();
        stat.getPercentile(50d);
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = doubleTime.get("percentile");
            doubleTime.put("percentile", time + duration);
        }
        start = System.currentTimeMillis();
        stat.getVariance();
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = doubleTime.get("variance");
            doubleTime.put("variance", time + duration);
        }
        start = System.currentTimeMillis();
        stat.getStandardDeviation();
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = doubleTime.get("standardDeviation");
            doubleTime.put("standardDeviation", time + duration);
        }
        start = System.currentTimeMillis();
        stat.getSum();
        finish = System.currentTimeMillis();
        if (writeResults) {
            long duration = finish - start;
            long time = doubleTime.get("sum");
            doubleTime.put("sum", time + duration);
        }
    }

    private void clear() {
        decimalTime = new HashMap<>(7);
        fillDictionary(decimalTime);
        doubleTime = new HashMap<>(7);
        fillDictionary(doubleTime);
    }

    private void fillDictionary(Map<String, Long> dict) {
        dict.put("creation", 0l);
        dict.put("max", 0l);
        dict.put("min", 0l);
        dict.put("mean", 0l);
        dict.put("percentile", 0l);
        dict.put("variance", 0l);
        dict.put("standardDeviation", 0l);
        dict.put("sum", 0l);
    }

    private void printResults() {
        System.out.println("");
        for (String s : decimalTime.keySet()) {
            System.out.format("%s : %.2f%n", s, (double) decimalTime.get(s) / doubleTime.get(s));
        }
    }

}
