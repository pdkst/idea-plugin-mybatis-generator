package com.caojx.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import com.caojx.idea.plugin.common.pojo.TableField;
import com.caojx.idea.plugin.common.pojo.TableInfo;

import java.sql.*;
import java.util.*;

/**
 * MySQL数据库工具类
 *
 * @author caojx
 * @date 2022/4/10 12:20 PM
 */
public class MySQLDBHelper {

    /**
     * 数据库
     */
    private final DatabaseSensitiveProperties databaseWithPwd;

    /**
     * 数据库连接属性
     */
    private final Properties properties;

    /**
     * 自定义jdbc映射关系
     */
    private Map<JDBCType, Class<?>> customerJdbcTypeMappingMap = new HashMap<>(4);

    /**
     * 构造器
     *
     * @param databaseWithPwd            数据库
     * @param customerJdbcTypeMappingMap jdbc映射关系
     */
    public MySQLDBHelper(DatabaseSensitiveProperties databaseWithPwd, Map<JDBCType, Class<?>> customerJdbcTypeMappingMap) {
        this.databaseWithPwd = databaseWithPwd;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        this.customerJdbcTypeMappingMap = customerJdbcTypeMappingMap;

        properties = new Properties();
        properties.put("user", this.databaseWithPwd.getUserName());
        properties.put("password", this.databaseWithPwd.getPassword());
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
    }

    /**
     * 获取指定数据库连接对象
     *
     * @return 连接对象
     */
    private Connection getConnection() {
        try {
            return DriverManager.getConnection(this.databaseWithPwd.getUrl(), properties);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 关闭连接
     *
     * @param conn 连接对象
     */
    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * 获取表信息
     *
     * @param tableName 表名
     * @return 表信息
     */
    public List<TableInfo> getTableInfos(String... tableName) {
        return getTableInfos(List.of(tableName), true);
    }

    public List<TableInfo> getTableInfosWithoutFields(String... tableName) {
        return getTableInfos(List.of(tableName), false);
    }

    /**
     * 获取表信息
     *
     * @param tableNameList 表名
     * @return 表信息
     */
    public List<TableInfo> getTableInfos(Collection<String> tableNameList, boolean withFields) {
        Connection conn = getConnection();
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            List<TableInfo> tableInfoList = new ArrayList<>();
            for (String tableName : tableNameList) {
                ResultSet rs = metaData.getTables(conn.getCatalog(), conn.getSchema(), tableName, new String[]{"TABLE"});
                while (rs.next()) {
                    // 表注释
                    String tableNameResult = rs.getString("TABLE_NAME");
                    String remarks = rs.getString("REMARKS");
                    // 列列表
                    List<TableField> fields = new ArrayList<>();
                    if (withFields) {
                        fields = getAllTableField(tableNameResult, conn);
                    }
                    // 返回表信息
                    TableInfo tableInfo = new TableInfo(tableNameResult, remarks, fields);
                    tableInfoList.add(tableInfo);
                }
            }
            return tableInfoList;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * 获取所有的列名
     *
     * @param tableName  表名
     * @param connection 连接
     * @return 列列表
     */
    private List<TableField> getAllTableField(String tableName, Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            // 主键
            String primaryKey = null;
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
            while (primaryKeys.next()) {
                primaryKey = primaryKeys.getString("COLUMN_NAME");
            }

            // 获取表中的所有列名
            ResultSet rs = metaData.getColumns(null, "%", tableName, "%");
            List<TableField> fields = new ArrayList<>();
            while (rs.next()) {
                // 列名
                String columnName = rs.getString("COLUMN_NAME");
                // 字段注释
                String remarks = rs.getString("REMARKS");
                // 字段类型
                int dataType = rs.getInt("DATA_TYPE");

                // 是否为主键
                boolean primaryKeyFlag = Objects.nonNull(primaryKey) && columnName.equals(primaryKey);

                // 构建表属性
                TableField tableField = new TableField(columnName, remarks, dataType, primaryKeyFlag, customerJdbcTypeMappingMap);
                fields.add(tableField);
            }
            return fields;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 测试数据库连接
     *
     * @return 连接结果
     */
    public String testDatabase() {
        Connection conn = getConnection();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT VERSION() AS MYSQL_VERSION");
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getString("MYSQL_VERSION");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return "Err";
    }

}
