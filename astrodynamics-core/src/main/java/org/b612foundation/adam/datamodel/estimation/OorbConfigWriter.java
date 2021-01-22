package org.b612foundation.adam.datamodel.estimation;

import lombok.var;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;

public final class OorbConfigWriter {

  public static void writeConfiguration(
      Path configFilePath,
      OrbitDeterminationConfiguration odConfig,
      PropagatorConfiguration propConfig)
      throws FileNotFoundException {
    try (var writer = new PrintWriter(configFilePath.toFile())) {
      odConfig.getExecutionSettings().forEach((key, value) -> writer.println(key + ": " + value));
      odConfig.getMeasurementSettings().forEach((key, value) -> writer.println(key + ": " + value));
      odConfig.getConvergenceSettings().forEach((key, value) -> writer.println(key + ": " + value));

      if (isTwoBody(propConfig)) {
        writer.println("dynamical_model:      2-body");
      } else {
        writer.println("dynamical_model:      n-body");
      }
      // Perturbing bodies to be taken into account in n-body propagation
      writer.println("perturber.Mercury: " + asTorF(propConfig.getMercury()));
      writer.println("perturber.Venus: " + asTorF(propConfig.getVenus()));
      writer.println("perturber.Earth: " + asTorF(propConfig.getEarth()));
      writer.println("perturber.Moon: " + asTorF(propConfig.getMoon()));
      writer.println("perturber.Mars: " + asTorF(propConfig.getMars()));
      writer.println("perturber.Jupiter: " + asTorF(propConfig.getJupiter()));
      writer.println("perturber.Saturn: " + asTorF(propConfig.getSaturn()));
      writer.println("perturber.Uranus: " + asTorF(propConfig.getUranus()));
      writer.println("perturber.Neptune: " + asTorF(propConfig.getNeptune()));
      writer.println("perturber.Pluto: " + asTorF(propConfig.getPluto()));
      writer.println("perturber.asteroids: F");

      // Asteroidal perturbations from BC430 ephemerides
      // perturber.asteroids: T
      if (!propConfig.getAsteroids().isEmpty()) {
        throw new IllegalArgumentException(
            "Don't support asteroid selection for OORB, requested "
                + propConfig.getAsteroidsString(),
            null);
      }
    }
  }

  private static String asTorF(PropagatorConfiguration.PlanetGravityMode mode) {
    switch (mode) {
      case OMIT:
        return "F";
      case POINT_MASS:
        return "T";
      default:
        throw new IllegalArgumentException("Unsupported gravity mode for OORB: " + mode, null);
    }
  }

  public static boolean isTwoBody(PropagatorConfiguration propConfig) {
    return propConfig.getAsteroids().isEmpty()
        && propConfig.getMercury() == PropagatorConfiguration.PlanetGravityMode.OMIT
        && propConfig.getVenus() == PropagatorConfiguration.PlanetGravityMode.OMIT
        && propConfig.getEarth() == PropagatorConfiguration.PlanetGravityMode.OMIT
        && propConfig.getMoon() == PropagatorConfiguration.PlanetGravityMode.OMIT
        && propConfig.getMars() == PropagatorConfiguration.PlanetGravityMode.OMIT
        && propConfig.getJupiter() == PropagatorConfiguration.PlanetGravityMode.OMIT
        && propConfig.getSaturn() == PropagatorConfiguration.PlanetGravityMode.OMIT
        && propConfig.getUranus() == PropagatorConfiguration.PlanetGravityMode.OMIT
        && propConfig.getNeptune() == PropagatorConfiguration.PlanetGravityMode.OMIT
        && propConfig.getPluto() == PropagatorConfiguration.PlanetGravityMode.OMIT;
  }
}
