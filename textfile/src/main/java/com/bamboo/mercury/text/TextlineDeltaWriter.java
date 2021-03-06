package com.bamboo.mercury.text;

import com.bamboo.mercury.api.CellsDeltaRecord;
import com.bamboo.mercury.api.Writer;
import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.element.Constants;
import com.bamboo.mercury.element.schema.RWDataSetSchema;
import com.bamboo.mercury.exception.InvalidRecordException;
import com.bamboo.mercury.util.JsonUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.configuration.Configuration;

public class TextlineDeltaWriter implements Writer<CellsDeltaRecord> {

  private static final String PREFIX_KEY = "textlinedeltawriter";
  protected BufferedWriter writer;
  private RWDataSetSchema schema;
  private String fieldSplit;

  @Override
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    initSchemaAndRecord(conf);
    initWriter(conf);
  }


  protected void initSchemaAndRecord(Configuration conf) throws IOException {
    String schemaFileName = conf.getString("schema.filename");
    schema = JsonUtil.readEntityFromFile(schemaFileName, RWDataSetSchema.class);

    fieldSplit = conf.getString("field.split", TextlineConstants.DEFAULT_FIELD_SPLIT);
  }

  protected void initWriter(Configuration conf) throws IOException {
    String filename = conf.getString("filename");
    writer = new BufferedWriter(new FileWriter(new File(filename)));
  }

  private String convertRecord(CellsDeltaRecord record) {
    StringBuilder sb = new StringBuilder().append(record.getDeltaType()).append(fieldSplit);
    for (int i = 0; i < record.cellSize() - 1; i++) {
      sb.append(record.getCell(i).asString());
      sb.append(fieldSplit);
    }
    sb.append(record.getCell(record.cellSize() - 1).asString());
    sb.append(Constants.LINE_BREAK);
    return sb.toString();
  }

  @Override
  public void write(CellsDeltaRecord record) throws Exception {
    validateRecord(record);
    writer.write(convertRecord(record));
  }

  @Override
  public String fakeWrite(CellsDeltaRecord record) throws Exception {
    validateRecord(record);
    return convertRecord(record);
  }

  @Override
  public void validateRecord(CellsDeltaRecord record) throws InvalidRecordException {
    if (record.cellSize() != schema.getFields().length) {
      throw new InvalidRecordException(String
          .format("record size not match with schema. record size : %d, schema field length : %d",
              record.cellSize(), schema.getFields().length));
    }
  }

  @Override
  public void close() throws Exception {
    if (writer != null) {
      writer.close();
    }
  }
}
