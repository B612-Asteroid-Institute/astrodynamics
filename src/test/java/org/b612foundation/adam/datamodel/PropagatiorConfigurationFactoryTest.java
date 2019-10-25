package org.b612foundation.adam.datamodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropagatiorConfigurationFactoryTest {

  @Test
  public void testGetAllMajorBodiesConfig() {
    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();
    assertEquals(0, config.getAsteroids().size());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getSun());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getMercury());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getVenus());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getEarth());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getMoon());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getMars());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getJupiter());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getSaturn());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getUranus());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getNeptune());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getPluto());
  }

  @Test
  public void testGetSunOnlyConfig() {
    PropagatorConfiguration config = PropagationConfigurationFactory.getSunOnlyConfig();
    assertEquals(0, config.getAsteroids().size());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.POINT_MASS, config.getSun());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.OMIT, config.getMercury());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.OMIT, config.getVenus());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.OMIT, config.getEarth());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.OMIT, config.getMoon());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.OMIT, config.getMars());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.OMIT, config.getJupiter());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.OMIT, config.getSaturn());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.OMIT, config.getUranus());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.OMIT, config.getNeptune());
    assertEquals(PropagatorConfiguration.PlanetGravityMode.OMIT, config.getPluto());
  }

}
