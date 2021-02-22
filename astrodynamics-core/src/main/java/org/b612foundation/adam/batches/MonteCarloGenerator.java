package org.b612foundation.adam.batches;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import org.b612foundation.adam.common.OrbitDataHelper;
import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagationType;
import org.b612foundation.adam.opm.KeplerianCovariance;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.opm.StateVector;
import org.hipparchus.exception.MathIllegalArgumentException;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static org.b612foundation.adam.astro.AstroConstants.AU_TO_KM;

/** Utility class to create Monte Carlo draws for propagations. */
public final class MonteCarloGenerator {
  private static final Logger log = Logger.getLogger(MonteCarloGenerator.class.getName());
  private static final double SINGULAR_MATRIX_THRESHOLD = 1e-15;

  public static boolean isMonteCarlo(PropagationParameters params) {
    return params.getPropagationType() == PropagationType.MONTE_CARLO;
  }

  public static Collection<OrbitParameterMessage> generateMonteCarloDraws(
      PropagationParameters params) {
    checkArgument(isMonteCarlo(params), "Propagation type should be MONTE_CARLO");
    long draws = params.getMonteCarloDraws();
    checkArgument(draws > 0, "Monte Carlo draws must be greater than 0");

    OrbitParameterMessage initialOpm = params.getOpm().deepCopy();
    boolean hasStates = initialOpm.getKeplerian() != null || initialOpm.getState_vector() != null;
    checkArgument(hasStates, "Need to provide a Cartesian or Keplerian state to process");

    boolean useKeplerian = initialOpm.getKeplerian() != null;
    if (useKeplerian) {
      boolean hasSigmaOrCovariance =
          params.getKeplerianSigma() != null || initialOpm.getKeplerianCovariance() != null;
      checkArgument(
          hasSigmaOrCovariance, "Keplerian state provided but no Keplerian sigma or covariance");
      if (params.getKeplerianSigma() != null) {
        KeplerianCovariance covariance =
            OrbitDataHelper.keplerianSigmaToCovariance(params.getKeplerianSigma());
        if (Math.abs(covariance.getCTT()) > OrbitDataHelper.ANOMALY_ANGLE_EPSILON
            && initialOpm.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON)) {
          throw new IllegalArgumentException(
              "Mean vs. True anomaly types don't match between covariance and state");
        }
        initialOpm.setKeplerianCovariance(covariance);
      }
    } else {
      boolean hasSigmaOrCovariance =
          params.getCartesianSigma() != null || initialOpm.getCartesianCovariance() != null;
      checkArgument(hasSigmaOrCovariance, "Cartesian state does not have a sigma or covariance");
      if (params.getCartesianSigma() != null) {
        initialOpm.setCartesianCovariance(
            OrbitDataHelper.cartesianSigmaToCovariance(params.getCartesianSigma()));
      }
    }

    return generateDrawFromCovariance(initialOpm, draws, useKeplerian, params);
  }

  /**
   * Generate a list of randomized {@link OrbitParameterMessage} from initial {@link
   * OrbitParameterMessage} containing {@link StateVector} a {@link
   * org.b612foundation.adam.opm.CartesianCovariance}
   */
  private static List<OrbitParameterMessage> generateDrawFromCovariance(
      OrbitParameterMessage initialOpm,
      long draws,
      boolean useKeplerian,
      PropagationParameters params) {
    StateCovarianceNormalDistribution distribution =
        generateCovarianceDistribution(initialOpm, useKeplerian, params);
    ImmutableList.Builder<OrbitParameterMessage> randomizedOpms = ImmutableList.builder();

    for (int i = 0; i < draws; i++) {
      randomizedOpms.add(
          generateDrawFromCovariance(initialOpm, distribution, useKeplerian, params));
    }
    return randomizedOpms.build();
  }

  private static OrbitParameterMessage generateDrawFromCovariance(
      OrbitParameterMessage initialOpm,
      StateCovarianceNormalDistribution distribution,
      boolean useKeplerian,
      PropagationParameters params) {

    final String comment = "Monte Carlo randomized";
    double[] newState = distribution.sample();
    OrbitParameterMessage newOpm = initialOpm.deepCopy();

    if (useKeplerian) {
      newOpm.getKeplerian().getComments().add(comment);

      newOpm
          .getKeplerian()
          .setSemi_major_axis(newState[0] * AU_TO_KM)
          .setEccentricity(newState[1])
          .setInclination(newState[2])
          .setRa_of_asc_node(newState[3])
          .setArg_of_pericenter(newState[4]);

      if (initialOpm.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON)) {
        newOpm.getKeplerian().setMean_anomaly(newState[5]);
      } else {
        newOpm.getKeplerian().setTrue_anomaly(newState[5]);
      }
    } else {
      newOpm.getState_vector().getComments().add(comment);

      newOpm
          .getState_vector()
          .setX(newState[0])
          .setY(newState[1])
          .setZ(newState[2])
          .setX_dot(newState[3])
          .setY_dot(newState[4])
          .setZ_dot(newState[5]);
    }

    return newOpm;
  }

  /**
   * Units scale are such that we have to normalize semimajor axis from KM to AU in order to make
   * the covariance matrix invertible, which is needed for the sampling method. If not at best the
   * results will be unstable.
   */
  @VisibleForTesting
  static StateCovarianceNormalDistribution generateCovarianceDistribution(
      OrbitParameterMessage opm, boolean useKeplerian, PropagationParameters params) {
    double[] state;
    double[][] covariance;

    if (useKeplerian) {
      state = OrbitDataHelper.extractKeplerianElements(opm.getKeplerian());
      state[0] /= AU_TO_KM;
      covariance =
          OrbitDataHelper.extractKeplerianCovarianceMatrix(
                  opm.getKeplerianCovariance(),
                  opm.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON))
              .getData();
      covariance[0][0] /= (AU_TO_KM * AU_TO_KM);
      covariance[1][0] /= AU_TO_KM;
      covariance[2][0] /= AU_TO_KM;
      covariance[3][0] /= AU_TO_KM;
      covariance[4][0] /= AU_TO_KM;
      covariance[5][0] /= AU_TO_KM;
      covariance[0][1] /= AU_TO_KM;
      covariance[0][2] /= AU_TO_KM;
      covariance[0][3] /= AU_TO_KM;
      covariance[0][4] /= AU_TO_KM;
      covariance[0][5] /= AU_TO_KM;
    } else {
      state = OrbitDataHelper.extractStateVector(opm.getState_vector());
      covariance =
          OrbitDataHelper.extractCartesianCovarianceMatrix(opm.getCartesianCovariance()).getData();
    }

    double matrixTolerance = SINGULAR_MATRIX_THRESHOLD;

    if (params.getSingularMatrixThreshold() > 0.0) {
      matrixTolerance = params.getSingularMatrixThreshold();
    }

    try {
      return new StateCovarianceNormalDistribution(state, covariance, matrixTolerance);
    } catch (MathIllegalArgumentException e) {
      // TODO Replace this with some sort of Run UUID so that not echoing orbit data in the logs
      log.severe("Error generating sample from opm: " + opm.toString());
      StringBuilder sb = new StringBuilder();
      sb.append("Covariance Matrix: ");
      sb.append(System.lineSeparator());
      for (int i = 0; i < covariance.length; i++) {
        for (int j = 0; j < covariance[i].length; j++) {
          sb.append(covariance[i][j]);
          if (j != covariance[i].length - 1) {
            sb.append(", ");
          }
        }
        sb.append(System.lineSeparator());
      }
      log.severe(sb.toString());
      throw e;
    }
  }
}
