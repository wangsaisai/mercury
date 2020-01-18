package com.bamboo.mercury.api;

public interface Reader<R extends Record> extends AutoCloseable {

  R read() throws Exception;

  void init() throws Exception;

//  List<R> batchRead(int batchSize) throws IOException;

}
