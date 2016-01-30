package hu.ipsystems.timeseries;

import com.google.common.primitives.Doubles;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static hu.ipsystems.timeseries.util.DateUtil.zonedDateTime;

public class BasicTimeSeriesTest {

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
        List<Double> iteratedData = new LinkedList<>();
        timeSeries.forEach(iteratedData::add);

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
        List<Double> iteratedData = new LinkedList<>();
        timeSeries.iterator(
                zonedDateTime("2015-01-15 00:00", ZoneId.of("CET")),
                zonedDateTime("2015-01-16 00:00", ZoneId.of("CET"))
        ).forEachRemaining(iteratedData::add);

        // then
        Assert.assertEquals(24, iteratedData.size());

        double[] expected = new double[24];
        Arrays.fill(expected, TimeSeries.GAP);
        System.arraycopy(data, 0, expected, 10, data.length);
        Assert.assertEquals(Doubles.asList(expected), iteratedData);
    }
}
