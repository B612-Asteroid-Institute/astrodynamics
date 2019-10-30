package org.b612foundation.adam.opm;

import javax.swing.plaf.nimbus.State;

/**
 * Writer class that turns OrbitParameterMessage objects into string representations
 */
public class OpmWriter {

  public static String toCcsdsOpmString(OrbitParameterMessage opm) {
    StringBuilder builder = new StringBuilder();

    OdmCommonHeader header = opm.getHeader();
    builder.append("CCSDS_OPM_VERS = " + opm.getCcsds_opm_vers() + "\n");
    builder.append("CREATION_DATE = " + header.getCreation_date() + "\n");
    builder.append("ORIGINATOR = " + header.getOriginator() + "\n");

    OdmCommonMetadata metadata = opm.getMetadata();
    for(String comment : metadata.getComments()) {
      builder.append("COMMENT " + comment + "\n");
    }
    builder.append("OBJECT_NAME = " + metadata.getObject_name() + "\n");
    builder.append("OBJECT_ID = " + metadata.getObject_id() + "\n");
    builder.append("CENTER_NAME = " + metadata.getCenter_name() + "\n");
    builder.append("REF_FRAME = " + metadata.getRef_frame() + "\n");
    if(metadata.getRef_frame_epoch() != null && !metadata.getRef_frame_epoch().isEmpty()) {
      builder.append("REF_FRAME_EPOCH = " + metadata.getRef_frame_epoch() + "\n");
    }
    builder.append("TIME_SYSTEM = " + metadata.getTime_system() + "\n");

    StateVector stateVector = opm.getState_vector();
    for(String comment : stateVector.getComments()) {
      builder.append("COMMENT " + comment + "\n");
    }
    builder.append("EPOCH = " + stateVector.getEpoch() + "\n");
    builder.append("X = " + stateVector.getX() + "\n");
    builder.append("Y = " + stateVector.getY() + "\n");
    builder.append("Z = " + stateVector.getZ() + "\n");
    builder.append("X_DOT = " + stateVector.getX_dot() + "\n");
    builder.append("Y_DOT = " + stateVector.getY_dot() + "\n");
    builder.append("Z_DOT = " + stateVector.getZ_dot() + "\n");

    if(opm.getKeplerian() != null) {
      KeplerianElements keplerian = opm.getKeplerian();
      for(String comment : keplerian.getComments()) {
        builder.append("COMMENT " + comment + "\n");
      }
      builder.append("SEMI_MAJOR_AXIS = " + keplerian.getSemi_major_axis() + "\n");
      builder.append("ECCENTRICITY = " + keplerian.getEccentricity() + "\n");
      builder.append("INCLINATION = " + keplerian.getInclination() + "\n");
      builder.append("RA_OF_ASC_NODE = " + keplerian.getRa_of_asc_node() + "\n");
      builder.append("ARG_OF_PERICENTER = " + keplerian.getArg_of_pericenter() + "\n");
      builder.append("TRUE_ANOMALY = " + keplerian.getTrue_anomaly() + "\n");
      builder.append("GM = " + keplerian.getGm() + "\n");
    }

    if(opm.getSpacecraft() != null) {
      SpacecraftParameters spacecraft = opm.getSpacecraft();
      for(String comment : spacecraft.getComments()) {
        builder.append("COMMENT " + comment + "\n");
      }
      builder.append("MASS = " + spacecraft.getMass() + "\n");
      builder.append("SOLAR_RAD_AREA = " + spacecraft.getSolar_rad_area() + "\n");
      builder.append("SOLAR_RAD_COEFF = " + spacecraft.getSolar_rad_coeff() + "\n");
      builder.append("DRAG_AREA = " + spacecraft.getDrag_area() + "\n");
      builder.append("DRAG_COEFF = " + spacecraft.getDrag_coeff() + "\n");
    }

    for(Manuever m : opm.getManuevers()) {
      for(String comment : m.getComments()) {
        builder.append("COMMENT " + comment + "\n");
      }
      builder.append("MAN_EPOCH_IGNITION = " + m.getMan_epoch_ignition() + "\n");
      builder.append("MAN_DURATION = " + m.getDuration() + "\n");
      builder.append("MAN_DELTA_MASS = " + m.getDelta_mass() + "\n");
      builder.append("MAN_REF_FRAME = " + m.getMan_ref_frame() + "\n");
      builder.append("MAN_DV_1 = " + m.getMan_dv_1() + "\n");
      builder.append("MAN_DV_2 = " + m.getMan_dv_2() + "\n");
      builder.append("MAN_DV_3 = " + m.getMan_dv_3() + "\n");
    }

    if(opm.getCovariance() != null) {
      CovarianceMatrix cov = opm.getCovariance();
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
}
