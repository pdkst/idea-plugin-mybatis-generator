package io.github.pdkst.idea.plugin.persistent;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author pdkst.zhang
 * @since 2025/03/28 11:07
 */
@Data
@State(
        // 存储xml标签信息
        name = "DatabaseListStateService",
        // 存放文件名
        storages = @Storage("mybatis-generator-database-list-plugin.xml"))
public class DatabaseListStateService implements PersistentStateComponent<DatabaseListState> {
    private DatabaseListState databaseListState;

    @Delegate
    @Override
    public @Nullable DatabaseListState getState() {
        if (databaseListState == null) {
            databaseListState = new DatabaseListState(new ArrayList<>());
        }
        return databaseListState;
    }

    @Override
    public void loadState(@NotNull DatabaseListState state) {
        this.databaseListState = state;
    }
}
