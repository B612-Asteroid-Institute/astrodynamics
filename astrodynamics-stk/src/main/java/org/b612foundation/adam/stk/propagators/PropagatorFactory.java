package org.b612foundation.adam.stk.propagators;

import java.io.IOException;

public final class PropagatorFactory {
  public static final String STK_PROPAGATOR_STRING = "STK";
  public static final String STK_PROPAGATOR_WITH_STOPPING_CONDITIONS = "STK_WITH_STOPPING_CONDS";
  public static final String OORB_PROPAGATOR_STRING = "OORB";

  public static OrbitPropagator getPropagator(String propagatorTypeString) throws IOException {
    switch (propagatorTypeString) {
      case STK_PROPAGATOR_STRING:
        return new StkPropagator();
      case STK_PROPAGATOR_WITH_STOPPING_CONDITIONS:
        return new StkSegmentPropagator();
      default:
        throw new IllegalArgumentException("Propagator type not found: " + propagatorTypeString);
    }
  }
}
