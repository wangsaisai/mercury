package com.bamboo.mercury.task;

import com.bamboo.mercury.api.Reader;
import com.bamboo.mercury.api.Record;

public class ReaderTask<R extends Record> implements Runnable {

  private volatile boolean shouldRun = true;

  private Channel<R, ?> channel;

  private Reader<R> reader;

  public ReaderTask(Reader<R> reader, Channel<R, ?> channel) {
    this.reader = reader;
    this.channel = channel;
  }

  @Override
  public void run() {
    try {
      R record = reader.read();
      while (shouldRun && record != null) {
        channel.put(record);
        record = reader.read();
      }
    } catch (Exception ignored) {
      ignored.printStackTrace();
    } finally {
      try {
        channel.terminate();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      try {
        reader.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void terminate() {
    shouldRun = false;
  }
}
