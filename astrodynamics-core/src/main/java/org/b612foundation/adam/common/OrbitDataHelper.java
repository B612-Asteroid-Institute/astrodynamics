package org.b612foundation.adam.common;

import org.b612foundation.adam.opm.CartesianCovariance;
import org.b612foundation.adam.opm.KeplerianCovariance;
import org.b612foundation.adam.opm.KeplerianElements;
import org.b612foundation.adam.opm.StateVector;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.RealMatrix;

import java.util.logging.Logger;

/** Collection of methods for extracting certain data from OPM, ephem, etc. */
public final class OrbitDataHelper {
  /** State is x, y, z, vx, vy, vz, so size is 6. This applies to all vectors and matrices. */
  public static final int STATE_DIMENSION = 6;
  /**
   * Threshold to use when comparing mean/true anomaly to values to assume it is zero. Used for
   * decided which is set*
   */
  public static final double ANOMALY_ANGLE_EPSILON = 1e-20;

  private static Logger log = Logger.getLogger(OrbitDataHelper.class.getName());

  private OrbitDataHelper() {}

  /** Builds a 6x6 JAMA matrix out of the given OPM covariance matrix. */
  public static RealMatrix extractCartesianCovarianceMatrix(CartesianCovariance source) {
    double[][] values = new double[STATE_DIMENSION][STATE_DIMENSION];
    values[0][0] = source.getCx_x();
    values[1][0] = source.getCy_x();
    values[1][1] = source.getCy_y();
    values[2][0] = source.getCz_x();
    values[2][1] = source.getCz_y();
    values[2][2] = source.getCz_z();
    values[3][0] = source.getCx_dot_x();
    values[3][1] = source.getCx_dot_y();
    values[3][2] = source.getCx_dot_z();
    values[3][3] = source.getCx_dot_x_dot();
    values[4][0] = source.getCy_dot_x();
    values[4][1] = source.getCy_dot_y();
    values[4][2] = source.getCy_dot_z();
    values[4][3] = source.getCy_dot_x_dot();
    values[4][4] = source.getCy_dot_y_dot();
    values[5][0] = source.getCz_dot_x();
    values[5][1] = source.getCz_dot_y();
    values[5][2] = source.getCz_dot_z();
    values[5][3] = source.getCz_dot_x_dot();
    values[5][4] = source.getCz_dot_y_dot();
    values[5][5] = source.getCz_dot_z_dot();

    // Fill in the rest of the matrix symmetrically.
    for (int row = 0; row < 5; row++) {
      for (int col = row + 1; col < STATE_DIMENSION; col++) {
        values[row][col] = values[col][row];
      }
    }
    return new Array2DRowRealMatrix(values);
  }

  /**
   * Builds a 6x6 JAMA matrix out of the given OPM Keplerian covariance matrix B612 extension.
   *
   * @param source
   * @param expectMeanAnomaly used to check that the correct orbit position (mean vs true anomaly)
   *     is provided
   * @return
   */
  public static RealMatrix extractKeplerianCovarianceMatrix(
      KeplerianCovariance source, boolean expectMeanAnomaly) {
    double[][] values = new double[STATE_DIMENSION][STATE_DIMENSION];
    values[0][0] = source.getCAA();
    values[1][0] = source.getCEA();
    values[1][1] = source.getCEE();
    values[2][0] = source.getCIA();
    values[2][1] = source.getCIE();
    values[2][2] = source.getCII();
    values[3][0] = source.getCOA();
    values[3][1] = source.getCOE();
    values[3][2] = source.getCOI();
    values[3][3] = source.getCOO();
    values[4][0] = source.getCWA();
    values[4][1] = source.getCWE();
    values[4][2] = source.getCWI();
    values[4][3] = source.getCWO();
    values[4][4] = source.getCWW();

    boolean hasMeanAnomaly = Math.abs(source.getCMM()) > ANOMALY_ANGLE_EPSILON;
    if (!hasMeanAnomaly && expectMeanAnomaly) {
      throw new IllegalArgumentException(
          "Covariance matrix has true anomaly set but was expecting mean anomaly");
    }

    if (hasMeanAnomaly && !expectMeanAnomaly) {
      throw new IllegalArgumentException(
          "Covariance matrix has mean anomaly set but was expecting true anomaly");
    }

    if (hasMeanAnomaly) {
      values[5][0] = source.getCMA();
      values[5][1] = source.getCME();
      values[5][2] = source.getCMI();
      values[5][3] = source.getCMO();
      values[5][4] = source.getCMW();
      values[5][5] = source.getCMM();
    } else {
      values[5][0] = source.getCTA();
      values[5][1] = source.getCTE();
      values[5][2] = source.getCTI();
      values[5][3] = source.getCTO();
      values[5][4] = source.getCTW();
      values[5][5] = source.getCTT();
    }

    // Fill in the rest of the matrix symmetrically.
    for (int row = 0; row < 5; row++) {
      for (int col = row + 1; col < STATE_DIMENSION; col++) {
        values[row][col] = values[col][row];
      }
    }
    return new Array2DRowRealMatrix(values);
  }

  /** Extracts the value of the state vector as a double array. */
  public static double[] extractStateVector(StateVector source) {
    double[] result = new double[STATE_DIMENSION];
    result[0] = source.getX();
    result[1] = source.getY();
    result[2] = source.getZ();
    result[3] = source.getX_dot();
    result[4] = source.getY_dot();
    result[5] = source.getZ_dot();
    return result;
  }

  /**
   * Extracts the value of the state vector as a double array. The order of the elements will be:
   * semimajor axis, eccentricity, inclination, right ascension of ascending node, argument of
   * pericenter, mean/true anomaly (whichever is set)
   */
  public static double[] extractKeplerianElements(KeplerianElements source) {
    double[] result = new double[STATE_DIMENSION];
    result[0] = source.getSemi_major_axis();
    result[1] = source.getEccentricity();
    result[2] = source.getInclination();
    result[3] = source.getRa_of_asc_node();
    result[4] = source.getArg_of_pericenter();

    if (source.hasMeanAnomaly(ANOMALY_ANGLE_EPSILON)) {
      result[5] = source.getMean_anomaly();
    } else {
      result[5] = source.getTrue_anomaly();
    }

    return result;
  }

  /** Formats the n'th column of the eigenvector matrix v as a pretty string. */
  public static String eigenvectorAsString(RealMatrix v, int column) {
    // TODO: no idea if this needs to be StringBuffer, investigate if we can just use StringBuilder.
    StringBuilder buf = new StringBuilder("[");
    for (int row = 0; row < STATE_DIMENSION; row++) {
      buf.append(" ").append(v.getEntry(row, column));
    }
    buf.append(" ]");
    return buf.toString();
  }

  /**
   * Updates the given state vector to be equal to initial + multiplier * v[,column]. All other
   * fields are unchanged.
   */
  public static void setStateVector(
      StateVector state, double[] initial, double multiplier, RealMatrix v, int column) {
    // Initialize from original to keep epoch and comments.
    state.setX(initial[0] + multiplier * v.getEntry(0, column));
    state.setY(initial[1] + multiplier * v.getEntry(1, column));
    state.setZ(initial[2] + multiplier * v.getEntry(2, column));
    state.setX_dot(initial[3] + multiplier * v.getEntry(3, column));
    state.setY_dot(initial[4] + multiplier * v.getEntry(4, column));
    state.setZ_dot(initial[5] + multiplier * v.getEntry(5, column));
  }

  /** Pulls out the last line out of the given text, which has contents of .e file. */
  public static String extractLastEphemerisPoint(String stkEphemeris) {
    // Kinda hacky and wasteful, but should do for now.
    String ephem = stkEphemeris.replaceAll("\r", ""); // Remove "\r" for those developing on windows
    String[] parts = ephem.split("\n");
    int end = parts.length - 1;
    final String THE_END = "END Ephemeris"; // Magic string in .e file.
    while (end >= 0 && !THE_END.equals(parts[end])) {
      end--;
    }
    if (end <= 0) {
      log.severe("No END found in ephemeris data");
      return null;
    }
    end--; // Want the string before the end.
    while (end >= 0 && parts[end].isEmpty()) {
      end--;
    }
    if (end < 0) {
      log.severe("Only empty strings before end of .e");
      return null;
    }
    return parts[end];
  }

  /**
   * Given orbital uncertainty as Cartesian sigma generate the corresponding diagonal covariance
   * matrix
   *
   * @param sigmas
   * @return {@link CartesianCovariance} Matrix
   */
  public static CartesianCovariance cartesianSigmaToCovariance(StateVector sigmas) {
    return new CartesianCovariance()
        .setCx_x(sigmas.getX() * sigmas.getX())
        .setCy_y(sigmas.getY() * sigmas.getY())
        .setCz_z(sigmas.getZ() * sigmas.getZ())
        .setCx_dot_x_dot(sigmas.getX_dot() * sigmas.getX_dot())
        .setCy_dot_y_dot(sigmas.getY_dot() * sigmas.getY_dot())
        .setCz_dot_z_dot(sigmas.getZ_dot() * sigmas.getZ_dot());
  }

  /**
   * Given orbital uncertainty as Keplerian sigma values generate the corresponding diagonal
   * covariance matrix
   *
   * @param sigmas
   * @return {@link KeplerianCovariance} Matrix
   */
  public static KeplerianCovariance keplerianSigmaToCovariance(KeplerianElements sigmas) {
    KeplerianCovariance covariance =
        new KeplerianCovariance()
            .setCAA(sigmas.getSemi_major_axis() * sigmas.getSemi_major_axis())
            .setCEE(sigmas.getEccentricity() * sigmas.getEccentricity())
            .setCII(sigmas.getInclination() * sigmas.getInclination())
            .setCOO(sigmas.getRa_of_asc_node() * sigmas.getRa_of_asc_node())
            .setCWW(sigmas.getArg_of_pericenter() * sigmas.getArg_of_pericenter());

    if (sigmas.hasMeanAnomaly(ANOMALY_ANGLE_EPSILON)) {
      covariance.setCMM(sigmas.getMean_anomaly() * sigmas.getMean_anomaly());
    } else {
      covariance.setCTT(sigmas.getTrue_anomaly() * sigmas.getTrue_anomaly());
    }

    return covariance;
  }
}
