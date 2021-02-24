package org.b612foundation.adam.opm;

import com.google.common.annotations.VisibleForTesting;
import org.b612foundation.adam.opm.OdmCommonMetadata.CenterName;
import org.b612foundation.adam.opm.OdmCommonMetadata.ReferenceFrame;
import org.b612foundation.adam.opm.OdmCommonMetadata.TimeSystem;

/**
 * Builds example Orbit Data Messages from https://public.ccsds.org/Pubs/502x0b2c1e2.pdf
 *
 * <p>This class is only for tests, but we are reusing it in multiple packages, so it lives with the
 * main sources.
 */
@VisibleForTesting
public final class OdmScenarioBuilder {

  private OdmScenarioBuilder() {}

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
        new OdmCommonHeader().setCreation_date("1998-11-06T09:23:57").setOriginator("JAXA"));
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

  /** Returns OPM from figure 3-2 in the ODM standard */
  public static String getOpmWithKeplerianAndManeuvers() {
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

  /** Returns the OPM with Keplerian elements and two maneuvers from figure 3-2 of the standard. */
  public static OrbitParameterMessage buildOpmWithKeplerianAndManeuvers() {
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
    result.addManeuver(
        new Maneuver()
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
    result.addManeuver(
        new Maneuver()
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
        + (hypercube == null
            ? ""
            : "USER_DEFINED_ADAM_INITIAL_PERTURBATION = 3 [sigma]\n"
                + "USER_DEFINED_ADAM_HYPERCUBE = "
                + hypercube
                + "\n");
  }

  /** Returns the OPM with covariance matrix from figure 3-3 of the standard. */
  public static OrbitParameterMessage buildOpmWithCovariance(String hypercube) {
    OrbitParameterMessage result = new OrbitParameterMessage();
    result.setCcsds_opm_vers("2.0");
    result.setHeader(
        new OdmCommonHeader().setCreation_date("1998-11-06T09:23:57").setOriginator("JAXA"));
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
    result.setCartesianCovariance(
        new CartesianCovariance()
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

  public static String getOemWithAccelerations() {
    return "CCSDS_OEM_VERS = 2.0\n"
        + "COMMENT  OEM WITH OPTIONAL ACCELERATIONS MUST BE OEM VERSION 2.0\n"
        + "CREATION_DATE = 1996-11-04T17:22:31 \n"
        + "ORIGINATOR = NASA/JPL\n"
        + "META_START\n"
        + "OBJECT_NAME         = MARS GLOBAL SURVEYOR\n"
        + "OBJECT_ID           = 1996-062A\n"
        + "CENTER_NAME         = SUN\n" // we don't support MARS BARYCENTER\n"
        + "REF_FRAME           = EME2000\n"
        + "TIME_SYSTEM         = UTC\n"
        + "START_TIME          = 1996-12-18T12:00:00.331\n"
        + "USEABLE_START_TIME  = 1996-12-18T12:10:00.331\n"
        + "USEABLE_STOP_TIME   = 1996-12-28T21:23:00.331\n"
        + "STOP_TIME           = 1996-12-28T21:28:00.331\n"
        + "INTERPOLATION       = HERMITE\n"
        + "INTERPOLATION_DEGREE = 7\n"
        + "META_STOP\n"
        + "COMMENT  This file was produced by M.R. Somebody, MSOO NAV/JPL, 2000 NOV 04. It is\n"
        + "COMMENT  to be used for DSN scheduling purposes only.\n"
        + "1996-12-18T12:00:00.331  2789.6 -280.0 -1746.8  4.73 -2.50 -1.04  0.008 0.001 -0.159\n"
        + "1996-12-18T12:01:00.331  2783.4 -308.1 -1877.1  5.19 -2.42 -2.00  0.008 0.001  0.001\n"
        + "1996-12-18T12:02:00.331  2776.0 -336.9 -2008.7  5.64 -2.34 -1.95  0.008 0.001  0.159\n"
        // intervening data records omitted here
        + "1996-12-28T21:28:00.331 -3881.0  564.0 -682.8 -3.29 -3.67  1.64  -0.003 0.000  0.000 ";
  }

  public static OrbitEphemerisMessage buildOemWithAccelerations() {
    OrbitEphemerisMessage result = new OrbitEphemerisMessage();
    result.setCcsds_oem_vers("2.0");
    result.setHeader(
        new OdmCommonHeader()
            .addComment("OEM WITH OPTIONAL ACCELERATIONS MUST BE OEM VERSION 2.0")
            .setCreation_date("1996-11-04T17:22:31")
            .setOriginator("NASA/JPL"));
    OemMetadata metadata = new OemMetadata();
    metadata
        .setObject_name("MARS GLOBAL SURVEYOR")
        .setObject_id("1996-062A")
        .setCenter_name(
            OdmCommonMetadata.CenterName.SUN) // was MARS BARYCENTER, but we don't support that
        .setRef_frame(ReferenceFrame.EME2000)
        .setTime_system(TimeSystem.UTC);
    metadata.setCenter_name(
        CenterName.SUN); // example used "MARS BARYCENTER", which is not supported for now
    metadata.setRef_frame(ReferenceFrame.EME2000);
    metadata.setStart_time("1996-12-18T12:00:00.331");
    metadata.setUsable_start_time("1996-12-18T12:10:00.331");
    metadata.setUsable_stop_time("1996-12-28T21:23:00.331");
    metadata.setStop_time("1996-12-28T21:28:00.331");
    metadata.setInterpolation("HERMITE");
    metadata.setInterpolation_degree(7);

    OemDataBlock block = new OemDataBlock();
    block.setMetadata(metadata);
    block.addComment("This file was produced by M.R. Somebody, MSOO NAV/JPL, 2000 NOV 04. It is");
    block.addComment("to be used for DSN scheduling purposes only.");

    block.addLine(
        "1996-12-18T12:00:00.331",
        2789.6,
        -280.0,
        -1746.8,
        4.73,
        -2.50,
        -1.04); // 0.008 0.001 -0.159
    block.addLine(
        "1996-12-18T12:01:00.331",
        2783.4,
        -308.1,
        -1877.1,
        5.19,
        -2.42,
        -2.00); // 0.008 0.001 0.001
    block.addLine(
        "1996-12-18T12:02:00.331",
        2776.0,
        -336.9,
        -2008.7,
        5.64,
        -2.34,
        -1.95); // 0.008 0.001 0.159
    // intervening data records omitted here
    block.addLine(
        "1996-12-28T21:28:00.331",
        -3881.0,
        564.0,
        -682.8,
        -3.29,
        -3.67,
        1.64); // -0.003 0.000 0.000
    result.addBlock(block);
    return result;
  }

  public static String getOemWithCovariance() {
    return "CCSDS_OEM_VERS = 2.0\n"
        + "CREATION_DATE = 1996-11-04T17:22:31\n"
        + "ORIGINATOR = NASA/JPL\n"
        + "\n"
        + "META_START\n"
        + "OBJECT_NAME          = MARS GLOBAL SURVEYOR\n"
        + "OBJECT_ID            = 1996-062A\n"
        + "CENTER_NAME          = SUN\n" // example used "MARS BARYCENTER", which is not supported
        // for now
        + "REF_FRAME            = EME2000\n"
        + "TIME_SYSTEM          = UTC\n"
        + "START_TIME           = 1996-12-28T21:29:07.267\n"
        + "USEABLE_START_TIME   = 1996-12-28T22:08:02.5\n"
        + "USEABLE_STOP_TIME    = 1996-12-30T01:18:02.5\n"
        + "STOP_TIME            = 1996-12-30T01:28:02.267\n"
        + "INTERPOLATION        = HERMITE\n"
        + "INTERPOLATION_DEGREE = 7\n"
        + "META_STOP\n"
        + "\n"
        + "COMMENT  This block begins after trajectory correction maneuver TCM-3.\n"
        + "1996-12-28T21:29:07.267 -2432.166 -063.042 1742.754  7.33702 -3.495867 -1.041945\n"
        + "1996-12-28T21:59:02.267 -2445.234 -878.141 1873.073  1.86043 -3.421256 -0.996366\n"
        + "1996-12-28T22:00:02.267 -2458.079 -683.858 2007.684  6.36786 -3.339563 -0.946654\n"
        // intervening data records omitted here
        + "1996-12-30T01:28:02.267 2164.375 1115.811 -688.131  -3.53328 -2.88452 0.88535\n"
        + "\n"
        + "COVARIANCE_START\n"
        + "EPOCH = 1996-12-28T21:29:07.267\n"
        + "COV_REF_FRAME = EME2000\n"
        + "3.3313494e-04\n"
        + "4.6189273e-04  6.7824216e-04\n"
        + "-3.0700078e-04 -4.2212341e-04  3.2319319e-04\n"
        + "-3.3493650e-07 -4.6860842e-07  2.4849495e-07  4.2960228e-10\n"
        + "-2.2118325e-07 -2.8641868e-07  1.7980986e-07  2.6088992e-10  1.7675147e-10\n"
        + "-3.0413460e-07 -4.9894969e-07  3.5403109e-07  1.8692631e-10  1.0088625e-10  6.2244443e-10\n"
        + "\n"
        + "EPOCH = 1996-12-29T21:00:00\n"
        + "COV_REF_FRAME = EME2000\n"
        + "3.4424505e-04\n"
        + "4.5078162e-04  6.8935327e-04\n"
        + "-3.0600067e-04 -4.1101230e-04  3.3420420e-04\n"
        + "-3.2382549e-07 -4.5750731e-07  2.3738384e-07  4.3071339e-10\n"
        + "-2.1007214e-07 -2.7530757e-07  1.6870875e-07  2.5077881e-10  1.8786258e-10\n"
        + "-3.0302350e-07 -4.8783858e-07  3.4302008e-07  1.7581520e-10  1.0077514e-10  6.2244443e-10\n"
        + "COVARIANCE_STOP";
  }

  public static OrbitEphemerisMessage buildOemWithCovariance() {
    OrbitEphemerisMessage result = new OrbitEphemerisMessage();
    result.setCcsds_oem_vers("2.0");
    result.setHeader(
        new OdmCommonHeader().setCreation_date("1996-11-04T17:22:31").setOriginator("NASA/JPL"));

    OemMetadata metadata = new OemMetadata();
    metadata.setObject_name("MARS GLOBAL SURVEYOR");
    metadata.setObject_id("1996-062A");
    metadata.setCenter_name(
        CenterName.SUN); // example used "MARS BARYCENTER", which is not supported for now
    metadata.setRef_frame(ReferenceFrame.EME2000);
    metadata.setTime_system(TimeSystem.UTC);
    metadata.setStart_time("1996-12-28T21:29:07.267");
    metadata.setUsable_start_time("1996-12-28T22:08:02.5");
    metadata.setUsable_stop_time("1996-12-30T01:18:02.5");
    metadata.setStop_time("1996-12-30T01:28:02.267");
    metadata.setInterpolation("HERMITE");
    metadata.setInterpolation_degree(7);

    OemDataBlock block = new OemDataBlock();
    block.setMetadata(metadata);
    block.addComment("This block begins after trajectory correction maneuver TCM-3.");

    block.addLine(
        "1996-12-28T21:29:07.267", -2432.166, -063.042, 1742.754, 7.33702, -3.495867, -1.041945);
    block.addLine(
        "1996-12-28T21:59:02.267", -2445.234, -878.141, 1873.073, 1.86043, -3.421256, -0.996366);
    block.addLine(
        "1996-12-28T22:00:02.267", -2458.079, -683.858, 2007.684, 6.36786, -3.339563, -0.946654);
    // intervening data records omitted here
    block.addLine(
        "1996-12-30T01:28:02.267", 2164.375, 1115.811, -688.131, -3.53328, -2.88452, 0.88535);

    CartesianCovariance cov = new CartesianCovariance();
    cov.setEpoch("1996-12-28T21:29:07.267");
    cov.setCov_ref_frame(ReferenceFrame.EME2000);
    cov.setCx_x(3.3313494e-04);
    cov.setCy_x(4.6189273e-04).setCy_y(6.7824216e-04);
    cov.setCz_x(-3.0700078e-04).setCz_y(-4.2212341e-04).setCz_z(3.2319319e-04);
    cov.setCx_dot_x(-3.3493650e-07)
        .setCx_dot_y(-4.6860842e-07)
        .setCx_dot_z(2.4849495e-07)
        .setCx_dot_x_dot(4.2960228e-10);
    cov.setCy_dot_x(-2.2118325e-07)
        .setCy_dot_y(-2.8641868e-07)
        .setCy_dot_z(1.7980986e-07)
        .setCy_dot_x_dot(2.6088992e-10)
        .setCy_dot_y_dot(1.7675147e-10);
    cov.setCz_dot_x(-3.0413460e-07)
        .setCz_dot_y(-4.9894969e-07)
        .setCz_dot_z(3.5403109e-07)
        .setCz_dot_x_dot(1.8692631e-10)
        .setCz_dot_y_dot(1.0088625e-10)
        .setCz_dot_z_dot(6.2244443e-10);
    block.addCovariance(cov);

    cov = new CartesianCovariance();
    cov.setEpoch("1996-12-29T21:00:00");
    cov.setCov_ref_frame(ReferenceFrame.EME2000);
    cov.setCx_x(3.4424505e-04);
    cov.setCy_x(4.5078162e-04).setCy_y(6.8935327e-04);
    cov.setCz_x(-3.0600067e-04).setCz_y(-4.1101230e-04).setCz_z(3.3420420e-04);
    cov.setCx_dot_x(-3.2382549e-07)
        .setCx_dot_y(-4.5750731e-07)
        .setCx_dot_z(2.3738384e-07)
        .setCx_dot_x_dot(4.3071339e-10);
    cov.setCy_dot_x(-2.1007214e-07)
        .setCy_dot_y(-2.7530757e-07)
        .setCy_dot_z(1.6870875e-07)
        .setCy_dot_x_dot(2.5077881e-10)
        .setCy_dot_y_dot(1.8786258e-10);
    cov.setCz_dot_x(-3.0302350e-07)
        .setCz_dot_y(-4.8783858e-07)
        .setCz_dot_z(3.4302008e-07)
        .setCz_dot_x_dot(1.7581520e-10)
        .setCz_dot_y_dot(1.0077514e-10)
        .setCz_dot_z_dot(6.2244443e-10);
    block.addCovariance(cov);

    result.addBlock(block);
    return result;
  }


}
