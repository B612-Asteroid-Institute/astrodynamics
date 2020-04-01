package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Header fields are common for all three ODM messages. https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public class OdmCommonHeader implements Serializable {
  /** Optional comments. */
  private List<String> comments = new ArrayList<>();

  /** Creation date is always in UTC. */
  private String creationDate = null;

  /** Creating agency or operator. */
  private String originator = null;

  public OdmCommonHeader deepCopy() {
    OdmCommonHeader res = new OdmCommonHeader();
    for (String c : comments) res.addComment(c);
    res.setCreation_date(creationDate);
    res.setOriginator(originator);
    return res;
  }

  public List<String> getComments() {
    return comments;
  }

  public OdmCommonHeader addComment(String comment) {
    comments.add(comment);
    return this;
  }

  public String getCreation_date() {
    return creationDate;
  }

  public OdmCommonHeader setCreation_date(String date) {
    creationDate = date;
    return this;
  }

  public String getOriginator() {
    return originator;
  }

  public OdmCommonHeader setOriginator(String originator) {
    this.originator = originator;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(comments, creationDate, originator);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    OdmCommonHeader other = (OdmCommonHeader) obj;
    return Objects.equals(comments, other.comments)
        && Objects.equals(creationDate, other.creationDate)
        && Objects.equals(originator, other.originator);
  }

  @Override
  public String toString() {
    return "OdmCommonHeader [comments="
        + comments
        + ", creationDate="
        + creationDate
        + ", originator="
        + originator
        + "]";
  }
}
