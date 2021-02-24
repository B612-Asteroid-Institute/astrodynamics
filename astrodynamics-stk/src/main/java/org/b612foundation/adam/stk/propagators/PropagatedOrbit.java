package org.b612foundation.adam.stk.propagators;

import agi.foundation.DateMotionCollection1;
import agi.foundation.Motion1;
import agi.foundation.MotionEvaluator1;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.geometry.ReferenceFrame;
import agi.foundation.numericalmethods.InterpolationAlgorithmType;
import agi.foundation.numericalmethods.TranslationalMotionInterpolator;
import agi.foundation.stk.StkEphemerisFile;
import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;

/**
 * PropagatedOrbit provides a way to query object position and velocity over time.
 *
 * <p>TODO: Move this to astrodynamics.
 */
public abstract class PropagatedOrbit {

  /** Returns the evaluator for the underlying propagator. */
  protected abstract MotionEvaluator1<Cartesian> getEvaluator();

  /** Returns the reference frame in which all values are specified. */
  public abstract ReferenceFrame getReferenceFrame();

  /** Returns object location, velocity, and acceleration at the given time. */
  public Motion1<Cartesian> getMotion(JulianDate when) {
    return getEvaluator().evaluate(when, 2); // 2nd order - up to acceleration
  }

  /**
   * Returns ephemeris within the given time interval with the given time step. Ephemeris is a list
   * of timed Cartesian positions and velocities. The timestamps are offsets in seconds from the
   * epoch (start time). The number of points in the list is defined by the stop date and the time
   * step.
   */
  public StkEphemerisFile getEphemeris(
      JulianDate startDate, JulianDate stopDate, Duration timeStep) {
    // 2nd order - up to acceleration. Last argument has something to do with tracking progress?
    DateMotionCollection1<Cartesian> rawData =
        getEvaluator().evaluate(startDate, stopDate, timeStep, 2, null);
    StkEphemerisFile.EphemerisTimePosVel ephemeris = new StkEphemerisFile.EphemerisTimePosVel();
    ephemeris.setInterpolator(
        new TranslationalMotionInterpolator(InterpolationAlgorithmType.HERMITE, 6, rawData));
    ephemeris.setCoordinateSystem(getReferenceFrame());
    ephemeris.setEphemerisData(rawData);
    StkEphemerisFile file = new StkEphemerisFile();
    file.setData(ephemeris);
    return file;
  }
}
