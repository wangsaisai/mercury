package com.bamboo.mercury.exception;

public class InvalidRecordException extends Exception {

  public InvalidRecordException() {
  }

  public InvalidRecordException(String message) {
    super(message);
  }

  public InvalidRecordException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidRecordException(Throwable cause) {
    super(cause);
  }


}
