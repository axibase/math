package com.axibase.statistics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Analog of the DescriptiveStatistics class from
 * the org.apache.commons.math3.stat.descriptive package,
 * but uses BigDecimals instead of doubles.
 * The Apache's design and code is heavily used,
 * but the standard deviation method is substantially different.
 *
 * The Apache's DescriptiveStatistics class description is valid for the StoredStatistics also,
 * (but use the StreamStatistics class instead of Apache's SummaryStatistics):
 * <blockquote cite="http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/stat/descriptive/DescriptiveStatistics.html">
 * Maintains a dataset of values of a single variable and computes descriptive statistics based on stored data.
 * The windowSize property sets a limit on the number of values that can be stored in the dataset.
 * The default value, INFINITE_WINDOW, puts no limit on the size of the dataset.
 * This value should be used with caution, as the backing store will grow without bound in this case.
 * For very large datasets, SummaryStatistics, which does not store the dataset, should be used instead of this class.
 * If windowSize is not INFINITE_WINDOW and more values are added than can be stored in the dataset,
 * new values are added in a "rolling" manner, with new values replacing the "oldest" values in the dataset.
 * <p>
 * Note: this class is not threadsafe. Use SynchronizedDescriptiveStatistics if concurrent access from multiple threads is required.
 * </>
 * </blockquote>
 * Maintains a data set of values of a single variable and computes descriptive statistics based on stored data.
 * The windowSize property sets a limit on the number of values that can be stored in the data set.
 * The default value INFINITE_WINDOW puts no limit on the size of the data set.
 * This value should be used with caution, as the backing store will grow without bound in this case.
 * If windowSize is not -1 and more values are added than can be stored in the data set, new values are added in a
 * "rolling" manner, with new values replacing the "oldest" values in the data set.
 */
public class StoredStatistics implements Statistics {

    private static final int INFINITE_WINDOW = -1;
    private int windowSize = INFINITE_WINDOW;

    /** Store data values. */
    private ResizableDecimalArray ra = new ResizableDecimalArray();

    private PercentileCalculator percentileCalculator = new PercentileCalculator(this.ra.getElements());

    private boolean arrayIsChanged = true;

    public StoredStatistics() {
    }

    public StoredStatistics(int windowSize) {
        setWindowSize(windowSize);
    }

    public StoredStatistics(BigDecimal[] values) {
        if (values != null) {
            ra = new ResizableDecimalArray(values);
        }
    }

    /** Adds value to the data set. If the data set is at the maximum size i.e.,
     * the number of stored elements equals the currently configured windowSize,
     * the first (oldest) element in the data set is discarded to make room for the new value.
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
        arrayIsChanged = true;
    }

    /**
     * Clear the data set.
     */
    public void clear() {
        ra.clear();
        arrayIsChanged = true;
    }

    public BigDecimal getElement(int index) {
        return ra.getElement(index);
    }

    /**
     * If there are no elements then result will be null.
     */
    public BigDecimal max() {
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
     * Returns the mean value of the data set and uses the mathContext to
     * to get a chosen precision and rounding mode by supplying an appropriate MathContext
     * If there are no elements then result will be null.
     */
    public BigDecimal mean(MathContext mathContext) {
        if (getN() == 0) {
            return null;
        }
        return sum().divide(new BigDecimal(getN()), mathContext);
    }

    /**
     * If there are no elements then result will be null.
     */
    public BigDecimal min() {
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

    /**
     * Return the number of elements in the data set.
     */
    public long getN() {
        return ra.getNumElements();
    }

    /**
     * Evaluates p-th percentile of the stored data set.
     * 0 <= p <= 1
     * Uses instance of {@link PercentileCalculator} for that.
     */
    public BigDecimal percentile(BigDecimal p) {
        if (arrayIsChanged) {
            percentileCalculator = new PercentileCalculator(this.ra.getElements());
            arrayIsChanged = false;
        }
        return percentileCalculator.evaluate(p);
    }

    /**
     * Returns the sample standard deviation of the data set.
     * If data set is empty the method returns null, if there is a single element
     * in the data set the method returns BigDecimal.ZERO.
     */
    public BigDecimal sampleStdDev(MathContext stDevContext) {
        return VarianceCalculator.stdDev(this, true, stDevContext);
    }

    /**
     * Returns the population standard deviation of the data set.
     * If data set is empty the method returns null, if there is a single element
     * in the data set the method returns BigDecimal.ZERO.
     */
    public BigDecimal populationStdDev(MathContext stDevContext) {
        return VarianceCalculator.stdDev(this, false, stDevContext);
    }

    /**
     * Returns exact sum of elements in the data set.
     * If there are no elements then result will be 0.
     */
    public BigDecimal sum() {
        BigDecimal sum = new BigDecimal(0);
        for (int i = 0; i < getN(); i++) {
            sum = sum.add(ra.getElement(i));
        }
        return sum;
    }

    /**
     * This method returns the bias-corrected sample variance of the data set.
     * The sample variance is the sum of the squared differences from the Mean
     * divided by (n -1), where n is the number of elements in the data set.
     * If data set is empty the method returns null, if there is a single element
     * in the data set the method returns BigDecimal.ZERO.
     */
    public BigDecimal sampleVariance(MathContext varianceContext) {
        return VarianceCalculator.variance(this, true, varianceContext);
    }

    /**
     * This method returns the population variance of the data set.
     * The population variance is the sum of the squared differences from the Mean
     * divided by n, where n is the number of elements in the data set.
     * The population variance is a biased statistics, use the {@link #sampleVariance(MathContext)}
     * to get unbiased sample variance.
     * If data set is empty the method returns null, if there is a single element
     * in the data set the method returns BigDecimal.ZERO.
     */
    public BigDecimal populationVariance(MathContext varianceContext) {
        return VarianceCalculator.variance(this, false, varianceContext);
    }

    public int getWindowSize() {
        return this.windowSize;
    }

    public void removeMostRecentValue() {
        try {
            ra.discardMostRecentElements(1);
            arrayIsChanged = true;
        } catch (MathIllegalArgumentException ex) {
            throw new MathIllegalStateException(LocalizedFormats.NO_DATA);
        }

    }

    public BigDecimal replaceMostRecentValue(BigDecimal number) {
        BigDecimal replaced = ra.substituteMostRecentElement(number);
        arrayIsChanged = true;
        return replaced;
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
            arrayIsChanged = true;
        }
    }

    /**
     * Returns the exact sum of squares of values in the data set.
     * If there are no elements then result will be 0.
     */
    public BigDecimal sumOfSquares() {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < getN(); i++) {
            sum = sum.add(ra.getElement(i).pow(2));
        }
        return sum;
    }

}
