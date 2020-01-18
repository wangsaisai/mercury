package com.bamboo.mercury.conf;

import static org.testng.Assert.assertEquals;

import org.apache.commons.configuration.Configuration;
import org.testng.annotations.Test;

public class ApplicationPropertiesTest {

  @Test
  public void testConf() {
    Configuration conf = ApplicationProperties.get();
    assertEquals("value1", conf.getString("key1"));

    Configuration prefixConf = ApplicationProperties.getSubsetConfiguration(conf, "prefix");
    assertEquals("valuex", prefixConf.getString("keyx"));
  }
}
