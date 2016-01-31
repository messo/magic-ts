package hu.ipsystems.timeseries.time;

import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class TemporalUtil {

    public static ZonedDateTime roundDown(ZonedDateTime dateTime, TemporalUnit temporalUnit) {
        // FIXME -- not only days!
        Preconditions.checkArgument(temporalUnit == ChronoUnit.DAYS);

        return dateTime.truncatedTo(temporalUnit);
    }

    public static ZonedDateTime roundUp(ZonedDateTime dateTime, TemporalUnit temporalUnit) {
        // FIXME -- not only days!
        Preconditions.checkArgument(temporalUnit == ChronoUnit.DAYS);

        ZonedDateTime trunc = dateTime.truncatedTo(temporalUnit);
        if (trunc.isEqual(dateTime)) {
            return dateTime;
        } else {
            return trunc.plus(1, temporalUnit);
        }
    }

}
