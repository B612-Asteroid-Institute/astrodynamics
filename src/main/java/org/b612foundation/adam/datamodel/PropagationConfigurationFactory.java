package org.b612foundation.adam.datamodel;

/**
 * Factory class which produces common configurations for the propagators.
 */
public class PropagationConfigurationFactory {

  private static final PropagatorConfiguration ALL_MAJOR_BODIES_CONFIG =
      new PropagatorConfiguration()
          .setSun(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setMercury(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setVenus(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setEarth(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setMars(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setJupiter(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setSaturn(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setUranus(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setNeptune(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setPluto(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setMoon(PropagatorConfiguration.PlanetGravityMode.POINT_MASS);

  private static final PropagatorConfiguration SUN_ONLY_CONFIG =
      new PropagatorConfiguration()
          .setSun(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
          .setMercury(PropagatorConfiguration.PlanetGravityMode.OMIT)
          .setVenus(PropagatorConfiguration.PlanetGravityMode.OMIT)
          .setEarth(PropagatorConfiguration.PlanetGravityMode.OMIT)
          .setMars(PropagatorConfiguration.PlanetGravityMode.OMIT)
          .setJupiter(PropagatorConfiguration.PlanetGravityMode.OMIT)
          .setSaturn(PropagatorConfiguration.PlanetGravityMode.OMIT)
          .setUranus(PropagatorConfiguration.PlanetGravityMode.OMIT)
          .setNeptune(PropagatorConfiguration.PlanetGravityMode.OMIT)
          .setPluto(PropagatorConfiguration.PlanetGravityMode.OMIT)
          .setMoon(PropagatorConfiguration.PlanetGravityMode.OMIT);

  public static PropagatorConfiguration getAllMajorBodiesConfig() {
    return ALL_MAJOR_BODIES_CONFIG;
  }

  public static PropagatorConfiguration getSunOnlyConfig() {
    return SUN_ONLY_CONFIG;
  }
}
