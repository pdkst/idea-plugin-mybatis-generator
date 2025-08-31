package io.github.pdkst.idea.plugin.state;

import io.github.pdkst.idea.plugin.common.pojo.DatabaseProperties;
import lombok.Data;

import java.util.List;

/**
 * 数据库配置
 *
 * @author pdkst
 * @since 2022/12/12 14:43
 */
@Data
public class DatabaseListState {
    private List<DatabaseProperties> databases;
}
