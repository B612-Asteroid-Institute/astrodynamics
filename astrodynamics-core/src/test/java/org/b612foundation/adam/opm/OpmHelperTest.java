package org.b612foundation.adam.opm;

import org.b612foundation.adam.util.OrekitDataLoader;
import org.junit.Before;
import org.junit.Test;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.b612foundation.adam.astro.AstroConstants.KM_TO_M;
import static org.junit.Assert.assertEquals;

public class OpmHelperTest {

  @Before
  public void before() {
    OrekitDataLoader.initialize();
  }

  @Test
  public void testFromKeplerianTrueAnomalyOpm() {
    final double posTolerance = 1e-12;
    final double velTolerance = 1e-12;
    final double sma = 10000 * KM_TO_M;
    final double ecc = 0.003;
    final double inc = Math.toRadians(40);
    final double raan = Math.toRadians(50);
    final double pa = Math.toRadians(60);
    final double ta = Math.toRadians(70);
    final double mu = Constants.WGS84_EARTH_MU;
    final AbsoluteDate epoch = new AbsoluteDate(2020, 1, 2, 3, 4, 5.0, TimeScalesFactory.getTT());
    KeplerianOrbit keplerianOrbit =
        new KeplerianOrbit(
            sma, ecc, inc, pa, raan, ta, PositionAngle.TRUE, FramesFactory.getICRF(), epoch, mu);
    StateVector expected = cartesianStateFromKeplerian(keplerianOrbit);
    OrbitParameterMessage opm = opmFromKeplerian(keplerianOrbit, PositionAngle.TRUE);
    StateVector actual = OpmHelper.getCartesianStateVector(opm);
    compareState(expected, actual, posTolerance, velTolerance);
  }

  @Test
  public void testFromKeplerianMeanAnomalyOpm() {
    final double posTolerance = 1e-12;
    final double velTolerance = 1e-12;
    final double sma = 10000 * KM_TO_M;
    final double ecc = 0.003;
    final double inc = Math.toRadians(40);
    final double raan = Math.toRadians(50);
    final double pa = Math.toRadians(60);
    final double ma = Math.toRadians(70);
    final double mu = Constants.WGS84_EARTH_MU;
    final AbsoluteDate epoch = new AbsoluteDate(2020, 1, 2, 3, 4, 5.0, TimeScalesFactory.getTT());
    KeplerianOrbit keplerianOrbit =
        new KeplerianOrbit(
            sma, ecc, inc, pa, raan, ma, PositionAngle.MEAN, FramesFactory.getICRF(), epoch, mu);
    StateVector expected = cartesianStateFromKeplerian(keplerianOrbit);
    OrbitParameterMessage opm = opmFromKeplerian(keplerianOrbit, PositionAngle.MEAN);
    StateVector actual = OpmHelper.getCartesianStateVector(opm);
    compareState(expected, actual, posTolerance, velTolerance);
  }

  @Test
  public void testFromCartesianOpm() {
    final double posTolerance = 1e-12;
    final double velTolerance = 1e-12;
    String epochString = "2000-01-02T23:59:37.816Z";
    ZonedDateTime startDate = ZonedDateTime.parse(epochString);

    StateVector state =
        new StateVector()
            .setEpoch(startDate.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
            .setX(10000)
            .setY(20000)
            .setZ(30000)
            .setX_dot(100)
            .setY_dot(200)
            .setZ_dot(300);
    OrbitParameterMessage opm = new OrbitParameterMessage().setState_vector(state);

    StateVector extractState = OpmHelper.getCartesianStateVector(opm);
    compareState(state, extractState, posTolerance, velTolerance);
  }

  private void compareState(
      StateVector expected, StateVector actual, double posTolerance, double velTolerance) {
    assertEquals(expected.getEpoch(), actual.getEpoch());
    assertEquals(expected.getX(), actual.getX(), posTolerance);
    assertEquals(expected.getY(), actual.getY(), posTolerance);
    assertEquals(expected.getZ(), actual.getZ(), posTolerance);
    assertEquals(expected.getX_dot(), actual.getX_dot(), velTolerance);
    assertEquals(expected.getY_dot(), actual.getY_dot(), velTolerance);
    assertEquals(expected.getZ_dot(), actual.getZ_dot(), velTolerance);
  }

  private StateVector cartesianStateFromKeplerian(KeplerianOrbit orbit) {
    return new StateVector()
        .setEpoch(orbit.getDate().toString(0))
        .setX(orbit.getPVCoordinates().getPosition().getX() / KM_TO_M)
        .setY(orbit.getPVCoordinates().getPosition().getY() / KM_TO_M)
        .setZ(orbit.getPVCoordinates().getPosition().getZ() / KM_TO_M)
        .setX_dot(orbit.getPVCoordinates().getVelocity().getX() / KM_TO_M)
        .setY_dot(orbit.getPVCoordinates().getVelocity().getY() / KM_TO_M)
        .setZ_dot(orbit.getPVCoordinates().getVelocity().getZ() / KM_TO_M);
  }

  private OrbitParameterMessage opmFromKeplerian(KeplerianOrbit orbit, PositionAngle angleType) {
    KeplerianElements elements =
        new KeplerianElements()
            .setGm(orbit.getMu() / KM_TO_M / KM_TO_M / KM_TO_M)
            .setArg_of_pericenter(Math.toDegrees(orbit.getPerigeeArgument()))
            .setEccentricity(orbit.getE())
            .setInclination(Math.toDegrees(orbit.getI()))
            .setRa_of_asc_node(Math.toDegrees(orbit.getRightAscensionOfAscendingNode()))
            .setSemi_major_axis(orbit.getA() / KM_TO_M);

    if (angleType == PositionAngle.TRUE) {
      elements = elements.setTrue_anomaly(Math.toDegrees(orbit.getTrueAnomaly()));
    } else {
      elements = elements.setMean_anomaly(Math.toDegrees(orbit.getMeanAnomaly()));
    }

    return new OrbitParameterMessage()
        .setState_vector(new StateVector().setEpoch(orbit.getDate().toString(0)))
        .setKeplerian(elements);
  }
}
