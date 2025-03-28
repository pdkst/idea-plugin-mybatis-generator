package io.github.pdkst.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.pojo.TableInfo;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库接口
 *
 * @author pdkst
 * @since 2025-03-15 16:08
 */
public interface Database {

    /**
     * 获取版本
     *
     * @return 版本
     * @throws SQLException 异常
     */
    String getVersion() throws SQLException;

    /**
     * 测试数据库
     *
     * @return 测试结果
     */
    boolean testDatabase();

    /**
     * 获取表信息
     *
     * @param tableName 表名
     * @return 表信息
     * @throws SQLException 异常
     */
    default List<TableInfo> getTablesAndFields(String... tableName) throws SQLException {
        return getTablesAndFields(Arrays.asList(tableName));
    }

    /**
     * 获取表信息
     *
     * @param tableNames 表名
     * @return 表信息
     * @throws SQLException 异常
     */
    List<TableInfo> getTablesAndFields(List<String> tableNames) throws SQLException;

    /**
     * 获取表信息，不包含字段信息
     *
     * @param tableName 表名
     * @return 表信息
     * @throws SQLException 异常
     */
    default List<TableInfo> getTables(String... tableName) throws SQLException {
        return getTables(Arrays.asList(tableName));
    }

    /**
     * 获取表信息，不包含字段信息
     *
     * @param tableNames 表名
     * @return 表信息
     * @throws SQLException 异常
     */
    List<TableInfo> getTables(List<String> tableNames) throws SQLException;
}
