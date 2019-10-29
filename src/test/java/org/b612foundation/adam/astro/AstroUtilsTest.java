package org.b612foundation.adam.astro;

import org.b612foundation.adam.astro.AstroUtils;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static junit.framework.TestCase.assertEquals;

public class AstroUtilsTest {

  @Test
  public void testLocalDateTimeFromMJD() {
    double mjd = 49987;
    LocalDateTime expected = LocalDateTime.of(1995, 9, 27, 0, 0, 0);
    LocalDateTime actual = AstroUtils.localDateTimefromMJD(mjd);
    assertEquals(expected, actual);

    mjd = 54617 + 4.66817129629631e-01;
    expected = LocalDateTime.of(2008, 5, 31, 11, 12, 12)
            .plusNanos(999999937);
    actual = AstroUtils.localDateTimefromMJD(mjd);
    assertEquals(expected, actual);

    mjd = 58485.00079861111111111;
    expected = LocalDateTime.of(2019, 1, 2, 0, 1, 8)
            .plusNanos(999999784);
    actual = AstroUtils.localDateTimefromMJD(mjd);
    assertEquals(expected.toString(), actual.toString());

  }

  @Test
  public void testMjdFromLocalDateTime() {
    double mjdExpected = 49987.0;
    LocalDateTime dateTime = LocalDateTime.of(1995, 9, 27, 0, 0, 0);
    double mjdActual = AstroUtils.mjdFromLocalDateTime(dateTime);
    assertEquals(mjdExpected, mjdActual, 1e-12);

    mjdExpected = 58485.00079861111;
    dateTime = LocalDateTime.of(2019, 1, 2, 0, 1, 9);
    mjdActual = AstroUtils.mjdFromLocalDateTime(dateTime);
    assertEquals(mjdExpected, mjdActual, 1e-12);

    mjdExpected = 54617.46681712962963;
    dateTime = LocalDateTime.of(2008, 5, 31, 11, 12, 13);
    mjdActual = AstroUtils.mjdFromLocalDateTime(dateTime);
    assertEquals(mjdExpected, mjdActual, 1e-12);
  }

  @Test
  public void testMjdFromZonedDateTime() {
    double mjdExpected = 49987.0;
    ZonedDateTime dateTime = ZonedDateTime.of(1995, 9, 27, 0, 0, 0, 0, ZoneId.of("Z"));
    double mjdActual = AstroUtils.mjdFromZonedDateTime(dateTime);
    assertEquals(mjdExpected, mjdActual, 1e-12);

    mjdExpected = 58485.00079861111;
    dateTime = ZonedDateTime.of(2019, 1, 2, 0, 1, 9, 0, ZoneId.of("Z"));
    mjdActual = AstroUtils.mjdFromZonedDateTime(dateTime);
    assertEquals(mjdExpected, mjdActual, 1e-12);

    mjdExpected = 54617.46681712962963;
    dateTime = ZonedDateTime.of(2008, 5, 31, 11, 12, 13, 0, ZoneId.of("Z"));
    mjdActual = AstroUtils.mjdFromZonedDateTime(dateTime);
    assertEquals(mjdExpected, mjdActual, 1e-12);
  }

}
