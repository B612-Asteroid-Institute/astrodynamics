package org.b612foundation.adam.opm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OpmWriterTest {

  @Test
  public void testToCcsdsOpmWithCovarianceString() throws OdmParseException {
    String expectedString = "CCSDS_OPM_VERS = 2.0\n" +
        "CREATION_DATE = 1998-11-06T09:23:57\n" +
        "ORIGINATOR = JAXA\n" +
        "COMMENT GEOCENTRIC, CARTESIAN, EARTH FIXED\n" +
        "OBJECT_NAME = GODZILLA 5\n" +
        "OBJECT_ID = 1998-057A\n" +
        "CENTER_NAME = EARTH\n" +
        "REF_FRAME = ITRF97\n" +
        "TIME_SYSTEM = UTC\n" +
        "EPOCH = 1998-12-18T14:28:15.1172\n" +
        "X = 6503.514\n" +
        "Y = 1239.647\n" +
        "Z = -717.49\n" +
        "X_DOT = -0.87316\n" +
        "Y_DOT = 8.74042\n" +
        "Z_DOT = -4.191076\n" +
        "MASS = 3000.0\n" +
        "SOLAR_RAD_AREA = 18.77\n" +
        "SOLAR_RAD_COEFF = 1.0\n" +
        "DRAG_AREA = 18.77\n" +
        "DRAG_COEFF = 2.5\n" +
        "CX_X = 3.331349476038534E-4\n" +
        "CY_X = 4.618927349220216E-4\n" +
        "CY_Y = 6.782421679971363E-4\n" +
        "CZ_X = -3.070007847730449E-4\n" +
        "CZ_Y = -4.221234189514228E-4\n" +
        "CZ_Z = 3.231931992380369E-4\n" +
        "CX_DOT_X = -3.34936503392263E-7\n" +
        "CX_DOT_Y = -4.686084221046758E-7\n" +
        "CX_DOT_Z = 2.484949578400095E-7\n" +
        "CX_DOT_X_DOT = 4.29602280558729E-10\n" +
        "CY_DOT_X = -2.211832501084875E-7\n" +
        "CY_DOT_Y = -2.864186892102733E-7\n" +
        "CY_DOT_Z = 1.798098699846038E-7\n" +
        "CY_DOT_X_DOT = 2.608899201686016E-10\n" +
        "CY_DOT_Y_DOT = 1.767514756338532E-10\n" +
        "CZ_DOT_X = -3.041346050686871E-7\n" +
        "CZ_DOT_Y = -4.989496988610662E-7\n" +
        "CZ_DOT_Z = 3.540310904497689E-7\n" +
        "CZ_DOT_X_DOT = 1.86926319295459E-10\n" +
        "CZ_DOT_Y_DOT = 1.008862586240695E-10\n" +
        "CZ_DOT_Z_DOT = 6.2244443386355E-10\n";
    OrbitParameterMessage opm = OdmFormatter.parseOpmString(expectedString);
    String ccsdsString = OpmWriter.toCcsdsOpmString(opm);
    assertEquals(expectedString, ccsdsString);
  }

  @Test
  public void testToCcsdsOpmWithKeplerianAndManeuvers() throws OdmParseException {
    String expectedString = "CCSDS_OPM_VERS = 2.0\n" +
        "CREATION_DATE = 2000-06-03T05:33:00.000\n" +
        "ORIGINATOR = GSOC\n" +
        "OBJECT_NAME = EUTELSAT W4\n" +
        "OBJECT_ID = 2000-028A\n" +
        "CENTER_NAME = EARTH\n" +
        "REF_FRAME = TOD\n" +
        "TIME_SYSTEM = UTC\n" +
        "COMMENT State Vector\n" +
        "EPOCH = 2006-06-03T00:00:00.000\n" +
        "X = 6655.9942\n" +
        "Y = -40218.5751\n" +
        "Z = -82.9177\n" +
        "X_DOT = 3.11548208\n" +
        "Y_DOT = 0.47042605\n" +
        "Z_DOT = -0.00101495\n" +
        "COMMENT Keplerian elements\n" +
        "SEMI_MAJOR_AXIS = 41399.5123\n" +
        "ECCENTRICITY = 0.020842611\n" +
        "INCLINATION = 0.117746\n" +
        "RA_OF_ASC_NODE = 17.604721\n" +
        "ARG_OF_PERICENTER = 218.242943\n" +
        "TRUE_ANOMALY = 41.922339\n" +
        "GM = 398600.4415\n" +
        "COMMENT Spacecraft parameters\n" +
        "MASS = 1913.0\n" +
        "SOLAR_RAD_AREA = 10.0\n" +
        "SOLAR_RAD_COEFF = 1.3\n" +
        "DRAG_AREA = 10.0\n" +
        "DRAG_COEFF = 2.3\n" +
        "COMMENT 2 planned maneuvers\n" +
        "COMMENT First maneuver: AMF-3\n" +
        "COMMENT Non-impulsive, thrust direction fixed in inertial frame\n" +
        "MAN_EPOCH_IGNITION = 2000-06-03T09:00:34.1\n" +
        "MAN_DURATION = 132.6\n" +
        "MAN_DELTA_MASS = -18.418\n" +
        "MAN_REF_FRAME = EME2000\n" +
        "MAN_DV_1 = -0.023257\n" +
        "MAN_DV_2 = 0.0168316\n" +
        "MAN_DV_3 = -0.00893444\n" +
        "COMMENT Second maneuver: first station acquisition maneuver\n" +
        "COMMENT impulsive, thrust direction fixed in RTN frame\n" +
        "MAN_EPOCH_IGNITION = 2000-06-05T18:59:21.0\n" +
        "MAN_DURATION = 0.0\n" +
        "MAN_DELTA_MASS = -1.469\n" +
        "MAN_REF_FRAME = RTN\n" +
        "MAN_DV_1 = 0.001015\n" +
        "MAN_DV_2 = -0.001873\n" +
        "MAN_DV_3 = 0.0\n";
    OrbitParameterMessage opm = OdmFormatter.parseOpmString(expectedString);
    String ccsdsString = OpmWriter.toCcsdsOpmString(opm);
    assertEquals(expectedString, ccsdsString);
  }
}
