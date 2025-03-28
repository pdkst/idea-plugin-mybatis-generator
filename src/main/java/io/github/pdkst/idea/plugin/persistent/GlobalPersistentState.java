package io.github.pdkst.idea.plugin.persistent;

import com.caojx.idea.plugin.common.pojo.DatabaseProperties;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pdkst
 * @since 2025/03/27
 */
@Data
public class GlobalPersistentState {
    /**
     * 作者
     */
    private String author;
    /**
     * 自定义jdbc类型映射
     */
    private Map<String, String> customerJdbcTypeMappingMap = new HashMap<>();

    /**
     * 数据库配置
     */
    private List<DatabaseProperties> databaseList;
}
