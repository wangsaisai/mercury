package com.bamboo.mercury.text;

import com.bamboo.mercury.api.CellsDeltaRecord;
import com.bamboo.mercury.api.DeltaType;
import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.element.cell.StringCell;
import com.bamboo.mercury.element.record.DefaultCellsDeltaRecord;
import com.bamboo.mercury.exception.InvalidRecordException;
import com.bamboo.mercury.util.ReflectUtil;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

public class TextlineDeltaReader2<T extends CellsDeltaRecord<T>> extends AbstractTextlineReader<T> {

  private static final String PREFIX_KEY = "textlinedeltareader";

  @Override
  protected void initRecord(Configuration conf) {
    String recordClassName = conf.getString("record.class.name");
    if (StringUtils.isNotEmpty(recordClassName)) {
      record = ReflectUtil.newInstance(recordClassName);
    } else {
      record = (T) new DefaultCellsDeltaRecord();
    }
  }

  @Override
  protected T convert(String line) throws InvalidRecordException {
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
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    initSchemaAndFieldSplit(conf);
    initRecord(conf);
    initReader(conf);
  }
}
