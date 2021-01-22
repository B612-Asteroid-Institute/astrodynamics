package org.b612foundation.adam.stk;

import agi.foundation.Motion1;
import agi.foundation.coordinates.Cartesian;
import org.b612foundation.adam.opm.OpmHelper;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.opm.StateVector;

public class StkOpmHelper {
  public static Motion1<Cartesian> getCartesianStateVector(OrbitParameterMessage opm) {
    StateVector state = OpmHelper.getCartesianStateVector(opm);
    // ODM uses km and km/s. STK uses m and m/s.
    // @formatter:off
    Cartesian initialPosition =
        new Cartesian(state.getX() * 1000, state.getY() * 1000, state.getZ() * 1000);
    Cartesian initialVelocity =
        new Cartesian(state.getX_dot() * 1000, state.getY_dot() * 1000, state.getZ_dot() * 1000);
    // @formatter:on
    return new Motion1<Cartesian>(initialPosition, initialVelocity);
  }
}
