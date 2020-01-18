package com.bamboo.mercury.text;

import com.bamboo.mercury.api.CellsDeltaRecord;
import com.bamboo.mercury.api.DeltaType;
import com.bamboo.mercury.api.Reader;
import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.element.cell.StringCell;
import com.bamboo.mercury.element.record.DefaultCellsDeltaRecord;
import com.bamboo.mercury.element.schema.RWDataSetSchema;
import com.bamboo.mercury.exception.InvalidRecordException;
import com.bamboo.mercury.util.JsonUtil;
import com.bamboo.mercury.util.ReflectUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

public class TextlineDeltaReader implements Reader<CellsDeltaRecord> {

  private static final String PREFIX_KEY = "textlinedeltareader";
  protected BufferedReader reader;
  private RWDataSetSchema schema;
  private CellsDeltaRecord<?> record;
  private String fieldSplitRegex;


  @Override
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    initSchemaAndRecord(conf);
    initReader(conf);
  }

  protected void initSchemaAndRecord(Configuration conf) throws IOException {
    String schemaFileName = conf.getString("schema.filename");
    schema = JsonUtil.readEntityFromFile(schemaFileName, RWDataSetSchema.class);

    fieldSplitRegex = conf
        .getString("field.split.regex", TextlineConstants.DEFAULT_FIELD_SPLIT_REGEX);

    String recordClassName = conf.getString("record.class.name");
    if (StringUtils.isNotEmpty(recordClassName)) {
      record = ReflectUtil.newInstance(recordClassName);
    } else {
      record = new DefaultCellsDeltaRecord();
    }
  }

  protected void initReader(Configuration conf) throws IOException {
    String filename = conf.getString("filename");
    reader = new BufferedReader(new FileReader(new File(filename)));
  }


  @Override
  public CellsDeltaRecord read() throws Exception {
    record = record.newRecord(schema.getFields().length);

    String line = reader.readLine();
    if (line == null) {
      return null;
    }

    String[] array = line.split(fieldSplitRegex);
    if (array.length - 1 != record.cellSize()) {
      throw new InvalidRecordException();
    }

    record.setDeltaType(DeltaType.valueOf(array[0]));

    for (int i = 1; i < array.length; i++) {
      // handle null
      if (TextlineConstants.NULL_DESERIALIZE.equalsIgnoreCase(array[i])) {
        array[i] = null;
      }
      record.addCell(i - 1, new StringCell(array[i], schema.getFields()[i - 1].getCellSchema()));
    }
    return record;
  }

  @Override
  public void close() throws Exception {
    if (reader != null) {
      reader.close();
    }
  }
}
