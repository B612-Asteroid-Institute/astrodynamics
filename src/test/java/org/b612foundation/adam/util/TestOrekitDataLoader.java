package org.b612foundation.adam.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TestOrekitDataLoader {
  @Rule public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  @Before
  public void setUp() {
    // Clear the properties and environment variables in case it's set on the system
    environmentVariables.set(OrekitDataLoader.OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME, null);
    System.clearProperty(OrekitDataLoader.OREKIT_DATA_RUNTIME_PROPERTY_NAME);
  }

  @After
  public void tearDown() {
    environmentVariables.set(OrekitDataLoader.OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME, null);
    System.clearProperty(OrekitDataLoader.OREKIT_DATA_RUNTIME_PROPERTY_NAME);
  }

  @Test
  public void testGetDataPath() {
    Path expectedDefaultValue = OrekitDataLoader.getDefaultOrekitDataPath();
    String environmentValue = "/tmp/orekitdata.zip";
    String systemValue = "/tmp/orekitdatafolder";
    assertEquals(expectedDefaultValue, OrekitDataLoader.getOrekitDataPath());
    environmentVariables.set(
        OrekitDataLoader.OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME, environmentValue);
    assertEquals(Paths.get(environmentValue), OrekitDataLoader.getOrekitDataPath());
    System.setProperty(OrekitDataLoader.OREKIT_DATA_RUNTIME_PROPERTY_NAME, systemValue);
    assertEquals(Paths.get(systemValue), OrekitDataLoader.getOrekitDataPath());
  }

  @Test(expected = OrekitException.class)
  public void testExpectUnitilizedToThrowExecption() {
    System.out.println(new AbsoluteDate());
  }

  @Test
  public void testInitialization() {
    OrekitDataLoader.initialize();
    System.out.println(new AbsoluteDate());
  }

  @Test(expected = IOException.class)
  public void testExceptionWithBadPath() throws IOException {
    OrekitDataLoader.initialize(Paths.get(UUID.randomUUID().toString()));
  }

  @Test
  public void testInitializeProperlyAfterFailedAttempt() {
    try {
      OrekitDataLoader.initialize(Paths.get(UUID.randomUUID().toString()));
    } catch (IOException e) {
      //Expecting exception here to test initialization properly works afterward
    }

    OrekitDataLoader.initialize();
    System.out.println(new AbsoluteDate());
  }

  @Test
  public void testLoadingFromDirectory() throws IOException {
    Path path = Paths.get(System.getProperty("user.home"), "orekit-data");
    OrekitDataLoader.initialize(path);
    System.out.println(new AbsoluteDate());
  }
}
