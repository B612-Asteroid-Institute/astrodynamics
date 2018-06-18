package org.b612foundation.adam.opm;

import org.junit.Assert;
import org.junit.Test;

public class OrbitParameterMessageTest {

  @Test
  public void sanityCheckEqualsAndHashcode() {
    // Includes metadata, header, state vector, spacecraft.
    OrbitParameterMessage opmSimple = OdmScenarioBuilder.buildSimpleOpm();
    Assert.assertEquals(OdmScenarioBuilder.buildSimpleOpm(), opmSimple);
    Assert.assertEquals(OdmScenarioBuilder.buildSimpleOpm().hashCode(), opmSimple.hashCode());
    opmSimple.setCcsds_opm_vers("different");
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm(), opmSimple);
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm().hashCode(), opmSimple.hashCode());
    opmSimple = OdmScenarioBuilder.buildSimpleOpm();
    opmSimple.addAdam_field("a", "b");
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm(), opmSimple);
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm().hashCode(), opmSimple.hashCode());
    opmSimple = OdmScenarioBuilder.buildSimpleOpm();
    opmSimple.getMetadata().addComment("new");
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm(), opmSimple);
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm().hashCode(), opmSimple.hashCode());
    opmSimple = OdmScenarioBuilder.buildSimpleOpm();
    opmSimple.getHeader().addComment("new");
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm(), opmSimple);
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm().hashCode(), opmSimple.hashCode());
    opmSimple = OdmScenarioBuilder.buildSimpleOpm();
    opmSimple.getSpacecraft().addComment("new");
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm(), opmSimple);
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm().hashCode(), opmSimple.hashCode());
    opmSimple = OdmScenarioBuilder.buildSimpleOpm();
    opmSimple.getState_vector().addComment("new");
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm(), opmSimple);
    Assert.assertNotEquals(OdmScenarioBuilder.buildSimpleOpm().hashCode(), opmSimple.hashCode());

    // Also has keplerian and maneuvers.
    OrbitParameterMessage opmKeplerianAndManeuvers = OdmScenarioBuilder.buildOpmWithKepelerianAndManuevers();
    Assert.assertEquals(OdmScenarioBuilder.buildOpmWithKepelerianAndManuevers(), opmKeplerianAndManeuvers);
    Assert.assertEquals(OdmScenarioBuilder.buildOpmWithKepelerianAndManuevers().hashCode(),
        opmKeplerianAndManeuvers.hashCode());
    opmKeplerianAndManeuvers.getKeplerian().addComment("new");
    Assert.assertNotEquals(OdmScenarioBuilder.buildOpmWithKepelerianAndManuevers(), opmKeplerianAndManeuvers);
    Assert.assertNotEquals(OdmScenarioBuilder.buildOpmWithKepelerianAndManuevers().hashCode(),
        opmKeplerianAndManeuvers.hashCode());
    opmKeplerianAndManeuvers = OdmScenarioBuilder.buildOpmWithKepelerianAndManuevers();
    opmKeplerianAndManeuvers.getManuevers().get(0).addComment("new");
    Assert.assertNotEquals(OdmScenarioBuilder.buildOpmWithKepelerianAndManuevers(), opmKeplerianAndManeuvers);
    Assert.assertNotEquals(OdmScenarioBuilder.buildOpmWithKepelerianAndManuevers().hashCode(),
        opmKeplerianAndManeuvers.hashCode());

    // Also has covariance and ADAM fields.
    OrbitParameterMessage opmWithCovariance = OdmScenarioBuilder.buildOpmWithCovariance("FACES");
    Assert.assertEquals(OdmScenarioBuilder.buildOpmWithCovariance("FACES"), opmWithCovariance);
    Assert.assertEquals(OdmScenarioBuilder.buildOpmWithCovariance("FACES").hashCode(), opmWithCovariance.hashCode());
    opmWithCovariance.getCovariance().addComment("new");
    Assert.assertNotEquals(OdmScenarioBuilder.buildOpmWithCovariance("FACES"), opmWithCovariance);
    Assert.assertNotEquals(OdmScenarioBuilder.buildOpmWithCovariance("FACES").hashCode(), opmWithCovariance.hashCode());
    opmWithCovariance = OdmScenarioBuilder.buildOpmWithCovariance("FACES");
    opmWithCovariance.getAdam_fields().remove(0);
    Assert.assertNotEquals(OdmScenarioBuilder.buildOpmWithCovariance("FACES"), opmWithCovariance);
    Assert.assertNotEquals(OdmScenarioBuilder.buildOpmWithCovariance("FACES").hashCode(), opmWithCovariance.hashCode());
    opmWithCovariance = OdmScenarioBuilder.buildOpmWithCovariance("FACES");
    opmWithCovariance.getAdam_fields().get(0).setValue("new");
    Assert.assertNotEquals(OdmScenarioBuilder.buildOpmWithCovariance("FACES"), opmWithCovariance);
    Assert.assertNotEquals(OdmScenarioBuilder.buildOpmWithCovariance("FACES").hashCode(), opmWithCovariance.hashCode());
  }

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
