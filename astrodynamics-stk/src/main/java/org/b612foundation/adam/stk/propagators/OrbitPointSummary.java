package org.b612foundation.adam.stk.propagators;

import agi.foundation.celestial.JplDECentralBody;
import agi.foundation.time.JulianDate;
import java.io.Serializable;
import lombok.Builder;
import lombok.Value;
import org.b612foundation.adam.common.DistanceType;
import org.b612foundation.adam.common.DistanceUnits;
import org.b612foundation.adam.job.model.types.propagation.OrbitPositionType;
import org.b612foundation.adam.opm.OdmCommonMetadata.ReferenceFrame;
import org.b612foundation.adam.opm.OdmCommonMetadata.TimeSystem;

/** TODO: naming? */
@Value
@Builder
public final class OrbitPointSummary implements Serializable {

  OrbitPositionType orbitPositionType;

  // Whether this was a stopping point in a propagation.
  boolean stopped;

  // The time associated with the point. Marked transient because JulianDate is not serializable and
  // it's easiest (for now) to create tests for correctness of the date using this field. e.g.
  // StkSegmentPropagatorTest.testStkPropagation_stoppedOnImpact()
  transient JulianDate time;

  // The time in ISO format.
  String timeIsoFormat;

  TimeSystem timeSystem;

  JplDECentralBody targetBody;

  // Target body centered position, e.g. Earth centered position. [x, y, z]
  double[] targetBodyCenteredPosition;

  DistanceUnits targetBodyCenteredPositionUnits;

  ReferenceFrame targetBodyReferenceFrame;

  double distanceFromTarget;

  DistanceType distanceType;

  DistanceUnits distanceUnits;
}
