package com.bamboo.mercury.element.cell;

import com.bamboo.mercury.api.DataType;
import com.bamboo.mercury.exception.CellConvertException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class DoubleCell extends Cell {

  /**
   * use string store the rawdata, instead of double. in case of the loss of data accuracy
   */
  public DoubleCell(final String data) {
    super(data, DataType.DOUBLE);
    this.validate(data);
  }

  public DoubleCell(Long data) {
    this(data == null ? null : String.valueOf(data));
  }

  public DoubleCell(Integer data) {
    this(data == null ? null : String.valueOf(data));
  }

  public DoubleCell(final Double data) {
    this(data == null ? null
        : new BigDecimal(String.valueOf(data)).toPlainString());
  }

  public DoubleCell(final Float data) {
    this(data == null ? null
        : new BigDecimal(String.valueOf(data)).toPlainString());
  }

  public DoubleCell(final BigDecimal data) {
    this(null == data ? null : data.toPlainString());
  }

  public DoubleCell(final BigInteger data) {
    this(null == data ? null : data.toString());
  }

  @Override
  public BigDecimal asBigDecimal() {
    if (null == this.getRawData()) {
      return null;
    }

    try {
      return new BigDecimal((String) this.getRawData());
    } catch (NumberFormatException e) {
      throw new CellConvertException("String", "BigDecimal");
    }
  }

  @Override
  public Double asDouble() {
    if (null == this.getRawData()) {
      return null;
    }

    String string = (String) this.getRawData();

    boolean isDoubleSpecific = string.equals("NaN")
        || string.equals("-Infinity") || string.equals("+Infinity");
    if (isDoubleSpecific) {
      return Double.valueOf(string);
    }

    BigDecimal result = this.asBigDecimal();
    OverFlowUtil.validateDoubleNotOverFlow(result);

    return result.doubleValue();
  }

  @Override
  public Integer asInt() {
    if (null == this.getRawData()) {
      return null;
    }

    BigDecimal result = this.asBigDecimal();
    OverFlowUtil.validateIntNotOverFlow(result.toBigInteger());

    return result.intValue();
  }

  @Override
  public Long asLong() {
    if (null == this.getRawData()) {
      return null;
    }

    BigDecimal result = this.asBigDecimal();
    OverFlowUtil.validateLongNotOverFlow(result.toBigInteger());

    return result.longValue();
  }

  @Override
  public BigInteger asBigInteger() {
    if (null == this.getRawData()) {
      return null;
    }

    return this.asBigDecimal().toBigInteger();
  }

  @Override
  public String asString() {
    if (null == this.getRawData()) {
      return null;
    }
    return (String) this.getRawData();
  }

  @Override
  public Boolean asBoolean() {
    throw new CellConvertException(DataType.DOUBLE, DataType.BOOL);
  }

  @Override
  public Date asDate() {
    throw new CellConvertException(DataType.DOUBLE, DataType.DATE);
  }

  @Override
  public byte[] asBytes() {
    throw new CellConvertException(DataType.DOUBLE, DataType.BYTES);
  }

  private void validate(final String data) {
    if (null == data) {
      return;
    }

    if (data.equalsIgnoreCase("NaN") || data.equalsIgnoreCase("-Infinity")
        || data.equalsIgnoreCase("Infinity")) {
      return;
    }

    try {
      new BigDecimal(data);
    } catch (Exception e) {
      throw new CellConvertException(DataType.STRING, data, DataType.DOUBLE);
    }
  }

}