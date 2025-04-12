package com.caojx.idea.plugin.persistent;

import com.caojx.idea.plugin.common.properties.GeneratorProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 持久化数据
 */
@Data
public class PersistentState implements Serializable {

    /**
     * 代码生成配置
     */
    private GeneratorProperties generatorProperties = new GeneratorProperties();

}
