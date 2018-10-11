package org.b612foundation.adam.datamodel;

import java.util.Objects;

public class TargetingParameters {

  /** The distance from earth that should be targeted, in km. */
  private double targetDistanceFromEarth;
  
  /** The distance from earth that should be targeted, in km, during the first stage of targeting */
  private double initialTargetDistanceFromEarth = -1.0;

  /** The tolerance on the targeting of the target distance from earth, in km. */
  private double tolerance;

  /** If true, runs only the nominal propagation of the targeter. If false, runs full targeting. */
  private boolean runNominalOnly;

  public double getTargetDistanceFromEarth() {
    return targetDistanceFromEarth;
  }

  public TargetingParameters setTargetDistanceFromEarth(double targetDistanceFromEarth) {
    this.targetDistanceFromEarth = targetDistanceFromEarth;
    return this;
  }

  public double getInitialTargetDistanceFromEarth() {
    return initialTargetDistanceFromEarth;
  }

  public TargetingParameters setInitialTargetDistanceFromEarth(double initialTargetDistanceFromEarth) {
    this.initialTargetDistanceFromEarth = initialTargetDistanceFromEarth;
    return this;
  }
  
  public double getTolerance() {
    return tolerance;
  }

  public TargetingParameters setTolerance(double tolerance) {
    this.tolerance = tolerance;
    return this;
  }

  public boolean isRunNominalOnly() {
    return runNominalOnly;
  }

  public TargetingParameters setRunNominalOnly(boolean runNominalOnly) {
    this.runNominalOnly = runNominalOnly;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(targetDistanceFromEarth, initialTargetDistanceFromEarth, tolerance, runNominalOnly);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TargetingParameters other = (TargetingParameters) obj;
    return Objects.equals(targetDistanceFromEarth, other.targetDistanceFromEarth)
        && Objects.equals(initialTargetDistanceFromEarth, other.initialTargetDistanceFromEarth)
        && Objects.equals(tolerance, other.tolerance) && Objects.equals(runNominalOnly, other.runNominalOnly);
  }

}
