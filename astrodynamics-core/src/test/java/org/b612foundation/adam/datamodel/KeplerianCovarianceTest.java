package org.b612foundation.adam.datamodel;

import org.b612foundation.adam.opm.KeplerianCovariance;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class KeplerianCovarianceTest {
  public static final double TOLERANCE = 1e-12;

  @Test
  public void testDefaultConstruction() {
    KeplerianCovariance cov = new KeplerianCovariance();
    assertEquals(0.0, cov.getCAA(), TOLERANCE);

    assertEquals(0.0, cov.getCEA(), TOLERANCE);
    assertEquals(0.0, cov.getCEE(), TOLERANCE);

    assertEquals(0.0, cov.getCIA(), TOLERANCE);
    assertEquals(0.0, cov.getCIE(), TOLERANCE);
    assertEquals(0.0, cov.getCII(), TOLERANCE);

    assertEquals(0.0, cov.getCOA(), TOLERANCE);
    assertEquals(0.0, cov.getCOE(), TOLERANCE);
    assertEquals(0.0, cov.getCOI(), TOLERANCE);
    assertEquals(0.0, cov.getCOO(), TOLERANCE);

    assertEquals(0.0, cov.getCWA(), TOLERANCE);
    assertEquals(0.0, cov.getCWE(), TOLERANCE);
    assertEquals(0.0, cov.getCWI(), TOLERANCE);
    assertEquals(0.0, cov.getCWO(), TOLERANCE);
    assertEquals(0.0, cov.getCWW(), TOLERANCE);

    assertEquals(0.0, cov.getCMA(), TOLERANCE);
    assertEquals(0.0, cov.getCME(), TOLERANCE);
    assertEquals(0.0, cov.getCMI(), TOLERANCE);
    assertEquals(0.0, cov.getCMO(), TOLERANCE);
    assertEquals(0.0, cov.getCMW(), TOLERANCE);
    assertEquals(0.0, cov.getCMM(), TOLERANCE);

    assertEquals(0.0, cov.getCTA(), TOLERANCE);
    assertEquals(0.0, cov.getCTE(), TOLERANCE);
    assertEquals(0.0, cov.getCTI(), TOLERANCE);
    assertEquals(0.0, cov.getCTO(), TOLERANCE);
    assertEquals(0.0, cov.getCTW(), TOLERANCE);
    assertEquals(0.0, cov.getCTT(), TOLERANCE);
  }

  @Test
  public void testGettersSetters() {
    double expected = 20.0;

    KeplerianCovariance cov = new KeplerianCovariance().setCAA(expected);
    assertEquals(expected, cov.getCAA(), TOLERANCE);

    cov = new KeplerianCovariance().setCEA(expected);
    assertEquals(expected, cov.getCEA(), TOLERANCE);
    cov = new KeplerianCovariance().setCEE(expected);
    assertEquals(expected, cov.getCEE(), TOLERANCE);

    cov = new KeplerianCovariance().setCIA(expected);
    assertEquals(expected, cov.getCIA(), TOLERANCE);
    cov = new KeplerianCovariance().setCIE(expected);
    assertEquals(expected, cov.getCIE(), TOLERANCE);
    cov = new KeplerianCovariance().setCII(expected);
    assertEquals(expected, cov.getCII(), TOLERANCE);

    cov = new KeplerianCovariance().setCOA(expected);
    assertEquals(expected, cov.getCOA(), TOLERANCE);
    cov = new KeplerianCovariance().setCOE(expected);
    assertEquals(expected, cov.getCOE(), TOLERANCE);
    cov = new KeplerianCovariance().setCOI(expected);
    assertEquals(expected, cov.getCOI(), TOLERANCE);
    cov = new KeplerianCovariance().setCOO(expected);
    assertEquals(expected, cov.getCOO(), TOLERANCE);

    cov = new KeplerianCovariance().setCWA(expected);
    assertEquals(expected, cov.getCWA(), TOLERANCE);
    cov = new KeplerianCovariance().setCWE(expected);
    assertEquals(expected, cov.getCWE(), TOLERANCE);
    cov = new KeplerianCovariance().setCWI(expected);
    assertEquals(expected, cov.getCWI(), TOLERANCE);
    cov = new KeplerianCovariance().setCWO(expected);
    assertEquals(expected, cov.getCWO(), TOLERANCE);
    cov = new KeplerianCovariance().setCWW(expected);
    assertEquals(expected, cov.getCWW(), TOLERANCE);

    cov = new KeplerianCovariance().setCMA(expected);
    assertEquals(expected, cov.getCMA(), TOLERANCE);
    cov = new KeplerianCovariance().setCME(expected);
    assertEquals(expected, cov.getCME(), TOLERANCE);
    cov = new KeplerianCovariance().setCMI(expected);
    assertEquals(expected, cov.getCMI(), TOLERANCE);
    cov = new KeplerianCovariance().setCMO(expected);
    assertEquals(expected, cov.getCMO(), TOLERANCE);
    cov = new KeplerianCovariance().setCMW(expected);
    assertEquals(expected, cov.getCMW(), TOLERANCE);
    cov = new KeplerianCovariance().setCMM(expected);
    assertEquals(expected, cov.getCMM(), TOLERANCE);

    cov = new KeplerianCovariance().setCTA(expected);
    assertEquals(expected, cov.getCTA(), TOLERANCE);
    cov = new KeplerianCovariance().setCTE(expected);
    assertEquals(expected, cov.getCTE(), TOLERANCE);
    cov = new KeplerianCovariance().setCTI(expected);
    assertEquals(expected, cov.getCTI(), TOLERANCE);
    cov = new KeplerianCovariance().setCTO(expected);
    assertEquals(expected, cov.getCTO(), TOLERANCE);
    cov = new KeplerianCovariance().setCTW(expected);
    assertEquals(expected, cov.getCTW(), TOLERANCE);
    cov = new KeplerianCovariance().setCTT(expected);
    assertEquals(expected, cov.getCTT(), TOLERANCE);
  }

  @Test
  public void testDeepCopy() {
    KeplerianCovariance cov =
        new KeplerianCovariance()
            .setCAA(1)
            .setCEA(2)
            .setCEE(3)
            .setCIA(4)
            .setCIE(5)
            .setCII(6)
            .setCOA(7)
            .setCOE(8)
            .setCOI(9)
            .setCOO(10)
            .setCWA(11)
            .setCWE(12)
            .setCWI(13)
            .setCWO(14)
            .setCWW(15)
            .setCMA(16)
            .setCME(17)
            .setCMI(18)
            .setCMO(19)
            .setCMW(20)
            .setCMM(21)
            .setCTA(16.1)
            .setCTE(17.1)
            .setCTI(18.1)
            .setCTO(19.1)
            .setCTW(20.1)
            .setCTT(21.1);

    KeplerianCovariance cov2 = cov.deepCopy();
    assertEquals(cov, cov2);
    cov.setCAA(10);
    assertNotEquals(cov, cov2);
  }
}
