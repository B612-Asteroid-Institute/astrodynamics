package org.b612foundation.adam.datamodel.estimation;

import org.b612foundation.adam.datamodel.PropagationConfigurationFactory;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OorbConfigWriterTest {

    @Test
    public void testDefaultConfigFile() throws IOException {
        var config = OorbEstimateConfigurationFactory.buildDefaultConfig();
        var tmpConfig = Files.createTempFile("oorb", ".conf");
        OorbConfigWriter.writeConfiguration(tmpConfig, config, PropagationConfigurationFactory.getAllMajorBodiesConfig());
        var lines = Files.readAllLines(tmpConfig);
        lines.forEach(System.out::println);
    }

    @Test
    public void testIsTwoBody()
    {
        PropagatorConfiguration fullConfig = PropagationConfigurationFactory.getAllMajorBodiesConfig();
        assertFalse(OorbConfigWriter.isTwoBody(fullConfig));
        PropagatorConfiguration twoBody = PropagationConfigurationFactory.getSunOnlyConfig();
        assertTrue(OorbConfigWriter.isTwoBody(twoBody));
    }
}
