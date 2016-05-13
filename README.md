# Overview

BigDecimal implementation of [Apache Commons Math](https://commons.apache.org/proper/commons-math/userguide/stat.html) Descriptive Statistics and Summary Statistics.

See JavaDocs for implementation details.

# License

The project is released under version 2.0 of the [Apache License](LICENCE.md).

# Examples

## DescriptiveStatistics
The interface of the Axibase DescriptiveStatistics is the same as Apache's,
but uses BigDecimal instead of double. As a result the MathContext object should 
be used in some methods for rounding. <br>
The Apache DescriptiveStatistics implements more statistics than the Axibase 
DescriptiveStatistics.

### BigDecimal

```java
import axibase.math.stat.descriptive;

BigDecimal[] numbers = {new BigDecimal("0.01"), new BigDecimal("0.2"), new BigDecimal("-1.3")};

// Get a DescriptiveStatistics instance
DescriptiveStatistics stats = new DescriptiveStatistics(numbers);

// Compute some statistics
BigDecimal size = stats.getN();
BigDecimal max = stats.getMax();
BigDecimal median = stats.getPercentile(50);

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

double[] numbers = {0.01, 0.2, -1.3};

// Get a DescriptiveStatistics instance
DescriptiveStatistics stats = new DescriptiveStatistics(numbers);

// Compute some statistics
double size = stats.getN();
double max = stats.getMax();
double median = stats.getPercentile(50);
double mean = stats.getMean();
double std = stats.getStandardDeviation();
```

## SummaryStatistics

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
//double median = stats.getMedian(); <-- NOT AVAILABLE
```
