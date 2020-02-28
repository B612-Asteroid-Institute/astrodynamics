package org.b612foundation.adam.datamodel;

import org.b612foundation.adam.opm.OdmFormatter;
import org.b612foundation.adam.opm.OdmParseException;
import org.b612foundation.adam.opm.OrbitParameterMessage;

import java.util.Objects;

public class PropagationParameters {

  /**
   * Beginning of the ephemerides. Should be UTC. Generated ephemerides will start at this time.
   */
  private String start_time;
  /**
   * End of the ephemerides. This should be UTC. Generated ephemerides will end at this time.
   */
  private String end_time;
  /**
   * Time step for the output ephemeris, seconds. If <=0, output will match integrator steps, no interpolation.
   */
  private long step_duration_sec;
  /**
   * Settings for the numeric propagator - the ID.
   */
  private String propagator_uuid;
  /**
   * Specific executor to be used, e.g. STK, OpenOrb. The behavior is up to the server implementation.
   */
  private String executor;
  /**
   * OPM as parsed from a single string in CCSDS format
   */
  private OrbitParameterMessage opm;

  public PropagationParameters deepCopy() {
    PropagationParameters copy = new PropagationParameters();
    copy.setEnd_time(end_time);
    copy.setOpm(opm.deepCopy());
    copy.setPropagator_uuid(propagator_uuid);
    copy.setStart_time(start_time);
    copy.setStep_duration_sec(step_duration_sec);
    copy.setExecutor(executor);
    return copy;
  }

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

  public String getExecutor() {
    return executor;
  }

  public PropagationParameters setExecutor(String executor) {
    this.executor = executor;
    return this;
  }

  public OrbitParameterMessage getOpm() {
    return opm;
  }

  public PropagationParameters setOpm(OrbitParameterMessage opm) {
    this.opm = opm;
    return this;
  }

  public void setOpmFromString(String opmString) throws OdmParseException {
    this.opm = OdmFormatter.parseOpmString(opmString);
  }

  @Override
  public int hashCode() {
    return Objects.hash(end_time, opm, propagator_uuid, executor, start_time, step_duration_sec);
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
    return Objects.equals(end_time, other.end_time) && Objects.equals(opm, other.opm)
        && Objects.equals(propagator_uuid, other.propagator_uuid) && Objects.equals(executor, other.executor)
        && Objects.equals(start_time, other.start_time) && Objects.equals(step_duration_sec, other.step_duration_sec);
  }

}
