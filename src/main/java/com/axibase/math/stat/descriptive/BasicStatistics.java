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

/**
 * The interface enumerates some basic univariate statistics.
 * The classes DescriptiveStatistics and SummaryStatistics implement this interface in different ways.
 * The DescriptiveStatistics recalculates a statistics for stored data from scratch each time
 * the appropriate method is called.
 * The SummaryStatistics just updates cached values of statistics for each new data point.
 * The VarianceCalculator can use any implementation of this interface as an "engine" to calculate data variance.
 * So this interface is introduced to let the VarianceCalculator make its job with the same code for
 * both DescriptiveStatistics and SummaryStatistics.
 */
public interface BasicStatistics {
    long getN();
    BigDecimal getMax();
    BigDecimal getMin();
    BigDecimal getSum();
    BigDecimal getSumsq();
}
