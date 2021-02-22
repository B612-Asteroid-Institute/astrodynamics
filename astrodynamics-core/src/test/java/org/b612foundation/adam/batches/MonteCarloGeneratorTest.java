package org.b612foundation.adam.batches;

import org.b612foundation.adam.common.OrbitDataHelper;
import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagationType;
import org.b612foundation.adam.opm.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.b612foundation.adam.testing.OpmTestData.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class MonteCarloGeneratorTest {

  @Test
  public void test2018VP1() {
    KeplerianElements state =
        new KeplerianElements()
            .setSemi_major_axis(237474765.37531)
            .setEccentricity(0.429795888482)
            .setInclination(26.007089460445)
            .setRa_of_asc_node(4.735934234028)
            .setArg_of_pericenter(350.637412636631)
            .setTrue_anomaly(250.396741067146)
            .setGm(132712440041.93938);
    KeplerianCovariance cov =
        new KeplerianCovariance()
            .setCAA(7166649140.5406229841675961)
            .setCEA(19.333093822576125)
            .setCEE(5.21538938E-08)
            .setCIA(115.5328385843402775)
            .setCIE(3.11667411E-07)
            .setCII(1.86254288E-06)
            .setCOA(-2.32625565582022302)
            .setCOE(-6.27420946E-09)
            .setCOI(-3.74432730E-08)
            .setCOO(8.81646243E-10)
            .setCWA(-146.6028387505613736)
            .setCWE(-3.95490448E-07)
            .setCWI(-2.36359792E-06)
            .setCWO(4.70864023E-08)
            .setCWW(3.00195616E-06)
            .setCTA(-13741.39795321096626)
            .setCTE(-3.70694474E-05)
            .setCTI(-2.21523664E-04)
            .setCTO(4.46048322E-06)
            .setCTW(2.81096913E-04)
            .setCTT(2.63478810E-02);
    OrbitParameterMessage opm =
        new OrbitParameterMessage().setKeplerian(state).setKeplerianCovariance(cov);
    PropagationParameters params = new PropagationParameters().setSingularMatrixThreshold(1e-15);

    StateCovarianceNormalDistribution dist =
        MonteCarloGenerator.generateCovarianceDistribution(opm, true, params);
  }

  @Test
  public void testMonteCarloGenerator_randomizeKeplerianInput_generatesNDraws() {
    int draws = 10;
    PropagationParameters propParams =
        buildMonteCarloKeplerianTASigmasPropagationParams(KEPLERIAN_SIGMA_TA, draws);
    Collection<OrbitParameterMessage> opms =
        MonteCarloGenerator.generateMonteCarloDraws(propParams);
    assertThat(opms.size()).isEqualTo(draws);
  }

  @Test
  public void testMonteCarloGenerator_randomizeKeplerianTAInput_drawsAreDifferent() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloKeplerianTASigmasPropagationParams(KEPLERIAN_SIGMA_TA, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      if (i == 0) {
        assertFalse(opm1.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON));
      }
      KeplerianElements randomized1 = opm1.getKeplerian();
      // No generated draw should equal the initial OPM.
      assertThat(randomized1).isNotEqualTo(propParams.getOpm());
      for (int j = 0; j < draws; j++) {
        // Don't compare same-index objects
        if (i == j) {
          continue;
        }
        OrbitParameterMessage opm2 = opms.get(j);
        KeplerianElements randomized2 = opm2.getKeplerian();
        // No generated draw should be equal to any of the other draws.
        assertThat(randomized1).isNotEqualTo(randomized2);
      }
    }
  }

  @Test
  public void testMonteCarloGenerator_randomizeKeplerianMAInput_drawsAreDifferent() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloKeplerianMASigmasPropagationParams(KEPLERIAN_SIGMA_MA, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      if (i == 0) {
        assertTrue(opm1.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON));
      }
      KeplerianElements randomized1 = opm1.getKeplerian();
      // No generated draw should equal the initial OPM.
      assertThat(randomized1).isNotEqualTo(propParams.getOpm());
      for (int j = 0; j < draws; j++) {
        // Don't compare same-index objects
        if (i == j) {
          continue;
        }
        OrbitParameterMessage opm2 = opms.get(j);
        KeplerianElements randomized2 = opm2.getKeplerian();
        // No generated draw should be equal to any of the other draws.
        assertThat(randomized1).isNotEqualTo(randomized2);
      }
    }
  }

  @Test
  public void testMonteCarloGenerator_randomizeKeplerianTACovInput_drawsAreDifferent() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloKeplerianTACovariancePropagationParams(KEPLERIAN_COVARIANCE_TA, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      if (i == 0) {
        assertFalse(opm1.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON));
      }
      KeplerianElements randomized1 = opm1.getKeplerian();
      // No generated draw should equal the initial OPM.
      assertThat(randomized1).isNotEqualTo(propParams.getOpm());
      for (int j = 0; j < draws; j++) {
        // Don't compare same-index objects
        if (i == j) {
          continue;
        }
        OrbitParameterMessage opm2 = opms.get(j);
        KeplerianElements randomized2 = opm2.getKeplerian();
        // No generated draw should be equal to any of the other draws.
        assertThat(randomized1).isNotEqualTo(randomized2);
      }
    }
  }

  @Test
  public void testMonteCarloGenerator_randomizeKeplerianMACovInput_drawsAreDifferent() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloKeplerianMACovariancePropagationParams(KEPLERIAN_COVARIANCE_MA, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      if (i == 0) {
        assertTrue(opm1.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON));
      }
      KeplerianElements randomized1 = opm1.getKeplerian();
      // No generated draw should equal the initial OPM.
      assertThat(randomized1).isNotEqualTo(propParams.getOpm());
      for (int j = 0; j < draws; j++) {
        // Don't compare same-index objects
        if (i == j) {
          continue;
        }
        OrbitParameterMessage opm2 = opms.get(j);
        KeplerianElements randomized2 = opm2.getKeplerian();
        // No generated draw should be equal to any of the other draws.
        assertThat(randomized1).isNotEqualTo(randomized2);
      }
    }
  }

  @Test
  public void testMonteCarloGenerator_randomizeCartesSigmaianInput_drawsAreDifferent() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloCartesianSigmasPropagationParams(CARTESIAN_SIGMA, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      StateVector randomized1 = opm1.getState_vector();
      // No generated draw should equal the initial OPM.
      assertThat(randomized1).isNotEqualTo(propParams.getOpm());
      for (int j = 0; j < draws; j++) {
        // Don't compare same-index objects
        if (i == j) {
          continue;
        }
        OrbitParameterMessage opm2 = opms.get(j);
        StateVector randomized2 = opm2.getState_vector();
        // No generated draw should be equal to any of the other draws.
        assertThat(randomized1).isNotEqualTo(randomized2);
      }
    }
  }

  @Test
  public void testMonteCarloGenerator_randomizeCartesCovianInput_drawsAreDifferent() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloCartesianCovPropagationParams(CARTESIAN_COVARIANCE, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      StateVector randomized1 = opm1.getState_vector();
      // No generated draw should equal the initial OPM.
      assertThat(randomized1).isNotEqualTo(propParams.getOpm());
      for (int j = 0; j < draws; j++) {
        // Don't compare same-index objects
        if (i == j) {
          continue;
        }
        OrbitParameterMessage opm2 = opms.get(j);
        StateVector randomized2 = opm2.getState_vector();
        // No generated draw should be equal to any of the other draws.
        assertThat(randomized1).isNotEqualTo(randomized2);
      }
    }
  }

  /** Generated OPMs should have identical data except for the Keplerian elements */
  @Test
  public void testMonteCarloGenerator_generateKeplerianTA_generatedOpmsAreEqualExceptOrbitData() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloKeplerianTASigmasPropagationParams(KEPLERIAN_SIGMA_TA, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    // Check that OPM data (except for Keplerian elements) are equal.
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      if (i == 0) {
        assertFalse(opm1.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON));
      }
      opm1.setKeplerian(null);
      for (int j = 0; j < draws; j++) {
        OrbitParameterMessage opm2 = opms.get(j);
        opm2.setKeplerian(null);
        assertThat(opm1).isEqualTo(opm2);
      }
    }
  }

  /** Generated OPMs should have identical data except for the Keplerian elements */
  @Test
  public void testMonteCarloGenerator_generateKeplerianMA_generatedOpmsAreEqualExceptOrbitData() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloKeplerianMASigmasPropagationParams(KEPLERIAN_SIGMA_MA, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    // Check that OPM data (except for Keplerian elements) are equal.
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      if (i == 0) {
        assertTrue(opm1.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON));
      }
      opm1.setKeplerian(null);
      for (int j = 0; j < draws; j++) {
        OrbitParameterMessage opm2 = opms.get(j);
        opm2.setKeplerian(null);
        assertThat(opm1).isEqualTo(opm2);
      }
    }
  }

  /** Generated OPMs should have identical data except for the Keplerian elements */
  @Test
  public void
      testMonteCarloGenerator_generateKeplerianTACov_generatedOpmsAreEqualExceptOrbitData() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloKeplerianTACovariancePropagationParams(KEPLERIAN_COVARIANCE_TA, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    // Check that OPM data (except for Keplerian elements) are equal.
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      if (i == 0) {
        assertFalse(opm1.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON));
      }
      opm1.setKeplerian(null);
      for (int j = 0; j < draws; j++) {
        OrbitParameterMessage opm2 = opms.get(j);
        opm2.setKeplerian(null);
        assertThat(opm1).isEqualTo(opm2);
      }
    }
  }

  /** Generated OPMs should have identical data except for the Keplerian elements */
  @Test
  public void
      testMonteCarloGenerator_generateKeplerianMACov_generatedOpmsAreEqualExceptOrbitData() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloKeplerianMACovariancePropagationParams(KEPLERIAN_COVARIANCE_MA, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    // Check that OPM data (except for Keplerian elements) are equal.
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      if (i == 0) {
        assertTrue(opm1.getKeplerian().hasMeanAnomaly(OrbitDataHelper.ANOMALY_ANGLE_EPSILON));
      }
      opm1.setKeplerian(null);
      for (int j = 0; j < draws; j++) {
        OrbitParameterMessage opm2 = opms.get(j);
        opm2.setKeplerian(null);
        assertThat(opm1).isEqualTo(opm2);
      }
    }
  }

  /** Generated OPMs should have identical data except for the Cartesian elements */
  @Test
  public void
      testMonteCarloGenerator_generateCartesianSigma_generatedOpmsAreEqualExceptOrbitData() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloCartesianSigmasPropagationParams(CARTESIAN_SIGMA, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    // Check that OPM data (except for Cartesian elements) are equal.
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      opm1.setState_vector(null);
      for (int j = 0; j < draws; j++) {
        OrbitParameterMessage opm2 = opms.get(j);
        opm2.setState_vector(null);
        assertThat(opm1).isEqualTo(opm2);
      }
    }
  }

  @Test
  public void testMonteCarloGenerator_generateCartesianCov_generatedOpmsAreEqualExceptOrbitData() {
    int draws = 1000;
    PropagationParameters propParams =
        buildMonteCarloCartesianCovPropagationParams(CARTESIAN_COVARIANCE, draws);
    List<OrbitParameterMessage> opms =
        new ArrayList<>(MonteCarloGenerator.generateMonteCarloDraws(propParams));
    // Check that OPM data (except for Cartesian elements) are equal.
    for (int i = 0; i < draws; i++) {
      OrbitParameterMessage opm1 = opms.get(i);
      opm1.setState_vector(null);
      for (int j = 0; j < draws; j++) {
        OrbitParameterMessage opm2 = opms.get(j);
        opm2.setState_vector(null);
        assertThat(opm1).isEqualTo(opm2);
      }
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMonteCarlo_paramsNotSetMonteCarloPropagationType_throws() {
    PropagationParameters propParams = new PropagationParameters();

    MonteCarloGenerator.generateMonteCarloDraws(propParams);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMonteCarlo_noDrawsSpecified_throws() {
    PropagationParameters propParams = new PropagationParameters();
    propParams.setPropagationType(PropagationType.MONTE_CARLO);

    MonteCarloGenerator.generateMonteCarloDraws(propParams);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMonteCarloGenerator_testKeplerianWithNoSigmasOrCovariance_throws() {
    PropagationParameters propParams = buildKeplerianMAPropagationParams();
    MonteCarloGenerator.generateMonteCarloDraws(propParams);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMonteCarloGenerator_testCartesianWithNoSigmasOrCovariance_throws() {
    PropagationParameters propParams = buildCartesianPropagationParams();
    MonteCarloGenerator.generateMonteCarloDraws(propParams);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMonteCarloGenerator_testKeplerianTAwithMACovariance_throws() {
    PropagationParameters propParams =
        buildMonteCarloKeplerianTACovariancePropagationParams(KEPLERIAN_COVARIANCE_MA, 10);
    MonteCarloGenerator.generateMonteCarloDraws(propParams);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMonteCarloGenerator_testKeplerianMAwithTACovariance_throws() {
    PropagationParameters propParams =
        buildMonteCarloKeplerianMACovariancePropagationParams(KEPLERIAN_COVARIANCE_TA, 10);
    MonteCarloGenerator.generateMonteCarloDraws(propParams);
  }

  private static PropagationParameters buildMonteCarloKeplerianTASigmasPropagationParams(
      KeplerianElements sigma, int draws) {
    PropagationParameters propParams = buildKeplerianTAPropagationParams();
    propParams.setMonteCarloDraws(draws);
    propParams.setKeplerianSigma(sigma);
    return propParams;
  }

  private static PropagationParameters buildMonteCarloKeplerianMASigmasPropagationParams(
      KeplerianElements sigma, int draws) {
    PropagationParameters propParams = buildKeplerianMAPropagationParams();
    propParams.setMonteCarloDraws(draws);
    propParams.setKeplerianSigma(sigma);
    return propParams;
  }

  private static PropagationParameters buildMonteCarloKeplerianMACovariancePropagationParams(
      KeplerianCovariance covariance, int draws) {
    PropagationParameters propParams = buildKeplerianMAPropagationParams();
    propParams.setMonteCarloDraws(draws);
    propParams.getOpm().setKeplerianCovariance(covariance);
    return propParams;
  }

  private static PropagationParameters buildMonteCarloKeplerianTACovariancePropagationParams(
      KeplerianCovariance covariance, int draws) {
    PropagationParameters propParams = buildKeplerianTAPropagationParams();
    propParams.setMonteCarloDraws(draws);
    propParams.getOpm().setKeplerianCovariance(covariance);
    return propParams;
  }

  private static PropagationParameters buildMonteCarloCartesianSigmasPropagationParams(
      StateVector sigma, int draws) {
    PropagationParameters propParams = buildCartesianPropagationParams();
    propParams.setMonteCarloDraws(draws);
    propParams.setCartesianSigma(sigma);
    return propParams;
  }

  private static PropagationParameters buildMonteCarloCartesianCovPropagationParams(
      CartesianCovariance covariance, int draws) {
    PropagationParameters propParams = buildCartesianPropagationParams();
    propParams.setMonteCarloDraws(draws);
    propParams.getOpm().setCartesianCovariance(covariance);
    return propParams;
  }

  private static PropagationParameters buildCartesianPropagationParams() {
    PropagationParameters propParams = new PropagationParameters();
    propParams.setOpm(buildOpmWithCartesian());
    propParams.setPropagationType(PropagationType.MONTE_CARLO);

    return propParams;
  }

  private static PropagationParameters buildKeplerianTAPropagationParams() {
    PropagationParameters propParams = new PropagationParameters();
    propParams.setOpm(buildOpmWithKeplerianTA());
    propParams.setPropagationType(PropagationType.MONTE_CARLO);

    return propParams;
  }

  private static PropagationParameters buildKeplerianMAPropagationParams() {
    PropagationParameters propParams = new PropagationParameters();
    propParams.setOpm(buildOpmWithKeplerianMA());
    propParams.setPropagationType(PropagationType.MONTE_CARLO);

    return propParams;
  }
}
