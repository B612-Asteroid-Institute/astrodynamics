package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Osculating Keplerian Elements in the specified reference frame (none or all parameters of this block must be given.)
 * 
 * https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public class KeplerianElements implements Serializable {
  /** Optional comments. */
  private List<String> comments = new ArrayList<>();
  /*
   * In TLE-based OMM mean_motion is used instead of semi_major_axis. Have separate fields here to keep things simpler.
   */
  /** Semi-major axis, km */
  private double semiMajorAxis;
  /** Mean motion, rev/day, used in TLE-based OMM instead of semi-major axis. */
  private double meanMotion;
  /** Eccentricity, unitless */
  private double eccentricity;
  /** Inclination, degrees */
  private double inclination;
  /** Right ascention of ascending node, degrees */
  private double raOfAscNode;
  /** Argument of pericenter, degrees */
  private double argOfPericenter;
  /**
   * Technically, it's true anomaly OR mean anomaly, but have separate fields to keep things simpler.
   */
  private double trueAnomaly;
  /** Mean anomaly, degrees */
  private double meanAnomaly;
  /** Gravitational Coefficient (G x Mass). km^3/s^2. */
  private double gm;

  public KeplerianElements deepCopy() {
    KeplerianElements res = new KeplerianElements();
    for (String c : comments)
      res.addComment(new String(c));
    res.setSemi_major_axis(semiMajorAxis);
    res.setMean_motion(meanMotion);
    res.setEccentricity(eccentricity);
    res.setInclination(inclination);
    res.setRa_of_asc_node(raOfAscNode);
    res.setArg_of_pericenter(argOfPericenter);
    res.setTrue_anomaly(trueAnomaly);
    res.setMean_anomaly(meanAnomaly);
    res.setGm(gm);
    return res;
  }

  public List<String> getComments() {
    return comments;
  }

  public KeplerianElements setComments(List<String> comments) {
    this.comments = comments;
    return this;
  }

  public KeplerianElements addComment(String comment) {
    this.comments.add(comment);
    return this;
  }

  public double getSemi_major_axis() {
    return semiMajorAxis;
  }

  public KeplerianElements setSemi_major_axis(double semiMajorAxis) {
    this.semiMajorAxis = semiMajorAxis;
    return this;
  }

  public double getMean_motion() {
    return meanMotion;
  }

  public KeplerianElements setMean_motion(double meanMotion) {
    this.meanMotion = meanMotion;
    return this;
  }

  public double getEccentricity() {
    return eccentricity;
  }

  public KeplerianElements setEccentricity(double eccentricity) {
    this.eccentricity = eccentricity;
    return this;
  }

  public double getInclination() {
    return inclination;
  }

  public KeplerianElements setInclination(double inclination) {
    this.inclination = inclination;
    return this;
  }

  public double getRa_of_asc_node() {
    return raOfAscNode;
  }

  public KeplerianElements setRa_of_asc_node(double raan) {
    this.raOfAscNode = raan;
    return this;
  }

  public double getArg_of_pericenter() {
    return argOfPericenter;
  }

  public KeplerianElements setArg_of_pericenter(double argPericenter) {
    this.argOfPericenter = argPericenter;
    return this;
  }

  public double getTrue_anomaly() {
    return trueAnomaly;
  }

  public KeplerianElements setTrue_anomaly(double trueAnomaly) {
    this.trueAnomaly = trueAnomaly;
    return this;
  }

  public double getMean_anomaly() {
    return meanAnomaly;
  }

  public KeplerianElements setMean_anomaly(double meanAnomaly) {
    this.meanAnomaly = meanAnomaly;
    return this;
  }

  public double getGm() {
    return gm;
  }

  public KeplerianElements setGm(double gm) {
    this.gm = gm;
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(argOfPericenter);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((comments == null) ? 0 : comments.hashCode());
    temp = Double.doubleToLongBits(eccentricity);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(gm);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(inclination);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(meanAnomaly);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(meanMotion);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(raOfAscNode);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(semiMajorAxis);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(trueAnomaly);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    KeplerianElements other = (KeplerianElements) obj;
    if (Double.doubleToLongBits(argOfPericenter) != Double.doubleToLongBits(other.argOfPericenter))
      return false;
    if (comments == null) {
      if (other.comments != null)
        return false;
    } else if (!comments.equals(other.comments))
      return false;
    if (Double.doubleToLongBits(eccentricity) != Double.doubleToLongBits(other.eccentricity))
      return false;
    if (Double.doubleToLongBits(gm) != Double.doubleToLongBits(other.gm))
      return false;
    if (Double.doubleToLongBits(inclination) != Double.doubleToLongBits(other.inclination))
      return false;
    if (Double.doubleToLongBits(meanAnomaly) != Double.doubleToLongBits(other.meanAnomaly))
      return false;
    if (Double.doubleToLongBits(meanMotion) != Double.doubleToLongBits(other.meanMotion))
      return false;
    if (Double.doubleToLongBits(raOfAscNode) != Double.doubleToLongBits(other.raOfAscNode))
      return false;
    if (Double.doubleToLongBits(semiMajorAxis) != Double.doubleToLongBits(other.semiMajorAxis))
      return false;
    if (Double.doubleToLongBits(trueAnomaly) != Double.doubleToLongBits(other.trueAnomaly))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("KeplerianElements [comments=").append(comments).append(", semiMajorAxis=").append(semiMajorAxis)
        .append(", meanMotion=").append(meanMotion).append(", eccentricity=").append(eccentricity)
        .append(", inclination=").append(inclination).append(", raOfAscNode=").append(raOfAscNode)
        .append(", argOfPericenter=").append(argOfPericenter).append(", trueAnomaly=").append(trueAnomaly)
        .append(", meanAnomaly=").append(meanAnomaly).append(", gm=").append(gm).append("]");
    return builder.toString();
  }
}
