package org.b612foundation.adam.stk.propagators;

import org.b612foundation.adam.propagators.OrbitPropagator;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;

public class PropagatorFactoryTest {

  @Test
  public void testGetSTKPropagator() throws IOException {
    OrbitPropagator propagator =
        PropagatorFactory.getPropagator(PropagatorFactory.STK_PROPAGATOR_STRING);
    assertTrue(propagator instanceof StkPropagator);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownPropagatorError() throws IOException {
    PropagatorFactory.getPropagator(UUID.randomUUID().toString());
  }
}
