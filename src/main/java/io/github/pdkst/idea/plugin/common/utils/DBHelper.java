package io.github.pdkst.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import lombok.experimental.UtilityClass;

import java.sql.JDBCType;
import java.util.Map;

@UtilityClass
public class DBHelper {

    public static Database getMySql(DatabaseSensitiveProperties databaseWithPwd, Map<JDBCType, Class<?>> customerJdbcTypeMappingMap) {
        return new MySqlDatabase(databaseWithPwd, customerJdbcTypeMappingMap);
    }
}
