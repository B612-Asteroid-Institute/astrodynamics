package org.b612foundation.adam.batches;

import org.b612foundation.adam.opm.OdmScenarioBuilder;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.opm.StateVector;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class HyperCubeGeneratorTest {
  private OrbitParameterMessage nullOpm = null;
  private OrbitParameterMessage emptyOpm = new OrbitParameterMessage();
  private OrbitParameterMessage fullFacesOpm = OdmScenarioBuilder.buildOpmWithCovariance("FACES");
  private OrbitParameterMessage fullCornerOpm =
      OdmScenarioBuilder.buildOpmWithCovariance("CORNERS");
  private OrbitParameterMessage kepOpm = OdmScenarioBuilder.buildOpmWithKeplerianAndManeuvers();
  private OrbitParameterMessage noHyperSettings = OdmScenarioBuilder.buildOpmWithCovariance(null);
  private OrbitParameterMessage noCovOpm =
      OdmScenarioBuilder.buildSimpleOpm()
          .addAdam_field("HYPERCUBE", "FACES")
          .addAdam_field("INITIAL_PERTURBATION", "3");
  private OrbitParameterMessage badTypeOpm =
      OdmScenarioBuilder.buildSimpleOpm()
          .addAdam_field("HYPERCUBE", "SMILES")
          .addAdam_field("INITIAL_PERTURBATION", "3");
  private OrbitParameterMessage badPertOpm =
      OdmScenarioBuilder.buildSimpleOpm()
          .addAdam_field("HYPERCUBE", "FACES")
          .addAdam_field("INITIAL_PERTURBATION", "THREE");
  private OrbitParameterMessage missingTypeOpm =
      OdmScenarioBuilder.buildOpmWithCovariance(null).addAdam_field("INITIAL_PERTURBATION", "3");
  private OrbitParameterMessage missingPertOpm =
      OdmScenarioBuilder.buildOpmWithCovariance(null).addAdam_field("HYPERCUBE", "FACES");

  @Test
  public void testGenFacesHyperCube() {
    final int expectedStateCount = 12;
    final StateVector initialState = fullFacesOpm.getState_vector();
    final List<OrbitParameterMessage> hypercube =
        new HyperCubeGenerator().getHypercubePerturbations(fullFacesOpm);
    assertEquals(expectedStateCount, hypercube.size());
    for (int i = 0; i < expectedStateCount; i++) {
      assertNotEquals(
          "State " + i + " equals initial state", initialState, hypercube.get(i).getState_vector());
    }
  }

  @Test
  public void testGenCornersHyperCube() {
    final int expectedStateCount = 64;
    final StateVector initialState = fullCornerOpm.getState_vector();
    final List<OrbitParameterMessage> hypercube =
        new HyperCubeGenerator().getHypercubePerturbations(fullCornerOpm);
    assertEquals(expectedStateCount, hypercube.size());
    for (int i = 0; i < expectedStateCount; i++) {
      assertNotEquals(
          "State " + i + " equals initial state", initialState, hypercube.get(i).getState_vector());
    }
  }

  @Test
  public void testGenNoTypeSpecified() {
    testBadGeneratorArgs(missingTypeOpm, "HyperCube wants a type and sigma, got null and 3.0");
  }

  @Test
  public void testGenUnknownTypeSpecified() {
    testBadGeneratorArgs(badTypeOpm, "Cannot parse enum value for HYPERCUBE");
  }

  @Test
  public void testGenNoSigmaSpecified() {
    testBadGeneratorArgs(missingPertOpm, "HyperCube wants a type and sigma, got FACES and null");
  }

  @Test
  public void testGenBadSigmaSpecified() {
    testBadGeneratorArgs(badPertOpm, "Cannot parse double value for INITIAL_PERTURBATION");
  }

  @Test
  public void testGenEmptyOpm() {
    testBadGeneratorArgs(emptyOpm, "OPM is supposed to have a cartesian state vector");
  }

  @Test
  public void testGenNoCovariance() {
    testBadGeneratorArgs(noCovOpm, "Requested hypercube run for an OPM with no covariance matrix.");
  }

  @Test
  public void testKeplerianOpm() {
    testBadGeneratorArgs(kepOpm, "OPM is supposed to have a cartesian not Keplerian state vector");
  }

  @Test
  public void testNoHyperSettings() {
    testBadGeneratorArgs(noHyperSettings, "HyperCube wants a type and sigma, got null and null");
  }

  @Test
  public void testIsHypercubeWithNullOpm() {
    assertFalse(HyperCubeGenerator.isHypercube(nullOpm));
  }

  @Test
  public void testIsHyperCubeWithEmptyOpm() {
    assertFalse(HyperCubeGenerator.isHypercube(emptyOpm));
  }

  @Test
  public void testIsHypercubeFullyPopulated() {
    assertTrue(HyperCubeGenerator.isHypercube(fullCornerOpm));
    assertTrue(HyperCubeGenerator.isHypercube(fullFacesOpm));
  }

  @Test
  public void testIsHyperCubeMissingCartesianCovariance() {
    assertFalse(HyperCubeGenerator.isHypercube(noCovOpm));
  }

  @Test
  public void testIsHyperCubeMissingType() {
    assertFalse(HyperCubeGenerator.isHypercube(missingTypeOpm));
  }

  @Test
  public void testIsHyperCubeMissingInitialPerturbation() {
    assertFalse(HyperCubeGenerator.isHypercube(missingPertOpm));
  }

  private void testBadGeneratorArgs(OrbitParameterMessage opm, String expectedMessage) {
    try {
      new HyperCubeGenerator().getHypercubePerturbations(opm);
      fail("Exception expected");
    } catch (IllegalArgumentException e) {
      assertEquals(expectedMessage, e.getMessage());
    }
  }
}
