package org.b612foundation.adam.astro;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.JulianFields;

import static org.b612foundation.adam.astro.AstroConstants.*;

public final class AstroUtils {

  /**
   * Given a Modified Julian Date return a LocalDateTime object with the Gregorian style date/time.
   *
   * @param mjd modified Julian Date representation
   * @return LocalDateTime version of the date
   */
  public static LocalDateTime localDateTimefromMJD(double mjd) {
        /*
         This is being done in stages rather than converting directly to seconds to pick up extra precision.
         The day->seconds conversion requires up to 5 decimal places full seconds versus no more than 2 with the
         staged calculation.
         */
    long dayComponent = (long) Math.floor(mjd);
    double hoursFull = (mjd - dayComponent) * DAY_TO_HR;
    long hoursComponent = (long) Math.floor(hoursFull);
    double minutesFull = (hoursFull - hoursComponent) * HR_TO_MIN;
    long minutesComponent = (long) Math.floor(minutesFull);
    double secondsFull = (minutesFull - minutesComponent) * MIN_TO_SEC;
    long secondsComponent = (long) Math.floor(secondsFull);
    long nanoComponent = (long) Math.floor((secondsFull - secondsComponent) * SEC_TO_NANO);
    LocalDate date = LocalDate.MIN.with(JulianFields.MODIFIED_JULIAN_DAY, dayComponent);
    LocalTime time = LocalTime.MIDNIGHT
        .plusHours(hoursComponent)
        .plusMinutes(minutesComponent)
        .plusSeconds(secondsComponent)
        .plusNanos(nanoComponent);
    return LocalDateTime.of(date, time);
  }

  /**
   * Given a date/time represented by a LocalDateTime object compute the equivalent Modified Julian Date
   *
   * @param dateTime date/time to be converted
   * @return Modified Julian Date of input date/time
   */
  public static double mjdFromLocalDateTime(LocalDateTime dateTime) {
    //1858-11-17
    final LocalDateTime julianEpoch = LocalDateTime.of(1858, 11, 17, 0, 0, 0);
    long seconds = ChronoUnit.MILLIS.between(julianEpoch, dateTime);
    return seconds / DAY_TO_MILLIS;
  }

  /**
   * Given a date/time represented by a ZonedDateTime object compute the equivalent Modified Julian Date
   *
   * @param dateTime date/time to be converted
   * @return Modified Julian Date of input date/time
   */
  public static double mjdFromZonedDateTime(ZonedDateTime dateTime) {
    //1858-11-17
    final ZonedDateTime julianEpoch = ZonedDateTime.of(1858, 11, 17, 0, 0, 0, 0, ZoneId.of("Z"));
    long seconds = ChronoUnit.MILLIS.between(julianEpoch, dateTime);
    return seconds / DAY_TO_MILLIS;
  }
}
