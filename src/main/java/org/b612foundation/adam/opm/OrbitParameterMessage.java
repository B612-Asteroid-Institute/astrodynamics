package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Orbit Parameter Message, or OPM, one of the three high-level message types defined in CCSDS ODM
 * standard. https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public class OrbitParameterMessage implements Serializable {

  /** OPM version is required by standard. Should be the same value always. */
  private String ccsdsOpmVers = "2.0";
  /** Headers are common for all message types. */
  private OdmCommonHeader header;
  /** Most of the metadata fields are common for all message types. */
  private OdmCommonMetadata metadata;
  /** Position/velocity at a given time. */
  private StateVector stateVector;
  /** Keplerian elements. */
  private KeplerianElements keplerian;
  /** Optional spacecraft details. */
  private SpacecraftParameters spacecraft;
  /** Covariance for position/velocity, optional. */
  private CovarianceMatrix covariance;
  /** 0 or more maneuvers. */
  private List<Maneuver> maneuvers = new ArrayList<>();
  /**
   * The standard also allows USER_DEFINED_X parameters. We ignore all except ADAM-specific ones
   * (start with USER_DEFINED_ADAM_). The order of user-defined fields matters, because there is no
   * way to group these fields other than inserting some sort of header or separator fields. So, we
   * cannot use proto map here, because that would lose ordering.
   */
  private List<AdamField> adamFields = new ArrayList<>();

  public OrbitParameterMessage deepCopy() {
    OrbitParameterMessage res = new OrbitParameterMessage();
    res.setCcsds_opm_vers(ccsdsOpmVers);
    if (header != null) res.setHeader(header.deepCopy());
    if (metadata != null) res.setMetadata(metadata.deepCopy());
    if (stateVector != null) res.setState_vector(stateVector.deepCopy());
    if (keplerian != null) res.setKeplerian(keplerian.deepCopy());
    if (spacecraft != null) res.setSpacecraft(spacecraft.deepCopy());
    if (covariance != null) res.setCovariance(covariance.deepCopy());
    for (Maneuver man : maneuvers) res.addManeuver(man.deepCopy());
    for (AdamField af : adamFields) res.addAdam_field(af.getKey(), af.getValue());
    return res;
  }

  public String getCcsds_opm_vers() {
    return ccsdsOpmVers;
  }

  public OrbitParameterMessage setCcsds_opm_vers(String ccsdsOpmVers) {
    this.ccsdsOpmVers = ccsdsOpmVers;
    return this;
  }

  public OdmCommonHeader getHeader() {
    return header;
  }

  public OrbitParameterMessage setHeader(OdmCommonHeader header) {
    this.header = header;
    return this;
  }

  public OdmCommonMetadata getMetadata() {
    return metadata;
  }

  public OrbitParameterMessage setMetadata(OdmCommonMetadata metadata) {
    this.metadata = metadata;
    return this;
  }

  public StateVector getState_vector() {
    return stateVector;
  }

  public OrbitParameterMessage setState_vector(StateVector stateVector) {
    this.stateVector = stateVector;
    return this;
  }

  public KeplerianElements getKeplerian() {
    return keplerian;
  }

  public OrbitParameterMessage setKeplerian(KeplerianElements keplerian) {
    this.keplerian = keplerian;
    return this;
  }

  public SpacecraftParameters getSpacecraft() {
    return spacecraft;
  }

  public OrbitParameterMessage setSpacecraft(SpacecraftParameters spacecraft) {
    this.spacecraft = spacecraft;
    return this;
  }

  public CovarianceMatrix getCovariance() {
    return covariance;
  }

  public OrbitParameterMessage setCovariance(CovarianceMatrix covariance) {
    this.covariance = covariance;
    return this;
  }

  public List<Maneuver> getManeuvers() {
    return maneuvers;
  }

  public OrbitParameterMessage setManeuvers(List<Maneuver> maneuvers) {
    this.maneuvers = maneuvers;
    return this;
  }

  public OrbitParameterMessage addManeuver(Maneuver maneuver) {
    this.maneuvers.add(maneuver);
    return this;
  }

  public List<AdamField> getAdam_fields() {
    return adamFields;
  }

  public OrbitParameterMessage setAdam_fields(List<AdamField> adamFields) {
    this.adamFields = adamFields;
    return this;
  }

  public OrbitParameterMessage addAdam_field(String key, String value) {
    this.adamFields.add(new AdamField(key, value));
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        adamFields,
        ccsdsOpmVers,
        covariance,
        header,
        keplerian,
        maneuvers,
        metadata,
        spacecraft,
        stateVector);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    OrbitParameterMessage other = (OrbitParameterMessage) obj;
    // @formatter:off
    return Objects.equals(adamFields, other.adamFields)
        && Objects.equals(ccsdsOpmVers, other.ccsdsOpmVers)
        && Objects.equals(covariance, other.covariance)
        && Objects.equals(header, other.header)
        && Objects.equals(keplerian, other.keplerian)
        && Objects.equals(maneuvers, other.maneuvers)
        && Objects.equals(metadata, other.metadata)
        && Objects.equals(spacecraft, other.spacecraft)
        && Objects.equals(stateVector, other.stateVector);
    // @formatter:on
  }

  @Override
  public String toString() {
    return "OrbitParameterMessage [ccsdsOpmVers="
        + ccsdsOpmVers
        + ", header="
        + header
        + ", metadata="
        + metadata
        + ", stateVector="
        + stateVector
        + ", keplerian="
        + keplerian
        + ", spacecraft="
        + spacecraft
        + ", covariance="
        + covariance
        + ", maneuvers="
        + maneuvers
        + ", adamFields="
        + adamFields
        + "]";
  }
}
