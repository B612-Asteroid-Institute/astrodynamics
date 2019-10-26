package org.b612foundation.adam.propagators;

public class PropagatorFactory {
    public static final String STK_PROPAGATOR_STRING = "STK";
    public static final String OORB_PROPAGATOR_STRING = "OORB";

    public static OrbitPropagator getPropagator(String propagatorTypeString) {
        switch(propagatorTypeString) {
            case STK_PROPAGATOR_STRING:
                return new StkPropagator();
            case OORB_PROPAGATOR_STRING:
                return new OpenOrbPropagator();
            default:
                throw new IllegalArgumentException("Propagator type not found: " + propagatorTypeString);
        }
    }
}
