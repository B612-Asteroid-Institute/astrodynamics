package org.b612foundation.adam.stk.propagators;

import agi.foundation.DateMotionCollection1;
import agi.foundation.EvaluatorGroup;
import agi.foundation.Motion1;
import agi.foundation.celestial.*;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.coordinates.ITimeBasedState;
import agi.foundation.coordinates.SphericalElement;
import agi.foundation.geometry.*;
import agi.foundation.numericalmethods.*;
import agi.foundation.propagators.NumericalPropagatorDefinition;
import agi.foundation.propagators.PropagationNewtonianPoint;
import agi.foundation.segmentpropagation.*;
import agi.foundation.stk.StkEphemerisFile;
import agi.foundation.stk.StkEphemerisFile.StkTimeFormat;
import agi.foundation.stoppingconditions.*;
import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.datamodel.TargetingParameters;
import org.b612foundation.adam.exceptions.AdamPropagationException;
import org.b612foundation.stk.StkLicense;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;

public class DistanceFromEarthTargeter {
  private static Logger log = Logger.getLogger(DistanceFromEarthTargeter.class.getName());

  private String motionId = "motionId";
  private TargetedSegmentList targetedSegmentList; // Will be non-null after initialize.
  private SegmentResults segmentResults; // Will be non-null after propagate.
  private ReferenceFrame referenceFrame;

  // Maximum number of iterations for the differential correctors.
  private int dcMaxIterations = 20;

  public DistanceFromEarthTargeter() throws IOException {
    StkLicense.activate();
    ForceModelHelper.loadStandardObjects();
  }

  /**
   * Mainly for testing, as I expect that we would have a decent idea of how many iterations is
   * reasonable.
   */
  protected void setMaxIterations(int maxIterations) {
    dcMaxIterations = maxIterations;
  }

  private <T> T last(List<T> list) {
    return list.get(list.size() - 1);
  }

  /**
   * Configures but does not run a targeting sequence that will compute the initial maneuver
   * required to propagate an object with the given initial state/epoch to a location that is the
   * given distance from the center of the earth (within the given tolerance) when the object
   * reaches its first perigee after the given duration past the initial epoch.
   *
   * @param initialState Initial position and velocity.
   * @param referenceFrame Reference frame to use.
   * @param epoch Time of initial state.
   * @param initialManeuver Initial maneuver to use. Assumed to be in object velocity orbit normal
   *     reference frame. (velocity = x, y = orbit normal, z = x cross y).
   * @param durationToNearEarth A slight underestimate of the time it will take to get to the target
   *     perigee. The really important thing is to set this to a duration that will get the object
   *     past the previous perigee but not yet to the target perigee. Beyond that, the closer the
   *     better for performance reasons (and for avoiding unexpectedly detected perigees). 30 days
   *     before perigee is a decent rule of thumb.
   * @param targetingParams Configuration for the targeting, including:
   *     <p>[targetDistanceFromEarth, km]: Distance from earth to be targeted.
   *     <p>[initialTargetDistanceFromEarth, km]: Distance from earth to be targeted during the 1st
   *     stage of the targeter.
   *     <p>[tolerance, km] Allowed tolerance on the target distance.
   *     <p>[runNominalOnly]: Whether to run the nominal sequence only (alternative: run targeted
   *     sequence).
   * @param propagatorConfig Force model configuration for the targeter.
   * @throws IllegalArgumentException
   */
  // Can we avoid making the user specify the duration? I don't see how, but it's a little janky.
  public void initialize(
      Motion1<Cartesian> initialState,
      ReferenceFrame referenceFrame,
      JulianDate epoch,
      Cartesian initialManeuver,
      Duration durationToNearEarth,
      TargetingParameters targetingParams,
      PropagatorConfiguration propagatorConfig)
      throws IllegalArgumentException {
    this.referenceFrame = referenceFrame;

    SunCentralBody sun = CentralBodiesFacet.getFromContext().getSun();
    EarthCentralBody earth = CentralBodiesFacet.getFromContext().getEarth();

    double targetDistanceFromEarth =
        targetingParams.getTargetDistanceFromEarth() * 1000.0; // km to m
    if (targetDistanceFromEarth <= 0) {
      log.warning("Target Distance from Earth is <= 0");
      throw new IllegalArgumentException("Target Distance from Earth must be > 0.");
    }

    double initialTargetDistanceFromEarth =
        targetingParams.getInitialTargetDistanceFromEarth() * 1000.0; // km to m
    if (initialTargetDistanceFromEarth <= 0) {
      initialTargetDistanceFromEarth = 5.0 * targetDistanceFromEarth;
      log.warning(
          "Initial Target Distance from Earth is <= 0; Setting to 5 x targetDistanceFromEarth = "
              + String.format("%f", initialTargetDistanceFromEarth)
              + " meters");
      // TODO: Add information to summary
    }

    double tolerance = targetingParams.getTolerance() * 1000.0; // km to m
    boolean runNominalOnly = targetingParams.isRunNominalOnly();

    // Create the propagation point and propagator.
    PropagationNewtonianPoint propagationPoint =
        new PropagationNewtonianPoint(
            motionId, referenceFrame, initialState.getValue(), initialState.getFirstDerivative());
    // STK barfs if no mass is specified, but it's not actually used in the computations so 0 is a
    // fine dummy value.
    propagationPoint.setMass(Scalar.toScalar(0));
    NumericalPropagatorDefinition propagator =
        getPropagator(propagatorConfig, propagationPoint, epoch);

    // Now, set up the segments defining the trajectory.

    // Initial state segment.
    NumericalInitialStateSegment initialStateSegment = new NumericalInitialStateSegment();
    initialStateSegment.setName("Initial State Segment");
    initialStateSegment.setPropagatorDefinition(propagator);

    // Maneuver segment. Uses axes relative to the object's velocity vector. X maneuvers are along
    // the velocity vector.
    // Y maneuvers are orbit-normal. Z maneuvers are along X cross Y.
    ImpulsiveManeuverSegment maneuverSegment = new ImpulsiveManeuverSegment();
    maneuverSegment.setName("Delta-V Maneuver Segment");
    // Maneuver starts at zero. Will be modified during targeting.
    ImpulsiveManeuverInformation applyDeltaVDetails =
        new ImpulsiveManeuverInformation(motionId, initialManeuver);
    applyDeltaVDetails.setOrientation(
        new AxesVelocityOrbitNormal(applyDeltaVDetails.getPropagationPoint(), sun));
    maneuverSegment.getManeuvers().add(applyDeltaVDetails);

    // Propagate for duration, to get object near earth.
    NumericalPropagatorSegment propagateForDurationSegment = new NumericalPropagatorSegment();
    propagateForDurationSegment.setName("Propagate for duration");
    propagateForDurationSegment.setPropagatorDefinition(propagator);
    DurationStoppingCondition durationStoppingCondition =
        new DurationStoppingCondition(durationToNearEarth);
    durationStoppingCondition.setName("Duration");
    propagateForDurationSegment.getStoppingConditions().add(durationStoppingCondition);
    // It is necessary to set maximum duration because there is a default maximum duration that is
    // generally much too short for our purposes. Null pointer exceptions will result if this is not
    // set to a duration longer than the stopping condition duration.
    propagateForDurationSegment.setMaximumDuration(durationToNearEarth.addSeconds(1));
    propagateForDurationSegment.setStopOnMaximumDurationBehavior(MaximumDurationBehavior.THROW);

    // Now propagate to perigee.
    NumericalPropagatorSegment propagateToPerigeeSegment = new NumericalPropagatorSegment();
    propagateToPerigeeSegment.setName("Propagate to perigee");
    propagateToPerigeeSegment.setPropagatorDefinition(propagator);
    PointEvaluator earthEvaluator =
        GeometryTransformer.observePoint(earth.getCenterOfMassPoint(), referenceFrame);
    // TODO: fixes to make the perigee stopping condition more accurate (see the 2 TODOs below)
    DelegateStoppingCondition perigeeStoppingCondition =
        new DelegateStoppingCondition(
            ConditionCheckCallback.of(
                currentState -> {
                  Cartesian pos = (Cartesian) currentState.getMotion(motionId).getValue();
                  Cartesian vel = (Cartesian) currentState.getMotion(motionId).getFirstDerivative();
                  Motion1<Cartesian> earthPosAndVel =
                      earthEvaluator.evaluate(currentState.getCurrentDate(), 2);
                  Cartesian earthPos = earthPosAndVel.getValue();
                  Cartesian earthVel = earthPosAndVel.getFirstDerivative();
                  Cartesian diffPos = pos.subtract(earthPos);
                  Cartesian diffVel = vel.subtract(earthVel);
                  // TODO: normalize the vectors and take the dot product
                  return diffPos.dot(diffVel) / (diffPos.getMagnitude() * diffVel.getMagnitude());
                }),
            0.0,
            // TODO: change the tolerance to something lower, e.g. 1e-7
            0.01,
            StopType.THRESHOLD_INCREASING);
    perigeeStoppingCondition.setName("Perigee");

    // We also need an altitude stopping condition because for some reason the b-plane targeter
    // completely chokes (returns NaN) if run inside the earth.
    ScalarSphericalElement distanceFromEarth =
        new ScalarSphericalElement(
            propagationPoint.getIntegrationPoint(),
            earth.getInternationalCelestialReferenceFrame(),
            SphericalElement.MAGNITUDE);
    ScalarStoppingCondition distanceFromEarthStoppingCondition =
        new ScalarStoppingCondition(
            distanceFromEarth,
            1e6 /* 1000 km threshold */ + WorldGeodeticSystem1984.SemimajorAxis,
            1e3 /* 1 km tolerance */,
            StopType.ANY_THRESHOLD);
    distanceFromEarthStoppingCondition.setName("Distance_from_Earth_condition");

    propagateToPerigeeSegment.getStoppingConditions().add(distanceFromEarthStoppingCondition);
    propagateToPerigeeSegment.getStoppingConditions().add(perigeeStoppingCondition);
    propagateToPerigeeSegment.setMaximumDuration(new Duration().addDays(365));

    // Then, set up the targeters.
    ReferenceFrame targetingFrame = earth.getInternationalCelestialReferenceFrame();

    // First, target the b-plane. This is a fast thing to target that will get us pretty close.
    TargetedSegmentListDifferentialCorrector bPlaneDifferentialCorrector =
        new TargetedSegmentListDifferentialCorrector();
    bPlaneDifferentialCorrector.setName("B Plane Differential Corrector");
    bPlaneDifferentialCorrector.setSolver(new NewtonRaphsonMultivariableFunctionSolver());

    // Constraint to target the initial target miss distance from Earth's center using the b-plane
    // to guide us.
    double earthG = ForceModelHelper.JPL_DE.getGravitationalParameter(JplDECentralBody.EARTH);
    DelegateBasedConstraint bMagConstraint =
        new DelegateBasedConstraint(
            DelegateBasedConstraintCallback.of(
                segmentResults -> {
                  DateMotionCollection1<Cartesian> data =
                      segmentResults.getDateMotionCollectionOfOverallTrajectory(
                          motionId, targetingFrame);
                  int order = 6;
                  Point point =
                      new PointInterpolator(
                          targetingFrame, InterpolationAlgorithmType.HERMITE, order, data);

                  Scalar bDotTScalar1 =
                      new VectorBPlane(point, earth, earthG, targetingFrame).getBDotT();
                  Scalar bDotRScalar1 =
                      new VectorBPlane(point, earth, earthG, targetingFrame).getBDotR();
                  JulianDate last = last(data.getDates());
                  Double bDotT = bDotTScalar1.getEvaluator().evaluate(last);
                  Double bDotR = bDotRScalar1.getEvaluator().evaluate(last);
                  double bMag = Math.sqrt(bDotT * bDotT + bDotR * bDotR);
                  return bMag;
                }),
            propagateToPerigeeSegment,
            initialTargetDistanceFromEarth,
            tolerance);

    bPlaneDifferentialCorrector
        .getVariables()
        .add(getManeuverXVariable(maneuverSegment, 10.0, .0001));
    bPlaneDifferentialCorrector.getConstraints().add(bMagConstraint);

    // Then, target the actual distance from earth.
    TargetedSegmentListDifferentialCorrector rMagDifferentialCorrector =
        new TargetedSegmentListDifferentialCorrector();
    rMagDifferentialCorrector.setName("R Magnitude Differential Corrector");
    rMagDifferentialCorrector.setSolver(new NewtonRaphsonMultivariableFunctionSolver());

    // Constraints to target the intended distance from Earth's center using actual distance.
    DelegateBasedConstraint rMagConstraint1 =
        new DelegateBasedConstraint(
            DelegateBasedConstraintCallback.of(
                segmentResults -> {
                  DateMotionCollection1<Cartesian> data =
                      segmentResults.getDateMotionCollectionOfOverallTrajectory(
                          motionId, targetingFrame);
                  Cartesian lastValue = last(data.getValues());

                  // The center of an earth-centered reference frame is the center of the earth, so
                  // the magnitude of a coordinate in this frame is equivalent to the distance from
                  // the center of the earth.
                  return lastValue.getMagnitude();
                }),
            propagateToPerigeeSegment,
            targetDistanceFromEarth,
            tolerance);

    rMagDifferentialCorrector.getVariables().add(getManeuverXVariable(maneuverSegment, 1.0, .0001));
    rMagDifferentialCorrector.getConstraints().add(rMagConstraint1);

    // Set some extra knobs on the differential correctors.
    bPlaneDifferentialCorrector.setMaximumIterations(dcMaxIterations);
    rMagDifferentialCorrector.setMaximumIterations(dcMaxIterations);
    bPlaneDifferentialCorrector.getSolver().setMultithreaded(false);
    rMagDifferentialCorrector.getSolver().setMultithreaded(false);

    // Finally, add all of these to a targeting sequence that can be used to actually carry out the
    // computation.
    targetedSegmentList = new TargetedSegmentList();
    if (runNominalOnly) {
      targetedSegmentList.setOperatorAction(
          TargetedSegmentListOperatorBehavior.RUN_NOMINAL_SEQUENCE);
    } else {
      targetedSegmentList.setOperatorAction(
          TargetedSegmentListOperatorBehavior.RUN_ACTIVE_OPERATORS);
    }
    targetedSegmentList.getSegments().add(initialStateSegment);
    targetedSegmentList.getSegments().add(maneuverSegment);
    targetedSegmentList.getSegments().add(propagateForDurationSegment);
    targetedSegmentList.getSegments().add(propagateToPerigeeSegment);
    targetedSegmentList.getOperators().add(bPlaneDifferentialCorrector);
    targetedSegmentList.getOperators().add(rMagDifferentialCorrector);
  }

  /**
   * Runs the targeter nominal sequence to determine whether the object already falls outside the
   * target distance from earth.
   *
   * <p>Note: Calling getEphemeris after this without any other intermediate method calls on this
   * object will return the nominal ephemeris.
   */
  public boolean runNominalPropagationToCheckRequiresManeuver(TargetingParameters targetingParams)
      throws AdamPropagationException {
    double targetDistanceFromEarth =
        targetingParams.getTargetDistanceFromEarth() * 1000.0; // km to m
    double tolerance = targetingParams.getTolerance() * 1000.0; // km to m

    // Run the nominal sequence to get the trajectory without a maneuver.
    targetedSegmentList.setOperatorAction(TargetedSegmentListOperatorBehavior.RUN_NOMINAL_SEQUENCE);
    SegmentPropagator propagator = targetedSegmentList.getSegmentPropagator(new EvaluatorGroup());
    segmentResults = propagator.propagate();

    // Restore the settings of the segment list.
    if (targetingParams.isRunNominalOnly()) {
      targetedSegmentList.setOperatorAction(
          TargetedSegmentListOperatorBehavior.RUN_NOMINAL_SEQUENCE);
    } else {
      targetedSegmentList.setOperatorAction(
          TargetedSegmentListOperatorBehavior.RUN_ACTIVE_OPERATORS);
    }

    if (segmentResults instanceof TargetedSegmentListResults) {
      // Compute the distance from earth. If it is already farther than the innermost boundary of
      // tolerable distance, no
      // maneuver is required.
      TargetedSegmentListResults targetedSegmentListResults =
          (TargetedSegmentListResults) segmentResults;
      ITimeBasedState currentState = targetedSegmentListResults.getFinalPropagatedState();
      Cartesian pos = (Cartesian) currentState.getMotion(motionId).getValue();

      PointEvaluator earthEvaluator =
          GeometryTransformer.observePoint(
              CentralBodiesFacet.getFromContext().getEarth().getCenterOfMassPoint(),
              referenceFrame);
      Cartesian earthPos = earthEvaluator.evaluate(currentState.getCurrentDate(), 2).getValue();

      double distanceFromEarth = pos.subtract(earthPos).getMagnitude();

      return distanceFromEarth < targetDistanceFromEarth - tolerance;
    }

    log.warning("Targeted segment list did not produce TargetedSegmentListResults.");
    throw new AdamPropagationException(
        "Unexpected result type while checking whether maneuver is required", null);
  }

  /**
   * Runs the targeted propagator, with or without targeting according to how it was initialized.
   */
  public void propagate() {
    SegmentPropagator propagator = targetedSegmentList.getSegmentPropagator(new EvaluatorGroup());
    segmentResults = propagator.propagate();
  }

  /**
   * Retrieves the ephemeris from the last run. If this is called after
   * runNominalPropagationToCheckRequiresManeuver, returns the nominal ephemeris. If called after
   * propagate, gets whichever ephemeris the targeter was initialized to produce.
   */
  public String getEphemeris() {
    StkEphemerisFile ephemeris = getEphemerisFromResults(segmentResults, motionId);
    StringWriter writer = new StringWriter();
    ephemeris.writeTo(writer);
    return writer.toString();
  }

  /**
   * Retrieves the computed maneuver from the targeter's results. Returns null and logs a warning if
   * targeting was not successful.
   *
   * <p>Note that this will compute a maneuver to move the object within the given tolerance of the
   * given distance from earth *even* if it is nominally outside that distance. To find out whether
   * the nominal propagation lands the object within the target distance from earth, use
   * requiresManeuver.
   *
   * @return The computed maneuver.
   */
  public double[] getManeuver() throws AdamPropagationException {
    double[] totalManeuver = {0, 0, 0};
    if (segmentResults instanceof TargetedSegmentListResults) {
      TargetedSegmentListResults targetedSegmentListResults =
          (TargetedSegmentListResults) segmentResults;

      for (TargetedSegmentListOperator operator : targetedSegmentList.getOperators()) {
        TargetedSegmentListOperatorResults operatorResults =
            targetedSegmentListResults.getOperatorResult(operator);
        if (operatorResults == null) {
          log.warning("Operator " + operator.getName() + " did not produce any results.");
          throw new AdamPropagationException("Targeter had no results.");
        }
        if (operatorResults instanceof TargetedSegmentListDifferentialCorrectorResults) {
          TargetedSegmentListDifferentialCorrectorResults dcResults =
              (TargetedSegmentListDifferentialCorrectorResults) operatorResults;

          MultivariableFunctionSolverResults solverResults = dcResults.getFunctionSolverResults();
          logStepsTaken(solverResults);
          if (dcResults.getCompletedSuccessfully()
              && dcResults.getConverged()
              && solverResults.getConverged()) {
            log.info(
                "Operator "
                    + operatorResults.getIdentifier().getName()
                    + " converged after "
                    + solverResults.getIterationResults().size()
                    + " iterations.");
            SolvableMultivariableFunctionResults finalResults =
                solverResults.getFinalIteration().getFunctionResult();
            double[] vars = finalResults.getVariablesUsed();
            for (int i = 0; i < vars.length; i++) {
              totalManeuver[i] += vars[i];
            }
          } else {
            log.warning(
                "Operator " + operatorResults.getIdentifier().getName() + " did not converge.");
            throw new AdamPropagationException(
                "Targeter did not converge after "
                    + solverResults.getIterationResults().size()
                    + " iterations.");
          }
        } else {
          log.warning(
              "Operator "
                  + operatorResults.getIdentifier().getName()
                  + " did not produce TargetedSegmentListDifferentialCorrectorResults.");
          throw new AdamPropagationException("Unexpected results type when computing maneuver.");
        }
      }
    } else {
      log.warning("Targeted segment list did not produce TargetedSegmentListResults.");
      throw new AdamPropagationException("Unexpected results type when computing maneuver.");
    }

    return totalManeuver;
  }

  private StkEphemerisFile getEphemerisFromResults(SegmentResults results, String motionId) {
    DateMotionCollection1<Cartesian> data =
        results.getDateMotionCollectionOfOverallTrajectory(motionId, referenceFrame);

    StkEphemerisFile.EphemerisTimePosVel ephemeris = new StkEphemerisFile.EphemerisTimePosVel();
    ephemeris.setInterpolator(
        new TranslationalMotionInterpolator(InterpolationAlgorithmType.HERMITE, 6, data));
    ephemeris.setCoordinateSystem(referenceFrame);
    ephemeris.setEphemerisData(data);
    ephemeris.setTimeFormat(StkTimeFormat.UTC_G);

    StkEphemerisFile file = new StkEphemerisFile();
    file.setData(ephemeris);
    return file;
  }

  /**
   * Returns a sun-centered propagator for the given point with a force model as defined in the
   * given config.
   */
  private NumericalPropagatorDefinition getPropagator(
      PropagatorConfiguration config,
      PropagationNewtonianPoint propagationPoint,
      JulianDate epoch) {
    // Only sun-centered forces are supported. This should be validated outside this class.
    ForceModelHelper.initializeSunCenteredForces(config, propagationPoint);

    // Create the propagator
    NumericalPropagatorDefinition propagator = new NumericalPropagatorDefinition();
    propagator.getIntegrationElements().add(propagationPoint);
    propagator.setIntegrator(ForceModelHelper.getRungeKuttaFehlberg78Integrator());
    propagator.setEpoch(epoch);

    return propagator;
  }

  /**
   * Adds variable to the differential corrector representing modifications to the x component of
   * the delta-v imparted in the given impulsive maneuver segment.
   *
   * <p>Notes about max step and perturbation:
   *
   * <p>Max step should be more than the total amount you think you might be wrong, since this is an
   * upper bound on the total maneuver.
   *
   * <p>Perturbation (the amount that the velocity will be perturbed to calculate the slope of the
   * effect of delta-V for each iteration) should be quite small, since the final maneuvers will be
   * very small and it's important that this be smaller.
   *
   * <p>TODO: consider figuring out how to make perturbation dynamic. For large steps, a larger
   * perturbation makes sense. But once we get very close to the target value, a perturbation can
   * become too large (relatively), causing us to overshoot the target and endlessly oscillate.
   * Ideally the perturbation would always be some small but significant fraction of the previous
   * step.
   */
  private SegmentPropagatorVariable getManeuverXVariable(
      ImpulsiveManeuverSegment maneuverSegment, double maxStep, double perturbation) {
    ImpulsiveManeuverInformation maneuverSegmentDetails = maneuverSegment.getManeuvers().get(0);
    SegmentPropagatorVariable maneuverVariableX =
        maneuverSegment.createVariable(
            maxStep,
            perturbation,
            SetVariableCallback.of(
                (variableDelta, configuration) -> {
                  ImpulsiveManeuverInformation info = configuration.get(maneuverSegmentDetails);
                  info.setX(info.getX() + variableDelta);
                }));
    maneuverVariableX.setName("Maneuver variable X");
    return maneuverVariableX;
  }

  private static boolean verbose = false;

  private void pFine(String str) {
    if (verbose) {
      System.out.println(str);
    }
  }

  // Leaving here; useful for debugging and comparing against desktop.
  private void logStepsTaken(MultivariableFunctionSolverResults solverResults) {
    if (!verbose) {
      return;
    }
    pFine("Attempted maneuvers:");
    for (MultivariableFunctionSolverIterationResults result : solverResults.getIterationResults()) {
      double[] vars = result.getFunctionResult().getVariablesUsed();
      double[] constraints = result.getFunctionResult().getConstraintValues();
      if (vars.length == 1) {
        pFine(
            "Iteration "
                + result.getIteration()
                + ", "
                + vars[0]
                + ", "
                + result.getFunctionResult().getConstraintValues()[0]);
      } else {
        pFine(
            "Iteration "
                + result.getIteration()
                + ", "
                + vars[0]
                + ", "
                + vars[1]
                + ", "
                + vars[2]
                + ", "
                + constraints[0]);
      }
    }
  }
}
