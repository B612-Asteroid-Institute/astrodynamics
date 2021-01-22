package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description of a spacecraft. May be included in OPM or OMM.
 * https://public.ccsds.org/Pubs/502x0b2c1e2.pdf
 */
public class SpacecraftParameters implements Serializable {
  /** Optional comments. */
  private List<String> comments = new ArrayList<>();
  /** Mass, kg */
  private double mass;
  /** Solar radiation pressure area, m^2. */
  private double solarRadArea;
  /** Solar radiation pressure coefficient. */
  private double solarRadCoeff;
  /** Drag area, m^2. */
  private double dragArea;
  /** Drag coefficient. */
  private double dragCoeff;

  public SpacecraftParameters deepCopy() {
    SpacecraftParameters copy = new SpacecraftParameters();
    for (String comment : comments) {
      copy.addComment(comment);
    }
    copy.setMass(mass);
    copy.setSolar_rad_area(solarRadArea);
    copy.setSolar_rad_coeff(solarRadCoeff);
    copy.setDrag_area(dragArea);
    copy.setDrag_coeff(dragCoeff);
    return copy;
  }

  public List<String> getComments() {
    return comments;
  }

  public SpacecraftParameters setComments(List<String> comments) {
    this.comments = comments;
    return this;
  }

  public SpacecraftParameters addComment(String comment) {
    this.comments.add(comment);
    return this;
  }

  public double getMass() {
    return mass;
  }

  public SpacecraftParameters setMass(double mass) {
    this.mass = mass;
    return this;
  }

  public double getSolar_rad_area() {
    return solarRadArea;
  }

  public SpacecraftParameters setSolar_rad_area(double solarRadArea) {
    this.solarRadArea = solarRadArea;
    return this;
  }

  public double getSolar_rad_coeff() {
    return solarRadCoeff;
  }

  public SpacecraftParameters setSolar_rad_coeff(double solarRadCoeff) {
    this.solarRadCoeff = solarRadCoeff;
    return this;
  }

  public double getDrag_area() {
    return dragArea;
  }

  public SpacecraftParameters setDrag_area(double dragArea) {
    this.dragArea = dragArea;
    return this;
  }

  public double getDrag_coeff() {
    return dragCoeff;
  }

  public SpacecraftParameters setDrag_coeff(double dragCoeff) {
    this.dragCoeff = dragCoeff;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(comments, dragArea, dragCoeff, mass, solarRadArea, solarRadCoeff);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SpacecraftParameters other = (SpacecraftParameters) obj;
    // @formatter:off
    return Objects.equals(comments, other.comments)
        && Objects.equals(dragArea, other.dragArea)
        && Objects.equals(dragCoeff, other.dragCoeff)
        && Objects.equals(mass, other.mass)
        && Objects.equals(solarRadArea, other.solarRadArea)
        && Objects.equals(solarRadCoeff, other.solarRadCoeff);
    // @formatter:on
  }

  @Override
  public String toString() {
    return "SpacecraftParameters [comments="
        + comments
        + ", mass="
        + mass
        + ", solarRadArea="
        + solarRadArea
        + ", solarRadCoeff="
        + solarRadCoeff
        + ", dragArea="
        + dragArea
        + ", dragCoeff="
        + dragCoeff
        + "]";
  }
}
