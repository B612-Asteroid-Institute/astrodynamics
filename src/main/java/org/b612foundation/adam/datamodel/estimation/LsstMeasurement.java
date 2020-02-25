package org.b612foundation.adam.datamodel.estimation;

import lombok.Data;

@Data
public class LsstMeasurement {
  //objId visitId mjd ra dec vra vdec phase solarElon helioD geoD Vmag true_anomaly H G ra_field dec_field mjd_field filter visitTime visitExposureTime seeingFwhmEff fiveSigmaDepth class p_v mag detected D_km
  private String objectId;

  private String stationId;

  private int visitId;

  private double mjd;

  private double raDeg;

  private double decDeg;

  private double vra;

  private double vdec;

  private double phase;

  private double solarElon;

  private double helioD;

  private double geoD;

  private double Vmag;

  private double trueAnomaly;

  private double H;

  private double G;

  private double raFieldDeg;

  private double decFieldDeg;

  private double mjdField;

  private String filter;

  private double visitTime;

  private double visitExposureTime;

  private double seeingFwhmEff;

  private double fiveSigmaDepth;

  private String orbitClass;

  private double pV;

  private double mag;

  private double detected;

  private double Dkm;

}
