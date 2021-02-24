package org.b612foundation.adam.common;

import org.b612foundation.adam.opm.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropagationHelperTest {
  private OrbitEphemerisMessage testOem = OdmScenarioBuilder.buildOemWithCovariance();
  private String testEpoch = "1996-12-30T01:28:02.267";
  private double testX = 2164.375;
  private double testY = 1115.811;
  private double testZ = -688.131;
  private double testVx = -3.53328;
  private double testVy = -2.88452;
  private double testVz = 0.88535;

  @Test
  public void testExtractFinalState() {
    OemDataLine expected = new OemDataLine(testEpoch, testX, testY, testZ, testVx, testVy, testVz);
    assertEquals(expected, PropagationHelper.extractFinalState(testOem));
  }

  @Test
  public void testToStateVectorFromOemLine() {
    OemDataLine line = new OemDataLine(testEpoch, testX, testY, testZ, testVx, testVy, testVz);
    StateVector expected = new StateVector()
        .setEpoch(testEpoch)
        .setX(testX)
        .setY(testY)
        .setZ(testZ)
        .setX_dot(testVx)
        .setY_dot(testVy)
        .setZ_dot(testVz);

    assertEquals(expected, PropagationHelper.toStateVector(line));
  }

  @Test
  public void testToStateVectorFromArrayAndDateString() {
    String epoch = "1996-12-30T01:28:02.267";
    double[] state = {2164.375, 1115.811, -688.131, -3.53328, -2.88452, 0.88535};
    StateVector expected = new StateVector()
        .setEpoch(testEpoch)
        .setX(testX)
        .setY(testY)
        .setZ(testZ)
        .setX_dot(testVx)
        .setY_dot(testVy)
        .setZ_dot(testVz);
    assertEquals(expected, PropagationHelper.toStateVector(state, epoch));
  }
}
