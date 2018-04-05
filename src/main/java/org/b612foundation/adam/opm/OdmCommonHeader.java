package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    for (String c : comments)
      res.addComment(new String(c));
    if (creationDate != null)
      res.setCreation_date(new String(creationDate));
    if (originator != null)
      res.setOriginator(new String(originator));
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
    final int prime = 31;
    int result = 1;
    result = prime * result + ((comments == null) ? 0 : comments.hashCode());
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((originator == null) ? 0 : originator.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OdmCommonHeader other = (OdmCommonHeader) obj;
    if (comments == null) {
      if (other.comments != null)
        return false;
    } else if (!comments.equals(other.comments))
      return false;
    if (creationDate == null) {
      if (other.creationDate != null)
        return false;
    } else if (!creationDate.equals(other.creationDate))
      return false;
    if (originator == null) {
      if (other.originator != null)
        return false;
    } else if (!originator.equals(other.originator))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("OdmCommonHeader [comments=").append(comments).append(", creationDate=").append(creationDate)
        .append(", originator=").append(originator).append("]");
    return builder.toString();
  }
}
