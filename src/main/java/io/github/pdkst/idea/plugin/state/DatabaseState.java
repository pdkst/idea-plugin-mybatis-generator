package io.github.pdkst.idea.plugin.state;

import lombok.Data;

/**
 * 数据库配置
 *
 * @author pdkst
 * @since 2022/12/12 14:43
 */
@Data
public class DatabaseState {
    /**
     * 当前选择的数据库配置
     */
    private String currentDatabase;
    /**
     * 数据库前缀
     */
    private String tablePrefix;
    /**
     * 表id模式
     */
    private String identifyPattern;
}
