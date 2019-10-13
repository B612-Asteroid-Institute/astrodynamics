package org.b612foundation.adam.astro;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
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

    public static double mjdFromLocalDateTime(LocalDateTime dateTime) {
        //1858-11-17
        LocalDateTime julianEpoch = LocalDateTime.of(1858, 11, 17, 0, 0, 0);
        long seconds = ChronoUnit.SECONDS.between(julianEpoch, dateTime);
        double mjd = seconds / 86400.0;
        return mjd;
    }
}
