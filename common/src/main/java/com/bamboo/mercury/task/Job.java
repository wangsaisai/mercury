package com.bamboo.mercury.task;

import com.bamboo.mercury.api.Converter;
import com.bamboo.mercury.api.Writer;
import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.exception.MercuryRuntimeException;
import com.bamboo.mercury.util.Factorys;
import com.bamboo.mercury.api.Reader;
import com.bamboo.mercury.api.Record;
import java.io.FileNotFoundException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.apache.commons.configuration.Configuration;

public class Job {

  private Reader<Record> reader;

  private Writer<Record> writer;

  private Channel<Record, Record> channel;

  private boolean fakeWrite;
  private long fakeWriteSize;
  private String fakeWriteFileName;

  public Job() {
    try {
      init();
    } catch (Exception e) {
      throw new MercuryRuntimeException(e);
    }
  }

  public static void main(String[] args) {
    Job job = new Job();
    job.start();
  }

  private void init() {
    Configuration conf = ApplicationProperties.get();

    reader = Factorys.getReader();
    writer = Factorys.getWriter();

    Converter<Record, Record> converter = Factorys.getRecordConverter();

    int queueSize = conf.getInt("queue.size", 10000);
    BlockingQueue<Record> queue = new LinkedBlockingDeque<>(queueSize);

    channel = new Channel<>(queue, converter);

    // fake write
    fakeWrite = conf.getBoolean("fake.write.enable", false);
    fakeWriteSize = conf.getLong("fake.write.size", 100);
    fakeWriteFileName = conf.getString("fake.write.file.name", "fakewrite.txt");
  }

  public void start() {
    ReaderTask<Record> readerTask = new ReaderTask<>(reader, channel);
    WriterTask<Record> writerTask;

    if (fakeWrite) {
      try {
        writerTask = new WriterTask<>(writer, channel, fakeWrite, fakeWriteSize, fakeWriteFileName);
      } catch (FileNotFoundException e) {
        throw new MercuryRuntimeException(e);
      }
    } else {
      writerTask = new WriterTask<>(writer, channel);
    }

    Thread readThread = new Thread(readerTask);
    readThread.setName("read-thread");
    Thread writeThread = new Thread(writerTask);
    writeThread.setName("write-thread");

    readThread.start();
    writeThread.start();

    while (writeThread.isAlive()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ignored) {
      }
    }

    // shutdown reader thread, if writer finish
    readerTask.terminate();
  }

}
