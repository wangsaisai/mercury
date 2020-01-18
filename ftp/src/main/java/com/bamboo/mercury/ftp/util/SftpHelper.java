package com.bamboo.mercury.ftp.util;

import com.bamboo.mercury.ftp.exception.FtpMercuryRuntimeException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SftpHelper implements FtpHelper {

  private static final Logger LOG = LoggerFactory.getLogger(SftpHelper.class);

  private Session session = null;
  private ChannelSftp channelSftp = null;

  @Override
  public void loginFtpServer(String host, String username, String password, int port, int timeout) {
    JSch jsch = new JSch(); // 创建JSch对象
    try {
      session = jsch.getSession(username, host, port);
      if (session == null) {
        throw new FtpMercuryRuntimeException(
            "session is null, cannot connect to sftp server, please check host and username");
      }

      session.setPassword(password);
      Properties config = new Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
      session.setTimeout(timeout);
      session.connect();

      channelSftp = (ChannelSftp) session.openChannel("sftp");
      channelSftp.connect();
    } catch (JSchException e) {
      LOG.error("login to sftp server error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public void logoutFtpServer() {
    if (channelSftp != null) {
      channelSftp.disconnect();
      channelSftp = null;
    }
    if (session != null) {
      session.disconnect();
      session = null;
    }
  }

  @Override
  public boolean isDirExist(String directoryPath) {
    try {
      SftpATTRS sftpATTRS = channelSftp.lstat(directoryPath);
      return sftpATTRS.isDir();
    } catch (SftpException e) {
      LOG.error("check dir exist error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public boolean isFileExist(String filePath) {
    try {
      SftpATTRS sftpATTRS = channelSftp.lstat(filePath);
      return sftpATTRS.getSize() >= 0;
    } catch (SftpException e) {
      LOG.error("check file exist error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public boolean isSymbolicLink(String filePath) {
    try {
      SftpATTRS sftpATTRS = channelSftp.lstat(filePath);
      return sftpATTRS.isLink();
    } catch (SftpException e) {
      LOG.error("check symbolic link error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public InputStream getInputStream(String filePath) {
    try {
      return channelSftp.get(filePath);
    } catch (SftpException e) {
      LOG.error("get input stream error", e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public void mkdir(String directoryPath) {
    boolean isDirExist = false;
    try {
      this.printWorkingDirectory();
      SftpATTRS sftpATTRS = this.channelSftp.lstat(directoryPath);
      isDirExist = sftpATTRS.isDir();
    } catch (SftpException e) {
      if (e.getMessage().toLowerCase().equals("no such file")) {
        LOG.warn(String.format(
            "path:[%s] not exist，will try to create it. patch errorMessage:%s",
            directoryPath, e.getMessage()), e);
        isDirExist = false;
      }
    }
    if (!isDirExist) {
      try {
        this.channelSftp.mkdir(directoryPath);
      } catch (SftpException e) {
        LOG.error("mkdir error", e);
        throw new FtpMercuryRuntimeException(e);
      }
    }
  }

  @Override
  public void mkDirRecursive(String directoryPath) {
    boolean isDirExist = false;
    try {
      this.printWorkingDirectory();
      SftpATTRS sftpATTRS = this.channelSftp.lstat(directoryPath);
      isDirExist = sftpATTRS.isDir();
    } catch (SftpException e) {
      if (e.getMessage().toLowerCase().equals("no such file")) {
        LOG.warn(String.format(
            "path:[%s] not exist，will try to create it. patch errorMessage:%s",
            directoryPath, e.getMessage()), e);
        isDirExist = false;
      }
    }
    if (!isDirExist) {
      StringBuilder dirPath = new StringBuilder();
      dirPath.append(IOUtils.DIR_SEPARATOR_UNIX);
      String[] dirSplit = StringUtils.split(directoryPath, IOUtils.DIR_SEPARATOR_UNIX);
      try {
        // ftp server does not support mkdir -p, create dir one by one
        for (String dirName : dirSplit) {
          dirPath.append(dirName);
          mkDirSingleHierarchy(dirPath.toString());
          dirPath.append(IOUtils.DIR_SEPARATOR_UNIX);
        }
      } catch (SftpException e) {
        LOG.error("mkdir recursive error", e);
        throw new FtpMercuryRuntimeException(e);
      }
    }
  }

  private void mkDirSingleHierarchy(String directoryPath) throws SftpException {
    boolean isDirExist;
    try {
      SftpATTRS sftpATTRS = this.channelSftp.lstat(directoryPath);
      isDirExist = sftpATTRS.isDir();
    } catch (SftpException e) {
      LOG.info(String.format("create dir [%s] one by one", directoryPath));
      this.channelSftp.mkdir(directoryPath);
      return;
    }
    if (!isDirExist) {
      LOG.info(String.format("create dir [%s] one by one", directoryPath));
      this.channelSftp.mkdir(directoryPath);
    }
  }

  @Override
  public OutputStream getOutputStream(String filePath) {
    try {
      this.printWorkingDirectory();
      String parentDir = filePath.substring(0,
          StringUtils.lastIndexOf(filePath, IOUtils.DIR_SEPARATOR));
      this.channelSftp.cd(parentDir);
      this.printWorkingDirectory();
      OutputStream writeOutputStream = this.channelSftp.put(filePath,
          ChannelSftp.APPEND);
      if (null == writeOutputStream) {
        throw new FtpMercuryRuntimeException("get OutPutStream error");
      }
      return writeOutputStream;
    } catch (SftpException e) {
      LOG.error("get OutPutStream error", e);
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
      this.channelSftp.cd(parentDir);
      this.printWorkingDirectory();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(22);
      this.channelSftp.get(filePath, outputStream);
      String result = outputStream.toString();
      IOUtils.closeQuietly(outputStream);
      return result;
    } catch (SftpException e) {
      LOG.error("get remote file content error");
      throw new FtpMercuryRuntimeException(e);
    }
  }

  @Override
  public void deleteFiles(Set<String> filesToDelete) {
    String eachFile = null;
    try {
      this.printWorkingDirectory();
      for (String each : filesToDelete) {
        LOG.info(String.format("delete file [%s].", each));
        eachFile = each;
        this.channelSftp.rm(each);
      }
    } catch (SftpException e) {
      LOG.error(String.format("delete file:%s error", eachFile), e);
      throw new FtpMercuryRuntimeException(e);
    }
  }

  private void printWorkingDirectory() {
    try {
      LOG.info(String.format("current working directory:%s",
          this.channelSftp.pwd()));
    } catch (Exception e) {
      LOG.warn(String.format("printWorkingDirectory error:%s",
          e.getMessage()));
    }
  }

  @Override
  public void completePendingCommand() {
  }

}
