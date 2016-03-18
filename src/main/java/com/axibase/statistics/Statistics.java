package com.axibase.statistics;

/**
 * Maintains a data set of values of a single variable and computes descriptive statistics based on stored data.
 * The windowSize property sets a limit on the number of values that can be stored in the data set.
 * The windowSize value -1 puts no limit on the size of the data set.
 * This value should be used with caution, as the backing store will grow without bound in this case.
 * If windowSize is not -1 and more values are added than can be stored in the data set, new values are added in a
 * "rolling" manner, with new values replacing the "oldest" values in the data set.
 */
public interface Statistics<T extends Number> {
    void addValue(T value);
    void clear();
    T getElement(int index);
    T getMax();
    double getMean();
    T getMin();
    long getN();
    double getPercentile(double p);
    double getStandardDeviation();
    T getSum();
    double getVariance();
    int getWindowSize();
    void removeMostRecentValue();
    T replaceMostRecentValue(T number);
    void setWindowSize(int windowSize);
}
