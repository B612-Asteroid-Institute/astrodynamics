package org.b612foundation.adam.opm;

import java.util.Objects;

/** OEM metadata blocks add a few fields to the common metadata. */
public class OemMetadata extends OdmCommonMetadata {
  /** Start time of the ephemeris. Required. */
  private String startTime = null;
  /** Stop time of the ephemeris. Requires. */
  private String stopTime = null;
  /** Usable start time. Optional. */
  private String usableStartTime = null;
  /** Usable stop time. Optional. */
  private String usableStopTime = null;
  /** Interpolation method: Hermite, Linear, Lagrange. Optional. */
  private String interpolation = null;
  /** Interpolation degree, optional. */
  private int interpolationDegree = 0;

  public String getStart_time() {
    return startTime;
  }

  public void setStart_time(String startTime) {
    this.startTime = startTime;
  }

  public String getStop_time() {
    return stopTime;
  }

  public void setStop_time(String stopTime) {
    this.stopTime = stopTime;
  }

  public String getUsable_start_time() {
    return usableStartTime;
  }

  public void setUsable_start_time(String usableStartTime) {
    this.usableStartTime = usableStartTime;
  }

  public String getUsable_stop_time() {
    return usableStopTime;
  }

  public void setUsable_stop_time(String usableStopTime) {
    this.usableStopTime = usableStopTime;
  }

  public String getInterpolation() {
    return interpolation;
  }

  public void setInterpolation(String interpolation) {
    this.interpolation = interpolation;
  }

  public int getInterpolation_degree() {
    return interpolationDegree;
  }

  public void setInterpolation_degree(int interpolationDegree) {
    this.interpolationDegree = interpolationDegree;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), startTime, stopTime, usableStartTime, usableStopTime, interpolation,
        interpolationDegree);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OemMetadata other = (OemMetadata) obj;
    // @formatter:off
    return super.equals(other) 
        && Objects.equals(startTime, other.startTime)
        && Objects.equals(stopTime, other.stopTime)
        && Objects.equals(usableStartTime, other.usableStartTime)
        && Objects.equals(usableStopTime, other.usableStopTime)
        && Objects.equals(interpolation, other.interpolation)
        && Objects.equals(interpolationDegree, other.interpolationDegree);
    // @formatter:on
  }
}
