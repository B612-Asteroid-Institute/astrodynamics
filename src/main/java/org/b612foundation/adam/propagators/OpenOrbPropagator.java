package org.b612foundation.adam.propagators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.datamodel.PropagatorConfiguration.PlanetGravityMode;
import org.b612foundation.adam.opm.*;
import org.b612foundation.adam.runnable.AdamRunnableException;

import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;

public class OpenOrbPropagator implements OrbitPropagator {
  private static Logger log = Logger.getLogger(OpenOrbPropagator.class.getName());
  public static double SECONDS_IN_DAY = 60 * 60 * 24;

  // OpenOrb's value of Astronomical Unit in km
  public static double AU_TO_KM = 149597870.700;
  // Subtract this from JulianDate to get Modified Julian Date
  public static double JULIAN_MODIFIER = 2400000.5;

  private String getOorbBasePath() {
    String var = System.getenv("OORBROOT");
    if (var != null) {
      return var;
    }
    String current = System.getProperty("user.dir");
    log.info("OpenOrb root var OORBROOT is not found, assuming current directory " + current);
    return current;
  }

  private String asTorF(PlanetGravityMode mode) throws AdamRunnableException {
    switch (mode) {
    case OMIT:
      return "F";
    case POINT_MASS:
      return "T";
    default:
      throw new AdamRunnableException("Unsupported gravity mode for OORB: " + mode, null);
    }
  }

  /**
   * OPM has dates as UTC strings. This converts such a string into a Modified Julian Date in TT, which OpenOrb wants.
   */
  private double utcToModifiedJulian(String utc) throws AdamRunnableException {
    JulianDate date = TimeHelper.fromIsoFormat(utc, TimeStandard.getCoordinatedUniversalTime())
        .toTimeStandard(TimeStandard.getTerrestrialTime());
    return date.getTotalDays() - JULIAN_MODIFIER;
  }

  private void writeConfigFile(PropagatorConfiguration config, double stepDays, Writer writer)
      throws IOException, AdamRunnableException {
    // Verbose level for informative messages (0=nothing and 5=maximum, default=1)
    writer.append("verbose.info:   1\n");
    writer.append("verbose.error:   1\n");
    writer.append("planetary_ephemeris_fname: de430.dat\n");

    // OUTPUT
    // Element type to be used in the resulting orbital element file
    // [ keplerian | cartesian | cometary ]
    writer.append("element_type_out: cartesian\n");
    // Write O-C residuals to file (T=yes/F=no)
    writer.append("write.residuals: F\n");
    writer.append("plot.results: F\n");
    writer.append("plot.open: F\n");
    // Format of output orbit file [ orb | des ]
    writer.append("orbit.format.out: des\n");

    // GENERAL INVERSION PARAMETERS
    // Orbital element type used during computations [ keplerian | cartesian | cometary ]
    writer.append("element_type_comp: cartesian\n");

    // PROPAGATION PARAMETERS
    // Dynamical model [ 2-body | n-body ]
    writer.append("dynamical_model:      n-body\n");
    // Perturbing bodies to be taken into account in n-body propagation
    writer.append("perturber.Mercury: " + asTorF(config.getMercury()) + "\n");
    writer.append("perturber.Venus: " + asTorF(config.getVenus()) + "\n");
    writer.append("perturber.Earth: " + asTorF(config.getEarth()) + "\n");
    writer.append("perturber.Moon: " + asTorF(config.getMoon()) + "\n");
    writer.append("perturber.Mars: " + asTorF(config.getMars()) + "\n");
    writer.append("perturber.Jupiter: " + asTorF(config.getJupiter()) + "\n");
    writer.append("perturber.Saturn: " + asTorF(config.getSaturn()) + "\n");
    writer.append("perturber.Uranus: " + asTorF(config.getUranus()) + "\n");
    writer.append("perturber.Neptune: " + asTorF(config.getNeptune()) + "\n");
    writer.append("perturber.Pluto: " + asTorF(config.getPluto()) + "\n");

    // Asteroidal perturbations from BC430 ephemerides
    // perturber.asteroids: T
    if (!config.getAsteroids().isEmpty()) {
      throw new AdamRunnableException(
          "Don't support asteroid selection for OORB, requested " + config.getAsteroidsString(), null);
    }

    writer.append("integrator: bulirsch-stoer\n");
    // Integrator step length (in days)
    writer.append("integration_step:      " + stepDays + "\n");
    // Relativistic corrections
    writer.append("relativity: F\n");
    // Dynamical model of the initial orbit [ 2-body | n-body ]
    writer.append("dynamical_model_init:      n-body\n");
    writer.append("integrator_init: bulirsch-stoer\n");
    // Integrator step length of the initial orbit (in days)
    writer.append("integration_step_init:       " + stepDays + "\n");
    // Maximum number of massless particles integrated simultaneously
    writer.append("simint: 1\n");
    writer.append("\n");
    writer.append("\n");

  }

  private void writeInputFile(PropagationParameters params, Writer writer) throws IOException, AdamRunnableException {
    // OPM has km, OpenOrb wants AU.
    // Example:
    // 917 CAR 0.364889925258992E+00 0.231131850188538E+01 0.208015104944204E+00
    // -0.105828005078420E-01 0.396012306441396E-02 0.690961818032559E-04
    // 0.200000000000000E+02 0.58600000000000E+05 1 6 -0.100000000000000E+01 OPENORB
    writer.append("!!OID FORMAT x y z dx/dt dy/dt dz/dt H t_0 INDEX N_PAR MOID COMPCODE\n");
    writer.append(params.getOpm().getMetadata().getObject_id()).append(" CAR");
    StateVector stateVector = params.getOpm().getState_vector();
    writer.append(" " + stateVector.getX() / AU_TO_KM);
    writer.append(" " + stateVector.getY() / AU_TO_KM);
    writer.append(" " + stateVector.getZ() / AU_TO_KM);
    writer.append(" " + stateVector.getX_dot() / AU_TO_KM * SECONDS_IN_DAY);
    writer.append(" " + stateVector.getY_dot() / AU_TO_KM * SECONDS_IN_DAY);
    writer.append(" " + stateVector.getZ_dot() / AU_TO_KM * SECONDS_IN_DAY);
    // H is absolute magnitude, keep 20 for now
    // t_0 is timestamp for the state vector in JED aka TT
    // index = 1, N_PAR is number of parameters = 6, MOID and COMPCODE are magic
    writer.append(" 20 " + utcToModifiedJulian(stateVector.getEpoch()) + " 1    6 -0.100000000000000E+01 OPENORB\n");
  }

  @Override
  public String propagate(PropagationParameters propagationParams, PropagatorConfiguration config,
      String propagationIdForLogging) throws AdamRunnableException {

    String response = "";
    Runtime runtime = Runtime.getRuntime();

    try {
      String base = getOorbBasePath();
      log.info("Base for OORB: " + base);

      // Make a config file.
      File configFile = File.createTempFile("oorb", ".conf");
      configFile.deleteOnExit();
      double stepSizeDays = propagationParams.getStep_duration_sec() / SECONDS_IN_DAY;
      try (FileWriter writer = new FileWriter(configFile)) {
        writeConfigFile(config, (stepSizeDays <= 0) ? 1.0 : stepSizeDays, writer);
      }
      log.info("Using config file " + configFile.getAbsolutePath());

      // Make input orbit file
      File inputOrbit = File.createTempFile("oorb_input", ".des");
      inputOrbit.deleteOnExit();
      try (FileWriter writer = new FileWriter(inputOrbit)) {
        writeInputFile(propagationParams, writer);
      }

      // StringWriter sw = new StringWriter();
      // writeConfigFile(config, (stepSizeSeconds <= 0) ? 1.0 : (stepSizeSeconds / SECONDS_IN_DAY), sw);
      // writeInputFile(propagationParams, sw);
      // System.out.println("INPUT\n" + sw);
      // System.out.println("--epoch-mjd-tt=" + utcToModifiedJulian(propagationParams.getEnd_time()));

      String execPath = Paths.get(base, "main", "oorb").toString();
      String[] cmd = { execPath, "--task=propagation", "--conf=" + configFile.getAbsolutePath(),
          "--orb-in=" + inputOrbit.getAbsolutePath(),
          "--epoch-mjd-tt=" + utcToModifiedJulian(propagationParams.getEnd_time()),
          "--output-interval-days=" + stepSizeDays};
      Process process = runtime.exec(cmd);
      try (InputStream input = process.getInputStream(); InputStream error = process.getErrorStream()) {
        response = response + new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n"))
            + "\n";
        String errors = new BufferedReader(new InputStreamReader(error)).lines().collect(Collectors.joining("\n"))
            + "\n";
        if (!errors.trim().isEmpty()) {
          System.out.println("ERRORS [" + errors + "]");
          throw new AdamRunnableException("Oorb error: " + errors, null);
        }
      }
    } catch (IOException e) {
      throw new AdamRunnableException("Couldn't run oorb", e);
    }

    String stkEphemeris = "";
    try {
      OrbitEphemerisMessage oem = OdmFormatter.parseOorbEphemerisString(response, "object", "object");
      stkEphemeris = OemToStkEphemerisWriter.toStkEphemerisString(oem);
    } catch (OdmParseException e) {
      e.printStackTrace();
    }
    return stkEphemeris;
  }
}
