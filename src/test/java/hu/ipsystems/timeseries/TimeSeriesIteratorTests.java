package hu.ipsystems.timeseries;

import com.google.common.primitives.Doubles;
import hu.ipsystems.timeseries.time.TemporalUnits;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.DoubleStream;

import static hu.ipsystems.timeseries.util.DateUtil.zonedDateTime;
import static hu.ipsystems.timeseries.util.TimeSeriesUtil.CET;
import static hu.ipsystems.timeseries.util.TimeSeriesUtil.generateDoubles;


public class TimeSeriesIteratorTests {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void basicHourlyIterator() {
        // given
        double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-15 10:00", CET),
                ChronoUnit.HOURS,
                data
        );

        // when
        List<Double> iteratedData = extractData(timeSeries.iterator());

        // then
        Assert.assertEquals(Doubles.asList(data), iteratedData);
    }

    @Test
    public void intervalHourlyIterator() {
        // given
        double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-15 10:00", CET),
                ChronoUnit.HOURS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-15 00:00", CET),
                        zonedDateTime("2015-01-16 00:00", CET)
                )
        );

        // then
        Assert.assertEquals(24, iteratedData.size());
        Assert.assertEquals(
                generateDoubles(
                        DoubleStream.generate(() -> TimeSeries.GAP).limit(10),
                        DoubleStream.of(1.0, 2.0, 3.0, 4.0, 5.0),
                        DoubleStream.generate(() -> TimeSeries.GAP).limit(9)
                ),
                iteratedData
        );
    }

    @Test
    public void hourlyIteratorForDaily_normal() {
        // given
        double[] data = {1.0, 2.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-15 00:00", CET),
                ChronoUnit.DAYS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-15 22:00", CET),
                        zonedDateTime("2015-01-16 02:00", CET),
                        ChronoUnit.HOURS
                )
        );

        // then
        Assert.assertEquals(4, iteratedData.size());
        Assert.assertEquals(Doubles.asList(1.0, 1.0, 2.0, 2.0), iteratedData);
    }

    @Test
    public void hourlyIteratorForDaily_23() {
        // given
        double[] data = {1.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-03-29 00:00", CET),
                ChronoUnit.DAYS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-03-29 00:00", CET),
                        zonedDateTime("2015-03-30 00:00", CET),
                        ChronoUnit.HOURS
                )
        );

        // then
        Assert.assertEquals(23, iteratedData.size());
        Assert.assertEquals(generateDoubles(DoubleStream.generate(() -> 1.0).limit(23)), iteratedData);
    }

    @Test
    public void hourlyIteratorForDaily_25() {
        // given
        double[] data = {1.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-10-25 00:00", CET),
                ChronoUnit.DAYS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-10-25 00:00", CET),
                        zonedDateTime("2015-10-26 00:00", CET),
                        ChronoUnit.HOURS
                )
        );

        // then
        Assert.assertEquals(25, iteratedData.size());
        Assert.assertEquals(generateDoubles(DoubleStream.generate(() -> 1.0).limit(25)), iteratedData);
    }

    @Test
    public void quarterHourlyIteratorForHourly() {
        // given
        double[] data = {1.0, 2.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-15 10:00", CET),
                ChronoUnit.HOURS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-15 09:45", CET),
                        zonedDateTime("2015-01-15 12:15", CET),
                        TemporalUnits.QUARTER_HOURS
                )
        );

        // then
        Assert.assertEquals(10, iteratedData.size());
        Assert.assertEquals(
                generateDoubles(
                        DoubleStream.of(TimeSeries.GAP),
                        DoubleStream.generate(() -> 1.0).limit(4),
                        DoubleStream.generate(() -> 2.0).limit(4),
                        DoubleStream.of(TimeSeries.GAP)
                ),
                iteratedData
        );
    }

    @Test
    public void dailyIteratorForMonthly() {
        // given
        double[] data = {1.0, 2.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-01 00:00", CET),
                ChronoUnit.MONTHS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-01 00:00", CET),
                        zonedDateTime("2015-03-01 00:00", CET),
                        ChronoUnit.DAYS
                )
        );

        // then
        Assert.assertEquals(31 + 28, iteratedData.size());
        Assert.assertEquals(
                generateDoubles(
                        DoubleStream.generate(() -> 1.0).limit(31),
                        DoubleStream.generate(() -> 2.0).limit(28)
                ),
                iteratedData
        );
    }

    @Test
    public void hourlyIteratorForMonthly() {
        // given
        double[] data = {1.0, 2.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-01 00:00", CET),
                ChronoUnit.MONTHS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-31 00:00", CET),
                        zonedDateTime("2015-02-02 00:00", CET),
                        ChronoUnit.HOURS
                )
        );

        // then
        Assert.assertEquals(24 + 24, iteratedData.size());
        Assert.assertEquals(
                generateDoubles(
                        DoubleStream.generate(() -> 1.0).limit(24),
                        DoubleStream.generate(() -> 2.0).limit(24)
                ),
                iteratedData
        );
    }

    @Test
    public void monthlyIteratorForQuarterYearly() {
        // given
        double[] data = {1.0, 2.0, 3.0, 4.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-01 00:00", CET),
                IsoFields.QUARTER_YEARS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-01 00:00", CET),
                        zonedDateTime("2016-01-01 00:00", CET),
                        ChronoUnit.MONTHS
                )
        );

        // then
        Assert.assertEquals(3 + 3 + 3 + 3, iteratedData.size());
        Assert.assertEquals(
                generateDoubles(
                        DoubleStream.generate(() -> 1.0).limit(3),
                        DoubleStream.generate(() -> 2.0).limit(3),
                        DoubleStream.generate(() -> 3.0).limit(3),
                        DoubleStream.generate(() -> 4.0).limit(3)
                ),
                iteratedData
        );
    }

    @Test
    public void dailyIteratorForQuarterYearly() {
        // given
        double[] data = {1.0, 2.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-04-01 00:00", CET),
                IsoFields.QUARTER_YEARS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-06-30 00:00", CET),
                        zonedDateTime("2015-07-02 00:00", CET),
                        ChronoUnit.DAYS
                )
        );

        // then
        Assert.assertEquals(2, iteratedData.size());
        Assert.assertEquals(generateDoubles(DoubleStream.of(1.0, 2.0)), iteratedData);
    }

    @Test
    public void quarterYearlyIteratorForYearly() {
        // given
        double[] data = {1.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-01 00:00", CET),
                ChronoUnit.YEARS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-01 00:00", CET),
                        zonedDateTime("2016-01-01 00:00", CET),
                        IsoFields.QUARTER_YEARS
                )
        );

        // then
        Assert.assertEquals(4, iteratedData.size());
        Assert.assertEquals(generateDoubles(DoubleStream.generate(() -> 1.0).limit(4)), iteratedData);
    }

    @Test
    public void monthlyIteratorForYearly() {
        // given
        double[] data = {1.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-01 00:00", CET),
                ChronoUnit.YEARS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-01 00:00", CET),
                        zonedDateTime("2016-01-01 00:00", CET),
                        ChronoUnit.MONTHS
                )
        );

        // then
        Assert.assertEquals(12, iteratedData.size());
        Assert.assertEquals(generateDoubles(DoubleStream.generate(() -> 1.0).limit(12)), iteratedData);
    }

    @Test
    public void dailyIteratorForYearly() {
        // given
        double[] data = {1.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-01 00:00", CET),
                ChronoUnit.YEARS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-01 00:00", CET),
                        zonedDateTime("2015-04-01 00:00", CET),
                        ChronoUnit.DAYS
                )
        );

        // then
        Assert.assertEquals(31 + 28 + 31, iteratedData.size());
        Assert.assertEquals(generateDoubles(DoubleStream.generate(() -> 1.0).limit(31 + 28 + 31)), iteratedData);
    }

    @Test
    public void hourlyIteratorForYearly() {
        // given
        double[] data = {1.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-01 00:00", CET),
                ChronoUnit.YEARS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-10-10 00:00", CET),
                        zonedDateTime("2015-10-10 08:00", CET),
                        ChronoUnit.HOURS
                )
        );

        // then
        Assert.assertEquals(8, iteratedData.size());
        Assert.assertEquals(generateDoubles(DoubleStream.generate(() -> 1.0).limit(8)), iteratedData);
    }

    @Test
    public void dailyIteratorForHourly() {
        // given
        double[] data = {1.0, 2.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-15 00:00", CET),
                ChronoUnit.HOURS,
                data
        );

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Iteration cannot be done in larger steps than the resolution.");

        // when
        timeSeries.iterator(
                zonedDateTime("2015-01-14 00:00", CET),
                zonedDateTime("2015-01-16 00:00", CET),
                ChronoUnit.DAYS
        );
    }

    private List<Double> extractData(Iterator<Double> iterator) {
        List<Double> data = new LinkedList<>();
        iterator.forEachRemaining(data::add);
        return data;
    }
}
