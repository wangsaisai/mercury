package com.bamboo.mercury.element.cellschema;

public class ByteSchema extends CellSchema {

  public static ByteSchema UTF8_SCHEMA = new ByteSchema("utf-8");

  private String encoding;

  public ByteSchema(String encoding) {
    this.encoding = encoding;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  @Override
  public String toString() {
    return "ByteSchema{" +
        "encoding='" + encoding + '\'' +
        '}';
  }
}
