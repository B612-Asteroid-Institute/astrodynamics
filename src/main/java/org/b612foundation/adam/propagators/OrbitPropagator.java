package org.b612foundation.adam.propagators;

import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.runnable.AdamRunnableException;

/**
 * Common interface for tools that can propagate an orbit. Instantiations include STK, OpenOrb, possibly more.
 */
public interface OrbitPropagator {
  /**
   * Propagates orbit specified by the given parameters and configuration (force model, etc).
   * 
   * @return native representation of the answer.
   * @throws AdamRunnableException
   *           if something went wrong.
   */
  String propagate(PropagationParameters propagationParams, PropagatorConfiguration config,
      String propagationIdForLogging) throws AdamRunnableException;
}
