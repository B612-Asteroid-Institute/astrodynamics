package org.b612foundation.adam.util;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.orekit.data.DataContext;
import org.orekit.time.AbsoluteDate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.Assert.*;

public class OrekitDataLoaderTest {
  @Rule public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  @Before
  public void before() {
    environmentVariables.set(OrekitDataLoader.OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME, null);
    System.clearProperty(OrekitDataLoader.OREKIT_DATA_RUNTIME_PROPERTY_NAME);
    OrekitDataLoader.IsLoaded.set(false);
    DataContext.getDefault().getDataProvidersManager().clearProviders();
  }

  @Test
  public void testGetDataPath() {
    String environmentValue = "/tmp/orekitdata.zip";
    String systemValue = "/tmp/orekitdatafolder";
    assertNull(OrekitDataLoader.getOrekitDataPath());
    environmentVariables.set(
        OrekitDataLoader.OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME, environmentValue);
    assertEquals(Paths.get(environmentValue), OrekitDataLoader.getOrekitDataPath());
    System.setProperty(OrekitDataLoader.OREKIT_DATA_RUNTIME_PROPERTY_NAME, systemValue);
    assertEquals(Paths.get(systemValue), OrekitDataLoader.getOrekitDataPath());
  }

  @Test(expected = RuntimeException.class)
  public void testExceptionWithBadPathInEnvironmentVariable() {
    environmentVariables.set(
        OrekitDataLoader.OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME, UUID.randomUUID().toString());
    OrekitDataLoader.initialize();
  }

  @Test(expected = RuntimeException.class)
  public void testExceptionWithBadPathInSystemValue() {
    System.setProperty(
        OrekitDataLoader.OREKIT_DATA_RUNTIME_PROPERTY_NAME, UUID.randomUUID().toString());
    OrekitDataLoader.initialize();
  }

  @Test
  public void testDefaultInitialization() {
    OrekitDataLoader.initialize();
    System.out.println(new AbsoluteDate());
  }

  @Test(expected = IOException.class)
  public void testInitFromFileExceptionWithBadPath() throws IOException {
    OrekitDataLoader.initializeFromFile(Paths.get(UUID.randomUUID().toString()));
  }

  @Test
  public void testInitializeProperlyAfterFailedAttempt() {
    try {
      OrekitDataLoader.initializeFromFile(Paths.get(UUID.randomUUID().toString()));
    } catch (IOException e) {
      // Expecting exception here to test initialization properly works afterward
    }

    OrekitDataLoader.initialize();
    System.out.println(new AbsoluteDate());
    assertTrue(OrekitDataLoader.isLoaded());
  }

  // This test can only be executed on a machine with the orekit-data folder on it. CI ignores it.
  @Test
  @Ignore
  public void testLoadingFromDirectory() throws IOException {
    Path path = Paths.get(System.getProperty("user.home"), "orekit-data");
    OrekitDataLoader.initializeFromFile(path);
    System.out.println(new AbsoluteDate());
    assertTrue(OrekitDataLoader.isLoaded());
  }

  @Test
  @Ignore
  public void testLoadingFromZip() throws IOException {
    Path path = Paths.get(System.getProperty("user.home"), "orekit-data.zip");
    OrekitDataLoader.initializeFromFile(path);
    System.out.println(new AbsoluteDate());
    assertTrue(OrekitDataLoader.isLoaded());
  }

  @Test
  @Ignore
  public void testInitializeFromEnvironmentVariable() {
    String orekitDataFolderPath =
        Paths.get(System.getProperty("user.home"), "orekit-data").toAbsolutePath().toString();
    environmentVariables.set(
        OrekitDataLoader.OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME, orekitDataFolderPath);
    OrekitDataLoader.initialize();
    System.out.println(new AbsoluteDate());
    assertTrue(OrekitDataLoader.isLoaded());
  }

  @Test
  @Ignore
  public void testInitializeFromSystemSetting() {
    String orekitDataFolderPath =
        Paths.get(System.getProperty("user.home"), "orekit-data").toAbsolutePath().toString();
    System.setProperty(OrekitDataLoader.OREKIT_DATA_RUNTIME_PROPERTY_NAME, orekitDataFolderPath);
    OrekitDataLoader.initialize();
    System.out.println(new AbsoluteDate());
    assertTrue(OrekitDataLoader.isLoaded());
  }
}
