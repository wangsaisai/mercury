package com.bamboo.mercury.exception;

import com.bamboo.mercury.api.DataType;

public class CellConvertException extends RuntimeException {

  public CellConvertException() {
  }

  public CellConvertException(String message) {
    super(message);
  }

  public CellConvertException(String srcType, String tgtType) {
    this(String.format("%s cannot cast to %s", srcType, tgtType));
  }

  public CellConvertException(String srcType, Object rawData, String tgtType) {
    this(String.format("%s:%s cannot cast to %s", srcType, rawData, tgtType));
  }

  public CellConvertException(DataType srcType, DataType tgtType) {
    this(String.format("%s cannot cast to %s", srcType, tgtType));
  }

  public CellConvertException(DataType srcType, Object rawData, DataType tgtType) {
    this(String.format("%s:%s cannot cast to %s", srcType, rawData, tgtType));
  }

  public CellConvertException(String message, Throwable cause) {
    super(message, cause);
  }

  public CellConvertException(Throwable cause) {
    super(cause);
  }

}
