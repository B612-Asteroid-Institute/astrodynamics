package org.b612foundation.adam.stk.propagators;

import org.b612foundation.adam.propagators.OrbitPropagator;
import org.b612foundation.stk.StkLicense;

import java.io.IOException;

public class StkPropagatorTest extends PropagatorTests {

  @Override
  OrbitPropagator getPropagator() throws IOException {
    StkLicense.activate();

    return new StkPropagator();
  }
}
