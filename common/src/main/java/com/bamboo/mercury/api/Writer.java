package com.bamboo.mercury.api;

import com.bamboo.mercury.exception.InvalidRecordException;

public interface Writer<R extends Record> extends AutoCloseable {

  void write(R r) throws Exception;

  String fakeWrite(R r) throws Exception;

  void validateRecord(R r) throws InvalidRecordException;

  void init() throws Exception;

}
