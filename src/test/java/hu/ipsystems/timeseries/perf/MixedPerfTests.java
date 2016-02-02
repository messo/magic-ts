package hu.ipsystems.timeseries.perf;

import hu.ipsystems.timeseries.TimeSeries;
import hu.ipsystems.timeseries.TimeSeriesList;
import hu.ipsystems.timeseries.util.DateUtil;
import hu.ipsystems.timeseries.util.TimeSeriesUtil;
import org.junit.Test;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static hu.ipsystems.timeseries.util.PerfUtil.measureNewSum;
import static hu.ipsystems.timeseries.util.PerfUtil.measureOldSum;
import static hu.ipsystems.timeseries.util.TimeSeriesUtil.toPrimitive;
import static org.junit.Assert.assertArrayEquals;

public class MixedPerfTests {

    @Test
    public void allDailyButOneHourly() {
        // given 1000 daily ts, all 1 year long!
        List<TimeSeries> timeSeriesList = TimeSeriesUtil.generate(
                DateUtil.zonedDateTime("2015-01-01 00:00", ZoneId.of("CET")),
                DateUtil.zonedDateTime("2016-01-01 00:00", ZoneId.of("CET")),
                ChronoUnit.DAYS,
                1_000
        );

        // given 1 hourly ts, 1 year long!
        timeSeriesList.add(
                TimeSeriesUtil.random(
                        DateUtil.zonedDateTime("2015-01-01 00:00", ZoneId.of("CET")),
                        DateUtil.zonedDateTime("2016-01-01 00:00", ZoneId.of("CET")),
                        ChronoUnit.HOURS
                )
        );

        TimeSeriesList list = TimeSeriesList.of(timeSeriesList);

        // when new
        TimeSeries sum1 = measureNewSum(list);
        // when old
        hu.ipsystems.timeseries.data.TimeSeries sum2 = measureOldSum(list);

        // then
        assertArrayEquals(toPrimitive(sum1.getData()), sum2.getData(), 1e-5);
    }
}
