package org.b612foundation.adam.opm;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;

public class OdmFormatterTest {
  @Test
  public void testParseSimpleOpmHappy() throws Exception {
    OrbitParameterMessage expected = OdmScenarioBuilder.buildSimpleOpm();
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(OdmScenarioBuilder.getSimpleOpm());
    Assert.assertEquals(parsed, expected);
  }

  /**
   * Returns OPM from figure 3-1 in the ODM standard but USING 'TT' as the TIME_SYSTEM instead of 'UTC'
   */
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

  @Test
  public void testBasicOorbOemHappy() {
    String objectName = UUID.randomUUID().toString();
    String objectId = UUID.randomUUID().toString();
    String oorbEphemerisString = String.join("\n",
        "!!OID FORMAT x y z dx/dt dy/dt dz/dt H t_0 INDEX N_PAR MOID COMPCODE",
        "smoke CAR  0.149995559432062E+01  0.140451497813544E-01 -0.893450119807154E-15 -0.131510871962501E-03  0.140447393095277E-01 -0.176296525267191E-14  0.200000000000000E+02   0.58485000800741E+05 1 6 -0.100000000000000E+01 OPENORB",
        "smoke CAR  0.149975833233652E+01  0.280890681682720E-01 -0.345024787678361E-14 -0.263010213738389E-03  0.140428922497122E-01 -0.329957019328539E-14  0.200000000000000E+02   0.58486000800741E+05 1 6 -0.100000000000000E+01 OPENORB");
    OrbitEphemerisMessage oemFromOorb = OdmFormatter.parseOorbEphemerisString(oorbEphemerisString, objectName, objectId, false);
    assertEquals(1, oemFromOorb.getBlocks().size());
    OemDataBlock block = oemFromOorb.getBlocks().get(0);
    OemMetadata metadata = block.getMetadata();
    String expectedStart = "2019-01-02T00:01:09.184022373";
    String expectedStop = "2019-01-03T00:01:09.184022373";
    assertEquals(expectedStart, metadata.getStart_time());
    assertEquals(metadata.getStart_time(), metadata.getUsable_start_time());
    assertEquals(expectedStop, metadata.getStop_time());
    assertEquals(metadata.getStop_time(), metadata.getUsable_stop_time());

    //TODO Validate frame and timescale
    assertEquals(OdmCommonMetadata.CenterName.SUN, metadata.getCenter_name());
    assertEquals(OdmCommonMetadata.ReferenceFrame.J2000_IAU76ECLIP, metadata.getRef_frame());
    assertEquals(OdmCommonMetadata.TimeSystem.TT, metadata.getTime_system());
    assertEquals(2, block.getLines().size());
    //TODO Validate these conversion numbers
    double positionTolerance = 1e-15;
    double velocityTolerance = 1e-12;
    //2019-01-02T00:01:09 2.2439016200494885E8 2101124.491121584 -1.3365823487439504E-7 -0.22770539879383658 24.31785994349861 -3.052498245421107E-12
    OemDataLine line = block.getLines().get(0);
    assertEquals(expectedStart, line.getDate());
    assertEquals(2.2439016200494885E8, line.getPoint()[0], positionTolerance);
    assertEquals(2101124.491121584, line.getPoint()[1], positionTolerance);
    assertEquals(-1.3365823487439504E-7, line.getPoint()[2], positionTolerance);
    assertEquals(-0.22770539879383658, line.getPoint()[3], velocityTolerance);
    assertEquals(24.31785994349861, line.getPoint()[4], velocityTolerance);
    assertEquals(-3.052498245421107E-12, line.getPoint()[5], velocityTolerance);
  }

  @Test
  public void testOorbOemWithFrameConversionHappy() {
    String objectName = UUID.randomUUID().toString();
    String objectId = UUID.randomUUID().toString();
    String oorbEphemerisString = String.join("\n",
        "!!OID FORMAT x y z dx/dt dy/dt dz/dt H t_0 INDEX N_PAR MOID COMPCODE",
        "smoke CAR  0.149995559432062E+01  0.140451497813544E-01 -0.893450119807154E-15 -0.131510871962501E-03  0.140447393095277E-01 -0.176296525267191E-14  0.200000000000000E+02   0.58485000800741E+05 1 6 -0.100000000000000E+01 OPENORB",
        "smoke CAR  0.149975833233652E+01  0.280890681682720E-01 -0.345024787678361E-14 -0.263010213738389E-03  0.140428922497122E-01 -0.329957019328539E-14  0.200000000000000E+02   0.58486000800741E+05 1 6 -0.100000000000000E+01 OPENORB");
    OrbitEphemerisMessage oemFromOorb = OdmFormatter.parseOorbEphemerisString(oorbEphemerisString, objectName, objectId, true);
    assertEquals(1, oemFromOorb.getBlocks().size());
    OemDataBlock block = oemFromOorb.getBlocks().get(0);
    OemMetadata metadata = block.getMetadata();
    String expectedStart = "2019-01-02T00:01:09.184022373";
    String expectedStop = "2019-01-03T00:01:09.184022373";
    assertEquals(expectedStart, metadata.getStart_time());
    assertEquals(metadata.getStart_time(), metadata.getUsable_start_time());
    assertEquals(expectedStop, metadata.getStop_time());
    assertEquals(metadata.getStop_time(), metadata.getUsable_stop_time());

    //TODO Validate frame and timescale
    assertEquals(OdmCommonMetadata.CenterName.SUN, metadata.getCenter_name());
    assertEquals(OdmCommonMetadata.ReferenceFrame.ICRF, metadata.getRef_frame());
    assertEquals(OdmCommonMetadata.TimeSystem.TT, metadata.getTime_system());
    assertEquals(2, block.getLines().size());
    //TODO Validate these conversion numbers
    double positionTolerance = 1e-15;
    double velocityTolerance = 1e-12;
    //2019-01-02T00:01:09 2.2439016200494885E8 2101124.491121584 -1.3365823487439504E-7 -0.22770539879383658 24.31785994349861 -3.052498245421107E-12
    OemDataLine line = block.getLines().get(0);
    assertEquals(expectedStart, line.getDate());
    assertEquals(2.2439016200494885E8, line.getPoint()[0], positionTolerance);
    assertEquals(1927744.0307783443, line.getPoint()[1], positionTolerance);
    assertEquals(835779.3243371106, line.getPoint()[2], positionTolerance);
    assertEquals(-0.22770539879383658, line.getPoint()[3], velocityTolerance);
    assertEquals(22.311200286071877, line.getPoint()[4], velocityTolerance);
    assertEquals(9.673089166672684, line.getPoint()[5], velocityTolerance);
  }
}
