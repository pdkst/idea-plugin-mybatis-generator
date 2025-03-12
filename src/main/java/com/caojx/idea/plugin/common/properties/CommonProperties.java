package com.caojx.idea.plugin.common.properties;

import com.caojx.idea.plugin.common.pojo.DatabaseWithOutPwd;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 公共配置属性
 *
 * @author caojx
 * @date 2022/4/10 12:20 PM
 */
@Data
public class CommonProperties implements Serializable {


    /**
     * 作者
     */
    private String author;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 模块路径
     */
    private String modulePath;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 基础路径
     */
    private String basePath;

    /**
     * entity 相对包
     */
    private String entityRelativePackage;

    /**
     * 数据库列表
     * 1.3.7版本之后，数据库配置将会保存到 user.home/.myBatisCodeGenerator/ext-config.json 文件中
     */
    private List<DatabaseWithOutPwd> databases = new ArrayList<>();

    /**
     * 选择的数据库
     */
    private String databaseComboBoxValue;

    /**
     * 框架类型列表
     */
    private List<String> frameworkTypeComboBoxValues;

    /**
     * 选择的框架类型
     */
    private String frameworkTypeComboBoxValue;
    /**
     * 表名前缀
     */
    private String tableNamePrefix;
}
