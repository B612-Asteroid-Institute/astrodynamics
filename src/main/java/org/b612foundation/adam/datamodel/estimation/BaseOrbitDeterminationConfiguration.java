package org.b612foundation.adam.datamodel.estimation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.b612foundation.adam.datamodel.AdamObject;


/**
 * This is a configuration for an Orbit determination system that will be used as settings when an OD is performed
 * which transcend a specific run's values like the space object ID and are about configuring force models, etc.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseOrbitDeterminationConfiguration extends AdamObject {
    /** Settings for the numeric propagator - the ID. */
    private String propagator_uuid;

    private double forwardPropagationDurationDays;

    private double observationSamplingWindowDays;

    private int maxIterations;

}
