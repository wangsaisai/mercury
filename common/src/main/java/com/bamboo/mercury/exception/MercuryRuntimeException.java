package com.bamboo.mercury.exception;

/**
 * Base Exception class for Mercury.
 */
public class MercuryRuntimeException extends RuntimeException {

  public MercuryRuntimeException() {
  }

  public MercuryRuntimeException(String message) {
    super(message);
  }

  public MercuryRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public MercuryRuntimeException(Throwable cause) {
    super(cause);
  }

  public MercuryRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
