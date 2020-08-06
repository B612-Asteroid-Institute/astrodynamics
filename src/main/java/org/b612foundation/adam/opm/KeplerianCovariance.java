package org.b612foundation.adam.opm;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Keplerian State Covariance Matrix (6x6 Lower Triangular Form). This is a custom extension to the
 * OPM standard in  https://public.ccsds.org/Pubs/502x0b2c1.pdf . It should be serialized as
 * USER_DEFINED_* terms per the standard.

 * Covariance matrix variables are in the following order and with the single letter key shown:
 *  - semi-major axis (A) in km.
 *  - eccentricity (E) which is unitless
 *  - inclination (I) in degrees
 *  - right ascension of the ascending node (O) in degrees
 *  - argument of pericenter (W) in degrees
 *  - true anomaly (T) or mean anomaly (M) in degrees
 *
 * If both the true and mean anomaly fields are set the result could be undefined behaviors
 * by code using this. The Covariance Matrix with mean anomaly used looks like:
 * <pre>
 * | CAA                     |
 * | CEA CEE                 |
 * | CIA CIE CII             |
 * | COA COE COI COO         |
 * | CWA CWE CWI CWO CWW     |
 * | CMA CME CMI CMO CMW CMM |
 *</pre>
 * The Covariance Matrix form with True anomaly used looks like:
 * <pre>
 * | CAA                     |
 * | CEA CEE                 |
 * | CIA CIE CII             |
 * | COA COE COI COO         |
 * | CWA CWE CWI CWO CWW     |
 * | CTA CTE CTI CTO CTW CTT |
 *</pre>
 */

@EqualsAndHashCode
@Getter
@ToString
public class KeplerianCovariance {
  /** Reference frame. If omited, same as metadata */
  private OdmCommonMetadata.ReferenceFrame covRefFrame = null;
  private double CAA;
  private double CEA, CEE;
  private double CIA, CIE, CII;
  private double COA, COE, COI, COO;
  private double CWA, CWE, CWI, CWO, CWW;
  private double CMA, CME, CMI, CMO, CMW, CMM;
  private double CTA, CTE, CTI, CTO, CTW, CTT;

  public KeplerianCovariance() {

  }

  public KeplerianCovariance setCovRefFrame(OdmCommonMetadata.ReferenceFrame covRefFrame) {
    this.covRefFrame = covRefFrame;
    return this;
  }

  public KeplerianCovariance setCAA(double value) {
    this.CAA = value;
    return this;
  }

  public KeplerianCovariance setCEA(double value) {
    this.CEA = value;
    return this;
  }

  public KeplerianCovariance setCEE(double value) {
    this.CEE = value;
    return this;
  }

  public KeplerianCovariance setCIA(double value) {
    this.CIA = value;
    return this;
  }

  public KeplerianCovariance setCIE(double value) {
    this.CIE = value;
    return this;
  }

  public KeplerianCovariance setCII(double value) {
    this.CII = value;
    return this;
  }

  public KeplerianCovariance setCOA(double value) {
    this.COA = value;
    return this;
  }

  public KeplerianCovariance setCOE(double value) {
    this.COE = value;
    return this;
  }

  public KeplerianCovariance setCOI(double value) {
    this.COI = value;
    return this;
  }

  public KeplerianCovariance setCOO(double value) {
    this.COO = value;
    return this;
  }

  public KeplerianCovariance setCWA(double value) {
    this.CWA = value;
    return this;
  }

  public KeplerianCovariance setCWE(double value) {
    this.CWE = value;
    return this;
  }

  public KeplerianCovariance setCWI(double value) {
    this.CWI = value;
    return this;
  }

  public KeplerianCovariance setCWO(double value) {
    this.CWO = value;
    return this;
  }

  public KeplerianCovariance setCWW(double value) {
    this.CWW = value;
    return this;
  }

  public KeplerianCovariance setCMA(double value) {
    this.CMA = value;
    return this;
  }

  public KeplerianCovariance setCME(double value) {
    this.CME = value;
    return this;
  }

  public KeplerianCovariance setCMI(double value) {
    this.CMI = value;
    return this;
  }

  public KeplerianCovariance setCMO(double value) {
    this.CMO = value;
    return this;
  }

  public KeplerianCovariance setCMW(double value) {
    this.CMW = value;
    return this;
  }

  public KeplerianCovariance setCMM(double value) {
    this.CMM = value;
    return this;
  }

  public KeplerianCovariance setCTA(double value) {
    this.CTA = value;
    return this;
  }

  public KeplerianCovariance setCTE(double value) {
    this.CTE = value;
    return this;
  }

  public KeplerianCovariance setCTI(double value) {
    this.CTI = value;
    return this;
  }

  public KeplerianCovariance setCTO(double value) {
    this.CTO = value;
    return this;
  }

  public KeplerianCovariance setCTW(double value) {
    this.CTW = value;
    return this;
  }

  public KeplerianCovariance setCTT(double value) {
    this.CTT = value;
    return this;
  }

}
