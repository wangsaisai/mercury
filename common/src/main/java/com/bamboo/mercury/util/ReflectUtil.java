package com.bamboo.mercury.util;

import com.bamboo.mercury.exception.MercuryRuntimeException;

public class ReflectUtil {

  public static <T> T newInstance(String className) {
    try {
      Class<T> clazz = (Class<T>) Class.forName(className);
      return clazz.newInstance();
    } catch (Exception e) {
      throw new MercuryRuntimeException("Error when create instance for " + className, e);
    }
  }

}
