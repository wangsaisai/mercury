package com.bamboo.mercury.element.record;

import com.bamboo.mercury.element.cell.Cell;
import com.bamboo.mercury.exception.MercuryRuntimeException;
import com.bamboo.mercury.api.CellsRecord;

public abstract class AbstractCellsRecord<T extends CellsRecord> implements CellsRecord<T> {

  private Cell[] cells;

  protected AbstractCellsRecord() {
    cells = new Cell[0];
  }

  protected AbstractCellsRecord(int cellNumber) {
    cells = new Cell[cellNumber];
  }

  private void validateIndex(int index) {
    if (index < 0 || index > cells.length) {
      throw new MercuryRuntimeException(String
          .format("index out of range, input index: %d, cells length: %d", index,
              cells.length));
    }
  }

  @Override
  public void addCell(int index, Cell cell) {
    validateIndex(index);

    if (cell == null) {
      throw new MercuryRuntimeException("cannot add null to cells record");
    }

    cells[index] = cell;
  }

  @Override
  public Cell getCell(int index) {
    validateIndex(index);
    return cells[index];
  }

  @Override
  public int cellSize() {
    return cells.length;
  }

}
