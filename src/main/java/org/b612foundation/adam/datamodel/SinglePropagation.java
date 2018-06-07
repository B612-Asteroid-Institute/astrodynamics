package org.b612foundation.adam.datamodel;

import java.util.Objects;

public class SinglePropagation extends AdamObject {

  /** Parameters used to generate the ephemeris of this propagation. Null if unknown. */
  private PropagationParameters propagationParameters;

  /** Human-readable description of this propagation. */
  private String description;

  /** Actual ephemeris produced as part of the propagation. May be null if not requested that ephemeris be kept. */
  private String ephemeris;

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

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), propagationParameters, ephemeris);
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
        && Objects.equals(ephemeris, other.ephemeris);
  }

}