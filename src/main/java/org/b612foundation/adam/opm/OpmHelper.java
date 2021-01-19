package org.b612foundation.adam.opm;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.time.AbsoluteDate;

import static org.b612foundation.adam.astro.AstroConstants.KM_TO_M;

public class OpmHelper {

  /**
   * Given an OPM message it will extract a cartesian position/velocity StateVector by converting
   * the Keplerian state, if it is supplied, or by returning the Cartesian vectors directly
   *
   * @param opm
   * @return StateVector
   */
  public static StateVector getCartesianStateVector(OrbitParameterMessage opm) {
    if (opm.getKeplerian() != null) {
      // ODM uses km for semimajor axis and km^3/s^2 for gravitational constant. Orekit uses m and
      // m^3/s^2.
      // Because not doing any state or frame transformation but epoch and frame are irrelevant
      // are paramaters setting them to stand-in values
      final Frame standInFrame = FramesFactory.getICRF();
      final AbsoluteDate standInEpoch = AbsoluteDate.ARBITRARY_EPOCH;
      final double epsilon = 1e-20;
      final double gm = opm.getKeplerian().getGm() * (KM_TO_M * KM_TO_M * KM_TO_M);
      final double sma = opm.getKeplerian().getSemi_major_axis() * KM_TO_M;
      final double ecc = opm.getKeplerian().getEccentricity();
      final double inc = Math.toRadians(opm.getKeplerian().getInclination());
      final double argPeri = Math.toRadians(opm.getKeplerian().getArg_of_pericenter());
      final double raan = Math.toRadians(opm.getKeplerian().getRa_of_asc_node());
      final PositionAngle anomalyType;
      final double anomaly;
      if (Math.abs(opm.getKeplerian().getMean_anomaly()) > epsilon) {
        anomalyType = PositionAngle.MEAN;
        anomaly = Math.toRadians(opm.getKeplerian().getMean_anomaly());
      } else {
        anomalyType = PositionAngle.TRUE;
        anomaly = Math.toRadians(opm.getKeplerian().getTrue_anomaly());
      }

      final KeplerianOrbit state =
          new KeplerianOrbit(
              sma, ecc, inc, argPeri, raan, anomaly, anomalyType, standInFrame, standInEpoch, gm);
      final Vector3D position = state.getPVCoordinates().getPosition();
      final Vector3D velocity = state.getPVCoordinates().getVelocity();

      return new StateVector()
          .setEpoch(opm.getState_vector().getEpoch())
          .setX(position.getX() / KM_TO_M)
          .setY(position.getY() / KM_TO_M)
          .setZ(position.getZ() / KM_TO_M)
          .setX_dot(velocity.getX() / KM_TO_M)
          .setY_dot(velocity.getY() / KM_TO_M)
          .setZ_dot(velocity.getZ() / KM_TO_M);
    } else {
      return opm.getState_vector();
    }
  }
}
