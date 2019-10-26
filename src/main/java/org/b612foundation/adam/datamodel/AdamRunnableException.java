package org.b612foundation.adam.runnable;

/**
 * For use when errors arise while generating/performing calculations for AdamObjects.
 * 
 * Note: This should live somewhere else, possibly in astrodynamics.
 */
public class AdamRunnableException extends Exception {
  private static final long serialVersionUID = 5087370502764947213L;

  public AdamRunnableException(String message, Throwable cause) {
      super(message, cause);
  }
}
