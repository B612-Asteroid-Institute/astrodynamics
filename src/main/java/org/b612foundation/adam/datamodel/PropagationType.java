package org.b612foundation.adam.datamodel;

public enum PropagationType {
  /**
   * The server generates orbit data using hypercube faces or corners, based on an initial state and
   * covariance.
   */
  HYPERCUBE_FACES,
  HYPERCUBE_CORNERS,
  /**
   * The server generates orbit data using Monte Carlo, based on an initial state and covariance.
   */
  MONTE_CARLO,
  /** The user specifies what orbit data to propagate. */
  USER_SPECIFIED
}
