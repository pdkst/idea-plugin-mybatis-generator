package io.github.pdkst.idea.plugin.persistent;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author pdkst
 * @since 2025/03/27
 */
@State(
        // 存储xml标签信息
        name = "GlobalPersistentStateService",
        // 存放文件名
        storages = @Storage("mybatis-generator-global-plugin.xml"))
public class GlobalPersistentStateService implements PersistentStateComponent<GlobalPersistentState> {
    private GlobalPersistentState globalPersistentState;

    @Delegate
    @Override
    public @Nullable GlobalPersistentState getState() {
        if (globalPersistentState == null) {
            return globalPersistentState = new GlobalPersistentState();
        }
        return globalPersistentState;
    }

    @Override
    public void loadState(@NotNull GlobalPersistentState state) {
        this.globalPersistentState = state;
    }

    public static GlobalPersistentStateService getInstance() {
        return ApplicationManager.getApplication().getService(GlobalPersistentStateService.class);
    }
}
