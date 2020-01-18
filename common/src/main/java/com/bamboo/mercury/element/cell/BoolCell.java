package com.bamboo.mercury.element.cell;

import com.bamboo.mercury.api.DataType;
import com.bamboo.mercury.exception.CellConvertException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class BoolCell extends Cell {

  public BoolCell(Boolean bool) {
    super(bool, DataType.BOOL, null);
  }

  public BoolCell(final String data) {
    this(false);
    this.validate(data);
    if (null == data) {
      this.setRawData(null);
    } else {
      this.setRawData(Boolean.valueOf(data));
    }
  }

  @Override
  public Boolean asBoolean() {
    if (null == super.getRawData()) {
      return null;
    }

    return (Boolean) super.getRawData();
  }

  @Override
  public Integer asInt() {
    if (null == this.getRawData()) {
      return null;
    }

    return this.asBoolean() ? 1 : 0;
  }

  @Override
  public Long asLong() {
    if (null == this.getRawData()) {
      return null;
    }

    return this.asBoolean() ? 1L : 0L;
  }

  @Override
  public Double asDouble() {
    if (null == this.getRawData()) {
      return null;
    }

    return this.asBoolean() ? 1.0d : 0.0d;
  }

  @Override
  public String asString() {
    if (null == super.getRawData()) {
      return null;
    }

    return this.asBoolean() ? "true" : "false";
  }

  @Override
  public BigInteger asBigInteger() {
    if (null == this.getRawData()) {
      return null;
    }

    return BigInteger.valueOf(this.asLong());
  }

  @Override
  public BigDecimal asBigDecimal() {
    if (null == this.getRawData()) {
      return null;
    }

    return BigDecimal.valueOf(this.asLong());
  }

  @Override
  public Date asDate() {
    throw new CellConvertException(DataType.BOOL, DataType.DATE);
  }

  @Override
  public byte[] asBytes() {
    throw new CellConvertException(DataType.BOOL, DataType.BYTES);
  }

  private void validate(final String data) {
    if (null == data) {
      return;
    }

    if ("true".equalsIgnoreCase(data) || "false".equalsIgnoreCase(data)) {
      return;
    }

    throw new CellConvertException(DataType.STRING, data, DataType.BOOL);
  }
}
