package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Position/Velocity Covariance Matrix (6x6 Lower Triangular Form. None or all parameters of the
 * matrix must be given. https://public.ccsds.org/Pubs/502x0b2c1.pdf
 *
 * <pre>
 * | CXX                                                     |
 * | CYX     CYY                                             |
 * | CZX     CZY     CZZ                                     |
 * | CXdotX  CXdotY  CXdotZ  CXdotXdot                       |
 * | CYdotX  CYdotY  CYdotZ  CYdotXdot  CYdotYdot            |
 * | CZdotX  CZdotY  CZdotZ  CZdotXdot  CZdotYdot  CZDotZdot |
 * </pre>
 */
public class CartesianCovariance implements Serializable {
  /** Optional comments. */
  private List<String> comments = new ArrayList<>();
  /** Separate Epoch for covariance, used only in OEM. */
  private String epoch;
  /** Reference frame. If omited, same as metadata */
  private OdmCommonMetadata.ReferenceFrame covRefFrame = null;
  /**
   * Covariance matrix. The variables are in order x,y,z,x',y',z'. Units are km and km/s, with
   * corresponding products.
   */
  private double CXX;

  private double CYX, CYY;
  private double CZX, CZY, CZZ;
  private double CXdotX, CXdotY, CXdotZ, CXdotXdot;
  private double CYdotX, CYdotY, CYdotZ, CYdotXdot, CYdotYdot;
  private double CZdotX, CZdotY, CZdotZ, CZdotXdot, CZdotYdot, CZdotZdot;

  public CartesianCovariance() {}

  public CartesianCovariance deepCopy() {
    CartesianCovariance copy = new CartesianCovariance();
    for (String comment : comments) {
      copy.addComment(comment);
    }

    copy.setEpoch(epoch);
    copy.setCov_ref_frame(covRefFrame);

    copy.setCx_x(CXX);

    copy.setCy_x(CYX);
    copy.setCy_y(CYY);

    copy.setCz_x(CZX);
    copy.setCz_y(CZY);
    copy.setCz_z(CZZ);

    copy.setCx_dot_x(CXdotX);
    copy.setCx_dot_y(CXdotY);
    copy.setCx_dot_z(CXdotZ);
    copy.setCx_dot_x_dot(CXdotXdot);

    copy.setCy_dot_x(CYdotX);
    copy.setCy_dot_y(CYdotY);
    copy.setCy_dot_z(CYdotZ);
    copy.setCy_dot_x_dot(CYdotXdot);
    copy.setCy_dot_y_dot(CYdotYdot);

    copy.setCz_dot_x(CZdotX);
    copy.setCz_dot_y(CZdotY);
    copy.setCz_dot_z(CZdotZ);
    copy.setCz_dot_x_dot(CZdotXdot);
    copy.setCz_dot_y_dot(CZdotYdot);
    copy.setCz_dot_z_dot(CZdotZdot);

    return copy;
  }

  public List<String> getComments() {
    return comments;
  }

  public CartesianCovariance setComments(List<String> comments) {
    this.comments = comments;
    return this;
  }

  public CartesianCovariance addComment(String comment) {
    this.comments.add(comment);
    return this;
  }

  public String getEpoch() {
    return epoch;
  }

  public CartesianCovariance setEpoch(String epoch) {
    this.epoch = epoch;
    return this;
  }

  public OdmCommonMetadata.ReferenceFrame getCov_ref_frame() {
    return covRefFrame;
  }

  public CartesianCovariance setCov_ref_frame(OdmCommonMetadata.ReferenceFrame covRefFrame) {
    this.covRefFrame = covRefFrame;
    return this;
  }

  public double getCx_x() {
    return CXX;
  }

  public CartesianCovariance setCx_x(double cXX) {
    this.CXX = cXX;
    return this;
  }

  public double getCy_x() {
    return CYX;
  }

  public CartesianCovariance setCy_x(double cYX) {
    CYX = cYX;
    return this;
  }

  public double getCy_y() {
    return CYY;
  }

  public CartesianCovariance setCy_y(double cYY) {
    CYY = cYY;
    return this;
  }

  public double getCz_x() {
    return CZX;
  }

  public CartesianCovariance setCz_x(double cZX) {
    CZX = cZX;
    return this;
  }

  public double getCz_y() {
    return CZY;
  }

  public CartesianCovariance setCz_y(double cZY) {
    CZY = cZY;
    return this;
  }

  public double getCz_z() {
    return CZZ;
  }

  public CartesianCovariance setCz_z(double cZZ) {
    CZZ = cZZ;
    return this;
  }

  public double getCx_dot_x() {
    return CXdotX;
  }

  public CartesianCovariance setCx_dot_x(double cXdotX) {
    CXdotX = cXdotX;
    return this;
  }

  public double getCx_dot_y() {
    return CXdotY;
  }

  public CartesianCovariance setCx_dot_y(double cXdotY) {
    CXdotY = cXdotY;
    return this;
  }

  public double getCx_dot_z() {
    return CXdotZ;
  }

  public CartesianCovariance setCx_dot_z(double cXdotZ) {
    CXdotZ = cXdotZ;
    return this;
  }

  public double getCx_dot_x_dot() {
    return CXdotXdot;
  }

  public CartesianCovariance setCx_dot_x_dot(double cXdotXdot) {
    CXdotXdot = cXdotXdot;
    return this;
  }

  public double getCy_dot_x() {
    return CYdotX;
  }

  public CartesianCovariance setCy_dot_x(double cYdotX) {
    CYdotX = cYdotX;
    return this;
  }

  public double getCy_dot_y() {
    return CYdotY;
  }

  public CartesianCovariance setCy_dot_y(double cYdotY) {
    CYdotY = cYdotY;
    return this;
  }

  public double getCy_dot_z() {
    return CYdotZ;
  }

  public CartesianCovariance setCy_dot_z(double cYdotZ) {
    CYdotZ = cYdotZ;
    return this;
  }

  public double getCy_dot_x_dot() {
    return CYdotXdot;
  }

  public CartesianCovariance setCy_dot_x_dot(double cYdotXdot) {
    CYdotXdot = cYdotXdot;
    return this;
  }

  public double getCy_dot_y_dot() {
    return CYdotYdot;
  }

  public CartesianCovariance setCy_dot_y_dot(double cYdotYdot) {
    CYdotYdot = cYdotYdot;
    return this;
  }

  public double getCz_dot_x() {
    return CZdotX;
  }

  public CartesianCovariance setCz_dot_x(double cZdotX) {
    CZdotX = cZdotX;
    return this;
  }

  public double getCz_dot_y() {
    return CZdotY;
  }

  public CartesianCovariance setCz_dot_y(double cZdotY) {
    CZdotY = cZdotY;
    return this;
  }

  public double getCz_dot_z() {
    return CZdotZ;
  }

  public CartesianCovariance setCz_dot_z(double cZdotZ) {
    CZdotZ = cZdotZ;
    return this;
  }

  public double getCz_dot_x_dot() {
    return CZdotXdot;
  }

  public CartesianCovariance setCz_dot_x_dot(double cZdotXdot) {
    CZdotXdot = cZdotXdot;
    return this;
  }

  public double getCz_dot_y_dot() {
    return CZdotYdot;
  }

  public CartesianCovariance setCz_dot_y_dot(double cZdotYdot) {
    CZdotYdot = cZdotYdot;
    return this;
  }

  public double getCz_dot_z_dot() {
    return CZdotZdot;
  }

  public CartesianCovariance setCz_dot_z_dot(double cZdotZdot) {
    CZdotZdot = cZdotZdot;
    return this;
  }

  @Override
  public int hashCode() {
    // @formatter:off
    return Objects.hash(
        comments,
        covRefFrame,
        epoch,
        CXX,
        CYX,
        CYY,
        CZX,
        CZY,
        CZZ,
        CXdotX,
        CXdotY,
        CXdotZ,
        CXdotXdot,
        CYdotX,
        CYdotY,
        CYdotZ,
        CYdotXdot,
        CYdotYdot,
        CZdotX,
        CZdotY,
        CZdotZ,
        CZdotXdot,
        CZdotYdot,
        CZdotZdot);
    // @formatter:on
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CartesianCovariance other = (CartesianCovariance) obj;
    // @formatter:off
    return Objects.equals(comments, other.comments)
        && Objects.equals(covRefFrame, other.covRefFrame)
        && Objects.equals(epoch, other.epoch)
        && Objects.equals(CXX, other.CXX)
        && Objects.equals(CYX, other.CYX)
        && Objects.equals(CYY, other.CYY)
        && Objects.equals(CZX, other.CZX)
        && Objects.equals(CZY, other.CZY)
        && Objects.equals(CZZ, other.CZZ)
        && Objects.equals(CXdotX, other.CXdotX)
        && Objects.equals(CXdotY, other.CXdotY)
        && Objects.equals(CXdotZ, other.CXdotZ)
        && Objects.equals(CXdotXdot, other.CXdotXdot)
        && Objects.equals(CYdotX, other.CYdotX)
        && Objects.equals(CYdotY, other.CYdotY)
        && Objects.equals(CYdotZ, other.CYdotZ)
        && Objects.equals(CYdotXdot, other.CYdotXdot)
        && Objects.equals(CYdotYdot, other.CYdotYdot)
        && Objects.equals(CZdotX, other.CZdotX)
        && Objects.equals(CZdotY, other.CZdotY)
        && Objects.equals(CZdotZ, other.CZdotZ)
        && Objects.equals(CZdotXdot, other.CZdotXdot)
        && Objects.equals(CZdotYdot, other.CZdotYdot)
        && Objects.equals(CZdotZdot, other.CZdotZdot);
    // @formatter:on
  }

  @Override
  public String toString() {
    return "CovarianceMatrix [comments="
        + comments
        + ", epoch="
        + epoch
        + ", covRefFrame="
        + covRefFrame
        + ", cXX="
        + CXX
        + ", CYX="
        + CYX
        + ", CYY="
        + CYY
        + ", CZX="
        + CZX
        + ", CZY="
        + CZY
        + ", CZZ="
        + CZZ
        + ", CXdotX="
        + CXdotX
        + ", CXdotY="
        + CXdotY
        + ", CXdotZ="
        + CXdotZ
        + ", CXdotXdot="
        + CXdotXdot
        + ", CYdotX="
        + CYdotX
        + ", CYdotY="
        + CYdotY
        + ", CYdotZ="
        + CYdotZ
        + ", CYdotXdot="
        + CYdotXdot
        + ", CYdotYdot="
        + CYdotYdot
        + ", CZdotX="
        + CZdotX
        + ", CZdotY="
        + CZdotY
        + ", CZdotZ="
        + CZdotZ
        + ", CZdotXdot="
        + CZdotXdot
        + ", CZdotYdot="
        + CZdotYdot
        + ", CZdotZdot="
        + CZdotZdot
        + "]";
  }
}
