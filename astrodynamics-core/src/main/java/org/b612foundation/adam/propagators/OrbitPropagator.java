package org.b612foundation.adam.propagators;

import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.exceptions.AdamPropagationException;
import org.b612foundation.adam.opm.OrbitEphemerisMessage;

/**
 * Common interface for tools that can propagate an orbit. Instantiations include STK, OpenOrb,
 * possibly more.
 */
public interface OrbitPropagator {
  /**
   * Propagates orbit specified by the given parameters and configuration (force model, etc).
   *
   * @return native representation of the answer as an {@link OrbitEphemerisMessage}.
   * @throws AdamPropagationException if something went wrong.
   */
  OrbitEphemerisMessage propagate(
      PropagationParameters propagationParams,
      PropagatorConfiguration config,
      String propagationIdForLogging)
      throws AdamPropagationException;
}

