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

import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * test
 */
public class DescriptiveStatisticsTest {

    BigDecimal[]  ticket_2353 = {
            new BigDecimal("0.02"),
            new BigDecimal("0.02"),
            new BigDecimal("0.02"),
            new BigDecimal("0.02"),
            new BigDecimal("0.3"),
            new BigDecimal("0.3"),
            new BigDecimal("0.3"),
    };


    @Test
    public void testPercentile() throws Exception {
        DescriptiveStatistics calculator = new DescriptiveStatistics(ticket_2353);
        System.out.println("50 getPercentile = " + calculator.getPercentile(new BigDecimal("0.5")));
        System.out.println("60 getPercentile = " + calculator.getPercentile(new BigDecimal("0.6")));
        System.out.println("70 getPercentile = " + calculator.getPercentile(new BigDecimal("0.7")));
    }

    @Test
    public void example() {
        BigDecimal[] numbers = {new BigDecimal("1.3"), new BigDecimal("0.3"), new BigDecimal("0.1")};

        // Get a DescriptiveStatistics instance with the default precision of 16 digits
        DescriptiveStatistics stats = new DescriptiveStatistics(numbers);

        // Compute some statistics
        long size = stats.getN();
        BigDecimal max = stats.getMax();
        BigDecimal median = stats.getPercentile(new BigDecimal("50"));

        BigDecimal mean = stats.getMean();
        BigDecimal std = stats.getStandardDeviation();
        BigDecimal sum = stats.getSum();

        System.out.println("size = " + size);
        System.out.println("max = " + max);
        System.out.println("median = " + median);
        System.out.println("mean = " + mean);
        System.out.println("std = " + std);
        System.out.println("sum = " + sum);

        // Compute standard deviation with 256 digit precision
        MathContext context = new MathContext(256, RoundingMode.HALF_UP);
        System.out.println(stats.getStandardDeviation(context));
    }
}