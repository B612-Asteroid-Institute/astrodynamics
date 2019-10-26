package org.b612foundation.adam.estimators;

import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.datamodel.estimation.OrbitDeterminationConfiguration;
import org.b612foundation.adam.datamodel.estimation.OrbitDeterminationParameters;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.runnable.AdamRunnableException;

import java.io.IOException;

public interface OrbitEstimator {
    OrbitParameterMessage batchEstimate(OrbitDeterminationParameters odParams,
                                        OrbitDeterminationConfiguration propagationConfig,
                                        PropagatorConfiguration config,
                                        String propagationIdForLogging) throws AdamRunnableException, IOException;

}
