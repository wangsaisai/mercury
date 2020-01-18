package com.bamboo.mercury.task;

import com.bamboo.mercury.api.Writer;
import com.bamboo.mercury.api.Record;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class WriterTask<R extends Record> implements Runnable {

  private Channel<?, R> channel;

  private Writer<R> writer;

  private boolean fakeWrite = false;

  private long fakeWriteSize;

  private BufferedWriter fakeWriter;

  public WriterTask(Writer<R> writer, Channel<?, R> channel) {
    this.writer = writer;
    this.channel = channel;
  }

  public WriterTask(Writer<R> writer, Channel<?, R> channel, boolean fakeWrite, long fakeWriteSize,
      String fakeWriteFileName)
      throws FileNotFoundException {
    this(writer, channel);
    if (fakeWrite) {
      this.fakeWrite = true;
      this.fakeWriteSize = fakeWriteSize <= 0 ? Long.MAX_VALUE : fakeWriteSize;
      this.fakeWriter = new BufferedWriter(
          new OutputStreamWriter(new FileOutputStream(fakeWriteFileName)));
    }
  }


  @Override
  public void run() {
    try {
      R record = channel.take();
      long fakeWriteCount = 0;
      while (record != null) {
        if (fakeWrite) {
          String content = writer.fakeWrite(record);
          fakeWriter.write(content);
          fakeWriteCount++;
          if (fakeWriteCount >= fakeWriteSize) {
            return;
          }
        } else {
          writer.write(record);
        }

        record = channel.take();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (fakeWriter != null) {
          fakeWriter.close();
        }
        if (writer != null) {
          writer.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
