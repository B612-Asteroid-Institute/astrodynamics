package org.b612foundation.adam.propagators;

import java.util.ArrayList;
import java.util.List;

import org.b612foundation.adam.opm.Manuever;
import org.b612foundation.adam.opm.OrbitParameterMessage;

import agi.foundation.Motion1;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.coordinates.KeplerianElements;

public class OpmHelper {

  public static Motion1<Cartesian> getStateVector(OrbitParameterMessage opm) {
    if (opm.getKeplerian() != null) {
      // ODM uses km for semimajor axis and km^3/s^2 for gravitational constant. STK uses m and m^3/s^2.
      // @formatter:off
      KeplerianElements elements = new KeplerianElements(
          opm.getKeplerian().getSemi_major_axis() * 1000,
          opm.getKeplerian().getEccentricity(),
          Math.toRadians(opm.getKeplerian().getInclination()),
          Math.toRadians(opm.getKeplerian().getArg_of_pericenter()),
          Math.toRadians(opm.getKeplerian().getRa_of_asc_node()),
          Math.toRadians(opm.getKeplerian().getTrue_anomaly()),
          opm.getKeplerian().getGm() * 1e9);
      // @formatter:on
      return elements.toCartesian();
    } else {
      // ODM uses km and km/s. STK uses m and m/s.
      // @formatter:off
      Cartesian initialPosition = new Cartesian(
          opm.getState_vector().getX() * 1000,
          opm.getState_vector().getY() * 1000,
          opm.getState_vector().getZ() * 1000);
      Cartesian initialVelocity = new Cartesian(
          opm.getState_vector().getX_dot() * 1000,
          opm.getState_vector().getY_dot() * 1000,
          opm.getState_vector().getZ_dot() * 1000);
      // @formatter:on
      return new Motion1<Cartesian>(initialPosition, initialVelocity);
    }
  }
}
