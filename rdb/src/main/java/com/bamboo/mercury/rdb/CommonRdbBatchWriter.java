package com.bamboo.mercury.rdb;

import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.exception.MercuryRuntimeException;
import com.bamboo.mercury.api.CellsRecord;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.Configuration;

public class CommonRdbBatchWriter extends CommonRdbWriter {

  private static final String PREFIX_KEY = "rdbbatchwriter";

  private int batchSize;

  private List<CellsRecord> cache;

  @Override
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    super.init(conf);
    this.batchSize = conf.getInt("batch.size", 1);
    this.cache = new ArrayList<>(batchSize);
  }

  @Override
  public void write(CellsRecord record) throws Exception {
    validateRecord(record);
    cache.add(record);

    if (cache.size() >= batchSize) {
      doBatchWrite();
    }
  }

  private void doBatchWrite() throws SQLException {
    cache.forEach(record -> {
      try {
        setupStmt(record);
        insertStmt.addBatch();
      } catch (SQLException e) {
        throw new MercuryRuntimeException("batch writer error", e);
      }
    });
    insertStmt.executeBatch();

    cache.clear();
  }

  @Override
  public void close() throws Exception {
    doBatchWrite();
    super.close();
  }

}
