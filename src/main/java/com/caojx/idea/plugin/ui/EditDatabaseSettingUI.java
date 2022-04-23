package com.caojx.idea.plugin.ui;

import com.caojx.idea.plugin.common.enums.DataBaseTypeEnum;
import com.caojx.idea.plugin.common.pojo.Database;
import com.caojx.idea.plugin.common.properties.CommonProperties;
import com.caojx.idea.plugin.common.utils.MyMessages;
import com.caojx.idea.plugin.common.utils.MySQLDBHelper;
import com.caojx.idea.plugin.persistent.PersistentState;
import com.caojx.idea.plugin.persistent.PersistentStateService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

/**
 * 编辑数据库
 *
 * @author caojx
 * @date 2022/4/10 10:00 AM
 */
public class EditDatabaseSettingUI extends DialogWrapper {
    private JPanel mainPanel;
    private JComboBox databaseTypeComboBox;
    private JTextField hostTf;
    private JTextField portTf;
    private JTextField databaseNameTf;
    private JTextField userNameTf;
    private JPasswordField passwordTf;
    private JButton saveBtn;
    private JButton testBtn;

    /**
     * 数据源UI配置类
     */
    private final DataSourcesSettingUI dataSourcesSettingUI;

    /**
     * 编辑的数据库
     */
    private final Database editDatabase;

    /**
     * 数据库列表
     */
    private List<Database> databases;

    public EditDatabaseSettingUI(@NotNull Project project, Database editDatabase, @NotNull DataSourcesSettingUI dataSourcesSettingUI) {
        super(true);
        init();

        this.editDatabase = editDatabase;
        this.dataSourcesSettingUI = dataSourcesSettingUI;

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
        PersistentState persistentState = PersistentStateService.getInstance(project).getState();
        CommonProperties commonProperties = persistentState.getGeneratorProperties().getCommonProperties();
        databases = commonProperties.getDatabases();

        // 设置数据库类型下拉框
        DataBaseTypeEnum.getDatabaseTypes().forEach(databaseType -> databaseTypeComboBox.addItem(databaseType));

        // 初始化数据
        if (Objects.nonNull(editDatabase)) {
            databaseTypeComboBox.setSelectedItem(StringUtils.trim(editDatabase.getDatabaseName()));
            hostTf.setText(StringUtils.trim(editDatabase.getHost()));
            portTf.setText(String.valueOf(editDatabase.getPort()));
            databaseNameTf.setText(StringUtils.trim(editDatabase.getDatabaseName()));
            userNameTf.setText(StringUtils.trim(editDatabase.getUserName()));
            passwordTf.setText(editDatabase.getPassword());
        }
    }

    /**
     * 创建事件监听器
     *
     * @param project 项目
     */
    private void initActionListener(Project project) {
        // 测试连接
        testBtn.addActionListener(e -> {
            Database formDatabase = getFormDatabase();
            if (!testConnectionDB(formDatabase)) {
                MyMessages.showWarningDialog(project, "数据库连接错误，请检查配置.", "Warning");
            } else {
                MyMessages.showInfoMessage(project, "Connection successful!", "Info");
            }
        });

        // 保存
        saveBtn.addActionListener(e -> {
            Database formDatabase = getFormDatabase();

            // 连接数据库测试
            if (!testConnectionDB(formDatabase)) {
                MyMessages.showWarningDialog(project, "数据库连接错误，请检查配置.", "Warning");
                return;
            }

            // 持久化
            databases.removeIf(next -> next.getDatabaseName().equals(formDatabase.getDatabaseName()));
            databases.add(formDatabase);

            // 刷新列表
            dataSourcesSettingUI.refreshDatabaseTable(databases);

            // 隐藏
            EditDatabaseSettingUI.this.dispose();
        });
    }

    /**
     * 获取表单数据库配置信息
     *
     * @return 数据库
     */
    private Database getFormDatabase() {
        Database database = new Database();
        database.setDatabaseType(StringUtils.trim(databaseTypeComboBox.getSelectedItem().toString()));
        database.setHost(StringUtils.trim(hostTf.getText()));
        database.setPort(Integer.valueOf(portTf.getText()));
        database.setDatabaseName(StringUtils.trim(databaseNameTf.getText()));
        database.setUserName(StringUtils.trim(userNameTf.getText()));
        database.setPassword(passwordTf.getText());
        return database;
    }

    /**
     * 测试连接数据库
     *
     * @param database 数据库
     */
    private boolean testConnectionDB(Database database) {
        try {
            MySQLDBHelper dbHelper = new MySQLDBHelper(database);
            dbHelper.testDatabase();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
