package com.bamboo.mercury.util;

import com.bamboo.mercury.api.Converter;
import com.bamboo.mercury.api.Reader;
import com.bamboo.mercury.api.Record;
import com.bamboo.mercury.api.Writer;
import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.exception.MercuryRuntimeException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

public class Factorys {

  private static Configuration conf = ApplicationProperties.get();

  public static <R extends Record> Reader<R> getReader() {
    String readerClassName = conf.getString("reader.class");

    Reader<R> reader = ReflectUtil.newInstance(readerClassName);
    try {
      reader.init();
    } catch (Exception e) {
      throw new MercuryRuntimeException("reader init error", e);
    }
    return reader;
  }

  public static <R extends Record> Writer<R> getWriter() {
    String writerClassName = conf.getString("writer.class");

    Writer<R> writer = ReflectUtil.newInstance(writerClassName);
    try {
      writer.init();
    } catch (Exception e) {
      throw new MercuryRuntimeException("writer init error", e);
    }
    return writer;
  }

  public static <SR extends Record, TR extends Record> Converter<SR, TR> getRecordConverter() {
    String converterClassName = conf.getString("converter.class");
    if (StringUtils.isEmpty(converterClassName)) {
      return null;
    }

    return ReflectUtil.newInstance(converterClassName);
  }

}
