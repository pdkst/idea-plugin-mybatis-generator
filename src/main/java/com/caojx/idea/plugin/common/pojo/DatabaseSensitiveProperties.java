package com.caojx.idea.plugin.common.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据库包含密码
 *
 * @author caojx
 * @date 2022/5/1 9:56 AM
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatabaseSensitiveProperties extends DatabaseProperties {

    /**
     * 密码
     */
    private String password;

    public DatabaseSensitiveProperties() {
    }

    public DatabaseSensitiveProperties(DatabaseProperties databaseWithOutPwd, String password) {
        super(databaseWithOutPwd);
        this.password = password;
    }
}
