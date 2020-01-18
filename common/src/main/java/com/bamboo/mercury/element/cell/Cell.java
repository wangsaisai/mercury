package com.bamboo.mercury.element.cell;

import com.bamboo.mercury.api.DataType;
import com.bamboo.mercury.element.cellschema.CellSchema;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public abstract class Cell {

  private DataType type;

  private Object rawData;

  private CellSchema schema;

  public Cell(final Object object, final DataType type) {
    this(object, type, null);
  }

  public Cell(final Object object, final DataType type, final CellSchema schema) {
    this.rawData = object;
    this.type = type;
    this.schema = schema;
  }

  public Object getRawData() {
    return this.rawData;
  }

  protected void setRawData(Object rawData) {
    this.rawData = rawData;
  }

  public DataType getType() {
    return this.type;
  }

  protected void setType(DataType type) {
    this.type = type;
  }

  public CellSchema getSchema() {
    return schema;
  }

  public void setSchema(CellSchema schema) {
    this.schema = schema;
  }

  public abstract Integer asInt();

  public abstract Long asLong();

  public abstract Double asDouble();

  public abstract String asString();

  public abstract Date asDate();

  public abstract byte[] asBytes();

  public abstract Boolean asBoolean();

  public abstract BigDecimal asBigDecimal();

  public abstract BigInteger asBigInteger();

  @Override
  public String toString() {
    return "Column{" +
        "type=" + type +
        ", rawData=" + rawData +
        ", schema=" + schema +
        '}';
  }

}
