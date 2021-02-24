package org.b612foundation.adam.exceptions;

/** Exceptions generated in the actual propagation process of an orbit calculations. */
public class AdamPropagationException extends Exception {

  private static final long serialVersionUID = 5149645226465438222L;

  public AdamPropagationException(String message, Throwable cause) {
    super(message, cause);
  }
}
