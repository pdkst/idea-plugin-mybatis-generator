package com.caojx.idea.plugin.common.properties;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 公共配置属性
 *
 * @author caojx
 * @since 2022/4/10 12:20 PM
 */
@Data
public class CommonProperties implements Serializable {

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
     * 框架类型列表
     */
    private List<String> frameworkTypeComboBoxValues;

    /**
     * 选择的框架类型
     */
    private String frameworkTypeComboBoxValue;
}
