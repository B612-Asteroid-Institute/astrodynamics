package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Metadata fields common to all three ODM messages. https://public.ccsds.org/Pubs/502x0b2c1e2.pdf
 */
public class OdmCommonMetadata implements Serializable {
  /**
   * The origin of the reference frame. The standard does not restrict the value, but we will use an
   * enum since we do not plan to propagate in arbitrary frames anyway.
   */
  public enum CenterName {
    EARTH,
    SUN
  }

  /**
   * Name of the reference frame in which the state vector and optional Keplerian element data are
   * given. The standard (appendix A2) defines multiple values. Appendix A3 defines local frames
   * that can be used only for maneuvers and covariances.
   */
  public enum ReferenceFrame {
    /**
     * The two local frames that can be used only for maneuvers or covariances. Radial, Transverse,
     * Normal. Can also be marked as RSW.
     */
    RTN,
    /**
     * A local orbital coordinate frame that has the x-axis along the velocity vector, W along the
     * orbital angular momentum vector, and N completes the right handed system.
     */
    TNW,

    // The rest of the frames can be used for state vectors, Keplerian elements,
    // maneuvers, or covariances.
    /** Earth Mean Equator and Equinox of J2000 */
    EME2000,
    /** Earth Mean Ecliptic and Equinox of J2000. Any center may be specified. */
    EMEME2000,
    /** Geocentric Celestial Reference Frame */
    GCRF,
    /** Greenwich Rotating Coordinates */
    GRC,
    /** International Celestial Reference Frame */
    ICRF,
    /** International Terrestrial Reference Frame 2000 */
    ITRF2000,
    /** International Terrestrial Reference Frame 1993 */
    ITRF93,
    /** International Terrestrial Reference Frame 1997 */
    ITRF97,
    /** Mars Centered Inertial */
    MCI,
    /** True of Date, Rotating */
    TDR,
    /** True Equator Mean Equinox */
    TEME,
    /** True of Date */
    TOD,
    /**
     * ODM non-standard referenece frame representing the IAU76 Mean Ecliptic Plane at J2000 epoch
     */
    J2000_IAU76ECLIP,
    /** Earth Centered, Earth Fixed */
    ECEF,
    /** Earth Centered, Inertial */
    ECI,
  }

  /**
   * The standard (appendix A1) lists multiple recognized time systems. We start with what we
   * support and extend this set as needed.
   */
  public enum TimeSystem {
    /** Coordinated Universal Time. */
    UTC,
    /** International Atomic Time. */
    TAI,
    /** Terrestrial Time. */
    TT,
    /** Global Positioning System time. */
    GPS,
    /** Barycentric Dynamical Time. */
    TDB,
    /** Barycentric Coordinate Time. */
    TCB
  }

  /** Optional comments */
  private List<String> comments = new ArrayList<>();

  /**
   * Spacecraft name for which the orbit state is provided. There is no CCSDS-based restriction on
   * the value for this keyword, but it is recommended to use names from the SPACEWARN Bulletin.
   */
  private String objectName = null;

  /**
   * Required Object ID. There is no CCSDS-based restriction on the value for this keyword, but it
   * is recommended that values be the international spacecraft designator as published in the
   * SPACEWARN Bulletin. Recommended values have the format YYYYNNNP{PP}, where: YYYY = Year of
   * launch. NNN = Three digit serial number of launch in year YYYY (with leading zeros) P{PP} = At
   * least one capital letter for the identification of the part brought into space by the launch.
   */
  private String objectId = null;

  /**
   * Origin of reference frame, which may be a natural solar system body (planets, asteroids,
   * comets, and natural satellites), including any planet barycenter or the solar system
   * barycenter, or another spacecraft. There is no CCSDS-based restriction on the value for this
   * keyword, but for natural bodies it is recommended to use names from the NASA/JPL Solar System
   * Dynamics Group at http://ssd.jpl.nasa.gov
   */
  private CenterName centerName = null;

  /**
   * Name of the reference frame in which the state vector and/or the Keplerian element data are
   * given.
   */
  private ReferenceFrame refFrame = null;

  /**
   * Epoch of reference frame, if not intrinsic to the definition of the reference frame (optional).
   */
  private String refFrameEpoch = null;

  /** Time system applies to state vector, maneuver, and covariance data. */
  private TimeSystem timeSystem = null;

  public OdmCommonMetadata deepCopy() {
    OdmCommonMetadata res = new OdmCommonMetadata();
    for (String c : comments) res.addComment(c);
    res.setObject_name(objectName);
    res.setObject_id(objectId);
    res.setCenter_name(centerName);
    res.setRef_frame(refFrame);
    res.setRef_frame_epoch(refFrameEpoch);
    res.setTime_system(timeSystem);
    return res;
  }

  public List<String> getComments() {
    return comments;
  }

  public OdmCommonMetadata addComment(String comment) {
    this.comments.add(comment);
    return this;
  }

  public OdmCommonMetadata setComments(List<String> comments) {
    this.comments = comments;
    return this;
  }

  public String getObject_name() {
    return objectName;
  }

  public OdmCommonMetadata setObject_name(String objectName) {
    this.objectName = objectName;
    return this;
  }

  public String getObject_id() {
    return objectId;
  }

  public OdmCommonMetadata setObject_id(String objectId) {
    this.objectId = objectId;
    return this;
  }

  public CenterName getCenter_name() {
    return centerName;
  }

  public OdmCommonMetadata setCenter_name(CenterName centerName) {
    this.centerName = centerName;
    return this;
  }

  public ReferenceFrame getRef_frame() {
    return refFrame;
  }

  public OdmCommonMetadata setRef_frame(ReferenceFrame refFrame) {
    this.refFrame = refFrame;
    return this;
  }

  public String getRef_frame_epoch() {
    return refFrameEpoch;
  }

  public OdmCommonMetadata setRef_frame_epoch(String refFrameEpoch) {
    this.refFrameEpoch = refFrameEpoch;
    return this;
  }

  public TimeSystem getTime_system() {
    return timeSystem;
  }

  public OdmCommonMetadata setTime_system(TimeSystem timeSystem) {
    this.timeSystem = timeSystem;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        centerName, comments, objectId, objectName, refFrame, refFrameEpoch, timeSystem);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    OdmCommonMetadata other = (OdmCommonMetadata) obj;
    // @formatter:off
    return Objects.equals(centerName, other.centerName)
        && Objects.equals(comments, other.comments)
        && Objects.equals(objectId, other.objectId)
        && Objects.equals(objectName, other.objectName)
        && Objects.equals(refFrame, other.refFrame)
        && Objects.equals(refFrameEpoch, other.refFrameEpoch)
        && Objects.equals(timeSystem, other.timeSystem);
    // @formatter:on
  }

  @Override
  public String toString() {
    return "OdmCommonMetadata [comments="
        + comments
        + ", objectName="
        + objectName
        + ", objectId="
        + objectId
        + ", centerName="
        + centerName
        + ", refFrame="
        + refFrame
        + ", refFrameEpoch="
        + refFrameEpoch
        + ", timeSystem="
        + timeSystem
        + "]";
  }
}
