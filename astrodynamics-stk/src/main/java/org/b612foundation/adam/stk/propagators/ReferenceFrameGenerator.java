package org.b612foundation.adam.stk.propagators;

import agi.foundation.celestial.CentralBodiesFacet;
import agi.foundation.celestial.CentralBody;
import agi.foundation.geometry.Axes;
import agi.foundation.geometry.AxesFixedAtJulianDate;
import agi.foundation.geometry.Point;
import agi.foundation.geometry.ReferenceFrame;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeConstants;
import org.b612foundation.adam.opm.OdmCommonMetadata;

// TODO: Move to astrodynamics.
public class ReferenceFrameGenerator {

  public CentralBody getCentralBody(OdmCommonMetadata.CenterName centerName) {
    switch (centerName) {
      case EARTH:
        return CentralBodiesFacet.getFromContext().getEarth();
      case SUN:
        return CentralBodiesFacet.getFromContext().getSun();
      default:
        throw new IllegalArgumentException("Central body " + centerName.name() + " not supported.");
    }
  }

  public ReferenceFrame getReferenceFrame(
      OdmCommonMetadata.ReferenceFrame referenceFrameName,
      OdmCommonMetadata.CenterName centerName) {
    CentralBody center = getCentralBody(centerName);
    Point origin = center.getCenterOfMassPoint();

    Axes referenceFrameAxes = null;
    switch (referenceFrameName) {
      case ICRF:
        // This is for backwards-compatibility. Client-side code all currently specifies "ITRF-97"
        // as the reference frame, while meaning to specify "ICRF". So maintain past behavior and
        // use ICRF until client side is updated.
      case ITRF97:
        referenceFrameAxes = getICRFReferenceFrameAxes();
        break;
      case EMEME2000:
        // Use the same axes regardless of central body. These axes are inertial, and defined by
        // the Earth's mean ecliptic and mean equinox, fixed at the Julian epoch. Commonly used
        // with sun as central body.
        referenceFrameAxes = getEarthMEME2000ReferenceFrameAxes();
        break;
      case ITRF2000:
      case ITRF93:
      case EME2000:
      case GCRF:
      case GRC:
      case MCI:
      case RTN:
      case TDR:
      case TEME:
      case TNW:
      case TOD:
      default:
        throw new IllegalArgumentException(
            "Reference frame " + referenceFrameName.name() + " not supported.");
    }

    return new ReferenceFrame(origin, referenceFrameAxes);
  }

  private Axes getICRFReferenceFrameAxes() {
    return CentralBodiesFacet.getFromContext()
        .getEarth()
        .getInternationalCelestialReferenceFrame()
        .getAxes();
  }

  private Axes getEarthMEME2000ReferenceFrameAxes() {
    // Get the axes from the Earth's MeanEclipticMeanEquinox frame.
    ReferenceFrame earthMeanEclipticMeanEquinox =
        CentralBodiesFacet.getFromContext().getEarth().getMeanEclipticMeanEquinoxFrame();
    Axes axes = earthMeanEclipticMeanEquinox.getAxes();

    // Fix them at the Julian epoch with respect to axes known to be inertial.
    JulianDate epoch = TimeConstants.J2000;
    AxesFixedAtJulianDate axesEarthMeanEclipticMeanEquinoxJ2000 =
        new AxesFixedAtJulianDate(
            axes, CentralBodiesFacet.getFromContext().getSun().getInertialFrame().getAxes(), epoch);

    return axesEarthMeanEclipticMeanEquinoxJ2000;
  }
}
