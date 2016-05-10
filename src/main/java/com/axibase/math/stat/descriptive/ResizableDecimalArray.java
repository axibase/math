package com.axibase.math.stat.descriptive;

import org.apache.commons.math3.exception.*;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
//import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * A variable length BigDecimals storage that automatically handles expanding and contracting as elements are added and removed.
 * Copy-pasted from
 * org.apache.commons.math3.util.ResizableDoubleArray
 * Deprecated methods and the <br/>
 * {@code public double compute(MathArrays.Function f)}
 * <br/>
 * method were removed.
 * The array automatically handles expanding and contracting its internal storage
 * array as elements are added and removed.
 */
public class ResizableDecimalArray {

    /** Default value for initial capacity. */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /** Default value for array size modifier. */
    private static final double DEFAULT_EXPANSION_FACTOR = 2.0;

    /**
     * Default value for the difference between {@link #contractionCriterion}
     * and {@link #expansionFactor}.
     */
    private static final double DEFAULT_CONTRACTION_DELTA = 0.5;

    /**
     * The contraction criteria determines when the internal array will be
     * contracted to fit the number of elements contained in the element
     *  array + 1.
     */
    private double contractionCriterion = 2.5;

    /**
     * The expansion factor of the array.  When the array needs to be expanded,
     * the new array size will be
     * {@code internalArray.length * expansionFactor}
     * if {@code expansionMode} is set to MULTIPLICATIVE, or
     * {@code internalArray.length + expansionFactor} if
     * {@code expansionMode} is set to ADDITIVE.
     */
    private double expansionFactor = 2.0;

    /**
     * Determines whether array expansion by {@code expansionFactor}
     * is additive or multiplicative.
     */
    private ExpansionMode expansionMode = ExpansionMode.MULTIPLICATIVE;

    /** The internal storage array. */
    private BigDecimal[] internalArray;

    /**
     * The number of addressable elements in the array.  Note that this
     * has nothing to do with the length of the internal storage array.
     */
    private int numElements = 0;

    /**
     * The position of the first addressable element in the internal storage
     * array.  The addressable elements in the array are
     * {@code internalArray[startIndex],...,internalArray[startIndex + numElements - 1]}.
     */
    private int startIndex = 0;

    /** Specification of expansion algorithm. */
    public enum ExpansionMode {
        MULTIPLICATIVE,
        ADDITIVE
    }

    /**
     * Creates an instance with default properties.
     * <ul>
     *  <li>{@code initialCapacity = 16}</li>
     *  <li>{@code expansionMode = MULTIPLICATIVE}</li>
     *  <li>{@code expansionFactor = 2.0}</li>
     *  <li>{@code contractionCriterion = 2.5}</li>
     * </ul>
     */
    public ResizableDecimalArray() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates an instance with the specified initial capacity.
     * Other properties take default values:
     * <ul>
     *  <li>{@code expansionMode = MULTIPLICATIVE}</li>
     *  <li>{@code expansionFactor = 2.0}</li>
     *  <li>{@code contractionCriterion = 2.5}</li>
     * </ul>
     * @param initialCapacity Initial size of the internal storage array.
     * @throws MathIllegalArgumentException if {@code initialCapacity <= 0}.
     */
    public ResizableDecimalArray(int initialCapacity) throws MathIllegalArgumentException {
        this(initialCapacity, DEFAULT_EXPANSION_FACTOR);
    }

    /**
     * Creates an instance with the specified initial capacity
     * and expansion factor.
     * The remaining properties take default values:
     * <ul>
     *  <li>{@code expansionMode = MULTIPLICATIVE}</li>
     *  <li>{@code contractionCriterion = 0.5 + expansionFactor}</li>
     * </ul>
     * <br/>
     * Throws IllegalArgumentException if the following conditions are
     * not met:
     * <ul>
     *  <li>{@code initialCapacity > 0}</li>
     *  <li>{@code expansionFactor > 1}</li>
     * </ul>
     *
     * @param initialCapacity Initial size of the internal storage array.
     * @param expansionFactor The array will be expanded based on this
     * parameter.
     * @throws MathIllegalArgumentException if parameters are not valid.
     */
    public ResizableDecimalArray(int initialCapacity,
                                double expansionFactor)
            throws MathIllegalArgumentException {
        this(initialCapacity,
                expansionFactor,
                DEFAULT_CONTRACTION_DELTA + expansionFactor);
    }

    /**
     * Creates an instance with the specified initial capacity,
     * expansion factor, and contraction criteria.
     * The expansion mode will default to {@code MULTIPLICATIVE}.
     * <br/>
     * Throws IllegalArgumentException if the following conditions are
     * not met:
     * <ul>
     *  <li>{@code initialCapacity > 0}</li>
     *  <li>{@code expansionFactor > 1}</li>
     *  <li>{@code contractionCriterion >= expansionFactor}</li>
     * </ul>
     *
     * @param initialCapacity Initial size of the internal storage array..
     * @param expansionFactor The array will be expanded based on this
     * parameter.
     * @param contractionCriterion Contraction criterion.
     * @throws MathIllegalArgumentException if the parameters are not valid.
     */
    public ResizableDecimalArray(int initialCapacity,
                                double expansionFactor,
                                double contractionCriterion)
            throws MathIllegalArgumentException {
        this(initialCapacity,
                expansionFactor,
                contractionCriterion,
                ExpansionMode.MULTIPLICATIVE,
                null);
    }

    /**
     * Creates an instance with the specified properties.
     * <br/>
     * Throws MathIllegalArgumentException if the following conditions are
     * not met:
     * <ul>
     *  <li>{@code initialCapacity > 0}</li>
     *  <li>{@code expansionFactor > 1}</li>
     *  <li>{@code contractionCriterion >= expansionFactor}</li>
     * </ul>
     *
     * @param initialCapacity Initial size of the internal storage array.
     * @param expansionFactor The array will be expanded based on this
     * parameter.
     * @param contractionCriterion Contraction criteria.
     * @param expansionMode Expansion mode.
     * @param data Initial contents of the array.
     * @throws MathIllegalArgumentException if the parameters are not valid.
     */
    public ResizableDecimalArray(int initialCapacity,
                                double expansionFactor,
                                double contractionCriterion,
                                ExpansionMode expansionMode,
                                BigDecimal ... data)
            throws MathIllegalArgumentException {
        if (initialCapacity <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.INITIAL_CAPACITY_NOT_POSITIVE,
                    initialCapacity);
        }
        checkContractExpand(contractionCriterion, expansionFactor);

        this.expansionFactor = expansionFactor;
        this.contractionCriterion = contractionCriterion;
        this.expansionMode = expansionMode;
        internalArray = new BigDecimal[initialCapacity];
        numElements = 0;
        startIndex = 0;

        if (data != null && data.length > 0) {
            addElements(data);
        }
    }

    /**
     * Creates an instance from an existing {@code BigDecimal[]} with the
     * initial capacity and numElements corresponding to the size of
     * the supplied {@code BigDecimal[]} array.
     * If the supplied array is null, a new empty array with the default
     * initial capacity will be created.
     * The input array is copied, not referenced.
     * Other properties take default values:
     * <ul>
     *  <li>{@code initialCapacity = 16}</li>
     *  <li>{@code expansionMode = MULTIPLICATIVE}</li>
     *  <li>{@code expansionFactor = 2.0}</li>
     *  <li>{@code contractionCriterion = 2.5}</li>
     * </ul>
     *
     * @param initialArray initial array
     */
    public ResizableDecimalArray(BigDecimal[] initialArray) {
        this(DEFAULT_INITIAL_CAPACITY,
                DEFAULT_EXPANSION_FACTOR,
                DEFAULT_CONTRACTION_DELTA + DEFAULT_EXPANSION_FACTOR,
                ExpansionMode.MULTIPLICATIVE,
                initialArray);
    }

    /**
     * Copy constructor. Creates a new ResizableDecimalArray that is a copy of the original.
     * But internal array stores links to the same (!) instances of BigDecimals as the original.
     * As BigDecimals are immutable it is admissible.
     * Needs to acquire synchronization lock on original. Original may not be null;
     * otherwise a {@link NullArgumentException} is thrown.
     *
     * @param original array to copy
     * @exception NullArgumentException if original is null
     */
    public ResizableDecimalArray(ResizableDecimalArray original)
            throws NullArgumentException {
        MathUtils.checkNotNull(original);
        copy(original, this);
    }

    /**
     * Adds an element to the end of this expandable array.
     *
     * @param value Value to be added to end of array.
     */
    public synchronized void addElement(BigDecimal value) {
        if (internalArray.length <= startIndex + numElements) {
            expand();
        }
        internalArray[startIndex + numElements++] = value;
    }

    /**
     * Adds several element to the end of this expandable array.
     *
     * @param values Values to be added to end of array.
     */
    public synchronized void addElements(BigDecimal[] values) {
        final BigDecimal[] tempArray = new BigDecimal[numElements + values.length + 1];
        System.arraycopy(internalArray, startIndex, tempArray, 0, numElements);
        System.arraycopy(values, 0, tempArray, numElements, values.length);
        internalArray = tempArray;
        startIndex = 0;
        numElements += values.length;
    }

    /**
     * <p>
     * Adds an element to the end of the array and removes the first
     * element in the array.  Returns the discarded first element.
     * The effect is similar to a push operation in a FIFO queue.
     * </p>
     * <p>
     * Example: If the array contains the elements 1, 2, 3, 4 (in that order)
     * and addElementRolling(5) is invoked, the result is an array containing
     * the entries 2, 3, 4, 5 and the value returned is 1.
     * </p>
     *
     * @param value Value to be added to the array.
     * @return the value which has been discarded or "pushed" out of the array
     * by this rolling insert.
     */
    public synchronized BigDecimal addElementRolling(BigDecimal value) {
        BigDecimal discarded = internalArray[startIndex];

        if ((startIndex + (numElements + 1)) > internalArray.length) {
            expand();
        }
        // Increment the start index
        startIndex += 1;

        // Add the new value
        internalArray[startIndex + (numElements - 1)] = value;

        // Check the contraction criterion.
        if (shouldContract()) {
            contract();
        }
        return discarded;
    }

    /**
     * Substitutes <code>value</code> for the most recently added value.
     * Returns the value that has been replaced. If the array is empty (i.e.
     * if {@link #numElements} is zero), an IllegalStateException is thrown.
     *
     * @param value New value to substitute for the most recently added value
     * @return the value that has been replaced in the array.
     * @throws MathIllegalStateException if the array is empty
     */
    public synchronized BigDecimal substituteMostRecentElement(BigDecimal value)
            throws MathIllegalStateException {
        if (numElements < 1) {
            throw new MathIllegalStateException(
                    LocalizedFormats.CANNOT_SUBSTITUTE_ELEMENT_FROM_EMPTY_ARRAY);
        }

        final int substIndex = startIndex + (numElements - 1);
        final BigDecimal discarded = internalArray[substIndex];

        internalArray[substIndex] = value;

        return discarded;
    }

    /**
     * Checks the expansion factor and the contraction criterion and raises
     * an exception if the contraction criterion is smaller than the
     * expansion criterion.
     *
     * @param contraction Criterion to be checked.
     * @param expansion Factor to be checked.
     * @throws NumberIsTooSmallException if {@code contraction < expansion}.
     * @throws NumberIsTooSmallException if {@code contraction <= 1}.
     * @throws NumberIsTooSmallException if {@code expansion <= 1 }.
     */
    protected void checkContractExpand(double contraction,
                                       double expansion)
            throws NumberIsTooSmallException {
        if (contraction < expansion) {
            final NumberIsTooSmallException e = new NumberIsTooSmallException(contraction, 1, true);
            e.getContext().addMessage(LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_EXPANSION_FACTOR,
                    contraction, expansion);
            throw e;
        }

        if (contraction <= 1) {
            final NumberIsTooSmallException e = new NumberIsTooSmallException(contraction, 1, false);
            e.getContext().addMessage(LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_ONE,
                    contraction);
            throw e;
        }

        if (expansion <= 1) {
            final NumberIsTooSmallException e = new NumberIsTooSmallException(contraction, 1, false);
            e.getContext().addMessage(LocalizedFormats.EXPANSION_FACTOR_SMALLER_THAN_ONE,
                    expansion);
            throw e;
        }
    }

    /**
     * Clear the array contents, resetting the number of elements to zero.
     */
    public synchronized void clear() {
        numElements = 0;
        startIndex = 0;
    }

    /**
     * Contracts the storage array to the (size of the element set) + 1 - to
     * avoid a zero length array. This function also resets the startIndex to
     * zero.
     */
    public synchronized void contract() {
        final BigDecimal[] tempArray = new BigDecimal[numElements + 1];

        // Copy and swap - copy only the element array from the src array.
        System.arraycopy(internalArray, startIndex, tempArray, 0, numElements);
        internalArray = tempArray;

        // Reset the start index to zero
        startIndex = 0;
    }

    /**
     * Discards the <code>i</code> initial elements of the array.  For example,
     * if the array contains the elements 1,2,3,4, invoking
     * <code>discardFrontElements(2)</code> will cause the first two elements
     * to be discarded, leaving 3,4 in the array.  Throws illegalArgumentException
     * if i exceeds numElements.
     *
     * @param i  the number of elements to discard from the front of the array
     * @throws MathIllegalArgumentException if i is greater than numElements.
     */
    public synchronized void discardFrontElements(int i)
            throws MathIllegalArgumentException {
        discardExtremeElements(i, true);
    }

    /**
     * Discards the <code>i</code> last elements of the array.  For example,
     * if the array contains the elements 1,2,3,4, invoking
     * <code>discardMostRecentElements(2)</code> will cause the last two elements
     * to be discarded, leaving 1,2 in the array.  Throws illegalArgumentException
     * if i exceeds numElements.
     *
     * @param i  the number of elements to discard from the end of the array
     * @throws MathIllegalArgumentException if i is greater than numElements.
     */
    public synchronized void discardMostRecentElements(int i)
            throws MathIllegalArgumentException {
        discardExtremeElements(i,false);
    }

    /**
     * Discards the <code>i</code> first or last elements of the array,
     * depending on the value of <code>front</code>.
     * For example, if the array contains the elements 1,2,3,4, invoking
     * <code>discardExtremeElements(2,false)</code> will cause the last two elements
     * to be discarded, leaving 1,2 in the array.
     * For example, if the array contains the elements 1,2,3,4, invoking
     * <code>discardExtremeElements(2,true)</code> will cause the first two elements
     * to be discarded, leaving 3,4 in the array.
     * Throws illegalArgumentException
     * if i exceeds numElements.
     *
     * @param i  the number of elements to discard from the front/end of the array
     * @param front true if elements are to be discarded from the front
     * of the array, false if elements are to be discarded from the end
     * of the array
     * @throws MathIllegalArgumentException if i is greater than numElements.
     * @since 2.0
     */
    private synchronized void discardExtremeElements(int i,
                                                     boolean front)
            throws MathIllegalArgumentException {
        if (i > numElements) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.TOO_MANY_ELEMENTS_TO_DISCARD_FROM_ARRAY,
                    i, numElements);
        } else if (i < 0) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.CANNOT_DISCARD_NEGATIVE_NUMBER_OF_ELEMENTS,
                    i);
        } else {
            // "Subtract" this number of discarded from numElements
            numElements -= i;
            if (front) {
                startIndex += i;
            }
        }
        if (shouldContract()) {
            contract();
        }
    }

    /**
     * Expands the internal storage array using the expansion factor.
     * <p>
     * if <code>expansionMode</code> is set to MULTIPLICATIVE,
     * the new array size will be <code>internalArray.length * expansionFactor.</code>
     * If <code>expansionMode</code> is set to ADDITIVE,  the length
     * after expansion will be <code>internalArray.length + expansionFactor</code>
     * </p>
     */
    protected synchronized void expand() {
        // notice the use of FastMath.ceil(), this guarantees that we will always
        // have an array of at least currentSize + 1.   Assume that the
        // current initial capacity is 1 and the expansion factor
        // is 1.000000000000000001.  The newly calculated size will be
        // rounded up to 2 after the multiplication is performed.
        int newSize;
        if (expansionMode == ExpansionMode.MULTIPLICATIVE) {
            newSize = (int) FastMath.ceil(internalArray.length * expansionFactor);
        } else {
            newSize = (int) (internalArray.length + FastMath.round(expansionFactor));
        }
        final BigDecimal[] tempArray = new BigDecimal[newSize];

        // Copy and swap
        System.arraycopy(internalArray, 0, tempArray, 0, internalArray.length);
        internalArray = tempArray;
    }

    /**
     * Expands the internal storage array to the specified size.
     *
     * @param size Size of the new internal storage array.
     */
    private synchronized void expandTo(int size) {
        final BigDecimal[] tempArray = new BigDecimal[size];
        // Copy and swap
        System.arraycopy(internalArray, 0, tempArray, 0, internalArray.length);
        internalArray = tempArray;
    }

    /**
     * The contraction criterion defines when the internal array will contract
     * to store only the number of elements in the element array.
     * If  the <code>expansionMode</code> is <code>MULTIPLICATIVE</code>,
     * contraction is triggered when the ratio between storage array length
     * and <code>numElements</code> exceeds <code>contractionFactor</code>.
     * If the <code>expansionMode</code> is <code>ADDITIVE</code>, the
     * number of excess storage locations is compared to
     * <code>contractionFactor.</code>
     *
     * @return the contraction criterion used to reclaim memory.
     * @since 3.1
     */
    public double getContractionCriterion() {
        return contractionCriterion;
    }

    /**
     * Returns the element at the specified index
     *
     * @param index index to fetch a value from
     * @return value stored at the specified index
     * @throws ArrayIndexOutOfBoundsException if <code>index</code> is less than
     * zero or is greater than <code>getNumElements() - 1</code>.
     */
    public synchronized BigDecimal getElement(int index) {
        if (index >= numElements) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else if (index >= 0) {
            return internalArray[startIndex + index];
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    /**
     * Returns a BigDecimal array containing the elements of this
     * <code>ResizableDecimalArray</code>.  This method returns a copy, not a
     * reference to the underlying array, so that changes made to the returned
     *  array have no effect on this <code>ResizableDecimalArray.</code>
     * @return the BigDecimal array.
     */
    public synchronized BigDecimal[] getElements() {
        final BigDecimal[] elementArray = new BigDecimal[numElements];
        System.arraycopy(internalArray, startIndex, elementArray, 0, numElements);
        return elementArray;
    }

    /**
     * The expansion factor controls the size of a new array when an array
     * needs to be expanded.  The <code>expansionMode</code>
     * determines whether the size of the array is multiplied by the
     * <code>expansionFactor</code> (MULTIPLICATIVE) or if
     * the expansion is additive (ADDITIVE -- <code>expansionFactor</code>
     * storage locations added).  The default <code>expansionMode</code> is
     * MULTIPLICATIVE and the default <code>expansionFactor</code>
     * is 2.0.
     *
     * @return the expansion factor of this expandable double array
     */
    public double getExpansionFactor() {
        return expansionFactor;
    }

    /**
     * Gets the currently allocated size of the internal data structure used
     * for storing elements.
     * This is not to be confused with {@link #getNumElements() the number of
     * elements actually stored}.
     *
     * @return the length of the internal array.
     */
    public int getCapacity() {
        return internalArray.length;
    }

    /**
     * Returns the number of elements currently in the array.  Please note
     * that this is different from the length of the internal storage array.
     *
     * @return the number of elements.
     */
    public synchronized int getNumElements() {
        return numElements;
    }

    /**
     * Provides <em>direct</em> access to the internal storage array.
     * Please note that this method returns a reference to this object's
     * storage array, not a copy.
     * <br/>
     * To correctly address elements of the array, the "start index" is
     * required (available via the {@link #getStartIndex() getStartIndex}
     * method.
     * <br/>
     * This method should only be used to avoid copying the internal array.
     * The returned value <em>must</em> be used for reading only; other
     * uses could lead to this object becoming inconsistent.
     * <br/>
     * The {@link #getElements} method has no such limitation since it
     * returns a copy of this array's addressable elements.
     *
     * @return the internal storage array used by this object.
     */
    protected BigDecimal[] getArrayRef() {
        return internalArray;
    }

    /**
     * Returns the "start index" of the internal array.
     * This index is the position of the first addressable element in the
     * internal storage array.
     * The addressable elements in the array are at indices contained in
     * the interval [{@link #getStartIndex()},
     *               {@link #getStartIndex()} + {@link #getNumElements()} - 1].
     *
     * @return the start index.
     * @since 3.1
     */
    protected int getStartIndex() {
        return startIndex;
    }

    /**
     * Performs an operation on the addressable elements of the array.
     *
     * @param f Function to be applied on this array.
     * @return the result.
     * @since 3.1

    public double compute(MathArrays.Function f) {
        final double[] array;
        final int start;
        final int num;
        synchronized(this) {
            array = internalArray;
            start = startIndex;
            num   = numElements;
        }
        return f.evaluate(array, start, num);
    }
    */

    /**
     * Sets the element at the specified index.  If the specified index is greater than
     * <code>getNumElements() - 1</code>, the <code>numElements</code> property
     * is increased to <code>index +1</code> and additional storage is allocated
     * (if necessary) for the new element and all  (uninitialized) elements
     * between the new element and the previous end of the array).
     *
     * @param index index to store a value in
     * @param value value to store at the specified index
     * @throws ArrayIndexOutOfBoundsException if {@code index < 0}.
     */
    public synchronized void setElement(int index, BigDecimal value) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (index + 1 > numElements) {
            numElements = index + 1;
        }
        if ((startIndex + index) >= internalArray.length) {
            expandTo(startIndex + (index + 1));
        }
        internalArray[startIndex + index] = value;
    }

    /**
     * This function allows you to control the number of elements contained
     * in this array, and can be used to "throw out" the last n values in an
     * array. This function will also expand the internal array as needed.
     *
     * @param i a new number of elements
     * @throws MathIllegalArgumentException if <code>i</code> is negative.
     */
    public synchronized void setNumElements(int i)
            throws MathIllegalArgumentException {
        // If index is negative thrown an error.
        if (i < 0) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.INDEX_NOT_POSITIVE,
                    i);
        }

        // Test the new num elements, check to see if the array needs to be
        // expanded to accommodate this new number of elements.
        final int newSize = startIndex + i;
        if (newSize > internalArray.length) {
            expandTo(newSize);
        }

        // Set the new number of elements to new value.
        numElements = i;
    }

    /**
     * Returns true if the internal storage array has too many unused
     * storage positions.
     *
     * @return true if array satisfies the contraction criteria
     */
    private synchronized boolean shouldContract() {
        if (expansionMode == ExpansionMode.MULTIPLICATIVE) {
            return (internalArray.length / ((float) numElements)) > contractionCriterion;
        } else {
            return (internalArray.length - numElements) > contractionCriterion;
        }
    }

    /**
     * <p>Copies source to dest, so that dest contains links to the same Bigdecimals as the source.
     * It is safe because BigDecimals are immutable. Does not contract before the copy.</p>
     *
     * <p>Obtains synchronization locks on both source and dest
     * (in that order) before performing the copy.</p>
     *
     * <p>Neither source nor dest may be null; otherwise a {@link NullArgumentException}
     * is thrown</p>
     *
     * @param source ResizableDecimalArray to copy
     * @param dest ResizableDecimalArray to replace with a copy of the source array
     * @exception NullArgumentException if either source or dest is null
     *
     */
    public static void copy(ResizableDecimalArray source,
                            ResizableDecimalArray dest)
            throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        synchronized(source) {
            synchronized(dest) {
                dest.contractionCriterion = source.contractionCriterion;
                dest.expansionFactor = source.expansionFactor;
                dest.expansionMode = source.expansionMode;
                dest.internalArray = new BigDecimal[source.internalArray.length];
                System.arraycopy(source.internalArray, 0, dest.internalArray,
                        0, dest.internalArray.length);
                dest.numElements = source.numElements;
                dest.startIndex = source.startIndex;
            }
        }
    }

    /**
     * Returns a copy of the ResizableDecimalArray, which contains links to the same Bigdecimals as the source.
     * It is safe because BigDecimals are immutable.  Does not contract before
     * the copy, so the returned object is an exact copy of this.
     *
     * @return a new ResizableDecimalArray with the same data and configuration
     * properties as this
     */
    public synchronized ResizableDecimalArray copy() {
        final ResizableDecimalArray result = new ResizableDecimalArray();
        copy(this, result);
        return result;
    }

    /**
     * Returns true iff object is a ResizableDecimalArray with the same properties
     * as this and an identical internal storage array.
     *
     * @param object object to be compared for equality with this
     * @return true iff object is a ResizableDoubleArray with the same data and
     * properties as this
     */
    @Override
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
        if (!(object instanceof ResizableDecimalArray)) {
            return false;
        }
        synchronized(this) {
            synchronized(object) {
                final ResizableDecimalArray other = (ResizableDecimalArray) object;
                boolean result = other.contractionCriterion == contractionCriterion;
                result = result && (other.expansionFactor == expansionFactor);
                result = result && (other.expansionMode == expansionMode);
                result = result && (other.numElements == numElements);
                result = result && (other.startIndex == startIndex);
                return result && Arrays.equals(internalArray, other.internalArray);
            }
        }
    }

    /**
     * Returns a hash code consistent with equals.
     *
     * @return the hash code representing this {@code ResizableDoubleArray}.
     * @since 2.0
     */
    @Override
    public synchronized int hashCode() {
        final int[] hashData = new int[6];
        hashData[0] = Double.valueOf(expansionFactor).hashCode();
        hashData[1] = Double.valueOf(contractionCriterion).hashCode();
        hashData[2] = expansionMode.hashCode();
        hashData[3] = Arrays.hashCode(internalArray);
        hashData[4] = numElements;
        hashData[5] = startIndex;
        return Arrays.hashCode(hashData);
    }
}
