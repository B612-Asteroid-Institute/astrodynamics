package org.b612foundation.adam.opm;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for translating between text-based ODM formats and classes in this package. The CCSDS ODM standard is here:
 * https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public final class OdmFormatter {
  private static final String CCSDS_OPM_VERS = "CCSDS_OPM_VERS";
  private static final String CCSDS_OEM_VERS = "CCSDS_OEM_VERS";
  private static final String COMMENT = "COMMENT";
  private static final String CREATION_DATE = "CREATION_DATE";
  private static final String ORIGINATOR = "ORIGINATOR";
  private static final String OBJECT_NAME = "OBJECT_NAME";
  private static final String OBJECT_ID = "OBJECT_ID";
  private static final String CENTER_NAME = "CENTER_NAME";
  private static final String REF_FRAME = "REF_FRAME";
  private static final String REF_FRAME_EPOCH = "REF_FRAME_EPOCH";
  private static final String TIME_SYSTEM = "TIME_SYSTEM";
  private static final String EPOCH = "EPOCH";
  private static final String X_COORD = "X";
  private static final String Y_COORD = "Y";
  private static final String Z_COORD = "Z";
  private static final String X_DOT = "X_DOT";
  private static final String Y_DOT = "Y_DOT";
  private static final String Z_DOT = "Z_DOT";
  private static final String SEMI_MAJOR_AXIS = "SEMI_MAJOR_AXIS";
  private static final String ECCENTRICITY = "ECCENTRICITY";
  private static final String INCLINATION = "INCLINATION";
  private static final String RA_OF_ASC_NODE = "RA_OF_ASC_NODE";
  private static final String ARG_OF_PERICENTER = "ARG_OF_PERICENTER";
  private static final String TRUE_ANOMALY = "TRUE_ANOMALY";
  private static final String MEAN_ANOMALY = "MEAN_ANOMALY";
  private static final String GRAVITATIONAL_COEFF = "GM";
  private static final String MASS = "MASS";
  private static final String SOLAR_RAD_AREA = "SOLAR_RAD_AREA";
  private static final String SOLAR_RAD_COEFF = "SOLAR_RAD_COEFF";
  private static final String DRAG_AREA = "DRAG_AREA";
  private static final String DRAG_COEFF = "DRAG_COEFF";
  private static final String COV_REF_FRAME = "COV_REF_FRAME";
  private static final String CX_X = "CX_X";
  private static final String CY_X = "CY_X";
  private static final String CY_Y = "CY_Y";
  private static final String CZ_X = "CZ_X";
  private static final String CZ_Y = "CZ_Y";
  private static final String CZ_Z = "CZ_Z";
  private static final String CX_DOT_X = "CX_DOT_X";
  private static final String CX_DOT_Y = "CX_DOT_Y";
  private static final String CX_DOT_Z = "CX_DOT_Z";
  private static final String CX_DOT_X_DOT = "CX_DOT_X_DOT";
  private static final String CY_DOT_X = "CY_DOT_X";
  private static final String CY_DOT_Y = "CY_DOT_Y";
  private static final String CY_DOT_Z = "CY_DOT_Z";
  private static final String CY_DOT_X_DOT = "CY_DOT_X_DOT";
  private static final String CY_DOT_Y_DOT = "CY_DOT_Y_DOT";
  private static final String CZ_DOT_X = "CZ_DOT_X";
  private static final String CZ_DOT_Y = "CZ_DOT_Y";
  private static final String CZ_DOT_Z = "CZ_DOT_Z";
  private static final String CZ_DOT_X_DOT = "CZ_DOT_X_DOT";
  private static final String CZ_DOT_Y_DOT = "CZ_DOT_Y_DOT";
  private static final String CZ_DOT_Z_DOT = "CZ_DOT_Z_DOT";
  private static final String MAN_EPOCH_IGNITION = "MAN_EPOCH_IGNITION";
  private static final String MAN_DURATION = "MAN_DURATION";
  private static final String MAN_DELTA_MASS = "MAN_DELTA_MASS";
  private static final String MAN_REF_FRAME = "MAN_REF_FRAME";
  private static final String MAN_DV_1 = "MAN_DV_1";
  private static final String MAN_DV_2 = "MAN_DV_2";
  private static final String MAN_DV_3 = "MAN_DV_3";
  // OEM fields
  private static final String META_START = "META_START";
  private static final String META_STOP = "META_STOP";
  private static final String START_TIME = "START_TIME";
  private static final String USEABLE_START_TIME = "USEABLE_START_TIME";
  private static final String USEABLE_STOP_TIME = "USEABLE_STOP_TIME";
  private static final String STOP_TIME = "STOP_TIME";
  private static final String INTERPOLATION = "INTERPOLATION";
  private static final String INTERPOLATION_DEGREE = "INTERPOLATION_DEGREE";
  private static final String COVARIANCE_START = "COVARIANCE_START";
  private static final String COVARIANCE_STOP = "COVARIANCE_STOP";

  private static final String ADAM_PREFIX = "USER_DEFINED_ADAM_";

  /* Do not instantiate. */
  private OdmFormatter() {
  }

  /**
   * Returns the string with mutable fields removed to facilitate search for duplicates.
   */
  public static String removeMutableFields(String originalOpm) {
    final String OPM_CREATION = "CREATION_DATE.*";
    return originalOpm.replaceAll(OPM_CREATION, "");
  }

  /**
   * Parses OPM message as described in the standard. From all user defined fields, pulls only adam-specific ones.
   */
  public static OrbitParameterMessage parseOpmString(String buffer) throws OdmParseException {
    ArrayList<String> lines = getNonEmptyLines(buffer);
    OrbitParameterMessage result = new OrbitParameterMessage();
    result.setCcsds_opm_vers(extractField(lines, CCSDS_OPM_VERS));
    result.setHeader(parseCommonHeader(lines));
    OdmCommonMetadata metadata = new OdmCommonMetadata();
    parseCommonMetadata(lines, metadata);
    result.setMetadata(metadata);
    result.setState_vector(parseStateVector(lines));
    // Several sections are optional, but each may contains COMMENT, so look
    // ahead.
    if (containsLater(lines, ECCENTRICITY)) {
      result.setKeplerian(parseKeplerian(lines));
    }
    if (containsLater(lines, MASS)) {
      result.setSpacecraft(parseSpacecraft(lines));
    }
    if (containsLater(lines, CX_X)) {
      result.setCovariance(parseLongFormCovariance(lines));
    }
    while (containsLater(lines, MAN_EPOCH_IGNITION)) {
      result.addManeuver(parseManeuver(lines));
    }

    // CCSDS ODM standard allows for USER_DEFINED_X fields. We store
    // ADAM-specific values as USER_DEFINED_ADAM_X. The following pulls out the
    // X and the value out of such fields. Just like with normal ODM fields,
    // units can be included in square brackets in the end of the line, but they
    // are ignored (they are given only for the benefit of the human readers).
    // So, we allow units, but drop them. For example, line
    // "USER_DEFINED_ADAM_INITIAL_PERTURBATION = 3 [sigma]"
    // produces a key-value pair "INITIAL_PERTURBATION" : "3", with sigma being
    // the implied unit for perturbation.
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).startsWith(ADAM_PREFIX)) {
        String str = lines.get(i).substring(ADAM_PREFIX.length()).trim();
        lines.remove(i--);
        // Drop units, silently.
        if (str.endsWith("]")) {
          int pos = str.lastIndexOf("[");
          if (pos < 0) {
            throw new OdmParseException("ADAM string has ], but no [: " + str);
          }
          str = str.substring(0, pos).trim();
        }
        String[] parts = str.split("=");
        if (parts.length != 2) {
          throw new OdmParseException("Expected 'key = value', got " + str);
        }
        result.addAdam_field(parts[0].trim(), parts[1].trim());
      }
    }
    if (!lines.isEmpty()) {
      throw new OdmParseException("Unparsed lines in OPM: " + lines);
    }
    return result;
  }

  public static OrbitEphemerisMessage parseOemString(String buffer) throws OdmParseException {
    ArrayList<String> lines = getNonEmptyLines(buffer);
    OrbitEphemerisMessage result = new OrbitEphemerisMessage();
    result.setCcsds_oem_vers(extractField(lines, CCSDS_OEM_VERS));
    result.setHeader(parseCommonHeader(lines));
    // There may be multiple blocks of data, each with its own metadata and optional covarience.
    while (containsLater(lines, META_START)) {
      result.addBlock(parseOemBlock(lines));
    }
    if (!lines.isEmpty()) {
      throw new OdmParseException("Unparsed lines in OEM: " + lines);
    }
    return result;
  }

  /**
   * Parses common header lines. Assumes the input lines all have data. On success removes the parsed lines from the
   * list.
   */
  private static OdmCommonHeader parseCommonHeader(List<String> lines) throws OdmParseException {
    OdmCommonHeader result = new OdmCommonHeader();
    while (containsNext(lines, COMMENT)) {
      result.addComment(extractField(lines, COMMENT));
    }
    // TODO: Here and everywhere, verify the timestamp's format.
    result.setCreation_date(extractField(lines, CREATION_DATE));
    result.setOriginator(extractField(lines, ORIGINATOR));
    return result;
  }

  private static OemDataBlock parseOemBlock(List<String> lines) throws OdmParseException {
    OemDataBlock block = new OemDataBlock();

    OemMetadata metadata = new OemMetadata();
    parseOemMetadata(lines, metadata);
    block.setMetadata(metadata);

    // Comments are optional, expect them to come as a group.
    while (containsNext(lines, COMMENT)) {
      block.addComment(extractField(lines, COMMENT));
    }

    // The data comes until the end of file, another META_START, or COVARIANCE_START
    while (!lines.isEmpty() && !containsNext(lines, META_START) && !containsNext(lines, COVARIANCE_START)) {
      String line = lines.remove(0);
      // Date x y z vx vy vz [ax ay az]. We ignore accelerations for now.
      String[] parts = line.split("\\s+");
      // for (int i = 0; i < parts.length; i++) System.out.println("LINE " + i + "[" + parts[i] + "]");
      if (parts.length < 7) {
        throw new OdmParseException("Ephemeris data should contain at least date, position, and velocity. Got " + line);
      }
      try {
        block.addLine(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
            Double.parseDouble(parts[4]), Double.parseDouble(parts[5]), Double.parseDouble(parts[6]));
      } catch (NumberFormatException e) {
        throw new OdmParseException("Couldn't parse ephemeris line " + line + ": " + e);
      }
    }

    // If there was no covariance, we are done.
    if (!containsNext(lines, COVARIANCE_START)) {
      return block;
    }

    // Eat the covariance tag.
    lines.remove(0);

    // There may be multiple entries until the end tag.
    while (!lines.isEmpty() && !containsNext(lines, COVARIANCE_STOP)) {
      block.addCovariance(parseShortFormCovariance(lines));
    }

    // Eat closing tag.
    if (containsNext(lines, COVARIANCE_STOP)) {
      lines.remove(0);
    }
    return block;
  }

  /**
   * Parses common metadata fields among three ODM types. On success removes the parsed lines from the list.
   */
  private static void parseCommonMetadata(List<String> lines, OdmCommonMetadata result) throws OdmParseException {
    while (containsNext(lines, COMMENT)) {
      result.addComment(extractField(lines, COMMENT));
    }
    result.setObject_name(extractField(lines, OBJECT_NAME));
    result.setObject_id(extractField(lines, OBJECT_ID));
    String center = extractField(lines, CENTER_NAME);
    result.setCenter_name(parseCenterName(center));
    String frame = extractField(lines, REF_FRAME);
    result.setRef_frame(parseReferenceFrame(frame));
    // Ref frame epoch is optional, other fields are mandatory.
    if (containsNext(lines, REF_FRAME_EPOCH)) {
      result.setRef_frame_epoch(extractField(lines, REF_FRAME_EPOCH));
    }
    String time = extractField(lines, TIME_SYSTEM);
    result.setTime_system(parseTimeSystem(time));
  }

  /**
   * Parses OEM metadata block, which has a few fields in addition to the common metadata block. On success removes the
   * parsed lines from the list.
   */
  private static void parseOemMetadata(List<String> lines, OemMetadata result) throws OdmParseException {
    if (!containsNext(lines, META_START)) {
      throw new OdmParseException("OEM metadata block missing " + META_START);
    }
    lines.remove(0); // consume START
    parseCommonMetadata(lines, result);
    result.setStart_time(extractField(lines, START_TIME));
    if (containsNext(lines, USEABLE_START_TIME)) {
      result.setUsable_start_time(extractField(lines, USEABLE_START_TIME));
    }
    if (containsNext(lines, USEABLE_STOP_TIME)) {
      result.setUsable_stop_time(extractField(lines, USEABLE_STOP_TIME));
    }
    result.setStop_time(extractField(lines, STOP_TIME));
    if (containsNext(lines, INTERPOLATION)) {
      result.setInterpolation(extractField(lines, INTERPOLATION));
    }
    if (containsNext(lines, INTERPOLATION_DEGREE)) {
      String value = extractField(lines, INTERPOLATION_DEGREE);
      try {
        result.setInterpolation_degree(Integer.parseInt(value));
      } catch (NumberFormatException e) {
        throw new OdmParseException(INTERPOLATION_DEGREE + " should be an integer, got " + value);
      }
    }
    if (!containsNext(lines, META_STOP)) {
      throw new OdmParseException("OEM metadata block missing " + META_STOP);
    }
    lines.remove(0); // consume STOP
  }

  /** Parses the state vector section. Removes parsed lines from the list. */
  private static StateVector parseStateVector(List<String> lines) throws OdmParseException {
    StateVector result = new StateVector();
    while (containsNext(lines, COMMENT)) {
      result.addComment(extractField(lines, COMMENT));
    }
    result.setEpoch(extractField(lines, EPOCH));
    result.setX(extractDoubleNoUnits(lines, X_COORD));
    result.setY(extractDoubleNoUnits(lines, Y_COORD));
    result.setZ(extractDoubleNoUnits(lines, Z_COORD));
    result.setX_dot(extractDoubleNoUnits(lines, X_DOT));
    result.setY_dot(extractDoubleNoUnits(lines, Y_DOT));
    result.setZ_dot(extractDoubleNoUnits(lines, Z_DOT));
    return result;
  }

  /**
   * Parses osculating Keplerian elements. Removes parsed lines from the list.
   */
  private static KeplerianElements parseKeplerian(ArrayList<String> lines) throws OdmParseException {
    KeplerianElements result = new KeplerianElements();
    while (containsNext(lines, COMMENT)) {
      result.addComment(extractField(lines, COMMENT));
    }
    result.setSemi_major_axis(extractDoubleNoUnits(lines, SEMI_MAJOR_AXIS));
    result.setEccentricity(extractDoubleNoUnits(lines, ECCENTRICITY));
    result.setInclination(extractDoubleNoUnits(lines, INCLINATION));
    result.setRa_of_asc_node(extractDoubleNoUnits(lines, RA_OF_ASC_NODE));
    result.setArg_of_pericenter(extractDoubleNoUnits(lines, ARG_OF_PERICENTER));
    // Either True Anomaly or Mean Anomaly must be specified.
    if (containsNext(lines, TRUE_ANOMALY)) {
      result.setTrue_anomaly(extractDoubleNoUnits(lines, TRUE_ANOMALY));
    } else {
      result.setMean_anomaly(extractDoubleNoUnits(lines, MEAN_ANOMALY));
    }
    result.setGm(extractDoubleNoUnits(lines, GRAVITATIONAL_COEFF));
    return result;
  }

  /** Parses spacecraft data. Removes parsed lines from the list. */
  private static SpacecraftParameters parseSpacecraft(ArrayList<String> lines) throws OdmParseException {
    SpacecraftParameters result = new SpacecraftParameters();
    while (containsNext(lines, COMMENT)) {
      result.addComment(extractField(lines, COMMENT));
    }
    result.setMass(extractDoubleNoUnits(lines, MASS));
    result.setSolar_rad_area(extractDoubleNoUnits(lines, SOLAR_RAD_AREA));
    result.setSolar_rad_coeff(extractDoubleNoUnits(lines, SOLAR_RAD_COEFF));
    result.setDrag_area(extractDoubleNoUnits(lines, DRAG_AREA));
    result.setDrag_coeff(extractDoubleNoUnits(lines, DRAG_COEFF));
    return result;
  }

  /**
   * Parses the long form of the covariance matrix (each element on a separate line with a variable name). Removes
   * parsed lines from the list. The long form is used in OPM and OMM. OEM uses a different (short) format for the same
   * data.
   */
  private static CovarianceMatrix parseLongFormCovariance(List<String> lines) throws OdmParseException {
    CovarianceMatrix result = new CovarianceMatrix();
    while (containsNext(lines, COMMENT)) {
      result.addComment(extractField(lines, COMMENT));
    }
    // Covariance reference frame may be omitted, in which case the reference
    // frame from the
    // metadata section is assumed.
    if (containsNext(lines, COV_REF_FRAME)) {
      String frame = extractField(lines, COV_REF_FRAME);
      result.setCov_ref_frame(parseReferenceFrame(frame));
    }
    result.setCx_x(extractDoubleNoUnits(lines, CX_X));
    result.setCy_x(extractDoubleNoUnits(lines, CY_X));
    result.setCy_y(extractDoubleNoUnits(lines, CY_Y));
    result.setCz_x(extractDoubleNoUnits(lines, CZ_X));
    result.setCz_y(extractDoubleNoUnits(lines, CZ_Y));
    result.setCz_z(extractDoubleNoUnits(lines, CZ_Z));
    result.setCx_dot_x(extractDoubleNoUnits(lines, CX_DOT_X));
    result.setCx_dot_y(extractDoubleNoUnits(lines, CX_DOT_Y));
    result.setCx_dot_z(extractDoubleNoUnits(lines, CX_DOT_Z));
    result.setCx_dot_x_dot(extractDoubleNoUnits(lines, CX_DOT_X_DOT));
    result.setCy_dot_x(extractDoubleNoUnits(lines, CY_DOT_X));
    result.setCy_dot_y(extractDoubleNoUnits(lines, CY_DOT_Y));
    result.setCy_dot_z(extractDoubleNoUnits(lines, CY_DOT_Z));
    result.setCy_dot_x_dot(extractDoubleNoUnits(lines, CY_DOT_X_DOT));
    result.setCy_dot_y_dot(extractDoubleNoUnits(lines, CY_DOT_Y_DOT));
    result.setCz_dot_x(extractDoubleNoUnits(lines, CZ_DOT_X));
    result.setCz_dot_y(extractDoubleNoUnits(lines, CZ_DOT_Y));
    result.setCz_dot_z(extractDoubleNoUnits(lines, CZ_DOT_Z));
    result.setCz_dot_x_dot(extractDoubleNoUnits(lines, CZ_DOT_X_DOT));
    result.setCz_dot_y_dot(extractDoubleNoUnits(lines, CZ_DOT_Y_DOT));
    result.setCz_dot_z_dot(extractDoubleNoUnits(lines, CZ_DOT_Z_DOT));
    return result;
  }

  /**
   * Parses short form of covariance matrix used in OEM. The matrix is listed as lower triangular, with 1 to 6 numbers
   * per line.
   */
  private static CovarianceMatrix parseShortFormCovariance(List<String> lines) throws OdmParseException {
    CovarianceMatrix result = new CovarianceMatrix();
    while (containsNext(lines, COMMENT)) {
      result.addComment(extractField(lines, COMMENT));
    }
    // Epoch is required in short-form
    result.setEpoch(extractField(lines, EPOCH));
    // Covariance reference frame may be omitted, in which case the reference
    // frame from the metadata section is assumed.
    if (containsNext(lines, COV_REF_FRAME)) {
      String frame = extractField(lines, COV_REF_FRAME);
      result.setCov_ref_frame(parseReferenceFrame(frame));
    }
    // There should be 6 lines with numbers. There should also be the end tag somewhere.
    if (lines.size() < 7) {
      throw new OdmParseException("Unexpected end of data while parsing short-form covariance");
    }
    try {
      String line = lines.remove(0);
      String[] parts = line.split("\\s+");
      if (parts.length < 1) {
        throw new OdmParseException("Not enough numbers in covariance line for X: " + line);
      }
      result.setCx_x(Double.parseDouble(parts[0]));

      line = lines.remove(0);
      parts = line.split("\\s+");
      if (parts.length < 2) {
        throw new OdmParseException("Not enough numbers in covariance line for Y: " + line);
      }
      result.setCy_x(Double.parseDouble(parts[0]));
      result.setCy_y(Double.parseDouble(parts[1]));

      line = lines.remove(0);
      parts = line.split("\\s+");
      if (parts.length < 3) {
        throw new OdmParseException("Not enough numbers in covariance line for Z: " + line);
      }
      result.setCz_x(Double.parseDouble(parts[0]));
      result.setCz_y(Double.parseDouble(parts[1]));
      result.setCz_z(Double.parseDouble(parts[2]));

      line = lines.remove(0);
      parts = line.split("\\s+");
      if (parts.length < 4) {
        throw new OdmParseException("Not enough numbers in covariance line for X_DOT: " + line);
      }
      result.setCx_dot_x(Double.parseDouble(parts[0]));
      result.setCx_dot_y(Double.parseDouble(parts[1]));
      result.setCx_dot_z(Double.parseDouble(parts[2]));
      result.setCx_dot_x_dot(Double.parseDouble(parts[3]));

      line = lines.remove(0);
      parts = line.split("\\s+");
      if (parts.length < 5) {
        throw new OdmParseException("Not enough numbers in covariance line for Y_DOT: " + line);
      }
      result.setCy_dot_x(Double.parseDouble(parts[0]));
      result.setCy_dot_y(Double.parseDouble(parts[1]));
      result.setCy_dot_z(Double.parseDouble(parts[2]));
      result.setCy_dot_x_dot(Double.parseDouble(parts[3]));
      result.setCy_dot_y_dot(Double.parseDouble(parts[4]));

      line = lines.remove(0);
      parts = line.split("\\s+");
      if (parts.length < 6) {
        throw new OdmParseException("Not enough numbers in covariance line for Z_DOT: " + line);
      }
      result.setCz_dot_x(Double.parseDouble(parts[0]));
      result.setCz_dot_y(Double.parseDouble(parts[1]));
      result.setCz_dot_z(Double.parseDouble(parts[2]));
      result.setCz_dot_x_dot(Double.parseDouble(parts[3]));
      result.setCz_dot_y_dot(Double.parseDouble(parts[4]));
      result.setCz_dot_z_dot(Double.parseDouble(parts[5]));
    } catch (NumberFormatException e) {
      throw new OdmParseException("Cannot parse short form covariance matrix: " + e);
    }
    return result;
  }

  /** Parses maneuver data. Removes parsed lines from the list. */
  private static Maneuver parseManeuver(ArrayList<String> lines) throws OdmParseException {
    Maneuver result = new Maneuver();
    while (containsNext(lines, COMMENT)) {
      result.addComment(extractField(lines, COMMENT));
    }
    result.setMan_epoch_ignition(extractField(lines, MAN_EPOCH_IGNITION));
    result.setDuration(extractDoubleNoUnits(lines, MAN_DURATION));
    result.setDelta_mass(extractDoubleNoUnits(lines, MAN_DELTA_MASS));
    String frame = extractField(lines, MAN_REF_FRAME);
    result.setMan_ref_frame(parseReferenceFrame(frame));
    result.setMan_dv_1(extractDoubleNoUnits(lines, MAN_DV_1));
    result.setMan_dv_2(extractDoubleNoUnits(lines, MAN_DV_2));
    result.setMan_dv_3(extractDoubleNoUnits(lines, MAN_DV_3));
    return result;
  }

  /**
   * Translates the given center name into an enum value, or throws if the name is not recognized. The names may contain
   * spaces in them and may be non-unique.
   */
  private static OdmCommonMetadata.CenterName parseCenterName(String name) throws OdmParseException {
    try {
      // Some of names are simple and straightforward. Don't want to enumerate
      // them.
      return OdmCommonMetadata.CenterName.valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      // We will try manually for non-unique names and those with dashes,
      // spaces, and such.
    }
    throw new OdmParseException("Don't know Center Name " + name);
  }

  /**
   * Translates the given reference frame name into an enum value, or throws if the name is not recognized. The names
   * may contain spaces in them and may be non-unique.
   */
  private static OdmCommonMetadata.ReferenceFrame parseReferenceFrame(String name) throws OdmParseException {
    try {
      // Some of names are simple and straightforward. Don't want to enumerate
      // them.
      return OdmCommonMetadata.ReferenceFrame.valueOf(name);
    } catch (IllegalArgumentException e) {
      // We will try manually for non-unique names and those with dashes and
      // such.
    }
    if ("ITRF-97".equals(name)) {
      return OdmCommonMetadata.ReferenceFrame.ITRF97;
    }
    throw new OdmParseException("Don't know " + name);
  }

  /**
   * Translates the given time system name into an enum value, or throws if the name is not recognized.
   */
  private static OdmCommonMetadata.TimeSystem parseTimeSystem(String name) throws OdmParseException {
    // All time system names so far are simple and unique.
    try {
      return OdmCommonMetadata.TimeSystem.valueOf(name);
    } catch (IllegalArgumentException e) {
      throw new OdmParseException("Unknown time system: " + name);
    }
  }

  /** Returns true iff the list contains a string starting with the prefix. */
  private static boolean containsLater(List<String> lines, String prefix) {
    for (String s : lines) {
      if (s.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }

  /** Returns true iff the first line in the list starts with the prefix. */
  private static boolean containsNext(List<String> lines, String prefix) {
    return !lines.isEmpty() && lines.get(0).startsWith(prefix);
  }

  /**
   * Expects the first line in the list to start with the prefix and extracts numeric value from that line. The line may
   * contain more text (units) after the number. Throws if the line is not found or the number doesn't parse. Removes
   * the parsed line from the list.
   */
  private static double extractDoubleNoUnits(List<String> lines, String prefix) throws OdmParseException {
    String line = extractField(lines, prefix);
    int space = line.indexOf(" ");
    if (space > 0) {
      line = line.substring(0, space);
    }
    try {
      return Double.parseDouble(line);
    } catch (NumberFormatException e) {
      throw new OdmParseException("Could not parse double: '" + line + "'");
    }
  }

  /**
   * Expects the first line in the list to start with the given prefix. If found, extracts the field value and removes
   * the line from the list. Otherwise throws.
   */
  private static String extractField(List<String> lines, String prefix) throws OdmParseException {
    if (lines.isEmpty() || !lines.get(0).startsWith(prefix + " ")) {
      throw new OdmParseException("Expected " + prefix);
    }
    String value = lines.remove(0).substring(prefix.length()).trim();
    if (!value.isEmpty() && value.charAt(0) == '=') {
      value = value.substring(1).trim();
    }
    return value;
  }

  /** Splits the string into a list of non-empty lines. */
  private static ArrayList<String> getNonEmptyLines(String buffer) {
    ArrayList<String> list = new ArrayList<>();
    for (String s : buffer.split("\n")) {
      s = s.trim();
      if (!s.isEmpty()) {
        list.add(s);
      }
    }
    return list;
  }
}
