package hu.ipsystems.timeseries.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static ZonedDateTime zonedDateTime(String date, ZoneId zoneId) {
        return ZonedDateTime.of(LocalDateTime.parse(date, formatter), zoneId);
    }

}
