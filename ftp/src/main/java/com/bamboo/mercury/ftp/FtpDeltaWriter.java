package com.bamboo.mercury.ftp;

import com.bamboo.mercury.text.TextlineDeltaWriter;
import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.ftp.util.FtpHelper;
import com.bamboo.mercury.ftp.util.FtpUtil;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.commons.configuration.Configuration;

public class FtpDeltaWriter extends TextlineDeltaWriter {

  private static final String PREFIX_KEY = "ftpdeltawriter";

  private FtpHelper ftpHelper;

  @Override
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    initSchemaAndRecord(conf);

    ftpHelper = FtpUtil.initFtpHelper(conf);

    initWriter(conf);
  }

  @Override
  protected void initWriter(Configuration conf) throws IOException {
    String filename = conf.getString("filename");
    writer = new BufferedWriter(new OutputStreamWriter(ftpHelper.getOutputStream(filename)));
  }

  @Override
  public void close() throws Exception {
    super.close();

    if (ftpHelper != null) {
      ftpHelper.logoutFtpServer();
    }
  }

}
