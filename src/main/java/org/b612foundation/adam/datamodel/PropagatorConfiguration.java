package org.b612foundation.adam.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Configuration for the interplanetary orbit propagator. Lists various forces to be included.
 */
public class PropagatorConfiguration extends AdamObject {

  /**
   * How to include gravity of individual bodies: ignore it all together, use it as point mass, or use a detailed model.
   * For now assume that there is only one "good" spherical harmonics model for each body, so we don't specify which one
   * to use.
   */
  public enum PlanetGravityMode {
    /**
     * Ignore this body's gravity completely.
     */
    OMIT,
    /**
     * Treat the body as point-mass.
     */
    POINT_MASS,
    /**
     * Use a spherical harmonics model. Assumes there is a default model. Not always applicable.
     */
    SPHERICAL_HARMONICS
  }

  /**
   * Human-readable description. Not used in hash code or equal.
   */
  private String description;

  /**
   * If and how to include Sun gravity.
   */
  private PlanetGravityMode sun = PlanetGravityMode.POINT_MASS;
  /**
   * If and how to include Mercury gravity.
   */
  private PlanetGravityMode mercury = PlanetGravityMode.POINT_MASS;
  /**
   * If and how to include Venus gravity.
   */
  private PlanetGravityMode venus = PlanetGravityMode.POINT_MASS;
  /**
   * If and how to include Earth gravity.
   */
  private PlanetGravityMode earth = PlanetGravityMode.POINT_MASS;
  /**
   * If and how to include Mars gravity.
   */
  private PlanetGravityMode mars = PlanetGravityMode.POINT_MASS;
  /**
   * If and how to include Jupiter gravity.
   */
  private PlanetGravityMode jupiter = PlanetGravityMode.POINT_MASS;
  /**
   * If and how to include Saturn gravity.
   */
  private PlanetGravityMode saturn = PlanetGravityMode.POINT_MASS;
  /**
   * If and how to include Uranus gravity.
   */
  private PlanetGravityMode uranus = PlanetGravityMode.POINT_MASS;
  /**
   * If and how to include Neptune gravity.
   */
  private PlanetGravityMode neptune = PlanetGravityMode.POINT_MASS;
  /**
   * If and how to include Pluto gravity.
   */
  private PlanetGravityMode pluto = PlanetGravityMode.POINT_MASS;
  /**
   * If and how to include Earth's Moon gravity.
   */
  private PlanetGravityMode moon = PlanetGravityMode.POINT_MASS;
  /**
   * Names of asteroids we want to include.
   */
  private List<String> asteroids = new ArrayList<>();

  public String getDescription() {
    return description;
  }

  public PropagatorConfiguration setDescription(String description) {
    this.description = description;
    return this;
  }

  public PlanetGravityMode getSun() {
    return sun;
  }

  public PropagatorConfiguration setSun(PlanetGravityMode sun) {
    this.sun = sun;
    return this;
  }

  public PlanetGravityMode getMercury() {
    return mercury;
  }

  public PropagatorConfiguration setMercury(PlanetGravityMode mercury) {
    this.mercury = mercury;
    return this;
  }

  public PlanetGravityMode getVenus() {
    return venus;
  }

  public PropagatorConfiguration setVenus(PlanetGravityMode venus) {
    this.venus = venus;
    return this;
  }

  public PlanetGravityMode getEarth() {
    return earth;
  }

  public PropagatorConfiguration setEarth(PlanetGravityMode earth) {
    this.earth = earth;
    return this;
  }

  public PlanetGravityMode getMars() {
    return mars;
  }

  public PropagatorConfiguration setMars(PlanetGravityMode mars) {
    this.mars = mars;
    return this;
  }

  public PlanetGravityMode getJupiter() {
    return jupiter;
  }

  public PropagatorConfiguration setJupiter(PlanetGravityMode jupiter) {
    this.jupiter = jupiter;
    return this;
  }

  public PlanetGravityMode getSaturn() {
    return saturn;
  }

  public PropagatorConfiguration setSaturn(PlanetGravityMode saturn) {
    this.saturn = saturn;
    return this;
  }

  public PlanetGravityMode getUranus() {
    return uranus;
  }

  public PropagatorConfiguration setUranus(PlanetGravityMode uranus) {
    this.uranus = uranus;
    return this;
  }

  public PlanetGravityMode getNeptune() {
    return neptune;
  }

  public PropagatorConfiguration setNeptune(PlanetGravityMode neptune) {
    this.neptune = neptune;
    return this;
  }

  public PlanetGravityMode getPluto() {
    return pluto;
  }

  public PropagatorConfiguration setPluto(PlanetGravityMode pluto) {
    this.pluto = pluto;
    return this;
  }

  public PlanetGravityMode getMoon() {
    return moon;
  }

  public PropagatorConfiguration setMoon(PlanetGravityMode moon) {
    this.moon = moon;
    return this;
  }

  public List<String> getAsteroids() {
    return asteroids;
  }

  public String getAsteroidsString() {
    return String.join(",", asteroids);
  }

  public PropagatorConfiguration addAsteroid(String name) {
    asteroids.add(name);
    return this;
  }

  public PropagatorConfiguration setAsteroids(List<String> asteroids) {
    this.asteroids = asteroids;
    return this;
  }

  public PropagatorConfiguration setAsteroidsString(String names) {
    asteroids.clear();
    if (names == null || names.isEmpty()) {
      return this;
    }
    for (String name : names.split(",")) {
      asteroids.add(name.trim());
    }
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), sun, mercury, venus, earth, mars, jupiter, saturn, uranus, neptune, pluto,
        moon, asteroids);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PropagatorConfiguration other = (PropagatorConfiguration) obj;
    return super.equals(other) && Objects.equals(sun, other.sun) && Objects.equals(mercury, other.mercury)
        && Objects.equals(venus, other.venus) && Objects.equals(earth, other.earth) && Objects.equals(mars, other.mars)
        && Objects.equals(jupiter, other.jupiter) && Objects.equals(saturn, other.saturn)
        && Objects.equals(uranus, other.uranus) && Objects.equals(neptune, other.neptune)
        && Objects.equals(pluto, other.pluto) && Objects.equals(moon, other.moon)
        && Objects.equals(asteroids, other.asteroids);
  }
}
