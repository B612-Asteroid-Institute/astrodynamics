package org.b612foundation.adam.propagators;

import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertTrue;


public class PropagatorFactoryTest {

    @Test
    public void testGetSTKPropagator() {
        OrbitPropagator propagator = PropagatorFactory.getPropagator(PropagatorFactory.STK_PROPAGATOR_STRING);
        assertTrue(propagator instanceof StkPropagator);
    }

    @Test
    public void testGetOorbPropagator() {
        OrbitPropagator propagator = PropagatorFactory.getPropagator(PropagatorFactory.OORB_PROPAGATOR_STRING);
        assertTrue(propagator instanceof OpenOrbPropagator);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUnknownPropagatorError() {
        PropagatorFactory.getPropagator(UUID.randomUUID().toString());
    }
}
