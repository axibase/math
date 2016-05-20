/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axibase.math.stat.descriptive;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Maintains a dataset of values of a single variable and computes descriptive statistics based on stored data.
 *
 * It is an analog of the DescriptiveStatistics class from
 * the org.apache.commons.math4.stat.descriptive package,
 * but uses BigDecimals instead of doubles.
 * The Apache's design and code is heavily used,
 * but the standard deviation method is substantially different.
 *
 * The Apache's DescriptiveStatistics class description is valid for this class also:
 * <blockquote cite="http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/stat/descriptive/DescriptiveStatistics.html">
 * Maintains a dataset of values of a single variable and computes descriptive statistics based on stored data.
 * The windowSize property sets a limit on the number of values that can be stored in the dataset.
 * The default value, INFINITE_WINDOW, puts no limit on the size of the dataset.
 * This value should be used with caution, as the backing store will grow without bound in this case.
 * For very large datasets, SummaryStatistics, which does not store the dataset, should be used instead of this class.
 * If windowSize is not INFINITE_WINDOW and more values are added than can be stored in the dataset,
 * new values are added in a "rolling" manner, with new values replacing the "oldest" values in the dataset.
 * <p>
 * Note: this class is not threadsafe.
 * </>
 * </blockquote>
 */
public class DescriptiveStatistics implements StatisticalSummary {

    private static final int INFINITE_WINDOW = -1;
    private int windowSize = INFINITE_WINDOW;

    private static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(16, RoundingMode.HALF_UP);
    private MathContext mathContext = DEFAULT_MATH_CONTEXT;

    /** Store data values. */
    private ResizableDecimalArray ra = new ResizableDecimalArray();

    private PercentileCalculator percentileCalculator = new PercentileCalculator(this.ra.getElements());

    private boolean arrayIsChanged = true;

    /**
     * Creates instance with infinite data storage and default MathContext - 16 digits precision and
     * HALF_UP rounding mode.
     */
    public DescriptiveStatistics() {
    }

    /**
     * Creates instance with infinite data storage and provided MathContext.
     */
    public DescriptiveStatistics(MathContext mathContext) {
        this.mathContext = mathContext;
    }

    /**
     * Construct an instance with provided storage capacity (= windowSize) and default MathContext - 16 digits precision and
     * HALF_UP rounding mode.
     * @param windowSize
     */
    public DescriptiveStatistics(int windowSize) {
        setWindowSize(windowSize);
    }

    /**
     * Construct an instance with provided storage capacity (= windowSize) and MathContext.
     * @param windowSize
     * @param
     */
    public DescriptiveStatistics(int windowSize, MathContext mathContext) {
        setWindowSize(windowSize);
        this.mathContext = mathContext;
    }

    /**
     * Construct an instance with provided data and default MathContext.
     * @param values
     */
    public DescriptiveStatistics(BigDecimal[] values) {
        if (values != null) {
            ra = new ResizableDecimalArray(values);
        }
    }

    /**
     * Construct an instance with provided data and MathContext.
     * @param values
     */
    public DescriptiveStatistics(BigDecimal[] values, MathContext mathContext) {
        if (values != null) {
            ra = new ResizableDecimalArray(values);
        }
        this.mathContext = mathContext;
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

    /**
     * Returns the value at the specified index.
     */
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
     * Returns the mean value of the data set and uses the default mathContext for rounding.
     * If there are no elements then result will be null.
     */
    @Override
    public BigDecimal getMean() {
        return getMean(mathContext);
    }

    /**
     * Returns the mean value of the data set and uses the provided mathContext for rounding.
     * If there are no elements then result will be null.
     */
    public BigDecimal getMean(MathContext mathContext) {
        if (getN() == 0) {
            return null;
        }
        return getSum().divide(new BigDecimal(getN()), mathContext);
    }

    /**
     * Return the minimal value in the data set.
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

    /**
     * Return the number of elements in the data set.
     */
    public long getN() {
        return ra.getNumElements();
    }

    /**
     * Evaluates p-th percentile of the stored data set.
     * 0 <= p <= 100
     */
    public BigDecimal getPercentile(BigDecimal p) {
        if (arrayIsChanged) {
            percentileCalculator = new PercentileCalculator(this.ra.getElements());
            arrayIsChanged = false;
        }
        return percentileCalculator.evaluate(p);
    }

    @Override
    public BigDecimal getStandardDeviation() {
        return getStandardDeviation(mathContext);
    }

    /**
     * Returns the sample standard deviation of the data set.
     * If data set is empty the method returns null, if there is a single element
     * in the data set the method returns BigDecimal.ZERO.
     */
    public BigDecimal getStandardDeviation(MathContext stDevContext) {
        return VarianceCalculator.stdDev(this, true, stDevContext);
    }

    @Override
    public BigDecimal getPopulationStandardDeviation() {
        return getPopulationStandardDeviation(mathContext);
    }

    /**
     * Returns the population standard deviation of the data set.
     * If data set is empty the method returns null, if there is a single element
     * in the data set the method returns BigDecimal.ZERO.
     */
    public BigDecimal getPopulationStandardDeviation(MathContext stDevContext) {
        return VarianceCalculator.stdDev(this, false, stDevContext);
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

    @Override
    public BigDecimal getVariance() {
        return getVariance(mathContext);
    }

    /**
     * This method returns the bias-corrected sample variance of the data set.
     * The sample variance is the sum of the squared differences from the Mean
     * divided by (n -1), where n is the number of elements in the data set.
     * If data set is empty the method returns null, if there is a single element
     * in the data set the method returns BigDecimal.ZERO.
     */
    public BigDecimal getVariance(MathContext varianceContext) {
        return VarianceCalculator.variance(this, true, varianceContext);
    }

    @Override
    public BigDecimal getPopulationVariance() {
        return getPopulationVariance(mathContext);
    }

    /**
     * This method returns the population variance of the data set.
     * The population variance is the sum of the squared differences from the Mean
     * divided by n, where n is the number of elements in the data set.
     * The population variance is a biased statistics, use the {@link #getVariance(MathContext)}
     * to get unbiased sample variance.
     * If data set is empty the method returns null, if there is a single element
     * in the data set the method returns BigDecimal.ZERO.
     */
    public BigDecimal getPopulationVariance(MathContext varianceContext) {
        return VarianceCalculator.variance(this, false, varianceContext);
    }

    public BigDecimal[] getValues() {
        return ra.getElements();
    }

    public int getWindowSize() {
        return this.windowSize;
    }

    public void removeMostRecentValue() {
        try {
            ra.discardMostRecentElements(1);
            arrayIsChanged = true;
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("no data");
        }

    }

    public BigDecimal replaceMostRecentValue(BigDecimal number) {
        BigDecimal replaced = ra.substituteMostRecentElement(number);
        arrayIsChanged = true;
        return replaced;
    }

    public void setWindowSize(int windowSize) {
        if (windowSize < 1 && windowSize != INFINITE_WINDOW) {
            throw new IllegalArgumentException("window size must be positive (" + windowSize + ")");
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
    public BigDecimal getSumsq() {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < getN(); i++) {
            sum = sum.add(ra.getElement(i).pow(2));
        }
        return sum;
    }

}
