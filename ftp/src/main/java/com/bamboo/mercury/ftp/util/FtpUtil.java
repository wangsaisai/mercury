package com.bamboo.mercury.ftp.util;

import com.bamboo.mercury.ftp.FtpConstant;
import com.bamboo.mercury.ftp.exception.FtpMercuryRuntimeException;
import org.apache.commons.configuration.Configuration;

public class FtpUtil {

  public static FtpHelper initFtpHelper(Configuration conf) {
    FtpHelper ftpHelper;

    String protocol = conf.getString(FtpConstant.PROTOCOL_KEY);
    int port;
    if ("ftp".equalsIgnoreCase(protocol)) {
      ftpHelper = new StandardFtpHelper();
      port = conf.getInt(FtpConstant.PORT_KEY, FtpConstant.DEFAULT_FTP_PORT);
    } else if ("sftp".equalsIgnoreCase(protocol)) {
      ftpHelper = new SftpHelper();
      port = conf.getInt(FtpConstant.PORT_KEY, FtpConstant.DEFAULT_SFTP_PORT);
    } else {
      throw new FtpMercuryRuntimeException("unSupport protocol : " + protocol);
    }
    String host = conf.getString(FtpConstant.HOST_KEY);
    String username = conf.getString(FtpConstant.USERNAME_KEY);
    String password = conf.getString(FtpConstant.PASSWORD_KEY);
    int timeout = conf.getInt(FtpConstant.TIMEOUT_KEY, FtpConstant.DEFAULT_TIMEOUT);
    ftpHelper.loginFtpServer(host, username, password, port, timeout);

    return ftpHelper;
  }

}
