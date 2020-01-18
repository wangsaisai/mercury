package com.bamboo.mercury.rdb;

import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.element.cell.Cell;
import com.bamboo.mercury.rdb.util.JDBCConnectorUtil;
import com.bamboo.mercury.rdb.util.RdbUtil;
import com.bamboo.mercury.util.JsonUtil;
import com.bamboo.mercury.util.ReflectUtil;
import com.bamboo.mercury.api.CellsDeltaRecord;
import com.bamboo.mercury.api.CellsRecord;
import com.bamboo.mercury.element.record.DefaultCellsDeltaRecord;
import com.bamboo.mercury.api.DeltaType;
import com.bamboo.mercury.element.schema.RWDataSetSchema;
import com.bamboo.mercury.api.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

public class CommonRdbDeltaReader implements Reader<CellsRecord> {

  private static final String PREFIX_KEY = "commonrdbdeltareader";

  private RWDataSetSchema schema;

  private Connection conn;

  private PreparedStatement selectStmt;

  private ResultSet rs;

  private CellsDeltaRecord<?> record;

  private DeltaType curRecordType = DeltaType.ADD;

  private String addSelectSql;
  private String updateSelectSql;
  private String deleteSelectSql;


  @Override
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    String readerSchemaFileName = conf.getString("schema.filename");
    schema = JsonUtil.readEntityFromFile(readerSchemaFileName, RWDataSetSchema.class);
    conn = JDBCConnectorUtil.getConnection(conf);

    addSelectSql = conf.getString("selectsql.add");
    updateSelectSql = conf.getString("selectsql.update");
    deleteSelectSql = conf.getString("selectsql.delete");

    selectStmt = conn.prepareStatement(addSelectSql);
    rs = selectStmt.executeQuery();

    String recordClassName = conf.getString("record.class.name");
    if (StringUtils.isNotEmpty(recordClassName)) {
      record = ReflectUtil.newInstance(recordClassName);
    } else {
      record = new DefaultCellsDeltaRecord();
    }
  }

  private boolean nextRs() throws SQLException {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException ignored) {
      }
    }
    if (selectStmt != null) {
      try {
        selectStmt.close();
      } catch (SQLException ignored) {
      }
    }

    if (curRecordType == DeltaType.ADD) {
      curRecordType = DeltaType.UPDATE;
      selectStmt = conn.prepareStatement(updateSelectSql);
      rs = selectStmt.executeQuery();
    } else if (curRecordType == DeltaType.UPDATE) {
      curRecordType = DeltaType.DELETE;
      selectStmt = conn.prepareStatement(deleteSelectSql);
      rs = selectStmt.executeQuery();
    } else {
      return false;
    }

    return true;
  }

  private CellsDeltaRecord doRead() throws Exception {
    ResultSetMetaData metaData = rs.getMetaData();
    int columnSize = metaData.getColumnCount();
    record = record.newRecord(metaData.getColumnCount());
    record.setDeltaType(curRecordType);

    for (int i = 0; i < columnSize; i++) {
      Cell cell = RdbUtil.createCell(schema.getFields()[i], rs, i + 1);
      record.addCell(i, cell);
    }
    return record;
  }

  @Override
  public CellsRecord read() throws Exception {
    if (rs.next()) {
      return doRead();
    } else if (nextRs()) {
      return read();
    } else {
      return null;
    }
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
