package org.b612foundation.adam.datamodel.estimation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Data
@ToString(callSuper = true)
public class InitialOrbitDeterminationConfiguration extends BaseOrbitDeterminationConfiguration {
    private int numberOfSamplePoints;

    private double samplingPointIntervalDays;

    private double minimumSmaAU;

    private double maximumSmaAU;

    @Builder
    public InitialOrbitDeterminationConfiguration(String propgator_uuid,
                                                   double forwardPropagationDurationDays,
                                                   double observationSamplingWindowDays,
                                                   int maxIterations,
                                                  int numberOfSamplePoints,
                                                  double samplingPointIntervalDays,
                                                  double minimumSmaAU,
                                                  double maximumSmaAU){
        super(propgator_uuid, forwardPropagationDurationDays, observationSamplingWindowDays, maxIterations);
        this.numberOfSamplePoints = numberOfSamplePoints;
        this.samplingPointIntervalDays = samplingPointIntervalDays;
        this.minimumSmaAU = minimumSmaAU;
        this.maximumSmaAU = maximumSmaAU;

    }

}
