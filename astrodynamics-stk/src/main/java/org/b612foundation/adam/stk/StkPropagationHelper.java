package org.b612foundation.adam.stk;

import static com.google.common.base.Preconditions.checkArgument;

import agi.foundation.Motion1;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.geometry.ReferenceFrame;
import agi.foundation.propagators.NumericalPropagatorDefinition;
import agi.foundation.propagators.PropagationNewtonianPoint;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeInterval;
import agi.foundation.time.TimeStandard;
import java.time.ZonedDateTime;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.opm.OdmCommonMetadata;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.stk.propagators.ForceModelHelper;

/** Common methods used in propagation classes. */
public final class StkPropagationHelper {

  private StkPropagationHelper() {}

  /**
   * Validates that start date <= end date and start and end dates are both in the supported date
   * range.
   */
  public static void validateStartAndEndDate(JulianDate startDate, JulianDate endDate) {
    TimeInterval supportedDateRange = ForceModelHelper.getSupportedDateRange();
    checkArgument(
        supportedDateRange.contains(startDate),
        "Start date not within supported range (" + supportedDateRange + ")");
    checkArgument(
        supportedDateRange.contains(endDate),
        "End date not within supported range (" + supportedDateRange + ")");
  }

  public static JulianDate parseUtcAsJulian(String utcDateTime) {
    if (Character.isDigit(utcDateTime.charAt(utcDateTime.length() - 1))) {
      utcDateTime = utcDateTime + "Z";
    }
    return new JulianDate(
        ZonedDateTime.parse(utcDateTime), TimeStandard.getCoordinatedUniversalTime());
  }

  /** Initializes the object we will track using given reference frame. */
  public static PropagationNewtonianPoint initializePropagationObjectWithReferenceFrame(
      OrbitParameterMessage opm, ReferenceFrame referenceFrame, String propagationObjectId) {
    Motion1<Cartesian> cartesianElements = StkOpmHelper.getCartesianStateVector(opm);
    return new PropagationNewtonianPoint(
        propagationObjectId,
        referenceFrame,
        cartesianElements.getValue(),
        cartesianElements.getFirstDerivative());
  }

  public static void initializeCentralBodyForces(
      PropagationNewtonianPoint propagationPoint,
      PropagatorConfiguration config,
      OdmCommonMetadata.CenterName centerName) {
    switch (centerName) {
      case EARTH:
        ForceModelHelper.initializeEarthCenteredForces(config, propagationPoint);
        break;
      case SUN:
        ForceModelHelper.initializeSunCenteredForces(config, propagationPoint);
        break;
      default:
        throw new IllegalArgumentException(
            "Can't initialize forces for central body " + centerName);
    }
  }

  /**
   * Returns a {@link NumericalPropagatorDefinition} for the given point with a force model as
   * defined in the given config.
   */
  public static NumericalPropagatorDefinition getNumericalPropagator(
      PropagationNewtonianPoint propagationPoint, JulianDate epoch) {
    NumericalPropagatorDefinition propagatorDefinition = new NumericalPropagatorDefinition();
    propagatorDefinition.getIntegrationElements().add(propagationPoint);
    propagatorDefinition.setIntegrator(ForceModelHelper.getRungeKuttaFehlberg78Integrator());
    propagatorDefinition.setEpoch(epoch);

    return propagatorDefinition;
  }
}
