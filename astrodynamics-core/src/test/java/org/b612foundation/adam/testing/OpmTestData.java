package org.b612foundation.adam.testing;


import org.b612foundation.adam.astro.AstroConstants;
import org.b612foundation.adam.opm.*;

/** Used in {@link MonteCarloGeneratorTest} */
public final class OpmTestData {

  // Sigmas for Monte Carlo testing
  // Keplerian sigma is from John's Monte Carlo Jupyter notebook
  public static final KeplerianElements KEPLERIAN_SIGMA_TA =
      new KeplerianElements()
          .setSemi_major_axis(0.0001 * AstroConstants.AU_TO_KM)
          .setEccentricity(0.00001)
          .setInclination(0.01)
          .setRa_of_asc_node(0.02)
          .setArg_of_pericenter(0.03)
          .setTrue_anomaly(0.03)
          .setGm(AstroConstants.GM);

  public static final KeplerianElements KEPLERIAN_SIGMA_MA =
      new KeplerianElements()
          .setSemi_major_axis(11967.8)
          .setEccentricity(1e-4)
          .setInclination(1e-1)
          .setRa_of_asc_node(8e-2)
          .setArg_of_pericenter(8e-2)
          .setMean_anomaly(3e-2)
          .setGm(AstroConstants.GM);

  /**
   * From a NEODyS asteroid (but not related to states used, just need a real matrix so math works
   * out) *
   */
  public static final KeplerianCovariance KEPLERIAN_COVARIANCE_MA =
      new KeplerianCovariance()
          .setCAA(3.94346903514E+03)
          .setCEA(-1.40266786788E-04)
          .setCEE(5.00812620000E-12)
          .setCIA(-2.91357694324E-04)
          .setCIE(1.06017205000E-11)
          .setCII(3.15658331000E-11)
          .setCOA(-3.83826656095E-03)
          .setCOE(1.40431472000E-10)
          .setCOI(2.32155752000E-09)
          .setCOO(8.81161492000E-07)
          .setCWA(-1.09220523817E-02)
          .setCWE(3.62452521000E-10)
          .setCWI(-1.53067748000E-09)
          .setCWO(-8.70304198000E-07)
          .setCWW(9.42413982000E-07)
          .setCMA(-2.96713683611E-01)
          .setCME(1.05830167000E-08)
          .setCMI(2.23110293000E-08)
          .setCMO(2.93564832000E-07)
          .setCMW(7.81029359000E-07)
          .setCMM(2.23721205000E-05);

  public static final KeplerianCovariance KEPLERIAN_COVARIANCE_TA =
      new KeplerianCovariance()
          .setCAA(3.94346903514E+03)
          .setCEA(-1.40266786788E-04)
          .setCEE(5.00812620000E-12)
          .setCIA(-2.91357694324E-04)
          .setCIE(1.06017205000E-11)
          .setCII(3.15658331000E-11)
          .setCOA(-3.83826656095E-03)
          .setCOE(1.40431472000E-10)
          .setCOI(2.32155752000E-09)
          .setCOO(8.81161492000E-07)
          .setCWA(-1.09220523817E-02)
          .setCWE(3.62452521000E-10)
          .setCWI(-1.53067748000E-09)
          .setCWO(-8.70304198000E-07)
          .setCWW(9.42413982000E-07)
          .setCTA(-2.96713683611E-01)
          .setCTE(1.05830167000E-08)
          .setCTI(2.23110293000E-08)
          .setCTO(2.93564832000E-07)
          .setCTW(7.81029359000E-07)
          .setCTT(2.23721205000E-05);

  public static final StateVector CARTESIAN_SIGMA =
      new StateVector()
          .setX(1e-3)
          .setY(1e-3)
          .setZ(1e-3)
          .setX_dot(5e-4)
          .setY_dot(5e-4)
          .setZ_dot(5e-4);

  /** From Jupiter Notebook Example * */
  public static final CartesianCovariance CARTESIAN_COVARIANCE =
      new CartesianCovariance()
          .setCx_x(3.331349476038534e-04)
          .setCy_x(4.618927349220216e-04)
          .setCy_y(6.782421679971363e-04)
          .setCz_x(-3.070007847730449e-04)
          .setCz_y(-4.221234189514228e-04)
          .setCz_z(3.231931992380369e-04)
          .setCx_dot_x(-3.349365033922630e-07)
          .setCx_dot_y(-4.686084221046758e-07)
          .setCx_dot_z(2.484949578400095e-07)
          .setCx_dot_x_dot(4.296022805587290e-10)
          .setCy_dot_x(-2.211832501084875e-07)
          .setCy_dot_y(-2.864186892102733e-07)
          .setCy_dot_z(1.798098699846038e-07)
          .setCy_dot_x_dot(2.608899201686016e-10)
          .setCy_dot_y_dot(1.767514756338532e-10)
          .setCz_dot_x(-3.041346050686871e-07)
          .setCz_dot_y(-4.989496988610662e-07)
          .setCz_dot_z(3.540310904497689e-07)
          .setCz_dot_x_dot(1.869263192954590e-10)
          .setCz_dot_y_dot(1.008862586240695e-10)
          .setCz_dot_z_dot(6.224444338635500e-10);

  /** Builds OPM with Keplerian elements from John's Monte Carlo Jupyter notebook. */
  public static OrbitParameterMessage buildOpmWithKeplerianTA() {
    OrbitParameterMessage result = new OrbitParameterMessage();
    result.setCcsds_opm_vers("2.0");
    result.setHeader(new OdmCommonHeader().addComment("Testing"));
    result.setMetadata(
        new OdmCommonMetadata()
            .setObject_name("example_from_John")
            .setObject_id("example_from_John")
            .setCenter_name(OdmCommonMetadata.CenterName.SUN)
            .setRef_frame(OdmCommonMetadata.ReferenceFrame.ICRF)
            .setTime_system(OdmCommonMetadata.TimeSystem.UTC));
    result.setState_vector(
        new StateVector().addComment("State Vector").setEpoch("2028-11-01T00:00:00Z"));
    result.setKeplerian(
        new KeplerianElements()
            .addComment("Test Keplerian elements from John")
            .setSemi_major_axis(333918935.01803308725)
            .setEccentricity(0.5836725969822691)
            .setInclination(0.889253120260506)
            .setRa_of_asc_node(38.46053156164339)
            .setArg_of_pericenter(34.36938785036925)
            .setTrue_anomaly(136.4013846143255)
            .setGm(398600.4415));
    return result;
  }

  public static OrbitParameterMessage buildOpmWithKeplerianMA() {
    OrbitParameterMessage result = new OrbitParameterMessage();
    result.setCcsds_opm_vers("2.0");
    result.setHeader(new OdmCommonHeader().addComment("Testing"));
    result.setMetadata(
        new OdmCommonMetadata()
            .setObject_name("example_from_John")
            .setObject_id("example_from_John")
            .setCenter_name(OdmCommonMetadata.CenterName.SUN)
            .setRef_frame(OdmCommonMetadata.ReferenceFrame.ICRF)
            .setTime_system(OdmCommonMetadata.TimeSystem.UTC));
    result.setState_vector(
        new StateVector().addComment("State Vector").setEpoch("2028-11-01T00:00:00Z"));
    result.setKeplerian(
        new KeplerianElements()
            .addComment("Test Keplerian elements from John")
            .setSemi_major_axis(333918935.01803308725)
            .setEccentricity(0.5836725969822691)
            .setInclination(0.889253120260506)
            .setRa_of_asc_node(38.46053156164339)
            .setArg_of_pericenter(34.36938785036925)
            .setMean_anomaly(136.4013846143255)
            .setGm(398600.4415));
    return result;
  }

  /** Builds OPM with state vector from Sarah's synthetic asteroids. */
  public static OrbitParameterMessage buildOpmWithCartesian() {
    OrbitParameterMessage result = new OrbitParameterMessage();
    result.setCcsds_opm_vers("2.0");
    result.setHeader(new OdmCommonHeader().addComment("Testing"));
    result.setMetadata(
        new OdmCommonMetadata()
            .setObject_name("Asteroid101")
            .setObject_id("asteroid_101")
            .setCenter_name(OdmCommonMetadata.CenterName.SUN)
            .setRef_frame(OdmCommonMetadata.ReferenceFrame.ICRF)
            .setTime_system(OdmCommonMetadata.TimeSystem.UTC));
    result.setState_vector(
        new StateVector()
            .addComment("State Vector")
            .setEpoch("2001-01-02T01:13:46.620000Z")
            .setX(-150874809.2)
            .setY(-187234595.3)
            .setZ(-73785026.7)
            .setX_dot(14.64403935)
            .setY_dot(-11.75744819)
            .setZ_dot(-5.583528281));
    return result;
  }
}

