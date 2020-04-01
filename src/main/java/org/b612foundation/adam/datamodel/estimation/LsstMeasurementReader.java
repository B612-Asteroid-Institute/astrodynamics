package org.b612foundation.adam.datamodel.estimation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.var;

public final class LsstMeasurementReader {
  // objId visitId mjd ra dec vra vdec phase solarElon helioD geoD Vmag true_anomaly H G ra_field
  // dec_field mjd_field filter visitTime visitExposureTime seeingFwhmEff fiveSigmaDepth class p_v
  // mag detected D_km
  private static final int objIdIdx = 0;
  private static final int visitIdIdx = 1;
  private static final int mjdIdx = 2;
  private static final int raIdx = 3;
  private static final int decIdx = 4;
  private static final int vraIdx = 5;
  private static final int vdecIdx = 6;
  private static final int phaseIdx = 7;
  private static final int solarElonIdx = 8;
  private static final int helioDIdx = 9;
  private static final int geoDIdx = 10;
  private static final int VmagIdx = 11;
  private static final int trueAnomalyIdx = 12;
  private static final int HIdx = 13;
  private static final int GIdx = 14;
  private static final int raFieldIdx = 15;
  private static final int decFieldIdx = 16;
  private static final int mjdFieldIdx = 17;
  private static final int filterIdx = 18;
  private static final int visitTimeIdx = 19;
  private static final int visitExposureTimeIdx = 20;
  private static final int seeingFwhmEffIdx = 21;
  private static final int fiveSigmaDepthIdx = 22;
  private static final int classIdx = 23;
  private static final int pvIdx = 24;
  private static final int magIdx = 25;
  private static final int detectedIdx = 26;
  private static final int distanceKmIdx = 27;
  private static final int expectedItemCount = 28;

  public static List<LsstMeasurement> readSingleObjectCsvFile(Path csvFile, String stationId)
      throws IOException {
    var lines = Files.readAllLines(csvFile);
    var measurements = new ArrayList<LsstMeasurement>();

    for (var line : lines) {
      var elements = line.split("\\s+");

      if (elements.length != expectedItemCount) {
        continue;
      }

      try {
        Integer.parseInt(elements[visitIdIdx]);
      } catch (NumberFormatException e) {
        continue;
      }

      var m = new LsstMeasurement();
      m.setObjectId(elements[objIdIdx]);
      m.setStationId(stationId);
      m.setVisitId(Integer.parseInt(elements[visitIdIdx]));
      m.setMjd(Double.parseDouble(elements[mjdIdx]));
      m.setRaDeg(Double.parseDouble(elements[raIdx]));
      m.setDecDeg(Double.parseDouble(elements[decIdx]));
      m.setVra(Double.parseDouble(elements[vraIdx]));
      m.setVdec(Double.parseDouble(elements[vdecIdx]));
      m.setPhase(Double.parseDouble(elements[phaseIdx]));
      m.setSolarElon(Double.parseDouble(elements[solarElonIdx]));
      m.setHelioD(Double.parseDouble(elements[helioDIdx]));
      m.setGeoD(Double.parseDouble(elements[geoDIdx]));
      m.setVmag(Double.parseDouble(elements[VmagIdx]));
      m.setTrueAnomaly(Double.parseDouble(elements[trueAnomalyIdx]));
      m.setH(Double.parseDouble(elements[HIdx]));
      m.setG(Double.parseDouble(elements[GIdx]));
      m.setRaFieldDeg(Double.parseDouble(elements[raFieldIdx]));
      m.setDecFieldDeg(Double.parseDouble(elements[decFieldIdx]));
      m.setMjdField(Double.parseDouble(elements[mjdFieldIdx]));
      m.setFilter(elements[filterIdx]);
      m.setVisitTime(Double.parseDouble(elements[visitTimeIdx]));
      m.setVisitExposureTime(Double.parseDouble(elements[visitExposureTimeIdx]));
      m.setSeeingFwhmEff(Double.parseDouble(elements[seeingFwhmEffIdx]));
      m.setFiveSigmaDepth(Double.parseDouble(elements[fiveSigmaDepthIdx]));
      m.setOrbitClass(elements[classIdx]);
      m.setPV(Double.parseDouble(elements[pvIdx]));
      m.setMag(Double.parseDouble(elements[magIdx]));
      m.setDetected(Double.parseDouble(elements[detectedIdx]));
      m.setDkm(Double.parseDouble(elements[distanceKmIdx]));
      measurements.add(m);
    }

    return measurements;
  }
}
