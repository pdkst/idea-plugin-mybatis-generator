package com.caojx.idea.plugin.generator;

import com.caojx.idea.plugin.common.pojo.TableInfo;
import com.caojx.idea.plugin.common.properties.GeneratorProperties;
import com.intellij.util.xmlb.annotations.Transient;
import io.github.pdkst.idea.plugin.persistent.GlobalPersistentState;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成代码上下文
 *
 * @author caojx
 * @date 2022/4/10 4:00 PM
 */
@Data
public class GeneratorContext implements Serializable {

    /**
     * 代码生成配置
     */
    private GlobalPersistentState globalPersistentState;
    /**
     * 代码生成配置
     */
    private GeneratorProperties generatorProperties;

    /**
     * 生成的表
     */
    @Transient
    private List<TableInfo> tables = new ArrayList<>();

}
