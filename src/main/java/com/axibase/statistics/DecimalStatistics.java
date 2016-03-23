package com.axibase.statistics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Analog of DescriptiveStatistics class from
 * the org.apache.commons.math3.stat.descriptive package,
 * but uses BigDecimals instead of doubles.
 * The code is refactoring of the open source Apache's code.
 */
public class DecimalStatistics {

    private static final int INFINITE_WINDOW = -1;
    private int windowSize = INFINITE_WINDOW;

    /** Store data values. */
    private ResizableDecimalArray ra = new ResizableDecimalArray();

    private MathContext mathContext = null;

    public DecimalStatistics() {
    }

    public DecimalStatistics(MathContext mathContext) {
        setMathContext(mathContext);
    }

    public DecimalStatistics(int windowSize) {
        setWindowSize(windowSize);
    }

    public DecimalStatistics(int windowSize, MathContext mathContext) {
        setWindowSize(windowSize);
        setMathContext(mathContext);
    }

    public DecimalStatistics(BigDecimal[] values) {
        if (values != null) {
            ra = new ResizableDecimalArray(values);
        }
    }

    /** Adds value to the dataset. If the dataset is at the maximum size
     * (i.e., the number of stored elements equals the currently configured windowSize),
     * the first (oldest) element in the dataset is discarded to make room for the new value.
     */
    public void addValue(BigDecimal value) {
        if (windowSize != INFINITE_WINDOW) {
            if (getN() == windowSize) {
                ra.addElementRolling(value);
            } else if (getN() < windowSize) {
                ra.addElement(value);
            }
        } else {
            ra.addElement(value);
        }
    }

    public void clear() {
        ra.clear();
    }

    public BigDecimal getElement(int index) {
        return ra.getElement(index);
    }

    /**
     * If there are no elements then result will be null.
     */
    public BigDecimal getMax() {
        BigDecimal max;
        if (getN() == 0) {
            return null;
        }
        max = ra.getElement(0);
        for (int i = 1; i < getN(); i++) {
            if (max.compareTo(ra.getElement(i)) < 0) {
                max = ra.getElement(i);
            }
        }
        return max;
    }

    /**
     * Returns the mean value of the dataset and uses the mathContext to
     * to get a chosen precision and rounding mode by supplying an appropriate MathContext
     * If there are no elements then result will be 0.
     */
    public BigDecimal getMean(MathContext mathContext) {
        if (getN() == 0) {
            return null;
        }
        return getSum().divide(new BigDecimal(getN()), mathContext);
    }

    /**
     * If there are no elements then result will be null.
     */
    public BigDecimal getMin() {
        BigDecimal min;
        if (getN() == 0) {
            return null;
        }
        min = ra.getElement(0);
        for (int i = 1; i < getN(); i++) {
            if (min.compareTo(ra.getElement(i)) > 0) {
                min = ra.getElement(i);
            }
        }
        return min;
    }

    public long getN() {
        return ra.getNumElements();
    }

    public double getPercentile(double p) {
        DescriptiveStatistics ds = new DescriptiveStatistics(toDoubleArray());
        return ds.getPercentile(p);
    }

    /**
     */
    public BigDecimal getStandardDeviation(MathContext mathContext) {
        BigDecimal stdDev = null;
//        if (getN() > 0) {
//            if (getN() > 1) {
//                stdDev = FastMath.sqrt(getVariance(mathContext));
//            } else {
//                stdDev = BigDecimal.ZERO;
//            }
//        }
        return stdDev;
    }

    /**
     * Returns exact sum of elements in the data set.
     * If there are no elements then result will be 0.
     */
    public BigDecimal getSum() {
        BigDecimal sum = new BigDecimal(0);
        for (int i = 0; i < getN(); i++) {
            sum = sum.add(ra.getElement(i));
        }
        return sum;
    }

    /**
     * Returns the sample variance of the dataset.
     * The sample variance is the sum of the squared differences from the Mean
     * divided by (N -1), where N is the number of elements in the dataset.
     * TODO estimate precision
     */
    public BigDecimal getVariance(MathContext mathContext) {
        BigDecimal meanSquared = getMean(mathContext).pow(2);
        BigDecimal meanOfSquares = sumOfSquares().divide(new BigDecimal(getN()), mathContext);
        return meanOfSquares.subtract(meanSquared);
    }

    public int getWindowSize() {
        return this.windowSize;
    }

    public void removeMostRecentValue() {
        try {
            ra.discardMostRecentElements(1);
        } catch (MathIllegalArgumentException ex) {
            throw new MathIllegalStateException(LocalizedFormats.NO_DATA);
        }

    }

    public BigDecimal replaceMostRecentValue(BigDecimal number) {
        return ra.substituteMostRecentElement(number);
    }

    public void setWindowSize(int windowSize) {
        if (windowSize < 1 && windowSize != INFINITE_WINDOW) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.NOT_POSITIVE_WINDOW_SIZE, windowSize);
        }

        this.windowSize = windowSize;

        // We need to check to see if we need to discard elements
        // from the front of the array.  If the windowSize is less than
        // the current number of elements.
        if (windowSize != INFINITE_WINDOW && windowSize < ra.getNumElements()) {
            ra.discardFrontElements(ra.getNumElements() - windowSize);
        }
    }

    /**
     * Returns the exact sum of squares of values in the dataset.
     * If there are no elements then result will be 0.
     */
    public BigDecimal sumOfSquares() {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < getN(); i++) {
            sum = sum.add(ra.getElement(i).pow(2));
        }
        return sum;
    }

    private double[] toDoubleArray() {
        double[] result = new double[(int) getN()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ra.getElement(i).doubleValue();
        }
        return result;
    }

    public void setMathContext(MathContext mathContext) {
        this.mathContext = mathContext;
    }
}
