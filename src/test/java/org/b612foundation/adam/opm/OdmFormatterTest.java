package org.b612foundation.adam.opm;

import org.junit.Assert;
import org.junit.Test;

public class OdmFormatterTest {
  @Test
  public void testParseSimpleOpmHappy() throws Exception {
    OrbitParameterMessage expected = OdmScenarioBuilder.buildSimpleOpm();
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(OdmScenarioBuilder.getSimpleOpm());
    Assert.assertEquals(parsed, expected);
  }
  
  /** Returns OPM from figure 3-1 in the ODM standard but USING 'TT' as the TIME_SYSTEM instead of 'UTC' */
  private String getSimpleOpmTT() {
    return "CCSDS_OPM_VERS = 2.0\n"
         + "CREATION_DATE = 1998-11-06T09:23:57\n"
         + "ORIGINATOR = JAXA\n"
         + "COMMENT GEOCENTRIC, CARTESIAN, EARTH FIXED\n"
         + "OBJECT_NAME = GODZILLA 5\n"
         + "OBJECT_ID = 1998-057A\n"
         + "CENTER_NAME = EARTH\n"
         + "REF_FRAME = ITRF-97\n"
         + "TIME_SYSTEM = TT\n"
         + "EPOCH = 1998-12-18T14:28:15.1172\n"
         + "X = 6503.514000\n"
         + "Y = 1239.647000\n"
         + "Z = -717.490000\n"
         + "X_DOT = -0.873160\n"
         + "Y_DOT = 8.740420\n"
         + "Z_DOT = -4.191076\n"
         + "MASS = 3000.000000\n"
         + "SOLAR_RAD_AREA = 18.770000\n"
         + "SOLAR_RAD_COEFF = 1.000000\n"
         + "DRAG_AREA = 18.770000\n"
         + "DRAG_COEFF = 2.500000\n";
  }
  
  @Test
  public void testParseSimpleOpmTTHappy() throws Exception {
    OrbitParameterMessage expected = OdmScenarioBuilder.buildSimpleOpm();
    expected.getMetadata().setTime_system(OdmCommonMetadata.TimeSystem.TT);
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(getSimpleOpmTT());
    Assert.assertEquals(parsed, expected);
  }

  @Test
  public void testParseKeplerianAndManeuversOpmHappy() throws Exception {
    OrbitParameterMessage expected = OdmScenarioBuilder.buildOpmWithKeplerianAndManeuvers();
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(OdmScenarioBuilder.getOpmWithKeplerianAndManeuvers());
    Assert.assertEquals(parsed, expected);
  }

  @Test
  public void testCovarianceOpmHappy() throws Exception {
    final String type = "FACES";
    OrbitParameterMessage expected = OdmScenarioBuilder.buildOpmWithCovariance(type);
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(OdmScenarioBuilder.getOpmWithCovariance(type));
    Assert.assertEquals(parsed, expected);
  }

  @Test
  public void testBasicOemHappy() throws Exception {
    OrbitEphemerisMessage expected = OdmScenarioBuilder.buildOemWithAccelerations();
    OrbitEphemerisMessage parsed = OdmFormatter.parseOemString(OdmScenarioBuilder.getOemWithAccelerations());
    Assert.assertEquals(parsed, expected);
  }

  @Test
  public void testOemWithCovariance() throws Exception {
    OrbitEphemerisMessage expected = OdmScenarioBuilder.buildOemWithCovariance();
    OrbitEphemerisMessage parsed = OdmFormatter.parseOemString(OdmScenarioBuilder.getOemWithCovariance());
    Assert.assertEquals(parsed, expected);
  }
}
