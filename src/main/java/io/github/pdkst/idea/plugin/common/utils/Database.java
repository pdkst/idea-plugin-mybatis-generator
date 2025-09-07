package io.github.pdkst.idea.plugin.common.utils;

import java.sql.SQLException;

/**
 * 数据库接口
 *
 * @author pdkst
 * @since 2025-03-15 16:08
 */
public interface Database {

    /**
     * 测试数据库
     *
     * @return 测试结果
     */
    boolean testConnection();

    /**
     * 执行SQL任务
     *
     * @param task 任务
     * @param <T>  结果类型
     * @return 结果
     * @throws SQLException 异常
     */
    <T> T execute(SQLConnectionTask<T> task) throws SQLException;
}
