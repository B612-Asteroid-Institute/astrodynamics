package org.b612foundation.adam.util;

import com.google.common.annotations.VisibleForTesting;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.data.ZipJarCrawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides a convenient bootstrapping function for loading Orekit data from a local folder
 * or ZIP file. The default path is an "orekit-data" folder in the user's home folder. One
 * can download the ZIP file here: https://gitlab.orekit.org/orekit/orekit-data/-/archive/master/orekit-data-master.zip
 * uncompress it, and place in the path. It is possible to set the environment variable
 * ADAM_OREKIT_DATA_PATH or a command line parameter stk.license.path to override this value
 */
public class OrekitDataLoader {
  public static final String DEFAULT_OREKIT_DATA = "orekit-data";
  public static final String OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME = "ADAM_OREKIT_DATA_PATH";
  public static final String OREKIT_DATA_RUNTIME_PROPERTY_NAME = "stk.license.path";
  public static final AtomicBoolean IsLoaded = new AtomicBoolean(false);

  public static void initialize() {
    try {
      OrekitDataLoader.initialize(getOrekitDataPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static synchronized void initialize(Path orekitDataPath) throws IOException {
    if (IsLoaded.get()) {
      return;
    }

    if (!Files.exists(orekitDataPath)) {
      String errorString = "Orekit data path is not found:" + orekitDataPath.toAbsolutePath();
      throw new IOException(errorString);
    }
    final DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();

    if (orekitDataPath.toFile().isFile()) {
      manager.addProvider(new ZipJarCrawler(orekitDataPath.toFile()));
    } else {
      manager.addProvider(new DirectoryCrawler(orekitDataPath.toFile()));
    }
  }

  @VisibleForTesting
  static Path getDefaultOrekitDataPath() {
    return Paths.get(System.getProperty("user.home"), DEFAULT_OREKIT_DATA).toAbsolutePath();
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

    return getDefaultOrekitDataPath();
  }

  @VisibleForTesting
  static String getOrekitDataFromEnvironmentVariable() {
    return System.getenv(OREKIT_DATA_ENVIRONMENT_VARIABLE_NAME);
  }

  @VisibleForTesting
  static String getOrekitDataFromProperty() {
    return System.getProperty(OREKIT_DATA_RUNTIME_PROPERTY_NAME);
  }
}
