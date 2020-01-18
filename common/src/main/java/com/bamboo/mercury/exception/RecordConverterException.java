package com.bamboo.mercury.exception;

public class RecordConverterException extends RuntimeException {

  public RecordConverterException() {
  }

  public RecordConverterException(String message) {
    super(message);
  }

  public RecordConverterException(String message, Throwable cause) {
    super(message, cause);
  }

  public RecordConverterException(Throwable cause) {
    super(cause);
  }

}
