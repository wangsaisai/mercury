package com.bamboo.mercury.rdb.util;

import com.bamboo.mercury.rdb.exception.RdbMercuryRuntimeException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.security.UserGroupInformation;

public class JDBCConnectorUtil {

  private static void kerberosLogin(String principal, String keytab) {
    org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
    conf.set(RdbCommonConstant.HDP_SECURITY_AUTH_KEY, RdbCommonConstant.KERBEROS_KEY);
    UserGroupInformation.setConfiguration(conf);
    try {
      UserGroupInformation.loginUserFromKeytab(principal, keytab);
    } catch (Exception e) {
      throw new RdbMercuryRuntimeException("kerberos login fail", e);
    }
  }

  public static Connection getConnection(Configuration conf) {
    String driverClassName;
    String url;

    String user = conf.getString(RdbCommonConstant.CONNECTOR_JDBC_USER_KEY);
    String pswd = conf.getString(RdbCommonConstant.CONNECTOR_JDBC_PSWD_KEY);

    String type = conf.getString(RdbCommonConstant.CONNECTOR_DATA_SOURCE_TYPE);
    DataSourceType dataSourceType = DataSourceType.getEnum(type);

    String principal = conf.getString(RdbCommonConstant.CONNECTOR_JDBC_PRINCIPAL_KEY);
    String keytab = conf.getString(RdbCommonConstant.CONNECTOR_JDBC_KEYTAB_KEY);

    switch (dataSourceType) {
      case MYSQL:
        driverClassName = RdbCommonConstant.MYSQL_JDBC_DRIVER_CLASS_NAME;

        url = String.format(RdbCommonConstant.MYSQL_JDBC_URL_TEMPLATE,
            conf.getString(RdbCommonConstant.CONNECTOR_JDBC_HOST_KEY),
            conf.getString(RdbCommonConstant.CONNECTOR_JDBC_PORT_KEY),
            conf.getString(RdbCommonConstant.CONNECTOR_JDBC_DATABASE_KEY),
            user,
            pswd);
        break;
      case POSTGRESQL:
        driverClassName = RdbCommonConstant.POSTGRESQL_JDBC_DRIVER_CLASS_NAME;

        url = String.format(RdbCommonConstant.POSTGRESQL_JDBC_URL_TEMPLATE,
            conf.getString(RdbCommonConstant.CONNECTOR_JDBC_HOST_KEY),
            conf.getString(RdbCommonConstant.CONNECTOR_JDBC_PORT_KEY),
            conf.getString(RdbCommonConstant.CONNECTOR_JDBC_DATABASE_KEY),
            user,
            pswd);
        break;
      case ORACLE:
        driverClassName = RdbCommonConstant.ORACLE_JDBC_DRIVER_CLASS_NAME;

        url = String.format(user, pswd,
            conf.getString(RdbCommonConstant.CONNECTOR_JDBC_HOST_KEY),
            conf.getString(RdbCommonConstant.CONNECTOR_JDBC_PORT_KEY),
            conf.getString(RdbCommonConstant.CONNECTOR_JDBC_DATABASE_KEY));
        break;
      case TERADATA:
        driverClassName = RdbCommonConstant.TERADATA_JDBC_DRIVER_CLASS_NAME;

        url = String.format(RdbCommonConstant.TERADATA_JDBC_URL_TEMPLATE,
            conf.getString(RdbCommonConstant.CONNECTOR_JDBC_HOST_KEY),
            user,
            pswd);
        break;
      case HIVE:
        driverClassName = RdbCommonConstant.HIVE_JDBC_DRIVER_CLASS_NAME;

        if (StringUtils.isEmpty(principal) || StringUtils.isEmpty(keytab)) {
          url = String.format(RdbCommonConstant.HIVE_JDBC_URL_TEMPLATE,
              conf.getString(RdbCommonConstant.CONNECTOR_JDBC_HOST_KEY),
              conf.getString(RdbCommonConstant.CONNECTOR_JDBC_PORT_KEY));
        } else {
          url = String.format(RdbCommonConstant.HIVE_JDBC_PRINCIPAL_URL_TEMPLATE,
              conf.getString(RdbCommonConstant.CONNECTOR_JDBC_HOST_KEY),
              conf.getString(RdbCommonConstant.CONNECTOR_JDBC_PORT_KEY),
              principal);
          kerberosLogin(principal, keytab);
        }

        break;
      default:
        throw new RdbMercuryRuntimeException(
            dataSourceType + " doesn't support JDBC connector yet!");
    }

    Connection connection;
    try {
      Class.forName(driverClassName);
      connection = DriverManager.getConnection(url);
    } catch (ClassNotFoundException | SQLException e) {
      throw new RdbMercuryRuntimeException(e);
    }

    return connection;
  }

}
