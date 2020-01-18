package com.bamboo.mercury.element.cell;

import com.bamboo.mercury.api.DataType;
import com.bamboo.mercury.element.cellschema.ByteSchema;
import com.bamboo.mercury.element.cellschema.CellSchema;
import com.bamboo.mercury.exception.CellConvertException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.apache.commons.lang3.ArrayUtils;

public class BytesCell extends Cell {

  public BytesCell(byte[] bytes) {
    this(bytes, null);
  }

  public BytesCell(byte[] bytes, CellSchema schema) {
    super(ArrayUtils.clone(bytes), DataType.BYTES, schema);
  }

  @Override
  public byte[] asBytes() {
    if (null == this.getRawData()) {
      return null;
    }

    return (byte[]) this.getRawData();
  }

  @Override
  public String asString() {
    if (null == this.getRawData()) {
      return null;
    }

    try {
      if (getSchema() == null || !(getSchema() instanceof ByteSchema)) {
        return new String(asBytes());
      } else {
        return new String(asBytes(), ((ByteSchema) getSchema()).getEncoding());
      }
    } catch (Exception e) {
      throw new CellConvertException(e);
    }
  }

  @Override
  public Integer asInt() {
    throw new CellConvertException(DataType.BYTES, DataType.INT);
  }

  @Override
  public Long asLong() {
    throw new CellConvertException(DataType.BYTES, DataType.LONG);
  }

  @Override
  public BigDecimal asBigDecimal() {
    throw new CellConvertException("Bytes", "BigDecimal");
  }

  @Override
  public BigInteger asBigInteger() {
    throw new CellConvertException("Bytes", "BigInteger");
  }

  @Override
  public Double asDouble() {
    throw new CellConvertException(DataType.BYTES, DataType.DOUBLE);
  }

  @Override
  public Date asDate() {
    throw new CellConvertException(DataType.BYTES, DataType.DATE);
  }

  @Override
  public Boolean asBoolean() {
    throw new CellConvertException(DataType.BYTES, DataType.BOOL);
  }
}
