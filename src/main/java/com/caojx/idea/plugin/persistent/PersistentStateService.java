package com.caojx.idea.plugin.persistent;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 持久化数据业务实现
 *
 * @author caojx
 * @since 2022/4/4 12:57 PM
 */
@State(
        // 存储xml标签信息
        name = "persistentStateService",
        // 存放文件名
        storages = @Storage("mbg-plugin.xml"))
public class PersistentStateService implements PersistentStateComponent<PersistentState> {

    /**
     * 持久化数据
     */
    private PersistentState persistentData = new PersistentState();

    public static PersistentStateService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, PersistentStateService.class);
    }

    @Override
    public @Nullable PersistentState getState() {
        if (this.persistentData == null) {
            this.persistentData = new PersistentState();
        }
        return this.persistentData;
    }

    @Override
    public void loadState(@NotNull PersistentState persistentData) {
        this.persistentData = persistentData;
    }
}
