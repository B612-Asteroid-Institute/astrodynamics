package org.b612foundation.adam.opm;

import org.junit.Assert;
import org.junit.Test;

public class OdmFormatterTest {
  @Test
  public void testParseSimpleOpmHappy() throws Exception {
    OrbitParameterMessage expected = OdmScenarioBuilder.buildSimpleOpm();
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(OdmScenarioBuilder.getSimpleOpm());
    Assert.assertEquals(parsed, expected);
  }

  @Test
  public void testParseKeplerianAndManueversOpmHappy() throws Exception {
    OrbitParameterMessage expected = OdmScenarioBuilder.buildOpmWithKepelerianAndManuevers();
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(OdmScenarioBuilder.getOpmWithKepelerianAndManuevers());
    Assert.assertEquals(parsed, expected);
  }

  @Test
  public void testCovarianceOpmHappy() throws Exception {
    final String type = "FACES";
    OrbitParameterMessage expected = OdmScenarioBuilder.buildOpmWithCovariance(type);
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(OdmScenarioBuilder.getOpmWithCovariance(type));
    Assert.assertEquals(parsed, expected);
  }
}
