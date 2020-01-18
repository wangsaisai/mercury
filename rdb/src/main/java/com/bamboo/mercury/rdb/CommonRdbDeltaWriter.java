package com.bamboo.mercury.rdb;

import static com.bamboo.mercury.element.Constants.COMMA;
import static com.bamboo.mercury.element.Constants.EQUAL_MARK;
import static com.bamboo.mercury.element.Constants.LINE_BREAK;
import static com.bamboo.mercury.element.Constants.QUESTION_MARK;

import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.rdb.exception.RdbMercuryRuntimeException;
import com.bamboo.mercury.rdb.util.JDBCConnectorUtil;
import com.bamboo.mercury.rdb.util.RdbUtil;
import com.bamboo.mercury.util.JsonUtil;
import com.bamboo.mercury.api.CellsDeltaRecord;
import com.bamboo.mercury.element.schema.RWDataSetSchema;
import com.bamboo.mercury.element.schema.RWFieldSchema;
import com.bamboo.mercury.api.Writer;
import com.bamboo.mercury.exception.InvalidRecordException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

public class CommonRdbDeltaWriter implements Writer<CellsDeltaRecord> {

  private static final String PREFIX_KEY = "commonrdbdeltawriter";

  private static final String INSERT_SQL_TEMPLATE = "insert into %s (%s) values (%s)";

  private static final String UPDATE_SQL_TEMPLATE = "update %s set %s where %s";

  private static final String DELETE_SQL_TEMPLATE = "delete from %s where %s";

  private RWDataSetSchema schema;

  private Connection conn;

  private PreparedStatement insertStmt;

  private PreparedStatement updateStmt;

  private PreparedStatement deleteStmt;

  @Override
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    String writerSchemaFileName = conf.getString("schema.filename");
    schema = JsonUtil.readEntityFromFile(writerSchemaFileName, RWDataSetSchema.class);
    if (schema.pkFields().length < 1) {
      throw new RdbMercuryRuntimeException("no primary key in schema.");
    }

    conn = JDBCConnectorUtil.getConnection(conf);

    // insert stmt
    String columns = StringUtils.join(Arrays.stream(schema.getFields()).map(
        RWFieldSchema::getName).collect(
        Collectors.toList()), COMMA);
    String[] questionMarks = new String[schema.getFields().length];
    for (int i = 0; i < schema.getFields().length; i++) {
      questionMarks[i] = QUESTION_MARK;
    }
    String questionMarkString = StringUtils.join(questionMarks, COMMA);

    String insertSql = String
        .format(INSERT_SQL_TEMPLATE, schema.getWname(), columns, questionMarkString);

    insertStmt = conn.prepareStatement(insertSql);

    // update stmt
    StringBuilder setSb = new StringBuilder();
    StringBuilder whereSb = new StringBuilder();
    for (int i = 0; i < schema.getFields().length; i++) {
      RWFieldSchema fieldSchema = schema.getFields()[i];
      if (fieldSchema.isPrimaryKey()) {
        whereSb.append(fieldSchema.getName()).append(EQUAL_MARK).append(QUESTION_MARK)
            .append(COMMA);
      } else {
        setSb.append(fieldSchema.getName()).append(EQUAL_MARK).append(QUESTION_MARK).append(COMMA);
      }
    }

    String setSql = setSb.substring(0, setSb.length() - 1);
    String whereSql = whereSb.substring(0, whereSb.length() - 1);

    String updateSql = String.format(UPDATE_SQL_TEMPLATE, schema.getWname(), setSql, whereSql);
    String deleteSql = String.format(DELETE_SQL_TEMPLATE, schema.getWname(), whereSql);

    updateStmt = conn.prepareStatement(updateSql);
    deleteStmt = conn.prepareStatement(deleteSql);
  }


  private void setupInsertStmt(CellsDeltaRecord record) throws SQLException {
    insertStmt.clearParameters();
    for (int i = 0; i < schema.getFields().length; i++) {
      RWFieldSchema fieldSchema = schema.getFields()[i];

      RdbUtil.setupStmt(fieldSchema, insertStmt, i + 1, record.getCell(i));
    }
  }


  private void setupUpdateStmt(CellsDeltaRecord record) throws SQLException {
    updateStmt.clearParameters();
    int setIndex = 1;
    int whereIndex = schema.getFields().length - schema.pkFields().length + 1;
    for (int i = 0; i < schema.getFields().length; i++) {
      RWFieldSchema fieldSchema = schema.getFields()[i];
      int index;
      if (fieldSchema.isPrimaryKey()) {
        index = whereIndex++;
      } else {
        index = setIndex++;
      }

      RdbUtil.setupStmt(fieldSchema, updateStmt, index, record.getCell(i));
    }
  }


  private void setupDeleteStmt(CellsDeltaRecord record) throws SQLException {
    deleteStmt.clearParameters();
    int index = 1;
    for (int i = 0; i < schema.getFields().length; i++) {
      RWFieldSchema fieldSchema = schema.getFields()[i];
      if (fieldSchema.isPrimaryKey()) {
        RdbUtil.setupStmt(fieldSchema, deleteStmt, index++, record.getCell(i));
      }
    }
  }

  @Override
  public void write(CellsDeltaRecord record) throws Exception {
    validateRecord(record);
    switch (record.getDeltaType()) {
      case ADD:
        setupInsertStmt(record);
        insertStmt.executeUpdate();
        break;
      case UPDATE:
        setupUpdateStmt(record);
        updateStmt.executeUpdate();
        break;
      case DELETE:
        setupDeleteStmt(record);
        deleteStmt.executeUpdate();
        break;
    }
  }

  private String doFakeWrite(PreparedStatement stmt, CellsDeltaRecord record) {
    StringBuilder sb = new StringBuilder().append(record.getDeltaType()).append(LINE_BREAK);
    for (int i = 0; i < record.cellSize(); i++) {
      sb.append(record.getCell(i).asString()).append(COMMA);
    }

    return sb.append(LINE_BREAK)
        .append(stmt.toString())
        .append(LINE_BREAK)
        .append(LINE_BREAK)
        .toString();
  }

  @Override
  public String fakeWrite(CellsDeltaRecord record) throws Exception {
    validateRecord(record);
    switch (record.getDeltaType()) {
      case ADD:
        setupInsertStmt(record);
        return doFakeWrite(insertStmt, record);
      case UPDATE:
        setupUpdateStmt(record);
        return doFakeWrite(updateStmt, record);
      case DELETE:
        setupDeleteStmt(record);
        return doFakeWrite(deleteStmt, record);
    }
    return "";
  }

  @Override
  public void validateRecord(CellsDeltaRecord record) throws InvalidRecordException {
    switch (record.getDeltaType()) {
      case ADD:
      case UPDATE:
        if (record.cellSize() != schema.getFields().length) {
          throw new InvalidRecordException(String
              .format("record cell size: %d, while schema fields length: %d", record.cellSize(),
                  schema.getFields().length));
        }
        break;
      case DELETE:
        if (record.cellSize() < schema.pkFields().length) {
          throw new InvalidRecordException(String
              .format("delete record cell size: %d less than schema primary keys length: %d",
                  record.cellSize(),
                  schema.pkFields().length));
        }
        break;
      default:
        break;
    }
  }


  @Override
  public void close() throws Exception {
    if (insertStmt != null) {
      insertStmt.close();
    }

    if (updateStmt != null) {
      updateStmt.close();
    }

    if (deleteStmt != null) {
      deleteStmt.close();
    }

    if (conn != null) {
      conn.close();
    }
  }
}
