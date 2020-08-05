package org.b612foundation.adam.datamodel;

import org.b612foundation.adam.opm.KeplerianCovariance;
import org.junit.Assert;
import org.junit.Test;

public class KeplerianCovarianceTest {
  public static final double TOLERANCE = 1e-12;

  @Test
  public void testDefaultConstruction() {
    KeplerianCovariance cov = new KeplerianCovariance();
    Assert.assertEquals(0.0, cov.getCAA(), TOLERANCE);

    Assert.assertEquals(0.0, cov.getCEA(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCEE(), TOLERANCE);

    Assert.assertEquals(0.0, cov.getCIA(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCIE(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCII(), TOLERANCE);

    Assert.assertEquals(0.0, cov.getCOA(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCOE(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCOI(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCOO(), TOLERANCE);

    Assert.assertEquals(0.0, cov.getCWA(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCWE(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCWI(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCWO(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCWW(), TOLERANCE);

    Assert.assertEquals(0.0, cov.getCMA(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCME(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCMI(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCMO(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCMW(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCMM(), TOLERANCE);

    Assert.assertEquals(0.0, cov.getCTA(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCTE(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCTI(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCTO(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCTW(), TOLERANCE);
    Assert.assertEquals(0.0, cov.getCTT(), TOLERANCE);

  }

  @Test
  public void testGettersSetters() {
    double expected = 20.0;

    KeplerianCovariance cov = new KeplerianCovariance().setCAA(expected);
    Assert.assertEquals(expected, cov.getCAA(), TOLERANCE);

    cov = new KeplerianCovariance().setCEA(expected);
    Assert.assertEquals(expected, cov.getCEA(), TOLERANCE);
    cov = new KeplerianCovariance().setCEE(expected);
    Assert.assertEquals(expected, cov.getCEE(), TOLERANCE);

    cov = new KeplerianCovariance().setCIA(expected);
    Assert.assertEquals(expected, cov.getCIA(), TOLERANCE);
    cov = new KeplerianCovariance().setCIE(expected);
    Assert.assertEquals(expected, cov.getCIE(), TOLERANCE);
    cov = new KeplerianCovariance().setCII(expected);
    Assert.assertEquals(expected, cov.getCII(), TOLERANCE);

    cov = new KeplerianCovariance().setCOA(expected);
    Assert.assertEquals(expected, cov.getCOA(), TOLERANCE);
    cov = new KeplerianCovariance().setCOE(expected);
    Assert.assertEquals(expected, cov.getCOE(), TOLERANCE);
    cov = new KeplerianCovariance().setCOI(expected);
    Assert.assertEquals(expected, cov.getCOI(), TOLERANCE);
    cov = new KeplerianCovariance().setCOO(expected);
    Assert.assertEquals(expected, cov.getCOO(), TOLERANCE);

    cov = new KeplerianCovariance().setCWA(expected);
    Assert.assertEquals(expected, cov.getCWA(), TOLERANCE);
    cov = new KeplerianCovariance().setCWE(expected);
    Assert.assertEquals(expected, cov.getCWE(), TOLERANCE);
    cov = new KeplerianCovariance().setCWI(expected);
    Assert.assertEquals(expected, cov.getCWI(), TOLERANCE);
    cov = new KeplerianCovariance().setCWO(expected);
    Assert.assertEquals(expected, cov.getCWO(), TOLERANCE);
    cov = new KeplerianCovariance().setCWW(expected);
    Assert.assertEquals(expected, cov.getCWW(), TOLERANCE);

    cov = new KeplerianCovariance().setCMA(expected);
    Assert.assertEquals(expected, cov.getCMA(), TOLERANCE);
    cov = new KeplerianCovariance().setCME(expected);
    Assert.assertEquals(expected, cov.getCME(), TOLERANCE);
    cov = new KeplerianCovariance().setCMI(expected);
    Assert.assertEquals(expected, cov.getCMI(), TOLERANCE);
    cov = new KeplerianCovariance().setCMO(expected);
    Assert.assertEquals(expected, cov.getCMO(), TOLERANCE);
    cov = new KeplerianCovariance().setCMW(expected);
    Assert.assertEquals(expected, cov.getCMW(), TOLERANCE);
    cov = new KeplerianCovariance().setCMM(expected);
    Assert.assertEquals(expected, cov.getCMM(), TOLERANCE);

    cov = new KeplerianCovariance().setCTA(expected);
    Assert.assertEquals(expected, cov.getCTA(), TOLERANCE);
    cov = new KeplerianCovariance().setCTE(expected);
    Assert.assertEquals(expected, cov.getCTE(), TOLERANCE);
    cov = new KeplerianCovariance().setCTI(expected);
    Assert.assertEquals(expected, cov.getCTI(), TOLERANCE);
    cov = new KeplerianCovariance().setCTO(expected);
    Assert.assertEquals(expected, cov.getCTO(), TOLERANCE);
    cov = new KeplerianCovariance().setCTW(expected);
    Assert.assertEquals(expected, cov.getCTW(), TOLERANCE);
    cov = new KeplerianCovariance().setCTT(expected);
    Assert.assertEquals(expected, cov.getCTT(), TOLERANCE);
  }

}
