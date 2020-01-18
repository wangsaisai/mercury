package com.bamboo.mercury.api;

public interface DeltaRecord extends Record {

  DeltaType getDeltaType();

  void setDeltaType(DeltaType deltaType);

}
