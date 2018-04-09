package org.b612foundation.adam.datamodel;

import java.util.Objects;

import org.b612foundation.adam.opm.OrbitParameterMessage;

/**
 * Single run within a batch.
 */
public class RunDescription {
  /** Id of the batch this run is from */
  private String batch_uuid;
  /** Sequential index within the parent batch */
  private int part_index;
  /** State of the calculation for this part. */
  private CalculationState calc_state;
  /** If calc_state is FAILED, error will have more information. */
  private String error;
  /** Full OPM for this particular run. */
  private OrbitParameterMessage opm;
  /** Propagated orbit in STK .e format. Available if calcSate is COMPLETED. */
  private String stk_ephemeris;
  /** Summary for this particular run, both setup and results. */
  private String summary;

  public RunDescription() {
  }

  public RunDescription(String batchUuid, int partIndex, OrbitParameterMessage opm) {
    this.batch_uuid = batchUuid;
    this.part_index = partIndex;
    this.calc_state = CalculationState.PENDING;
    this.opm = opm;
  }

  public String getBatch_uuid() {
    return batch_uuid;
  }

  public RunDescription setBatch_uuid(String batch_uuid) {
    this.batch_uuid = batch_uuid;
    return this;
  }

  public int getPart_index() {
    return part_index;
  }

  public RunDescription setPart_index(int part_index) {
    this.part_index = part_index;
    return this;
  }

  public CalculationState getCalc_state() {
    return calc_state;
  }

  public RunDescription setCalc_state(CalculationState calc_state) {
    this.calc_state = calc_state;
    return this;
  }

  public String getError() {
    return error;
  }

  public RunDescription setError(String error) {
    this.error = error;
    return this;
  }

  public OrbitParameterMessage getOpm() {
    return opm;
  }

  public RunDescription setOpm(OrbitParameterMessage opm) {
    this.opm = opm;
    return this;
  }

  public String getStk_ephemeris() {
    return stk_ephemeris;
  }

  public RunDescription setStk_ephemeris(String stk_ephemeris) {
    this.stk_ephemeris = stk_ephemeris;
    return this;
  }

  public String getSummary() {
    return summary;
  }

  public RunDescription setSummary(String summary) {
    this.summary = summary;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(batch_uuid, part_index);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RunDescription other = (RunDescription) obj;
    return Objects.equals(batch_uuid, other.batch_uuid) && Objects.equals(part_index, other.part_index);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RunDescription [batch_uuid=").append(batch_uuid).append(", part_index=").append(part_index)
        .append(", calc_state=").append(calc_state).append(", error=").append(error).append(", summary=")
        .append(summary).append(", stk_ephemeris=").append(stk_ephemeris).append("]");
    return builder.toString();
  }
}
