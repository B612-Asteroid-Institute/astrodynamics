package org.b612foundation.adam.datamodel.estimation;

import org.b612foundation.adam.datamodel.PropagatorConfiguration;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class OorbConfigWriter {

    public static void writeConfiguration(Path configFilePath,
                                          OrbitDeterminationConfiguration odConfig,
                                          PropagatorConfiguration propConfig) throws FileNotFoundException {
        try (var writer = new PrintWriter(configFilePath.toFile())) {
            odConfig.getExecutionSettings().entrySet().forEach(
                    entry -> writer.println(entry.getKey() + ": " + entry.getValue())
            );
            odConfig.getMeasurementSettings().entrySet().forEach(
                    entry -> writer.println(entry.getKey() + ": " + entry.getValue())
            );
            odConfig.getConvergenceSettings().entrySet().forEach(
                    entry -> writer.println(entry.getKey() + ": " + entry.getValue())
            );
            writer.println("dynamical_model:      n-body");
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
                        "Don't support asteroid selection for OORB, requested " + propConfig.getAsteroidsString(), null);
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
}