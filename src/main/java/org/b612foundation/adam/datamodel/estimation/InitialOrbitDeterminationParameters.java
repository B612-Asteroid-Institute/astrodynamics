package org.b612foundation.adam.datamodel.estimation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Configuration settings specific to Initial Orbit Determinations
 */
@AllArgsConstructor
@Data
@ToString(callSuper = true)
public class InitialOrbitDeterminationParameters extends BaseOrbitDeterminationParameters {

    @Builder
    public InitialOrbitDeterminationParameters(
            String executor,
            int loggingLevel,
            String measurementsFormatType,
            String measurements,
            String orbit_determination_uuid,
            String spaceObjectId,
            List<String> observerIds,
            String outputFrame){
        super(executor, loggingLevel, measurementsFormatType, measurements, orbit_determination_uuid,
                spaceObjectId, observerIds, outputFrame);
    }

}
