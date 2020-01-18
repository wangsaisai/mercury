package com.bamboo.mercury.ftp;

import com.bamboo.mercury.conf.ApplicationProperties;
import com.bamboo.mercury.ftp.util.FtpHelper;
import com.bamboo.mercury.ftp.util.FtpUtil;
import com.bamboo.mercury.text.TextlineWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.commons.configuration.Configuration;

public class FtpWriter extends TextlineWriter {

  private static final String PREFIX_KEY = "ftpwriter";

  private FtpHelper ftpHelper;

  @Override
  public void init() throws Exception {
    Configuration conf = ApplicationProperties.getPrefixConf(PREFIX_KEY);
    initSchemaAndFieldSplit(conf);

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
