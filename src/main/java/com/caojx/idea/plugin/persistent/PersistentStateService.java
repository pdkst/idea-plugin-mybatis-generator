package com.caojx.idea.plugin.persistent;

import com.caojx.idea.plugin.common.pojo.DatabaseProperties;
import com.caojx.idea.plugin.common.properties.CommonProperties;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 持久化数据业务实现
 *
 * @author caojx
 * @date 2022/4/4 12:57 PM
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
            return this.persistentData = new PersistentState();
        }
        return this.persistentData;
    }

    @Override
    public void loadState(@NotNull PersistentState persistentData) {
        this.persistentData = persistentData;
        // 迁移数据库配置
        transferXmlDatabasesConfig(this.persistentData);
    }

    /**
     * 数据库配置迁移到user.home/.myBatisCodeGenerator/ext-config.json 文件中
     *
     * @param persistentData
     */
    private void transferXmlDatabasesConfig(PersistentState persistentData) {
        if (Objects.nonNull(persistentData.getGeneratorProperties()) && Objects.nonNull(persistentData.getGeneratorProperties().getCommonProperties())) {
            CommonProperties commonProperties = persistentData.getGeneratorProperties().getCommonProperties();
            List<DatabaseProperties> databases = commonProperties.getDatabases();
            if (databases != null && !databases.isEmpty()) {
                List<DatabaseProperties> databasesConfigList = PersistentExtConfig.loadDatabase();
                for (DatabaseProperties database : databases) {
                    boolean anyMatch = databasesConfigList.stream().anyMatch(item -> item.getIdentifierName().equals(database.getIdentifierName()));
                    if (!anyMatch) {
                        databasesConfigList.add(database);
                    }
                }
                PersistentExtConfig.saveDatabases(databasesConfigList);
            }
            // 迁移完成，清空配置
            commonProperties.setDatabases(new ArrayList<>());
        }
    }
}
