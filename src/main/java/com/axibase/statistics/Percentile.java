package com.axibase.statistics;

import org.apache.commons.math3.util.FastMath;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * An instance of the class stores the copy of an array of BigDecimal values provided to the constructor.
 * Method evaluate(p) calculates the exact value of p-th percentile, 0 <= p <= 1.
 * An argument p can be String or BigDecimal number.
 * Method select(k) returns k-th smallest element of the data array.
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

    // TODO: java 7, 8 Arrays.sort is not insertion sort but TimSort
    // TODO: (or LegacyMergeSort if system property java.util.Arrays.useLegacyMergeSort is used) and
    // TODO: it is not clear if the trick works.
    /** Minimum selection size for insertion sort rather than selection. */
    private static final int MIN_SELECT_SIZE = 15;

    /** This is number of values + 1, and is used to calculate position (index) of percentile in sorted data. */
    private final BigDecimal multiplier;

    public Percentile(BigDecimal[] values) {
        if (values == null) {
            throw new NullPointerException("BigDecimal array argument of the Percentile constructor is null.");
        }
        this.storedData = Arrays.copyOf(values, values.length);
        cachedPivots = new int[PIVOTS_HEAP_LENGTH];
        Arrays.fill(cachedPivots, -1);
        multiplier = new BigDecimal(storedData.length + 1);
    }

    /**
     * Returns the result of evaluating the statistic over the stored data.
     * The algorithm implemented here works as follows:
     * <ol>
     * <li>Let <code>n</code> be the length of the (sorted) array and
     * <code>0 <= p <= 1</code> be the desired percentile.</li>
     * <li>If <code> n = 1 </code> return the unique array element (regardless of
     * the value of <code>p</code>); otherwise </li>
     * <li>Compute the estimated percentile position
     * <code> pos = p * (n + 1) </code> and the difference, <code>d</code>
     * between <code>pos</code> and <code>floor(pos)</code> (i.e. the fractional
     * part of <code>pos</code>).</li>
     * <li> If <code>pos < 1</code> return the smallest element in the array.</li>
     * <li> Else if <code>pos >= n</code> return the largest element in the array.</li>
     * <li> Else let <code>lower</code> be the element in position
     * <code>floor(pos)</code> in the array and let <code>upper</code> be the
     * next element in the array.  Return <code>lower + d * (upper - lower)</code>
     * </li>
     * </ol>
     * @param p the percentile value to compute, 0 <= p <= 1.
     * @return the value of the statistic applied to the stored data.
     * If there are no data the method returns null.
     */
    public BigDecimal evaluate(final BigDecimal p) {
        if (storedData.length == 0) {
            return null;
        }
        if (storedData.length == 1) {
            return storedData[0];
        }
        final BigDecimal pos = p.multiply(multiplier);
        final BigDecimal floorPos = pos.setScale(0, BigDecimal.ROUND_DOWN);
        final int intPos = floorPos.intValue();
        final BigDecimal diff = pos.subtract(floorPos);

        if (intPos < 1) {
            return select(0);
        }
        if (intPos >= storedData.length) {
            return select(storedData.length - 1);
        }

        // Indexing in the array is zero based.
        final BigDecimal lower = select(intPos - 1);
        final BigDecimal upper = select(intPos);
        return lower.add(diff.multiply((upper.subtract(lower))));
    }

    public BigDecimal evaluate(String p) {
        return evaluate(new BigDecimal(p));
    }

    /**
     * Select K<sup>th</sup> value in the array in ascending order.
     *
     * @param k the index whose value in the array is of interest
     * @return K<sup>th</sup> value
     */
    public BigDecimal select(final int k) {

        int begin = 0;
        int end = storedData.length;
        if (k < begin || k >= end) {
            throw new IllegalArgumentException("Attempt to select element with index: " + k +
                    " in zero based indexed array of length " + storedData.length);
        }

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
                node = FastMath.min(2 * node + 1, cachedPivots.length);
            } else {
                // the element is in the right partition
                begin = pivot + 1;
                node  = FastMath.min(2 * node + 2, cachedPivots.length);
            }
        }
        Arrays.sort(storedData, begin, end);
        return storedData[k];
    }


    /**
     * Choose the pivot value among values from begin index (inclusive) up to end index (exclusive).
     * The "median of 3 pivoting" strategy is used. Returns the index of the pivot element.
     */
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
     * Partition an array slice around a pivot. Partitioning exchanges array
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
