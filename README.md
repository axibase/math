# Overview

BigDecimal implementation of [Apache Commons Math](https://commons.apache.org/proper/commons-math/userguide/stat.html) Descriptive Statistics and Summary Statistics.

See JavaDocs for implementation details.

The following methods from Apache DescriptiveStatistics are _not_ implemented in this release:

- getGeometricMean()
- getKurtosis()
- getQuadraticMean()
- getSkewness()

The MathContext object should be used for rounding for the following methods since they may return an infitinite number of decimal digits:

- getMean()
- getVariance()
- getStandardDeviation()

# License

The project is released under version 2.0 of the [Apache License](LICENCE.md).

# Examples

## DescriptiveStatistics

### BigDecimal

```java
import axibase.math.stat.descriptive;

BigDecimal[] numbers = {new BigDecimal("1.3"), new BigDecimal("0.3"), new BigDecimal("0.1")};

// Get a DescriptiveStatistics instance with the default precision of 16 digits
DescriptiveStatistics stats = new DescriptiveStatistics(numbers);

// Compute some statistics
BigDecimal size = stats.getN();
BigDecimal max = stats.getMax();
BigDecimal median = stats.getPercentile(50);

BigDecimal mean = stats.getMean();
BigDecimal std = stats.getStandardDeviation();
BigDecimal sum = stats.getSum();
```

```
size = 
max = 
median = 
mean = 
std = 
sum =
```

```java
// Compute standard deviation with 256 digit precision
MathContext context = 
BigDecimal std = stats.getStandardDeviation(context);
```

```
std-256=
```


### double

```java
import org.apache.commons.math4.stat.descriptive;

double[] numbers = {1.3, 0.3, 0.1};

// Get a DescriptiveStatistics instance
DescriptiveStatistics stats = new DescriptiveStatistics(numbers);

// Compute some statistics
double size = stats.getN();
double max = stats.getMax();
double median = stats.getPercentile(50);
double mean = stats.getMean();
double std = stats.getStandardDeviation();
```

```
size = 
max =
median = 
mean = 
std = 
sum =
```

## SummaryStatistics

BigDecimal SummaryStatistics has the same interface as the Apache SummaryStatistics
and is used to calculate statistics for a stream of numeric data.

### BigDecimal

```java
import axibase.math.stat.descriptive;

// Get a SummaryStatistics instance
SummaryStatistics stats = new SummaryStatistics();

// Read data from an input stream,
// adding values and updating sums, counters, etc.
while (line != null) {
        line = in.readLine();
        stats.addValue(Double.parseDouble(line.trim()));
}
in.close();

// Compute some statistics
BigDecimal size = stats.getN();
BigDecimal max = stats.getMax();

// Some of statistics can have infinite number of digits. 
// So we should use MathContext to round them.
// Let round result to 16 digits.
MathContext rounding = new MathContext(16, RoundingMode.HALF_UP);
BigDecimal mean = stats.getMean(rounding);
BigDecimal std = stats.getStandardDeviation(rounding);
```

### double

```java
import org.apache.commons.math4.stat.descriptive;

// Get a SummaryStatistics instance
SummaryStatistics stats = new SummaryStatistics();

// Read data from an input stream,
// adding values and updating sums, counters, etc.
while (line != null) {
        line = in.readLine();
        stats.addValue(Double.parseDouble(line.trim()));
}
in.close();

// Compute the statistics
double size = stats.getN();
double max = stats.getMax();
double mean = stats.getMean();
double std = stats.getStandardDeviation();
```
