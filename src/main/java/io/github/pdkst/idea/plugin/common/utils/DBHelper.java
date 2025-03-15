package io.github.pdkst.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.pojo.DatabaseWithPwd;
import lombok.experimental.UtilityClass;

import java.sql.JDBCType;
import java.util.Map;

@UtilityClass
public class DBHelper {

    public static Database getMySql(DatabaseWithPwd databaseWithPwd, Map<JDBCType, Class<?>> customerJdbcTypeMappingMap) {
        return new MySqlDatabase(databaseWithPwd, customerJdbcTypeMappingMap);
    }
}
