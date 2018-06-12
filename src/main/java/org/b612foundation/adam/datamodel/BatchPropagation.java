package org.b612foundation.adam.datamodel;

import java.util.Objects;

public class BatchPropagation extends AdamObject {

  /** Parameters used to generate the ephemerides of propagations in this batch. */
  private PropagationParameters templatePropagationParameters;

  /** Human-readable description of this object. */
  private String description;

  /** Summary of the results. */
  private String summary;

  public PropagationParameters getTemplatePropagationParameters() {
    return templatePropagationParameters;
  }

  public BatchPropagation setTemplatePropagationParameters(PropagationParameters templatePropagationParameters) {
    this.templatePropagationParameters = templatePropagationParameters;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public BatchPropagation setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getSummary() {
    return summary;
  }

  public BatchPropagation setSummary(String summary) {
    this.summary = summary;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), templatePropagationParameters, description, summary);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BatchPropagation other = (BatchPropagation) obj;
    return super.equals(other) && Objects.equals(templatePropagationParameters, other.templatePropagationParameters)
        && Objects.equals(description, other.description) && Objects.equals(summary, other.summary);
  }

}
