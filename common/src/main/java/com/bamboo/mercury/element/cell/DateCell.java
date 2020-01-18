package com.bamboo.mercury.element.cell;

import com.bamboo.mercury.api.DataType;
import com.bamboo.mercury.element.cellschema.CellSchema;
import com.bamboo.mercury.element.cellschema.DateSchema;
import com.bamboo.mercury.exception.CellConvertException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

public class DateCell extends Cell {

  private DateType subType = DateType.DATETIME;

  /**
   * use long to store rawdata
   */
  public DateCell(final Long stamp) {
    super(stamp, DataType.DATE);
  }

  public DateCell(final Date date) {
    this(date == null ? null : date.getTime());
  }

  public DateCell(final java.sql.Date date) {
    this(date == null ? null : date.getTime());
    this.setSubType(DateType.DATE);
  }

  public DateCell(final java.sql.Time time) {
    this(time == null ? null : time.getTime());
    this.setSubType(DateType.TIME);
  }

  public DateCell(final java.sql.Timestamp ts) {
    this(ts == null ? null : ts.getTime());
    this.setSubType(DateType.DATETIME);
  }

  public DateCell(final java.sql.Timestamp ts, CellSchema schema) {
    this(ts);
    this.setSchema(schema);
  }

  @Override
  public Long asLong() {
    return (Long) this.getRawData();
  }

  @Override
  public String asString() {
    if (null == asDate()) {
      return null;
    }

    if (null == getSchema() || !(getSchema() instanceof DateSchema)) {
      throw new CellConvertException(
          String.format("Date[%s] cannot cast to String. less date schema", this.toString()));
    }

    try {
      return DateFormatUtils
          .format(asDate(), ((DateSchema) getSchema()).getPattern(),
              ((DateSchema) getSchema()).getTimeZoneEntity());
    } catch (Exception e) {
      throw new CellConvertException(e);
    }
  }

  @Override
  public Date asDate() {
    if (null == this.getRawData()) {
      return null;
    }

    return new Date((Long) this.getRawData());
  }

  @Override
  public Integer asInt() {
    throw new CellConvertException(DataType.DATE, DataType.INT);
  }

  @Override
  public byte[] asBytes() {
    throw new CellConvertException(DataType.DATE, DataType.BYTES);
  }

  @Override
  public Boolean asBoolean() {
    throw new CellConvertException(DataType.DATE, DataType.BOOL);
  }

  @Override
  public Double asDouble() {
    throw new CellConvertException(DataType.DATE, DataType.DOUBLE);
  }

  @Override
  public BigInteger asBigInteger() {
    throw new CellConvertException("Date", "BigInteger");
  }

  @Override
  public BigDecimal asBigDecimal() {
    throw new CellConvertException("Date", "BigDecimal");
  }

  public DateType getSubType() {
    return subType;
  }

  public void setSubType(DateType subType) {
    this.subType = subType;
  }

  public enum DateType {
    DATE, TIME, DATETIME
  }
}