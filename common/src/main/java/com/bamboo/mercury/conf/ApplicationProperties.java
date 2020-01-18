package com.bamboo.mercury.conf;

import com.bamboo.mercury.exception.MercuryRuntimeException;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application properties used by Mercury.
 */
public final class ApplicationProperties extends PropertiesConfiguration {

  private static final String MERCURY_CONFIGURATION_FILE_PROPERTY = "mercury.conf";
  private static final String APPLICATION_PROPERTIES = "mercury-application.properties";
  private static final Logger LOG = LoggerFactory.getLogger(ApplicationProperties.class);
  private static volatile Configuration instance = null;

  private ApplicationProperties(URL url) throws ConfigurationException {
    super(url);
  }

  public static void forceReload() {
    if (instance != null) {
      synchronized (ApplicationProperties.class) {
        if (instance != null) {
          instance = null;
        }
      }
    }
  }

  public static Configuration get() {
    if (instance == null) {
      synchronized (ApplicationProperties.class) {
        if (instance == null) {
          instance = get(APPLICATION_PROPERTIES);
        }
      }
    }
    return instance;
  }

  public static Configuration getPrefixConf(String prefix) {
    return getSubsetConfiguration(get(), prefix);
  }

  public static Configuration get(String fileName) {
    String confLocation = System.getProperty(MERCURY_CONFIGURATION_FILE_PROPERTY);
    try {
      URL url = null;

      if (confLocation == null) {
        LOG.info("Looking for {} in classpath", fileName);

        url = ApplicationProperties.class.getClassLoader().getResource(fileName);

        if (url == null) {
          LOG.info("Looking for /{} in classpath", fileName);

          url = ApplicationProperties.class.getClassLoader().getResource("/" + fileName);
        }
      } else {
        url = new File(confLocation, fileName).toURI().toURL();
      }

      LOG.info("Loading {} from {}", fileName, url);

      ApplicationProperties appProperties = new ApplicationProperties(url);

      Configuration configuration = appProperties.interpolatedConfiguration();

      logConfiguration(configuration);
      return configuration;
    } catch (Exception e) {
      throw new MercuryRuntimeException("Failed to load mercury application properties", e);
    }
  }

  private static void logConfiguration(Configuration configuration) {
    Iterator<String> keys = configuration.getKeys();
    LOG.info("Configuration loaded:");
    while (keys.hasNext()) {
      String key = keys.next();
      LOG.info("{} = {}", key, configuration.getProperty(key));
    }
  }

  public static Configuration getSubsetConfiguration(Configuration inConf, String prefix) {
    return inConf.subset(prefix);
  }

}
