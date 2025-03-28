package io.github.pdkst.idea.plugin.persistent;

import com.caojx.idea.plugin.common.pojo.DatabaseProperties;
import lombok.Data;

import java.util.ArrayList;
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

    public DatabaseListState() {
        this.databases = new ArrayList<>();
    }

    public DatabaseListState(List<DatabaseProperties> databases) {
        this.databases = databases;
    }
}
