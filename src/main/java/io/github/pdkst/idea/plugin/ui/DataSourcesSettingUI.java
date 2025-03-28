package io.github.pdkst.idea.plugin.ui;

import com.caojx.idea.plugin.common.pojo.DatabaseProperties;
import com.caojx.idea.plugin.common.utils.MyMessages;
import com.caojx.idea.plugin.persistent.PersistentExtConfig;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.pdkst.idea.plugin.common.utils.PasswordUtils;
import io.github.pdkst.idea.plugin.common.utils.RefreshDispatcher;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 数据源UI配置类
 *
 * @author caojx
 * @date 2022/4/10 10:00 AM
 */
public class DataSourcesSettingUI extends DialogWrapper {
    private JPanel mainPanel;
    private JTable dataSourcesTable;
    private JButton addBtn;
    private JButton deleteBtn;
    private JButton editBtn;

    private final Project project;

    /**
     * 数据库列表
     */
    private List<DatabaseProperties> databases;

    /**
     * 表头
     */
    private static final String[] TABLE_COLUMN_NAME = {"database", "host", "port", "type"};

    /**
     * 表数据模型
     */
    public static DefaultTableModel TABLE_MODEL = new DefaultTableModel(null, TABLE_COLUMN_NAME);

    /**
     * 选中的行
     */
    private int selectedRow = -1;

    /**
     * 刷新监听器
     */
    @Delegate
    private final RefreshDispatcher refreshDispatcher = new RefreshDispatcher();

    public DataSourcesSettingUI(@NotNull Project project) {
        super(true);
        init();

        this.project = project;

        // 初始化界面数据
        renderUIData(project);

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
    private void renderUIData(Project project) {
        // 数据库列表
        databases = PersistentExtConfig.loadDatabase();

        // 初始化表数据
        dataSourcesTable.setModel(TABLE_MODEL);
        refreshDatabaseTable(databases);
    }

    /**
     * 创建事件监听器
     *
     * @param project 项目
     */
    private void initActionListener(Project project) {
        // 监听表格选中的行
        dataSourcesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRow = dataSourcesTable.getSelectedRow();
            }
        });

        // 删除数据库
        deleteBtn.addActionListener(e -> {
            if (selectedRow == -1) {
                MyMessages.showWarningDialog(project, "请选择需要删除的数据库", "Warning");
                return;
            }

            // 清除密码
            DatabaseProperties deleteDatabase = databases.get(selectedRow);
            PasswordUtils.clearPassword(deleteDatabase.getIdentifierName());

            // 从数组列表中移除
            databases.remove(selectedRow);
            selectedRow = -1;

            // 更新数据库配置
            PersistentExtConfig.saveDatabases(databases);

            // 刷新
            refreshDatabaseTable(databases);
            refreshDispatcher.triggerRefresh(databases);
        });

        // 编辑数据库
        editBtn.addActionListener(e -> {
            if (selectedRow == -1) {
                MyMessages.showWarningDialog(project, "请选择需要编辑的数据库", "Warning");
                return;
            }

            DatabaseProperties database = databases.get(selectedRow);
            EditDatabaseSettingUI editDatabaseSettingUI = new EditDatabaseSettingUI(project, database);
            editDatabaseSettingUI.show();
            selectedRow = -1;
        });

        // 添加数据库
        addBtn.addActionListener(e -> {
            EditDatabaseSettingUI editDatabaseSettingUI = new EditDatabaseSettingUI(project, null);
            editDatabaseSettingUI.addListener(args -> {
                refreshDatabaseTable(PersistentExtConfig.loadDatabase());
            });
            editDatabaseSettingUI.show();
        });
    }

    /**
     * 刷新数据库表
     *
     * @param databases 数据库列表
     */
    public void refreshDatabaseTable(List<DatabaseProperties> databases) {
        // 刷新数据库表
        TABLE_MODEL.setDataVector(null, TABLE_COLUMN_NAME);
        databases.forEach(database -> {
            Object[] row = new Object[4];
            row[0] = database.getDatabaseName();
            row[1] = database.getHost();
            row[2] = database.getPort();
            row[3] = database.getDatabaseType();
            TABLE_MODEL.addRow(row);
        });

        // 刷新数据库选择下拉框
        refreshDispatcher.triggerRefresh(databases);
    }
}
