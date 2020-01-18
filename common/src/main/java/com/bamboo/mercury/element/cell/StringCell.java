package com.bamboo.mercury.element.cell;

import com.bamboo.mercury.api.DataType;
import com.bamboo.mercury.element.cellschema.ByteSchema;
import com.bamboo.mercury.element.cellschema.CellSchema;
import com.bamboo.mercury.element.cellschema.DateSchema;
import com.bamboo.mercury.exception.CellConvertException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;


public class StringCell extends Cell {

  public StringCell(final String rawData) {
    super(rawData, DataType.STRING);
  }

  public StringCell(final String rawData, CellSchema schema) {
    super(rawData, DataType.STRING, schema);
  }

  @Override
  public String asString() {
    if (null == this.getRawData()) {
      return null;
    }

    return (String) this.getRawData();
  }

  private void validateDoubleSpecific(final String data) {
    if ("NaN".equals(data) || "Infinity".equals(data)
        || "-Infinity".equals(data)) {
      throw new CellConvertException(
          String.format("String[\"%s\"] is specific double type, cannot convert to other type",
              data));
    }
  }

  @Override
  public BigInteger asBigInteger() {
    if (null == this.getRawData()) {
      return null;
    }

    this.validateDoubleSpecific((String) this.getRawData());

    try {
      return this.asBigDecimal().toBigInteger();
    } catch (Exception e) {
      throw new CellConvertException(e);
    }
  }

  @Override
  public Integer asInt() {
    if (null == this.getRawData()) {
      return null;
    }

    this.validateDoubleSpecific((String) this.getRawData());

    try {
      BigInteger integer = this.asBigInteger();
      OverFlowUtil.validateIntNotOverFlow(integer);
      return integer.intValue();
    } catch (Exception e) {
      throw new CellConvertException(DataType.STRING, DataType.INT);
    }
  }

  @Override
  public Long asLong() {
    if (null == this.getRawData()) {
      return null;
    }

    this.validateDoubleSpecific((String) this.getRawData());

    try {
      BigInteger integer = this.asBigInteger();
      OverFlowUtil.validateLongNotOverFlow(integer);
      return integer.longValue();
    } catch (Exception e) {
      throw new CellConvertException(DataType.STRING, DataType.LONG);
    }
  }

  @Override
  public BigDecimal asBigDecimal() {
    if (null == this.getRawData()) {
      return null;
    }

    this.validateDoubleSpecific((String) this.getRawData());

    try {
      return new BigDecimal(this.asString());
    } catch (Exception e) {
      throw new CellConvertException(e);
    }
  }

  @Override
  public Double asDouble() {
    if (null == this.getRawData()) {
      return null;
    }

    String data = (String) this.getRawData();
    if ("NaN".equals(data)) {
      return Double.NaN;
    }

    if ("Infinity".equals(data)) {
      return Double.POSITIVE_INFINITY;
    }

    if ("-Infinity".equals(data)) {
      return Double.NEGATIVE_INFINITY;
    }

    BigDecimal decimal = this.asBigDecimal();
    OverFlowUtil.validateDoubleNotOverFlow(decimal);

    return decimal.doubleValue();
  }

  @Override
  public Boolean asBoolean() {
    if (null == this.getRawData()) {
      return null;
    }

    if ("true".equalsIgnoreCase(this.asString())) {
      return true;
    }

    if ("false".equalsIgnoreCase(this.asString())) {
      return false;
    }

    throw new CellConvertException("String", this.asString(), "Bool");
  }

  @Override
  public Date asDate() {
    if (null == asString()) {
      return null;
    }

    if (getSchema() == null || !(getSchema() instanceof DateSchema)) {
      throw new CellConvertException(
          String.format("String[\"%s\"] can't cast to Date. less date schema", this.asString()));
    }

    try {
      return ((DateSchema) getSchema()).getFastDateFormat().parse(this.asString());
    } catch (Exception e) {
      throw new CellConvertException(e);
    }
  }

  @Override
  public byte[] asBytes() {
    if (null == asString()) {
      return null;
    }

    if (getSchema() == null || !(getSchema() instanceof ByteSchema)) {
      return this.asString().getBytes();
    }

    try {
      return this.asString().getBytes(((ByteSchema) getSchema()).getEncoding());
    } catch (Exception e) {
      throw new CellConvertException(e);
    }
  }
}
