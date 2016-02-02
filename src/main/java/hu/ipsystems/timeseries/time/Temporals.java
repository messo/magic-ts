package hu.ipsystems.timeseries.time;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public final class Temporals {

    public static ZonedDateTime roundDown(ZonedDateTime dateTime, TemporalUnit temporalUnit) {
        if (temporalUnit.getDuration().compareTo(ChronoUnit.DAYS.getDuration()) != 1) {
            return dateTime.truncatedTo(temporalUnit);
        } else if (temporalUnit instanceof ChronoUnit) {
            switch ((ChronoUnit) temporalUnit) {
                case MONTHS:
                    return dateTime.with(ChronoField.NANO_OF_DAY, 0).with(ChronoField.DAY_OF_MONTH, 1);
                case YEARS:
                    return dateTime.with(ChronoField.NANO_OF_DAY, 0).with(ChronoField.DAY_OF_YEAR, 1);
            }
        }

        throw new IllegalArgumentException("Unsupported temporal unit: " + temporalUnit);
    }

    public static ZonedDateTime roundUp(ZonedDateTime dateTime, TemporalUnit temporalUnit) {
        ZonedDateTime roundDown = roundDown(dateTime, temporalUnit);
        if (roundDown.isEqual(dateTime)) {
            return dateTime;
        } else {
            return roundDown.plus(1, temporalUnit);
        }
    }

}
