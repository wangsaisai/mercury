package com.bamboo.mercury.ftp.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public interface FtpHelper {

  void loginFtpServer(String host, String username, String password, int port,
      int timeout);

  void logoutFtpServer();

  boolean isDirExist(String directoryPath);

  boolean isFileExist(String filePath);

  boolean isSymbolicLink(String filePath);

  InputStream getInputStream(String filePath);

  void mkdir(String directoryPath);

  /**
   * mkdir -p
   */
  void mkDirRecursive(String directoryPath);

  OutputStream getOutputStream(String filePath);

  /**
   * unSupport delete directory
   */
  void deleteFiles(Set<String> filesToDelete);

  void completePendingCommand();

  String getRemoteFileContent(String filePath);

}
