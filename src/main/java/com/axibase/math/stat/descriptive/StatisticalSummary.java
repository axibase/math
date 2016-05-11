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

/**
 * The interface extends BasicStatistics by common univariate statistics.
 * It is an analog of the StatisticalSummary interface from
 * the org.apache.commons.math4.stat.descriptive package for BigDecimal numbers.
 * It is implemented by the DescriptiveStatistics and SummaryStatistics classes.
 * As the methods of this class may return decimal numbers with infinite number of digits,
 * so the MathContext should be used for rounding.
 * If a method hasn't a MathContext argument, then implementation will use its own MathContext.
 */
public interface StatisticalSummary extends BasicStatistics {

    BigDecimal getMean();
    BigDecimal getMean(MathContext meanContext);

    BigDecimal getVariance();
    BigDecimal getVariance(MathContext varianceContext);
    BigDecimal getPopulationVariance();
    BigDecimal getPopulationVariance(MathContext varianceContext);

    BigDecimal getStandardDeviation();
    BigDecimal getStandardDeviation(MathContext stdDevContext);
    BigDecimal getPopulationStandardDeviation();
    BigDecimal getPopulationStandardDeviation(MathContext stdDevContext);

}
