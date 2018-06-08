package org.b612foundation.adam.datamodel;

import java.util.Objects;

public class AdamObject {

  /** Unique id for an object, generated on creation. */
  private String uuid;

  /** Project to which this object belongs. */
  private String project;

  public String getUuid() {
    return uuid;
  }

  public AdamObject setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public String getProject() {
    return project;
  }

  public AdamObject setProject(String project) {
    this.project = project;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, project);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AdamObject other = (AdamObject) obj;
    return Objects.equals(uuid, other.uuid) && Objects.equals(project, other.project);
  }

}
