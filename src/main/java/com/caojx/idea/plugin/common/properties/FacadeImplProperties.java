package com.caojx.idea.plugin.common.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * FacadeImpl配置属性
 *
 * @author pdkst
 * @since 2025-03-10 19:31:42
 */
@Data
public class FacadeImplProperties implements Serializable {

    /**
     * 是否生成facadeImpl
     */
    private boolean selectedGenerateCheckBox;

    /**
     * facadeImpl路径
     */
    private String path;

    /**
     * facadeImpl包名
     */
    private String packageName;

    /**
     * facadeImpl命名格式
     */
    private String namePattern;

    /**
     * facadeImpl父类
     */
    private String superClass;

}
