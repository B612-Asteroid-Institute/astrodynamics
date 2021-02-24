package org.b612foundation.adam.stk.propagators;

import static org.junit.Assert.*;

import agi.foundation.DateMotionCollection1;
import agi.foundation.Motion1;
import agi.foundation.celestial.CentralBodiesFacet;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.coordinates.KinematicTransformation;
import agi.foundation.geometry.GeometryTransformer;
import agi.foundation.stk.StkEphemerisFile;
import agi.foundation.stk.StkEphemerisFile.Ephemeris;
import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;
import java.io.IOException;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.List;
import org.b612foundation.adam.analysis.DatedValue;
import org.b612foundation.adam.datamodel.PropagationConfigurationFactory;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.opm.OdmCommonMetadata;
import org.b612foundation.adam.opm.OdmCommonMetadata.CenterName;
import org.b612foundation.adam.opm.OdmCommonMetadata.ReferenceFrame;
import org.b612foundation.adam.opm.OdmCommonMetadata.TimeSystem;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.opm.StateVector;
import org.b612foundation.adam.stk.propagators.ForceModelHelper;
import org.b612foundation.stk.StkLicense;
import org.junit.Test;

public class TransformationTest {
  // Data for scenarios. Dates are STK's TAIG, coordinates in km, velocity in km/s.
  ImpactorScenario[] scenarios = {
    new ImpactorScenario(
        "2007-07-22T09:45:20.382Z",
        "2009-07-21T21:45:20.382Z",
        "2009-08-21T21:45:20.382Z",
        new Cartesian(-236121040.2, 331996754.1, 143912102),
        new Cartesian(-13.60452956, -2.016598238, -0.874821429),
        new Cartesian(73960950.35, -121837565.6, -52815163.62),
        new Cartesian(29.58047076, 26.55353119, 9.135546988),
        new Cartesian(-1179.175917, -5758.297502, 2468.035341),
        new Cartesian(4.028721308, 13.35109648, 3.410669947)),
    new ImpactorScenario(
        "2033-04-29T01:35:28.312Z",
        "2035-04-29T13:35:28.312Z",
        "2036-04-29T13:35:28.312Z",
        new Cartesian(-55251606.88, 93510033.11, 25940567.05),
        new Cartesian(-33.96366572, -14.53926934, -7.435807572),
        new Cartesian(-117727431.7, -86216132.86, -37367077.55),
        new Cartesian(7.070857521, -27.96085037, -8.082478469),
        new Cartesian(3813.182654, 2678.9828, 4340.002486),
        new Cartesian(-11.01793748, -6.490343834, 1.223356922)),
    new ImpactorScenario(
        "2058-11-12T10:25:07.359Z",
        "2060-11-11T22:25:07.359Z",
        "2060-11-12T22:25:07.359Z",
        new Cartesian(-558409272.9, 10645080.94, -78805272.91),
        new Cartesian(-0.845770204, -9.051518955, -2.809511089),
        new Cartesian(96269703.27, 103247168.9, 44750442.96),
        new Cartesian(-20.36165962, 35.06313371, 4.489928184),
        new Cartesian(2492.90383, -4025.850134, 4258.584017),
        new Cartesian(2.746087073, 17.39752145, -3.165955497)),
    new ImpactorScenario(
        "2084-02-05T09:03:28.009Z",
        "2086-02-04T21:03:28.009Z",
        "2086-03-04T21:03:28.009Z",
        new Cartesian(39703984.8, -103966518.1, -21618106.39),
        new Cartesian(30.30306473, 18.18170268, -7.867554612),
        new Cartesian(-104830363.5, 95174574.72, 41240272.79),
        new Cartesian(-17.33661116, -18.69096986, 8.166280644),
        new Cartesian(-4701.754215, -1515.86435, -4020.600668),
        new Cartesian(4.103361807, 0.833269082, 16.62677842)),
    new ImpactorScenario(
        "2108-05-30T06:59:08.232Z",
        "2110-05-30T18:59:08.232Z",
        "2110-05-30T19:59:08.232Z",
        new Cartesian(104101339.5, -140675712.5, -37078480.01),
        new Cartesian(28.85751653, 8.271540415, 7.178252004),
        new Cartesian(-57883363.27, -128566930.3, -55706963.64),
        new Cartesian(31.86288808, -18.60360814, 4.871364616),
        new Cartesian(3768.237771, 1682.701845, -4847.041831),
        new Cartesian(4.820831017, -8.072427817, 9.435331215))
  };

  class ImpactorScenario {
    JulianDate epoch;
    JulianDate hit;
    JulianDate end;
    Cartesian startPositionSunIcrf;
    Cartesian startVelocitySunIcrf;
    Cartesian endPositionSunIcrf;
    Cartesian endVelocitySunIcrf;
    Cartesian endPositionEarthIcrf;
    Cartesian endVelocityEarthIcrf;

    ImpactorScenario(
        String epoch,
        String hit,
        String end,
        Cartesian startPositionSunIcrf,
        Cartesian startVelocitySunIcrf,
        Cartesian endPositionSunIcrf,
        Cartesian endVelocitySunIcrf,
        Cartesian endPositionEarthIcrf,
        Cartesian endVelocityEarthIcrf) {
      this.epoch =
          new JulianDate(ZonedDateTime.parse(epoch), TimeStandard.getInternationalAtomicTime());
      this.hit =
          new JulianDate(ZonedDateTime.parse(hit), TimeStandard.getInternationalAtomicTime());
      this.end =
          new JulianDate(ZonedDateTime.parse(end), TimeStandard.getInternationalAtomicTime());
      this.startPositionSunIcrf = startPositionSunIcrf;
      this.startVelocitySunIcrf = startVelocitySunIcrf;
      this.endPositionSunIcrf = endPositionSunIcrf;
      this.endVelocitySunIcrf = endVelocitySunIcrf;
      this.endPositionEarthIcrf = endPositionEarthIcrf;
      this.endVelocityEarthIcrf = endVelocityEarthIcrf;
    }

    OrbitParameterMessage getOpm(String name) {
      return new OrbitParameterMessage()
          .setCcsds_opm_vers("2.0")
          .setMetadata(
              new OdmCommonMetadata()
                  .setObject_name(name)
                  .setObject_id("KillerAsteroid")
                  .setCenter_name(CenterName.SUN)
                  .setRef_frame(ReferenceFrame.ICRF)
                  .setTime_system(TimeSystem.UTC))
          .setState_vector(
              new StateVector()
                  .setEpoch(epoch.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString())
                  .setX(startPositionSunIcrf.getX())
                  .setY(startPositionSunIcrf.getY())
                  .setZ(startPositionSunIcrf.getZ())
                  .setX_dot(startVelocitySunIcrf.getX())
                  .setY_dot(startVelocitySunIcrf.getY())
                  .setZ_dot(startVelocitySunIcrf.getZ()));
    }
  }

  /** Initialize an orbit with the data from Hits Earth test. */
  private PropagatedInterplanetaryOrbit makeAsteroidThatHitsEarth(
      ImpactorScenario scenario, String name) throws IOException {
    return makeAsteroidThatHitsEarth(scenario, name, scenario.end);
  }

  private PropagatedInterplanetaryOrbit makeAsteroidThatHitsEarth(
      ImpactorScenario scenario, String name, JulianDate endDate) throws IOException {
    StkLicense.activate();
    ForceModelHelper.loadStandardObjects();

    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();

    return PropagatedInterplanetaryOrbit.fromOpm(scenario.getOpm(name), endDate, config);
  }

  private void compareEphemerides(Ephemeris expected, Ephemeris actual) {
    int numPoints = expected.getTimes().size();
    assertEquals("Ephemeris point counts differ", numPoints, actual.getTimes().size());
    System.out.println("Ephemerides have " + numPoints + " points each");
    DateMotionCollection1<Cartesian> expectedData = expected.getInterpolator().getData();
    DateMotionCollection1<Cartesian> actualData = actual.getInterpolator().getData();
    assertEquals("Expected data point count differs", numPoints, expectedData.getCount());
    assertEquals("Actual data point count differs", numPoints, actualData.getCount());
    for (int i = 0; i < numPoints; i++) {
      assertEquals(
          "Ephemeris dates differ at " + i, expected.getTimes().get(i), actual.getTimes().get(i));
      // We are not reading back velocity or accelerations, so check 0'th order only.
      Cartesian point1 = expectedData.get(0).get(i);
      Cartesian point2 = actualData.get(0).get(i);
      assertTrue("Point values differ at " + i, point1.equalsEpsilon(point2, 1e-3));
    }
  }

  @Test
  public void testReadBackStandardEphemeris() throws Exception {
    // One scenario is enough. Also, don't propagate the whole 2 years, not needed to check IO.
    JulianDate endTime = scenarios[0].epoch.addDays(30);
    PropagatedInterplanetaryOrbit orbit =
        makeAsteroidThatHitsEarth(scenarios[0], "SomeRock", endTime);
    Duration timeStep = Duration.fromDays(1.0);
    StkEphemerisFile originalEphem = orbit.getEphemeris(scenarios[0].epoch, endTime, timeStep);
    StringWriter writer = new StringWriter();
    originalEphem.writeTo(writer);

    StkPropagator stk = new StkPropagator();
    StkEphemerisFile recoveredEphem = stk.loadFromString(writer.toString());
    compareEphemerides(originalEphem.getData(), recoveredEphem.getData());
  }

  @Test
  public void testReadBackRawEphemeris() throws Exception {
    JulianDate endTime = scenarios[0].epoch.addDays(30);
    PropagatedInterplanetaryOrbit orbit =
        makeAsteroidThatHitsEarth(scenarios[0], "SomeRock", endTime);
    StringWriter writer = new StringWriter();
    orbit.writeRawEphemeris(writer);
    StkPropagator stk = new StkPropagator();
    StkEphemerisFile recoveredEphem = stk.loadFromString(writer.toString());

    List<JulianDate> expectedDates = orbit.getRawDates();
    List<JulianDate> actualDates = recoveredEphem.getData().getTimes();
    List<double[]> expectedValues = orbit.getRawValues();
    DateMotionCollection1<Cartesian> actualData =
        recoveredEphem.getData().getInterpolator().getData();
    assertEquals("Raw timestamp counts differ", expectedDates.size(), actualDates.size());
    for (int i = 0; i < expectedDates.size(); i++) {
      // Allow for some truncation when printing.
      double delta = expectedDates.get(i).secondsDifference(actualDates.get(i));
      assertTrue(
          "Raw timestamps differ at "
              + i
              + " by "
              + delta
              + " seconds:\nexpected "
              + expectedDates.get(i)
              + "\ngot "
              + actualDates.get(i),
          Math.abs(delta) < 1e-3);

      // As before, only compare positions, because we are not recovering velocities.
      Cartesian point1 =
          new Cartesian(
              expectedValues.get(i)[0], expectedValues.get(i)[1], expectedValues.get(i)[2]);
      Cartesian point2 = actualData.get(0).get(i);
      assertTrue("Point values differ at " + i, point1.equalsEpsilon(point2, 1e-3));
    }
  }

  @Test
  public void testTransformToEarth() throws Exception {
    for (int i = 0; i < scenarios.length; ++i) {
      runTransformToEarth(scenarios[i], "Scenario " + (i + 1));
    }
  }

  /**
   * Get instantaneous reference frame transformation from Sun ICRF to Earth ICRF at the given time
   * for positions and velocities.
   */
  public KinematicTransformation getSunIcrfToEarthIcrf(JulianDate when) {
    return GeometryTransformer.getReferenceFrameTransformation(
            CentralBodiesFacet.getFromContext().getSun().getInertialFrame(),
            CentralBodiesFacet.getFromContext().getEarth().getInertialFrame())
        .evaluate(when, 1);
  }

  private void runTransformToEarth(ImpactorScenario scenario, String name) throws Exception {
    PropagatedInterplanetaryOrbit orbit = makeAsteroidThatHitsEarth(scenario, name);
    StringWriter writer = new StringWriter();
    orbit.writeRawEphemeris(writer);

    Motion1<Cartesian> hit = orbit.getEvaluator().evaluate(scenario.hit, 1);
    // Check match in Sun frame.
    double error =
        hit.getValue().subtract(scenario.endPositionSunIcrf.multiply(1e3)).getMagnitude() / 1e3;
    assertTrue(
        "Distance to expected impact location in Sun ICRF for scenario "
            + name
            + " is "
            + error
            + " km",
        error < 2.0);
    error =
        hit.getFirstDerivative().subtract(scenario.endVelocitySunIcrf.multiply(1e3)).getMagnitude()
            / 1e3;
    assertTrue(
        "Velocity error at expected impact location in Sun ICRF for scenario "
            + name
            + " is "
            + error
            + " km/s",
        error < 0.1);

    Motion1<Cartesian> atEarth = getSunIcrfToEarthIcrf(scenario.hit).transform(hit, 1);
    error =
        atEarth.getValue().subtract(scenario.endPositionEarthIcrf.multiply(1e3)).getMagnitude()
            / 1e3;
    assertTrue(
        "Distance to expected impact location in Earth ICRF for scenario "
            + name
            + " is "
            + error
            + " km",
        error < 2.0);
    error =
        atEarth
                .getFirstDerivative()
                .subtract(scenario.endVelocityEarthIcrf.multiply(1e3))
                .getMagnitude()
            / 1e3;
    assertTrue(
        "Velocity error at expected impact location in Earth ICRF for scenario "
            + name
            + " is "
            + error
            + " km/s",
        error < 0.1);

    StkPropagator stk = new StkPropagator();
    StkEphemerisFile recoveredEphem = stk.loadFromString(writer.toString());
    List<DatedValue<Cartesian>> inEarth = stk.transformToEarthFrame(recoveredEphem);

    // Dates should match.
    double minDistance = Double.MAX_VALUE;
    double maxDistance = Double.MIN_VALUE;
    List<JulianDate> expectedDates = recoveredEphem.getData().getTimes();
    assertEquals(
        "Times for distances to Earth differ in size", expectedDates.size(), inEarth.size());
    for (int i = 0; i < expectedDates.size(); i++) {
      assertEquals(
          "Timestamps for point in Earth frame differ at " + i,
          expectedDates.get(i),
          inEarth.get(i).getDate());
      Cartesian value = inEarth.get(i).getValue();
      double dist = value.getMagnitude() / 1000;
      minDistance = Math.min(minDistance, dist);
      maxDistance = Math.max(maxDistance, dist);
    }
    System.out.println(
        name
            + ": distances to Earth center: min "
            + minDistance
            + " km, max "
            + maxDistance
            + " km");
    assertTrue(
        name + " min distance not within Earth's radius: " + minDistance + " km",
        minDistance < 6371);
  }

  @Test
  public void testDetectImpact() throws Exception {
    for (int i = 0; i < scenarios.length; ++i) {
      runDetectImpact(scenarios[i], "Scenario " + (i + 1));
    }
  }

  private void runDetectImpact(ImpactorScenario scenario, String name) throws Exception {
    PropagatedInterplanetaryOrbit orbit = makeAsteroidThatHitsEarth(scenario, name);
    StringWriter writer = new StringWriter();
    orbit.writeRawEphemeris(writer);
    StkPropagator stk = new StkPropagator();
    StkEphemerisFile recoveredEphem = stk.loadFromString(writer.toString());
    List<DatedValue<Double>> impacts = stk.findEarthImpacts(recoveredEphem, 1e3); // 1 km
    List<DatedValue<Double>> minima = stk.findClosestApproachesToEarth(recoveredEphem);

    // The values are both minima and threshold crossings on the way in and out, if any. Print them
    // all.
    System.out.println(
        "\nSCEN " + name + ": " + impacts.size() + " impacts, " + minima.size() + " minima");
    for (DatedValue<Double> v : impacts) {
      System.out.println("\thit " + v.getValue() + " on " + v.getDate());
    }

    double minDistance = Double.MAX_VALUE;
    double closestAfter = Double.MAX_VALUE;
    JulianDate impactDate = impacts.get(0).getDate();
    JulianDate approachDate = minima.isEmpty() ? null : minima.get(0).getDate();
    for (DatedValue<Double> v : minima) {
      System.out.println("\tmin " + v.getValue() + " on " + v.getDate());
      if (v.getValue() < minDistance) {
        minDistance = v.getValue();
      }
      double minutesAfter = impactDate.minutesDifference(v.getDate());
      if (minutesAfter > 0 && minutesAfter < closestAfter) {
        approachDate = v.getDate();
        closestAfter = minutesAfter;
      }
    }

    assertFalse("No impacts found in scenario " + name, impacts.isEmpty());
    assertTrue("Each impact should correspond to a minimum", minima.size() >= impacts.size());

    double timeDelta = impactDate.secondsDifference(scenario.hit);
    System.out.println(
        "Impact on "
            + impactDate
            + ", delta from expected "
            + timeDelta
            + " seconds; matching minimum on "
            + approachDate);
    assertTrue(
        "Scenario " + name + " didn't get within Earth radius, min distance " + minDistance + " m",
        minDistance < 6371e3);
    // All match with less than 0.2 seconds error from original scenarios.
    assertTrue(
        "Scenario " + name + " impact time too far: " + timeDelta + " seconds",
        Math.abs(timeDelta) < 1);
    // At about 12 km/s these things go it takes about 10 minutes to cross Earth radius, generously
    // check for 20.
    assertTrue(
        "One of the minima should be close in date to impact, scenario " + name, closestAfter < 20);
  }
}
