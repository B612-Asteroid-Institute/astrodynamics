package org.b612foundation.adam.stk;

import agi.foundation.coordinates.Cartesian;
import org.b612foundation.adam.opm.OdmCommonMetadata;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.opm.StateVector;

/** Utility methods used in tests. */
public final class PropagatorTestHelper {
  /** Input Cartesians are in m and m/s, OPM wants km and km/s. */
  public static OrbitParameterMessage getOpm(
      String name, String epoch, Cartesian startPositionSunIcrf, Cartesian startVelocitySunIcrf) {
    return new OrbitParameterMessage()
        .setCcsds_opm_vers("2.0")
        .setMetadata(
            new OdmCommonMetadata()
                .setObject_name(name)
                .setObject_id(name)
                .setCenter_name(OdmCommonMetadata.CenterName.SUN)
                .setRef_frame(OdmCommonMetadata.ReferenceFrame.ICRF)
                .setTime_system(OdmCommonMetadata.TimeSystem.UTC))
        .setState_vector(
            new StateVector()
                .setEpoch(epoch)
                .setX(startPositionSunIcrf.getX() / 1000)
                .setY(startPositionSunIcrf.getY() / 1000)
                .setZ(startPositionSunIcrf.getZ() / 1000)
                .setX_dot(startVelocitySunIcrf.getX() / 1000)
                .setY_dot(startVelocitySunIcrf.getY() / 1000)
                .setZ_dot(startVelocitySunIcrf.getZ() / 1000));
  }

  public static OrbitParameterMessage getOpm(String name, StateVector stateVector) {
    return new OrbitParameterMessage()
        .setCcsds_opm_vers("2.0")
        .setMetadata(
            new OdmCommonMetadata()
                .setObject_name(name)
                .setObject_id(name)
                .setCenter_name(OdmCommonMetadata.CenterName.SUN)
                .setRef_frame(OdmCommonMetadata.ReferenceFrame.ICRF)
                .setTime_system(OdmCommonMetadata.TimeSystem.UTC))
        .setState_vector(stateVector);
  }
}
