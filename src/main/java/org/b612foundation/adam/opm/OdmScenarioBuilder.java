package org.b612foundation.adam.opm;

/**
 * Builds example Orbit Data Messages from
 * http://public.ccsds.org/publications/archive/502x0b2c1.pdf
 *
 * This class is only for tests, but we are reusing it in multiple packages, so
 * it lives with the main sources.
 */
public class OdmScenarioBuilder {

  /** Returns OPM from figure 3-1 in the ODM standard */
  public static String getSimpleOpm() {
    return "CCSDS_OPM_VERS = 2.0\n"
         + "CREATION_DATE = 1998-11-06T09:23:57\n"
         + "ORIGINATOR = JAXA\n"
         + "COMMENT GEOCENTRIC, CARTESIAN, EARTH FIXED\n"
         + "OBJECT_NAME = GODZILLA 5\n"
         + "OBJECT_ID = 1998-057A\n"
         + "CENTER_NAME = EARTH\n"
         + "REF_FRAME = ITRF-97\n"
         + "TIME_SYSTEM = UTC\n"
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

  /** Returns simple Orbit Parameter Message from figure 3-1 of the standard. */
  public static OrbitParameterMessage buildSimpleOpm() {
    OrbitParameterMessage result = new OrbitParameterMessage();
    result.setCcsds_opm_vers("2.0");
    result.setHeader(
        new OdmCommonHeader()
            .setCreation_date("1998-11-06T09:23:57")
            .setOriginator("JAXA"));
    result.setMetadata(
        new OdmCommonMetadata()
            .addComment("GEOCENTRIC, CARTESIAN, EARTH FIXED")
            .setObject_name("GODZILLA 5")
            .setObject_id("1998-057A")
            .setCenter_name(OdmCommonMetadata.CenterName.EARTH)
            .setRef_frame(OdmCommonMetadata.ReferenceFrame.ITRF97)
            .setTime_system(OdmCommonMetadata.TimeSystem.UTC));
    result.setState_vector(
        new StateVector()
            .setEpoch("1998-12-18T14:28:15.1172")
            .setX(6503.514000)
            .setY(1239.647000)
            .setZ(-717.490000)
            .setX_dot(-0.873160)
            .setY_dot(8.740420)
            .setZ_dot(-4.191076));
    result.setSpacecraft(
        new SpacecraftParameters()
            .setMass(3000.000000)
            .setSolar_rad_area(18.770000)
            .setSolar_rad_coeff(1.000000)
            .setDrag_area(18.770000)
            .setDrag_coeff(2.500000));
    return result;
  }
  
  /** Returns OPM from figure 3-1 in the ODM standard but USING 'TT' as the TIME_SYSTEM instead of 'UTC' */
  public static String getSimpleOpmTT() {
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

  /** Returns simple Orbit Parameter Message from figure 3-1 of the standard, but USING 'TT' as the TIME_SYSTEM instead of 'UTC' */
  public static OrbitParameterMessage buildSimpleOpmTT() {
    OrbitParameterMessage result = new OrbitParameterMessage();
    result.setCcsds_opm_vers("2.0");
    result.setHeader(
        new OdmCommonHeader()
            .setCreation_date("1998-11-06T09:23:57")
            .setOriginator("JAXA"));
    result.setMetadata(
        new OdmCommonMetadata()
            .addComment("GEOCENTRIC, CARTESIAN, EARTH FIXED")
            .setObject_name("GODZILLA 5")
            .setObject_id("1998-057A")
            .setCenter_name(OdmCommonMetadata.CenterName.EARTH)
            .setRef_frame(OdmCommonMetadata.ReferenceFrame.ITRF97)
            .setTime_system(OdmCommonMetadata.TimeSystem.TT));
    result.setState_vector(
        new StateVector()
            .setEpoch("1998-12-18T14:28:15.1172")
            .setX(6503.514000)
            .setY(1239.647000)
            .setZ(-717.490000)
            .setX_dot(-0.873160)
            .setY_dot(8.740420)
            .setZ_dot(-4.191076));
    result.setSpacecraft(
        new SpacecraftParameters()
            .setMass(3000.000000)
            .setSolar_rad_area(18.770000)
            .setSolar_rad_coeff(1.000000)
            .setDrag_area(18.770000)
            .setDrag_coeff(2.500000));
    return result;
  } 
  
  /** Returns OPM from figure 3-2 in the ODM standard */
  public static String getOpmWithKepelerianAndManuevers() {
    return "CCSDS_OPM_VERS = 2.0\n"
         + "COMMENT Generated by GSOC, R. Kiehling\n"
         + "COMMENT Current intermediate orbit IO2 and maneuver planning data\n"
         + "CREATION_DATE = 2000-06-03T05:33:00.000\n"
         + "ORIGINATOR = GSOC\n"
         + "OBJECT_NAME = EUTELSAT W4\n"
         + "OBJECT_ID = 2000-028A\n"
         + "CENTER_NAME = EARTH\n"
         + "REF_FRAME = TOD\n"
         + "TIME_SYSTEM = UTC\n"
         + "COMMENT State Vector\n"
         + "EPOCH = 2006-06-03T00:00:00.000\n"
         + "X = 6655.9942 [km]\n"
         + "Y = -40218.5751 [km]\n"
         + "Z = -82.9177 [km]\n"
         + "X_DOT = 3.11548208 [km/s]\n"
         + "Y_DOT = 0.47042605 [km/s]\n"
         + "Z_DOT = -0.00101495 [km/s]\n"
         + "COMMENT Keplerian elements\n"
         + "SEMI_MAJOR_AXIS = 41399.5123 [km]\n"
         + "ECCENTRICITY = 0.020842611\n"
         + "INCLINATION = 0.117746 [deg]\n"
         + "RA_OF_ASC_NODE = 17.604721 [deg]\n"
         + "ARG_OF_PERICENTER = 218.242943 [deg]\n"
         + "TRUE_ANOMALY = 41.922339 [deg]\n"
         + "GM = 398600.4415 [km**3/s**2]\n"
         + "COMMENT Spacecraft parameters\n"
         + "MASS = 1913.000 [kg]\n"
         + "SOLAR_RAD_AREA = 10.000 [m**2]\n"
         + "SOLAR_RAD_COEFF = 1.300\n"
         + "DRAG_AREA = 10.000 [m**2]\n"
         + "DRAG_COEFF = 2.300\n"
         + "COMMENT 2 planned maneuvers\n"
         + "COMMENT First maneuver: AMF-3\n"
         + "COMMENT Non-impulsive, thrust direction fixed in inertial frame\n"
         + "MAN_EPOCH_IGNITION = 2000-06-03T09:00:34.1\n"
         + "MAN_DURATION = 132.60 [s]\n"
         + "MAN_DELTA_MASS = -18.418 [kg]\n"
         + "MAN_REF_FRAME = EME2000\n"
         + "MAN_DV_1 = -0.02325700 [km/s]\n"
         + "MAN_DV_2 = 0.01683160 [km/s]\n"
         + "MAN_DV_3 = -0.00893444 [km/s]\n"
         + "COMMENT Second maneuver: first station acquisition maneuver\n"
         + "COMMENT impulsive, thrust direction fixed in RTN frame\n"
         + "MAN_EPOCH_IGNITION = 2000-06-05T18:59:21.0\n"
         + "MAN_DURATION = 0.00 [s]\n"
         + "MAN_DELTA_MASS = -1.469 [kg]\n"
         + "MAN_REF_FRAME = RTN\n"
         + "MAN_DV_1 = 0.00101500 [km/s]\n"
         + "MAN_DV_2 = -0.00187300 [km/s]\n"
         + "MAN_DV_3 = 0.00000000 [km/s]";
  }

  /** Returns the OPM with Keplerian elements and two manuevers from figure 3-2 of the standard. */
  public static OrbitParameterMessage buildOpmWithKepelerianAndManuevers() {
    OrbitParameterMessage result = new OrbitParameterMessage();
    result.setCcsds_opm_vers("2.0");
    result.setHeader(
        new OdmCommonHeader()
            .addComment("Generated by GSOC, R. Kiehling")
            .addComment("Current intermediate orbit IO2 and maneuver planning data")
            .setCreation_date("2000-06-03T05:33:00.000")
            .setOriginator("GSOC"));
    result.setMetadata(
        new OdmCommonMetadata()
            .setObject_name("EUTELSAT W4")
            .setObject_id("2000-028A")
            .setCenter_name(OdmCommonMetadata.CenterName.EARTH)
            .setRef_frame(OdmCommonMetadata.ReferenceFrame.TOD)
            .setTime_system(OdmCommonMetadata.TimeSystem.UTC));
    result.setState_vector(
        new StateVector()
            .addComment("State Vector")
            .setEpoch("2006-06-03T00:00:00.000")
            .setX(6655.9942)
            .setY(-40218.5751)
            .setZ(-82.9177)
            .setX_dot(3.11548208)
            .setY_dot(0.47042605)
            .setZ_dot(-0.00101495));
    result.setKeplerian(
        new KeplerianElements()
            .addComment("Keplerian elements")
            .setSemi_major_axis(41399.5123)
            .setEccentricity(0.020842611)
            .setInclination(0.117746)
            .setRa_of_asc_node(17.604721)
            .setArg_of_pericenter(218.242943)
            .setTrue_anomaly(41.922339)
            .setGm(398600.4415));
    result.setSpacecraft(
        new SpacecraftParameters()
            .addComment("Spacecraft parameters")
            .setMass(1913.000)
            .setSolar_rad_area(10.000)
            .setSolar_rad_coeff(1.300)
            .setDrag_area(10.000)
            .setDrag_coeff(2.300));
    result.addManuever(
        new Manuever()
            .addComment("2 planned maneuvers")
            .addComment("First maneuver: AMF-3")
            .addComment("Non-impulsive, thrust direction fixed in inertial frame")
            .setMan_epoch_ignition("2000-06-03T09:00:34.1")
            .setDuration(132.60)
            .setDelta_mass(-18.418)
            .setMan_ref_frame(OdmCommonMetadata.ReferenceFrame.EME2000)
            .setMan_dv_1(-0.02325700)
            .setMan_dv_2(0.01683160)
            .setMan_dv_3(-0.00893444));
    result.addManuever(
        new Manuever()
            .addComment("Second maneuver: first station acquisition maneuver")
            .addComment("impulsive, thrust direction fixed in RTN frame")
            .setMan_epoch_ignition("2000-06-05T18:59:21.0")
            .setDuration(0.00)
            .setDelta_mass(-1.469)
            .setMan_ref_frame(OdmCommonMetadata.ReferenceFrame.RTN)
            .setMan_dv_1(0.00101500)
            .setMan_dv_2(-0.00187300)
            .setMan_dv_3(0.00000000));
    return result;
  }

  /** returns OPM with covariance matrix from figure 3-3 of the standard. */
  public static String getOpmWithCovariance(String hypercube) {
    return "CCSDS_OPM_VERS = 2.0\n"
         + "CREATION_DATE = 1998-11-06T09:23:57\n"
         + "ORIGINATOR = JAXA\n"
         + "COMMENT GEOCENTRIC, CARTESIAN, EARTH FIXED\n"
         + "OBJECT_NAME = GODZILLA 5\n"
         + "OBJECT_ID = 1998-057A\n"
         + "CENTER_NAME = EARTH\n"
         + "REF_FRAME = ITRF-97\n"
         + "TIME_SYSTEM = UTC\n"
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
         + "DRAG_COEFF = 2.500000\n"
         + "CX_X = 3.331349476038534e-04\n"
         + "CY_X = 4.618927349220216e-04\n"
         + "CY_Y = 6.782421679971363e-04\n"
         + "CZ_X = -3.070007847730449e-04\n"
         + "CZ_Y = -4.221234189514228e-04\n"
         + "CZ_Z = 3.231931992380369e-04\n"
         + "CX_DOT_X = -3.349365033922630e-07\n"
         + "CX_DOT_Y = -4.686084221046758e-07\n"
         + "CX_DOT_Z = 2.484949578400095e-07\n"
         + "CX_DOT_X_DOT = 4.296022805587290e-10\n"
         + "CY_DOT_X = -2.211832501084875e-07\n"
         + "CY_DOT_Y = -2.864186892102733e-07\n"
         + "CY_DOT_Z = 1.798098699846038e-07\n"
         + "CY_DOT_X_DOT = 2.608899201686016e-10\n"
         + "CY_DOT_Y_DOT = 1.767514756338532e-10\n"
         + "CZ_DOT_X = -3.041346050686871e-07\n"
         + "CZ_DOT_Y = -4.989496988610662e-07\n"
         + "CZ_DOT_Z = 3.540310904497689e-07\n"
         + "CZ_DOT_X_DOT = 1.869263192954590e-10\n"
         + "CZ_DOT_Y_DOT = 1.008862586240695e-10\n"
         + "CZ_DOT_Z_DOT = 6.224444338635500e-10\n"
         + (hypercube == null ? ""
             : "USER_DEFINED_ADAM_INITIAL_PERTURBATION = 3 [sigma]\n"
             + "USER_DEFINED_ADAM_HYPERCUBE = " + hypercube + "\n");
  }

  /** Returns the OPM with covariance matrix from figure 3-3 of the standard. */
  public static OrbitParameterMessage buildOpmWithCovariance(String hypercube) {
    OrbitParameterMessage result = new OrbitParameterMessage();
    result.setCcsds_opm_vers("2.0");
    result.setHeader(
        new OdmCommonHeader()
            .setCreation_date("1998-11-06T09:23:57")
            .setOriginator("JAXA"));
    result.setMetadata(
        new OdmCommonMetadata()
            .addComment("GEOCENTRIC, CARTESIAN, EARTH FIXED")
            .setObject_name("GODZILLA 5")
            .setObject_id("1998-057A")
            .setCenter_name(OdmCommonMetadata.CenterName.EARTH)
            .setRef_frame(OdmCommonMetadata.ReferenceFrame.ITRF97)
            .setTime_system(OdmCommonMetadata.TimeSystem.UTC));
    result.setState_vector(
        new StateVector()
            .setEpoch("1998-12-18T14:28:15.1172")
            .setX(6503.514000)
            .setY(1239.647000)
            .setZ(-717.490000)
            .setX_dot(-0.873160)
            .setY_dot(8.740420)
            .setZ_dot(-4.191076));
    result.setSpacecraft(
        new SpacecraftParameters()
            .setMass(3000.000000)
            .setSolar_rad_area(18.770000)
            .setSolar_rad_coeff(1.000000)
            .setDrag_area(18.770000)
            .setDrag_coeff(2.500000));
    result.setCovariance(
        new CovarianceMatrix()
            .setCx_x(3.331349476038534e-04)
            .setCy_x(4.618927349220216e-04)
            .setCy_y(6.782421679971363e-04)
            .setCz_x(-3.070007847730449e-04)
            .setCz_y(-4.221234189514228e-04)
            .setCz_z(3.231931992380369e-04)
            .setCx_dot_x(-3.349365033922630e-07)
            .setCx_dot_y(-4.686084221046758e-07)
            .setCx_dot_z(2.484949578400095e-07)
            .setCx_dot_x_dot(4.296022805587290e-10)
            .setCy_dot_x(-2.211832501084875e-07)
            .setCy_dot_y(-2.864186892102733e-07)
            .setCy_dot_z(1.798098699846038e-07)
            .setCy_dot_x_dot(2.608899201686016e-10)
            .setCy_dot_y_dot(1.767514756338532e-10)
            .setCz_dot_x(-3.041346050686871e-07)
            .setCz_dot_y(-4.989496988610662e-07)
            .setCz_dot_z(3.540310904497689e-07)
            .setCz_dot_x_dot(1.869263192954590e-10)
            .setCz_dot_y_dot(1.008862586240695e-10)
            .setCz_dot_z_dot(6.224444338635500e-10));
    if (hypercube != null) {
      result.addAdam_field("INITIAL_PERTURBATION", "3");
      result.addAdam_field("HYPERCUBE", hypercube);
    }

    return result;
  }
}
