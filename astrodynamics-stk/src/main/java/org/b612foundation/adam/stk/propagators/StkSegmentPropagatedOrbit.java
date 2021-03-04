package org.b612foundation.adam.stk.propagators;

import agi.foundation.DateMotionCollection1;
import agi.foundation.EvaluatorGroup;
import agi.foundation.Motion1;
import agi.foundation.MotionEvaluator1;
import agi.foundation.celestial.CentralBodiesFacet;
import agi.foundation.celestial.EarthCentralBody;
import agi.foundation.celestial.JplDECentralBody;
import agi.foundation.celestial.WorldGeodeticSystem1984;
import agi.foundation.coordinates.*;
import agi.foundation.geometry.*;
import agi.foundation.numericalmethods.IntegrationSense;
import agi.foundation.numericalmethods.InterpolationAlgorithmType;
import agi.foundation.numericalmethods.NumericalIntegrator;
import agi.foundation.propagators.NumericalPropagatorDefinition;
import agi.foundation.propagators.PropagationNewtonianPoint;
import agi.foundation.segmentpropagation.*;
import agi.foundation.stk.StkEphemerisFile;
import agi.foundation.stoppingconditions.*;
import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;
import com.google.common.base.Preconditions;
import org.b612foundation.adam.common.DistanceType;
import org.b612foundation.adam.common.DistanceUnits;
import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.opm.*;
import org.b612foundation.adam.opm.OdmCommonMetadata.TimeSystem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.b612foundation.adam.propagators.OrbitPositionType;

import static agi.foundation.stoppingconditions.StoppingConditionTriggeredBehavior.CONTINUE_TO_NEXT_EVENT;
import static agi.foundation.stoppingconditions.StoppingConditionTriggeredBehavior.STOP_FUNCTION;
import static org.b612foundation.adam.astro.AstroConstants.M_TO_KM;
import static org.b612foundation.adam.stk.StkPropagationHelper.*;

/**
 * Sets up and propagates an orbit, given an {@link OrbitParameterMessage}, {@link
 * PropagatorConfiguration}, and the start and end dates for propagation.
 *
 * <p>This differs from {@link PropagatedInterplanetaryOrbit} in that this class keeps track of
 * impacts and close approaches. The orbit will propagate for some maximum duration before stopping,
 * if the impact and/or close approach stopping conditions don't trigger first.
 *
 * <p>This class is not intended to be used outside of this package, hence the package-private
 * visibility.
 */
class StkSegmentPropagatedOrbit extends PropagatedOrbit {
  private static final Logger log = Logger.getLogger(StkSegmentPropagatedOrbit.class.getName());
  private static final String POINT_OBJECT_ID = "propagatedOrbit";

  private final EvaluatorGroup evaluatorGroup = new EvaluatorGroup();
  private PointInterpolator interpolator;
  private List<JulianDate> rawDates = new ArrayList<>();
  private List<double[]> rawValues = new ArrayList<>();
  private boolean stoppedOnCloseApproach;
  private List<OrbitPointSummary> closeApproaches = new ArrayList<>();
  private Optional<OrbitPointSummary> impact = Optional.empty();
  private ReferenceFrame referenceFrame;
  private PropagationNewtonianPoint pointObject;
  private OrbitPointSummary finalPositionAndTime;

  private StkSegmentPropagatedOrbit() {}

  /**
   * Setup force model, initialize the orbit from {@link OrbitParameterMessage} and {@link
   * PropagatorConfiguration}.
   */
  static StkSegmentPropagatedOrbit initializeOrbit(
      OrbitParameterMessage opm, PropagatorConfiguration config) {
    ForceModelHelper.loadStandardObjects();
    StkSegmentPropagatedOrbit orbit = new StkSegmentPropagatedOrbit();
    orbit.setReferenceFrame(opm.getMetadata());
    orbit.pointObject =
        initializePropagationObjectWithReferenceFrame(opm, orbit.referenceFrame, POINT_OBJECT_ID);
    if (opm.getSpacecraft() != null) {
      orbit.pointObject.setMass(Scalar.toScalar(opm.getSpacecraft().getMass()));
    } else {
      orbit.pointObject.setMass(Scalar.toScalar(0));
    }
    initializeCentralBodyForces(orbit.pointObject, config, opm.getMetadata().getCenter_name());
    return orbit;
  }

  protected MotionEvaluator1<Cartesian> getEvaluator() {
    return interpolator.getEvaluator(evaluatorGroup);
  }

  public ReferenceFrame getReferenceFrame() {
    return interpolator.getReferenceFrame();
  }

  private void setReferenceFrame(OdmCommonMetadata metadata) {
    ReferenceFrameGenerator generator = new ReferenceFrameGenerator();
    referenceFrame =
        generator.getReferenceFrame(metadata.getRef_frame(), metadata.getCenter_name());
  }

  /**
   * Propagate with stopping conditions.
   *
   * <p>1. Create a {@link NumericalPropagatorSegment} to perform the propagation for a maximum
   * duration.
   *
   * <p>2. Create stopping conditions on impact and close approach.
   *
   * <p>A lot of this is taken from the example code:
   * https://help.agi.com/AGIComponentsJava/html/SegmentPropagationCodeSample.htm
   */
  void propagate(PropagationParameters propagationParams, JulianDate epoch, JulianDate endDate) {
    EarthCentralBody earth = CentralBodiesFacet.getFromContext().getEarth();

    SegmentList segmentList = new SegmentList();
    segmentList.setName("Segment list");

    NumericalPropagatorDefinition propagatorDefinition = getNumericalPropagator(pointObject, epoch);

    // Initial state
    NumericalInitialStateSegment initialStateSegment = new NumericalInitialStateSegment();
    initialStateSegment.setName("Initial State Segment");
    initialStateSegment.setPropagatorDefinition(propagatorDefinition);

    // Propagate for duration.
    NumericalPropagatorSegment propagateSegment = new NumericalPropagatorSegment();
    propagateSegment.setName("Propagate for duration");
    // Set the integration direction
    boolean increasing = JulianDate.lessThanOrEqual(epoch, endDate);
    NumericalIntegrator integrator = propagatorDefinition.getIntegrator();
    integrator.setDirection(increasing ? IntegrationSense.INCREASING : IntegrationSense.DECREASING);
    propagatorDefinition.setIntegrator(integrator);
    propagateSegment.setPropagatorDefinition(propagatorDefinition);
    Duration propagationTime = endDate.subtract(epoch);
    // Set the duration for propagation (for some reason, using DurationStoppingCondition for the
    // same propagationTime did not work and would terminate the propagation before the entire
    // duration has elapsed, for longer durations)
    propagateSegment.setMaximumDuration(propagationTime);

    // Create a stopping condition for close approach (perigee), if logging of close approaches is
    // enabled.
    // TODO: make this not Earth-specific?
    if (propagationParams.getEnableLogCloseApproaches()) {
      DelegateStoppingCondition perigeeStoppingCondition =
          buildPerigeeStoppingCondition(earth, propagationParams);
      propagateSegment.getStoppingConditions().add(perigeeStoppingCondition);
    }

    // Record impacts, some altitude above target body's surface.
    // TODO: user inputs either a radius (dist from coordinate system) OR altitude (distance from
    //   surface) stopping condition
    ScalarStoppingCondition altitudeStoppingCondition =
        buildImpactAltitudeFromEarthStoppingCondition(earth, propagationParams);
    propagateSegment.getStoppingConditions().add(altitudeStoppingCondition);

    // Add segments
    segmentList.getSegments().add(initialStateSegment);
    segmentList.getSegments().add(propagateSegment);

    // Actually do the propagation
    SegmentPropagator propagator = segmentList.getSegmentPropagator(new EvaluatorGroup());
    log.info(
        "Propagating in direction "
            + propagator.getPropagationDirection().toString()
            + " from "
            + epoch.toString()
            + " to "
            + endDate.toString());
    SegmentListResults segmentResults = (SegmentListResults) propagator.propagate();

    // Recover the position and velocity. OPM does not include acceleration data, so we won't get
    // it here.
    DateMotionCollection1<Cartesian> ephemeris =
        segmentResults.getDateMotionCollectionOfOverallTrajectory(
            POINT_OBJECT_ID, pointObject.getIntegrationFrame());
    interpolator =
        new PointInterpolator(
            pointObject.getIntegrationFrame(), InterpolationAlgorithmType.HERMITE, 6, ephemeris);
    List<ITimeBasedState> ephemOverallTrajectory =
        segmentResults.getEphemerisForOverallTrajectory();
    for (ITimeBasedState state : ephemOverallTrajectory) {
      rawDates.add(state.getCurrentDate());
      Cartesian pos = (Cartesian) state.getMotion(POINT_OBJECT_ID).getValue();
      Cartesian vel = (Cartesian) state.getMotion(POINT_OBJECT_ID).getFirstDerivative();
      rawValues.add(cartesianToArray(pos));
    }
    finalPositionAndTime =
        buildFinalState(
            earth, ephemOverallTrajectory.get(ephemOverallTrajectory.size() - 1), referenceFrame);
  }

  /**
   * Build the final state in a value class, {@link OrbitPointSummary}.
   *
   * <p>Final state cases:
   *
   * <ul>
   *   <li>Impact: return the impact as the final state. The {@link Optional} impact is set in
   *       {@link #buildImpactAltitudeFromEarthStoppingCondition(EarthCentralBody,
   *       PropagationParameters)}.
   *   <li>Stopped on a close approach: if user turned on the stopOnCloseApproach flag and the
   *       propagation stopped on a close approach, return the last close approach in the list of
   *       detected intervening close approaches. Close approaches are logged in {@link
   *       #buildPerigeeStoppingCondition(EarthCentralBody, PropagationParameters)}.
   *   <li>Otherwise, return a Miss.
   * </ul>
   *
   * // TODO: make not Earth-specific
   */
  private OrbitPointSummary buildFinalState(
      EarthCentralBody earth, ITimeBasedState state, ReferenceFrame referenceFrame) {
    if (impact.isPresent()) {
      return impact.get();
    } else if (stoppedOnCloseApproach) {
      List<OrbitPointSummary> closeApproaches = getCloseApproaches();
      return closeApproaches.get(closeApproaches.size() - 1);
    }

    PointEvaluator earthEvaluator =
        GeometryTransformer.observePoint(earth.getCenterOfMassPoint(), referenceFrame);
    Cartesian pos = (Cartesian) state.getMotion(POINT_OBJECT_ID).getValue();
    Cartesian earthPos = earthEvaluator.evaluate(state.getCurrentDate(), 2).getValue();
    Cartesian relPos = pos.subtract(earthPos);
    double distanceFromTarget = relPos.getMagnitude();

    return OrbitPointSummary.builder()
        .orbitPositionType(OrbitPositionType.MISS)
        .stopped(true)
        .time(state.getCurrentDate())
        .timeIsoFormat(TimeHelper.toIsoFormat(state.getCurrentDate()))
        .timeSystem(TimeSystem.UTC)
        .targetBody(JplDECentralBody.EARTH)
        .targetBodyCenteredPositionUnits(DistanceUnits.METERS)
        .targetBodyCenteredPosition(cartesianToArray(relPos))
        .targetBodyReferenceFrame(OdmCommonMetadata.ReferenceFrame.ICRF)
        .distanceFromTarget(distanceFromTarget)
        .distanceUnits(DistanceUnits.METERS)
        .distanceType(DistanceType.RADIUS)
        .build();
  }

  OrbitEphemerisMessage exportOrbitEphemerisMessage(
      JulianDate startDate, JulianDate stopDate, Duration timeStep) {
    StkEphemerisFile ephemeris = getEphemeris(startDate, stopDate, timeStep);
    StkEphemerisFile.EphemerisTimePosVel posVelData =
        (StkEphemerisFile.EphemerisTimePosVel) ephemeris.getData();
    int numberEphemPoints = posVelData.getEphemerisData().getCount();
    List<JulianDate> dates = new ArrayList<>();
    List<double[]> posVel = new ArrayList<>();
    for (int i = 0; i < numberEphemPoints; i++) {
      JulianDate date = posVelData.getEphemerisData().getDates().get(i);
      Cartesian position = posVelData.getEphemerisData().getValues().get(i);
      Cartesian velocity = posVelData.getEphemerisData().getFirstDerivatives().get(i);
      dates.add(date);
      posVel.add(
          new double[] {
            position.getX(),
            position.getY(),
            position.getZ(),
            velocity.getX(),
            velocity.getY(),
            velocity.getZ()
          });
    }

    return exportOrbitEphemerisMessage(dates, posVel);
  }

  OrbitEphemerisMessage exportOrbitEphemerisMessageFromRawValues() {
    return exportOrbitEphemerisMessage(rawDates, rawValues);
  }

  // TODO: Maybe this should be made common
  private OrbitEphemerisMessage exportOrbitEphemerisMessage(
      List<JulianDate> dates, List<double[]> posVelValues) {
    int last = validateDatesAndReturnValidSize(dates);
    OdmCommonHeader header = new OdmCommonHeader();
    header.setCreation_date(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    header.setOriginator("ADAM AGI-components based propagator");

    String startDateString =
        dates.get(0).toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString();
    String stopDateString =
        dates.get(last).toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString();
    OemMetadata metadata = new OemMetadata();
    metadata.setInterpolation("HERMITE");
    metadata.setInterpolation_degree(5);
    metadata.setStart_time(startDateString);
    metadata.setUsable_start_time(metadata.getStart_time());
    metadata.setStop_time(stopDateString);
    metadata.setUsable_stop_time(metadata.getStop_time());
    metadata.setObject_id(POINT_OBJECT_ID);
    metadata.setObject_name(POINT_OBJECT_ID);
    metadata.setTime_system(OdmCommonMetadata.TimeSystem.UTC);
    metadata.setCenter_name(OdmCommonMetadata.CenterName.SUN);
    metadata.setRef_frame(OdmCommonMetadata.ReferenceFrame.ICRF);

    OemDataBlock block = new OemDataBlock();
    block.getComments().add("ADAM AGI-components based propagation");
    block.setMetadata(metadata);

    for (int i = 0; i <= last; i++) {
      String lineDateString =
          dates.get(i).toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString();
      double[] posVel = posVelValues.get(i);
      block.addLine(
          lineDateString,
          posVel[0] * M_TO_KM,
          posVel[1] * M_TO_KM,
          posVel[2] * M_TO_KM,
          posVel[3] * M_TO_KM,
          posVel[4] * M_TO_KM,
          posVel[5] * M_TO_KM);
    }

    OrbitEphemerisMessage oem = new OrbitEphemerisMessage();
    oem.setHeader(header);
    oem.addBlock(block);

    return oem;
  }

  // TODO: make this common?
  private static double[] motionToCartesianArray(Motion1 motion) {
    Cartesian pos = (Cartesian) motion.getValue();
    Cartesian vel = (Cartesian) motion.getFirstDerivative();
    return new double[] {pos.getX(), pos.getY(), pos.getZ(), vel.getX(), vel.getY(), vel.getZ()};
  }

  private static double[] cartesianToArray(Cartesian cartesian) {
    return new double[] {cartesian.getX(), cartesian.getY(), cartesian.getZ()};
  }

  private static int validateDatesAndReturnValidSize(List<JulianDate> dates) {
    Preconditions.checkState(!dates.isEmpty());
    // Data. We are pulling previous values in the callback, not current, so the last element may
    // get duplicated.
    int last = dates.size() - 1;
    if (last > 1 && dates.get(last).equals(dates.get(last - 1))) {
      last--;
    }

    return last;
  }

  /** Returns the list of dates corresponding to integrator steps, not interpolated. */
  public List<JulianDate> getRawDates() {
    return rawDates;
  }

  /**
   * Returns the list of raw values from integrator steps, not interpolated. Each 6-value array
   * contains position followed by velocity.
   */
  public List<double[]> getRawValues() {
    return rawValues;
  }

  public boolean getStoppedOnCloseApproach() {
    return stoppedOnCloseApproach;
  }

  public List<OrbitPointSummary> getCloseApproaches() {
    return closeApproaches;
  }

  public Optional<OrbitPointSummary> getImpact() {
    return impact;
  }

  public OrbitPointSummary getFinalPositionAndTime() {
    return finalPositionAndTime;
  }

  /**
   * Builds a {@link DelegateStoppingCondition} for perigee.
   *
   * <p>When the stopping condition is triggered (when perigee is reached), the close approach will
   * be added to a list of {@link OrbitPointSummary} and either stop, if user has opted to, or
   * continue the propagation and keep adding the close approaches to the list. The user can also
   * specify an additional constraint on the stopping condition, that is, to stop the propagation
   * only after a particular epoch.
   *
   * <p>The user will provide their input via {@link PropagationParameters}.
   *
   * <p>TODO: make this not Earth-specific.
   */
  private DelegateStoppingCondition buildPerigeeStoppingCondition(
      EarthCentralBody earth, PropagationParameters propagationParams) {
    PointEvaluator earthEvaluator =
        GeometryTransformer.observePoint(earth.getCenterOfMassPoint(), referenceFrame);
    DelegateStoppingCondition stoppingCondition =
        new DelegateStoppingCondition(
            ConditionCheckCallback.of(
                currentState -> {
                  Cartesian pos = (Cartesian) currentState.getMotion(POINT_OBJECT_ID).getValue();
                  Cartesian vel =
                      (Cartesian) currentState.getMotion(POINT_OBJECT_ID).getFirstDerivative();
                  Motion1<Cartesian> earthPosAndVel =
                      earthEvaluator.evaluate(currentState.getCurrentDate(), 2);
                  Cartesian earthPos = earthPosAndVel.getValue();
                  Cartesian earthVel = earthPosAndVel.getFirstDerivative();
                  Cartesian diffPos = pos.subtract(earthPos);
                  Cartesian diffVel = vel.subtract(earthVel);

                  UnitCartesian unitPos = diffPos.normalize();
                  UnitCartesian unitVel = diffVel.normalize();

                  return unitPos.dot(unitVel);
                }),
            0.0,
            1e-7,
            StopType.THRESHOLD_INCREASING);

    stoppingCondition.setName("Perigee stopping condition");
    // When stopping condition is satisfied, record the close approach and other information.
    stoppingCondition.setSatisfiedCallback(
        StoppingTriggeredCallback.of(
            currentState -> {
              Cartesian position = (Cartesian) currentState.getMotion(POINT_OBJECT_ID).getValue();
              Cartesian earthPos =
                  earthEvaluator.evaluate(currentState.getCurrentDate(), 2).getValue();
              Cartesian relPos = position.subtract(earthPos);
              double distanceFromTarget = relPos.getMagnitude();

              OrbitPointSummary closeApproach =
                  OrbitPointSummary.builder()
                      .orbitPositionType(OrbitPositionType.CLOSE_APPROACH)
                      .stopped(propagationParams.getStopOnCloseApproach())
                      .time(currentState.getCurrentDate())
                      .timeIsoFormat(TimeHelper.toIsoFormat(currentState.getCurrentDate()))
                      .timeSystem(TimeSystem.UTC)
                      .targetBody(JplDECentralBody.EARTH)
                      .targetBodyCenteredPosition(cartesianToArray(relPos))
                      .targetBodyCenteredPositionUnits(DistanceUnits.METERS)
                      .targetBodyReferenceFrame(OdmCommonMetadata.ReferenceFrame.ICRF)
                      .distanceFromTarget(distanceFromTarget)
                      .distanceType(DistanceType.RADIUS)
                      .distanceUnits(DistanceUnits.METERS)
                      .build();
              closeApproaches.add(closeApproach);
              if (propagationParams.getStopOnCloseApproach()) {
                stoppedOnCloseApproach = true;
                return STOP_FUNCTION;
              }
              return CONTINUE_TO_NEXT_EVENT;
            }));

    // Set the stopping condition constraint: after a user-specified epoch.
    if (propagationParams.getStopOnCloseApproach()
        && propagationParams.getStopOnCloseApproachAfterEpoch() != null
        && !propagationParams.getStopOnCloseApproachAfterEpoch().isEmpty()) {
      JulianDate stopAfterEpoch =
          parseUtcAsJulian(propagationParams.getStopOnCloseApproachAfterEpoch());
      DelegateStoppingConditionConstraint closeApproachStopAfterEpochConstraint =
          new DelegateStoppingConditionConstraint(
              ConstraintSatisfiedCallback.of(
                  currentState ->
                      JulianDate.greaterThanOrEqual(currentState.getCurrentDate(), stopAfterEpoch)),
              WhenToCheckConstraint.WHEN_EVENT_IS_DETECTED);
      stoppingCondition.getConstraints().add(closeApproachStopAfterEpochConstraint);
    }

    if (propagationParams.getCloseApproachRadiusFromTargetMeters() > 0.0) {
      DelegateStoppingConditionConstraint closeApproachDistanceConstraint =
          new DelegateStoppingConditionConstraint(
              ConstraintSatisfiedCallback.of(
                  currentState -> {
                    Cartesian position =
                        (Cartesian) currentState.getMotion(POINT_OBJECT_ID).getValue();
                    Cartesian earthPos =
                        earthEvaluator.evaluate(currentState.getCurrentDate(), 2).getValue();
                    double distanceFromTarget = position.subtract(earthPos).getMagnitude();

                    return distanceFromTarget
                        <= propagationParams.getCloseApproachRadiusFromTargetMeters();
                  }),
              WhenToCheckConstraint.WHEN_EVENT_IS_DETECTED);
      stoppingCondition.getConstraints().add(closeApproachDistanceConstraint);
    }

    return stoppingCondition;
  }

  /**
   * Builds the impact {@link ScalarStoppingCondition} when object reaches some altitude above
   * Earth's surface.
   *
   * <p>The actual distance will be provided via {@link
   * PropagationParameters#getStopOnImpactAltitudeMeters()}.
   *
   * <p>This might be more accurate than we need, but revisit later. We might just be happy with a
   * certain distance from the Earth.
   *
   * <p>TODO: make this not Earth-specific
   */
  private ScalarStoppingCondition buildImpactAltitudeFromEarthStoppingCondition(
      EarthCentralBody earth, PropagationParameters propagationParams) {
    ScalarSphericalElement distanceFromEarth =
        new ScalarSphericalElement(
            pointObject.getIntegrationPoint(),
            earth.getInternationalCelestialReferenceFrame(),
            SphericalElement.MAGNITUDE);
    ScalarStoppingCondition distanceFromEarthStoppingCondition =
        new ScalarStoppingCondition(
            distanceFromEarth,
            propagationParams.getStopOnImpactAltitudeMeters()
                + WorldGeodeticSystem1984.SemimajorAxis,
            1e3 /* 1 km tolerance */,
            StopType.ANY_THRESHOLD);
    distanceFromEarthStoppingCondition.setName("Distance from earth stopping condition");
    distanceFromEarthStoppingCondition.setSatisfiedCallback(
        StoppingTriggeredCallback.of(
            currentState -> {
              PointEvaluator earthEvaluator =
                  GeometryTransformer.observePoint(earth.getCenterOfMassPoint(), referenceFrame);
              Cartesian position = (Cartesian) currentState.getMotion(POINT_OBJECT_ID).getValue();
              Cartesian earthPos =
                  earthEvaluator.evaluate(currentState.getCurrentDate(), 2).getValue();
              Cartesian relPosInertial = position.subtract(earthPos);
              double distanceFromTarget = relPosInertial.getMagnitude();
              ReferenceFrameEvaluator frameEvaluator =
                  GeometryTransformer.getReferenceFrameTransformation(
                      earth.getInertialFrame(), earth.getFixedFrame());
              KinematicTransformation frameTransform =
                  frameEvaluator.evaluate(currentState.getCurrentDate());
              Cartesian relPosFixed = frameTransform.transform(relPosInertial);
              impact =
                  Optional.of(
                      OrbitPointSummary.builder()
                          .orbitPositionType(OrbitPositionType.IMPACT)
                          .stopped(propagationParams.getStopOnImpact())
                          .time(currentState.getCurrentDate())
                          .timeIsoFormat(TimeHelper.toIsoFormat(currentState.getCurrentDate()))
                          .timeSystem(TimeSystem.UTC)
                          .targetBody(JplDECentralBody.EARTH)
                          .targetBodyCenteredPosition(cartesianToArray(relPosFixed))
                          .targetBodyCenteredPositionUnits(DistanceUnits.METERS)
                          .targetBodyReferenceFrame(OdmCommonMetadata.ReferenceFrame.ECEF)
                          .distanceFromTarget(distanceFromTarget)
                          .distanceType(DistanceType.ALTITUDE)
                          .distanceUnits(DistanceUnits.METERS)
                          .build());
              if (propagationParams.getStopOnImpact()) {
                return STOP_FUNCTION;
              }

              return CONTINUE_TO_NEXT_EVENT;
            }));
    return distanceFromEarthStoppingCondition;
  }
}
