package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * State Vector Components. The coordinate system is given in the metadata. https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public class StateVector implements Serializable {
  /** Optional comments. */
  private List<String> comments = new ArrayList<>();
  /** For state vector and Keplerian elements. */
  private String epoch = null;
  /** Coordinates in km */
  private double x, y, z;
  /** Velocity in km/s */
  private double xDot, yDot, zDot;

  public StateVector deepCopy() {
    StateVector res = new StateVector();
    for (String c : comments)
      res.addComment(c);
    res.setEpoch(epoch);
    res.setX(x);
    res.setY(y);
    res.setZ(z);
    res.setX_dot(xDot);
    res.setY_dot(yDot);
    res.setZ_dot(zDot);
    return res;
  }

  public List<String> getComments() {
    return comments;
  }

  public StateVector setComments(List<String> comments) {
    this.comments = comments;
    return this;
  }

  public StateVector addComment(String comment) {
    this.comments.add(comment);
    return this;
  }

  public String getEpoch() {
    return epoch;
  }

  public StateVector setEpoch(String epoch) {
    this.epoch = epoch;
    return this;
  }

  public double getX() {
    return x;
  }

  public StateVector setX(double x) {
    this.x = x;
    return this;
  }

  public double getY() {
    return y;
  }

  public StateVector setY(double y) {
    this.y = y;
    return this;
  }

  public double getZ() {
    return z;
  }

  public StateVector setZ(double z) {
    this.z = z;
    return this;
  }

  public double getX_dot() {
    return xDot;
  }

  public StateVector setX_dot(double xDot) {
    this.xDot = xDot;
    return this;
  }

  public double getY_dot() {
    return yDot;
  }

  public StateVector setY_dot(double yDot) {
    this.yDot = yDot;
    return this;
  }

  public double getZ_dot() {
    return zDot;
  }

  public StateVector setZ_dot(double zDot) {
    this.zDot = zDot;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(comments, epoch, x, y, z, xDot, yDot, zDot);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StateVector other = (StateVector) obj;
    // @formatter:off
    return Objects.equals(comments, other.comments) 
        && Objects.equals(epoch, other.epoch) 
        && Objects.equals(x, other.x) 
        && Objects.equals(y, other.y) 
        && Objects.equals(z, other.z) 
        && Objects.equals(xDot, other.xDot) 
        && Objects.equals(yDot, other.yDot) 
        && Objects.equals(zDot, other.zDot);
    // @formatter:on
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("StateVector [comments=").append(comments).append(", epoch=").append(epoch).append(", x=").append(x)
        .append(", y=").append(y).append(", z=").append(z).append(", xDot=").append(xDot).append(", yDot=").append(yDot)
        .append(", zDot=").append(zDot).append("]");
    return builder.toString();
  }
}
