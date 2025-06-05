package io.github.pdkst.idea.plugin.ui;

import com.caojx.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import com.caojx.idea.plugin.common.utils.MyMessages;
import com.intellij.openapi.project.Project;
import io.github.pdkst.idea.plugin.common.utils.DatabaseTableModel;
import io.github.pdkst.idea.plugin.common.utils.RefreshListener;
import io.github.pdkst.idea.plugin.persistent.DatabaseListStateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * 数据源UI配置类
 *
 * @author caojx
 * @date 2022/4/10 10:00 AM
 */
public class DataSourcesSettingUI extends AbstractDialog {
    private JPanel mainPanel;
    private JTable dataSourcesTable;
    private JButton addBtn;
    private JButton deleteBtn;
    private JButton editBtn;

    private final Project project;
    private final DatabaseListStateService databaseListStateService;

    /**
     * 表数据模型
     */
    public static DatabaseTableModel tableModel = new DatabaseTableModel();

    public DataSourcesSettingUI(@NotNull Project project, RefreshListener... listeners) {
        super(project, listeners);
        init();

        this.project = project;
        this.databaseListStateService = DatabaseListStateService.getInstance();

        refresh();

        // 创建事件监听器
        initActionListener(project);
    }

    @Override
    public void refresh(Object... args) {
        // 初始化表数据
        dataSourcesTable.setModel(tableModel);
        // 数据库列表
        refreshDatabaseTable();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    protected JComponent createSouthPanel() {
        return null;
    }

    /**
     * 刷新数据库表
     */
    public void refreshDatabaseTable() {
        final List<DatabaseSensitiveProperties> databases = databaseListStateService.getDatabases();
        // 刷新数据库表
        tableModel.setDatabases(databases);

        // 刷新数据库选择下拉框
        triggerRefresh(databases);
    }

    /**
     * 创建事件监听器
     *
     * @param project 项目
     */
    private void initActionListener(Project project) {

        // 删除数据库
        deleteBtn.addActionListener(e -> {
            final int selectedRow = dataSourcesTable.getSelectedRow();
            if (selectedRow == -1) {
                MyMessages.showWarningDialog(project, "请选择需要删除的数据库", "Warning");
                return;
            }

            // 从数组列表中移除
            databaseListStateService.remove(selectedRow);

            // 刷新
            refreshDatabaseTable();
            triggerRefresh();
        });

        // 编辑数据库
        editBtn.addActionListener(e -> {
            final int selectedRow = dataSourcesTable.getSelectedRow();
            if (selectedRow == -1) {
                MyMessages.showWarningDialog(project, "请选择需要编辑的数据库", "Warning");
                return;
            }
            final List<DatabaseSensitiveProperties> databases = databaseListStateService.getDatabases();
            DatabaseSensitiveProperties database = databases.get(selectedRow);
            EditDatabaseSettingUI editDatabaseSetting = new EditDatabaseSettingUI(project, database, this);
            editDatabaseSetting.show();
        });

        // 添加数据库
        addBtn.addActionListener(e -> {
            EditDatabaseSettingUI editDatabaseSetting = new EditDatabaseSettingUI(project, null, this);
            editDatabaseSetting.show();
        });
    }
}
