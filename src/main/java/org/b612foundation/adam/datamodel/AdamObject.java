package org.b612foundation.adam.datamodel;

import java.util.Objects;

public class AdamObject {

  /** Id for an object, unique among all AdamObjects. */
  private String uuid;

  public String getUuid() {
    return uuid;
  }

  public AdamObject setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
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
    return Objects.equals(uuid, other.uuid);
  }

}
