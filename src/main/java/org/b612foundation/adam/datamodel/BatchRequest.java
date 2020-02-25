package org.b612foundation.adam.datamodel;

import java.util.Objects;

/**
 * A request for propagation of one or more runs grouped together. Individual runs are generated from this batch based
 * on parameters specified using OPM extension tags.
 */
public class BatchRequest {

  // Parts specified by the user.
  /**
   * Human-readable description of this batch request.
   */
  private String description;
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
   * OPM as a single string in CCSDS format
   */
  private String opm_string;

  // Parts added by the server.
  /**
   * Unique id for a pending batch request, generated on creation. A single request can result in multiple runs.
   */
  private String uuid;
  /**
   * Total number of parts (individual runs) for this request.
   */
  private int partsCount = -1;
  /**
   * State of the calculation for the whole batch
   */
  private CalculationState calcState;
  /**
   * If calc_state is FAILED, error will have more information.
   */
  private String error;
  /**
   * Summary of the batch results.
   */
  private String summary;
  /**
   * Batch manager that processes this request. Not used in equals or hashcode.
   */
  private String manager;

  public String getDescription() {
    return description;
  }

  public BatchRequest setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getStart_time() {
    return start_time;
  }

  public BatchRequest setStart_time(String start_time) {
    this.start_time = start_time;
    return this;
  }

  public String getEnd_time() {
    return end_time;
  }

  public BatchRequest setEnd_time(String end_time) {
    this.end_time = end_time;
    return this;
  }

  public long getStep_duration_sec() {
    return step_duration_sec;
  }

  public BatchRequest setStep_duration_sec(long step_duration_sec) {
    this.step_duration_sec = step_duration_sec;
    return this;
  }

  public String getPropagator_uuid() {
    return propagator_uuid;
  }

  public BatchRequest setPropagator_uuid(String propagator_uuid) {
    this.propagator_uuid = propagator_uuid;
    return this;
  }

  public String getOpm_string() {
    return opm_string;
  }

  public BatchRequest setOpm_string(String opm_string) {
    this.opm_string = opm_string;
    return this;
  }

  public String getUuid() {
    return uuid;
  }

  public BatchRequest setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public int getParts_count() {
    return partsCount;
  }

  public BatchRequest setParts_count(int partsCount) {
    this.partsCount = partsCount;
    return this;
  }

  public CalculationState getCalc_state() {
    return calcState;
  }

  public BatchRequest setCalc_state(CalculationState calcState) {
    this.calcState = calcState;
    return this;
  }

  public String getError() {
    return error;
  }

  public BatchRequest setError(String error) {
    this.error = error;
    return this;
  }

  public String getSummary() {
    return summary;
  }

  public BatchRequest setSummary(String summary) {
    this.summary = summary;
    return this;
  }

  public String getManager() {
    return manager;
  }

  public BatchRequest setManager(String manager) {
    this.manager = manager;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(calcState, description, end_time, error, opm_string, partsCount, propagator_uuid, start_time,
        step_duration_sec, summary, uuid);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BatchRequest other = (BatchRequest) obj;
    // Check uuid first, since non-equal BatchRequests almost always have non-equal uuids.
    return Objects.equals(uuid, other.uuid) && Objects.equals(calcState, other.calcState)
        && Objects.equals(description, other.description) && Objects.equals(end_time, other.end_time)
        && Objects.equals(error, other.error) && Objects.equals(opm_string, other.opm_string)
        && Objects.equals(partsCount, other.partsCount) && Objects.equals(propagator_uuid, other.propagator_uuid)
        && Objects.equals(start_time, other.start_time) && Objects.equals(step_duration_sec, other.step_duration_sec)
        && Objects.equals(summary, other.summary);
  }

  @Override
  public String toString() {
    return "BatchRequest [description=" + description + ", start_time=" + start_time +
        ", end_time=" + end_time + ", step_duration_sec=" + step_duration_sec +
        ", propagator_uuid=" + propagator_uuid + ", opm_string=" + opm_string +
        ", uuid=" + uuid + ", partsCount=" + partsCount + ", calcState=" +
        calcState + ", error=" + error + ", summary=" + summary + ", manager=" +
        manager + "]";
  }
}
