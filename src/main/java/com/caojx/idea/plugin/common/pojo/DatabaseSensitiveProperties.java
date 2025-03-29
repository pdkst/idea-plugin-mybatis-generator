package com.caojx.idea.plugin.common.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据库包含密码
 *
 * @author caojx
 * @date 2022/5/1 9:56 AM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatabaseSensitiveProperties extends DatabaseProperties {

    /**
     * 密码
     */
    @EqualsAndHashCode.Exclude
    private String password;

    public DatabaseSensitiveProperties() {
    }

    public DatabaseSensitiveProperties(DatabaseProperties databaseWithOutPwd, String password) {
        super(databaseWithOutPwd);
        this.password = password;
    }

    public DatabaseSensitiveProperties(DatabaseSensitiveProperties source) {
        super(source);
        this.password = source.getPassword();
    }
}
