package org.b612foundation.adam.datamodel.estimation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Configurations specific to full orbit determination execution
 */

@AllArgsConstructor
@Data
@ToString(callSuper = true)
public class FullOrbitDeterminationParameters extends BaseOrbitDeterminationParameters {

    @Builder
    public FullOrbitDeterminationParameters(
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
