package hu.ipsystems.timeseries;

import com.google.common.primitives.Doubles;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.DoubleStream;

import static hu.ipsystems.timeseries.util.DateUtil.zonedDateTime;


public class TimeSeriesIteratorTests {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void basicHourlyIterator() {
        // given
        double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-15 10:00", ZoneId.of("CET")),
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
                zonedDateTime("2015-01-15 10:00", ZoneId.of("CET")),
                ChronoUnit.HOURS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-15 00:00", ZoneId.of("CET")),
                        zonedDateTime("2015-01-16 00:00", ZoneId.of("CET"))
                )
        );

        // then
        Assert.assertEquals(24, iteratedData.size());

        double[] expected = new double[24];
        Arrays.fill(expected, TimeSeries.GAP);
        System.arraycopy(data, 0, expected, 10, data.length);
        Assert.assertEquals(Doubles.asList(expected), iteratedData);
    }

    @Test
    public void hourlyIteratorForDaily_normal() {
        // given
        double[] data = {1.0, 2.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-15 00:00", ZoneId.of("CET")),
                ChronoUnit.DAYS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-01-15 22:00", ZoneId.of("CET")),
                        zonedDateTime("2015-01-16 02:00", ZoneId.of("CET")),
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
                zonedDateTime("2015-03-29 00:00", ZoneId.of("CET")),
                ChronoUnit.DAYS,
                data
        );

        // when
        List<Double> iteratedData = extractData(
                timeSeries.iterator(
                        zonedDateTime("2015-03-29 00:00", ZoneId.of("CET")),
                        zonedDateTime("2015-03-30 00:00", ZoneId.of("CET")),
                        ChronoUnit.HOURS
                )
        );

        // then
        Assert.assertEquals(23, iteratedData.size());
        Assert.assertEquals(Doubles.asList(DoubleStream.generate(() -> 1.0).limit(23).toArray()), iteratedData);
    }

    @Test
    public void dailyIteratorForHourly() {
        // given
        double[] data = {1.0, 2.0};
        TimeSeries timeSeries = new TimeSeries(
                zonedDateTime("2015-01-15 00:00", ZoneId.of("CET")),
                ChronoUnit.HOURS,
                data
        );

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Iteration cannot be done in larger steps than the resolution.");

        // when
        timeSeries.iterator(
                zonedDateTime("2015-01-14 00:00", ZoneId.of("CET")),
                zonedDateTime("2015-01-16 00:00", ZoneId.of("CET")),
                ChronoUnit.DAYS
        );
    }

    private List<Double> extractData(Iterator<Double> iterator) {
        List<Double> data = new LinkedList<>();
        iterator.forEachRemaining(data::add);
        return data;
    }
}
