package io.github.pdkst.idea.plugin.common.utils;

import io.github.pdkst.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DatabaseHelper {

    public static Database getMySql(DatabaseSensitiveProperties databaseWithPwd) {
        return new MySqlDatabase(databaseWithPwd);
    }
}
