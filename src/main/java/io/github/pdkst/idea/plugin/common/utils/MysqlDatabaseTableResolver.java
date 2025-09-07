package io.github.pdkst.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.pojo.TableField;
import com.caojx.idea.plugin.common.pojo.TableInfo;
import com.caojx.idea.plugin.common.utils.JdbcTypeMappingHandler;
import com.google.common.base.CaseFormat;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * @author pdkst
 * @since 2025/9/7
 */
@Getter
public class MysqlDatabaseTableResolver implements TableResolver {
    /**
     * 数据库
     */
    private final Database database;
    /**
     * 自定义jdbc映射类型
     */
    private final JdbcTypeMappingHandler jdbcTypeMappingHandler;
    /**
     * 表名模式
     */
    private final String identifyPatten;

    public MysqlDatabaseTableResolver(Database database, String identifyPatten) {
        this.database = database;
        this.jdbcTypeMappingHandler = new JdbcTypeMappingHandler(new HashMap<>());
        this.identifyPatten = identifyPatten;
    }

    public MysqlDatabaseTableResolver(Database database,
                                      Map<JDBCType, Class<?>> customerJdbcTypeMappingMap,
                                      String identifyPatten) {
        this.database = database;
        this.jdbcTypeMappingHandler = new JdbcTypeMappingHandler(customerJdbcTypeMappingMap);
        this.identifyPatten = identifyPatten;
    }

    public List<TableInfo> resolveTableList(Connection conn,
                                            List<String> tableNames,
                                            boolean withFields) throws SQLException {
        List<TableInfo> tableInfoList = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        for (String tableName : tableNames) {
            ResultSet rs = metaData.getTables(conn.getCatalog(), conn.getSchema(), tableName, new String[]{"TABLE"});
            while (rs.next()) {
                // 表注释
                String tableNameResult = rs.getString("TABLE_NAME");
                TableInfo tableInfo;
                if (withFields) {
                    // 列列表
                    List<TableField> fields = resolveFieldList(conn, tableNameResult);
                    // 返回表信息
                    tableInfo = resolveTableInfo(rs, fields);
                } else {
                    // 列列表
                    tableInfo = resolveTableInfo(rs, new ArrayList<>());
                }
                tableInfoList.add(tableInfo);
            }
        }
        return tableInfoList;
    }

    public TableInfo resolveTableInfo(ResultSet rs, List<TableField> fields) throws SQLException {
        String tableNameResult = rs.getString("TABLE_NAME");
        String remarks = rs.getString("REMARKS");
        TableInfo tableInfo = new TableInfo(tableNameResult, remarks, new ArrayList<>());
        tableInfo.setName(tableNameResult);
        tableInfo.setComment(remarks);
        tableInfo.setFields(fields);

        final TableField primaryKeyField = findPrimaryKey(fields);
        if (Objects.nonNull(primaryKeyField)) {
            tableInfo.setHavePrimaryKey(true);
            tableInfo.setPrimaryKeyName(primaryKeyField.getName());
            tableInfo.setPrimaryKeyType(primaryKeyField.getType());
        }

        return tableInfo;
    }

    private static @Nullable TableField findPrimaryKey(List<TableField> fields) {
        // 主键类型
        return Optional.ofNullable(fields)
                .orElse(new ArrayList<>())
                .stream()
                .filter(TableField::isPrimaryKeyFlag)
                .findAny()
                .orElse(null);
    }


    private List<TableField> resolveFieldList(Connection conn, String tableName) throws SQLException {
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
            TableField tableField = resolveField(rs, primaryKey);
            fields.add(tableField);
        }
        return fields;
    }

    public TableField resolveField(ResultSet rs, String primaryKey) throws SQLException {
        // 列名
        String columnName = rs.getString("COLUMN_NAME");
        // 字段注释
        String remarks = rs.getString("REMARKS");
        // 字段类型
        int dataType = rs.getInt("DATA_TYPE");

        // 是否为主键
        boolean primaryKeyFlag = Objects.nonNull(primaryKey) && columnName.equals(primaryKey);
        final TableField tableField = new TableField();
        tableField.setColumnName(columnName);
        tableField.setComment(remarks);
        tableField.setName(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName));

        tableField.setType(jdbcTypeMappingHandler.convertJavaType(dataType));
        tableField.setJdbcTypeName(jdbcTypeMappingHandler.convertJdbcType(dataType));
        tableField.setPrimaryKeyFlag(primaryKeyFlag);
        tableField.setJdbcDateFlag(jdbcTypeMappingHandler.isJDBCDateColumn(dataType));
        tableField.setJdbcTimeFlag(jdbcTypeMappingHandler.isJDBCTimeColumn(dataType));
        tableField.setBlobFlag(jdbcTypeMappingHandler.isBLOBColumn(dataType));
        if (isIdentifyPatten(columnName)) {
            tableField.setType(Long.class);
        }
        // 构建表属性
        return tableField;
    }

    private boolean isIdentifyPatten(String columnName) {
        if (StringUtils.isBlank(identifyPatten)) {
            return false;
        }
        Pattern pattern = Pattern.compile(identifyPatten, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(columnName);
        return matcher.find();
    }

    @Override
    public TableInfo getTable(String tableName) throws SQLException {
        List<TableInfo> tableInfos = database.execute(
                connection -> resolveTableList(connection, singletonList(tableName), true));
        if (tableInfos == null || tableInfos.isEmpty()) {
            return null;
        }
        return tableInfos.get(0);
    }

    @Override
    public List<TableInfo> getTablesAndFields(List<String> tableNames) throws SQLException {
        return database.execute(connection -> resolveTableList(connection, tableNames, true));
    }

    @Override
    public List<TableInfo> getTables(List<String> tableNames) throws SQLException {
        return database.execute(connection -> resolveTableList(connection, tableNames, false));
    }
}
