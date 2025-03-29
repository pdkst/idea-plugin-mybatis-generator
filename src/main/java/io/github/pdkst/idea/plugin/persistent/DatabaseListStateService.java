package io.github.pdkst.idea.plugin.persistent;


import com.caojx.idea.plugin.common.pojo.DatabaseProperties;
import com.caojx.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import io.github.pdkst.idea.plugin.common.utils.PasswordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库列表
 *
 * @author pdkst.zhang
 * @since 2025/03/28 11:07
 */
@State(
        // 存储xml标签信息
        name = "DatabaseListStateService",
        // 存放文件名
        storages = @Storage("mybatis-generator-database-list-plugin.xml"))
public class DatabaseListStateService implements PersistentStateComponent<DatabaseListState> {
    private List<DatabaseSensitiveProperties> databases;

    @Override
    public @Nullable DatabaseListState getState() {
        final List<DatabaseProperties> databasePropertiesList = new ArrayList<>();
        for (DatabaseSensitiveProperties databaseSensitiveProperties : this.databases) {
            final DatabaseProperties databaseProperties = new DatabaseProperties(databaseSensitiveProperties);
            databasePropertiesList.add(databaseProperties);
        }
        return new DatabaseListState(databasePropertiesList);
    }

    @Override
    public void loadState(@NotNull DatabaseListState state) {
        final List<DatabaseSensitiveProperties> sensitivePropertiesList = new ArrayList<>();
        final List<DatabaseProperties> databases = state.getDatabases();
        for (DatabaseProperties database : databases) {
            final DatabaseSensitiveProperties sensitiveProperties = new DatabaseSensitiveProperties();
            sensitiveProperties.setPassword(PasswordUtils.getPassword(database.getIdentifierName()));
            sensitivePropertiesList.add(sensitiveProperties);
        }
        this.databases = sensitivePropertiesList;
    }

    public void setDatabases(List<DatabaseSensitiveProperties> databases) {
        for (DatabaseSensitiveProperties database : databases) {
            PasswordUtils.setPassword(database.getIdentifierName(), database.getPassword());
        }
        this.databases = databases;
    }
}
