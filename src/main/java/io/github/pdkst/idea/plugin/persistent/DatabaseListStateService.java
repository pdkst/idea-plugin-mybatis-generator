package io.github.pdkst.idea.plugin.persistent;


import com.caojx.idea.plugin.common.pojo.DatabaseProperties;
import com.caojx.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import com.caojx.idea.plugin.persistent.PersistentExtConfig;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import io.github.pdkst.idea.plugin.common.utils.PasswordUtils;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private DatabaseListState databaseListState;
    private List<DatabaseSensitiveProperties> databases;

    @Override
    public @Nullable DatabaseListState getState() {
        final List<DatabaseProperties> databasePropertiesList = new ArrayList<>();
        for (DatabaseSensitiveProperties databaseSensitiveProperties : this.databases) {
            final DatabaseProperties databaseProperties = new DatabaseProperties(databaseSensitiveProperties);
            databasePropertiesList.add(databaseProperties);
        }
        databaseListState.setDatabases(databasePropertiesList);
        return databaseListState;
    }

    @Override
    public void loadState(@NotNull DatabaseListState state) {
        final List<DatabaseProperties> databases = state.getDatabases();
        this.databases = buildDatabasesWithPassword(databases);
        this.databaseListState = state;
    }

    private List<DatabaseSensitiveProperties> buildDatabasesWithPassword(List<DatabaseProperties> databases) {
        final List<DatabaseSensitiveProperties> sensitivePropertiesList = new ArrayList<>();
        if (CollectionUtils.isEmpty(databases)) {
            return new ArrayList<>();
        }

        for (DatabaseProperties database : databases) {
            final DatabaseSensitiveProperties sensitiveProperties = new DatabaseSensitiveProperties(database,
                    PasswordUtils.getPassword(database.getIdentifierName()));
            sensitivePropertiesList.add(sensitiveProperties);
        }
        return sensitivePropertiesList;
    }

    public List<DatabaseSensitiveProperties> getDatabases() {
        if (CollectionUtils.isEmpty(this.databases)) {
            List<DatabaseProperties> databasesWithoutPassword = PersistentExtConfig.loadDatabase();
            final List<DatabaseSensitiveProperties> sensitivePropertiesList = buildDatabasesWithPassword(
                    databasesWithoutPassword);
            this.databases = sensitivePropertiesList;
        }
        return this.databases;
    }

    public void setDatabases(List<DatabaseSensitiveProperties> databases) {
        for (DatabaseSensitiveProperties database : this.databases) {
            PasswordUtils.clearPassword(database.getIdentifierName());
        }
        for (DatabaseSensitiveProperties database : databases) {
            PasswordUtils.setPassword(database.getIdentifierName(), database.getPassword());
        }
        this.databases = databases;
    }

    public static DatabaseListStateService getInstance() {
        return ApplicationManager.getApplication().getService(DatabaseListStateService.class);
    }

    public void remove(int index) {
        final DatabaseSensitiveProperties sensitiveProperties = this.databases.remove(index);
        PasswordUtils.clearPassword(sensitiveProperties.getIdentifierName());
    }

    public void replaceByIdentify(DatabaseSensitiveProperties replace) {
        this.databases.removeIf(database -> Objects.equals(database.getIdentifierName(), replace.getIdentifierName()));
        this.databases.add(replace);
    }
}
