package com.bamboo.mercury.element.schema;

import com.bamboo.mercury.api.DataType;
import com.bamboo.mercury.element.cellschema.CellSchema;

/**
 * field schema used by reader and writer
 */
public class RWFieldSchema {

  // start from 0
  private int index;

  private boolean primaryKey;

  private String name;

  private DataType rtype;

  private DataType wtype;

  private CellSchema cellSchema;

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public boolean isPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(boolean primaryKey) {
    this.primaryKey = primaryKey;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DataType getRtype() {
    return rtype;
  }

  public void setRtype(DataType rtype) {
    this.rtype = rtype;
  }

  public DataType getWtype() {
    return wtype;
  }

  public void setWtype(DataType wtype) {
    this.wtype = wtype;
  }

  public CellSchema getCellSchema() {
    return cellSchema;
  }

  public void setCellSchema(CellSchema cellSchema) {
    this.cellSchema = cellSchema;
  }
}
