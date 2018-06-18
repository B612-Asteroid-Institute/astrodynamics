package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.Objects;

public class AdamField implements Serializable {
  private String key, value;

  AdamField(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AdamField other = (AdamField) obj;
    return Objects.equals(key, other.key) && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(key).append("=").append(value);
    return builder.toString();
  }
}
