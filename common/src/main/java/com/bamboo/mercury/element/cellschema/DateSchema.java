package com.bamboo.mercury.element.cellschema;

import com.google.gson.annotations.Expose;
import java.util.TimeZone;
import org.apache.commons.lang3.time.FastDateFormat;

public class DateSchema extends CellSchema {

  private String pattern;

  private String timeZone;

  @Expose(serialize = false, deserialize = false)
  private FastDateFormat fastDateFormat;

  public DateSchema() {
  }

  public TimeZone getTimeZoneEntity() {
    return fastDateFormat.getTimeZone();
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public FastDateFormat getFastDateFormat() {
    if (fastDateFormat == null) {
      if (pattern == null || timeZone == null) {
        return null;
      }

      fastDateFormat = FastDateFormat.getInstance(pattern, TimeZone.getTimeZone(timeZone));
    }

    return fastDateFormat;
  }

  public void setFastDateFormat(FastDateFormat fastDateFormat) {
    this.fastDateFormat = fastDateFormat;
  }

  @Override
  public String toString() {
    return "DateSchema{" +
        "pattern='" + pattern + '\'' +
        ", timeZone='" + timeZone + '\'' +
        '}';
  }
}
