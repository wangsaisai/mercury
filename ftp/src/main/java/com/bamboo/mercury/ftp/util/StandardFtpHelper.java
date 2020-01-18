package com.bamboo.mercury.ftp.util;

import com.bamboo.mercury.ftp.exception.FtpMercuryRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardFtpHelper implements FtpHelper {

  private static final Logger LOG = LoggerFactory.getLogger(StandardFtpHelper.class);
  private FTPClient ftpClient = null;

  @Override
  public void loginFtpServer(String host, String username, String password, int port, int timeout) {
    ftpClient = new FTPClient();
    try {
      ftpClient.connect(host, port);
      ftpClient.login(username, password);
      ftpClient.setConnectTimeout(timeout);
      ftpClient.setDataTimeout(timeout);

      // by default, use PASV ftp connect mode
      ftpClient.enterRemotePassiveMode();
      ftpClient.enterLocalPassiveMode();

      int reply = ftpClient.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
        ftpClient.disconnect();
        LOG.error("cannot connect to ftp server, please check host, port, username, password");
        throw new FtpMercuryRuntimeException("ftp server connect error");
      }
      String fileEncoding = System.getProperty("file.encoding");
      ftpClient.setControlEncoding(fileEncoding);
    } catch (Exception e) {
      LOG.error("login to ftp server error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public void logoutFtpServer() {
    if (ftpClient.isConnected()) {
      try {
        ftpClient.logout();
      } catch (Exception e) {
        LOG.warn("logout ftp server error, will ignore it", e);
      } finally {
        if (ftpClient.isConnected()) {
          try {
            ftpClient.disconnect();
          } catch (IOException e) {
            LOG.warn("disconnect ftp server error, will ignore it", e);
          }
        }
      }
    }
  }

  @Override
  public boolean isDirExist(String directoryPath) {
    try {
      return ftpClient.changeWorkingDirectory(
          new String(directoryPath.getBytes(), FTP.DEFAULT_CONTROL_ENCODING));
    } catch (IOException e) {
      LOG.error("check dir exist error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public boolean isFileExist(String filePath) {
    try {
      FTPFile[] ftpFiles = ftpClient
          .listFiles(new String(filePath.getBytes(), FTP.DEFAULT_CONTROL_ENCODING));
      return ftpFiles.length == 1 && ftpFiles[0].isFile();
    } catch (IOException e) {
      LOG.error("check file exist error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public boolean isSymbolicLink(String filePath) {
    try {
      FTPFile[] ftpFiles = ftpClient
          .listFiles(new String(filePath.getBytes(), FTP.DEFAULT_CONTROL_ENCODING));
      return ftpFiles.length == 1 && ftpFiles[0].isSymbolicLink();
    } catch (IOException e) {
      LOG.error("check symbolic link error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public InputStream getInputStream(String filePath) {
    try {
      return ftpClient
          .retrieveFileStream(new String(filePath.getBytes(), FTP.DEFAULT_CONTROL_ENCODING));
    } catch (IOException e) {
      LOG.error("get input stream error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public void mkdir(String directoryPath) {
    try {
      this.printWorkingDirectory();
      boolean isDirExist = this.ftpClient
          .changeWorkingDirectory(directoryPath);
      if (!isDirExist) {
        int replayCode = this.ftpClient.mkd(directoryPath);
        if (replayCode != FTPReply.COMMAND_OK
            && replayCode != FTPReply.PATHNAME_CREATED) {
          String errorMsg = String
              .format("create path:%s error, replayCode:%s", directoryPath, replayCode);
          LOG.error(errorMsg);
          throw new FtpMercuryRuntimeException(errorMsg);
        }
      }
    } catch (IOException e) {
      LOG.error("mkdir error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public void mkDirRecursive(String directoryPath) {
    StringBuilder dirPath = new StringBuilder();
    dirPath.append(IOUtils.DIR_SEPARATOR_UNIX);
    String[] dirSplit = StringUtils.split(directoryPath, IOUtils.DIR_SEPARATOR_UNIX);
    try {
      // ftp server does not support mkdir -p
      for (String dirName : dirSplit) {
        dirPath.append(dirName);
        boolean mkdirSuccess = mkDirSingleHierarchy(dirPath.toString());
        dirPath.append(IOUtils.DIR_SEPARATOR_UNIX);
        if (!mkdirSuccess) {
          throw new FtpMercuryRuntimeException("mkdir recursive error");
        }
      }
    } catch (IOException e) {
      LOG.error("mkdir recursive error");
      throw new FtpMercuryRuntimeException(e);
    }
  }

  private boolean mkDirSingleHierarchy(String directoryPath) throws IOException {
    boolean isDirExist = this.ftpClient
        .changeWorkingDirectory(directoryPath);
    if (!isDirExist) {
      int replayCode = this.ftpClient.mkd(directoryPath);
      return replayCode == FTPReply.COMMAND_OK
          || replayCode == FTPReply.PATHNAME_CREATED;
    }
    return true;
  }

  @Override
  public OutputStream getOutputStream(String filePath) {
    try {
      this.printWorkingDirectory();
      String parentDir = filePath.substring(0,
          StringUtils.lastIndexOf(filePath, IOUtils.DIR_SEPARATOR));
      this.ftpClient.changeWorkingDirectory(parentDir);
      this.printWorkingDirectory();
      OutputStream writeOutputStream = this.ftpClient
          .appendFileStream(filePath);
      if (null == writeOutputStream) {
        throw new FtpMercuryRuntimeException("get output stream error");
      }

      return writeOutputStream;
    } catch (IOException e) {
      LOG.error("get output stream error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public void deleteFiles(Set<String> filesToDelete) {
    String eachFile;
    boolean deleteOk;
    try {
      this.printWorkingDirectory();
      for (String each : filesToDelete) {
        LOG.info(String.format("delete file [%s].", each));
        eachFile = each;
        deleteOk = this.ftpClient.deleteFile(each);
        if (!deleteOk) {
          throw new FtpMercuryRuntimeException(String.format("delete file : %s error", eachFile));
        }
      }
    } catch (IOException e) {
      LOG.error("delete file : %s error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  private void printWorkingDirectory() {
    try {
      LOG.info(String.format("current working directory:%s",
          this.ftpClient.printWorkingDirectory()));
    } catch (Exception e) {
      LOG.warn(String.format("printWorkingDirectory error:%s",
          e.getMessage()));
    }
  }

  @Override
  public void completePendingCommand() {
    /*
     * Q:After I perform a file transfer to the server,
     * printWorkingDirectory() returns null. A:You need to call
     * completePendingCommand() after transferring the file. wiki:
     * http://wiki.apache.org/commons/Net/FrequentlyAskedQuestions
     */
    try {
      boolean isOk = this.ftpClient.completePendingCommand();
      if (!isOk) {
        throw new FtpMercuryRuntimeException("completePendingCommand error");
      }
    } catch (IOException e) {
      LOG.error("completePendingCommand error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public String getRemoteFileContent(String filePath) {
    try {
      this.completePendingCommand();
      this.printWorkingDirectory();
      String parentDir = filePath.substring(0,
          StringUtils.lastIndexOf(filePath, IOUtils.DIR_SEPARATOR));
      this.ftpClient.changeWorkingDirectory(parentDir);
      this.printWorkingDirectory();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(22);
      this.ftpClient.retrieveFile(filePath, outputStream);
      String result = outputStream.toString();
      IOUtils.closeQuietly(outputStream);
      return result;
    } catch (IOException e) {
      LOG.error("get remote file content error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

}
