package org.b612foundation.adam.stk.propagators;

import agi.foundation.geometry.ReferenceFrame;
import agi.foundation.segmentpropagation.SegmentPropagator;
import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;
import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.exceptions.AdamPropagationException;
import org.b612foundation.adam.opm.OrbitEphemerisMessage;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.propagators.OrbitPropagator;
import org.b612foundation.stk.StkLicense;

import java.util.List;
import java.util.logging.Logger;

import static org.b612foundation.adam.stk.StkPropagationHelper.parseUtcAsJulian;
import static org.b612foundation.adam.stk.StkPropagationHelper.validateStartAndEndDate;
import static org.b612foundation.adam.stk.propagators.StkSegmentPropagatedOrbit.initializeOrbit;

/**
 * An STK propagator that uses {@link SegmentPropagator}.
 *
 * <p>This is a wrapper class that delegates the propagation to {@link StkSegmentPropagatedOrbit}.
 */
public final class StkSegmentPropagator implements OrbitPropagator {
  private static final Logger log = Logger.getLogger(StkSegmentPropagator.class.getName());

  private StkSegmentPropagatedOrbit orbit;
  private JulianDate startDate;
  private JulianDate endDate;
  private Duration step;
  private OrbitPointSummary finalState;

  public StkSegmentPropagator() {
    StkLicense.activate();
  }

  public StkSegmentPropagator(String stkLicense) {
    StkLicense.activate(stkLicense);
  }

  @Override
  public OrbitEphemerisMessage propagate(
      PropagationParameters propagationParams,
      PropagatorConfiguration config,
      String propagationIdForLogging)
      throws AdamPropagationException {
    try {
      startDate =
          TimeHelper.fromIsoFormat(
              propagationParams.getStart_time(), TimeStandard.getCoordinatedUniversalTime());
      endDate =
          TimeHelper.fromIsoFormat(
              propagationParams.getEnd_time(), TimeStandard.getCoordinatedUniversalTime());
      validateStartAndEndDate(startDate, endDate);

      step = Duration.fromSeconds(propagationParams.getStep_duration_sec());

      log.info("Starting propagation for " + propagationIdForLogging);
      OrbitParameterMessage opm = propagationParams.getOpm();
      JulianDate epoch = parseUtcAsJulian(opm.getState_vector().getEpoch());

      orbit = initializeOrbit(opm, config);
      orbit.propagate(propagationParams, epoch, endDate);

      finalState = determineFinalState();

      // Adjust end date to when the propagation actually ended, e.g. when using stopping conditions
      if (orbit.getRawDates().size() > 0) {
        endDate = orbit.getRawDates().get(orbit.getRawDates().size() - 1);
      }

      boolean interpolated = propagationParams.getStep_duration_sec() > 0;
      if (interpolated) {
        if (JulianDate.greaterThan(startDate, endDate)) {
          // getEphemeris requires a negative step for a backwards propagation.
          step = step.multiply(-1);
        }
        return orbit.exportOrbitEphemerisMessage(startDate, endDate, step);
      } else {
        return orbit.exportOrbitEphemerisMessageFromRawValues();
      }
    } catch (Exception e) {
      String cause = e.getCause() == null ? "" : " Caused by: " + e.getCause().toString();
      log.info("Failed to propagate orbit: " + e.toString() + cause);
      e.printStackTrace();
      if (e.getCause() != null) {
        e.getCause().printStackTrace();
      }
      throw new AdamPropagationException(
          "Failed to propagate orbit for " + propagationIdForLogging, e);
    }
  }

  /** The close approaches that occurred during propagation. */
  public List<OrbitPointSummary> getCloseApproaches() {
    return orbit.getCloseApproaches();
  }

  private OrbitPointSummary determineFinalState() {
    if (orbit.getImpact().isPresent()) {
      return orbit.getImpact().get();
    } else if (orbit.getStoppedOnCloseApproach()) {
      List<OrbitPointSummary> closeApproaches = getCloseApproaches();
      return closeApproaches.get(closeApproaches.size() - 1);
    }

    OrbitPointSummary finalPositionAndTime = orbit.getFinalPositionAndTime();
    return OrbitPointSummary.builder()
        .orbitPositionType(OrbitPositionType.MISS)
        .stopped(finalPositionAndTime.isStopped())
        .time(finalPositionAndTime.getTime())
        .timeIsoFormat(finalPositionAndTime.getTimeIsoFormat())
        .timeSystem(finalPositionAndTime.getTimeSystem())
        .targetBody(finalPositionAndTime.getTargetBody())
        .targetBodyCenteredPositionUnits(finalPositionAndTime.getTargetBodyCenteredPositionUnits())
        .targetBodyCenteredPosition(finalPositionAndTime.getTargetBodyCenteredPosition())
        .targetBodyReferenceFrame(finalPositionAndTime.getTargetBodyReferenceFrame())
        .distanceFromTarget(finalPositionAndTime.getDistanceFromTarget())
        .distanceUnits(finalPositionAndTime.getDistanceUnits())
        .distanceType(finalPositionAndTime.getDistanceType())
        .build();
  }

  public OrbitPointSummary getFinalState() {
    return finalState;
  }

  public ReferenceFrame getReferenceFrame() {
    return orbit.getReferenceFrame();
  }
}
