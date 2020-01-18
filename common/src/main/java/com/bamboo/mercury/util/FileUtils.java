package com.bamboo.mercury.util;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

  private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

  public static InputStream loadFileAsInputStream(String fileName) throws IOException {
    if (fileName == null) {
      throw new IllegalArgumentException("fileName is null, unable to load file");
    }

    File fileToLoad = new File(fileName);
    InputStream is;

    if (fileToLoad.exists()) {
      LOG.info("Looking for {} in file system", fileName);
      is = new FileInputStream(fileToLoad);
    } else {
      LOG.info("Looking for {} in classpath", fileName);

      is = FileUtils.class.getClassLoader().getResourceAsStream(fileName);
      if (is == null && !fileName.startsWith("/")) {
        LOG.info("Looking for /{} in classpath", fileName);
        is = FileUtils.class.getClassLoader().getResourceAsStream("/" + fileName);
      }

      if (is == null) {
        throw new IOException("Error loading file " + fileName);
      } else {
        LOG.info("Loading {} success!", fileName);
      }
    }

    return is;
  }

  public static String getFileContent(String filename) throws IOException {
    Reader reader = new InputStreamReader(loadFileAsInputStream(filename));
    CharArrayWriter writer = new CharArrayWriter();
    char[] buffer = new char[1024];
    int count;

    try {
      while ((count = reader.read(buffer)) != -1) {
        writer.write(buffer, 0, count);
      }
      return String.valueOf(writer.toCharArray());
    } finally {
      try {
        reader.close();
        writer.close();
      } catch (Exception ignored) {
      }
    }
  }
}
