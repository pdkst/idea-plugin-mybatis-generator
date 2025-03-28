package io.github.pdkst.idea.plugin.persistent;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author pdkst.zhang
 * @since 2025/03/28 11:10
 */
@State(
        // 存储xml标签信息
        name = "DatabaseState",
        // 存放文件名
        storages = @Storage("mybatis-generator-database-plugin.xml"))
public class DatabaseStateService implements PersistentStateComponent<DatabaseState> {
    private DatabaseState databaseState;

    @Delegate
    @Override
    public @Nullable DatabaseState getState() {
        if (databaseState == null) {
            databaseState = new DatabaseState();
        }
        return databaseState;
    }

    @Override
    public void loadState(@NotNull DatabaseState state) {
        this.databaseState = state;
    }
}
