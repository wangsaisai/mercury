package com.bamboo.mercury.api;

import com.bamboo.mercury.element.cell.Cell;

public interface CellsRecord<T extends CellsRecord> extends Record {

  T newRecord(int cellNumber);

  void addCell(int index, Cell cell);

  Cell getCell(int index);

  int cellSize();

}
