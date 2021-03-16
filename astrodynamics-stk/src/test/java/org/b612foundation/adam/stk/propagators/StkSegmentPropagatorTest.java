package org.b612foundation.adam.stk.propagators;

import static com.google.common.truth.Truth.assertThat;
import static org.b612foundation.adam.common.PropagationHelper.extractFinalState;
import static org.b612foundation.adam.stk.PropagatorTestHelper.getOpm;
import static org.b612foundation.adam.stk.StkPropagationHelper.parseUtcAsJulian;

import agi.foundation.celestial.JplDECentralBody;
import agi.foundation.time.JulianDate;
import com.google.common.collect.ImmutableList;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.b612foundation.adam.common.DistanceType;
import org.b612foundation.adam.common.DistanceUnits;
import org.b612foundation.adam.datamodel.PropagationConfigurationFactory;
import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.exceptions.AdamPropagationException;
import org.b612foundation.adam.opm.OdmCommonMetadata;
import org.b612foundation.adam.opm.OdmCommonMetadata.TimeSystem;
import org.b612foundation.adam.opm.OemDataLine;
import org.b612foundation.adam.opm.OrbitEphemerisMessage;
import org.b612foundation.adam.opm.StateVector;
import org.b612foundation.adam.propagators.OrbitEventType;
import org.b612foundation.stk.StkLicense;
import org.junit.Before;
import org.junit.Test;

// TODO: The test assertion tolerances aren't really good, try to find how to make them closer.
// see if can match up to 7 SD, even better 15 SD
public final class StkSegmentPropagatorTest {

  private static final long SECONDS_IN_DAY = 86400;

  private static final String ASTEROID_101_EPOCH = "2001-01-02T01:13:46.620000Z";
  // Initial state vector, in km and km/s
  // [-150874809.2, -187234595.3, -73785026.7,
  //  14.64403935, -11.75744819, -5.583528281]
  private static final StateVector ASTEROID_101_INITIAL_STATE_VECTOR =
      new StateVector()
          .setEpoch(ASTEROID_101_EPOCH)
          .setX(-150874809.2)
          .setY(-187234595.3)
          .setZ(-73785026.7)
          .setX_dot(14.64403935)
          .setY_dot(-11.75744819)
          .setZ_dot(-5.583528281);
  // Asteroid 101 close approaches, within a distance of 7.0e9 meters
  private static final List<EventEphemerisPoint> ASTEROID_101_CLOSE_APPROACHES =
      ImmutableList.of(
          EventEphemerisPoint.builder()
              .time(parseUtcAsJulian("2004-10-15T01:13:45Z"))
              .timeSystem(TimeSystem.UTC)
              .distanceType(DistanceType.RADIUS)
              .distanceUnits(DistanceUnits.METERS)
              .targetBody(JplDECentralBody.EARTH)
              .distanceFromTarget(6.066052262161629e9)
              .build(),
          EventEphemerisPoint.builder()
              .time(parseUtcAsJulian("2007-12-28T08:03:23Z"))
              .timeSystem(TimeSystem.UTC)
              .distanceType(DistanceType.RADIUS)
              .distanceUnits(DistanceUnits.METERS)
              .targetBody(JplDECentralBody.EARTH)
              .distanceFromTarget(1.4693312359680953e9)
              .build());

  @Before
  public void addLicense() {
    StkLicense.activate();
  }

  private static PropagationParameters setupPropagationParams(
      String startEpoch,
      String endEpoch,
      long propagationStepDurationSeconds,
      StateVector stateVector) {
    ZonedDateTime startDateZonedDateTime = ZonedDateTime.parse(startEpoch);
    ZonedDateTime endDateZonedDateTime = ZonedDateTime.parse(endEpoch);
    return setupPropagationParams(
        startDateZonedDateTime, endDateZonedDateTime, propagationStepDurationSeconds, stateVector);
  }

  private static PropagationParameters setupPropagationParams(
      ZonedDateTime startDate,
      ZonedDateTime endDate,
      long propagationStepDurationSeconds,
      StateVector stateVector) {
    PropagationParameters params = new PropagationParameters();
    params.setStart_time(startDate.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    params.setEnd_time(endDate.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    params.setStep_duration_sec(propagationStepDurationSeconds);
    params.setPropagator_uuid("test-propagator-id");
    params.setExecutor("testJplHorizonsMatching");
    // getOpm() hardcodes the center name to Sun and reference frame to ICRF
    params.setOpm(getOpm("testObject", stateVector));
    return params;
  }

  @Test
  public void testStkPropagation_propagate7Days_finalStateVectorMatchesExpected()
      throws AdamPropagationException {
    String epoch = "2017-10-04T00:00:00.000Z";
    // Test input data is from single_run_demo.
    // Initial state vector, in km and km/s:
    // [130347560.13690618, -74407287.6018632, -35247598.541470632,
    //  23.935241263310683, 27.146279819258538, 10.346605942591514]
    // Force model is "Sun and planets (9) and moon".
    StateVector stateVector =
        new StateVector()
            .setEpoch(epoch)
            .setX(130347560.13690618)
            .setY(-74407287.6018632)
            .setZ(-35247598.541470632)
            .setX_dot(23.935241263310683)
            .setY_dot(27.146279819258538)
            .setZ_dot(10.346605942591514);
    long propagationDurationDays = 7;
    ZonedDateTime startDate = ZonedDateTime.parse(epoch);
    ZonedDateTime endDate = startDate.plusDays(propagationDurationDays);
    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();
    PropagationParameters params = setupPropagationParams(startDate, endDate, 60 * 60, stateVector);
    Double[] expectedPosition =
        new Double[] {143947724.26470003, -57542002.25428, -28774871.060080003};
    Double[] expectedVelocity =
        new Double[] {21.02344057526, 28.549821594860003, 11.027405485920001};

    StkSegmentPropagator propagator = new StkSegmentPropagator();
    OrbitEphemerisMessage oem = propagator.propagate(params, config, "test-propagator");
    OemDataLine finalState = extractFinalState(oem);
    // final state vector (from STK, interpolated, in km and km/s) is:
    // [143947724.26470003, -57542002.25428, -28774871.060080003,
    //  21.02344057526, 28.549821594860003, 11.027405485920001]
    double[] finalPoint = finalState.getPoint();
    double[] actualPosition = new double[] {finalPoint[0], finalPoint[1], finalPoint[2]};
    double[] actualVelocity = new double[] {finalPoint[3], finalPoint[4], finalPoint[5]};

    assertThat(actualPosition)
        .usingTolerance(1.0e-4)
        .containsExactlyElementsIn(expectedPosition)
        .inOrder();
    assertThat(actualVelocity)
        .usingTolerance(1.0e-11)
        .containsExactlyElementsIn(expectedVelocity)
        .inOrder();
  }

  @Test
  public void testStkPropagation_propagate10Years_finalStateVectorMatchesExpected()
      throws AdamPropagationException {
    String startEpoch = ASTEROID_101_EPOCH;
    // 10 years from start, minus 30 days
    String endEpoch = "2010-12-01T01:13:46.620000Z";
    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();
    PropagationParameters params =
        setupPropagationParams(
            startEpoch, endEpoch, SECONDS_IN_DAY, ASTEROID_101_INITIAL_STATE_VECTOR);
    // final state vector (from STK, interpolated, in km and km/s) for the start/end dates:
    // [62739872.06, 117402282.4, 47549221.51,
    //  -31.06961002, 13.64009464, 7.070881807]
    Double[] expectedPosition = new Double[] {62739872.06, 117402282.4, 47549221.51};
    Double[] expectedVelocity = new Double[] {-31.06961002, 13.64009464, 7.070881807};

    StkSegmentPropagator propagator = new StkSegmentPropagator();
    OrbitEphemerisMessage oem = propagator.propagate(params, config, "test-propagator");
    OemDataLine finalState = extractFinalState(oem);
    double[] finalPoint = finalState.getPoint();
    double[] actualPosition = new double[] {finalPoint[0], finalPoint[1], finalPoint[2]};
    double[] actualVelocity = new double[] {finalPoint[3], finalPoint[4], finalPoint[5]};

    // TODO: figure out why tolerances are not so precise
    assertThat(actualPosition)
        .usingTolerance(1.0e4)
        .containsExactlyElementsIn(expectedPosition)
        .inOrder();
    assertThat(actualVelocity)
        .usingTolerance(1.0e-3)
        .containsExactlyElementsIn(expectedVelocity)
        .inOrder();
  }

  @Test
  public void testStkPropagation_stoppedOnImpact() throws AdamPropagationException {
    long propagationDurationYears = 10;
    ZonedDateTime startDate = ZonedDateTime.parse(ASTEROID_101_EPOCH);
    ZonedDateTime endDate = startDate.plusYears(propagationDurationYears);
    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();
    PropagationParameters params =
        setupPropagationParams(
            startDate, endDate, SECONDS_IN_DAY, ASTEROID_101_INITIAL_STATE_VECTOR);
    params.setStopOnImpact(true);
    params.setCloseApproachRadiusFromTargetMeters(7.0e9);
    params.setStopOnImpactAltitudeMeters(500_000); // 500km
    JulianDate expectedImpactDate = parseUtcAsJulian("2010-12-31T01:13:46.620000Z");

    StkSegmentPropagator propagator = new StkSegmentPropagator();
    propagator.propagate(params, config, "test-propagator");
    EventEphemerisPoint finalState = propagator.getFinalState();
    JulianDate actualImpactDate = finalState.getTime();

    // Check that actual and expected differ no more than 6 hours
    assertThat(finalState.getOrbitEventType()).isEqualTo(OrbitEventType.IMPACT);
    assertThat(finalState.getTargetBodyReferenceFrame())
        .isEqualTo(OdmCommonMetadata.ReferenceFrame.ECEF);
    assertThat(finalState.isStopped()).isTrue();
    assertThat(actualImpactDate.secondsDifference(expectedImpactDate)).isAtMost(21600);
    // Check that distance is within 1e3 meters of stopDistance, which is the stopping condition
    // distance for impact.
    assertThat(finalState.getDistanceFromTarget()).isWithin(1e3).of(500000);
    assertThat(finalState.getDistanceType()).isEqualTo(DistanceType.ALTITUDE);
    assertThat(finalState.getDistanceUnits()).isEqualTo(DistanceUnits.METERS);
  }

  @Test
  public void testStkPropagation_recordsCorrectCloseApproaches() throws AdamPropagationException {
    // 10 years from start, minus 30 days
    String endEpoch = "2010-12-01T01:13:46.620000Z";
    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();
    PropagationParameters params =
        setupPropagationParams(
            ASTEROID_101_EPOCH, endEpoch, SECONDS_IN_DAY, ASTEROID_101_INITIAL_STATE_VECTOR);
    params.setEnableLogCloseApproaches(true);
    params.setCloseApproachRadiusFromTargetMeters(7.0e9);
    EventEphemerisPoint expectedCloseApproach1 = ASTEROID_101_CLOSE_APPROACHES.get(0);
    EventEphemerisPoint expectedCloseApproach2 = ASTEROID_101_CLOSE_APPROACHES.get(1);

    StkSegmentPropagator propagator = new StkSegmentPropagator();
    propagator.propagate(params, config, "test-propagator");
    List<EventEphemerisPoint> actual = propagator.getCloseApproaches();
    EventEphemerisPoint actualCloseApproach1 = actual.get(0);
    EventEphemerisPoint actualCloseApproach2 = actual.get(1);

    // Check that the close approach date is within some time range
    assertThat(actualCloseApproach1.getTime().secondsDifference(expectedCloseApproach1.getTime()))
        .isAtMost(1);
    assertThat(actualCloseApproach2.getTime().secondsDifference(expectedCloseApproach2.getTime()))
        .isAtMost(1);
    // Check that the close approach distance from target is within some tolerance
    assertThat(actualCloseApproach1.getDistanceFromTarget())
        .isWithin(1e-7)
        .of(expectedCloseApproach1.getDistanceFromTarget());
    assertThat(actualCloseApproach2.getDistanceFromTarget())
        .isWithin(1e-7)
        .of(expectedCloseApproach2.getDistanceFromTarget());
  }

  @Test
  public void testStkPropagation_stoppedOnFirstCloseApproach() throws AdamPropagationException {
    // 10 years from start, minus 30 days
    String endEpoch = "2010-12-01T01:13:46.620000Z";
    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();
    PropagationParameters params =
        setupPropagationParams(
            ASTEROID_101_EPOCH, endEpoch, SECONDS_IN_DAY, ASTEROID_101_INITIAL_STATE_VECTOR);
    params.setEnableLogCloseApproaches(true);
    params.setStopOnCloseApproach(true);
    params.setCloseApproachRadiusFromTargetMeters(7.0e9);
    EventEphemerisPoint expectedCloseApproach1 = ASTEROID_101_CLOSE_APPROACHES.get(0);

    StkSegmentPropagator propagator = new StkSegmentPropagator();
    propagator.propagate(params, config, "test-propagator");
    List<EventEphemerisPoint> actual = propagator.getCloseApproaches();
    EventEphemerisPoint actualCloseApproach1 = actual.get(0);

    assertThat(actual).hasSize(1);
    // Check that the close approach date is within some time range
    assertThat(actualCloseApproach1.getTime().secondsDifference(expectedCloseApproach1.getTime()))
        .isAtMost(1);
    // Check that the close approach distance from target is within some tolerance
    assertThat(actualCloseApproach1.getDistanceFromTarget())
        .isWithin(1e-7)
        .of(expectedCloseApproach1.getDistanceFromTarget());
  }

  @Test
  public void testStkPropagation_stoppedOnCloseApproachAfterEpoch()
      throws AdamPropagationException {
    // 10 years from start, minus 30 days
    String endEpoch = "2010-12-01T01:13:46.620000Z";
    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();
    PropagationParameters params =
        setupPropagationParams(
            ASTEROID_101_EPOCH, endEpoch, SECONDS_IN_DAY, ASTEROID_101_INITIAL_STATE_VECTOR);
    params.setEnableLogCloseApproaches(true);
    params.setStopOnCloseApproach(true);
    params.setCloseApproachRadiusFromTargetMeters(7.0e9);
    params.setStopOnCloseApproachAfterEpoch("2004-10-16T01:13:45Z");
    // Since the first close approach is 2004-10-15 and the stop-after-epoch is 2004-10-16, expect
    // that the actual close approach matches the second close approach for Asteroid 101.
    EventEphemerisPoint expectedCloseApproach2 = ASTEROID_101_CLOSE_APPROACHES.get(1);

    StkSegmentPropagator propagator = new StkSegmentPropagator();
    propagator.propagate(params, config, "test-propagator");
    List<EventEphemerisPoint> actual = propagator.getCloseApproaches();
    EventEphemerisPoint actualCloseApproach1 = actual.get(0);

    assertThat(actual).hasSize(1);
    // Check that the close approach date is within some time range
    assertThat(actualCloseApproach1.getTime().secondsDifference(expectedCloseApproach2.getTime()))
        .isAtMost(1);
    // Check that the close approach distance from target is within some tolerance
    assertThat(actualCloseApproach1.getDistanceFromTarget())
        .isWithin(1e-7)
        .of(expectedCloseApproach2.getDistanceFromTarget());
  }

  @Test
  public void testStkPropagation_parametersDisableCloseApproachLogging_logsNoCloseApproaches()
      throws AdamPropagationException {
    // 10 years from start, minus 30 days
    String endEpoch = "2010-12-01T01:13:46.620000Z";
    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();
    PropagationParameters params =
        setupPropagationParams(
            ASTEROID_101_EPOCH, endEpoch, SECONDS_IN_DAY, ASTEROID_101_INITIAL_STATE_VECTOR);
    params.setEnableLogCloseApproaches(false);

    StkSegmentPropagator propagator = new StkSegmentPropagator();
    propagator.propagate(params, config, "test-propagator");
    List<EventEphemerisPoint> actual = propagator.getCloseApproaches();

    assertThat(actual).isEmpty();
  }

  // TODO: write more tests, eg testing backward propagation
}
