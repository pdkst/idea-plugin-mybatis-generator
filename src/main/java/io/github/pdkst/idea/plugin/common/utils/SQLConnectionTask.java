package io.github.pdkst.idea.plugin.common.utils;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLConnectionTask<T> {
    T execute(Connection connection) throws SQLException;
}
