package org.b612foundation.adam.datamodel;

import org.b612foundation.adam.opm.OrbitParameterMessage;

public interface EntityFactory {
  public RunDescription makeRunDescription(String batchUuid, int partIndex, OrbitParameterMessage opm);
}
