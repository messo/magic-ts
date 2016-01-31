package hu.ipsystems.timeseries.util;

import hu.ipsystems.timeseries.TimeSeries;
import hu.ipsystems.timeseries.resolution.TimePeriod;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimeSeriesUtil {

    private static final Random random = new Random();

    public static TimeSeries random(ZonedDateTime begin, ZonedDateTime end, TemporalUnit unit) {
        double[] data = new double[(int) unit.between(begin, end)];
        data[0] = 1.0; // just for check
        for (int i = 1; i < data.length; i++) {
            data[i] = random.nextDouble();
        }

        return new TimeSeries(begin, unit, data);
    }

    public static List<TimeSeries> generate(ZonedDateTime begin, ZonedDateTime end, TemporalUnit unit, int count) {
        List<TimeSeries> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(random(begin, end, unit));
        }

        return list;
    }

    public static List<TimeSeries> generateWithRandomLength(ZonedDateTime begin, ZonedDateTime end, TemporalUnit unit, int count) {
        List<TimeSeries> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ZonedDateTime randomStart, randomEnd;

            while (true) {
                long epochStart = begin.toEpochSecond() + random.nextInt((int) (end.toEpochSecond() - begin.toEpochSecond()));
                long epochEnd = epochStart + random.nextInt((int) (end.toEpochSecond() - epochStart));
                randomStart = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochStart), begin.getZone()).truncatedTo(unit);
                randomEnd = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochEnd), end.getZone()).truncatedTo(unit);
                if (unit.between(randomStart, randomEnd) > 1) {
                    break;
                }
            }

            list.add(random(randomStart, randomEnd, unit));
        }

        return list;
    }

    public static hu.ipsystems.timeseries.data.TimeSeries asOld(TimeSeries ts) {
        DateTimeZone dtz = DateTimeZone.forID(ts.getBegin().getZone().getId());
        DateTime dt = new DateTime(ts.getBegin().toInstant().toEpochMilli(), dtz);
        return hu.ipsystems.timeseries.data.TimeSeries.timeSeries(dt, temporalUnitToTimePeriod(ts.getUnit()), ts.getData());
    }

    private static TimePeriod temporalUnitToTimePeriod(TemporalUnit unit) {
        if (unit == ChronoUnit.HOURS) {
            return TimePeriod.HOURLY;
        } else if (unit == ChronoUnit.DAYS) {
            return TimePeriod.DAILY;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
