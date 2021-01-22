import agi.foundation.Motion1;
import agi.foundation.coordinates.Cartesian;
import org.b612foundation.adam.opm.KeplerianElements;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.opm.StateVector;
import org.b612foundation.adam.stk.StkOpmHelper;
import org.b612foundation.adam.util.OrekitDataLoader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StkOpmHelperTest {
  static final double DEFAULT_EPSILON = 1e-7;

  @Before
  public void before() {
    OrekitDataLoader.initialize();
  }

  @Test
  public void testGetStateVectorFromKeplerianTrueAnomaly() {
    final double sma = 9325.9;
    final double ecc = 0.255311;
    final double inc = 40.2378;
    final double raan = 23.1986;
    final double w = 143.048;
    final double ta = 335.820576311552;
    final double earthGm = 398600.4415;
    final KeplerianElements keplerianElements =
        new KeplerianElements()
            .setSemi_major_axis(sma)
            .setEccentricity(ecc)
            .setInclination(inc)
            .setRa_of_asc_node(raan)
            .setArg_of_pericenter(w)
            .setTrue_anomaly(ta)
            .setGm(earthGm);
    final OrbitParameterMessage opm = new OrbitParameterMessage().setKeplerian(keplerianElements);

    final double expectedX = -4999969.94997909;
    final double expectedY = 3000030.6355778845;
    final double expectedZ = 4000014.351394289;
    final double expectedVx = -5000.04057443341;
    final double expectedVy = -5999.980959250626;
    final double expectedVz = -2999.97073727087;
    final Motion1<Cartesian> stateVector = StkOpmHelper.getCartesianStateVector(opm);
    validateState(
        stateVector,
        expectedX,
        expectedY,
        expectedZ,
        expectedVx,
        expectedVy,
        expectedVz,
        DEFAULT_EPSILON);
  }

  @Ignore("testGetStateVectorFromKeplerianMeanAnomaly fails when executed in JDK1.8")
  @Test
  public void testGetStateVectorFromKeplerianMeanAnomaly() {
    final double sma = 9325.9;
    final double ecc = 0.255311;
    final double inc = 40.2378;
    final double raan = 23.1986;
    final double w = 143.048;
    final double ma = 345.962717588036;
    final double earthGm = 398600.4415;
    final KeplerianElements keplerianElements =
        new KeplerianElements()
            .setSemi_major_axis(sma)
            .setEccentricity(ecc)
            .setInclination(inc)
            .setRa_of_asc_node(raan)
            .setArg_of_pericenter(w)
            .setMean_anomaly(ma)
            .setGm(earthGm);
    final OrbitParameterMessage opm = new OrbitParameterMessage().setKeplerian(keplerianElements);

    final double expectedX = -4999969.922025165;
    final double expectedY = 3000030.6691222163;
    final double expectedZ = 4000014.3681663442;
    final double expectedVx = -5000.040605948567;
    final double expectedVy = -5999.980940341226;
    final double expectedVz = -2999.9707120585035;
    final Motion1<Cartesian> stateVector = StkOpmHelper.getCartesianStateVector(opm);
    validateState(
        stateVector,
        expectedX,
        expectedY,
        expectedZ,
        expectedVx,
        expectedVy,
        expectedVz,
        DEFAULT_EPSILON);
  }

  @Test
  public void testGetStateVectorFromCartesian() {
    final double x = -5000;
    final double y = 3000;
    final double z = 4000;
    final double vx = -5;
    final double vy = -6;
    final double vz = -3;
    final StateVector opmStateVector =
        new StateVector().setX(x).setY(y).setZ(z).setX_dot(vx).setY_dot(vy).setZ_dot(vz);
    final OrbitParameterMessage opm = new OrbitParameterMessage().setState_vector(opmStateVector);

    final Motion1<Cartesian> stateVector = StkOpmHelper.getCartesianStateVector(opm);
    validateState(
        stateVector,
        x * 1000.0,
        y * 1000.0,
        z * 1000.0,
        vx * 1000.0,
        vy * 1000.0,
        vz * 1000.0,
        DEFAULT_EPSILON);
  }

  private void validateState(
      Motion1<Cartesian> stateVector,
      double expectedX,
      double expectedY,
      double expectedZ,
      double expectedVx,
      double expectedVy,
      double expectedVz,
      double epsilon) {
    final Cartesian position = stateVector.getValue();
    final Cartesian velocity = stateVector.getFirstDerivative();
    assertEquals(expectedX, position.getX(), epsilon);
    assertEquals(expectedY, position.getY(), epsilon);
    assertEquals(expectedZ, position.getZ(), epsilon);
    assertEquals(expectedVx, velocity.getX(), epsilon);
    assertEquals(expectedVy, velocity.getY(), epsilon);
    assertEquals(expectedVz, velocity.getZ(), epsilon);
  }
}
