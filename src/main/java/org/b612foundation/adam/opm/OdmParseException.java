package org.b612foundation.adam.opm;

/**
 * Exception thrown during parsing of ODM messages.
 */
public class OdmParseException extends Exception {
  public OdmParseException(String message) {
    super(message);
  }
}
