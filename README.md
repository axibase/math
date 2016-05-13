# Overview

BigDecimal implementation of [Apache Commons Math](https://commons.apache.org/proper/commons-math/userguide/stat.html) Descriptive Statistics and Summary Statistics.

See JavaDocs for implementation details.

# License

The project is released under version 2.0 of the [Apache License](./LICENSE.md).

# Examples

## DescriptiveStatistics

### BigDecimal


### double

```java
// Get a DescriptiveStatistics instance
DescriptiveStatistics stats = new DescriptiveStatistics();

// Add the data from the array
for( int i = 0; i < inputArray.length; i++) {
        stats.addValue(inputArray[i]);
}

// Compute some statistics
double mean = stats.getMean();
double std = stats.getStandardDeviation();
double median = stats.getPercentile(50);
```

## SummaryStatistics

### BigDecimal

### double


