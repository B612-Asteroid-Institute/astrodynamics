package org.b612foundation.adam.stk.propagators;

import java.io.IOException;
import org.b612foundation.stk.StkLicense;

public class StkPropagatorTest extends PropagatorTests {

  @Override
  OrbitPropagator getPropagator() throws IOException {
    StkLicense.activate();

    return new StkPropagator();
  }
}
