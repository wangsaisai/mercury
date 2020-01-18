package com.bamboo.mercury.task;

import com.bamboo.mercury.api.Converter;
import com.bamboo.mercury.api.Record;
import com.bamboo.mercury.element.TerminateRecord;
import java.util.concurrent.BlockingQueue;

public class Channel<SR extends Record, TR extends Record> {

  private BlockingQueue<Record> cache;

  private Converter<SR, TR> converter;

  public Channel(BlockingQueue<Record> cache, Converter<SR, TR> converter) {
    this.cache = cache;
    this.converter = converter;
  }

  /**
   * convert at read thread todo support convert both in read/write thread
   */
  public void put(SR record) throws InterruptedException {
    Record r = record;
    if (converter != null) {
      r = converter.convert(record);
    }

    cache.put(r);
  }

  public TR take() throws InterruptedException {
    Record r = cache.take();
    if (r instanceof TerminateRecord) {
      return null;
    }
    return (TR) r;
  }

  public void terminate() throws InterruptedException {
    cache.put(TerminateRecord.get());
  }

}
