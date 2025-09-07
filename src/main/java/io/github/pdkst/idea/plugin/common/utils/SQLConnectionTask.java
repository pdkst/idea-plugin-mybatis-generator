package io.github.pdkst.idea.plugin.common.utils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @param <T> 执行结果
 */
@FunctionalInterface
public interface SQLConnectionTask<T> {
    /**
     * 执行SQL任务
     *
     * @param connection 数据库连接
     * @return 执行结果
     * @throws SQLException SQL异常
     */
    T execute(Connection connection) throws SQLException;
}
