package io.github.pdkst.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.pojo.DatabaseWithPwd;
import com.caojx.idea.plugin.common.pojo.TableField;
import com.caojx.idea.plugin.common.pojo.TableInfo;
import lombok.Data;

import java.sql.*;
import java.util.*;

import static java.util.Collections.singletonList;

@Data
public class MySqlDatabase implements Database {
    /**
     * 数据库信息
     */
    private final DatabaseWithPwd databaseWithPwd;
    /**
     * 自定义jdbc映射关系
     */
    private final Map<JDBCType, Class<?>> customerJdbcTypeMappingMap;

    public TableInfo getTable(String tableName) throws SQLException {
        List<TableInfo> tableInfos = executeWithConnection(connection -> getTables(connection, singletonList(tableName), true));
        if (tableInfos == null || tableInfos.isEmpty()) {
            return null;
        }
        return tableInfos.get(0);
    }

    @Override
    public List<TableInfo> getTables(String... tableName) throws SQLException {
        return executeWithConnection(connection -> getTables(connection, Arrays.asList(tableName), true));
    }

    @Override
    public List<TableInfo> getTablesWithoutFields(String... tableName) throws SQLException {
        return executeWithConnection(connection -> getTables(connection, Arrays.asList(tableName), false));
    }

    private List<TableInfo> getTables(Connection conn, List<String> tableNames, boolean withFields) throws SQLException {
        List<TableInfo> tableInfoList = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        for (String tableName : tableNames) {
            ResultSet rs = metaData.getTables(conn.getCatalog(), conn.getSchema(), tableName, new String[]{"TABLE"});
            while (rs.next()) {
                // 表注释
                String tableNameResult = rs.getString("TABLE_NAME");
                String remarks = rs.getString("REMARKS");
                TableInfo tableInfo;
                if (withFields) {
                    // 列列表
                    List<TableField> fields = getTableField(conn, tableNameResult);
                    // 返回表信息
                    tableInfo = new TableInfo(tableNameResult, remarks, fields);
                } else {
                    // 列列表
                    tableInfo = new TableInfo(tableNameResult, remarks, new ArrayList<>());
                }
                tableInfoList.add(tableInfo);
            }
        }
        return tableInfoList;
    }

    private List<TableField> getTableField(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();

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
    }

    private <T> T executeWithConnection(SQLConnectionTask<T> task) throws SQLException {
        try (Connection connection = getConnection()) {
            return task.execute(connection);
        }
    }

    private Connection getConnection() throws SQLException {
        Properties properties = getMySqlConnectionProperties(databaseWithPwd);
        return DriverManager.getConnection(databaseWithPwd.getUrl(), properties);
    }


    private static Properties getMySqlConnectionProperties(DatabaseWithPwd databaseWithPwd) {
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
