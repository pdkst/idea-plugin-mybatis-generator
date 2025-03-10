package com.caojx.idea.plugin.common.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * Facade配置属性
 *
 * @author pdkst
 * @since 2025-03-10 19:31:26
 */
@Data
public class FacadeProperties implements Serializable {

    /**
     * 是否生成facade
     */
    private boolean selectedGenerateCheckBox;

    /**
     * facade路径
     */
    private String path;

    /**
     * facade包名
     */
    private String packageName;

    /**
     * facade命名格式
     */
    private String namePattern;

    /**
     * facade父类
     */
    private String superClass;

}
