package com.bamboo.mercury.element;

import com.bamboo.mercury.api.Record;

/**
 * Used to mark the end of the reader
 */
public class TerminateRecord implements Record {

  private final static TerminateRecord SINGLE = new TerminateRecord();

  private TerminateRecord() {
  }

  public static TerminateRecord get() {
    return SINGLE;
  }
}
