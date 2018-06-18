package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
      res.addComment(c);
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
    return Objects.hash(argOfPericenter, comments, eccentricity, gm, inclination, meanAnomaly, meanMotion, raOfAscNode,
        semiMajorAxis, trueAnomaly);
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
    // @formatter:off
    return Objects.equals(argOfPericenter, other.argOfPericenter)
        && Objects.equals(comments, other.comments)
        && Objects.equals(eccentricity, other.eccentricity)
        && Objects.equals(gm, other.gm)
        && Objects.equals(inclination, other.inclination)
        && Objects.equals(meanAnomaly, other.meanAnomaly)
        && Objects.equals(meanMotion, other.meanMotion)
        && Objects.equals(raOfAscNode, other.raOfAscNode)
        && Objects.equals(semiMajorAxis, other.semiMajorAxis)
        && Objects.equals(trueAnomaly, other.trueAnomaly);
    // @formatter:on
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
