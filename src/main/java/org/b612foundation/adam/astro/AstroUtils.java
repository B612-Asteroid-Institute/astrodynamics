package org.b612foundation.adam.astro;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.JulianFields;

import static org.b612foundation.adam.astro.AstroConstants.*;

public class AstroUtils {

    public static LocalDateTime localDateTimefromMJD(double mjd) {
        long dayComponent = (long)mjd;
        double timeComponentDays = (mjd - dayComponent);
        double hoursFull = timeComponentDays * DAY_TO_HR;
        long hoursComponent = (long) hoursFull;
        double minutesFull = (hoursFull - hoursComponent) * HR_TO_MIN;
        long minutesComponent = (long)  minutesFull;
        double secondsFull = (minutesFull - minutesComponent) * MIN_TO_SEC;
        long secondsComponent = (long ) secondsFull;
        long nanoComponent = (long)((secondsFull - secondsComponent) * SEC_TO_NANO);
        LocalDate date = LocalDate.MIN.with(JulianFields.MODIFIED_JULIAN_DAY, dayComponent) ;
        LocalTime time = LocalTime.MIDNIGHT
                .plusHours(hoursComponent)
                .plusMinutes(minutesComponent)
                .plusSeconds(secondsComponent)
                .plusNanos(nanoComponent);
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
