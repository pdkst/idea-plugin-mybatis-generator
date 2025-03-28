package io.github.pdkst.idea.plugin.persistent;

import com.caojx.idea.plugin.common.pojo.DatabaseProperties;
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
    private DatabaseProperties databases;
}
