package org.b612foundation.adam.datamodel;

/** State of individual runs or the whole batch request. */
public enum CalculationState {
  /** Default value, should not be used. */
  UNKNOWN_STATE,
  /** Propagation has started, work in progress. */
  RUNNING,
  /** Propagation completed successfully, output value is valid. */
  COMPLETED,
  /** Propagation failed, error field contains details. */
  FAILED,
  /** The request is pending in the queue, not yet running. */
  PENDING;

  public boolean isFinal() {
    return COMPLETED.equals(this) || FAILED.equals(this);
  }
}
