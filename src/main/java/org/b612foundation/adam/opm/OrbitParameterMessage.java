package org.b612foundation.adam.opm;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Orbit Parameter Message, or OPM, one of the three high-level message types defined in CCSDS ODM
 * standard. https://public.ccsds.org/Pubs/502x0b2c1e2.pdf
 *
 * <p>This class has fields for state information in Cartesian ({@link
 * org.b612foundation.adam.opm.StateVector} ) and Keplerian ({@link
 * org.b612foundation.adam.opm.KeplerianElements}. By specification Cartesian must be filled in but
 * ADAM workflows where users only input Keplerian elements may only have that element filled in.
 *
 * <p>Covariance information also can exist in either or both Cartesian ({@link
 * org.b612foundation.adam.opm.CartesianCovariance}) or Keplerian ({@link
 * org.b612foundation.adam.opm.KeplerianCovariance}) forms. The Keplerian form is B612's custom
 * extensions to OPM, which leverages the USER_DEFINED_ fields. The format does not limit to one
 * form of the other, just like with the state information. However users reading both state and
 * covariance will often expect the supplied covariance matrix form to match the given state form.
 * So if Keplerian state is provided the type of matrix that should be provided is Keplerian form.
 * If only a Cartesian state is provided then a Cartesian covariance matrix would be expected.
 * Behavior when there is not a corresponding covariance type will be left up to data users but
 * would often generate argument exceptions.
 */
@EqualsAndHashCode
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
  /** Covariance for position/velocity state vector, optional. */
  private CartesianCovariance cartesianCovariance;
  /** Covariance for Keplerian state vector, optional. */
  private KeplerianCovariance keplerianCovariance;
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
    if (cartesianCovariance != null) res.setCartesianCovariance(cartesianCovariance.deepCopy());
    if (keplerianCovariance != null) res.setKeplerianCovariance(keplerianCovariance.deepCopy());
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

  public CartesianCovariance getCartesianCovariance() {
    return cartesianCovariance;
  }

  public OrbitParameterMessage setCartesianCovariance(CartesianCovariance cartesianCovariance) {
    this.cartesianCovariance = cartesianCovariance;
    return this;
  }

  public KeplerianCovariance getKeplerianCovariance() {
    return keplerianCovariance;
  }

  public OrbitParameterMessage setKeplerianCovariance(KeplerianCovariance keplerianCovariance) {
    this.keplerianCovariance = keplerianCovariance;
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
        + ", cartesianCovariance="
        + cartesianCovariance
        + ", keplerianCovariance="
        + keplerianCovariance
        + ", maneuvers="
        + maneuvers
        + ", adamFields="
        + adamFields
        + "]";
  }
}
