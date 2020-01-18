package com.bamboo.mercury.element.record;

public class DefaultCellsRecord extends AbstractCellsRecord<DefaultCellsRecord> {

  public DefaultCellsRecord() {
    super();
  }

  private DefaultCellsRecord(int cellNumber) {
    super(cellNumber);
  }

  @Override
  public DefaultCellsRecord newRecord(int cellNumber) {
    return new DefaultCellsRecord(cellNumber);
  }
}
