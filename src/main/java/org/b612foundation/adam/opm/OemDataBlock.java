package org.b612foundation.adam.opm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * One OrbitEphemerisMessage can contain multiple ephemerides blocks, each with its own metadata block and optional
 * covariances.
 */
public class OemDataBlock {
  /**
   * Optional comments.
   */
  private List<String> comments = new ArrayList<>();
  /**
   * Metadata, required
   */
  private OemMetadata metadata = null;
  /**
   * Data, required: date, x, y, x, vx, vy, vz. Accelerations are optional, ignore them for now.
   */
  private List<OemDataLine> ephemeris = new ArrayList<>();
  /**
   * Optional covariances, each with its own epoch.
   */
  private List<CovarianceMatrix> covariances = new ArrayList<>();

  public List<String> getComments() {
    return comments;
  }

  public OemDataBlock setComments(List<String> comments) {
    this.comments = comments;
    return this;
  }

  public OemDataBlock addComment(String comment) {
    this.comments.add(comment);
    return this;
  }

  public OemMetadata getMetadata() {
    return metadata;
  }

  public OemDataBlock setMetadata(OemMetadata metadata) {
    this.metadata = metadata;
    return this;
  }

  public List<OemDataLine> getLines() {
    return ephemeris;
  }

  public OemDataBlock addLine(String date, double x, double y, double z, double vx, double vy, double vz) {
    ephemeris.add(new OemDataLine(date, x, y, z, vx, vy, vz));
    return this;
  }

  public List<CovarianceMatrix> getCovariances() {
    return covariances;
  }

  public OemDataBlock addCovariance(CovarianceMatrix covariance) {
    this.covariances.add(covariance);
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(comments, metadata, ephemeris, covariances);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OemDataBlock other = (OemDataBlock) obj;
    return Objects.equals(comments, other.comments) && Objects.equals(metadata, other.metadata)
        && Objects.equals(ephemeris, other.ephemeris) && Objects.equals(covariances, other.covariances);
  }
}
