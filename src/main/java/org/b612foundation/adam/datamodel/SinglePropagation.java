package org.b612foundation.adam.datamodel;

import java.util.Objects;

public class SinglePropagation extends AdamObject {

  /**
   * Parameters used to generate the ephemeris of this propagation. Null if unknown.
   */
  private PropagationParameters propagationParameters;

  /**
   * Human-readable description of this propagation.
   */
  private String description;

  /**
   * Ephemeris produced as part of the propagation. Formatted as contents of STK .e file.
   * <p>
   * The header contains information about the reference frame of the data, and a UTC timestamp that can be used to
   * interpret the times of the state vectors in the data.
   * <p>
   * The data section contains cartesian state vector coordinates in meters and meters/second in the metadata reference
   * frame, with times given as seconds offset from the metadata timestamp.
   * <p>
   * For a complete specification, see http://help.agi.com/stk/index.htm#stk/importfiles-02.htm#formats.
   * <p>
   * May be null if not requested that ephemeris be kept.
   */
  private String ephemeris;

  /**
   * Calculated state vector at end time as specified in propagation parameters. Equal to the last line of the
   * ephemeris.
   * <p>
   * Format: time, x, y, z, x velocity, y velocity, z velocity.
   * <p>
   * Units: seconds-since-file-epoch, meters, meters, meters, meters/second, meters/second, meters/second
   */
  private String finalStateVector;

  public PropagationParameters getPropagationParameters() {
    return propagationParameters;
  }

  public SinglePropagation setPropagationParameters(PropagationParameters propagationParameters) {
    this.propagationParameters = propagationParameters;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public SinglePropagation setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getEphemeris() {
    return ephemeris;
  }

  public SinglePropagation setEphemeris(String ephemeris) {
    this.ephemeris = ephemeris;
    return this;
  }

  public String getFinalStateVector() {
    return finalStateVector;
  }

  public SinglePropagation setFinalStateVector(String finalStateVector) {
    this.finalStateVector = finalStateVector;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), propagationParameters, ephemeris, finalStateVector);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SinglePropagation other = (SinglePropagation) obj;
    return super.equals(other) && Objects.equals(propagationParameters, other.propagationParameters)
        && Objects.equals(ephemeris, other.ephemeris) && Objects.equals(finalStateVector, other.finalStateVector);
  }

}