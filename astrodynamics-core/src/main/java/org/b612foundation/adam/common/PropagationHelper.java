package org.b612foundation.adam.common;

import org.b612foundation.adam.opm.OemDataBlock;
import org.b612foundation.adam.opm.OemDataLine;
import org.b612foundation.adam.opm.OrbitEphemerisMessage;
import org.b612foundation.adam.opm.StateVector;

import java.util.List;

public class PropagationHelper {
  private PropagationHelper() {

  }

  public static OemDataLine extractFinalState(OrbitEphemerisMessage oem) {
    List<OemDataBlock> blocks = oem.getBlocks();
    List<OemDataLine> lines = blocks.get(blocks.size() - 1).getLines();
    return lines.get(lines.size() - 1);
  }

  public static StateVector toStateVector(OemDataLine line) {
    StateVector sv = new StateVector();
    sv.setEpoch(line.getDate());
    double[] point = line.getPoint();
    sv.setEpoch(line.getDate()).setX(point[0]).setY(point[1]).setZ(point[2]);
    if (point.length == 6) {
      sv.setX_dot(point[3]).setY_dot(point[4]).setZ_dot(point[5]);
    }
    return sv;
  }

  public static StateVector toStateVector(double[] point, String date) {
    StateVector sv = new StateVector();
    sv.setEpoch(date);
    sv.setX(point[0]).setY(point[1]).setZ(point[2]);
    if (point.length == 6) {
      sv.setX_dot(point[3]).setY_dot(point[4]).setZ_dot(point[5]);
    }
    return sv;
  }
}
