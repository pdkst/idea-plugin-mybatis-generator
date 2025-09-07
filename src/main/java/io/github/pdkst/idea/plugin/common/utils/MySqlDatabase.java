package io.github.pdkst.idea.plugin.common.utils;

import io.github.pdkst.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import lombok.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

@Data
public class MySqlDatabase implements Database {
    /**
     * 数据库信息
     */
    private final DatabaseSensitiveProperties databaseWithPwd;

    private String getVersion() throws SQLException {
        return execute(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT VERSION() AS MYSQL_VERSION");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("MYSQL_VERSION");
            }
            return null;
        });
    }

    @Override
    public boolean testConnection() {
        try {
            String version = getVersion();
            return version != null;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public <T> T execute(SQLConnectionTask<T> task) throws SQLException {
        try (Connection connection = getConnection()) {
            return task.execute(connection);
        }
    }

    private Connection getConnection() throws SQLException {
        Properties properties = getMySqlConnectionProperties(databaseWithPwd);
        return DriverManager.getConnection(databaseWithPwd.getUrl(), properties);
    }


    private static Properties getMySqlConnectionProperties(DatabaseSensitiveProperties databaseWithPwd) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        Properties properties = new Properties();
        properties.put("user", databaseWithPwd.getUserName());
        properties.put("password", databaseWithPwd.getPassword());
        // 返回注释
        properties.putIfAbsent("remarks", "true");
        // 将元数据返回给调用者，INFORMATION_SCHEMA 是 MySQL 中的一个特殊数据库，用于存储关于数据库和表的元数据信息，
        // 例如表的清单，列的清单等。通过在连接字符串中添加 useInformationSchema=true 参数，
        // 可以告诉 JDBC 驱动程序在返回 ResultSet 元数据时使用 INFORMATION_SCHEMA。
        properties.putIfAbsent("useInformationSchema", "true");
        properties.putIfAbsent("connectTimeout", "3000"); // 3秒超时时间

        properties.putIfAbsent("useUnicode", "true");
        properties.putIfAbsent("characterEncoding", "UTF-8");
        properties.putIfAbsent("zeroDateTimeBehavior", "convertToNull");
        properties.putIfAbsent("useSSL", "false");
        return properties;
    }
}
