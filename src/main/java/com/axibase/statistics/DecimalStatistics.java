package com.axibase.statistics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.math.BigDecimal;

/**
 * Analog of org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
 * but uses BigDecimals instead of doubles.
 * implementation follows to the implementation of
 * org.apache.commons.math3.stat.descriptive.Statistics
 */
public class DecimalStatistics implements Statistics<BigDecimal> {

    private static final int INFINITE_WINDOW = -1;
    private int windowSize = INFINITE_WINDOW;

    /** Store data values. */
    protected ResizableDecimalArray ra = new ResizableDecimalArray();

    public DecimalStatistics() {
    }

    public DecimalStatistics(int windowSize) {
        setWindowSize(windowSize);
    }

    public DecimalStatistics(BigDecimal[] values) {
        if (values != null) {
            ra = new ResizableDecimalArray(values);
        }
    }

    @Override
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

    @Override
    public void clear() {
        ra.clear();
    }

    @Override
    public BigDecimal getElement(int index) {
        return ra.getElement(index);
    }


    /**
     * If there are no elements then result will be null.
     */
    @Override
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
     * If there are no elements then result will be 0.
     */
    @Override
    public double getMean() {
        BigDecimal[] result = getSum().divideAndRemainder(new BigDecimal(getN()));
        return result[0].doubleValue() + result[1].doubleValue() / getN();
    }

    /**
     * If there are no elements then result will be null.
     */
    @Override
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

    @Override
    public long getN() {
        return ra.getNumElements();
    }

    @Override
    public double getPercentile(double p) {
        DescriptiveStatistics ds = new DescriptiveStatistics(toDoubleArray());
        return ds.getPercentile(p);
    }

    /**
     * Returns the standard deviation, Double.NaN if no values have been added
     * or 0.0 for a single value set.
     */
    @Override
    public double getStandardDeviation() {
        double stdDev = Double.NaN;
        if (getN() > 0) {
            if (getN() > 1) {
                stdDev = FastMath.sqrt(getVariance());
            } else {
                stdDev = 0.0d;
            }
        }
        return stdDev;
    }

    /**
     * If there are no elements then result will be 0.
     */
    @Override
    public BigDecimal getSum() {
        BigDecimal sum = new BigDecimal(0);
        for (int i = 0; i < getN(); i++) {
            sum = sum.add(ra.getElement(i));
        }
        return sum;
    }

    @Override
    public double getVariance() {
        BigDecimal[] x =  sumOfSquares().divideAndRemainder(new BigDecimal((getN() - 1)));
        double mean = getMean();
        return x[0].doubleValue() + (x[1].doubleValue() / (getN() - 1)) - mean * mean;
    }

    @Override
    public int getWindowSize() {
        return this.windowSize;
    }

    @Override
    public void removeMostRecentValue() {
        try {
            ra.discardMostRecentElements(1);
        } catch (MathIllegalArgumentException ex) {
            throw new MathIllegalStateException(LocalizedFormats.NO_DATA);
        }

    }

    @Override
    public BigDecimal replaceMostRecentValue(BigDecimal number) {
        return ra.substituteMostRecentElement(number);
    }

    @Override
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

    public BigDecimal sumOfSquares() {
        BigDecimal sum = new BigDecimal(0);
        for (int i = 0; i < getN(); i++) {
            sum = sum.add(ra.getElement(i).multiply(ra.getElement(i)));
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
}
