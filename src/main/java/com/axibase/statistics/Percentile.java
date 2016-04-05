package com.axibase.statistics;

import org.apache.commons.math3.util.FastMath;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 *
 */
public class Percentile {

    /** Values. */
    private final BigDecimal[] storedData;

    /** Cached pivots. */
    private int[] cachedPivots;

    /** Maximum number of partitioning pivots cached (each level double the number of pivots). */
    private static final int MAX_CACHED_LEVELS = 10;

    /** Maximum number of cached pivots in the pivots cached array */
    private static final int PIVOTS_HEAP_LENGTH = 0x1 << MAX_CACHED_LEVELS - 1;

    /** Minimum selection size for insertion sort rather than selection. */
    private static final int MIN_SELECT_SIZE = 15;

    public Percentile(BigDecimal[] values) {
        if (values == null) {
            throw new NullPointerException("BigDecimal array argument of Percentile() constructor is null.");
        }
        this.storedData = Arrays.copyOf(values, values.length);
        cachedPivots = new int[PIVOTS_HEAP_LENGTH];
        Arrays.fill(cachedPivots, -1);
    }

    /**
     * Returns the result of evaluating the statistic over the stored data.
     *
     * @param p the percentile value to compute
     * @return the value of the statistic applied to the stored data
     * (p must be greater than 0 and less than or equal to 1)
     */
    public BigDecimal evaluate(final BigDecimal p) {
        if (storedData.length == 0) {
            return null;
        }
        if (storedData.length == 1) {
            return storedData[0];
        }
        final BigDecimal pos = p.multiply(new BigDecimal(storedData.length + 1));
        final BigDecimal fPos = pos.setScale(0, BigDecimal.ROUND_DOWN);
        final int intPos = fPos.intValue();
        final BigDecimal diff = pos.subtract(fPos);

        if (intPos < 1) {
            return select(0);
        }
        if (intPos >= storedData.length) {
            return select(storedData.length - 1);
        }

        final BigDecimal lower = select(intPos - 1);
        final BigDecimal upper = select(intPos);
        return lower.add(diff.multiply((upper.subtract(lower))));
    }

    public BigDecimal evaluate(String p) {
        return evaluate(new BigDecimal(p));
    }

    /**
     * Select K<sup>th</sup> value in the array.
     *
     * @param k the index whose value in the array is of interest
     * @return K<sup>th</sup> value
     */
    public BigDecimal select(final int k) {
        int begin = 0;
        int end = storedData.length;
        int node = 0;
        while (end - begin > MIN_SELECT_SIZE) {
            final int pivot;

            if (node < cachedPivots.length && cachedPivots[node] >= 0) {
                // the pivot has already been found in a previous call
                // and the array has already been partitioned around it
                pivot = cachedPivots[node];
            } else {
                // select a pivot and partition work array around it
                pivot = partition(begin, end, pivotIndex(storedData, begin, end));
                if (node < cachedPivots.length) {
                    cachedPivots[node] = pivot;
                }
            }

            if (k == pivot) {
                // the pivot was exactly the element we wanted
                return storedData[k];
            } else if (k < pivot) {
                // the element is in the left partition
                end  = pivot;
                node = FastMath.min(2 * node + 1, cachedPivots.length); // TODO just set node = 2 * node + 1 ???
            } else {
                // the element is in the right partition
                begin = pivot + 1;
                node  = FastMath.min(2 * node + 2, cachedPivots.length); // TODO just set node = 2 * node + 2 ???
            }
        }
        Arrays.sort(storedData, begin, end);
        return storedData[k];
    }

    private int pivotIndex(BigDecimal[] storedData, final int begin, final int end) {

        final int inclusiveEnd = end - 1;
        final int middle = begin + (inclusiveEnd - begin) / 2;
        final BigDecimal wBegin = storedData[begin];
        final BigDecimal wMiddle = storedData[middle];
        final BigDecimal wEnd = storedData[inclusiveEnd];

        if (wBegin.compareTo(wMiddle) < 0) {
            if (wMiddle.compareTo(wEnd) < 0) {
                return middle;
            } else {
                return wBegin.compareTo(wEnd) < 0 ? inclusiveEnd : begin;
            }
        } else {
            if (wBegin.compareTo(wEnd) < 0) {
                return begin;
            } else {
                return wMiddle.compareTo(wEnd) < 0 ? inclusiveEnd : middle;
            }
        }
    }

    /**
     * Partition an array slice around a pivot.Partitioning exchanges array
     * elements such that all elements smaller than pivot are before it and
     * all elements larger than pivot are after it.
     *
     * @param begin index of the first element of the slice of work array
     * @param end index after the last element of the slice of work array
     * @param pivot initial index of the pivot
     * @return index of the pivot after partition
     */
    private int partition(final int begin, final int end, final int pivot) {

        final BigDecimal value = storedData[pivot];
        storedData[pivot] = storedData[begin];

        int i = begin + 1;
        int j = end - 1;
        while (i < j) {
            while (i < j && storedData[j].compareTo(value) > 0) {
                --j;
            }
            while (i < j && storedData[i].compareTo(value) < 0) {
                ++i;
            }

            if (i < j) {
                final BigDecimal tmp = storedData[i];
                storedData[i++] = storedData[j];
                storedData[j--] = tmp;
            }
        }

        if (i >= end || storedData[i].compareTo(value) > 0) {
            --i;
        }
        storedData[begin] = storedData[i];
        storedData[i] = value;
        return i;
    }

}
