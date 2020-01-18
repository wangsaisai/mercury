package com.bamboo.mercury.element.cell;

import com.bamboo.mercury.api.DataType;
import com.bamboo.mercury.exception.CellConvertException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.apache.commons.lang3.math.NumberUtils;

public class LongCell extends Cell {

  /**
   * use BigInteger to store rawdata
   */
  public LongCell(final String data) {
    super(null, DataType.LONG);
    if (null == data) {
      return;
    }

    try {
      BigInteger rawData = NumberUtils.createBigDecimal(data)
          .toBigInteger();
      super.setRawData(rawData);
    } catch (Exception e) {
      throw new CellConvertException(DataType.STRING, data, DataType.LONG);
    }
  }

  public LongCell(Long data) {
    this(null == data ? null : BigInteger.valueOf(data));
  }

  public LongCell(Integer data) {
    this(null == data ? null : BigInteger.valueOf(data));
  }

  public LongCell(BigInteger data) {
    super(data, DataType.LONG);
  }

  @Override
  public BigInteger asBigInteger() {
    if (null == this.getRawData()) {
      return null;
    }

    return (BigInteger) this.getRawData();
  }

  @Override
  public Integer asInt() {
    BigInteger rawData = (BigInteger) this.getRawData();
    if (null == rawData) {
      return null;
    }

    OverFlowUtil.validateIntNotOverFlow(rawData);

    return rawData.intValue();
  }

  @Override
  public Long asLong() {
    BigInteger rawData = (BigInteger) this.getRawData();
    if (null == rawData) {
      return null;
    }

    OverFlowUtil.validateLongNotOverFlow(rawData);

    return rawData.longValue();
  }

  @Override
  public Double asDouble() {
    if (null == this.getRawData()) {
      return null;
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

    return this.asBigInteger().compareTo(BigInteger.ZERO) != 0;
  }

  @Override
  public BigDecimal asBigDecimal() {
    if (null == this.getRawData()) {
      return null;
    }

    return new BigDecimal(this.asBigInteger());
  }

  @Override
  public String asString() {
    if (null == this.getRawData()) {
      return null;
    }
    return this.getRawData().toString();
  }

  @Override
  public Date asDate() {
    if (null == this.getRawData()) {
      return null;
    }
    return new Date(this.asLong());
  }

  @Override
  public byte[] asBytes() {
    throw new CellConvertException(DataType.LONG, DataType.BYTES);
  }

}
