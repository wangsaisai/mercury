package com.bamboo.mercury.rdb;

import com.bamboo.mercury.api.Writer;
import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.element.Constants;
import com.bamboo.mercury.exception.InvalidRecordException;
import com.bamboo.mercury.rdb.util.JDBCConnectorUtil;
import com.bamboo.mercury.rdb.util.RdbUtil;
import com.bamboo.mercury.util.JsonUtil;
import com.bamboo.mercury.api.CellsRecord;
import com.bamboo.mercury.element.schema.RWDataSetSchema;
import com.bamboo.mercury.element.schema.RWFieldSchema;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

public class CommonRdbWriter implements Writer<CellsRecord> {

  private static final String PREFIX_KEY = "commonrdbwriter";

  private static final String INSERT_SQL_TEMPLATE = "insert into %s (%s) values (%s)";
  protected PreparedStatement insertStmt;
  private RWDataSetSchema schema;
  private Connection conn;

  protected void init(Configuration conf) throws Exception {
    String writerSchemaFileName = conf.getString("schema.filename");
    schema = JsonUtil.readEntityFromFile(writerSchemaFileName, RWDataSetSchema.class);
    conn = JDBCConnectorUtil.getConnection(conf);

    String columns = StringUtils.join(Arrays.stream(schema.getFields()).map(
        RWFieldSchema::getName).collect(
        Collectors.toList()), Constants.COMMA);
    String[] questionMarks = new String[schema.getFields().length];
    for (int i = 0; i < schema.getFields().length; i++) {
      questionMarks[i] = Constants.QUESTION_MARK;
    }
    String questionMarkString = StringUtils.join(questionMarks, Constants.COMMA);

    String insertSql = String
        .format(INSERT_SQL_TEMPLATE, schema.getWname(), columns, questionMarkString);

    insertStmt = conn.prepareStatement(insertSql);
  }

  @Override
  public void init() throws Exception {
    init(ApplicationProperties.getPrefixConf(PREFIX_KEY));
  }

  @Override
  public void write(CellsRecord record) throws Exception {
    validateRecord(record);
    doWriteRecord(record);
  }

  @Override
  public String fakeWrite(CellsRecord record) throws Exception {
    validateRecord(record);
    setupStmt(record);

    // output record content, in case prepareStatement can't output sql clearly
    // see detail : https://stackoverflow.com/questions/2382532/how-can-i-get-the-sql-of-a-preparedstatement
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < record.cellSize(); i++) {
      sb.append(record.getCell(i).asString()).append(Constants.COMMA);
    }

    return sb.append(Constants.LINE_BREAK)
        .append(insertStmt.toString())
        .append(Constants.LINE_BREAK)
        .append(Constants.LINE_BREAK)
        .toString();
  }

  @Override
  public void validateRecord(CellsRecord record) throws InvalidRecordException {
    if (record.cellSize() != schema.getFields().length) {
      throw new InvalidRecordException(String
          .format("record cell size: %d, while schema column size: %d", record.cellSize(),
              schema.getFields().length));
    }
  }

  protected void setupStmt(CellsRecord record) throws SQLException {
    insertStmt.clearParameters();
    for (int i = 0; i < schema.getFields().length; i++) {
      RWFieldSchema fieldSchema = schema.getFields()[i];
      RdbUtil.setupStmt(fieldSchema, insertStmt, i + 1, record.getCell(i));
    }
  }

  private void doWriteRecord(CellsRecord record) throws Exception {
    setupStmt(record);
    insertStmt.executeUpdate();
  }

  @Override
  public void close() throws Exception {
    if (insertStmt != null) {
      insertStmt.close();
    }

    if (conn != null) {
      conn.close();
    }
  }
}
