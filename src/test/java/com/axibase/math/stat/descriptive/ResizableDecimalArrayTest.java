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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;

/**
 * test
 */
public class ResizableDecimalArrayTest extends DecimalArrayAbstractTest{

    @SuppressWarnings("deprecation")
    @Test
    public void testConstructors() {
        double defaultExpansionFactor = 2.0d;
        double defaultContractionCriteria = 2.5;
        ResizableDecimalArray.ExpansionMode defaultMode = ResizableDecimalArray.ExpansionMode.MULTIPLICATIVE;

        ResizableDecimalArray testDa = new ResizableDecimalArray(2);
        Assert.assertEquals(0, testDa.getNumElements());
        Assert.assertEquals(2, testDa.getCapacity());
        Assert.assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        Assert.assertEquals(defaultContractionCriteria, testDa.getContractionCriterion(), 0);
        try {
            da = new ResizableDecimalArray(-1);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        testDa = new ResizableDecimalArray((BigDecimal[]) null);
        Assert.assertEquals(0, testDa.getNumElements());

        BigDecimal[] initialArray = toBD(new double[] { 0, 1, 2 });
        testDa = new ResizableDecimalArray(initialArray);
        Assert.assertEquals(3, testDa.getNumElements());

        testDa = new ResizableDecimalArray(2, 2.0);
        Assert.assertEquals(0, testDa.getNumElements());
        Assert.assertEquals(2, testDa.getCapacity());
        Assert.assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        Assert.assertEquals(defaultContractionCriteria, testDa.getContractionCriterion(), 0);

        try {
            da = new ResizableDecimalArray(2, 0.5);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        testDa = new ResizableDecimalArray(2, 3.0);
        Assert.assertEquals(3.0d, testDa.getExpansionFactor(), 0);
        Assert.assertEquals(3.5f, testDa.getContractionCriterion(), 0);

        testDa = new ResizableDecimalArray(2, 2.0, 3.0);
        Assert.assertEquals(0, testDa.getNumElements());
        Assert.assertEquals(2, testDa.getCapacity());
        Assert.assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        Assert.assertEquals(3.0f, testDa.getContractionCriterion(), 0);

        try {
            da = new ResizableDecimalArray(2, 2.0, 1.5);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }

        testDa = new ResizableDecimalArray(2, 2.0, 3.0, ResizableDecimalArray.ExpansionMode.ADDITIVE);
        Assert.assertEquals(0, testDa.getNumElements());
        Assert.assertEquals(2, testDa.getCapacity());
        Assert.assertEquals(defaultExpansionFactor, testDa.getExpansionFactor(), 0);
        Assert.assertEquals(3.0f, testDa.getContractionCriterion(), 0);

        // Copy constructor
        testDa = new ResizableDecimalArray(2, 2.0, 3.0, ResizableDecimalArray.ExpansionMode.ADDITIVE);
        testDa.addElement(new BigDecimal(2.0));
        testDa.addElement(new BigDecimal(3.2));
        ResizableDecimalArray copyDa = new ResizableDecimalArray(testDa);
        Assert.assertEquals(copyDa, testDa);
        Assert.assertEquals(testDa, copyDa);

        // JIRA: MATH-1252
        final BigDecimal[] values = toBD(new double[] {1});
        testDa = new ResizableDecimalArray(values);
        Assert.assertArrayEquals(values, testDa.getElements());
        Assert.assertEquals(1, testDa.getNumElements());
        Assert.assertEquals(new BigDecimal(1), testDa.getElement(0));

    }

    @Test
    public void testSetElementArbitraryExpansion1() {

        // MULTIPLICATIVE_MODE
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(4.0));
        da.addElement(new BigDecimal(6.0));
        da.setElement(1, new BigDecimal(3.0));

        // Expand the array arbitrarily to 1000 items
        da.setElement(1000, new BigDecimal(3.4));

        Assert.assertEquals( "The number of elements should now be 1001, it isn't",
                da.getNumElements(), 1001);

        Assert.assertEquals( "Uninitialized Elements are default value of null, index 766 wasn't", null,
                da.getElement(766));

        Assert.assertEquals( "The 1000th index should be 3.4, it isn't", new BigDecimal(3.4), da.getElement(1000));

        Assert.assertEquals( "The 0th index should be 2.0, it isn't", new BigDecimal(2.0), da.getElement(0));
    }

    @Test
    public void testSetElementArbitraryExpansion2() {
        // Make sure numElements and expansion work correctly for expansion boundary cases
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(4.0));
        da.addElement(new BigDecimal(6.0));
        Assert.assertEquals(16, da.getCapacity());
        Assert.assertEquals(3, da.getNumElements());
        da.setElement(3, new BigDecimal(7.0));
        Assert.assertEquals(16, da.getCapacity());
        Assert.assertEquals(4, da.getNumElements());
        da.setElement(10, new BigDecimal(10.0));
        Assert.assertEquals(16, da.getCapacity());
        Assert.assertEquals(11, da.getNumElements());
        da.setElement(9, new BigDecimal(10.0));
        Assert.assertEquals(16, da.getCapacity());
        Assert.assertEquals(11, da.getNumElements());

        try {
            da.setElement(-2, new BigDecimal(3));
            Assert.fail("Expecting ArrayIndexOutOfBoundsException for negative index");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }

        // ADDITIVE_MODE

        ResizableDecimalArray testDa = new ResizableDecimalArray(2, 2.0, 3.0, ResizableDecimalArray.ExpansionMode.ADDITIVE);
        Assert.assertEquals(2, testDa.getCapacity());
        testDa.addElement(new BigDecimal(1d));
        testDa.addElement(new BigDecimal(1d));
        Assert.assertEquals(2, testDa.getCapacity());
        testDa.addElement(new BigDecimal(1d));
        Assert.assertEquals(4, testDa.getCapacity());
    }

    @Override
    @Test
    public void testAdd1000() {
        super.testAdd1000();
        Assert.assertEquals("Internal Storage length should be 1024 if we started out with initial capacity of " +
                        "16 and an expansion factor of 2.0",
                1024, da.getCapacity());
    }

    @Test
    public void testAddElements() {
        ResizableDecimalArray testDa = new ResizableDecimalArray();

        // MULTIPLICATIVE_MODE
        testDa.addElements(toBD(new double[] {4, 5, 6}));
        Assert.assertEquals(3, testDa.getNumElements(), 0);
        Assert.assertEquals(new BigDecimal(4), testDa.getElement(0));
        Assert.assertEquals(new BigDecimal(5), testDa.getElement(1));
        Assert.assertEquals(new BigDecimal(6), testDa.getElement(2));

        testDa.addElements(toBD(new double[] {4, 5, 6}));
        Assert.assertEquals(6, testDa.getNumElements());

        // ADDITIVE_MODE  (x's are occupied storage locations, 0's are open)
        testDa = new ResizableDecimalArray(2, 2.0, 2.5, ResizableDecimalArray.ExpansionMode.ADDITIVE);
        Assert.assertEquals(2, testDa.getCapacity());
        testDa.addElements(toBD(new double[] { 1d })); // x,0
        testDa.addElements(toBD(new double[] { 2d })); // x,x
        testDa.addElements(toBD(new double[] { 3d })); // x,x,x,0 -- expanded
        Assert.assertEquals(new BigDecimal(1d), testDa.getElement(0));
        Assert.assertEquals(new BigDecimal(2d), testDa.getElement(1));
        Assert.assertEquals(new BigDecimal(3d), testDa.getElement(2));
        Assert.assertEquals(4, testDa.getCapacity());  // x,x,x,0
        Assert.assertEquals(3, testDa.getNumElements());
    }

    @Override
    @Test
    public void testAddElementRolling() {
        super.testAddElementRolling();

        // MULTIPLICATIVE_MODE
        da.clear();
        da.addElement(new BigDecimal(1));
        da.addElement(new BigDecimal(2));
        da.addElementRolling(new BigDecimal(3));
        Assert.assertEquals(new BigDecimal(3), da.getElement(1));
        da.addElementRolling(new BigDecimal(4));
        Assert.assertEquals(new BigDecimal(3), da.getElement(0));
        Assert.assertEquals(new BigDecimal(4), da.getElement(1));
        da.addElement(new BigDecimal(5));
        Assert.assertEquals(new BigDecimal(5), da.getElement(2));
        da.addElementRolling(new BigDecimal(6));
        Assert.assertEquals(new BigDecimal(4), da.getElement(0));
        Assert.assertEquals(new BigDecimal(5), da.getElement(1));
        Assert.assertEquals(new BigDecimal(6), da.getElement(2));

        // ADDITIVE_MODE  (x's are occupied storage locations, 0's are open)
        ResizableDecimalArray testDa = new ResizableDecimalArray(2, 2.0, 2.5, ResizableDecimalArray.ExpansionMode.ADDITIVE);
        Assert.assertEquals(2, testDa.getCapacity());
        testDa.addElement(new BigDecimal(1d)); // x,0
        testDa.addElement(new BigDecimal(2d)); // x,x
        testDa.addElement(new BigDecimal(3d)); // x,x,x,0 -- expanded
        Assert.assertEquals(new BigDecimal(1d), testDa.getElement(0));
        Assert.assertEquals(new BigDecimal(2d), testDa.getElement(1));
        Assert.assertEquals(new BigDecimal(3d), testDa.getElement(2));
        Assert.assertEquals(4, testDa.getCapacity());  // x,x,x,0
        Assert.assertEquals(3, testDa.getNumElements());
        testDa.addElementRolling(new BigDecimal(4d));
        Assert.assertEquals(new BigDecimal(2d), testDa.getElement(0));
        Assert.assertEquals(new BigDecimal(3d), testDa.getElement(1));
        Assert.assertEquals(new BigDecimal(4d), testDa.getElement(2));
        Assert.assertEquals(4, testDa.getCapacity());  // 0,x,x,x
        Assert.assertEquals(3, testDa.getNumElements());
        testDa.addElementRolling(new BigDecimal(5d));   // 0,0,x,x,x,0 -- time to contract
        Assert.assertEquals(new BigDecimal(3d), testDa.getElement(0));
        Assert.assertEquals(new BigDecimal(4d), testDa.getElement(1));
        Assert.assertEquals(new BigDecimal(5d), testDa.getElement(2));
        Assert.assertEquals(4, testDa.getCapacity());  // contracted -- x,x,x,0
        Assert.assertEquals(3, testDa.getNumElements());
        try {
            testDa.getElement(4);
            Assert.fail("Expecting ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }
        try {
            testDa.getElement(-1);
            Assert.fail("Expecting ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        }
    }

    @Test
    public void testSetNumberOfElements() {
        da.addElement( new BigDecimal(1.0) );
        da.addElement( new BigDecimal(1.0) );
        da.addElement( new BigDecimal(1.0) );
        da.addElement( new BigDecimal(1.0) );
        da.addElement( new BigDecimal(1.0) );
        da.addElement( new BigDecimal(1.0) );
        Assert.assertEquals( "Number of elements should equal 6", da.getNumElements(), 6);

        da.setNumElements( 3 );
        Assert.assertEquals( "Number of elements should equal 3", da.getNumElements(), 3);

        try {
            da.setNumElements( -3 );
            Assert.fail( "Setting number of elements to negative should've thrown an exception");
        } catch( IllegalArgumentException iae ) {
        }

        da.setNumElements(1024);
        Assert.assertEquals( "Number of elements should now be 1024", da.getNumElements(), 1024);
        Assert.assertEquals( "Element 453 should be a default value of null", null, da.getElement( 453 ));

    }

    @Test
    public void testWithInitialCapacity() {

        ResizableDecimalArray eDA2 = new ResizableDecimalArray(2);
        Assert.assertEquals("Initial number of elements should be 0", 0, eDA2.getNumElements());

        Random generator = new Random();
        final int iterations = 100 + generator.nextInt(500);

        for( int i = 0; i < iterations; i++) {
            eDA2.addElement( new BigDecimal(i) );
        }

        Assert.assertEquals("Number of elements should be equal to " + iterations, iterations, eDA2.getNumElements());

        eDA2.addElement( new BigDecimal(2.0) );

        Assert.assertEquals("Number of elements should be equals to " + (iterations + 1),
                iterations + 1 , eDA2.getNumElements() );
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testWithInitialCapacityAndExpansionFactor() {

        ResizableDecimalArray eDA3 = new ResizableDecimalArray(3, 3.0, 3.5);
        Assert.assertEquals("Initial number of elements should be 0", 0, eDA3.getNumElements() );

        Random generator = new Random();
        final int iterations = 100 + generator.nextInt(3000);

        for( int i = 0; i < iterations; i++) {
            eDA3.addElement( new BigDecimal(i) );
        }

        Assert.assertEquals("Number of elements should be equal to " + iterations, iterations,eDA3.getNumElements());

        eDA3.addElement( new BigDecimal(2.0) );

        Assert.assertEquals("Number of elements should be equals to " + (iterations +1),
                iterations +1, eDA3.getNumElements() );

        Assert.assertEquals("Expansion factor should equal 3.0", 3.0d, eDA3.getExpansionFactor(), Double.MIN_VALUE);
    }

    @Test
    public void testDiscard() {
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        Assert.assertEquals( "Number of elements should be 11", 11, da.getNumElements());

        da.discardFrontElements(5);
        Assert.assertEquals( "Number of elements should be 6", 6, da.getNumElements());

        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        Assert.assertEquals( "Number of elements should be 10", 10, da.getNumElements());

        da.discardMostRecentElements(2);
        Assert.assertEquals( "Number of elements should be 8", 8, da.getNumElements());

        try {
            da.discardFrontElements(-1);
            Assert.fail( "Trying to discard a negative number of element is not allowed");
        } catch( Exception e ){
        }

        try {
            da.discardMostRecentElements(-1);
            Assert.fail( "Trying to discard a negative number of element is not allowed");
        } catch( Exception e ){
        }

        try {
            da.discardFrontElements( 10000 );
            Assert.fail( "You can't discard more elements than the array contains");
        } catch( Exception e ){
        }

        try {
            da.discardMostRecentElements( 10000 );
            Assert.fail( "You can't discard more elements than the array contains");
        } catch( Exception e ){
        }

    }

    @Test
    public void testSubstitute() {

        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        da.addElement(new BigDecimal(2.0));
        Assert.assertEquals( "Number of elements should be 11", 11, da.getNumElements());

        da.substituteMostRecentElement(new BigDecimal(24));

        Assert.assertEquals( "Number of elements should be 11", 11, da.getNumElements());

        try {
            da.discardMostRecentElements(10);
        } catch( Exception e ){
            Assert.fail( "Trying to discard a negative number of element is not allowed");
        }

        da.substituteMostRecentElement(new BigDecimal(24));

        Assert.assertEquals( "Number of elements should be 1", 1, da.getNumElements());

    }

    /*
    @SuppressWarnings("deprecation")
    @Test
    public void testMutators() {
        da.setContractionCriteria(10f);
        Assert.assertEquals(10f, da.getContractionCriterion(), 0);
        da.setExpansionFactor(8f);
        Assert.assertEquals(8f, da.getExpansionFactor(), 0);
        try {
            da.setExpansionFactor(11f);  // greater than contractionCriteria
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        da.setExpansionMode(
                new ResizableDecimalArray.ADDITIVE_MODE);
        Assert.assertEquals(new ResizableDecimalArray.ADDITIVE_MODE,
                da.getExpansionMode());
        try {
            da.setExpansionMode(-1);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    */

    @Test
    public void testEqualsAndHashCode() throws Exception {

        // Wrong type
        ResizableDecimalArray first = new ResizableDecimalArray();
        Double other = new Double(2);
        Assert.assertFalse(first.equals(other));

        // Null
        other = null;
        Assert.assertFalse(first.equals(other));

        // Reflexive
        Assert.assertTrue(first.equals(first));

        // Argumentless constructor
        ResizableDecimalArray second = new ResizableDecimalArray();
        verifyEquality(first, second);

        // Equals iff same data, same properties
        ResizableDecimalArray third = new ResizableDecimalArray(3, 2.0, 2.0);
        verifyInequality(third, first);
        ResizableDecimalArray fourth = new ResizableDecimalArray(3, 2.0, 2.0);
        ResizableDecimalArray fifth = new ResizableDecimalArray(2, 2.0, 2.0);
        verifyEquality(third, fourth);
        verifyInequality(third, fifth);
        third.addElement(new BigDecimal(4.1));
        third.addElement(new BigDecimal(4.2));
        third.addElement(new BigDecimal(4.3));
        fourth.addElement(new BigDecimal(4.1));
        fourth.addElement(new BigDecimal(4.2));
        fourth.addElement(new BigDecimal(4.3));
        verifyEquality(third, fourth);

        // expand
        fourth.addElement(new BigDecimal(4.4));
        verifyInequality(third, fourth);
        third.addElement(new BigDecimal(4.4));
        verifyEquality(third, fourth);
        fourth.addElement(new BigDecimal(4.4));
        verifyInequality(third, fourth);
        third.addElement(new BigDecimal(4.4));
        verifyEquality(third, fourth);
        fourth.addElementRolling(new BigDecimal(4.5));
        third.addElementRolling(new BigDecimal(4.5));
        verifyEquality(third, fourth);

        // discard
        third.discardFrontElements(1);
        verifyInequality(third, fourth);
        fourth.discardFrontElements(1);
        verifyEquality(third, fourth);

        // discard recent
        third.discardMostRecentElements(2);
        fourth.discardMostRecentElements(2);
        verifyEquality(third, fourth);

        // wrong order
        third.addElement(new BigDecimal(18));
        fourth.addElement(new BigDecimal(17));
        third.addElement(new BigDecimal(17));
        fourth.addElement(new BigDecimal(18));
        verifyInequality(third, fourth);

        // copy
        ResizableDecimalArray.copy(fourth, fifth);
        verifyEquality(fourth, fifth);

        // Copy constructor
        verifyEquality(fourth, new ResizableDecimalArray(fourth));

        // Instance copy
        verifyEquality(fourth, fourth.copy());

    }

    @Test
    public void testGetArrayRef() {
        final ResizableDecimalArray a = new ResizableDecimalArray();

        // Modify "a" through the public API.
        final int index = 20;
        final BigDecimal v1 = new BigDecimal(1.2);
        a.setElement(index, v1);

        // Modify the internal storage through the protected API.
        final BigDecimal v2 = new BigDecimal(1.2 + 3.4);
        final BigDecimal[] aInternalArray = a.getArrayRef();
        aInternalArray[a.getStartIndex() + index] = v2;

        Assert.assertEquals(v2, a.getElement(index));
    }

    /*
    @Test
    public void testCompute() {
        final ResizableDecimalArray a = new ResizableDecimalArray();
        final int getMax = 20;
        for (int i = 1; i <= getMax; i++) {
            a.setElement(i, new BigDecimal(i));
        }

        final MathArrays.Function add = new MathArrays.Function() {
            public double evaluate(BigDecimal[] a, int index, int num) {
                double sum = 0;
                final int getMax = index + num;
                for (int i = index; i < getMax; i++) {
                    sum += a[i];
                }
                return sum;
            }
            public double evaluate(BigDecimal[] a) {
                return evaluate(a, 0, a.length);
            }
        };

        final double sum = a.compute(add);
        Assert.assertEquals(0.5 * getMax * (getMax + 1), sum, 0);
    }
    */

    private void verifyEquality(ResizableDecimalArray a, ResizableDecimalArray b) {
        Assert.assertTrue(b.equals(a));
        Assert.assertTrue(a.equals(b));
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }

    private void verifyInequality(ResizableDecimalArray a, ResizableDecimalArray b) {
        Assert.assertFalse(b.equals(a));
        Assert.assertFalse(a.equals(b));
        Assert.assertFalse(a.hashCode() == b.hashCode());
    }

    private BigDecimal[] toBD(double[] doubles) {
        BigDecimal[] bD = new BigDecimal[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            bD[i] = new BigDecimal(doubles[i]);
        }
        return bD;
    }

}