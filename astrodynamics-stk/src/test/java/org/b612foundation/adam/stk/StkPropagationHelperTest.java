package org.b612foundation.adam.stk;

import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeInterval;
import agi.foundation.time.TimeStandard;
import org.b612foundation.adam.stk.propagators.ForceModelHelper;
import org.b612foundation.stk.StkLicense;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;

import static com.google.common.truth.Truth.assertThat;
import static org.b612foundation.adam.stk.StkPropagationHelper.validateStartAndEndDate;
import static org.junit.Assert.assertThrows;

public final class StkPropagationHelperTest {

  private TimeInterval supportedTimeInterval;

  @Before
  public void setUp() {
    StkLicense.activate();

    supportedTimeInterval = ForceModelHelper.getSupportedDateRange();
  }

  @Test
  public void testValidate_startDateNotInSupported_throws() {
    // startDate before initial epoch (2433264:43200 TDB (12/13/1949 11:59:17 PM))
    JulianDate startDate = supportedTimeInterval.getStart().subtract(Duration.fromSeconds(1));
    JulianDate endDate =
        new JulianDate(
            ZonedDateTime.parse("2000-01-01T11:59:27.816Z"),
            TimeStandard.getInternationalAtomicTime());

    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class, () -> validateStartAndEndDate(startDate, endDate));
    assertThat(e).hasMessageThat().startsWith("Start date not within");
  }

  @Test
  public void testValidate_endDateNotInSupported_throws() {
    JulianDate startDate =
        new JulianDate(
            ZonedDateTime.parse("2000-01-01T11:59:28Z"), TimeStandard.getInternationalAtomicTime());
    // endDate after final epoch (2506352:43200 TDB (1/21/2150 11:58:50 PM))
    JulianDate endDate = supportedTimeInterval.getStop().add(Duration.fromSeconds(1));

    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class, () -> validateStartAndEndDate(startDate, endDate));
    assertThat(e).hasMessageThat().startsWith("End date not within");
  }
}

