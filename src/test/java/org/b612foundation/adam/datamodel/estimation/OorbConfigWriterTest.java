package org.b612foundation.adam.datamodel.estimation;

import org.b612foundation.adam.datamodel.PropagationConfigurationFactory;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;

public class OorbConfigWriterTest {

    @Test
    public void testDefaultConfigFile() throws IOException {
        var config = OorbEstimateConfigurationFactory.buildDefaultConfig();
        var tmpConfig = Files.createTempFile("oorb", ".conf");
        OorbConfigWriter.writeConfiguration(tmpConfig, config, PropagationConfigurationFactory.getAllMajorBodiesConfig());
        var lines = Files.readAllLines(tmpConfig);
        lines.forEach(System.out::println);
    }
}
