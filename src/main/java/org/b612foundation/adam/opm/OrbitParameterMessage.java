package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Orbit Parameter Message, or OPM, one of the three high-level message types
 * defined in CCSDS ODM standard. https://public.ccsds.org/Pubs/502x0b2c1.pdf
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
	/** 0 or more manuevers. */
	private List<Manuever> manuevers = new ArrayList<>();
	/**
	 * The standard also allows USER_DEFINED_X parameters. We ignore all except
	 * ADAM-specific ones (start with USER_DEFINED_ADAM_). The order of
	 * user-defined fields matters, because there is no way to group these fields
	 * other than inserting some sort of header or separator fields. So, we cannot
	 * use proto map here, because that would lose ordering.
	 */
	private List<AdamField> adamFields = new ArrayList<>();

  public OrbitParameterMessage deepCopy() {
    OrbitParameterMessage res = new OrbitParameterMessage();
    res.setCcsds_opm_vers(ccsdsOpmVers);
    if (header != null)
      res.setHeader(header.deepCopy());
    if (metadata != null)
      res.setMetadata(metadata.deepCopy());
    if (stateVector != null)
      res.setState_vector(stateVector.deepCopy());
    if (keplerian != null)
      res.setKeplerian(keplerian.deepCopy());
    for (Manuever man : manuevers)
      res.addManuever(man.deepCopy());
    for (AdamField af : adamFields)
      res.addAdam_field(af.getKey(), af.getValue());
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

	public List<Manuever> getManuevers() {
		return manuevers;
	}

	public OrbitParameterMessage setManuevers(List<Manuever> manuevers) {
		this.manuevers = manuevers;
    return this;
	}
	
	public OrbitParameterMessage addManuever(Manuever manuever) {
		this.manuevers.add(manuever);
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adamFields == null) ? 0 : adamFields.hashCode());
		result = prime * result + ((ccsdsOpmVers == null) ? 0 : ccsdsOpmVers.hashCode());
		result = prime * result + ((covariance == null) ? 0 : covariance.hashCode());
		result = prime * result + ((header == null) ? 0 : header.hashCode());
		result = prime * result + ((keplerian == null) ? 0 : keplerian.hashCode());
		result = prime * result + ((manuevers == null) ? 0 : manuevers.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result + ((spacecraft == null) ? 0 : spacecraft.hashCode());
		result = prime * result + ((stateVector == null) ? 0 : stateVector.hashCode());
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
		OrbitParameterMessage other = (OrbitParameterMessage) obj;
		if (adamFields == null) {
			if (other.adamFields != null)
				return false;
		} else if (!adamFields.equals(other.adamFields))
			return false;
		if (ccsdsOpmVers == null) {
			if (other.ccsdsOpmVers != null)
				return false;
		} else if (!ccsdsOpmVers.equals(other.ccsdsOpmVers))
			return false;
		if (covariance == null) {
			if (other.covariance != null)
				return false;
		} else if (!covariance.equals(other.covariance))
			return false;
		if (header == null) {
			if (other.header != null)
				return false;
		} else if (!header.equals(other.header))
			return false;
		if (keplerian == null) {
			if (other.keplerian != null)
				return false;
		} else if (!keplerian.equals(other.keplerian))
			return false;
		if (manuevers == null) {
			if (other.manuevers != null)
				return false;
		} else if (!manuevers.equals(other.manuevers))
			return false;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		if (spacecraft == null) {
			if (other.spacecraft != null)
				return false;
		} else if (!spacecraft.equals(other.spacecraft))
			return false;
		if (stateVector == null) {
			if (other.stateVector != null)
				return false;
		} else if (!stateVector.equals(other.stateVector))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrbitParameterMessage [ccsdsOpmVers=").append(ccsdsOpmVers).append(", header=").append(header)
				.append(", metadata=").append(metadata).append(", stateVector=").append(stateVector).append(", keplerian=")
				.append(keplerian).append(", spacecraft=").append(spacecraft).append(", covariance=").append(covariance)
				.append(", manuevers=").append(manuevers).append(", adamFields=").append(adamFields).append("]");
		return builder.toString();
	}
}
