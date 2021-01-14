package org.b612foundation.adam.util;

import com.google.common.annotations.VisibleForTesting;
import org.orekit.data.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides a convenient bootstrapping function for loading Orekit data from a local folder or ZIP
 * file. The default path is an "orekit-data.zip" resource embedded in the jar file. One can
 * download the ZIP file here:
 * https://gitlab.orekit.org/orekit/orekit-data/-/archive/master/orekit-data-master.zip uncompress
 * it, and place in the path if they wish to override it. It is possible to set the environment
 * variable ADAM_OREKIT_DATA_PATH or a command line parameter stk.license.path to override the
 * internal defaults with a preferred version on the file system.
 */
public class OrekitDataLoader {
  public static final String OREKIT_RESOURCE_PATH = "orekit-data.zip";
  public static final String OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME = "ADAM_OREKIT_DATA_PATH";
  public static final String OREKIT_DATA_RUNTIME_PROPERTY_NAME = "stk.license.path";

  public static boolean isLoaded() {
    return IsLoaded.get();
  }

  public static synchronized void initialize() {
    try {
      Path dataPath = getOrekitDataPath();
      if (dataPath == null) {
        initializeOrekitData(new ClasspathCrawler(OREKIT_RESOURCE_PATH));
      } else {
        OrekitDataLoader.initializeFromFile(dataPath);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @VisibleForTesting static final AtomicBoolean IsLoaded = new AtomicBoolean(false);

  @VisibleForTesting
  static synchronized void initializeFromFile(Path orekitDataPath) throws IOException {
    if (IsLoaded.get()) {
      return;
    }

    if (!Files.exists(orekitDataPath)) {
      String errorString = "Orekit data path is not found:" + orekitDataPath.toAbsolutePath();
      throw new IOException(errorString);
    }

    if (orekitDataPath.toFile().isFile()) {
      initializeOrekitData(new ZipJarCrawler(orekitDataPath.toFile()));
    } else {
      initializeOrekitData(new DirectoryCrawler(orekitDataPath.toFile()));
    }
  }

  @VisibleForTesting
  static Path getOrekitDataPath() {
    String orekitDataPath = getOrekitDataFromProperty();
    if (orekitDataPath != null) {
      return Paths.get(orekitDataPath);
    }

    orekitDataPath = getOrekitDataFromEnvironmentVariable();
    if (orekitDataPath != null) {
      return Paths.get(orekitDataPath);
    }

    return null;
  }

  @VisibleForTesting
  static String getOrekitDataFromEnvironmentVariable() {
    return System.getenv(OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME);
  }

  @VisibleForTesting
  static String getOrekitDataFromProperty() {
    return System.getProperty(OREKIT_DATA_RUNTIME_PROPERTY_NAME);
  }

  private static synchronized void initializeOrekitData(DataProvider provider) {
    final DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
    manager.addProvider(provider);
    IsLoaded.set(true);
  }
}
