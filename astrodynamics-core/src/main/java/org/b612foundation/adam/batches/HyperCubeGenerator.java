package org.b612foundation.adam.batches;

import org.b612foundation.adam.common.OrbitDataHelper;
import org.b612foundation.adam.opm.AdamField;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.opm.StateVector;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.EigenDecomposition;
import org.hipparchus.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Contains helpers for generating perturbations of an OPM corresponding to corners or faces of a
 * hypercube defined by the covariance matrix from the OPM. The axes of the hypercube are
 * eigenvectors of the covariance matrix, and the size is determined by the requested number of
 * sigmas.
 *
 * <p>TODO: Move to astrodynamics.
 */
public final class HyperCubeGenerator {
  public static final String SIGMA_FIELD = "INITIAL_PERTURBATION";
  public static final String TYPE_FIELD = "HYPERCUBE";

  private static Logger log = Logger.getLogger(HyperCubeGenerator.class.getName());

  /** Possible values of the type field. */
  enum FieldType {
    FACES,
    CORNERS
  }

  public static boolean isHypercube(OrbitParameterMessage opm) {
    if (opm == null) {
      return false;
    }

    boolean isCartesianOpm = opm.getState_vector() != null && opm.getKeplerian() == null;
    boolean hasCovariance = opm.getCartesianCovariance() != null;

    boolean hasFieldType = false;
    boolean hasSigma = false;
    for (AdamField field : opm.getAdam_fields()) {
      if (TYPE_FIELD.equals(field.getKey())) {
        hasFieldType = true;
      }
      if (SIGMA_FIELD.equals(field.getKey())) {
        hasSigma = true;
      }
      if (hasFieldType && hasSigma) {
        break;
      }
    }

    return isCartesianOpm && hasCovariance && hasFieldType && hasSigma;
  }

  /**
   * Generates the perturbations (not including nominal) for a hypercube described by the given OPM.
   *
   * <p>Expects that the OPM does describe a hypercube (can be checked with isHypercube).
   */
  public List<OrbitParameterMessage> getHypercubePerturbations(OrbitParameterMessage opm)
      throws IllegalArgumentException {

    if (opm.getState_vector() == null) {
      throw new IllegalArgumentException("OPM is supposed to have a cartesian state vector");
    }

    if (opm.getKeplerian() != null) {
      throw new IllegalArgumentException(
          "OPM is supposed to have a cartesian not Keplerian state vector");
    }

    List<AdamField> fields = opm.getAdam_fields();
    FieldType type;
    try {
      type = extractTypeField(fields);
    } catch (IllegalArgumentException e) {
      log.warning("Cannot parse enum value: " + e.getMessage());
      throw new IllegalArgumentException("Cannot parse enum value for " + TYPE_FIELD, e);
    }
    Double sigma;
    try {
      sigma = extractSigmaField(fields);
    } catch (NumberFormatException e) {
      log.warning("Cannot parse double value: " + e.getMessage());
      throw new IllegalArgumentException("Cannot parse double value for " + SIGMA_FIELD, e);
    }
    if (type == null || sigma == null) {
      throw new IllegalArgumentException(
          "HyperCube wants a type and sigma, got " + type + " and " + sigma, null);
    }
    if (opm.getCartesianCovariance() == null) {
      throw new IllegalArgumentException(
          "Requested hypercube run for an OPM with no covariance matrix.", null);
    }
    Matrix covariance =
        JamaOrbitDataHelper.extractCartesianCovarianceMatrix(opm.getCartesianCovariance());
    double[] initialState = JamaOrbitDataHelper.extractStateVector(opm.getState_vector());

    // JAMA has smallest eigenvalue first. This doesn't affect the workings, but
    // it seem strange, since usually the largest one comes first.
    EigenvalueDecomposition eigen = covariance.eig();

    switch (type) {
      case FACES:
        return getFaces(eigen, sigma, opm, initialState);
      case CORNERS:
        return getCorners(eigen, sigma, opm, initialState);
      default:
        throw new UnsupportedOperationException();
    }
  }

  /** Add 6 runs for the 6 faces of the hypercube to the given list of parts. */
  private List<OrbitParameterMessage> getFaces(
      EigenvalueDecomposition eigen, Double sigma, OrbitParameterMessage opm, double[] initialState)
      throws IllegalArgumentException {
    List<OrbitParameterMessage> faces = new ArrayList<>();

    // For all eigenvectors do +- sigma for each eigenvector separately.
    for (int i = 0; i < 6; i++) {
      double eigenvalue = eigen.getRealEigenvalues()[i];
      if (eigenvalue < 0) {
        throw new IllegalArgumentException(
            "Covariance matrix is supposed to be positive semi-definite, but we got a negative "
                + "eigenvalue "
                + eigenvalue,
            null);
      }
      eigenvalue = Math.sqrt(eigenvalue);
      String eigenvector = JamaOrbitDataHelper.eigenvectorAsString(eigen.getV(), i);

      OrbitParameterMessage minusSigma = opm.deepCopy();
      minusSigma.getHeader().addComment("-" + sigma + " * " + eigenvalue + " * " + eigenvector);
      JamaOrbitDataHelper.setStateVector(
          minusSigma.getState_vector(), initialState, -sigma * eigenvalue, eigen.getV(), i);
      faces.add(minusSigma);

      OrbitParameterMessage plusSigma = opm.deepCopy();
      plusSigma.getHeader().addComment("+" + sigma + " * " + eigenvalue + " * " + eigenvector);
      JamaOrbitDataHelper.setStateVector(
          plusSigma.getState_vector(), initialState, sigma * eigenvalue, eigen.getV(), i);
      faces.add(plusSigma);
    }
    return faces;
  }

  /** Add 2^6 = 64 runs for the 64 corners of the hypercube to the given list of parts. */
  private List<OrbitParameterMessage> getCorners(
      EigenvalueDecomposition eigen, Double sigma, OrbitParameterMessage opm, double[] initialState)
      throws IllegalArgumentException {
    List<OrbitParameterMessage> corners = new ArrayList<>();

    // Pull out eigenvalues and strings for vectors once.
    double[] eigenvalue = new double[6];
    Matrix[] eigenvector = new Matrix[6];
    for (int i = 0; i < 6; i++) {
      eigenvalue[i] = eigen.getRealEigenvalues()[i];
      if (eigenvalue[i] < 0) {
        throw new IllegalArgumentException(
            "Covariance matrix is supposed to be positive semi-definite, but we got a negative"
                + " eigenvalue "
                + eigenvalue[i],
            null);
      }
      eigenvalue[i] = Math.sqrt(eigenvalue[i]);
      eigenvector[i] = eigen.getV().getMatrix(0, 5, i, i);
    }

    // Each corner includes all eigenvectors, each of which is multiplied by +sigma or -sigma.
    for (int i = 0; i < 64; i++) {
      // Initialize value for the new vector.
      Matrix value = new Matrix(initialState, 6);

      // Split into booleans for each of the 6 parts of the state.
      boolean[] use = new boolean[6];
      String corner = ""; // Binary string describing the corner.
      for (int v = i, j = 0; j < 6; j++, v = v / 2) {
        use[j] = (v & 1) == 1;
        corner += use[j] ? "+" : "-";
        double multiplier = (use[j] ? 1 : -1) * sigma * eigenvalue[j];
        value = value.plus(eigenvector[j].times(multiplier));
      }

      OrbitParameterMessage cornerOpm = opm.deepCopy();
      cornerOpm.getHeader().addComment("corner " + corner + " with sigma " + sigma);

      // Initialize from original to keep epoch and comments.
      StateVector state = cornerOpm.getState_vector();
      state.setX(value.get(0, 0));
      state.setY(value.get(1, 0));
      state.setZ(value.get(2, 0));
      state.setX_dot(value.get(3, 0));
      state.setY_dot(value.get(4, 0));
      state.setZ_dot(value.get(5, 0));

      corners.add(cornerOpm);
    }
    return corners;
  }

  /** Look for the HYPERCUBE type field. */
  private FieldType extractTypeField(List<AdamField> fields) {
    for (AdamField field : fields) {
      if (TYPE_FIELD.equals(field.getKey())) {
        String value = field.getValue();
        return FieldType.valueOf(value);
      }
    }

    return null;
  }

  /** Look for the sigma field for the initial perturbation. */
  private Double extractSigmaField(List<AdamField> fields) {
    for (AdamField field : fields) {
      if (SIGMA_FIELD.equals(field.getKey())) {
        String value = field.getValue();
        return Double.valueOf(value);
      }
    }

    return null;
  }
}