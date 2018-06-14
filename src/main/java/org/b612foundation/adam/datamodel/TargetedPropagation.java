package org.b612foundation.adam.datamodel;

import java.util.Objects;

/**
 * Represents a targeted propagation to at least a certain distance from earth.
 */
public class TargetedPropagation extends AdamObject {

  /**
   * Parameters used to initialize targeter.
   * 
   * The stopping time should be an estimate of the time of the perigee for which targeting should be run.
   * 
   * The step size will be used for any generated ephemeris files.
   * 
   * The specified OPM may provide a maneuver, which will be used as the initial maneuver for targeting. Currently only
   * maneuvers of 0 seconds and 0 change in mass are supported. The only reference frame currently supported is TNW,
   * which is a local orbital coordinate frame that has the x-axis along the velocity vector, W along the orbital
   * angular momentum vector, and N completes the right handed system.
   */
  private PropagationParameters initialPropagationParameters;

  /**
   * Parameters for the actual targeting.
   */
  private TargetingParameters targetingParameters;

  /** Human-readable description of this object. */
  private String description;

  /**
   * The resulting ephemeris from the targeter. This will be the nominal ephemeris if the targeter is configured to
   * propagate only the nominal trajectory, otherwise the targeted ephemeris.
   */
  private String ephemeris;

  /**
   * The calculated maneuver. Currently will only contain a modification from the initial maneuver in the x-direction.
   */
  private double maneuverX;
  private double maneuverY;
  private double maneuverZ;

  public PropagationParameters getInitialPropagationParameters() {
    return initialPropagationParameters;
  }

  public TargetedPropagation setInitialPropagationParameters(PropagationParameters initialPropagationParameters) {
    this.initialPropagationParameters = initialPropagationParameters;
    return this;
  }

  public TargetingParameters getTargetingParameters() {
    return targetingParameters;
  }

  public TargetedPropagation setTargetingParameters(TargetingParameters targetingParameters) {
    this.targetingParameters = targetingParameters;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public TargetedPropagation setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getEphemeris() {
    return ephemeris;
  }

  public TargetedPropagation setEphemeris(String ephemeris) {
    this.ephemeris = ephemeris;
    return this;
  }

  public double getManeuverX() {
    return maneuverX;
  }

  public TargetedPropagation setManeuverX(double maneuverX) {
    this.maneuverX = maneuverX;
    return this;
  }

  public double getManeuverY() {
    return maneuverY;
  }

  public TargetedPropagation setManeuverY(double maneuverY) {
    this.maneuverY = maneuverY;
    return this;
  }

  public double getManeuverZ() {
    return maneuverZ;
  }

  public TargetedPropagation setManeuverZ(double maneuverZ) {
    this.maneuverZ = maneuverZ;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), initialPropagationParameters, targetingParameters, description, ephemeris,
        maneuverX, maneuverY, maneuverZ);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TargetedPropagation other = (TargetedPropagation) obj;
    return super.equals(other) && Objects.equals(initialPropagationParameters, other.initialPropagationParameters)
        && Objects.equals(targetingParameters, other.targetingParameters)
        && Objects.equals(description, other.description) && Objects.equals(ephemeris, other.ephemeris)
        && Objects.equals(maneuverX, other.maneuverX) && Objects.equals(maneuverY, other.maneuverY)
        && Objects.equals(maneuverZ, other.maneuverZ);
  }

}
