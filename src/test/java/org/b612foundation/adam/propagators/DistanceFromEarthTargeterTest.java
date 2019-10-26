package org.b612foundation.adam.propagators;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.datamodel.TargetingParameters;
import org.b612foundation.adam.runnable.AdamRunnableException;
import org.junit.Assert;
import org.junit.Test;

import agi.foundation.Motion1;
import agi.foundation.celestial.CentralBodiesFacet;
import agi.foundation.celestial.EarthCentralBody;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.geometry.GeometryTransformer;
import agi.foundation.geometry.PointEvaluator;
import agi.foundation.geometry.ReferenceFrame;
import agi.foundation.stk.StkEphemerisFile;
import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;

public class DistanceFromEarthTargeterTest {

  private static int DAYS_BEFORE_IMPACT_TO_SWITCH_TO_TARGETING_PERIGEE = 30;

  private double finalStateVectorDistanceFromEarth(String ephemeris, ReferenceFrame referenceFrame) {
    StkEphemerisFile file = StkEphemerisFile
        .readFrom(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(ephemeris.getBytes()))));
    JulianDate finalTime = file.getData().getTimes().get(file.getData().getTimes().size() - 1);
    Cartesian finalPosition = file.createPoint().getEvaluator().evaluate(finalTime);

    EarthCentralBody earth = CentralBodiesFacet.getFromContext().getEarth();
    PointEvaluator earthEvaluator = GeometryTransformer.observePoint(earth.getCenterOfMassPoint(), referenceFrame);
    Cartesian earthPos = earthEvaluator.evaluate(finalTime, 2).getValue();
    return finalPosition.subtract(earthPos).getMagnitude();
  }

  @Test
  public void testInitialManeuver() throws AdamRunnableException {
    System.out.println("--- testInitialManeuver");
    // This uses the initial state and computed maneuver from desktop scenario 1.
    DistanceFromEarthTargeter targeter = new DistanceFromEarthTargeter();

    JulianDate startEpoch = new JulianDate(2456437.50002315, TimeStandard.getCoordinatedUniversalTime());
    JulianDate stopEpoch = new JulianDate(2458263.62933198, TimeStandard.getCoordinatedUniversalTime());
    Duration duration = stopEpoch.subtractDays(DAYS_BEFORE_IMPACT_TO_SWITCH_TO_TARGETING_PERIGEE).subtract(startEpoch);

    Motion1<Cartesian> initialState = new Motion1<Cartesian>(
        // Location, values in meters.
        new Cartesian(-1.4914794358536252e+11, 1.0582106861692128e+11, 6.0492834101479955e+10),
        // Velocity, values in meters / second.
        new Cartesian(-11.2528789273597756e+03, -22.3258231726462242e+03, -9.7271222877710155e+03));

    TargetingParameters targetingParams = new TargetingParameters();
    targetingParams.setTargetDistanceFromEarth(1e4); // km
    targetingParams.setInitialTargetDistanceFromEarth(5e4); // km
    targetingParams.setTolerance(1); // km
    targetingParams.setRunNominalOnly(false);
    PropagatorConfiguration config = new PropagatorConfiguration(); // Default is all planets, no asteroids.

    ReferenceFrame sunIcrf = CentralBodiesFacet.getFromContext().getSun().getInertialFrame();

    Cartesian initialManeuver = new Cartesian(-0.07517388561431965, 0, 0);

    targeter.initialize(initialState, sunIcrf, startEpoch, initialManeuver, duration, targetingParams, config);

    // No maneuver should be required because the initial maneuver should have been enough to push it away.
    Assert.assertFalse(targeter.runNominalPropagationToCheckRequiresManeuver(targetingParams));
  }

  @Test
  public void testNominalOnly() throws AdamRunnableException {
    System.out.println("--- testNominalOnly");
    // This uses the initial state from desktop scenario 1.
    DistanceFromEarthTargeter targeter = new DistanceFromEarthTargeter();

    JulianDate startEpoch = new JulianDate(2456437.50002315, TimeStandard.getCoordinatedUniversalTime()); // 25 May 2013 00:00:02.00016 UTCG
    JulianDate stopEpoch = new JulianDate(2458263.62933198, TimeStandard.getCoordinatedUniversalTime()); //25 May 2018 03:06:14.28307 UTCG
    Duration duration = stopEpoch.subtractDays(DAYS_BEFORE_IMPACT_TO_SWITCH_TO_TARGETING_PERIGEE).subtract(startEpoch);

    Motion1<Cartesian> initialState = new Motion1<Cartesian>(
        // Location, values in meters.
        new Cartesian(-1.4914794358536252e+11, 1.0582106861692128e+11, 6.0492834101479955e+10),
        // Velocity, values in meters / second.
        new Cartesian(-11.2528789273597756e+03, -22.3258231726462242e+03, -9.7271222877710155e+03));

    TargetingParameters targetingParams = new TargetingParameters();
    targetingParams.setTargetDistanceFromEarth(1e4); // km
    targetingParams.setInitialTargetDistanceFromEarth(5e4); // km
    targetingParams.setTolerance(1); // km
    targetingParams.setRunNominalOnly(true);
    PropagatorConfiguration config = new PropagatorConfiguration(); // Default is all planets, no asteroids.

    ReferenceFrame sunIcrf = CentralBodiesFacet.getFromContext().getSun().getInertialFrame();

    targeter.initialize(initialState, sunIcrf, startEpoch, new Cartesian(), duration, targetingParams, config);

    Assert.assertTrue(targeter.runNominalPropagationToCheckRequiresManeuver(targetingParams));
    // Running propation isn't necessary - checking for a maneuver required already runs the nominal propagation.

    // Final state vector will be too close to earth since targeting didn't run.
    double minDistanceFromEarth = targetingParams.getTargetDistanceFromEarth() * 1000.0
        - targetingParams.getTolerance() * 1000.0;
    Assert.assertTrue(finalStateVectorDistanceFromEarth(targeter.getEphemeris(), sunIcrf) < minDistanceFromEarth);
  }

  @Test
  public void testDesktopScenario1() throws AdamRunnableException {
    System.out.println("--- testDesktopScenario1");
    DistanceFromEarthTargeter targeter = new DistanceFromEarthTargeter();

    JulianDate startEpoch = new JulianDate(2456437.50002315, TimeStandard.getCoordinatedUniversalTime()); //25 May 2013 00:00:02.00016 UTCG
    JulianDate stopEpoch = new JulianDate(2458263.62933198, TimeStandard.getCoordinatedUniversalTime()); //25 May 2018 03:06:14.28307 UTCG
    Duration duration = stopEpoch.subtractDays(DAYS_BEFORE_IMPACT_TO_SWITCH_TO_TARGETING_PERIGEE).subtract(startEpoch);

    Motion1<Cartesian> initialState = new Motion1<Cartesian>(
        // Location, values in meters.
        new Cartesian(-1.4914794358536252e+11, 1.0582106861692128e+11, 6.0492834101479955e+10),
        // Velocity, values in meters / second.
        new Cartesian(-11.2528789273597756e+03, -22.3258231726462242e+03, -9.7271222877710155e+03));

    TargetingParameters targetingParams = new TargetingParameters();
    targetingParams.setTargetDistanceFromEarth(1e4); // km
    targetingParams.setInitialTargetDistanceFromEarth(5e4); // km
    targetingParams.setTolerance(1); // km
    targetingParams.setRunNominalOnly(false);
    PropagatorConfiguration config = new PropagatorConfiguration(); // Default is all planets, no asteroids.

    ReferenceFrame sunIcrf = CentralBodiesFacet.getFromContext().getSun().getInertialFrame();

    targeter.initialize(initialState, sunIcrf, startEpoch, new Cartesian(), duration, targetingParams, config);

    Assert.assertTrue(targeter.runNominalPropagationToCheckRequiresManeuver(targetingParams));

    targeter.propagate();

    double[] finalResults = targeter.getManeuver();
    Assert.assertNotNull(finalResults);
    System.out.println("Maneuver: " + finalResults[0]);
    double expected = 0.0748720378552678; // Desktop maneuver. (Updated for dual miss distance)
    Assert.assertEquals(expected, finalResults[0], Math.abs(expected) * .01);

    // Check that the final state vector of the ephemeris is indeed within tolerance of the target distance from earth.
    Assert.assertEquals(targetingParams.getTargetDistanceFromEarth() * 1000.0,
        finalStateVectorDistanceFromEarth(targeter.getEphemeris(), sunIcrf), targetingParams.getTolerance() * 1000.0);
  }

  @Test
  public void testDesktopScenario2() throws AdamRunnableException {
    System.out.println("--- testDesktopScenario2");
    DistanceFromEarthTargeter targeter = new DistanceFromEarthTargeter();

    JulianDate startEpoch = new JulianDate(2457175.00002315, TimeStandard.getCoordinatedUniversalTime());  // 1 Jun 2015 12:00:02.00000 
    JulianDate stopEpoch = new JulianDate(2460827.49900783, TimeStandard.getCoordinatedUniversalTime());   // 31 May 2025 23:58:34.27651 UTCG
    Duration duration = stopEpoch.subtractDays(DAYS_BEFORE_IMPACT_TO_SWITCH_TO_TARGETING_PERIGEE).subtract(startEpoch);

    Motion1<Cartesian> initialState = new Motion1<Cartesian>(
        // Location, values in meters.
        new Cartesian(-1.3162148431055502e+11, -1.0215322647665953e+11, -5.8415540509782318e+10),
        // Velocity, values in meters / second.
        new Cartesian(21485.8402004957024474, -14313.7297744805437105, -2067.0888991341325891));

    TargetingParameters targetingParams = new TargetingParameters();
    targetingParams.setTargetDistanceFromEarth(1e4); // km
    targetingParams.setInitialTargetDistanceFromEarth(5e4); // km
    targetingParams.setTolerance(1); // km
    targetingParams.setRunNominalOnly(false);
    PropagatorConfiguration config = new PropagatorConfiguration(); // Default is all planets, no asteroids.

    ReferenceFrame sunIcrf = CentralBodiesFacet.getFromContext().getSun().getInertialFrame();

    targeter.initialize(initialState, sunIcrf, startEpoch, new Cartesian(), duration, targetingParams, config);

    Assert.assertTrue(targeter.runNominalPropagationToCheckRequiresManeuver(targetingParams));

    targeter.propagate();

    double[] finalResults = targeter.getManeuver();
    Assert.assertNotNull(finalResults);
    System.out.println("Maneuver: " + finalResults[0]);
    double expected = 0.01261398468347413; // Desktop maneuver. (Updated for dual miss distance)
    Assert.assertEquals(expected, finalResults[0], Math.abs(expected) * .01);  

    // Check that the final state vector of the ephemeris is indeed within tolerance of the target distance from earth.
    Assert.assertEquals(targetingParams.getTargetDistanceFromEarth() * 1000.0,
        finalStateVectorDistanceFromEarth(targeter.getEphemeris(), sunIcrf), targetingParams.getTolerance() * 1000.0);
  }

  @Test
  public void testDesktopScenario3() throws AdamRunnableException {
    System.out.println("--- testDesktopScenario3");
    DistanceFromEarthTargeter targeter = new DistanceFromEarthTargeter();

    JulianDate startEpoch = new JulianDate(2457327.75001157, TimeStandard.getCoordinatedUniversalTime()); // 1 Nov 2015 06:00:00.99965 UTCG
    JulianDate stopEpoch = new JulianDate(2462806.5, TimeStandard.getCoordinatedUniversalTime()); // 1 Nov 2030 00:00:00.00000 UTCG
    Duration duration = stopEpoch.subtractDays(DAYS_BEFORE_IMPACT_TO_SWITCH_TO_TARGETING_PERIGEE).subtract(startEpoch);

    Motion1<Cartesian> initialState = new Motion1<Cartesian>(
        // Location, values in meters.
        new Cartesian(-6.1734205715280347e+11, -1.0066137279860652e+11, -9.7607883465475815e+10),
        // Velocity, values in meters / second.
        new Cartesian(5819.1326109913024993, -7396.4490768075338565, -1590.6044631494423811));

    TargetingParameters targetingParams = new TargetingParameters();
    targetingParams.setTargetDistanceFromEarth(1e4); // km
    targetingParams.setInitialTargetDistanceFromEarth(5e4); // km
    targetingParams.setTolerance(1); // km
    targetingParams.setRunNominalOnly(false);
    PropagatorConfiguration config = new PropagatorConfiguration(); // Default is all planets, no asteroids.

    ReferenceFrame sunIcrf = CentralBodiesFacet.getFromContext().getSun().getInertialFrame();

    targeter.initialize(initialState, sunIcrf, startEpoch, new Cartesian(), duration, targetingParams, config);

    Assert.assertTrue(targeter.runNominalPropagationToCheckRequiresManeuver(targetingParams));

    targeter.propagate();

    double[] finalResults = targeter.getManeuver();
    Assert.assertNotNull(finalResults);
    System.out.println("Maneuver: " + finalResults[0]);
    double expected = 0.006229044440726722; // Desktop maneuver. (Updated for dual miss distance)
    Assert.assertEquals(expected, finalResults[0], Math.abs(expected) * .01);

    // Check that the final state vector of the ephemeris is indeed within tolerance of the target distance from earth.
    Assert.assertEquals(targetingParams.getTargetDistanceFromEarth() * 1000.0,
        finalStateVectorDistanceFromEarth(targeter.getEphemeris(), sunIcrf), targetingParams.getTolerance() * 1000.0);
  }

  @Test
  public void testDesktopScenario4() throws AdamRunnableException {
    System.out.println("--- testDesktopScenario4");
    // This is a 50-year scenario, unlike the others which are 15-year.
    //
    // The initial conditions that match the desktop don't actually result in a collision. Confirm that
    // this results in a targeter that does not require a maneuver. Then propagate backwards from the
    // impact state and use that as an initial state instead.
    DistanceFromEarthTargeter targeter = new DistanceFromEarthTargeter();

    JulianDate startEpoch = new JulianDate(2441257.2503125, TimeStandard.getCoordinatedUniversalTime()); //1 Nov 1971 18:00:27.00000 UTCG
    JulianDate stopEpoch = new JulianDate(2459519.75, TimeStandard.getCoordinatedUniversalTime()); // 1 Nov 2021 06:00:00.00000 UTCG
    Duration duration = stopEpoch.subtractDays(DAYS_BEFORE_IMPACT_TO_SWITCH_TO_TARGETING_PERIGEE).subtract(startEpoch);

    // These are the coordinates that result in impact on the desktop. They don't result in impact
    // in components - in fact, they result in a flyby that is outside the target distance from Earth.
    // This should be detectable using requiresManeuver.
    //
    // Note: this is because of a very close intermediate flyby that amplifies the difference between
    // the propagators of desktop vs components.
    Motion1<Cartesian> initialState = new Motion1<Cartesian>(
        // Location, values in meters.
        new Cartesian(2.4167141842509586e+11, -8.8688120048419128e+10, -5.4902542318402397e+10),
        // Velocity, values in meters / second.
        new Cartesian(-3435.5656629493569199, 19608.3102641408622731, 9841.4952624646775803));

    TargetingParameters targetingParams = new TargetingParameters();
    targetingParams.setTargetDistanceFromEarth(1e4); // km
    targetingParams.setInitialTargetDistanceFromEarth(5e4); // km
    targetingParams.setTolerance(1); // km
    targetingParams.setRunNominalOnly(false);
    PropagatorConfiguration config = new PropagatorConfiguration(); // Default is all planets, no asteroids.

    ReferenceFrame sunIcrf = CentralBodiesFacet.getFromContext().getSun().getInertialFrame();

    targeter.initialize(initialState, sunIcrf, startEpoch, new Cartesian(), duration, targetingParams, config);

    Assert.assertFalse(targeter.runNominalPropagationToCheckRequiresManeuver(targetingParams));

    // The ephemeris retrieved from the nominal run should show that the object ends up far from earth.
    double minTargetDistance = targetingParams.getTargetDistanceFromEarth() * 1000.0
        - targetingParams.getTolerance() * 1000.0;
    Assert.assertTrue(minTargetDistance <= finalStateVectorDistanceFromEarth(targeter.getEphemeris(), sunIcrf));

    // These are the coordinates propagated backwards from the point of collision as output from the desktop.
    // They do result in an impact and therefore require a maneuver.
    Motion1<Cartesian> initialState2 = new Motion1<Cartesian>(
        // Location, values in meters.
        new Cartesian(241642103742.00336, -88417231789.5305, -54766749199.37157),
        // Velocity, values in meters / second.
        new Cartesian(-3459.2170011929456, 19616.267480901595, 9846.45446510894));

    targeter.initialize(initialState2, sunIcrf, startEpoch, new Cartesian(), duration, targetingParams, config);

    Assert.assertTrue(targeter.runNominalPropagationToCheckRequiresManeuver(targetingParams));

    targeter.propagate();

    double[] finalResults2 = targeter.getManeuver();
    Assert.assertNotNull(finalResults2);
    System.out.println("Maneuver: " + finalResults2[0]);
    double expected = -0.001284287871532393; // Desktop maneuver. (Updated for dual miss distance)
    Assert.assertEquals(expected, finalResults2[0], Math.abs(expected) * .01);

    // Check that the final state vector of the ephemeris is indeed within tolerance of the target distance from earth.
    Assert.assertEquals(targetingParams.getTargetDistanceFromEarth() * 1000.0,
        finalStateVectorDistanceFromEarth(targeter.getEphemeris(), sunIcrf), targetingParams.getTolerance() * 1000.0);
}

@Test
public void testDesktopScenario5() throws AdamRunnableException {
  System.out.println("--- testDesktopScenario5");
  // This tests to check if default initial miss distance is not set then it sets it to 5 * target distance and still works.

  DistanceFromEarthTargeter targeter = new DistanceFromEarthTargeter();

  JulianDate startEpoch = new JulianDate(2456437.50002315, TimeStandard.getCoordinatedUniversalTime()); //25 May 2013 00:00:02.00016 UTCG
  JulianDate stopEpoch = new JulianDate(2458263.62933198, TimeStandard.getCoordinatedUniversalTime()); //25 May 2018 03:06:14.28307 UTCG
  Duration duration = stopEpoch.subtractDays(DAYS_BEFORE_IMPACT_TO_SWITCH_TO_TARGETING_PERIGEE).subtract(startEpoch);

  Motion1<Cartesian> initialState = new Motion1<Cartesian>(
      // Location, values in meters.
      new Cartesian(-1.4914794358536252e+11, 1.0582106861692128e+11, 6.0492834101479955e+10),
      // Velocity, values in meters / second.
      new Cartesian(-11.2528789273597756e+03, -22.3258231726462242e+03, -9.7271222877710155e+03));

  TargetingParameters targetingParams = new TargetingParameters();
  targetingParams.setTargetDistanceFromEarth(1e4); // km

  // Not setting this, but it should still converge
  // targetingParams.setInitialTargetDistanceFromEarth(5e4); // km
  
  targetingParams.setTolerance(1); // km
  targetingParams.setRunNominalOnly(false);
  PropagatorConfiguration config = new PropagatorConfiguration(); // Default is all planets, no asteroids.

  ReferenceFrame sunIcrf = CentralBodiesFacet.getFromContext().getSun().getInertialFrame();

  targeter.initialize(initialState, sunIcrf, startEpoch, new Cartesian(), duration, targetingParams, config);

  Assert.assertTrue(targeter.runNominalPropagationToCheckRequiresManeuver(targetingParams));

  targeter.propagate();

  double[] finalResults = targeter.getManeuver();
  Assert.assertNotNull(finalResults);
  System.out.println("Maneuver: " + finalResults[0]);
  double expected = 0.0748720378552678; // Desktop maneuver. (Updated for dual miss distance)
  Assert.assertEquals(expected, finalResults[0], Math.abs(expected) * .01);

  // Check that the final state vector of the ephemeris is indeed within tolerance of the target distance from earth.
  Assert.assertEquals(targetingParams.getTargetDistanceFromEarth() * 1000.0,
      finalStateVectorDistanceFromEarth(targeter.getEphemeris(), sunIcrf), targetingParams.getTolerance() * 1000.0);

}
}
