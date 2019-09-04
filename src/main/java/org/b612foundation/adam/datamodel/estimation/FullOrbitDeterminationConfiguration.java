package org.b612foundation.adam.datamodel.estimation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.b612foundation.adam.opm.OrbitParameterMessage;

import java.util.List;

@AllArgsConstructor
@Data
@ToString(callSuper = true)
public class FullOrbitDeterminationConfiguration extends BaseOrbitDeterminationConfiguration {
    /** The initial orbital state and covariance that the OD will be performed against **/
    private OrbitParameterMessage initialOrbitGuess;

    @Builder
    public FullOrbitDeterminationConfiguration(String propgator_uuid,
                                                double forwardPropagationDurationDays,
                                                double observationSamplingWindowDays,
                                                int maxIterations){
        super(propgator_uuid, forwardPropagationDurationDays, observationSamplingWindowDays, maxIterations);
    }

}
