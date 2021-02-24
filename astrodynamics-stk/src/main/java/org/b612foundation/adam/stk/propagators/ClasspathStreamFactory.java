package org.b612foundation.adam.stk.propagators;


import agi.foundation.compatibility.MemoryStream;
import agi.foundation.infrastructure.StreamFactory;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class ClasspathStreamFactory extends StreamFactory {

  private String resourceName;

  /** Creates a stream factory to load from a resource in the classpath relative to this class. */
  public ClasspathStreamFactory(String resourceName) {
    this.resourceName = resourceName;
  }

  @Override
  public InputStream openStream() {
    // NOTE! Using getClassLoader().getResourceAsStream may be less safe than
    // ClassLoader.getSystemResourceAsStream!
    // If running into strange issues loading files, try the other one! getClassLoader()... was
    // added because of
    // difficulty loading files in AppEngine, which may be related to the way it is containerized.
    InputStream input = ClassLoader.getSystemResourceAsStream(resourceName);
    // InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName);
    if (input != null) {
      try {
        return new MemoryStream(IOUtils.toByteArray(input));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    throw new RuntimeException("Cannot load resource " + resourceName);
  }
}

