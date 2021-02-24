package org.b612foundation.adam.stk.propagators;

import static org.junit.Assert.fail;

import agi.foundation.Motion1;
import agi.foundation.celestial.JplDECentralBody;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.coordinates.KeplerianElements;
import agi.foundation.geometry.GeometryTransformer;
import agi.foundation.geometry.ReferenceFrameEvaluator;
import agi.foundation.propagators.NumericalPropagationException;
import agi.foundation.stk.StkEphemerisFile;
import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeInterval;
import agi.foundation.time.TimeStandard;
import java.io.IOException;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.datamodel.PropagatorConfiguration.PlanetGravityMode;
import org.b612foundation.adam.opm.OdmCommonHeader;
import org.b612foundation.adam.opm.OdmCommonMetadata;
import org.b612foundation.adam.opm.OdmCommonMetadata.CenterName;
import org.b612foundation.adam.opm.OdmCommonMetadata.ReferenceFrame;
import org.b612foundation.adam.opm.OdmCommonMetadata.TimeSystem;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.opm.StateVector;
import org.b612foundation.adam.stk.propagators.ForceModelHelper;
import org.b612foundation.stk.StkLicense;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class ForcesTest {

  @Before
  public void activateLicense() throws IOException {
    StkLicense.activate();
  }

  // From jcarrico email on 2016-10-21.
  protected static final OrbitParameterMessage INITIAL_OPM =
      new OrbitParameterMessage()
          .setCcsds_opm_vers("2.0")
          .setHeader(
              new OdmCommonHeader().setCreation_date("2016-10-22").setOriginator("B612-jcarrico"))
          .setMetadata(
              new OdmCommonMetadata()
                  .addComment("Geocentric, Cartesian")
                  .setObject_name("AvAstSat")
                  .setObject_id("2020-900Z")
                  .setCenter_name(CenterName.SUN)
                  .setRef_frame(ReferenceFrame.ICRF)
                  .setTime_system(TimeSystem.UTC))
          .setState_vector(
              new StateVector()
                  .setEpoch("2000-01-01T11:58:55.816Z")
                  .setX(-3.0653634150102222e+008) // km and km/s
                  .setY(-1.1097955684640282e+008)
                  .setZ(-4.8129706422527283e+007)
                  .setX_dot(15.7598552764090590)
                  .setY_dot(-10.5875673291958420)
                  .setZ_dot(-4.5896734328869746));

  // The whole Solar system, general relativity is turned off.
  protected static final PropagatorConfiguration SOLAR_SYSTEM_NO_GR_CONFIG =
      new PropagatorConfiguration()
          .setSun(PlanetGravityMode.POINT_MASS)
          .setMercury(PlanetGravityMode.POINT_MASS)
          .setVenus(PlanetGravityMode.POINT_MASS)
          .setEarth(PlanetGravityMode.POINT_MASS)
          .setMars(PlanetGravityMode.POINT_MASS)
          .setJupiter(PlanetGravityMode.POINT_MASS)
          .setSaturn(PlanetGravityMode.POINT_MASS)
          .setUranus(PlanetGravityMode.POINT_MASS)
          .setNeptune(PlanetGravityMode.POINT_MASS)
          .setPluto(PlanetGravityMode.POINT_MASS)
          .setMoon(PlanetGravityMode.POINT_MASS);

  // Just the Sun, general relativity is turned off.
  protected static final PropagatorConfiguration ONLY_SUN_NO_GR_CONFIG =
      new PropagatorConfiguration()
          .setSun(PlanetGravityMode.POINT_MASS)
          .setMercury(PlanetGravityMode.OMIT)
          .setVenus(PlanetGravityMode.OMIT)
          .setEarth(PlanetGravityMode.OMIT)
          .setMars(PlanetGravityMode.OMIT)
          .setJupiter(PlanetGravityMode.OMIT)
          .setSaturn(PlanetGravityMode.OMIT)
          .setUranus(PlanetGravityMode.OMIT)
          .setNeptune(PlanetGravityMode.OMIT)
          .setPluto(PlanetGravityMode.OMIT)
          .setMoon(PlanetGravityMode.OMIT);

  // The Sun and Jupiter, general relativity is turned off.
  protected static final PropagatorConfiguration SUN_JUPITER_NO_GR_CONFIG =
      new PropagatorConfiguration()
          .setSun(PlanetGravityMode.POINT_MASS)
          .setMercury(PlanetGravityMode.OMIT)
          .setVenus(PlanetGravityMode.OMIT)
          .setEarth(PlanetGravityMode.OMIT)
          .setMars(PlanetGravityMode.OMIT)
          .setJupiter(PlanetGravityMode.POINT_MASS)
          .setSaturn(PlanetGravityMode.OMIT)
          .setUranus(PlanetGravityMode.OMIT)
          .setNeptune(PlanetGravityMode.OMIT)
          .setPluto(PlanetGravityMode.OMIT)
          .setMoon(PlanetGravityMode.OMIT);

  /** For debugging, might remove when everything works. */
  protected void printEphemerides(
      JulianDate startDate, JulianDate endDate, PropagatedInterplanetaryOrbit orbit) {
    StkEphemerisFile eph = orbit.getEphemeris(startDate, endDate, Duration.fromDays(1));
    StringWriter writer = new StringWriter();
    eph.writeTo(writer);
    System.out.println("\n\n" + writer + "\n");
  }

  /**
   * Runs one test. Note that STK values are in m and m/s, while OPM is in km and km/s. All the
   * expected values are copy-pasted from email. There are two ways the date is specified there. We
   * use both and do sanity check.
   *
   * @param fromDate the date of the resulting state vector parsed from spelled-out string
   *     (year-month-etc).
   * @param fromNumber the date of the resulting state vector obtained from the numeric value of
   *     Julian days, should be the same value as fromDate.
   * @param expectedPosition the expected coordinate (Sun-centered ICRF) in meters.
   * @param expectedVelocity the expected velocity (Sun-centered ICRF) in m/s.
   * @param positionEps tolerance for position, magnitude of the vector difference in meters.
   * @param velocityEps tolerance for velocity, magnitude of the vector difference in m/s.
   */
  private void runOneTest(
      OrbitParameterMessage opm,
      JulianDate fromDate,
      JulianDate fromNumber,
      Cartesian expectedPosition,
      Cartesian expectedVelocity,
      double positionEps,
      double velocityEps) {
    // Date is parsed from email, so let's make sure the two versions agree.
    // 1e-3 days is 86.4 seconds. Note that speeds are on the order of tens of km/s, so this eats
    // into the epsilons.
    System.out.println(
        "Two versions of the date differ by "
            + (fromNumber.subtract(fromDate).getSeconds())
            + " seconds");
    Assert.assertEquals(fromDate.getTotalDays(), fromNumber.getTotalDays(), 1e-3);

    PropagatedInterplanetaryOrbit orbit =
        PropagatedInterplanetaryOrbit.fromOpm(opm, fromNumber, SOLAR_SYSTEM_NO_GR_CONFIG);
    Motion1<Cartesian> posVel = orbit.getMotion(fromNumber);
    Cartesian position = posVel.getValue();
    Cartesian velocity = posVel.getFirstDerivative();
    Cartesian deltaPosition = position.subtract(expectedPosition);
    Cartesian deltaVelocity = velocity.subtract(expectedVelocity);

    System.out.println("Deltas " + deltaPosition + "; " + deltaVelocity);
    System.out.println(deltaPosition.getMagnitude() + "; " + deltaVelocity.getMagnitude());
    System.out.println("Actuals " + position + "; " + velocity);
    System.out.println(
        "Actual speed "
            + velocity.getMagnitude()
            + "; time to cover distance "
            + (deltaPosition.getMagnitude() / velocity.getMagnitude())
            + " seconds");

    Assert.assertTrue(deltaPosition.getMagnitude() < positionEps);
    Assert.assertTrue(deltaVelocity.getMagnitude() < velocityEps);
    for (int i = 0; i < 3; i++) {
      Assert.assertEquals(expectedPosition.get(i), position.get(i), positionEps);
      Assert.assertEquals(expectedVelocity.get(i), velocity.get(i), velocityEps);
    }
  }

  @Test
  public void testDatesOutOfRange() throws Exception {
    TimeInterval supportedRange = ForceModelHelper.getSupportedDateRange();

    JulianDate tooEarly = supportedRange.getStart().subtractSeconds(1);

    try {
      runOneTest(
          INITIAL_OPM,
          tooEarly,
          tooEarly,
          // The rest of these parameters don't matter.
          new Cartesian(7.3978163610693619e10, -1.2182276005571476e11, -5.2811158832497582e10),
          new Cartesian(31.7100034398931780e3, 29.9657246374751020e3, 0.6754531613947713e3),
          4.5, // meters
          0.01); // limit to 1 cm/s; actual < 5 mm
      fail();
    } catch (NumericalPropagationException e) {
      // Expected.
    }

    JulianDate tooLate = supportedRange.getStop().addSeconds(1);

    try {
      runOneTest(
          INITIAL_OPM,
          tooLate,
          tooLate,
          // The rest of these parameters don't matter.
          new Cartesian(7.3978163610693619e10, -1.2182276005571476e11, -5.2811158832497582e10),
          new Cartesian(31.7100034398931780e3, 29.9657246374751020e3, 0.6754531613947713e3),
          4.5, // meters
          0.01); // limit to 1 cm/s; actual < 5 mm
      fail();
    } catch (NumericalPropagationException e) {
      // Expected.
    }
  }

  @Test
  public void testAtPerigee() throws Exception {
    // Dates from STK output:
    // UTC Gregorian Date: 21 Jul 2009 21:55:08.813  UTC Julian Date: 2455034.41329645
    // Ignored Julian Ephemeris Date: 2455034.41406247
    runOneTest(
        INITIAL_OPM,
        new JulianDate(
            ZonedDateTime.parse("2009-07-21T21:55:08.813Z"),
            TimeStandard.getCoordinatedUniversalTime()),
        new JulianDate(2455034.41329645, TimeStandard.getCoordinatedUniversalTime()),
        new Cartesian(7.3978163610693619e10, -1.2182276005571476e11, -5.2811158832497582e10),
        new Cartesian(31.7100034398931780e3, 29.9657246374751020e3, 0.6754531613947713e3),
        4.5, // meters
        0.01); // limit to 1 cm/s; actual < 5 mm
  }

  @Test
  public void testTenYearsAfterSwingBy() throws Exception {
    runOneTest(
        INITIAL_OPM,
        new JulianDate(
            ZonedDateTime.parse("2019-07-22T09:55:05.813Z"),
            TimeStandard.getCoordinatedUniversalTime()),
        new JulianDate(2458686.91326173, TimeStandard.getCoordinatedUniversalTime()),
        new Cartesian(4.9782746773377337e10, 2.1886396125292808e11, 5.0570942661222927e10),
        new Cartesian(-19.6851468985649450e3, 7.9694302041760183e3, 7.0701308566945276e3),
        1200, // 1.2 km
        0.01); // 1 cm/s
  }

  @Test
  public void testEmeme() {
    // This test uses all the same numbers as testAtPerigee, just transformed to sun-centered Earth
    // mean ecliptic mean
    // equinox.
    OrbitParameterMessage opm = INITIAL_OPM.deepCopy();
    opm.getMetadata().setRef_frame(ReferenceFrame.EMEME2000);

    ReferenceFrameGenerator rfg = new ReferenceFrameGenerator();
    ReferenceFrameEvaluator eval =
        GeometryTransformer.getReferenceFrameTransformation(
            rfg.getReferenceFrame(
                OdmCommonMetadata.ReferenceFrame.ICRF, OdmCommonMetadata.CenterName.SUN),
            rfg.getReferenceFrame(
                OdmCommonMetadata.ReferenceFrame.EMEME2000, OdmCommonMetadata.CenterName.SUN));

    JulianDate startDate =
        new JulianDate(
            ZonedDateTime.parse(opm.getState_vector().getEpoch()),
            TimeStandard.getCoordinatedUniversalTime());
    JulianDate endDate =
        new JulianDate(
            ZonedDateTime.parse("2009-07-21T21:55:08.813Z"),
            TimeStandard.getCoordinatedUniversalTime());
    JulianDate endDateFromNumber =
        new JulianDate(2455034.41329645, TimeStandard.getCoordinatedUniversalTime());

    StateVector state = opm.getState_vector();
    Motion1<Cartesian> icrfInitialState =
        new Motion1<Cartesian>(
            new Cartesian(state.getX() * 1000, state.getY() * 1000, state.getZ() * 1000),
            new Cartesian(
                state.getX_dot() * 1000, state.getY_dot() * 1000, state.getZ_dot() * 1000));
    Motion1<Cartesian> ememeInitialState =
        eval.evaluate(startDate, 2).transform(icrfInitialState, 2);
    opm.setState_vector(
        new StateVector()
            .setEpoch(state.getEpoch())
            .setX(ememeInitialState.getValue().getX() * .001)
            .setY(ememeInitialState.getValue().getY() * .001)
            .setZ(ememeInitialState.getValue().getZ() * .001)
            .setX_dot(ememeInitialState.getFirstDerivative().getX() * .001)
            .setY_dot(ememeInitialState.getFirstDerivative().getY() * .001)
            .setZ_dot(ememeInitialState.getFirstDerivative().getZ() * .001));

    Motion1<Cartesian> icrfFinalState =
        new Motion1<Cartesian>(
            new Cartesian(7.3978163610693619e10, -1.2182276005571476e11, -5.2811158832497582e10),
            new Cartesian(31.7100034398931780e3, 29.9657246374751020e3, 0.6754531613947713e3));
    Motion1<Cartesian> ememeFinalState = eval.evaluate(startDate, 2).transform(icrfFinalState, 2);

    runOneTest(
        opm,
        endDate,
        endDateFromNumber,
        ememeFinalState.getValue(),
        ememeFinalState.getFirstDerivative(),
        4.5, // meters
        0.01); // limit to 1 cm/s; actual < 5 mm
  }

  private double getJplSunG() {
    // Returns gravitational constant in m^3/s^2.
    return ForceModelHelper.JPL_DE.getGravitationalParameter(JplDECentralBody.SUN);
  }

  @Test
  public void testKeplerianElements() {
    // This test is exactly like testTenYearsAfterSwingBy except that we use Keplerian elements
    // instead of cartesian.
    OrbitParameterMessage opm = INITIAL_OPM.deepCopy();

    // Compute keplerian elements from the cartesian elements already in the OPM.
    Cartesian initialPosition =
        new Cartesian(
            opm.getState_vector().getX() * 1000,
            opm.getState_vector().getY() * 1000,
            opm.getState_vector().getZ() * 1000);
    Cartesian initialVelocity =
        new Cartesian(
            opm.getState_vector().getX_dot() * 1000,
            opm.getState_vector().getY_dot() * 1000,
            opm.getState_vector().getZ_dot() * 1000);
    double sunGravity = getJplSunG();
    KeplerianElements keplerian =
        new KeplerianElements(initialPosition, initialVelocity, sunGravity);
    // STK units are m^3/s^2 and m, OPM units are km^3/s^2 and km.
    // STK units are radians, OPM units are degrees.
    opm.setKeplerian(
        new org.b612foundation.adam.opm.KeplerianElements()
            .setArg_of_pericenter(Math.toDegrees(keplerian.getArgumentOfPeriapsis()))
            .setEccentricity(keplerian.getEccentricity())
            .setGm(keplerian.getGravitationalParameter() / 1e9)
            .setInclination(Math.toDegrees(keplerian.getInclination()))
            .setRa_of_asc_node(Math.toDegrees(keplerian.getRightAscensionOfAscendingNode()))
            .setSemi_major_axis(keplerian.getSemimajorAxis() / 1000)
            .setTrue_anomaly(Math.toDegrees(keplerian.getTrueAnomaly())));

    // 30 km; so large because the conversion between keplerian and cartesian coordinates is not
    // exact.
    double positionEps = 30000;
    // 1 cm/s
    double velocityEps = 0.01;
    runOneTest(
        opm,
        new JulianDate(
            ZonedDateTime.parse("2019-07-22T09:55:05.813Z"),
            TimeStandard.getCoordinatedUniversalTime()),
        new JulianDate(2458686.91326173, TimeStandard.getCoordinatedUniversalTime()),
        new Cartesian(4.9782746773377337e10, 2.1886396125292808e11, 5.0570942661222927e10),
        new Cartesian(-19.6851468985649450e3, 7.9694302041760183e3, 7.0701308566945276e3),
        positionEps,
        velocityEps);
  }

  @Test
  public void testHitsEarth() throws Exception {
    // STK Components values are in m and m/s.
    Cartesian expectedPosition =
        new Cartesian(-5.9932231588364700000e9, -1.078913372809520000e11, -4.167415391575490000e10);
    Cartesian expectedVelocity =
        new Cartesian(35.4047344932644000000e3, -5.5175410907140700000e3, -8.5497232261685700000e3);
    // Date given in STK Desktop's TAIG.
    JulianDate epoch =
        new JulianDate(
            ZonedDateTime.parse("2000-01-01T11:59:27.816Z"),
            TimeStandard.getInternationalAtomicTime());
    JulianDate endTime =
        new JulianDate(
            ZonedDateTime.parse("2020-01-01T11:59:27.816Z"),
            TimeStandard.getInternationalAtomicTime());

    OrbitParameterMessage startOpm =
        new OrbitParameterMessage()
            .setCcsds_opm_vers("2.0")
            .setMetadata(
                new OdmCommonMetadata()
                    .setObject_name("HitTheEarth")
                    .setObject_id("KillerAsteroid")
                    .setCenter_name(CenterName.SUN)
                    .setRef_frame(ReferenceFrame.ICRF)
                    .setTime_system(TimeSystem.UTC))
            .setState_vector(
                new StateVector()
                    .setEpoch(
                        epoch.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString())
                    .setX(1.9614856544727500000e7) // km and km/s
                    .setY(1.4843205548512800000e8)
                    .setZ(5.5892860024181600000e7)
                    .setX_dot(-28.4909386978969000000)
                    .setY_dot(-5.3762767683990300000)
                    .setZ_dot(2.5848941723961300000));

    PropagatedInterplanetaryOrbit orbit =
        PropagatedInterplanetaryOrbit.fromOpm(
            startOpm, endTime.addDays(10), SOLAR_SYSTEM_NO_GR_CONFIG);
    Motion1<Cartesian> posVel = orbit.getMotion(endTime);
    Cartesian position = posVel.getValue();
    Cartesian velocity = posVel.getFirstDerivative();
    Cartesian deltaPosition = position.subtract(expectedPosition);
    Cartesian deltaVelocity = velocity.subtract(expectedVelocity);
    System.out.println("==== HIT EARTH ====");
    System.out.println("Epoch " + epoch.getTotalDays() + " " + epoch.getStandard());
    System.out.println("End " + endTime.getTotalDays() + " " + endTime.getStandard());
    System.out.println("Deltas " + deltaPosition + "; " + deltaVelocity);
    System.out.println(deltaPosition.getMagnitude() + "; " + deltaVelocity.getMagnitude());
    System.out.println("Actuals " + position + "; " + velocity);
    System.out.println(
        "Actual speed "
            + velocity.getMagnitude()
            + "; time to cover distance "
            + (deltaPosition.getMagnitude() / velocity.getMagnitude())
            + " seconds");

    double positionEps = 10e3; // 10 km
    double velocityEps = 1e-2; // m/s
    Assert.assertTrue(deltaPosition.getMagnitude() < positionEps);
    Assert.assertTrue(deltaVelocity.getMagnitude() < velocityEps);
    for (int i = 0; i < 3; i++) {
      Assert.assertEquals(expectedPosition.get(i), position.get(i), positionEps);
      Assert.assertEquals(expectedVelocity.get(i), velocity.get(i), velocityEps);
    }
    // printEphemerides(epoch, endTime, orbit);
    // OR
    // StringWriter writer = new StringWriter();
    // orbit.writeRawEphemeris(writer);
    // System.out.println("\n\n" + writer + "\n");
  }

  @Test
  public void testOnlySun() throws Exception {
    // Assume the object is in circular orbit around the Sun, and this is the radius in meters.
    double radius = 224400000e3;
    double gm = ForceModelHelper.JPL_DE.getGravitationalParameter(JplDECentralBody.SUN);
    // Then, given the value of GM, these would be the orbital velocity and period.
    double speed = Math.sqrt(gm / radius);
    double periodDays = 2 * Math.PI * Math.sqrt(radius * radius * radius / gm) / 86400;
    // The epoch is an arbitrary number, we do everything relative to it.
    double epochDays = 2457700;

    Cartesian expectedPosition = new Cartesian(radius, 0, 0);
    Cartesian expectedVelocity = new Cartesian(0, speed, 0);
    JulianDate epoch = new JulianDate(epochDays, TimeStandard.getInternationalAtomicTime());
    // Check the values after 10 complete orbits.
    JulianDate endTime =
        new JulianDate(epochDays + 10 * periodDays, TimeStandard.getInternationalAtomicTime());

    OrbitParameterMessage startOpm =
        new OrbitParameterMessage()
            .setCcsds_opm_vers("2.0")
            .setMetadata(
                new OdmCommonMetadata()
                    .setObject_name("IgnorePlanets")
                    .setObject_id("Blah")
                    .setCenter_name(CenterName.SUN)
                    .setRef_frame(ReferenceFrame.ICRF)
                    .setTime_system(TimeSystem.UTC))
            .setState_vector(
                new StateVector()
                    .setEpoch(
                        epoch.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString())
                    .setX(expectedPosition.getX() * 1e-3) // km and km/s
                    .setY(expectedPosition.getY() * 1e-3)
                    .setZ(expectedPosition.getZ() * 1e-3)
                    .setX_dot(expectedVelocity.getX() * 1e-3)
                    .setY_dot(expectedVelocity.getY() * 1e-3)
                    .setZ_dot(expectedVelocity.getZ() * 1e-3));

    PropagatedInterplanetaryOrbit orbit =
        PropagatedInterplanetaryOrbit.fromOpm(startOpm, endTime, ONLY_SUN_NO_GR_CONFIG);
    Motion1<Cartesian> posVel = orbit.getMotion(endTime);
    Cartesian position = posVel.getValue();
    Cartesian velocity = posVel.getFirstDerivative();
    Cartesian deltaPosition = position.subtract(expectedPosition);
    Cartesian deltaVelocity = velocity.subtract(expectedVelocity);
    System.out.println("==== SUN ONLY ====");
    System.out.println("Deltas " + deltaPosition + "; " + deltaVelocity);
    System.out.println(deltaPosition.getMagnitude() + "; " + deltaVelocity.getMagnitude());
    System.out.println("Actuals " + position + "; " + velocity);
    System.out.println(
        "Actual speed "
            + velocity.getMagnitude()
            + "; time to cover distance "
            + (deltaPosition.getMagnitude() / velocity.getMagnitude())
            + " seconds");
    System.out.println("Speed " + speed + ", period " + periodDays + ", Sun GM " + gm);

    double positionEps = 1.0; // m
    double velocityEps = 1e-6; // m/s
    Assert.assertTrue(
        "deltaPosition is too large " + deltaPosition.getMagnitude(),
        deltaPosition.getMagnitude() < positionEps);
    Assert.assertTrue(
        "deltaVelocity is too large " + deltaVelocity.getMagnitude(),
        deltaVelocity.getMagnitude() < velocityEps);
    for (int i = 0; i < 3; i++) {
      Assert.assertEquals(
          "expected position component " + i,
          expectedPosition.get(i),
          position.get(i),
          positionEps);
      Assert.assertEquals(
          "expected velocity component " + i,
          expectedVelocity.get(i),
          velocity.get(i),
          velocityEps);
    }
  }

  @Test
  public void testForwardsAndBackwards() throws Exception {
    // STK Components values are in m and m/s.
    Cartesian initialPosition =
        new Cartesian(1.9614856544727500000e10, 1.4843205548512800000e11, 5.5892860024181600000e10);
    Cartesian initialVelocity =
        new Cartesian(-28.4909386978969000000e3, -5.3762767683990300000e3, 2.5848941723961300000e3);
    Cartesian finalPosition =
        new Cartesian(-5.9932231588364700000e9, -1.078913372809520000e11, -4.167415391575490000e10);
    Cartesian finalVelocity =
        new Cartesian(35.4047344932644000000e3, -5.5175410907140700000e3, -8.5497232261685700000e3);

    // Date given in STK Desktop's TAIG.
    JulianDate initialTime =
        new JulianDate(
            ZonedDateTime.parse("2000-01-01T11:59:27.816Z"),
            TimeStandard.getInternationalAtomicTime());
    JulianDate finalTime =
        new JulianDate(
            ZonedDateTime.parse("2020-01-01T11:59:27.816Z"),
            TimeStandard.getInternationalAtomicTime());

    // Test it on the earth killing asteroid.
    OrbitParameterMessage opmTemplate =
        new OrbitParameterMessage()
            .setCcsds_opm_vers("2.0")
            .setMetadata(
                new OdmCommonMetadata()
                    .setObject_name("HitTheEarth")
                    .setObject_id("KillerAsteroid")
                    .setCenter_name(CenterName.SUN)
                    .setRef_frame(ReferenceFrame.ICRF)
                    .setTime_system(TimeSystem.UTC));

    OrbitParameterMessage forwardsOpm =
        opmTemplate
            .deepCopy()
            .setState_vector(
                new StateVector()
                    .setEpoch(
                        initialTime
                            .toDateTime(TimeStandard.getCoordinatedUniversalTime())
                            .toString())
                    .setX(initialPosition.getX() * 1e-3) // km and km/s
                    .setY(initialPosition.getY() * 1e-3)
                    .setZ(initialPosition.getZ() * 1e-3)
                    .setX_dot(initialVelocity.getX() * 1e-3)
                    .setY_dot(initialVelocity.getY() * 1e-3)
                    .setZ_dot(initialVelocity.getZ() * 1e-3));

    OrbitParameterMessage backwardsOpm =
        opmTemplate
            .deepCopy()
            .setState_vector(
                new StateVector()
                    .setEpoch(
                        finalTime.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString())
                    .setX(finalPosition.getX() * 1e-3) // km and km/s
                    .setY(finalPosition.getY() * 1e-3)
                    .setZ(finalPosition.getZ() * 1e-3)
                    .setX_dot(finalVelocity.getX() * 1e-3)
                    .setY_dot(finalVelocity.getY() * 1e-3)
                    .setZ_dot(finalVelocity.getZ() * 1e-3));

    double positionEps = 10e3; // 10 km
    double velocityEps = 1e-2; // m/s

    {
      PropagatedInterplanetaryOrbit orbitForwards =
          PropagatedInterplanetaryOrbit.fromOpm(forwardsOpm, finalTime, SOLAR_SYSTEM_NO_GR_CONFIG);
      Motion1<Cartesian> posVel = orbitForwards.getMotion(finalTime);
      Cartesian position = posVel.getValue();
      Cartesian velocity = posVel.getFirstDerivative();
      Cartesian deltaPosition = position.subtract(finalPosition);
      Cartesian deltaVelocity = velocity.subtract(finalVelocity);
      System.out.println("==== FORWARDS PROPAGATION ====");
      System.out.println("Start " + initialTime.getTotalDays() + " " + initialTime.getStandard());
      System.out.println("End " + finalTime.getTotalDays() + " " + finalTime.getStandard());
      System.out.println("Deltas " + deltaPosition + "; " + deltaVelocity);
      System.out.println(deltaPosition.getMagnitude() + "; " + deltaVelocity.getMagnitude());
      System.out.println("Actuals " + position + "; " + velocity);
      System.out.println(
          "Actual speed "
              + velocity.getMagnitude()
              + "; time to cover distance "
              + (deltaPosition.getMagnitude() / velocity.getMagnitude())
              + " seconds");

      Assert.assertTrue(deltaPosition.getMagnitude() < positionEps);
      Assert.assertTrue(deltaVelocity.getMagnitude() < velocityEps);
      for (int i = 0; i < 3; i++) {
        Assert.assertEquals(finalPosition.get(i), position.get(i), positionEps);
        Assert.assertEquals(finalVelocity.get(i), velocity.get(i), velocityEps);
      }
    }

    {
      PropagatedInterplanetaryOrbit orbitBackwards =
          PropagatedInterplanetaryOrbit.fromOpm(
              backwardsOpm, initialTime, SOLAR_SYSTEM_NO_GR_CONFIG);
      Motion1<Cartesian> posVel = orbitBackwards.getMotion(initialTime);
      Cartesian position = posVel.getValue();
      Cartesian velocity = posVel.getFirstDerivative();
      Cartesian deltaPosition = position.subtract(initialPosition);
      Cartesian deltaVelocity = velocity.subtract(initialVelocity);
      System.out.println("==== BACKWARDS PROPAGATION ====");
      System.out.println("Start " + finalTime.getTotalDays() + " " + finalTime.getStandard());
      System.out.println("End " + initialTime.getTotalDays() + " " + initialTime.getStandard());
      System.out.println("Deltas " + deltaPosition + "; " + deltaVelocity);
      System.out.println(deltaPosition.getMagnitude() + "; " + deltaVelocity.getMagnitude());
      System.out.println("Actuals " + position + "; " + velocity);
      System.out.println(
          "Actual speed "
              + velocity.getMagnitude()
              + "; time to cover distance "
              + (deltaPosition.getMagnitude() / velocity.getMagnitude())
              + " seconds");

      Assert.assertTrue(deltaPosition.getMagnitude() < positionEps);
      Assert.assertTrue(deltaVelocity.getMagnitude() < velocityEps);
      for (int i = 0; i < 3; i++) {
        Assert.assertEquals(initialPosition.get(i), position.get(i), positionEps);
        Assert.assertEquals(initialVelocity.get(i), velocity.get(i), velocityEps);
      }
    }
  }

  @Test
  public void testOnlySunAndJupiter() throws Exception {
    // Data from https://docs.google.com/spreadsheets/d/1ypEA0oqZ6Y0io8zj3dUKhq2nNgSfnBaDZZ0Xow8Mx-Q
    // Jupiter tab.
    double startPositionX = 224400000; // km
    double startVelocityY = 24; // km/s
    double epochDays = 2451545;

    Cartesian expectedPosition =
        new Cartesian(1.2917024662828300000e11, -1.804315213471710000e11, -6.1700642083871700000e7);
    Cartesian expectedVelocity =
        new Cartesian(2.0039167736937100000e4, 1.3701380157525100000e4, -9.0141398344605000000);
    JulianDate epoch = new JulianDate(epochDays, TimeStandard.getInternationalAtomicTime());
    JulianDate endTime =
        new JulianDate(epochDays + 7000, TimeStandard.getInternationalAtomicTime());

    OrbitParameterMessage startOpm =
        new OrbitParameterMessage()
            .setCcsds_opm_vers("2.0")
            .setMetadata(
                new OdmCommonMetadata()
                    .setObject_name("SunAndJupiter")
                    .setCenter_name(CenterName.SUN)
                    .setRef_frame(ReferenceFrame.ICRF)
                    .setTime_system(TimeSystem.UTC))
            .setState_vector(
                new StateVector()
                    .setEpoch(
                        epoch.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString())
                    .setX(startPositionX) // km and km/s
                    .setY(0)
                    .setZ(0)
                    .setX_dot(0)
                    .setY_dot(startVelocityY)
                    .setZ_dot(0));

    PropagatedInterplanetaryOrbit orbit =
        PropagatedInterplanetaryOrbit.fromOpm(startOpm, endTime, SUN_JUPITER_NO_GR_CONFIG);
    Motion1<Cartesian> posVel = orbit.getMotion(endTime);
    Cartesian position = posVel.getValue();
    Cartesian velocity = posVel.getFirstDerivative();
    Cartesian deltaPosition = position.subtract(expectedPosition);
    Cartesian deltaVelocity = velocity.subtract(expectedVelocity);
    System.out.println("==== SUN + JUP ====");
    System.out.println("Deltas " + deltaPosition + "; " + deltaVelocity);
    System.out.println(deltaPosition.getMagnitude() + "; " + deltaVelocity.getMagnitude());
    System.out.println("Actuals " + position + "; " + velocity);
    System.out.println(
        "Actual speed "
            + velocity.getMagnitude()
            + "; time to cover distance "
            + (deltaPosition.getMagnitude() / velocity.getMagnitude())
            + " seconds");

    double positionEps = 1.0; // m
    double velocityEps = 1e-7; // m/s
    Assert.assertTrue(deltaPosition.getMagnitude() < positionEps);
    Assert.assertTrue(deltaVelocity.getMagnitude() < velocityEps);
    for (int i = 0; i < 3; i++) {
      Assert.assertEquals(expectedPosition.get(i), position.get(i), positionEps);
      Assert.assertEquals(expectedVelocity.get(i), velocity.get(i), velocityEps);
    }
  }

  @Test
  public void testOnlySunAndJupiterAndCo() throws Exception {
    // Data from https://docs.google.com/spreadsheets/d/1ypEA0oqZ6Y0io8zj3dUKhq2nNgSfnBaDZZ0Xow8Mx-Q
    // Jupiter tab.
    double startPositionX = 224400000; // km
    double startVelocityY = 24; // km/s
    double epochDays = 2451545;

    Cartesian expectedPosition =
        new Cartesian(1.2919802266285300000e11, -1.8041359984290200000e11, -7.497688666392950000e7);
    Cartesian expectedVelocity =
        new Cartesian(2.0036878313956300000e4, 1.3704422147710300000e4, -1.0942037852877500000e1);
    JulianDate epoch = new JulianDate(epochDays, TimeStandard.getInternationalAtomicTime());
    JulianDate endTime =
        new JulianDate(epochDays + 7000, TimeStandard.getInternationalAtomicTime());

    OrbitParameterMessage startOpm =
        new OrbitParameterMessage()
            .setCcsds_opm_vers("2.0")
            .setMetadata(
                new OdmCommonMetadata()
                    .setObject_name("SunAndJupiter")
                    .setCenter_name(CenterName.SUN)
                    .setRef_frame(ReferenceFrame.ICRF)
                    .setTime_system(TimeSystem.UTC))
            .setState_vector(
                new StateVector()
                    .setEpoch(
                        epoch.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString())
                    .setX(startPositionX) // km and km/s
                    .setY(0)
                    .setZ(0)
                    .setX_dot(0)
                    .setY_dot(startVelocityY)
                    .setZ_dot(0));

    PropagatedInterplanetaryOrbit orbit =
        PropagatedInterplanetaryOrbit.fromOpm(startOpm, endTime, SOLAR_SYSTEM_NO_GR_CONFIG);
    Motion1<Cartesian> posVel = orbit.getMotion(endTime);
    Cartesian position = posVel.getValue();
    Cartesian velocity = posVel.getFirstDerivative();
    Cartesian deltaPosition = position.subtract(expectedPosition);
    Cartesian deltaVelocity = velocity.subtract(expectedVelocity);
    System.out.println("==== SUN + JUP + REST ====");
    System.out.println("Deltas " + deltaPosition + "; " + deltaVelocity);
    System.out.println(deltaPosition.getMagnitude() + "; " + deltaVelocity.getMagnitude());
    System.out.println("Actuals " + position + "; " + velocity);
    System.out.println(
        "Actual speed "
            + velocity.getMagnitude()
            + "; time to cover distance "
            + (deltaPosition.getMagnitude() / velocity.getMagnitude())
            + " seconds");

    double positionEps = 1.0; // m
    double velocityEps = 1e-7; // m/s
    Assert.assertTrue(deltaPosition.getMagnitude() < positionEps);
    Assert.assertTrue(deltaVelocity.getMagnitude() < velocityEps);
    for (int i = 0; i < 3; i++) {
      Assert.assertEquals(expectedPosition.get(i), position.get(i), positionEps);
      Assert.assertEquals(expectedVelocity.get(i), velocity.get(i), velocityEps);
    }
  }

  @Test
  public void testAsteroidsSmoke() throws Exception {
    double startPositionX = 224400000; // km
    double startVelocityY = 24; // km/s
    double epochDays = 2451545;

    // These numbers are from the old test without asteroids. For asteroids they
    // will be off, so we don't actually check the numbers, only print them. The
    // primary purpose of this test is to make sure all asteroids load without
    // errors.
    Cartesian expectedPosition =
        new Cartesian(1.2919802266285300000e11, -1.8041359984290200000e11, -7.497688666392950000e7);
    Cartesian expectedVelocity =
        new Cartesian(2.0036878313956300000e4, 1.3704422147710300000e4, -1.0942037852877500000e1);
    JulianDate epoch = new JulianDate(epochDays, TimeStandard.getInternationalAtomicTime());
    JulianDate endTime =
        new JulianDate(epochDays + 7000, TimeStandard.getInternationalAtomicTime());

    OrbitParameterMessage startOpm =
        new OrbitParameterMessage()
            .setCcsds_opm_vers("2.0")
            .setMetadata(
                new OdmCommonMetadata()
                    .setObject_name("AsteroidsTest")
                    .setCenter_name(CenterName.SUN)
                    .setRef_frame(ReferenceFrame.ICRF)
                    .setTime_system(TimeSystem.UTC))
            .setState_vector(
                new StateVector()
                    .setEpoch(
                        epoch.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString())
                    .setX(startPositionX) // km and km/s
                    .setY(0)
                    .setZ(0)
                    .setX_dot(0)
                    .setY_dot(startVelocityY)
                    .setZ_dot(0));

    PropagatorConfiguration config =
        new PropagatorConfiguration()
            .setSun(PlanetGravityMode.POINT_MASS)
            .setMercury(PlanetGravityMode.POINT_MASS)
            .setVenus(PlanetGravityMode.POINT_MASS)
            .setEarth(PlanetGravityMode.POINT_MASS)
            .setMars(PlanetGravityMode.POINT_MASS)
            .setJupiter(PlanetGravityMode.POINT_MASS)
            .setSaturn(PlanetGravityMode.POINT_MASS)
            .setUranus(PlanetGravityMode.POINT_MASS)
            .setNeptune(PlanetGravityMode.POINT_MASS)
            .setPluto(PlanetGravityMode.POINT_MASS)
            .setMoon(PlanetGravityMode.POINT_MASS);
    config.setAsteroidsString("Ceres,PALLAS,Juno,Vesta,Hebe");
    config
        .addAsteroid("iris")
        .addAsteroid("Hygeia")
        .addAsteroid("Eunomia")
        .addAsteroid("Psyche")
        .addAsteroid("Amphitrite")
        .addAsteroid("Europa")
        .addAsteroid("Cybele")
        .addAsteroid("Sylvia")
        .addAsteroid("Thisbe");
    config.addAsteroid("davidA");
    config.addAsteroid("Interamnia");
    Assert.assertEquals("Asteroids count", 16, config.getAsteroids().size());

    PropagatedInterplanetaryOrbit orbit =
        PropagatedInterplanetaryOrbit.fromOpm(startOpm, endTime, config);
    Motion1<Cartesian> posVel = orbit.getMotion(endTime);
    Cartesian position = posVel.getValue();
    Cartesian velocity = posVel.getFirstDerivative();
    Cartesian deltaPosition = position.subtract(expectedPosition);
    Cartesian deltaVelocity = velocity.subtract(expectedVelocity);
    System.out.println("==== ALL PLANETS AND 16 ASTEROIDS ====");
    System.out.println("Deltas " + deltaPosition + "; " + deltaVelocity);
    System.out.println(deltaPosition.getMagnitude() + "; " + deltaVelocity.getMagnitude());
    System.out.println("Actuals " + position + "; " + velocity);
    System.out.println(
        "Actual speed "
            + velocity.getMagnitude()
            + "; time to cover distance "
            + (deltaPosition.getMagnitude() / velocity.getMagnitude())
            + " seconds");
  }
}
