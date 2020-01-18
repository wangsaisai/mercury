package com.bamboo.mercury.rdb.exception;

import com.bamboo.mercury.exception.MercuryRuntimeException;

public class RdbMercuryRuntimeException extends MercuryRuntimeException {

  public RdbMercuryRuntimeException() {
  }

  public RdbMercuryRuntimeException(String message) {
    super(message);
  }

  public RdbMercuryRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public RdbMercuryRuntimeException(Throwable cause) {
    super(cause);
  }

}
