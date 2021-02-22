package org.b612foundation.adam.common;

import org.junit.Assert;
import org.junit.Test;

public final class OrbitDataHelperTest {

  // These tests verify that the helper method BatchRunHelper.extractLastEphemerisPoint()
  // works on both Windows and non-Windows platforms, testing the different end-of-line
  // character sequences.

  // Each numerical row in an STK .e file has 7 doubles in it
  private static String sample_row = "00000.0 11111.1 22222.2 33333.3 44444.4 55555.5 66666.6";
  private static String last_row = "01234.0 12345.1 23456.2 34567.3 45678.4 56789.5 67890.6";

  @Test
  public void testWindowsString() {

    // In windows the end of line has both carriage return and newline
    String eol_str = "\r\n";

    // Create a typical ephem string
    String test_ephem_string =
        "Ephemeris:"
            + eol_str
            + "stk.v.9.0"
            + eol_str
            + "# WrittenBy    STK_Components_2017 r4(17.4.392.0)"
            + eol_str
            + "BEGIN Ephemeris"
            + eol_str
            + "NumberOfEphemerisPoints 3"
            + eol_str
            + "ScenarioEpoch   2 Oct 2018 00:00:00"
            + eol_str
            + "InterpolationMethod Hermite"
            + eol_str
            + "InterpolationSamplesM1  2"
            + eol_str
            + "CentralBody Sun"
            + eol_str
            + "CoordinateSystem    ICRF"
            + eol_str
            + "EphemerisTimePosVel"
            + eol_str
            + "BEGIN Ephemeris"
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + last_row
            + eol_str
            + eol_str
            + "END Ephemeris"
            + eol_str;

    // Test the method
    String ret_str = OrbitDataHelper.extractLastEphemerisPoint(test_ephem_string);

    // Verify that the final state vector of the ephemeris is actually the final state
    Assert.assertEquals(ret_str, last_row);
  }

  @Test
  public void testNonWindowsString() {

    // In non windows machines the end of line is only the newline
    String eol_str = "\n";

    // Create a typical ephem string
    String test_ephem_string =
        "Ephemeris:"
            + eol_str
            + "stk.v.9.0"
            + eol_str
            + "# WrittenBy    STK_Components_2017 r4(17.4.392.0)"
            + eol_str
            + "BEGIN Ephemeris"
            + eol_str
            + "NumberOfEphemerisPoints 3"
            + eol_str
            + "ScenarioEpoch   2 Oct 2018 00:00:00"
            + eol_str
            + "InterpolationMethod Hermite"
            + eol_str
            + "InterpolationSamplesM1  2"
            + eol_str
            + "CentralBody Sun"
            + eol_str
            + "CoordinateSystem    ICRF"
            + eol_str
            + "EphemerisTimePosVel"
            + eol_str
            + "BEGIN Ephemeris"
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + sample_row
            + eol_str
            + last_row
            + eol_str
            + eol_str
            + "END Ephemeris"
            + eol_str;

    // Test the method
    String ret_str = OrbitDataHelper.extractLastEphemerisPoint(test_ephem_string);

    // Verify that the final state vector of the ephemeris is actually the final state
    Assert.assertEquals(ret_str, last_row);
  }
}
