package org.b612foundation.adam.astro;

public final class AstroConstants {
  public static final double GM = 132712440041.93938;   //From JPL Horizons
  public static final double KM_TO_M = 1000.0;
  public static final double M_TO_KM = 1.0 / KM_TO_M;

  public static final double AU_TO_METERS = 1.49597870e+11; // Astronomical Units to meters
  public static final double AU_TO_KM = 149597870.0;     // conversion from AU to km
  public static final double AU_PER_DAY_TO_METER_PER_SEC = 1731456.84; // AU/Day conversion to m/sec
  public static final double AU_PER_DAY_TO_KM_PER_SEC = AU_PER_DAY_TO_METER_PER_SEC * M_TO_KM; // AU/Day conversion to km/sec
  public static final double DAY_TO_SEC = 86400.0;
  public static final double DAY_TO_HR = 24.0;
  public static final double HR_TO_MIN = 60.0;
  public static final double MIN_TO_SEC = 60.0;
  public static final double SEC_TO_MILLIS = 1000.0;
  public static final double SEC_TO_NANO = 1e9;
  public static final double DAY_TO_MILLIS = 86400.0 * SEC_TO_MILLIS;
}
