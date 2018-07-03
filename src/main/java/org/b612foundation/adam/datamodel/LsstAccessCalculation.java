package org.b612foundation.adam.datamodel;

import java.util.List;
import java.util.Objects;

/**
 * Represents a calculation to compute accesses between the LSST and an asteroid.
 */
public class LsstAccessCalculation extends AdamObject {

  /**
   * Parameters to propagate the target asteroid. Either this or asteroidPropagationUuid must be provided.
   */
  private PropagationParameters asteroidPropagationParameters;

  /**
   * Uuid of the propagation of the target asteroid. This will be used if present - otherwise the given propagation
   * parameters will be used to propagate the ephemeris for the target asteroid.
   */
  private String asteroidPropagationUuid;

  /**
   * When to start computing accesses, formatted like an OPM epoch, in UTC. Must be no earlier than the earliest point
   * covered by the asteroid ephemeris.
   */
  private String accessStartTime;

  /**
   * When to stop computing accesses, formatted like an OPM epoch, in UTC. Must be no later than the latest point
   * covered by the asteroid ephemeris.
   */
  private String accessEndTime;

  /**
   * The name of the table in the database from which telescope pointings should be retrieved.
   */
  private String pointingsTableName;

  /** Human-readable description of this object. */
  private String description;

  /**
   * A list of lines describing detected accesses. The format of the line will be
   * "<start_julian_date>,<end_julian_date>;<start_date>,<end_date>", where start_julian_date and end_julian_date are
   * Julian dates and start_date and end_date are ISO-format strings.
   */
  private List<String> accesses;

  public PropagationParameters getAsteroidPropagationParameters() {
    return asteroidPropagationParameters;
  }

  public void setAsteroidPropagationParameters(PropagationParameters asteroidPropagationParameters) {
    this.asteroidPropagationParameters = asteroidPropagationParameters;
  }

  public String getAsteroidPropagationUuid() {
    return asteroidPropagationUuid;
  }

  public void setAsteroidPropagationUuid(String asteroidPropagationUuid) {
    this.asteroidPropagationUuid = asteroidPropagationUuid;
  }

  public String getAccessStartTime() {
    return accessStartTime;
  }

  public void setAccessStartTime(String accessStartTime) {
    this.accessStartTime = accessStartTime;
  }

  public String getAccessEndTime() {
    return accessEndTime;
  }

  public void setAccessEndTime(String accessEndTime) {
    this.accessEndTime = accessEndTime;
  }

  public String getDescription() {
    return description;
  }

  public LsstAccessCalculation setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getPointingsTableName() {
    return pointingsTableName;
  }

  public void setPointingsTableName(String pointingsTableName) {
    this.pointingsTableName = pointingsTableName;
  }

  public List<String> getAccesses() {
    return accesses;
  }

  public void setAccesses(List<String> accesses) {
    this.accesses = accesses;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), asteroidPropagationParameters, asteroidPropagationUuid, accessStartTime,
        accessEndTime, pointingsTableName, description, accesses == null ? null : accesses.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LsstAccessCalculation other = (LsstAccessCalculation) obj;
    return super.equals(other) && Objects.equals(asteroidPropagationParameters, other.asteroidPropagationParameters)
        && Objects.equals(asteroidPropagationUuid, other.asteroidPropagationUuid)
        && Objects.equals(accessStartTime, other.accessStartTime) && Objects.equals(accessEndTime, other.accessEndTime)
        && Objects.equals(pointingsTableName, other.pointingsTableName)
        && Objects.equals(description, other.description) && accesses == null ? other.accesses == null
            : accesses.equals(other.accesses);
  }
}
