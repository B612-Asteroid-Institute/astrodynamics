package org.b612foundation.adam.astro;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.JulianFields;

import static org.b612foundation.adam.astro.AstroConstants.DAY_TO_SEC;

public class AstroUtils {

    public static LocalDateTime localDateTimefromMJD(double mjd) {
        long dayComponent = (long)mjd;
        double timeComponentSeconds = (mjd - dayComponent) * DAY_TO_SEC;
        long timeSeconds = (long)timeComponentSeconds;
        long timeNanoSeconds = (long)(timeComponentSeconds - timeSeconds);
        LocalDate date = LocalDate.MIN.with(JulianFields.MODIFIED_JULIAN_DAY, dayComponent);
        LocalTime time = LocalTime.MIDNIGHT.plusSeconds(timeSeconds).plusNanos(timeNanoSeconds);
        return LocalDateTime.of(date, time);
    }
}
