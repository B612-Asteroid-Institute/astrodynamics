package org.b612foundation.adam.stk.propagators;

import agi.foundation.celestial.*;
import agi.foundation.geometry.Point;
import agi.foundation.numericalmethods.KindOfStepSize;
import agi.foundation.numericalmethods.RungeKuttaFehlberg78Integrator;
import agi.foundation.propagators.PropagationNewtonianPoint;
import agi.foundation.stk.StkEphemerisFile;
import agi.foundation.time.TimeInterval;
import com.google.common.base.Preconditions;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/** Helper methods for setting up forces of the solar system correctly for AGI Component propagators. */
public class ForceModelHelper {
  private static Logger log = Logger.getLogger(ForceModelHelper.class.getName());
  // Resource directory with asteroid ephemerides. Files are name.e.
  private static final String ASTEROIDS_DIR = "data/asteroids/";

  // Gravity model.
  private static final String EGM96_FILE = "data/EGM96.grv";

  // Load JPL Ephemerides.
  // TODO: allow user to specify DE file.
  private static final String DE_FILE = "data/plneph.430";
  public static final JplDE JPL_DE = new JplDE430(new ClasspathStreamFactory(DE_FILE));

  /** Initializes standard objects, including loading DE. */
  public static void loadStandardObjects() {
    JPL_DE.useForCentralBodyPositions(CentralBodiesFacet.getFromContext());
    CentralBodiesFacet.getFromContext().getEarth().setNutationModel(JPL_DE.getEarthNutationModel());
  }

  public static TimeInterval getSupportedDateRange() {
    // Initial epoch: 2433264:43200 TDB (12/13/1949 11:59:17 PM)
    // Final epoch: 2506352:43200 TDB (1/21/2150 11:58:50 PM)
    return new TimeInterval(JPL_DE.getInitialEpoch(), JPL_DE.getFinalEpoch());
  }

  /** Obtains an integrator configured to match STK desktop. */
  public static RungeKuttaFehlberg78Integrator getRungeKuttaFehlberg78Integrator() {
    // TODO: We probably want to pull all the constants out into solver config that would be
    // specifiable by the user.
    RungeKuttaFehlberg78Integrator integrator = new RungeKuttaFehlberg78Integrator();
    integrator.setStepSizeBehavior(KindOfStepSize.RELATIVE);
    // Parameters are set to match STK Desktop settings.
    integrator.setInitialStepSize(1);
    integrator.setMaximumStepSize(86400);
    integrator.setMinimumStepSize(1);
    integrator.setAbsoluteTolerance(1e-11);
    integrator.setRelativeTolerance(1e-13);
    return integrator;
  }

  /** Sets up earth-centered forces, including other bodies according to the given config. */
  public static void initializeEarthCenteredForces(
      PropagatorConfiguration config, PropagationNewtonianPoint object) {
    switch (config.getEarth()) {
      case OMIT:
        log.warning("Earth is the central body, but Earth gravity is not included");
        break;
      case POINT_MASS:
        addEarthPointMassGravity(object);
        break;
      case SPHERICAL_HARMONICS:
        addEgm96Gravity(object);
        break;
    }
    // TODO: other bodies? We probably won't use the Earth frame often.
  }

  /** Adds a third body defined in JPL epehemerides to the given third body gravity force. */
  private static void addThirdBody(
      String name,
      PropagatorConfiguration.PlanetGravityMode mode,
      JplDECentralBody body,
      ThirdBodyGravity bodies) {
    switch (mode) {
      case OMIT:
        log.fine("Skipping '" + name + "' in planetary mode");
        break;
      case POINT_MASS:
        bodies.addThirdBody(
            name, JPL_DE.getCenterOfMassPoint(body), JPL_DE.getGravitationalParameter(body));
        break;
      default:
        throw new IllegalArgumentException(
            "Do not support " + mode + " for " + name + " in planetary mode");
    }
  }

  /**
   * Sets up sun-centered forces, including other bodies according to the given config. Adds those
   * forces to the given object.
   */
  public static void initializeSunCenteredForces(
      PropagatorConfiguration config, PropagationNewtonianPoint object) {
    // Add Sun's gravity.
    SunCentralBody sun = CentralBodiesFacet.getFromContext().getSun();
    Preconditions.checkArgument(
        config.getSun() == PropagatorConfiguration.PlanetGravityMode.POINT_MASS,
        "Expect Sun to be treated as point-mass in the Sun-centered frame, got %s",
        config.getSun());
    TwoBodyGravity sunGravity =
        new TwoBodyGravity(
            object.getIntegrationPoint(),
            sun,
            JPL_DE.getGravitationalParameter(JplDECentralBody.SUN));
    sunGravity.setTargetPoint(object.getIntegrationPoint());
    object.getAppliedForces().add(sunGravity);

    // Add planets and other big things as third bodies.
    ThirdBodyGravity bodies = new ThirdBodyGravity(object.getIntegrationPoint());
    bodies.setCentralBody(CentralBodiesFacet.getFromContext().getSun());
    addThirdBody("Mercury", config.getMercury(), JplDECentralBody.MERCURY, bodies);
    addThirdBody("Venus", config.getVenus(), JplDECentralBody.VENUS, bodies);
    addThirdBody("Earth", config.getEarth(), JplDECentralBody.EARTH, bodies);
    addThirdBody("Mars", config.getMars(), JplDECentralBody.MARS, bodies);
    addThirdBody("Jupiter", config.getJupiter(), JplDECentralBody.JUPITER, bodies);
    addThirdBody("Saturn", config.getSaturn(), JplDECentralBody.SATURN, bodies);
    addThirdBody("Uranus", config.getUranus(), JplDECentralBody.URANUS, bodies);
    addThirdBody("Neptune", config.getNeptune(), JplDECentralBody.NEPTUNE, bodies);
    addThirdBody("Pluto", config.getPluto(), JplDECentralBody.PLUTO, bodies);
    addThirdBody("Moon", config.getMoon(), JplDECentralBody.MOON, bodies);
    if (config.getAsteroids() != null) {
      for (String name : config.getAsteroids()) {
        if (!loadAsteroid(name.toLowerCase(), bodies)) {
          throw new IllegalArgumentException("Can't add an asteroid named '" + name + "'");
        }
      }
    }
    if (!bodies.getThirdBodies().isEmpty()) {
      object.getAppliedForces().add(bodies);
    }
  }

  /** Attaches the given asteroid as a third body to the gravity model. Returns true on success. */
  private static boolean loadAsteroid(String name, ThirdBodyGravity bodies) {
    String resourceName = ASTEROIDS_DIR + name + ".e";
    InputStream input = ClassLoader.getSystemResourceAsStream(resourceName);
    if (input == null) {
      log.severe("Cannot open resource " + resourceName);
      return false;
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    // Reset the reader and look for the gravity constant string
    double g = -1; // km3/s2
    try {
      reader.mark(1024); // Expect the GravityConstant somewhere near the top.
      String str = reader.readLine();
      while (str != null) {
        final String prefix = "# GravityConstant ";
        if (str.startsWith(prefix)) {
          g = Double.parseDouble(str.substring(prefix.length())); // km3/s2
          break;
        }
        str = reader.readLine();
      }
    } catch (Exception e) {
      log.severe("Cannot get GravityConstant for asteroid " + name + ": " + e);
      return false;
    }
    if (g < 0) {
      log.severe("Don't have GM for asteroid " + name);
      return false;
    }
    try {
      reader.reset();
    } catch (IOException e) {
      log.info("Reset on file failed for asteroid " + name);
      reader = new BufferedReader(new InputStreamReader(input));
    }
    StkEphemerisFile file = StkEphemerisFile.readFrom(reader);
    Point point = file.createPoint();
    bodies.addThirdBody(name, point, g * 1e9); // g is expected in m3/s2
    return true;
  }

  private static void addEarthPointMassGravity(PropagationNewtonianPoint object) {
    EarthCentralBody earth = CentralBodiesFacet.getFromContext().getEarth();
    TwoBodyGravity gravity =
        new TwoBodyGravity(
            object.getIntegrationPoint(),
            earth,
            JPL_DE.getGravitationalParameter(JplDECentralBody.EARTH));
    gravity.setTargetPoint(object.getIntegrationPoint());
    object.getAppliedForces().add(gravity);
  }

  private static void addEgm96Gravity(PropagationNewtonianPoint object) {
    SphericalHarmonicGravity gravity = new SphericalHarmonicGravity();
    gravity.setTargetPoint(object.getIntegrationPoint());
    // Spherical harmonics are to functions defined on spheres as what Fourier series are to 1D
    // functions (https://en.wikipedia.org/wiki/Spherical_harmonics). A function defined on a sphere
    // can be represented as a linear combination of spherical harmonics. Each harmonic has a degree
    // and an order. Degree is similar to a Fourier harmonic frequency and intuitively defines "the
    // number of zero crossings". Order, which goes from 0 to degree, [very colloquially speaking]
    // says how many of those crossing are on latitude vs longitude.
    // The EGM96 model includes spherical harmonic coefficients up to 70 order and 70 degree. Any
    // given instantiation of this model can use fewer coefficients, which will get lower precision
    // but better computational performance. The only requirement is that 0 < order <= degree <= 70.
    // TODO: We might want to move degree and order values into the configuration
    // at some point. For now we use magic values from AGI examples, without any justification.
    final int degree = 41;
    final int order = 41;
    boolean includeTwoBodyForces = true;
    InputStream stream = ForceModelHelper.class.getResourceAsStream(EGM96_FILE);
    if (stream == null) {
      throw new RuntimeException("Cannot load gravity model file " + EGM96_FILE);
    }
    SphericalHarmonicGravityModel model =
        SphericalHarmonicGravityModel.readFrom(new BufferedReader(new InputStreamReader(stream)));
    gravity.setGravityField(
        new SphericalHarmonicGravityField(
            model, degree, order, includeTwoBodyForces, SphericalHarmonicsTideType.NONE));
    gravity.setTargetPoint(object.getIntegrationPoint());
    object.getAppliedForces().add(gravity);
  }
}
