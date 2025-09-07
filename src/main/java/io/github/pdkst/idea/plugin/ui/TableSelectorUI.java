package io.github.pdkst.idea.plugin.ui;

import com.caojx.idea.plugin.common.pojo.TableInfo;
import com.caojx.idea.plugin.common.properties.EntityProperties;
import com.caojx.idea.plugin.common.properties.GeneratorProperties;
import com.caojx.idea.plugin.common.utils.MyMessages;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.pdkst.idea.plugin.common.pojo.DatabaseProperties;
import io.github.pdkst.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import io.github.pdkst.idea.plugin.common.utils.Database;
import io.github.pdkst.idea.plugin.common.utils.DatabaseHelper;
import io.github.pdkst.idea.plugin.common.utils.DatabaseWithOutPwdListCellRenderer;
import io.github.pdkst.idea.plugin.common.utils.JdbcTypeUtils;
import io.github.pdkst.idea.plugin.common.utils.MysqlDatabaseTableResolver;
import io.github.pdkst.idea.plugin.common.utils.TableInfoTableModel;
import io.github.pdkst.idea.plugin.common.utils.TableResolver;
import io.github.pdkst.idea.plugin.generator.GeneratorContext;
import io.github.pdkst.idea.plugin.generator.GeneratorService;
import io.github.pdkst.idea.plugin.generator.engin.FreemarkerTemplateEngine;
import io.github.pdkst.idea.plugin.state.DatabaseListStateService;
import io.github.pdkst.idea.plugin.state.DatabaseStateService;
import io.github.pdkst.idea.plugin.state.GlobalPersistentStateService;
import io.github.pdkst.idea.plugin.state.PersistentStateService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableSelectorUI extends DialogWrapper {
    // 生成代码业务接口
    private final GeneratorService generatorService = new GeneratorService(new FreemarkerTemplateEngine());

    private final Project project;
    private final PersistentStateService persistentStateService;
    private final GlobalPersistentStateService globalPersistentStateService;
    private final DatabaseListStateService databaseListStateService;
    private final DatabaseStateService databaseStateService;

    // 界面
    private JPanel contentPane;
    private JComboBox<DatabaseSensitiveProperties> databaseComboBox;
    private JTextField tfTableNameRegex;
    private JTextField tfTablePrefix;
    private JTextField tfIdentifyPattern;
    private JButton btnQueryTable;
    private JButton btnConfigDataBase;
    // 选择生成的类
    private JCheckBox entityGenerateCheckBox;
    private JCheckBox mapperGenerateCheckBox;
    private JCheckBox serviceGenerateCheckBox;
    private JCheckBox facadeGenerateCheckBox;
    private JCheckBox entityExampleGenerateCheckBox;
    private JCheckBox controllerGenerateCheckBox;
    private JCheckBox mapperXmlGenerateCheckBox;
    private JCheckBox serviceImplGenerateCheckBox;
    private JCheckBox facadeImplGenerateCheckBox;
    // 数据库表列表
    private JTable table;
    private TableInfoTableModel dataModel;
    // 按钮
    private JButton btnGenerate;
    private JButton btnGeneratorSetting;
    private JButton btnCancel;

    public TableSelectorUI(Project project) {
        super(project); // use current window as parent
        init();
        setTitle("数据库表选择");
        this.project = project;
        this.persistentStateService = PersistentStateService.getInstance(project);
        this.globalPersistentStateService = GlobalPersistentStateService.getInstance();
        this.databaseListStateService = DatabaseListStateService.getInstance();
        this.databaseStateService = project.getService(DatabaseStateService.class);
        // 初始化界面
        initData();
        initListener();
    }

    private void initData() {
        initProperties();
        dataModel = new TableInfoTableModel();
        table.setModel(dataModel);
        refreshDatabaseTable();
        databaseComboBox.setRenderer(new DatabaseWithOutPwdListCellRenderer());
    }

    private void initProperties() {
        GeneratorProperties generatorProperties = persistentStateService.getState().getGeneratorProperties();
        this.entityGenerateCheckBox.setSelected(generatorProperties.getEntityProperties().isSelectedGenerateCheckBox());
        this.entityExampleGenerateCheckBox.setSelected(
                generatorProperties.getEntityProperties().isSelectedGenerateEntityExampleCheckBox());
        this.mapperGenerateCheckBox.setSelected(generatorProperties.getMapperProperties().isSelectedGenerateCheckBox());
        this.mapperXmlGenerateCheckBox.setSelected(
                generatorProperties.getMapperXmlProperties().isSelectedGenerateCheckBox());
        this.serviceGenerateCheckBox.setSelected(
                generatorProperties.getServiceProperties().isSelectedGenerateCheckBox());
        this.serviceImplGenerateCheckBox.setSelected(
                generatorProperties.getServiceImplProperties().isSelectedGenerateCheckBox());
        this.facadeGenerateCheckBox.setSelected(generatorProperties.getFacadeProperties().isSelectedGenerateCheckBox());
        this.facadeImplGenerateCheckBox.setSelected(
                generatorProperties.getFacadeImplProperties().isSelectedGenerateCheckBox());
        this.controllerGenerateCheckBox.setSelected(
                generatorProperties.getControllerProperties().isSelectedGenerateCheckBox());
    }

    private void saveProperties() {
        databaseStateService.setTablePrefix(tfTablePrefix.getText());
        databaseStateService.setIdentifyPattern(tfIdentifyPattern.getText());
        final GeneratorProperties generatorProperties = persistentStateService.getState().getGeneratorProperties();
        generatorProperties.getEntityProperties().setSelectedGenerateCheckBox(entityGenerateCheckBox.isSelected());
        generatorProperties.getEntityProperties()
                .setSelectedGenerateEntityExampleCheckBox(entityExampleGenerateCheckBox.isSelected());
        generatorProperties.getMapperProperties().setSelectedGenerateCheckBox(mapperGenerateCheckBox.isSelected());
        generatorProperties.getMapperXmlProperties()
                .setSelectedGenerateCheckBox(mapperXmlGenerateCheckBox.isSelected());
        generatorProperties.getServiceProperties().setSelectedGenerateCheckBox(serviceGenerateCheckBox.isSelected());
        generatorProperties.getServiceImplProperties()
                .setSelectedGenerateCheckBox(serviceImplGenerateCheckBox.isSelected());
        generatorProperties.getFacadeProperties().setSelectedGenerateCheckBox(facadeGenerateCheckBox.isSelected());
        generatorProperties.getFacadeImplProperties()
                .setSelectedGenerateCheckBox(facadeImplGenerateCheckBox.isSelected());
        generatorProperties.getControllerProperties()
                .setSelectedGenerateCheckBox(controllerGenerateCheckBox.isSelected());
        project.save();
    }

    private void initListener() {
        databaseComboBox.addActionListener(e -> {
            DatabaseSensitiveProperties database = (DatabaseSensitiveProperties) databaseComboBox.getSelectedItem();
            if (database == null) {
                return;
            }
            // 重置表数据
            dataModel.clearData();
            databaseStateService.setCurrentDatabase(database.getIdentifierName());
        });
        // 设置表名正则输入框，键释放的时候
        tfTableNameRegex.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // 只监听enter
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                searchTables();
            }
        });
        tfTablePrefix.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                final JTextField source = (JTextField) e.getSource();
                databaseStateService.setTablePrefix(source.getText());
            }
        });
        tfIdentifyPattern.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                final JTextField source = (JTextField) e.getSource();
                databaseStateService.setIdentifyPattern(source.getText());
            }
        });
        // 设置监听
        btnQueryTable.addActionListener(e -> {
            searchTables();
        });
        btnConfigDataBase.addActionListener(e -> {
            // 打开数据库配置界面
            DataSourcesListUI dataSourcesListUI = new DataSourcesListUI(project);
            dataSourcesListUI.addListener(args -> {
                refreshDatabaseTable();
            });
            dataSourcesListUI.show();
        });
        // 跳转到生成代码配置页面
        btnGeneratorSetting.addActionListener(e -> {
            // 打开数据库配置界面
            saveProperties();
            GeneratorSettingUI generatorSettingUI = new GeneratorSettingUI(project);
            generatorSettingUI.show();
        });
        btnCancel.addActionListener(e -> {
            // 取消
            dispose();
        });
        btnGenerate.addActionListener(e -> {
            // 生成代码
            saveProperties();
            generateCode();
        });
    }

    private void refreshDatabaseTable() {
        initProperties();
        List<DatabaseSensitiveProperties> extDatabases = databaseListStateService.getDatabases();
        initDatabaseComBox(extDatabases, databaseStateService.getCurrentDatabase());
        tfTablePrefix.setText(ObjectUtils.defaultIfNull(databaseStateService.getTablePrefix(), "t_"));
    }

    private void searchTables() {
        saveProperties();
        DatabaseSensitiveProperties database = (DatabaseSensitiveProperties) databaseComboBox.getSelectedItem();
        if (database == null) {
            MyMessages.showWarningDialog(project, "请选择一个数据库", "Warning");
            return;
        }
        try {
            Database mysql = DatabaseHelper.getMySql(database);

            String tableNamePattern = StringUtils.isBlank(
                    tfTableNameRegex.getText()) ? "%" : "%" + tfTableNameRegex.getText() + "%";
            final TableResolver tableResolver = new MysqlDatabaseTableResolver(mysql,
                    databaseStateService.getIdentifyPattern());
            List<TableInfo> tableList = tableResolver.getTables(tableNamePattern);

            dataModel.setDataList(tableList);
        } catch (Exception ex) {
            MyMessages.showWarningDialog(project, "数据库连接错误,请检查配置.", "Warning");
        }
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
        DatabaseSensitiveProperties database = (DatabaseSensitiveProperties) databaseComboBox.getSelectedItem();
        List<TableInfo> tables = getTables(database, generatorProperties.getEntityProperties(), selectedTableNames);

        // 校验数据
        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setTables(tables);
        generatorContext.setGeneratorProperties(generatorProperties);
        generatorContext.setGlobalPersistentState(globalPersistentStateService.getState());
        generatorContext.setDatabaseState(databaseStateService.getState());
        String message = GeneratorService.validGeneratorData(generatorContext);
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
            Database database = DatabaseHelper.getMySql(databaseConfig);
            final TableResolver tableResolver = new MysqlDatabaseTableResolver(database,
                    JdbcTypeUtils.toJdbcTypeMap(customerJdbcTypeMappingMap), databaseStateService.getIdentifyPattern());
            return tableResolver.getTablesAndFields(selectedTableNames);
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
    private void initDatabaseComBox(List<DatabaseSensitiveProperties> databases, String selectedShowDatabaseName) {
        // 数据库为空
        databaseComboBox.removeAllItems();

        // 初始化下拉列表，默认选中0号数据库
        if (CollectionUtils.isNotEmpty(databases)) {
            for (DatabaseSensitiveProperties database : databases) {
                databaseComboBox.addItem(database);
            }
            databaseComboBox.setSelectedItem(databases.get(0));
        }

        // 设置为选中的数据库
        boolean selectedDatabaseChange = true;
        for (DatabaseProperties database : databases) {
            if (StringUtils.equals(database.getIdentifierName(), selectedShowDatabaseName)) {
                selectedDatabaseChange = false;
                databaseComboBox.setSelectedItem(database);
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
