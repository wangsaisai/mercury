package com.bamboo.mercury.rdb.util;

public enum DataSourceType {
  MYSQL("MySQL"),
  POSTGRESQL("PostgreSQL"),
  CASSANDRA("Cassandra"),
  ORACLE("Oracle"),
  HIVE("Hive"),
  TERADATA("Teradata");

  String typeName;

  DataSourceType(String typeName) {
    this.typeName = typeName;
  }

  public static DataSourceType getEnum(String typeName) {
    if (typeName != null) {
      typeName = typeName.trim().toLowerCase();
      for (DataSourceType dataSourceType : DataSourceType.values()) {
        if (dataSourceType.typeName.toLowerCase().equals(typeName)) {
          return dataSourceType;
        }
      }
    }

    throw new IllegalArgumentException("Illegal type name");
  }

  public String getTypeName() {
    return typeName;
  }
}
