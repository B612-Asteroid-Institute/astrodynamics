package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Orbit Parameter Message, or OPM, one of the three high-level message types defined in CCSDS ODM standard.
 * https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public class OrbitEphemerisMessage implements Serializable {
  /** OEM version is required by standard. Should be the same value always. */
  private String ccsdsOemVers = "2.0";
  /** Headers are common for all message types. */
  private OdmCommonHeader header;
  /** One OEM message can contain several ephemerides, each with its own metadata block and optional covariance. */
  private List<OemDataBlock> data = new ArrayList<>();

  public String getCcsds_oem_vers() {
    return ccsdsOemVers;
  }

  public OrbitEphemerisMessage setCcsds_oem_vers(String ccsdsOemVers) {
    this.ccsdsOemVers = ccsdsOemVers;
    return this;
  }

  public OdmCommonHeader getHeader() {
    return header;
  }

  public OrbitEphemerisMessage setHeader(OdmCommonHeader header) {
    this.header = header;
    return this;
  }

  public void addBlock(OemDataBlock block) {
    this.data.add(block);
  }

  public List<OemDataBlock> getBlocks() {
    return this.data;
  }

  @Override
  public int hashCode() {
    return Objects.hash(ccsdsOemVers, header, data);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OrbitEphemerisMessage other = (OrbitEphemerisMessage) obj;
    return Objects.equals(ccsdsOemVers, other.ccsdsOemVers) && Objects.equals(header, other.header)
        && Objects.equals(data, other.data);
  }
}
