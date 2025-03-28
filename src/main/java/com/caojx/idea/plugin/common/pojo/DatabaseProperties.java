package com.caojx.idea.plugin.common.pojo;

import lombok.Data;

/**
 * 数据库信息不带密码
 * <p>
 * 提示：保留了一些非url属性是为了兼容老版本
 *
 * @author caojx
 * @date 2022/4/10 4:00 PM
 */
@Data
public class DatabaseProperties {

    /**
     * 数据库类型
     */
    private String databaseType;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 数据库
     */
    private String databaseName;

    /**
     * 用户名
     */
    private String userName;


    public DatabaseProperties() {
    }

    public DatabaseProperties(String databaseType, String host, Integer port, String databaseName, String userName) {
        this.databaseType = databaseType;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.userName = userName;
    }

    public DatabaseProperties(DatabaseProperties properties) {
        this.databaseType = properties.getDatabaseType();
        this.host = properties.getHost();
        this.port = properties.getPort();
        this.databaseName = properties.getDatabaseName();
        this.userName = properties.getUserName();
    }

    public String getIdentifierName() {
        return databaseName + "@" + host + ":" + port + ":" + databaseType;
    }

    public String getUrl() {
        return "jdbc:" + databaseType + "://" + this.host + ":" + this.port + "/" + this.databaseName;
    }

}
