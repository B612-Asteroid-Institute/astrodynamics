package org.b612foundation.adam.stk.propagators;

import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// TODO: use this everywhere ISO-format date strings are parsed.
public class TimeHelper {

  /**
   * Parses the given ISO-formatted date string as a timestamp in the given time standard.
   *
   * @throws IllegalArgumentException if there is an error parsing the date.
   */
  public static JulianDate fromIsoFormat(String date, TimeStandard timeStandard)
      throws IllegalArgumentException {
    if (Character.isDigit(date.charAt(date.length() - 1))) {
      date = date + "Z";
    }
    try {
      return new JulianDate(ZonedDateTime.parse(date), timeStandard);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Could not parse date " + date, e);
    }
  }

  /**
   * Formats the given date as a timestamp using the date's internal time standard (which will be
   * whatever it is created with).
   */
  public static String toIsoFormat(JulianDate date) {
    // Round to the nearest microsecond, since these are mainly used to hand back to the client and
    // that's the precision the client can handle.
    double secondsDifference = date.getSecondsOfDay() % .000001;
    date = date.subtractSeconds(secondsDifference);
    return date.toDateTime(date.getStandard()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
  }
}
