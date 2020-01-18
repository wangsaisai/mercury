package com.bamboo.mercury.element.record;

import com.bamboo.mercury.api.CellsDeltaRecord;
import com.bamboo.mercury.api.DeltaType;

public class DefaultCellsDeltaRecord extends AbstractCellsRecord<DefaultCellsDeltaRecord> implements
    CellsDeltaRecord<DefaultCellsDeltaRecord> {

  private DeltaType deltaType;

  public DefaultCellsDeltaRecord() {
    super();
  }

  private DefaultCellsDeltaRecord(int cellNumber) {
    super(cellNumber);
  }

  @Override
  public DefaultCellsDeltaRecord newRecord(int cellNumber) {
    return new DefaultCellsDeltaRecord(cellNumber);
  }

  @Override
  public DeltaType getDeltaType() {
    return deltaType;
  }

  @Override
  public void setDeltaType(DeltaType deltaType) {
    this.deltaType = deltaType;
  }
}
