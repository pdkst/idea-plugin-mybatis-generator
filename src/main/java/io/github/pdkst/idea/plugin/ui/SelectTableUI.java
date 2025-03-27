package io.github.pdkst.idea.plugin.ui;

import com.caojx.idea.plugin.common.pojo.DatabaseWithOutPwd;
import com.caojx.idea.plugin.common.pojo.DatabaseWithPwd;
import com.caojx.idea.plugin.common.pojo.TableInfo;
import com.caojx.idea.plugin.common.properties.EntityProperties;
import com.caojx.idea.plugin.common.properties.GeneratorProperties;
import com.caojx.idea.plugin.common.utils.DatabaseConvert;
import com.caojx.idea.plugin.common.utils.MyMessages;
import com.caojx.idea.plugin.generator.AbstractGeneratorService;
import com.caojx.idea.plugin.generator.GeneratorContext;
import com.caojx.idea.plugin.generator.GeneratorServiceImpl;
import com.caojx.idea.plugin.generator.IGeneratorService;
import com.caojx.idea.plugin.persistent.PersistentExtConfig;
import com.caojx.idea.plugin.persistent.PersistentStateService;
import com.caojx.idea.plugin.ui.DataSourcesSettingUI;
import com.caojx.idea.plugin.ui.GeneratorSettingUI;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.pdkst.idea.plugin.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectTableUI extends DialogWrapper {
    private final Project project;
    private final PersistentStateService persistentStateService;

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

        // 初始化界面
        initUI();
    }

    private void initUI() {
        dataModel = new TableInfoTableModel();
        table.setModel(dataModel);
        databaseComboBox.setRenderer(new DatabaseWithOutPwdListCellRenderer());
        List<DatabaseWithOutPwd> extDatabases = PersistentExtConfig.loadDatabase();
        initDatabaseComBox(extDatabases, null);

        // 设置监听
        queryTableBtn.addActionListener(e -> {
            searchTables();
        });
        configDataBaseBtn.addActionListener(e -> {
            // 打开数据库配置界面
            DataSourcesSettingUI dataSourcesSettingUI = new DataSourcesSettingUI(project);
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

    private void searchTables() {
        DatabaseWithOutPwd database = (DatabaseWithOutPwd) databaseComboBox.getSelectedItem();
        if (database == null) {
            MyMessages.showWarningDialog(project, "请选择一个数据库", "Warning");
            return;
        }
        try {
            DatabaseWithPwd databaseWithPwd = convertDatabaseWithPwd(database);
            Database mysql = DBHelper.getMySql(databaseWithPwd, new HashMap<>(4));

            String tableNamePattern = StringUtils.isBlank(tableNameRegexTf.getText()) ? "%" : "%" + tableNameRegexTf.getText() + "%";
            List<TableInfo> tableList = mysql.getTables(tableNamePattern);

            dataModel.clearData();

            int rows = Math.min(tableList.size(), 30);
            for (int i = 0; i < rows; i++) {
                dataModel.addData(tableList.get(i));
            }

            // 设置列为复选框
            TableColumn tableColumn = table.getColumnModel().getColumn(0);
            tableColumn.setCellRenderer(new TableInfoTableCellRenderer());
            tableColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));

            tableColumn.setMaxWidth(100);
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
    private DatabaseWithPwd convertDatabaseWithPwd(DatabaseWithOutPwd database) {
        String password = persistentStateService.getPassword(database.getIdentifierName());
        return DatabaseConvert.convertDatabaseWithPwd(database, password);
    }

    public void generateCode() {
        // 生成代码
        // 获取代码生成配置
        GeneratorProperties generatorProperties = persistentStateService.getState().getGeneratorProperties();
        // 获取表列表
        int[] selectedRows = table.getSelectedRows();
        List<String> selectedTableNames = dataModel.getSelectedTableNames(selectedRows);
        if (CollectionUtils.isEmpty(selectedTableNames)) {
            MyMessages.showWarningDialog(project, "请选择要生成的表", "info");
            return;
        }
        DatabaseWithPwd database = (DatabaseWithPwd) databaseComboBox.getSelectedItem();
        List<TableInfo> tables = getTables(database, generatorProperties.getEntityProperties(), selectedTableNames);

        // 校验数据
        String message = AbstractGeneratorService.validGeneratorData(generatorProperties);
        if (StringUtils.isNotBlank(message)) {
            MyMessages.showWarningDialog(project, message, "info");
            return;
        }

        // 表校验
        if (CollectionUtils.isEmpty(selectedTableNames)) {
            MyMessages.showInfoMessage(project, "生成代码执行完成", "info");
            return;
        }

        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setTables(tables);
        generatorContext.setGeneratorProperties(generatorProperties);

        // 生成代码
        generatorService.doGenerator(project, generatorContext);
        MyMessages.showInfoMessage(project, "生成代码执行完成", "info");
    }

    private List<TableInfo> getTables(DatabaseWithPwd databaseConfig, EntityProperties entityProperties, List<String> selectedTableNames) {
        try {
            Map<String, String> customerJdbcTypeMappingMap = entityProperties.getCustomerJdbcTypeMappingMap();
            Database database = DBHelper.getMySql(databaseConfig, JdbcTypeUtils.toJdbcTypeMap(customerJdbcTypeMappingMap));
            return database.getTables(selectedTableNames);
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
    private void initDatabaseComBox(List<DatabaseWithOutPwd> databases, String selectedShowDatabaseName) {
        // 数据库为空
        databaseComboBox.removeAllItems();

        // 初始化下拉列表，默认选中0号数据库
        if (CollectionUtils.isNotEmpty(databases)) {
            for (DatabaseWithOutPwd database : databases) {
                databaseComboBox.addItem(database);
            }
            databaseComboBox.setSelectedItem(databases.get(0).getIdentifierName());
        }

        // 设置为选中的数据库
        boolean selectedDatabaseChange = true;
        for (DatabaseWithOutPwd database : databases) {
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
