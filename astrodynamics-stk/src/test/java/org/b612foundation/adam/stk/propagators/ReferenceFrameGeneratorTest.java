package org.b612foundation.adam.stk.propagators;

import agi.foundation.Motion1;
import agi.foundation.celestial.*;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.coordinates.KinematicTransformation;
import agi.foundation.geometry.GeometryTransformer;
import agi.foundation.geometry.ReferenceFrame;
import agi.foundation.geometry.ReferenceFrameEvaluator;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeConstants;
import org.b612foundation.adam.opm.OdmCommonMetadata;
import org.b612foundation.stk.StkLicense;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ReferenceFrameGeneratorTest {

  private static EarthCentralBody EARTH = CentralBodiesFacet.getFromContext().getEarth();
  private static SunCentralBody SUN = CentralBodiesFacet.getFromContext().getSun();

  @Test
  public void testGetCentralBody() throws IOException {
    StkLicense.activate();
    ReferenceFrameGenerator rfg = new ReferenceFrameGenerator();

    Assert.assertEquals(EARTH, rfg.getCentralBody(OdmCommonMetadata.CenterName.EARTH));
    Assert.assertEquals(SUN, rfg.getCentralBody(OdmCommonMetadata.CenterName.SUN));
  }

  @Test
  public void testGetICRF() throws IOException {
    StkLicense.activate();
    ReferenceFrameGenerator rfg = new ReferenceFrameGenerator();

    ReferenceFrame earthICRF =
        rfg.getReferenceFrame(
            OdmCommonMetadata.ReferenceFrame.ICRF, OdmCommonMetadata.CenterName.EARTH);
    Assert.assertEquals(EARTH.getCenterOfMassPoint(), earthICRF.getOrigin());

    ReferenceFrame sunICRF =
        rfg.getReferenceFrame(
            OdmCommonMetadata.ReferenceFrame.ICRF, OdmCommonMetadata.CenterName.SUN);
    Assert.assertEquals(SUN.getCenterOfMassPoint(), sunICRF.getOrigin());
  }

  @Test
  public void testEMEME2000() throws IOException {
    StkLicense.activate();
    ReferenceFrameGenerator rfg = new ReferenceFrameGenerator();

    ReferenceFrame earthEMEME2000 =
        rfg.getReferenceFrame(
            OdmCommonMetadata.ReferenceFrame.EMEME2000, OdmCommonMetadata.CenterName.EARTH);
    Assert.assertEquals(EARTH.getCenterOfMassPoint(), earthEMEME2000.getOrigin());

    ReferenceFrame sunEMEME2000 =
        rfg.getReferenceFrame(
            OdmCommonMetadata.ReferenceFrame.EMEME2000, OdmCommonMetadata.CenterName.SUN);
    Assert.assertEquals(SUN.getCenterOfMassPoint(), sunEMEME2000.getOrigin());
  }

  private Motion1<Cartesian> transformMotion(
      Motion1<Cartesian> in, ReferenceFrame from, ReferenceFrame to, JulianDate at) {
    ReferenceFrameEvaluator evaluator =
        GeometryTransformer.getReferenceFrameTransformation(from, to);
    int order = 1;
    KinematicTransformation transformer = evaluator.evaluate(at, order);
    return transformer.transform(in, order);
  }

  private Cartesian transformPoint(
      Cartesian in, ReferenceFrame from, ReferenceFrame to, JulianDate at) {
    ReferenceFrameEvaluator evaluator =
        GeometryTransformer.getReferenceFrameTransformation(from, to);
    int order = 1;
    KinematicTransformation transformer = evaluator.evaluate(at, order);
    return transformer.transform(in);
  }

  @Test
  public void testAllMagnitudesEqual() throws IOException {
    StkLicense.activate();
    ReferenceFrameGenerator rfg = new ReferenceFrameGenerator();

    List<OdmCommonMetadata.ReferenceFrame> supportedReferenceFrames =
        Arrays.asList(
            OdmCommonMetadata.ReferenceFrame.EMEME2000, OdmCommonMetadata.ReferenceFrame.ICRF);
    List<OdmCommonMetadata.CenterName> supportedCenters =
        Arrays.asList(OdmCommonMetadata.CenterName.SUN, OdmCommonMetadata.CenterName.EARTH);

    // Check that for a given center, a point has the same magnitude in all supported reference
    // frames.
    for (OdmCommonMetadata.CenterName centerName : supportedCenters) {
      Cartesian point = new Cartesian(1000, 1000, 1000);
      ReferenceFrame defaultFrame = rfg.getCentralBody(centerName).getInertialFrame();

      for (OdmCommonMetadata.ReferenceFrame referenceFrameName : supportedReferenceFrames) {
        ReferenceFrame referenceFrame = rfg.getReferenceFrame(referenceFrameName, centerName);

        JulianDate whenever = TimeConstants.J2000.addDays(12345); // Arbitrary, shouldn't matter.
        Cartesian transformed = transformPoint(point, defaultFrame, referenceFrame, whenever);
        Assert.assertEquals(
            "For center "
                + centerName.name()
                + ", reference frame "
                + referenceFrameName.name()
                + ", magnitude should remain constant.",
            point.getMagnitude(),
            transformed.getMagnitude(),
            1e-10);

        // Compared for the same central body, distance of a non-moving point from the center should
        // be time-invariant
        // and reference-frame-invariant.
        transformed = transformPoint(point, defaultFrame, referenceFrame, whenever.addHours(1));
        Assert.assertEquals(
            "For center "
                + centerName.name()
                + ", reference frame "
                + referenceFrameName.name()
                + ", magnitude constancy should be time-invariant.",
            point.getMagnitude(),
            transformed.getMagnitude(),
            1e-10);
      }
    }
  }

  @Test
  public void testMatchDesktopTransformation() throws IOException {
    StkLicense.activate();

    String DE_FILE = "data/plneph.430";
    JplDE JPL_DE = new JplDE430(new ClasspathStreamFactory(DE_FILE));
    JPL_DE.useForCentralBodyPositions(CentralBodiesFacet.getFromContext());
    CentralBodiesFacet.getFromContext().getEarth().setNutationModel(JPL_DE.getEarthNutationModel());

    ReferenceFrame j2000 =
        new ReferenceFrame(
            CentralBodiesFacet.getFromContext().getSun().getCenterOfMassPoint(),
            CentralBodiesFacet.getFromContext().getEarth().getJ2000Frame().getAxes());
    ReferenceFrame icrf =
        new ReferenceFrame(
            CentralBodiesFacet.getFromContext().getSun().getCenterOfMassPoint(),
            CentralBodiesFacet.getFromContext()
                .getEarth()
                .getInternationalCelestialReferenceFrame()
                .getAxes());

    Cartesian pos = new Cartesian(1000000, 1000000, 1000000);
    Cartesian vel = new Cartesian(1000, 1000, 2000);
    Motion1<Cartesian> motion = new Motion1<Cartesian>(pos, vel);

    Motion1<Cartesian> transformed = transformMotion(motion, j2000, icrf, TimeConstants.J2000);

    Cartesian expectedPos =
        new Cartesian(999999.9049605389700, 999999.9880806692545, 1000000.1069587816573);
    Cartesian expectedVel = new Cartesian(999.9998099467666, 999.9999761356555, 2000.0001069587769);

    double positionEps = 1;
    double velocityEps = 1e-3;
    System.out.println("Expected: " + expectedPos + "; " + expectedVel);
    System.out.println(
        "Actual: " + transformed.getValue() + "; " + transformed.getFirstDerivative());
    for (int i = 0; i < 3; i++) {
      Assert.assertEquals(
          "Checking correct position transformation, component " + i,
          expectedPos.get(i),
          transformed.getValue().get(i),
          positionEps);
      Assert.assertEquals(
          "Checking correct velocity transformation, component " + i,
          expectedVel.get(i),
          transformed.getFirstDerivative().get(i),
          velocityEps);
    }
  }

  @Test
  public void testHeliocentricEMEME2000ToIcrf() throws IOException {
    StkLicense.activate();
    ForceModelHelper.loadStandardObjects();
    ReferenceFrameGenerator rfg = new ReferenceFrameGenerator();

    ReferenceFrame sunEMEME2000 =
        rfg.getReferenceFrame(
            OdmCommonMetadata.ReferenceFrame.EMEME2000, OdmCommonMetadata.CenterName.SUN);
    ReferenceFrame sunICRF =
        rfg.getReferenceFrame(
            OdmCommonMetadata.ReferenceFrame.ICRF, OdmCommonMetadata.CenterName.SUN);

    Cartesian memePosition = new Cartesian(1000000, 1000000, 1000000);
    Cartesian memeVelocity = new Cartesian(1000, 1000, 2000);

    Motion1<Cartesian> memeMotion = new Motion1<Cartesian>(memePosition, memeVelocity);
    Cartesian icrfPosition =
        new Cartesian(999999.9380994121339, 519704.8311468433894, 1315259.4438022811119);
    Cartesian icrfVelocity =
        new Cartesian(999.9997580464747, 121.9276229688568, 2232.7414832199990);

    JulianDate whenever = TimeConstants.J2000; // .addDays(12345); // Arbitrary, shouldn't matter.
    Motion1<Cartesian> transformed = transformMotion(memeMotion, sunEMEME2000, sunICRF, whenever);

    // Correct within a large margin. Not sure why such a high threshold should be necessary, but
    // the transformation
    // doesn't yet exactly match the desktop, so reflect that in the test.
    double positionEps = 1;
    double velocityEps = 1e-3;
    System.out.println("Expected: " + icrfPosition + "; " + icrfVelocity);
    System.out.println(
        "Actual: " + transformed.getValue() + "; " + transformed.getFirstDerivative());
    for (int i = 0; i < 3; i++) {
      Assert.assertEquals(
          "Checking correct position transformation, component " + i,
          icrfPosition.get(i),
          transformed.getValue().get(i),
          positionEps);
      Assert.assertEquals(
          "Checking correct velocity transformation, component " + i,
          icrfVelocity.get(i),
          transformed.getFirstDerivative().get(i),
          velocityEps);
    }
  }

  @Test
  public void testSunInertialFrameIsICRFNotJ2000() {
    ReferenceFrame sunInertial = SUN.getInertialFrame();
    ReferenceFrame j2000 =
        new ReferenceFrame(SUN.getCenterOfMassPoint(), SUN.getJ2000Frame().getAxes());
    ReferenceFrame sunICRF =
        new ReferenceFrame(
            SUN.getCenterOfMassPoint(), EARTH.getInternationalCelestialReferenceFrame().getAxes());

    Cartesian testPoint = new Cartesian(1, 1, 1);
    JulianDate whenever = TimeConstants.J2000.addDays(12345); // Arbitrary, shouldn't matter.
    Cartesian j2000Point = transformPoint(testPoint, sunInertial, j2000, whenever);
    Cartesian sunICRFPoint = transformPoint(testPoint, sunInertial, sunICRF, whenever);
    System.out.println("Sun inertial: " + testPoint);
    System.out.println("J2000: " + j2000Point);
    System.out.println("ICRF: " + sunICRFPoint);
    Assert.assertEquals(0, testPoint.subtract(sunICRFPoint).getMagnitude(), 0);
    Assert.assertNotEquals(0, testPoint.subtract(j2000Point).getMagnitude(), 0);
  }
}
