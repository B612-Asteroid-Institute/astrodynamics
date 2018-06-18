package org.b612foundation.adam.opm;

import org.junit.Assert;
import org.junit.Test;

public class OrbitParameterMessageTest {
  
  @Test
  public void testDeepCopy() {
    // Includes metadata, header, state vector, spacecraft.
    OrbitParameterMessage opmSimple = OdmScenarioBuilder.buildSimpleOpm();
    Assert.assertEquals(opmSimple, opmSimple.deepCopy());

    // Includes metadata, header, state vector, spacecraft, keplerian, and maneuvers.
    OrbitParameterMessage opmKeplerianAndManeuvers = OdmScenarioBuilder.buildOpmWithKepelerianAndManuevers();
    Assert.assertEquals(opmKeplerianAndManeuvers, opmKeplerianAndManeuvers.deepCopy());
    
    // Includes metadata, header, state vector, spacecraft, covariance, and ADAM fields.
    OrbitParameterMessage opmWithCovariance = OdmScenarioBuilder.buildOpmWithCovariance("FACES");
    Assert.assertEquals(opmWithCovariance, opmWithCovariance.deepCopy());
  }
  
}
