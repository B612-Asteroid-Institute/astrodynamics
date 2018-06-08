package org.b612foundation.adam.datamodel;

import java.util.List;
import java.util.Objects;

public class BatchPropagation extends AdamObject {

  /** Parameters used to generate the ephemerides of propagations in this batch. */
  private PropagationParameters templatePropagationParameters;

  /** Type of this batch propagation. Options: HYPERCUBE, SINGLE. Type may indicate presence of custom fields in OPM. */
  private String type;

  /** Summary of the results. */
  private String summary;

  /** Uuids of single propagations carried out as part of this batch. */
  private List<String> propagationUuids;

  public PropagationParameters getTemplatePropagationParameters() {
    return templatePropagationParameters;
  }

  public BatchPropagation setTemplatePropagationParameters(PropagationParameters templatePropagationParameters) {
    this.templatePropagationParameters = templatePropagationParameters;
    return this;
  }

  public String getType() {
    return type;
  }

  public BatchPropagation setType(String type) {
    this.type = type;
    return this;
  }

  public String getSummary() {
    return summary;
  }

  public BatchPropagation setSummary(String summary) {
    this.summary = summary;
    return this;
  }

  public List<String> getPropagationUuids() {
    return propagationUuids;
  }

  public BatchPropagation setPropagationUuids(List<String> propagationUuids) {
    this.propagationUuids = propagationUuids;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), templatePropagationParameters, type, summary, propagationUuids.hashCode());
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
        && Objects.equals(summary, other.summary) && Objects.equals(propagationUuids, other.propagationUuids);
  }

}
