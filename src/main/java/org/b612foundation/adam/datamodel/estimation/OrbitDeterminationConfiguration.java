package org.b612foundation.adam.datamodel.estimation;

import java.util.Map;
import lombok.*;
import org.b612foundation.adam.datamodel.AdamObject;

/**
 * This is a configuration for an Orbit determination system that will be used as settings when an
 * OD is performed which transcend a specific run's values like the space object ID and are about
 * configuring force models, etc.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class OrbitDeterminationConfiguration extends AdamObject {
  /**
   * Specific executor to be used, e.g. OpenOrb, Orekit, etc. The behavior is up to the server
   * implementation.
   */
  private String executor;

  /** Settings for the numeric propagator - the ID. */
  private String propagatorConfigUuid;

  @Singular private Map<String, String> convergenceSettings;

  @Singular private Map<String, String> executionSettings;

  @Singular private Map<String, String> measurementSettings;
}
