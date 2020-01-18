package com.bamboo.mercury.text;

import com.bamboo.mercury.exception.InvalidRecordException;
import com.bamboo.mercury.util.JsonUtil;
import com.bamboo.mercury.api.CellsRecord;
import com.bamboo.mercury.api.Reader;
import com.bamboo.mercury.element.schema.RWDataSetSchema;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.configuration.Configuration;

public abstract class AbstractTextlineReader<T extends CellsRecord<T>> implements Reader<T> {

  protected BufferedReader reader;
  protected RWDataSetSchema schema;
  protected T record;
  protected String fieldSplitRegex;

  protected void initSchemaAndFieldSplit(Configuration conf)
      throws IOException {
    String schemaFileName = conf.getString("schema.filename");
    schema = JsonUtil.readEntityFromFile(schemaFileName, RWDataSetSchema.class);

    fieldSplitRegex = conf
        .getString("field.split.regex", TextlineConstants.DEFAULT_FIELD_SPLIT_REGEX);
  }

  protected abstract void initRecord(Configuration conf)
      throws ClassNotFoundException, IllegalAccessException, InstantiationException;

  protected void initReader(Configuration conf) throws IOException {
    String filename = conf.getString("filename");
    reader = new BufferedReader(new FileReader(new File(filename)));
  }

  protected abstract T convert(String line) throws InvalidRecordException;

  @Override
  public T read() throws Exception {
    record = record.newRecord(schema.getFields().length);

    return convert(reader.readLine());
  }

  @Override
  public void close() throws Exception {
    if (reader != null) {
      reader.close();
    }
  }
}
