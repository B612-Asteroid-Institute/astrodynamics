package org.b612foundation.adam.datamodel;

/**
 * Types of Analysis object.
 */
public enum AnalysisType {
  /**
   * Default value, should not be used.
   */
  UNKNOWN,
  /**
   * Minima of distance to Earth center.
   */
  EARTH_CLOSE_APPROACH,
  /**
   * Earth impacts defined as crossing WSG84 ellipsoid.
   */
  EARTH_IMPACT
}
