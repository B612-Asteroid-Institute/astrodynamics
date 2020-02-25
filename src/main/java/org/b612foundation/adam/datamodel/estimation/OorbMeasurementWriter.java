package org.b612foundation.adam.datamodel.estimation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

public class OorbMeasurementWriter {
  public static final double DEFAULT_RA_ERROR_ESTIMATE_ASEC = 1.000;
  public static final double DEFAULT_DEC_ERROR_ESTIMATE_ASEC = 1.000;
  public static final double DEFAULT_VMAG_ERROR_ESTIMATE = 1.000; //means ignore

  private double raErrorEstimate;

  private double decErrorEstimate;

  private double vMagErrorEstimate;

  public OorbMeasurementWriter() {
    this(DEFAULT_RA_ERROR_ESTIMATE_ASEC, DEFAULT_DEC_ERROR_ESTIMATE_ASEC, DEFAULT_VMAG_ERROR_ESTIMATE);
  }

  public OorbMeasurementWriter(double raErrorEstimateAsec, double decErrorEstimateAsec, double vMagEstimateError) {
    this.raErrorEstimate = raErrorEstimateAsec;
    this.decErrorEstimate = decErrorEstimateAsec;
    this.vMagErrorEstimate = vMagEstimateError;
  }

  public void writeLsstMeasurementsToDesFile(Path desFile, List<LsstMeasurement> measurements) throws FileNotFoundException {
    try (PrintWriter writer = new PrintWriter(desFile.toFile())) {
      measurements.forEach(m -> writer.println(convertWriteLsstToDesFormat(m)));
    }
  }

  String convertWriteLsstToDesFormat(LsstMeasurement m) {
    var stationIdRequiredSize = 5;
    var stationId = m.getStationId();
    if (stationId.length() > stationIdRequiredSize) {
      stationId = stationId.substring(0, stationIdRequiredSize);
    }

    while (stationId.length() < stationIdRequiredSize) {
      stationId += " ";
    }

    return String.format("%s %16.10f O %14.10f %14.10f %14.10f X %s%13.10f %14.10f %14.10f  0.1000000E+01 X",
        m.getObjectId(), m.getMjd(), m.getRaDeg(), m.getDecDeg(), m.getVmag(), stationId,
        raErrorEstimate, decErrorEstimate, vMagErrorEstimate);
  }
}
