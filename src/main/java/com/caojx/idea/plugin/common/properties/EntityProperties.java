package com.caojx.idea.plugin.common.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.JDBCType;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体配置属性
 *
 * @author caojx
 * @date 2022/4/10 12:15 PM
 */
@Data
public class EntityProperties implements Serializable {

    /**
     * 是否生成实体
     */
    private boolean selectedGenerateCheckBox = true;

    /**
     * 实体路径
     */
    private String path;

    /**
     * 实体包名
     */
    private String packageName;

    /**
     * 实体命名格式
     */
    private String namePattern;

    /**
     * entityExample是否生成
     */
    private boolean selectedGenerateEntityExampleCheckBox;

    /**
     * entityExample 命名格式
     */
    private String exampleNamePattern;

    /**
     * entity 实现 Serializable
     */
    private boolean selectedSerializableCheckBox;

    /**
     * 实体lombok @Data注解
     */
    private boolean selectedDataCheckBox;

    /**
     * 实体lombok @Builder注解
     */
    private boolean selectedBuilderCheckBox;

    /**
     * 实体lombok @NoArgsConstructor注解
     */
    private boolean selectedNoArgsConstructorCheckBox;

    /**
     * 实体lombok @AllArgsConstructor注解
     */
    private boolean selectedAllArgsConstructorCheckBox;

    /**
     * entity swagger注解
     */
    private boolean selectedSwaggerCheckBox;

    /**
     * 自定义jdbc类型映射
     */
    private Map<String, String> customerJdbcTypeMappingMap = new HashMap<>();

}
