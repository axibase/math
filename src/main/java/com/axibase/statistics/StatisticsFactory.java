package com.axibase.statistics;

import java.math.BigDecimal;
import java.util.List;

/**
 * Produces implementation of Statistics interface.
 */
public class StatisticsFactory {

    /**
     * Returns {@link DecimalStatistics} if valueType is BigDecimal,
     * and {@link DoubleStatistics} otherwise.
     */
    public static Statistics getDescriptiveStatistics(String valueType) {
        if (valueType.equals("BigDecimal")) {
            return new DecimalStatistics();
        }
        return new DoubleStatistics();
    }

    public static Statistics getDescriptiveStatistics(String valueType, int windowSize) {
        if (valueType.equals("BigDecimal")) {
            return new DecimalStatistics(windowSize);
        }
        return new DoubleStatistics(windowSize);
    }

    public static Statistics getDescriptiveStatistics(List<? extends Number> values) {
        if (values != null && !values.isEmpty()) {
            if (values.get(0) instanceof BigDecimal) {
                return new DecimalStatistics(toDecimalArray(values));
            }

            return new DoubleStatistics(toDoubleArray(values));
        }
        return null;
    }

    private static BigDecimal[] toDecimalArray(List<? extends Number> values) {
        BigDecimal[] internalArray = new BigDecimal[values.size()];
        for (int i = 0; i < values.size(); i++) {
            internalArray[i] = new BigDecimal(values.get(i).toString());
        }
        return internalArray;
    }

    private static double[] toDoubleArray(List<? extends Number> values) {
        double[] internalArray = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            internalArray[i] = values.get(i).doubleValue();
        }
        return internalArray;
    }

}
