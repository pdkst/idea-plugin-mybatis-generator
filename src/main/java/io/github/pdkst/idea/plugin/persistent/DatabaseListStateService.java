package io.github.pdkst.idea.plugin.persistent;


import com.caojx.idea.plugin.common.pojo.DatabaseProperties;
import com.caojx.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import com.caojx.idea.plugin.persistent.PersistentExtConfig;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import io.github.pdkst.idea.plugin.common.utils.PasswordUtils;
// 移除未使用的导入
// import lombok.Getter;
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
    private List<DatabaseSensitiveProperties> databases;

    public static DatabaseListStateService getInstance() {
        return ApplicationManager.getApplication().getService(DatabaseListStateService.class);
    }

    @Override
    public @Nullable DatabaseListState getState() {
        final List<DatabaseProperties> databasePropertiesList = new ArrayList<>();
        for (DatabaseSensitiveProperties databaseSensitiveProperties : this.databases) {
            final DatabaseProperties databaseProperties = new DatabaseProperties(databaseSensitiveProperties);
            databasePropertiesList.add(databaseProperties);
        }
        // 创建新的 DatabaseListState 实例并设置属性
        DatabaseListState state = new DatabaseListState();
        state.setDatabases(databasePropertiesList);
        return state;
    }

    @Override
    public void loadState(@NotNull DatabaseListState state) {
        final List<DatabaseProperties> databases = state.getDatabases();
        this.databases = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(databases)) {
            for (DatabaseProperties database : databases) {
                final DatabaseSensitiveProperties sensitiveProperties = new DatabaseSensitiveProperties(database,
                        PasswordUtils.getPassword(database.getIdentifierName()));
                this.databases.add(sensitiveProperties);
            }
        }
    }

    private List<DatabaseSensitiveProperties> buildDatabasesWithPassword(List<DatabaseProperties> databases) {
        // 消除冗余的局部变量
        if (CollectionUtils.isEmpty(databases)) {
            return new ArrayList<>();
        }

        List<DatabaseSensitiveProperties> result = new ArrayList<>();
        for (DatabaseProperties database : databases) {
            final DatabaseSensitiveProperties sensitiveProperties = new DatabaseSensitiveProperties(database,
                    PasswordUtils.getPassword(database.getIdentifierName()));
            result.add(sensitiveProperties);
        }
        return result;
    }

    public List<DatabaseSensitiveProperties> getDatabases() {
        if (CollectionUtils.isEmpty(this.databases)) {
            List<DatabaseProperties> databasesWithoutPassword = PersistentExtConfig.loadDatabase();
            this.databases = buildDatabasesWithPassword(databasesWithoutPassword);
        }
        return this.databases;
    }

    public void remove(int index) {
        final DatabaseSensitiveProperties sensitiveProperties = this.databases.remove(index);
        PasswordUtils.clearPassword(sensitiveProperties.getIdentifierName());
    }

    public void replaceByIdentify(DatabaseSensitiveProperties replace) {
        this.databases.removeIf(database -> Objects.equals(database.getIdentifierName(), replace.getIdentifierName()));
        this.databases.add(replace);
    }

    public DatabaseSensitiveProperties getDatabaseByIdentify(String identifierName) {
        for (DatabaseSensitiveProperties database : this.databases) {
            if (Objects.equals(database.getIdentifierName(), identifierName)) {
                return database;
            }
        }
        return null;
    }
}
