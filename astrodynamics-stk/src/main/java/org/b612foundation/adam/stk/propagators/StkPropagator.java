package org.b612foundation.adam.stk.propagators;

import static org.b612foundation.adam.stk.StkPropagationHelper.validateStartAndEndDate;

import agi.foundation.celestial.CentralBodiesFacet;
import agi.foundation.compatibility.EventHandler;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.geometry.GeometryTransformer;
import agi.foundation.geometry.Point;
import agi.foundation.geometry.PointEvaluator;
import agi.foundation.geometry.shapes.Ellipsoid;
import agi.foundation.numericalmethods.ExtremumType;
import agi.foundation.numericalmethods.FunctionSegmentSlope;
import agi.foundation.numericalmethods.JulianDateExtremumFoundEventArgs;
import agi.foundation.numericalmethods.JulianDateFunctionExplorer;
import agi.foundation.numericalmethods.JulianDateFunctionSampling;
import agi.foundation.numericalmethods.JulianDateSampleSuggestionCallback;
import agi.foundation.numericalmethods.JulianDateSimpleFunction;
import agi.foundation.numericalmethods.JulianDateThresholdCrossingFoundEventArgs;
import agi.foundation.numericalmethods.advanced.JulianDateFunctionExtremumFound;
import agi.foundation.numericalmethods.advanced.JulianDateFunctionThresholdCrossingFound;
import agi.foundation.stk.StkEphemerisFile;
import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeIntervalCollection;
import agi.foundation.time.TimeStandard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.b612foundation.adam.analysis.DatedValue;
import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.exceptions.AdamPropagationException;
import org.b612foundation.adam.opm.OrbitEphemerisMessage;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.runnable.AdamRunnableException;
import org.b612foundation.adam.stk.propagators.ForceModelHelper;
import org.b612foundation.stk.StkLicense;

/**
 * Propagator that uses STK components.
 *
 * <p>TODO: Move to astrodynamics.
 */
public class StkPropagator implements OrbitPropagator {
  private static Logger log = Logger.getLogger(StkPropagator.class.getName());

  public StkPropagator() throws IOException {
    StkLicense.activate();
  }

  /**
   * Propagates one orbit according to the given parameters, using a force model as specified in the
   * given config.
   */
  public OrbitEphemerisMessage propagate(
      PropagationParameters propagationParams,
      PropagatorConfiguration config,
      String propagationIdForLogging)
      throws AdamPropagationException {
    try {
      JulianDate startDate =
          TimeHelper.fromIsoFormat(
              propagationParams.getStart_time(), TimeStandard.getCoordinatedUniversalTime());
      JulianDate endDate =
          TimeHelper.fromIsoFormat(
              propagationParams.getEnd_time(), TimeStandard.getCoordinatedUniversalTime());
      validateStartAndEndDate(startDate, endDate);
      OrbitParameterMessage opm = propagationParams.getOpm();
      log.info("Starting propagation for " + propagationIdForLogging);
      PropagatedInterplanetaryOrbit orbit =
          PropagatedInterplanetaryOrbit.fromOpm(opm, endDate, config);
      boolean interpolated = propagationParams.getStep_duration_sec() > 0;
      if (interpolated) {
        Duration step = Duration.fromSeconds(propagationParams.getStep_duration_sec());
        if (JulianDate.greaterThan(startDate, endDate)) {
          step =
              step.multiply(
                  -1); // getEphemeris requires a negative step for a backwards propagation.
        }

        return orbit.exportOrbitEphemerisMessage(startDate, endDate, step);
      } else {
        return orbit.exportOrbitEphemerisMessage();
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

  /** Loads ephemeris from a string for post-processing. */
  public StkEphemerisFile loadFromString(String contents) {
    StringReader reader = new StringReader(contents);
    return StkEphemerisFile.readFrom(new BufferedReader(reader));
  }

  /** Load the point from the ephemeris and evaluate it in Earth inertial frame. */
  public PointEvaluator getPointInEarthInertialFrame(StkEphemerisFile originalEphem) {
    ForceModelHelper.loadStandardObjects(); // Load a DE file.
    Point original = originalEphem.createPoint();
    return GeometryTransformer.observePoint(
        original, CentralBodiesFacet.getFromContext().getEarth().getInertialFrame());
  }

  /** Returns the list of original points in the ephemeris in the Earth inertial frame. */
  public List<DatedValue<Cartesian>> transformToEarthFrame(StkEphemerisFile originalEphem) {
    PointEvaluator observed = getPointInEarthInertialFrame(originalEphem);
    List<DatedValue<Cartesian>> toEarth = new ArrayList<>();
    for (JulianDate date : originalEphem.getData().getTimes()) {
      Cartesian position = observed.evaluate(date);
      toEarth.add(new DatedValue<>(date, position));
    }
    return toEarth;
  }

  /**
   * Collects all minima of the distance to Earth center from the given ephemeris by using numeric
   * extrema search. Note that in case of impact the distance would be less than Earth radius.
   * Distances are in meters. The result is sorted by time.
   */
  // TODO: expose this
  public List<DatedValue<Double>> findClosestApproachesToEarth(StkEphemerisFile originalEphem) {
    // Pull out a point and wrap it in a function of date. To get the distance to Earth center,
    // transform the point into
    // Earth ICRF and take magnitude of the coordinate vector.
    PointEvaluator observed = getPointInEarthInertialFrame(originalEphem);
    JulianDateSimpleFunction function =
        JulianDateSimpleFunction.of((date) -> observed.evaluate(date).getMagnitude());

    // The point came from ephemeris, so there should be one interval it is defined on. We want the
    // ends of that
    // interval.
    TimeIntervalCollection definedIntervals = observed.getAvailabilityIntervals();
    if (definedIntervals.size() != 1) {
      throw new IllegalStateException(
          "A point from ephemeris should be defined in one interval, got "
              + definedIntervals.size());
    }
    JulianDate startTime = definedIntervals.get(0).getStart();
    JulianDate stopTime = definedIntervals.get(0).getStop();

    // Explore the function and pull out all local minima.
    JulianDateFunctionExplorer explorer = new JulianDateFunctionExplorer();
    explorer.setFindAllExtremaPrecisely(true);
    explorer.setReportExtremaAtEndpoints(false);

    JulianDateFunctionSampling sampling = new JulianDateFunctionSampling();
    sampling.setMinimumStep(Duration.fromSeconds(1));
    sampling.setMaximumStep(Duration.fromDays(1));
    sampling.setDefaultStep(Duration.fromHours(3));
    sampling.setTrendingStep(Duration.fromHours(1));
    explorer.setSampleSuggestionCallback(
        JulianDateSampleSuggestionCallback.of(sampling.getFunctionSampler()::getNextSample));

    // Collect minima into this list in the callback.
    List<DatedValue<Double>> approaches = new ArrayList<>();
    explorer.addLocalExtremumFound(
        new EventHandler<JulianDateExtremumFoundEventArgs>() {
          @Override
          public void invoke(Object sender, JulianDateExtremumFoundEventArgs args) {
            JulianDateFunctionExtremumFound finding = args.getFinding();
            if (ExtremumType.MINIMUM.equals(finding.getExtremumType())) {
              approaches.add(
                  new DatedValue<>(finding.getExtremumDate(), finding.getExtremumValue()));
            }
          }
        });
    explorer.getFunctions().add(function);
    explorer.explore(startTime, stopTime);
    Collections.sort(approaches);
    return approaches;
  }

  /**
   * Collects all descending crossings of the threshold distance to Earth's WSG84 surface (AKA
   * impacts) from the given ephemeris by using numeric search. We collect all such events, not just
   * the first one. The values are distances to the surface, which may not be exactly equal to the
   * threshold due to numeric step and tolerances. The result is sorted by time.
   */
  // TODO: expose this
  public List<DatedValue<Double>> findEarthImpacts(
      StkEphemerisFile originalEphem, double metersToSurfaceThreshold) {
    // Pull out a point and wrap it in a function of date. First transform the point to Earth ICRF
    // and then get height
    // above the ellipsoid from that.
    PointEvaluator observed = getPointInEarthInertialFrame(originalEphem);
    final Ellipsoid earthSurface = CentralBodiesFacet.getFromContext().getEarth().getShape();
    JulianDateSimpleFunction function =
        JulianDateSimpleFunction.of(
            (date) -> earthSurface.computeApproximateHeight(observed.evaluate(date)));

    // The point came from ephemeris, so there should be one interval it is defined on. We want the
    // ends of that
    // interval.
    TimeIntervalCollection definedIntervals = observed.getAvailabilityIntervals();
    if (definedIntervals.size() != 1) {
      throw new IllegalStateException(
          "A point from ephemeris should be defined in one interval, got "
              + definedIntervals.size());
    }
    JulianDate startTime = definedIntervals.get(0).getStart();
    JulianDate stopTime = definedIntervals.get(0).getStop();

    // We don't care about the extrema, but the explorer finds the crossings much better if we tell
    // it to use extrema.
    JulianDateFunctionExplorer explorer = new JulianDateFunctionExplorer();
    explorer.setFindAllCrossingsPrecisely(true);
    explorer.setExploreExtremaToFindCrossings(true);
    explorer.setReportExtremaAtEndpoints(false);

    JulianDateFunctionSampling sampling = new JulianDateFunctionSampling();
    sampling.setMinimumStep(Duration.fromSeconds(1));
    sampling.setMaximumStep(Duration.fromDays(1));
    sampling.setDefaultStep(Duration.fromHours(3));
    sampling.setTrendingStep(Duration.fromHours(1));
    explorer.setSampleSuggestionCallback(
        JulianDateSampleSuggestionCallback.of(sampling.getFunctionSampler()::getNextSample));

    // Collect descending crossings into this list in the callback.
    List<DatedValue<Double>> approaches = new ArrayList<>();
    explorer.addThresholdCrossingFound(
        new EventHandler<JulianDateThresholdCrossingFoundEventArgs>() {
          @Override
          public void invoke(Object sender, JulianDateThresholdCrossingFoundEventArgs args) {
            JulianDateFunctionThresholdCrossingFound finding = args.getFinding();
            if (finding.getSlope().equals(FunctionSegmentSlope.DECREASING)) {
              approaches.add(
                  new DatedValue<>(finding.getCrossingDate(), finding.getCrossingValue()));
            }
          }
        });
    explorer.getFunctions().add(function, metersToSurfaceThreshold);
    explorer.explore(startTime, stopTime);
    Collections.sort(approaches);
    return approaches;
  }

  // TODO: create more general methods for finding close approach/impact to other bodies (which user
  // will pass in to the method, from the propagator force model)
}
