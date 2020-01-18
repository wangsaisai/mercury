package com.bamboo.mercury.ftp.exception;

import com.bamboo.mercury.exception.MercuryRuntimeException;

public class FtpMercuryRuntimeException extends MercuryRuntimeException {

  public FtpMercuryRuntimeException() {
  }

  public FtpMercuryRuntimeException(String message) {
    super(message);
  }

  public FtpMercuryRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public FtpMercuryRuntimeException(Throwable cause) {
    super(cause);
  }


}
