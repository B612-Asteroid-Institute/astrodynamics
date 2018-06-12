package org.b612foundation.adam.datamodel;

import java.util.Objects;

public class PropagationParameters {

  /** Beginning of the ephemerides. Should be UTC. Generated ephemerides will start at this time. */
  private String start_time;
  /** End of the ephemerides. This should be UTC. Generated ephemerides will end at this time. */
  private String end_time;
  /** Time step for the output ephemeris, seconds. If <=0, output will match integrator steps, no interpolation. */
  private long step_duration_sec;
  /** Settings for the numeric propagator - the ID. */
  private String propagator_uuid;
  /** OPM as a single string in CCSDS format */
  private String opm_string;

  public String getStart_time() {
    return start_time;
  }

  public PropagationParameters setStart_time(String start_time) {
    this.start_time = start_time;
    return this;
  }

  public String getEnd_time() {
    return end_time;
  }

  public PropagationParameters setEnd_time(String end_time) {
    this.end_time = end_time;
    return this;
  }

  public long getStep_duration_sec() {
    return step_duration_sec;
  }

  public PropagationParameters setStep_duration_sec(long step_duration_sec) {
    this.step_duration_sec = step_duration_sec;
    return this;
  }

  public String getPropagator_uuid() {
    return propagator_uuid;
  }

  public PropagationParameters setPropagator_uuid(String propagator_uuid) {
    this.propagator_uuid = propagator_uuid;
    return this;
  }

  public String getOpm_string() {
    return opm_string;
  }

  public PropagationParameters setOpm_string(String opm_string) {
    this.opm_string = opm_string;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(end_time, opm_string, propagator_uuid, start_time, step_duration_sec);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PropagationParameters other = (PropagationParameters) obj;
    return Objects.equals(end_time, other.end_time) && Objects.equals(opm_string, other.opm_string)
        && Objects.equals(propagator_uuid, other.propagator_uuid) && Objects.equals(start_time, other.start_time)
        && Objects.equals(step_duration_sec, other.step_duration_sec);
  }

}
