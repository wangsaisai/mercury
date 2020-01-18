package com.bamboo.mercury.rdb.util;

import com.bamboo.mercury.element.schema.RWFieldSchema;
import com.bamboo.mercury.element.cell.BoolCell;
import com.bamboo.mercury.element.cell.BytesCell;
import com.bamboo.mercury.element.cell.Cell;
import com.bamboo.mercury.element.cell.DateCell;
import com.bamboo.mercury.element.cell.DoubleCell;
import com.bamboo.mercury.element.cell.LongCell;
import com.bamboo.mercury.element.cell.StringCell;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RdbUtil {

  public static Cell createCell(RWFieldSchema fieldSchema, ResultSet rs, int rsIndex)
      throws SQLException {
    Cell cell;

    switch (fieldSchema.getRtype()) {
      case STRING:
        cell = new StringCell(rs.getString(rsIndex), fieldSchema.getCellSchema());
        break;
      case DOUBLE:
        cell = new DoubleCell(rs.getDouble(rsIndex));
        break;
      case BYTES:
        cell = new BytesCell(rs.getBytes(rsIndex));
        break;
      case LONG:
      case INT:
        cell = new LongCell(rs.getLong(rsIndex));
        break;
      case DATE:
        // todo add support for date, time, timestamp
        cell = new DateCell(rs.getTimestamp(rsIndex), fieldSchema.getCellSchema());
        break;
      case BOOL:
        cell = new BoolCell(rs.getBoolean(rsIndex));
        break;
      default:
        Object obj = rs.getObject(rsIndex);
        String rawData = obj == null ? null : obj.toString();
        cell = new StringCell(rawData);
        break;
    }

    return cell;
  }


  public static void setupStmt(RWFieldSchema schema, PreparedStatement stmt, int parameterIndex,
      Cell cell)
      throws SQLException {
    // PreparedStatement index start from 1
    switch (schema.getWtype()) {
      case INT:
        stmt.setInt(parameterIndex, cell.asInt());
        break;
      case BOOL:
        stmt.setBoolean(parameterIndex, cell.asBoolean());
        break;
      case DATE:
        java.util.Date date = cell.asDate();
        stmt.setTimestamp(parameterIndex, new Timestamp(date.getTime()));
        break;
      case LONG:
        stmt.setLong(parameterIndex, cell.asLong());
        break;
      case BYTES:
        stmt.setBytes(parameterIndex, cell.asBytes());
        break;
      case DOUBLE:
        stmt.setDouble(parameterIndex, cell.asDouble());
        break;
      case STRING:
        stmt.setString(parameterIndex, cell.asString());
        break;
      default:
        String tmp = cell.getRawData() == null ? null : cell.getRawData().toString();
        stmt.setString(parameterIndex, tmp);
        break;
    }
  }

}
