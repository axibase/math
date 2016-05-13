# Overview

BigDecimal implementation of [Apache Commons Math](https://commons.apache.org/proper/commons-math/userguide/stat.html) Descriptive Statistics and Summary Statistics.

See JavaDocs for implementation details.

# License

The project is released under version 2.0 of the [Apache License](LICENCE.md).

# Examples

## DescriptiveStatistics

### BigDecimal

```java
// Let we have a BigDecimal array.
BigDecimal[] numbers = {new BigDecimal("0.01"), new BigDecimal("0.2"), new BigDecimal("-1.3")};

// Get a DescriptiveStatistics instance which stores the numbers.
DescriptiveStatistics stats = new DescriptiveStatistics(numbers);

// Compute some statistics
BigDecimal cardinality = stats.getN();
BigDecimal max = stats.getMax();
BigDecimal median = stats.getPercentile(50);

// Some of statistics can have infinite number of digits. So we should use MathContext to round them.
// Let round result to 16 digits.
MathContext rounding = new MathContext(16, RoundingMode.HALF_UP);
BigDecimal mean = stats.getMean(rounding);
BigDecimal std = stats.getStandardDeviation();
```


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


