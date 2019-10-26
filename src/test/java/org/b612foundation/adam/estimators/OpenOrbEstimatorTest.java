package org.b612foundation.adam.estimators;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.datamodel.estimation.OorbEstimateConfigurationFactory;
import org.b612foundation.adam.datamodel.estimation.OrbitDeterminationConfiguration;
import org.b612foundation.adam.datamodel.estimation.OrbitDeterminationParameters;
import org.b612foundation.adam.opm.CovarianceMatrix;
import org.b612foundation.adam.opm.OdmCommonMetadata;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.opm.StateVector;
import org.b612foundation.adam.runnable.AdamRunnableException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OpenOrbEstimatorTest {
    private PropagatorConfiguration allMajorBodiesConfig =
            new PropagatorConfiguration()
                    .setSun(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setMercury(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setVenus(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setEarth(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setMars(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setJupiter(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setSaturn(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setUranus(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setNeptune(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setPluto(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setMoon(PropagatorConfiguration.PlanetGravityMode.POINT_MASS);

    @Test
    public void testIodHappyPath() throws IOException, AdamRunnableException {
        Path executionDir = Files.createTempDirectory("oorb_estimator_test");
        Path inputObsPath = Paths.get("src/test/resources/single_object_lsst_iod.csv");
        OrbitDeterminationParameters odParams = new OrbitDeterminationParameters();
        odParams.setMeasurementsFormatType(OrbitDeterminationParameters.MeasurumentType.LsstCsvFilePath.toString());
        odParams.setMeasurements(inputObsPath.toString());
        odParams.setType(OrbitDeterminationParameters.OdType.Initial);
        OrbitDeterminationConfiguration config = OorbEstimateConfigurationFactory.buildDefaultConfig();
        OpenOrbEstimator estimator = new OpenOrbEstimator(executionDir);
        OrbitParameterMessage opm = estimator.batchEstimate(odParams, config, allMajorBodiesConfig, "aoeu");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(opm));
        //TODO: Add numerical check once validation complete
    }

    @Test
    public void testOdHappyPath() throws IOException, AdamRunnableException {
        Path executionDir = Files.createTempDirectory("oorb_estimator_test");
        Path inputObsPath = Paths.get("src/test/resources/single_object_lsst_fullod.csv");
        OrbitDeterminationParameters odParams = new OrbitDeterminationParameters();
        odParams.setMeasurementsFormatType(OrbitDeterminationParameters.MeasurumentType.LsstCsvFilePath.toString());
        odParams.setMeasurements(inputObsPath.toString());
        odParams.setType(OrbitDeterminationParameters.OdType.Full);
        OrbitDeterminationConfiguration config = OorbEstimateConfigurationFactory.buildDefaultConfig();
        StateVector stateVector = new StateVector()
                .setEpoch("2059-05-20T00:00:00")
                .setX(-2.0288576146134263E8).setY(-3.552819412109843E8).setZ(-8.780151840932983E7)
                .setX_dot(8.97012070187471).setY_dot(-9.185698176609113).setZ_dot(1.221119275526078);
        CovarianceMatrix cv = new CovarianceMatrix()
                .setCx_x(2.2273772749882252E15)
                .setCy_y(4.0976100416513555E15)
                .setCz_z(1.587630643961312E15)
                .setCx_dot_x_dot(3208.104574162037)
                .setCy_dot_y_dot(2680.7790289748573)
                .setCz_dot_z_dot(863.6674448092298);

        OdmCommonMetadata metadata = new OdmCommonMetadata()
                .setCenter_name(OdmCommonMetadata.CenterName.SUN)
                .setTime_system(OdmCommonMetadata.TimeSystem.UTC)
                .setRef_frame(OdmCommonMetadata.ReferenceFrame.GCRF)
                .setObject_id("5021")
                .setObject_name("5021");
        OrbitParameterMessage inputOpm = new OrbitParameterMessage()
                .setMetadata(metadata)
                .setState_vector(stateVector)
                .setCovariance(cv);
        odParams.setInitialStateEstimate(inputOpm);
        OpenOrbEstimator estimator = new OpenOrbEstimator(executionDir);
        OrbitParameterMessage outputOpm = estimator.batchEstimate(odParams, config, allMajorBodiesConfig, "aoeu");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(outputOpm));
        //TODO: Add numerical check once validation complete
    }
}
