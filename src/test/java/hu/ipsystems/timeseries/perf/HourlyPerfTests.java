package hu.ipsystems.timeseries.perf;

import hu.ipsystems.timeseries.TimeSeries;
import hu.ipsystems.timeseries.TimeSeriesList;
import hu.ipsystems.timeseries.util.DateUtil;
import hu.ipsystems.timeseries.util.TimeSeriesUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static hu.ipsystems.timeseries.util.PerfUtil.measureNewSum;
import static hu.ipsystems.timeseries.util.PerfUtil.measureOldSum;
import static org.junit.Assert.assertArrayEquals;

public class HourlyPerfTests {

    @Test
    public void yearLongsInHourly() {
        // given
        TimeSeriesList list = TimeSeriesList.of(
                TimeSeriesUtil.generate(
                        DateUtil.zonedDateTime("2015-01-01 00:00", ZoneId.of("CET")),
                        DateUtil.zonedDateTime("2016-01-01 00:00", ZoneId.of("CET")),
                        ChronoUnit.HOURS,
                        10_000
                )
        );

        // when new
        TimeSeries sum1 = measureNewSum(list);
        // when old
        hu.ipsystems.timeseries.data.TimeSeries sum2 = measureOldSum(list);

        // then
        assertArrayEquals(sum1.getData(), sum2.getData(), 1e-5);
    }

    @Test
    public void fullRandom() {
        // given
        TimeSeriesList list = TimeSeriesList.of(
                TimeSeriesUtil.generateWithRandomLength(
                        DateUtil.zonedDateTime("2015-01-01 00:00", ZoneId.of("CET")),
                        DateUtil.zonedDateTime("2016-01-01 00:00", ZoneId.of("CET")),
                        ChronoUnit.HOURS,
                        10_000
                )
        );

        // when new
        TimeSeries sum1 = measureNewSum(list);
        // when old
        hu.ipsystems.timeseries.data.TimeSeries sum2 = measureOldSum(list);

        // then
        assertArrayEquals(sum1.getData(), sum2.getData(), 1e-5);
    }
}
