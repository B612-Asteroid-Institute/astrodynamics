package org.b612foundation.adam.datamodel.estimation;

import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;

public class OorbConfigWriterTest {
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

    private PropagatorConfiguration justTheSunConfig =
            new PropagatorConfiguration()
                    .setSun(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
                    .setMercury(PropagatorConfiguration.PlanetGravityMode.OMIT)
                    .setVenus(PropagatorConfiguration.PlanetGravityMode.OMIT)
                    .setEarth(PropagatorConfiguration.PlanetGravityMode.OMIT)
                    .setMars(PropagatorConfiguration.PlanetGravityMode.OMIT)
                    .setJupiter(PropagatorConfiguration.PlanetGravityMode.OMIT)
                    .setSaturn(PropagatorConfiguration.PlanetGravityMode.OMIT)
                    .setUranus(PropagatorConfiguration.PlanetGravityMode.OMIT)
                    .setNeptune(PropagatorConfiguration.PlanetGravityMode.OMIT)
                    .setPluto(PropagatorConfiguration.PlanetGravityMode.OMIT)
                    .setMoon(PropagatorConfiguration.PlanetGravityMode.OMIT);

    @Test
    public void testDefaultConfigFile() throws IOException {
        var config = OorbEstimateConfigurationFactory.buildDefaultConfig();
        var tmpConfig = Files.createTempFile("oorb", ".conf");
        OorbConfigWriter.writeConfiguration(tmpConfig, config, allMajorBodiesConfig);
        var lines = Files.readAllLines(tmpConfig);
        lines.forEach(System.out::println);
    }
}
