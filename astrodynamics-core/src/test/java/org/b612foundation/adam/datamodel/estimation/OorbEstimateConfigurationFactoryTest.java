package org.b612foundation.adam.datamodel.estimation;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class OorbEstimateConfigurationFactoryTest {

  @Test
  public void testDefaultConfigHappy() {
    assertNotNull(OorbEstimateConfigurationFactory.buildDefaultConfig());
  }
}
