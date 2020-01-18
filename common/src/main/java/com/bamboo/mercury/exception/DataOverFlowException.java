package com.bamboo.mercury.exception;

import com.bamboo.mercury.api.DataType;

public class DataOverFlowException extends RuntimeException {

  public DataOverFlowException(Object rawData, DataType tgtType) {
    super(String.format("%s cast to %s overflow", rawData, tgtType));
  }

}
