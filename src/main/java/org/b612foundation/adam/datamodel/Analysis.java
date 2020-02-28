package org.b612foundation.adam.datamodel;

import java.util.Objects;

/**
 * Post-processing analysis run against a Run or the whole Batch.
 */
public class Analysis {
  /**
   * Type of this analysis.
   */
  private AnalysisType type;
  /**
   * Id of the batch this analysis is about
   */
  private String batch_uuid;
  /**
   * Run index within that batch, -1 if the whole batch is used (depends on the type of the analysis).
   */
  private int part_index = -1;

  /* TODO some sort of parameter JSON. */

  /**
   * State of the calculation for this analysis.
   */
  private CalculationState calc_state;
  /**
   * If calc_state is FAILED, error will have more information.
   */
  private String error;
  /**
   * Results of the analysis, if COMPLETED. This likely has some internal structure.
   */
  private String result;

  public AnalysisType getType() {
    return type;
  }

  public Analysis setType(AnalysisType type) {
    this.type = type;
    return this;
  }

  public String getBatch_uuid() {
    return batch_uuid;
  }

  public Analysis setBatch_uuid(String batch_uuid) {
    this.batch_uuid = batch_uuid;
    return this;
  }

  public int getPart_index() {
    return part_index;
  }

  public Analysis setPart_index(int part_index) {
    this.part_index = part_index;
    return this;
  }

  public CalculationState getCalc_state() {
    return calc_state;
  }

  public Analysis setCalc_state(CalculationState calc_state) {
    this.calc_state = calc_state;
    return this;
  }

  public String getError() {
    return error;
  }

  public Analysis setError(String error) {
    this.error = error;
    return this;
  }

  public String getResult() {
    return result;
  }

  public Analysis setResult(String result) {
    this.result = result;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, batch_uuid, part_index);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Analysis other = (Analysis) obj;
    return Objects.equals(type, other.type) && Objects.equals(batch_uuid, other.batch_uuid)
        && Objects.equals(part_index, other.part_index);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Analysis [type=").append(type).append(", batch_uuid=").append(batch_uuid).append(", part_index=")
        .append(part_index).append(", calc_state=").append(calc_state).append(", error=").append(error)
        .append(", result=").append(result).append("]");
    return builder.toString();
  }

}
