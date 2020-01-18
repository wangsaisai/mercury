package com.bamboo.mercury.text;

import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.element.cell.StringCell;
import com.bamboo.mercury.element.record.DefaultCellsRecord;
import com.bamboo.mercury.util.ReflectUtil;
import com.bamboo.mercury.api.CellsRecord;
import com.bamboo.mercury.exception.InvalidRecordException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

public class TextlineReader2 extends AbstractTextlineReader {

  private static final String PREFIX_KEY = "textlinereader";

  @Override
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    initSchemaAndFieldSplit(conf);
    initRecord(conf);
    initReader(conf);
  }

  @Override
  protected void initRecord(Configuration conf) {
    String recordClassName = conf.getString("record.class.name");
    if (StringUtils.isNotEmpty(recordClassName)) {
      record = ReflectUtil.newInstance(recordClassName);
    } else {
      record = new DefaultCellsRecord();
    }
  }

  @Override
  protected CellsRecord convert(String line) throws InvalidRecordException {
    if (line == null) {
      return null;
    }

    String[] array = line.split(fieldSplitRegex);
    if (array.length != record.cellSize()) {
      throw new InvalidRecordException();
    }

    for (int i = 0; i < array.length; i++) {
      // handle null
      if (TextlineConstants.NULL_DESERIALIZE.equalsIgnoreCase(array[i])) {
        array[i] = null;
      }
      record.addCell(i, new StringCell(array[i], schema.getFields()[i].getCellSchema()));
    }
    return record;
  }

}
