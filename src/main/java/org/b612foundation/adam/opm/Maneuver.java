package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description of an individual maneuver (burn). Used in OPM. https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public class Maneuver implements Serializable {
  /**
   * Optional comments.
   */
  private List<String> comments = new ArrayList<>();
  /**
   * Epoch of ignition.
   */
  private String manEpochIgnition;
  /**
   * Durations in seconds, >=0.
   */
  private double duration;
  /**
   * Mass change in kg, <=0.
   */
  private double deltaMass;
  /**
   * Coordinate system for velocity increment vector.
   */
  private OdmCommonMetadata.ReferenceFrame manRefFrame;
  /**
   * Velocity increment vector components, km/s
   */
  private double manDv1, manDv2, manDv3;

  public Maneuver deepCopy() {
    Maneuver res = new Maneuver();
    for (String c : comments)
      res.addComment(c);
    res.setMan_epoch_ignition(manEpochIgnition);
    res.setDuration(duration);
    res.setDelta_mass(deltaMass);
    res.setMan_ref_frame(manRefFrame);
    res.setMan_dv_1(manDv1);
    res.setMan_dv_2(manDv2);
    res.setMan_dv_3(manDv3);
    return res;
  }

  public List<String> getComments() {
    return comments;
  }

  public Maneuver setComments(List<String> comments) {
    this.comments = comments;
    return this;
  }

  public Maneuver addComment(String comment) {
    this.comments.add(comment);
    return this;
  }

  public String getMan_epoch_ignition() {
    return manEpochIgnition;
  }

  public Maneuver setMan_epoch_ignition(String manEpochIgnition) {
    this.manEpochIgnition = manEpochIgnition;
    return this;
  }

  public double getDuration() {
    return duration;
  }

  public Maneuver setDuration(double duration) {
    this.duration = duration;
    return this;
  }

  public double getDelta_mass() {
    return deltaMass;
  }

  public Maneuver setDelta_mass(double deltaMass) {
    this.deltaMass = deltaMass;
    return this;
  }

  public OdmCommonMetadata.ReferenceFrame getMan_ref_frame() {
    return manRefFrame;
  }

  public Maneuver setMan_ref_frame(OdmCommonMetadata.ReferenceFrame manRefFrame) {
    this.manRefFrame = manRefFrame;
    return this;
  }

  public double getMan_dv_1() {
    return manDv1;
  }

  public Maneuver setMan_dv_1(double manDv1) {
    this.manDv1 = manDv1;
    return this;
  }

  public double getMan_dv_2() {
    return manDv2;
  }

  public Maneuver setMan_dv_2(double manDv2) {
    this.manDv2 = manDv2;
    return this;
  }

  public double getMan_dv_3() {
    return manDv3;
  }

  public Maneuver setMan_dv_3(double manDv3) {
    this.manDv3 = manDv3;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(comments, deltaMass, duration, manDv1, manDv2, manDv3, manEpochIgnition, manRefFrame);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Maneuver other = (Maneuver) obj;
    // @formatter:off
    return Objects.equals(comments, other.comments)
        && Objects.equals(deltaMass, other.deltaMass)
        && Objects.equals(duration, other.duration)
        && Objects.equals(manDv1, other.manDv1)
        && Objects.equals(manDv2, other.manDv2)
        && Objects.equals(manDv3, other.manDv3)
        && Objects.equals(manEpochIgnition, other.manEpochIgnition)
        && Objects.equals(manRefFrame, other.manRefFrame);
    // @formatter:on
  }

  @Override
  public String toString() {
    return "Maneuver [comments=" + comments + ", manEpochIgnition=" + manEpochIgnition +
        ", duration=" + duration + ", deltaMass=" + deltaMass + ", manRefFrame=" +
        manRefFrame + ", manDv1=" + manDv1 + ", manDv2=" + manDv2 + ", manDv3=" +
        manDv3 + "]";
  }
}
