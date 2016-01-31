package hu.ipsystems.timeseries;

import hu.ipsystems.timeseries.util.DateUtil;
import hu.ipsystems.timeseries.util.TimeSeriesUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TimeSeriesListTests {

    @Test
    public void sum() {
        // given
        TimeSeriesList list = TimeSeriesList.of(
                TimeSeriesUtil.generate(
                        DateUtil.zonedDateTime("2015-01-01 00:00", ZoneId.of("CET")),
                        DateUtil.zonedDateTime("2016-01-01 00:00", ZoneId.of("CET")),
                        ChronoUnit.HOURS,
                        10
                )
        );

        // when
        TimeSeries sum = list.sum();

        // assert
        Assert.assertEquals(10.0, sum.iterator().next(), 1e-10);
    }
}
