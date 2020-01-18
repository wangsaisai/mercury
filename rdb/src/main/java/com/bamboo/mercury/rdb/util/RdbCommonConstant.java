package com.bamboo.mercury.rdb.util;


public class RdbCommonConstant {

  /**
   * Connector related configuration.
   */
  // common
  public final static String CONNECTOR_DATA_SOURCE_TYPE = "connector.data.source.type";
  // jdbc
  public final static String CONNECTOR_JDBC_HOST_KEY = "connector.jdbc.host";
  public final static String CONNECTOR_JDBC_PORT_KEY = "connector.jdbc.port";
  public final static String CONNECTOR_JDBC_DATABASE_KEY = "connector.jdbc.database";
  public final static String CONNECTOR_JDBC_USER_KEY = "connector.jdbc.user";
  public final static String CONNECTOR_JDBC_PSWD_KEY = "connector.jdbc.password";
  // mysql
  public final static String MYSQL_JDBC_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
  public final static String MYSQL_JDBC_URL_TEMPLATE = "jdbc:mysql://%s:%s/%s?user=%s&password=%s&useSSL=false";
  // postgres
  public final static String POSTGRESQL_JDBC_DRIVER_CLASS_NAME = "org.postgresql.Driver";
  public final static String POSTGRESQL_JDBC_URL_TEMPLATE = "jdbc:postgresql://%s:%s/%s?user=%s&password=%s";
  // oracle
  public final static String ORACLE_JDBC_DRIVER_CLASS_NAME = "oracle.jdbc.OracleDriver";
  public final static String ORACLE_JDBC_URL_TEMPLATE = "jdbc:oracle:thin:%s/%s@%s:%s:%s";
  // teradata
  public final static String TERADATA_JDBC_DRIVER_CLASS_NAME = "com.teradata.jdbc.TeraDriver";
  public final static String TERADATA_JDBC_URL_TEMPLATE = "jdbc:teradata://%s/TMODE=TERA,CHARSET=UTF8,USER=%s,PASSWORD=%s";
  // hive
  public final static String HIVE_JDBC_DRIVER_CLASS_NAME = "org.apache.hive.jdbc.HiveDriver";
  public final static String HIVE_JDBC_URL_TEMPLATE = "jdbc:hive2://%s:%s";
  public final static String HIVE_JDBC_PRINCIPAL_URL_TEMPLATE = "jdbc:hive2://%s:%s/;principal=%s";
  // kerberos
  public final static String KERBEROS_KEY = "Kerberos";
  public final static String HDP_SECURITY_AUTH_KEY = "hadoop.security.authentication";
  public final static String CONNECTOR_JDBC_KEYTAB_KEY = "connector.jdbc.keytab";
  public final static String CONNECTOR_JDBC_PRINCIPAL_KEY = "connector.jdbc.principal";
  private RdbCommonConstant() {
  }
}
