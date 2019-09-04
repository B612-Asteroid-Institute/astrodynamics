package org.b612foundation.adam.datamodel.estimation;

import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class OdDataExamplesTest {

    @Test
    public void configuringInputDataExample() {
        var desRawObsFileText =
                "433     54617.0000000000 O  78.2887200000  26.5336200000  13.5600000000 X  568   0.0010000000   0.0000000000  -1.0000000000 -0.1000000E+01 X\n" +
                "433     54624.0000000000 O  85.2759500000  26.3621300000  13.5200000000 X  568   0.0010000000   0.0000000000  -1.0000000000 -0.1000000E+01 X\n" +
                "433     54631.0000000000 O  92.2911900000  25.8549400000  13.4800000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n" +
                "433     54638.0000000000 O  99.2839000000  25.0127500000  13.4500000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n" +
                "433     54645.0000000000 O 106.2096100000  23.8451900000  13.4300000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n" +
                "433     54652.0000000000 O 113.0300000000  22.3700600000  13.4100000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n" +
                "433     54659.0000000000 O 119.7126800000  20.6130900000  13.4000000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n" +
                "433     54666.0000000000 O 126.2366900000  18.6056500000  13.4000000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n" +
                "433     54673.0000000000 O 132.5936500000  16.3820000000  13.4100000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X";
        var base64ObsFile = Base64.getEncoder().encodeToString(desRawObsFileText.getBytes());
        //String desObsFileFromBase64 = new String(Base64.getDecoder().decode(base64ObsFile));

        var propagatorConfig = new PropagatorConfiguration()
                .setEarth(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                .setSun(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                .setMoon(PropagatorConfiguration.PlanetGravityMode.POINT_MASS);

        var iodConfig = InitialOrbitDeterminationConfiguration.builder()
                .observationSamplingWindowDays(7)
                .forwardPropagationDurationDays(60)
                .maxIterations(100)
                .propgator_uuid(propagatorConfig.getUuid())
                .minimumSmaAU(0.1)
                .maximumSmaAU(100)
                .numberOfSamplePoints(4)
                .samplingPointIntervalDays(1)
                .build();

        var fullOdConfig = FullOrbitDeterminationConfiguration.builder()
                .forwardPropagationDurationDays(60)
                .maxIterations(10)
                .observationSamplingWindowDays(30)
                .propgator_uuid(propagatorConfig.getUuid())
                .build();

        var initialOdParams = InitialOrbitDeterminationParameters.builder()
                .executor("Thor")
                .loggingLevel(5)
                .measurementsFormatType("DES")
                .measurements(base64ObsFile)
                .observerIds(Arrays.asList("568"))
                .spaceObjectId("433")
                .outputFrame("J2000")
                .orbit_determination_uuid(iodConfig.getUuid())
                .build();

        var fullOdParams = FullOrbitDeterminationParameters.builder()
                .executor("OpenOrb")
                .loggingLevel(3)
                .measurementsFormatType("DES")
                .measurements(base64ObsFile)
                .observerIds(Arrays.asList("568"))
                .spaceObjectId("433")
                .outputFrame("J2000")
                .orbit_determination_uuid(fullOdConfig.getUuid())
                .build();

        System.out.println("Init OD Configurations = " + initialOdParams.toString());
        System.out.println("Full OD Configurations = " + fullOdParams.toString());
    }
}
