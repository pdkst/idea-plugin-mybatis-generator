package io.github.pdkst.idea.plugin.ui;

import com.caojx.idea.plugin.common.pojo.DatabaseProperties;
import com.caojx.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import com.caojx.idea.plugin.common.pojo.TableInfo;
import com.caojx.idea.plugin.common.properties.EntityProperties;
import com.caojx.idea.plugin.common.properties.GeneratorProperties;
import com.caojx.idea.plugin.common.utils.MyMessages;
import com.caojx.idea.plugin.generator.AbstractGeneratorService;
import com.caojx.idea.plugin.generator.GeneratorContext;
import com.caojx.idea.plugin.generator.GeneratorServiceImpl;
import com.caojx.idea.plugin.generator.IGeneratorService;
import com.caojx.idea.plugin.persistent.PersistentExtConfig;
import com.caojx.idea.plugin.persistent.PersistentStateService;
import com.caojx.idea.plugin.ui.GeneratorSettingUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.pdkst.idea.plugin.common.utils.Database;
import io.github.pdkst.idea.plugin.common.utils.DatabaseHelper;
import io.github.pdkst.idea.plugin.common.utils.DatabaseWithOutPwdListCellRenderer;
import io.github.pdkst.idea.plugin.common.utils.JdbcTypeUtils;
import io.github.pdkst.idea.plugin.common.utils.PasswordUtils;
import io.github.pdkst.idea.plugin.common.utils.TableInfoTableModel;
import io.github.pdkst.idea.plugin.persistent.GlobalPersistentStateService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectTableUI extends DialogWrapper {
    private final Project project;
    private final PersistentStateService persistentStateService;
    private final GlobalPersistentStateService globalPersistentStateService;

    /**
     * 生成代码业务接口
     */
    private IGeneratorService generatorService = new GeneratorServiceImpl();

    // 界面
    private JPanel contentPane;
    private JComboBox databaseComboBox;
    private JTextField tableNameRegexTf;
    private JButton queryTableBtn;
    private JButton configDataBaseBtn;
    // 数据库表列表
    private JTable table;
    private TableInfoTableModel dataModel;
    // 按钮
    private JButton btnGenerate;
    private JButton btnGeneratorSetting;
    private JButton btnCancel;

    public SelectTableUI(Project project) {
        super(project); // use current window as parent
        init();
        setTitle("数据库表选择");
        this.project = project;
        this.persistentStateService = PersistentStateService.getInstance(project);
        this.globalPersistentStateService = GlobalPersistentStateService.getInstance();
        // 初始化界面
        initData();
        initListener();
    }

    private void initData() {
        dataModel = new TableInfoTableModel();
        table.setModel(dataModel);
        databaseComboBox.setRenderer(new DatabaseWithOutPwdListCellRenderer());
        refreshDatabaseTable();
    }

    private void initListener() {
        // 设置监听
        queryTableBtn.addActionListener(e -> {
            searchTables();
        });
        configDataBaseBtn.addActionListener(e -> {
            // 打开数据库配置界面
            DataSourcesSettingUI dataSourcesSettingUI = new DataSourcesSettingUI(project);
            dataSourcesSettingUI.addListener(args -> {
                refreshDatabaseTable();
            });
            dataSourcesSettingUI.show();
        });
        // 跳转到生成代码配置页面
        btnGeneratorSetting.addActionListener(e -> {
            // 打开数据库配置界面
            GeneratorSettingUI generatorSettingUI = new GeneratorSettingUI(project);
            generatorSettingUI.show();
        });
        btnCancel.addActionListener(e -> {
            // 取消
            dispose();
        });
        btnGenerate.addActionListener(e -> {
            // 生成代码
            generateCode();
        });
    }

    private void refreshDatabaseTable() {
        List<DatabaseProperties> extDatabases = PersistentExtConfig.loadDatabase();
        initDatabaseComBox(extDatabases, null);
    }

    private void searchTables() {
        DatabaseProperties database = (DatabaseProperties) databaseComboBox.getSelectedItem();
        if (database == null) {
            MyMessages.showWarningDialog(project, "请选择一个数据库", "Warning");
            return;
        }
        try {
            DatabaseSensitiveProperties databaseWithPwd = convertDatabaseWithPwd(database);
            Database mysql = DatabaseHelper.getMySql(databaseWithPwd, new HashMap<>(4));

            String tableNamePattern = StringUtils.isBlank(
                    tableNameRegexTf.getText()) ? "%" : "%" + tableNameRegexTf.getText() + "%";
            List<TableInfo> tableList = mysql.getTables(tableNamePattern);

            dataModel.setDataList(tableList);
        } catch (Exception ex) {
            MyMessages.showWarningDialog(project, "数据库连接错误,请检查配置.", "Warning");
        }
    }

    /**
     * 转换为带密码的数据库信息
     *
     * @param database 数据库信息
     * @return 带密码的数据库信息
     */
    private DatabaseSensitiveProperties convertDatabaseWithPwd(DatabaseProperties database) {
        String password = PasswordUtils.getPassword(database.getIdentifierName());
        return new DatabaseSensitiveProperties(database, password);
    }

    public void generateCode() {
        // 生成代码
        // 获取代码生成配置
        GeneratorProperties generatorProperties = persistentStateService.getState().getGeneratorProperties();
        // 获取表列表
        List<String> selectedTableNames = dataModel.getSelectedTableNames();
        if (CollectionUtils.isEmpty(selectedTableNames)) {
            MyMessages.showWarningDialog(project, "请选择要生成的表", "info");
            return;
        }
        DatabaseProperties database = (DatabaseProperties) databaseComboBox.getSelectedItem();
        DatabaseSensitiveProperties databaseWithPwd = new DatabaseSensitiveProperties(database,
                PasswordUtils.getPassword(database.getIdentifierName()));
        List<TableInfo> tables = getTables(databaseWithPwd, generatorProperties.getEntityProperties(),
                selectedTableNames);

        // 校验数据
        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setTables(tables);
        generatorContext.setGeneratorProperties(generatorProperties);
        generatorContext.setGlobalPersistentState(globalPersistentStateService.getState());
        String message = AbstractGeneratorService.validGeneratorData(generatorContext);
        if (StringUtils.isNotBlank(message)) {
            MyMessages.showWarningDialog(project, message, "info");
            return;
        }

        // 表校验
        if (CollectionUtils.isEmpty(selectedTableNames)) {
            MyMessages.showInfoMessage(project, "生成代码执行完成", "info");
            return;
        }

        // 生成代码
        generatorService.doGenerator(project, generatorContext);
        MyMessages.showInfoMessage(project, "生成代码执行完成", "info");
    }

    private List<TableInfo> getTables(DatabaseSensitiveProperties databaseConfig,
                                      EntityProperties entityProperties,
                                      List<String> selectedTableNames) {
        try {
            Map<String, String> customerJdbcTypeMappingMap = entityProperties.getCustomerJdbcTypeMappingMap();
            Database database = DatabaseHelper.getMySql(databaseConfig,
                    JdbcTypeUtils.toJdbcTypeMap(customerJdbcTypeMappingMap));
            return database.getTablesAndFields(selectedTableNames);
        } catch (SQLException e) {
            MyMessages.showWarningDialog(project, "获取表信息失败", "info");
            return new ArrayList<>();
        }
    }

    /**
     * 初始化数据库下拉框
     *
     * @param databases                数据库列表
     * @param selectedShowDatabaseName 选中的数据库名
     */
    private void initDatabaseComBox(List<DatabaseProperties> databases, String selectedShowDatabaseName) {
        // 数据库为空
        databaseComboBox.removeAllItems();

        // 初始化下拉列表，默认选中0号数据库
        if (CollectionUtils.isNotEmpty(databases)) {
            for (DatabaseProperties database : databases) {
                databaseComboBox.addItem(database);
            }
            databaseComboBox.setSelectedItem(databases.get(0));
        }

        // 设置为选中的数据库
        boolean selectedDatabaseChange = true;
        for (DatabaseProperties database : databases) {
            if (StringUtils.equals(database.getIdentifierName(), selectedShowDatabaseName)) {
                selectedDatabaseChange = false;
                databaseComboBox.setSelectedItem(selectedShowDatabaseName);
            }
        }

        // 数据库选择有变化，重置表数据
        if (selectedDatabaseChange) {
            // 重置表数据
            dataModel.clearData();
        }
    }


    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    protected JComponent createSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout());
        // 重置配置
        btnGenerate = new JButton("生成代码");
        southPanel.add(btnGenerate);

        // 保存配置
        btnGeneratorSetting = new JButton("配置");
        southPanel.add(btnGeneratorSetting);

        // 取消配置
        btnCancel = new JButton("取消");
        southPanel.add(btnCancel);
        return southPanel;
    }
}
