package io.github.pdkst.idea.plugin.ui;

import com.caojx.idea.plugin.common.pojo.DatabaseProperties;
import com.caojx.idea.plugin.common.utils.MyMessages;
import com.caojx.idea.plugin.persistent.PersistentExtConfig;
import com.intellij.openapi.project.Project;
import io.github.pdkst.idea.plugin.common.utils.DatabaseTableModel;
import io.github.pdkst.idea.plugin.common.utils.PasswordUtils;
import io.github.pdkst.idea.plugin.common.utils.RefreshDispatcher;
import lombok.experimental.Delegate;
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

    /**
     * 表数据模型
     */
    public static DatabaseTableModel tableModel = new DatabaseTableModel();

    public DataSourcesSettingUI(@NotNull Project project) {
        super(project);
        init();

        this.project = project;

        // 初始化界面数据
        initData(project);

        // 创建事件监听器
        initActionListener(project);
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
     * 渲染UI数据
     *
     * @param project 项目
     */
    private void initData(Project project) {

        // 初始化表数据
        dataSourcesTable.setModel(tableModel);
        // 数据库列表
        refreshDatabaseTable();
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

            final List<DatabaseProperties> databases = PersistentExtConfig.loadDatabase();
            // 清除密码
            DatabaseProperties deleteDatabase = databases.get(selectedRow);
            PasswordUtils.clearPassword(deleteDatabase.getIdentifierName());

            // 从数组列表中移除
            databases.remove(selectedRow);

            // 更新数据库配置
            PersistentExtConfig.saveDatabases(databases);

            // 刷新
            refreshDatabaseTable();
            triggerRefresh(databases);
        });

        // 编辑数据库
        editBtn.addActionListener(e -> {
            final int selectedRow = dataSourcesTable.getSelectedRow();
            if (selectedRow == -1) {
                MyMessages.showWarningDialog(project, "请选择需要编辑的数据库", "Warning");
                return;
            }
            final List<DatabaseProperties> databases = PersistentExtConfig.loadDatabase();
            DatabaseProperties database = databases.get(selectedRow);
            EditDatabaseSettingUI editDatabaseSettingUI = new EditDatabaseSettingUI(project, database);
            editDatabaseSettingUI.addListener(args -> {
                refreshDatabaseTable();
            });
            editDatabaseSettingUI.show();
        });

        // 添加数据库
        addBtn.addActionListener(e -> {
            EditDatabaseSettingUI editDatabaseSettingUI = new EditDatabaseSettingUI(project, null);
            editDatabaseSettingUI.addListener(args -> {
                refreshDatabaseTable();
            });
            editDatabaseSettingUI.show();
        });
    }

    /**
     * 刷新数据库表
     */
    public void refreshDatabaseTable() {
        final List<DatabaseProperties> databases = PersistentExtConfig.loadDatabase();
        // 刷新数据库表
        tableModel.setDatabases(databases);

        // 刷新数据库选择下拉框
        triggerRefresh(databases);
    }
}
