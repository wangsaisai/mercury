package com.bamboo.mercury.element.schema;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DataSet (table, file, ...) schema used by reader and writer
 */
public class RWDataSetSchema {

  private String rname;

  private String wname;

  private RWFieldSchema[] fields;

  @Expose(serialize = false, deserialize = false)
  private RWFieldSchema[] pkFields;

  public RWFieldSchema[] pkFields() {
    if (pkFields == null) {
      List<RWFieldSchema> list = new ArrayList<>(1);
      Arrays.stream(fields).filter(RWFieldSchema::isPrimaryKey).forEach(list::add);
      pkFields = list.toArray(new RWFieldSchema[0]);
    }

    return pkFields;
  }

  public String getRname() {
    return rname;
  }

  public void setRname(String rname) {
    this.rname = rname;
  }

  public String getWname() {
    return wname;
  }

  public void setWname(String wname) {
    this.wname = wname;
  }

  public RWFieldSchema[] getFields() {
    return fields;
  }

  public void setFields(RWFieldSchema[] fields) {
    this.fields = fields;
  }
}
