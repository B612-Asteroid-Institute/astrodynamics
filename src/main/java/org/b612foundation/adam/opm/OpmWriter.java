package org.b612foundation.adam.opm;

import java.util.List;
import lombok.var;

/** Writer class that turns OrbitParameterMessage objects into string representations */
public class OpmWriter {

  public static String toCcsdsOpmString(OrbitParameterMessage opm) {
    StringBuilder builder = new StringBuilder();

    OdmCommonHeader header = opm.getHeader();
    builder.append("CCSDS_OPM_VERS = " + opm.getCcsds_opm_vers() + "\n");
    builder.append("CREATION_DATE = " + header.getCreation_date() + "\n");
    builder.append("ORIGINATOR = " + header.getOriginator() + "\n");

    if (opm.getMetadata() == null) {
      throw new IllegalArgumentException("OPM metadata must be filled in to correctly serialize");
    }

    OdmCommonMetadata metadata = opm.getMetadata();
    outputComments(builder, metadata.getComments());
    builder.append("OBJECT_NAME = " + metadata.getObject_name() + "\n");
    builder.append("OBJECT_ID = " + metadata.getObject_id() + "\n");
    builder.append("CENTER_NAME = " + metadata.getCenter_name() + "\n");
    builder.append("REF_FRAME = " + metadata.getRef_frame() + "\n");
    if (metadata.getRef_frame_epoch() != null && !metadata.getRef_frame_epoch().isEmpty()) {
      builder.append("REF_FRAME_EPOCH = " + metadata.getRef_frame_epoch() + "\n");
    }
    builder.append("TIME_SYSTEM = " + metadata.getTime_system() + "\n");

    if (opm.getState_vector() == null && opm.getKeplerian() == null) {
      throw new IllegalArgumentException(
          "OPM must have state vector data (cartesian or keplerian) to correctly serialize");
    }

    if (opm.getState_vector() != null) {
      StateVector stateVector = opm.getState_vector();
      outputComments(builder, stateVector.getComments());
      builder.append("EPOCH = " + stateVector.getEpoch() + "\n");
      builder.append("X = " + stateVector.getX() + "\n");
      builder.append("Y = " + stateVector.getY() + "\n");
      builder.append("Z = " + stateVector.getZ() + "\n");
      builder.append("X_DOT = " + stateVector.getX_dot() + "\n");
      builder.append("Y_DOT = " + stateVector.getY_dot() + "\n");
      builder.append("Z_DOT = " + stateVector.getZ_dot() + "\n");
    }

    if (opm.getKeplerian() != null) {
      KeplerianElements keplerian = opm.getKeplerian();
      outputComments(builder, keplerian.getComments());
      builder.append("SEMI_MAJOR_AXIS = " + keplerian.getSemi_major_axis() + "\n");
      builder.append("ECCENTRICITY = " + keplerian.getEccentricity() + "\n");
      builder.append("INCLINATION = " + keplerian.getInclination() + "\n");
      builder.append("RA_OF_ASC_NODE = " + keplerian.getRa_of_asc_node() + "\n");
      builder.append("ARG_OF_PERICENTER = " + keplerian.getArg_of_pericenter() + "\n");
      builder.append("TRUE_ANOMALY = " + keplerian.getTrue_anomaly() + "\n");
      builder.append("GM = " + keplerian.getGm() + "\n");
    }

    if (opm.getSpacecraft() != null) {
      SpacecraftParameters spacecraft = opm.getSpacecraft();
      outputComments(builder, spacecraft.getComments());
      builder.append("MASS = " + spacecraft.getMass() + "\n");
      builder.append("SOLAR_RAD_AREA = " + spacecraft.getSolar_rad_area() + "\n");
      builder.append("SOLAR_RAD_COEFF = " + spacecraft.getSolar_rad_coeff() + "\n");
      builder.append("DRAG_AREA = " + spacecraft.getDrag_area() + "\n");
      builder.append("DRAG_COEFF = " + spacecraft.getDrag_coeff() + "\n");
    }

    if (opm.getManeuvers() != null) {
      for (var m : opm.getManeuvers()) {
        outputComments(builder, m.getComments());
        builder.append("MAN_EPOCH_IGNITION = " + m.getMan_epoch_ignition() + "\n");
        builder.append("MAN_DURATION = " + m.getDuration() + "\n");
        builder.append("MAN_DELTA_MASS = " + m.getDelta_mass() + "\n");
        builder.append("MAN_REF_FRAME = " + m.getMan_ref_frame() + "\n");
        builder.append("MAN_DV_1 = " + m.getMan_dv_1() + "\n");
        builder.append("MAN_DV_2 = " + m.getMan_dv_2() + "\n");
        builder.append("MAN_DV_3 = " + m.getMan_dv_3() + "\n");
      }
    }

    if (opm.getCartesianCovariance() != null) {
      CartesianCovariance cov = opm.getCartesianCovariance();
      builder.append("CX_X = " + cov.getCx_x() + "\n");
      builder.append("CY_X = " + cov.getCy_x() + "\n");
      builder.append("CY_Y = " + cov.getCy_y() + "\n");
      builder.append("CZ_X = " + cov.getCz_x() + "\n");
      builder.append("CZ_Y = " + cov.getCz_y() + "\n");
      builder.append("CZ_Z = " + cov.getCz_z() + "\n");
      builder.append("CX_DOT_X = " + cov.getCx_dot_x() + "\n");
      builder.append("CX_DOT_Y = " + cov.getCx_dot_y() + "\n");
      builder.append("CX_DOT_Z = " + cov.getCx_dot_z() + "\n");
      builder.append("CX_DOT_X_DOT = " + cov.getCx_dot_x_dot() + "\n");
      builder.append("CY_DOT_X = " + cov.getCy_dot_x() + "\n");
      builder.append("CY_DOT_Y = " + cov.getCy_dot_y() + "\n");
      builder.append("CY_DOT_Z = " + cov.getCy_dot_z() + "\n");
      builder.append("CY_DOT_X_DOT = " + cov.getCy_dot_x_dot() + "\n");
      builder.append("CY_DOT_Y_DOT = " + cov.getCy_dot_y_dot() + "\n");
      builder.append("CZ_DOT_X = " + cov.getCz_dot_x() + "\n");
      builder.append("CZ_DOT_Y = " + cov.getCz_dot_y() + "\n");
      builder.append("CZ_DOT_Z = " + cov.getCz_dot_z() + "\n");
      builder.append("CZ_DOT_X_DOT = " + cov.getCz_dot_x_dot() + "\n");
      builder.append("CZ_DOT_Y_DOT = " + cov.getCz_dot_y_dot() + "\n");
      builder.append("CZ_DOT_Z_DOT = " + cov.getCz_dot_z_dot() + "\n");
    }

    return builder.toString();
  }

  private static void outputComments(StringBuilder builder, List<String> comments) {
    if (comments == null) {
      return;
    }

    for (String comment : comments) {
      builder.append("COMMENT " + comment + "\n");
    }
  }
}
