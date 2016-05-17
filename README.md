# BigDecimal Statistics

## Overview

BigDecimal implementation of [Apache Commons Math](https://commons.apache.org/proper/commons-math/userguide/stat.html) Descriptive Statistics and Summary Statistics.

See JavaDocs for implementation details.

The following methods from Apache DescriptiveStatistics are _not_ implemented in this release:

- getGeometricMean()
- getKurtosis()
- getQuadraticMean()
- getSkewness()

The MathContext object should be used for rounding for the following methods since they may return an infitinite number of fractional digits:

- getMean()
- getVariance()
- getStandardDeviation()

## License

The project is released under version 2.0 of the [Apache License](LICENCE.md).

## Examples

### DescriptiveStatistics

* Axibase BigDecimal DescriptiveStatistics

```java
import com.axibase.math.stat.descriptive;

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
```

```
size = 3
max = 1.3
median = 0.300
mean = 0.5666666666666667
std = 0.6429100507328637
sum = 1.7
```

```java
// Compute standard deviation with 256 digit precision
MathContext context = new MathContext(256, RoundingMode.HALF_UP);
System.out.println("std256 = " stats.getStandardDeviation(context));
```

```
std256 = 0.6429100507328636663840020698288442122604602462042168271533451258487524279112433148600036472862405727387582388938630659235005210461543930677311557061223125970622348551846400953679001533442110739077534147619453047556859898336326044546221595799048295894202745

```

* Apache Math double DescriptiveStatistics

```java
import org.apache.commons.math3.stat.descriptive;

double[] numbers = {1.3, 0.3, 0.1};

// Get a DescriptiveStatistics instance
DescriptiveStatistics stats = new DescriptiveStatistics(numbers);

// Compute some statistics
long size = stats.getN();
double max = stats.getMax();
double median = stats.getPercentile(50);
double mean = stats.getMean();
double std = stats.getStandardDeviation();
double sum = stats.getSum();
```

```
size = 3
max = 1.3
median = 0.3
mean = 0.5666666666666667
std = 0.6429100507328638
sum = 1.7000000000000002
```

### SummaryStatistics

BigDecimal SummaryStatistics has the same interface as the Apache SummaryStatistics
and is used to calculate statistics for a stream of numeric data.

* Axibase BigDecimal SummaryStatistics

```java
import com.axibase.math.stat.descriptive;

String[] numbers = {"1.3", "0.3", "0.1"};

// Get a SummaryStatistics instance
SummaryStatistics stats = new SummaryStatistics();

//Read data as BigDecimal values
for (String v : numbers) {
        //no precision loss
        BigDecimal bd = new BigDecimal(v);
        stats.addValue(bd);
}

// Compute some statistics
long size = stats.getN();
BigDecimal max = stats.getMax();

// Some of statistics can have infinite number of digits. 
// So we should use MathContext to round them.
// Let round result to 16 digits.
MathContext rounding = new MathContext(16, RoundingMode.HALF_UP);
BigDecimal mean = stats.getMean(rounding);
BigDecimal std = stats.getStandardDeviation(rounding);
```

* Apache Math double SummaryStatistics

```java
import org.apache.commons.math3.stat.descriptive;

String[] numbers = {"1.3", "0.3", "0.1"};

// Get a SummaryStatistics instance
SummaryStatistics stats = new SummaryStatistics();

// Read data as double values
for (String v : numbers) {
        //possible precision loss
        double dbl = Double.parseDouble(v);
        stats.addValue(dbl);
}

// Compute the statistics
long size = stats.getN();
double max = stats.getMax();
double mean = stats.getMean();
double std = stats.getStandardDeviation();
```
