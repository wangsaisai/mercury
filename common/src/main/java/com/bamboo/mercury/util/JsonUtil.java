package com.bamboo.mercury.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.bamboo.mercury.element.cellschema.ByteSchema;
import com.bamboo.mercury.element.cellschema.CellSchema;
import com.bamboo.mercury.element.cellschema.DateSchema;
import java.io.IOException;

public class JsonUtil {

  private final static RuntimeTypeAdapterFactory<CellSchema> typeFactory = RuntimeTypeAdapterFactory
      .of(CellSchema.class, "type")
      .registerSubtype(DateSchema.class, "DATE")
      .registerSubtype(ByteSchema.class, "BYTES");

  // Gson is thread-safe
  private static Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();

  public static <T> T readEntityFromFile(String fileName, Class<T> classOfT) throws IOException {
    String content = FileUtils.getFileContent(fileName);
    return gson.fromJson(content, classOfT);
  }
}
