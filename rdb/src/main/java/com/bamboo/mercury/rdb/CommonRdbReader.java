package com.bamboo.mercury.rdb;

import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.element.cell.Cell;
import com.bamboo.mercury.element.record.DefaultCellsRecord;
import com.bamboo.mercury.rdb.util.JDBCConnectorUtil;
import com.bamboo.mercury.rdb.util.RdbUtil;
import com.bamboo.mercury.util.JsonUtil;
import com.bamboo.mercury.util.ReflectUtil;
import com.bamboo.mercury.api.CellsRecord;
import com.bamboo.mercury.element.schema.RWDataSetSchema;
import com.bamboo.mercury.api.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

public class CommonRdbReader implements Reader<CellsRecord> {

  private static final String PREFIX_KEY = "commonrdbreader";

  private RWDataSetSchema schema;

  private Connection conn;

  private PreparedStatement selectStmt;

  private ResultSet rs;

  private CellsRecord record;

  @Override
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    String readerSchemaFileName = conf.getString("schema.filename");
    schema = JsonUtil.readEntityFromFile(readerSchemaFileName, RWDataSetSchema.class);
    conn = JDBCConnectorUtil.getConnection(conf);

    String selectSql = conf.getString("selectsql");
    selectStmt = conn.prepareStatement(selectSql);
    rs = selectStmt.executeQuery();

    String recordClassName = conf.getString("record.class.name");
    if (StringUtils.isNotEmpty(recordClassName)) {
      record = ReflectUtil.newInstance(recordClassName);
    } else {
      record = new DefaultCellsRecord();
    }
  }


  @Override
  public CellsRecord read() throws Exception {
    if (rs.next()) {
      ResultSetMetaData metaData = rs.getMetaData();
      int columnSize = metaData.getColumnCount();
      record = record.newRecord(metaData.getColumnCount());

      for (int i = 0; i < columnSize; i++) {
        // resultset index start from 1
        Cell cell = RdbUtil.createCell(schema.getFields()[i], rs, i + 1);
        record.addCell(i, cell);
      }
      return record;

    }
    return null;
  }

  @Override
  public void close() throws Exception {
    if (rs != null) {
      rs.close();
    }

    if (selectStmt != null) {
      selectStmt.close();
    }

    if (conn != null) {
      conn.close();
    }
  }
}
