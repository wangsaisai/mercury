package com.bamboo.mercury.ftp;

import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.ftp.util.FtpHelper;
import com.bamboo.mercury.ftp.util.FtpUtil;
import com.bamboo.mercury.text.TextlineDeltaReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.configuration.Configuration;

public class FtpDeltaReader extends TextlineDeltaReader {

  private static final String PREFIX_KEY = "ftpdeltareader";

  private FtpHelper ftpHelper;

  @Override
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    initSchemaAndRecord(conf);

    ftpHelper = FtpUtil.initFtpHelper(conf);

    initReader(conf);
  }

  @Override
  protected void initReader(Configuration conf) throws IOException {
    String filename = conf.getString("filename");
    reader = new BufferedReader(new InputStreamReader(ftpHelper.getInputStream(filename)));
  }

  @Override
  public void close() throws Exception {
    super.close();

    if (ftpHelper != null) {
      ftpHelper.logoutFtpServer();
    }
  }


}
