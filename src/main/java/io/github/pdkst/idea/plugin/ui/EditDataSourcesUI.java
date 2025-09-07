package io.github.pdkst.idea.plugin.ui;

import com.caojx.idea.plugin.common.utils.MyMessages;
import com.intellij.openapi.project.Project;
import io.github.pdkst.idea.plugin.common.enums.DataBaseTypeEnum;
import io.github.pdkst.idea.plugin.common.pojo.DatabaseProperties;
import io.github.pdkst.idea.plugin.common.pojo.DatabaseSensitiveProperties;
import io.github.pdkst.idea.plugin.common.utils.Database;
import io.github.pdkst.idea.plugin.common.utils.DatabaseHelper;
import io.github.pdkst.idea.plugin.common.utils.RefreshListener;
import io.github.pdkst.idea.plugin.state.DatabaseListStateService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 编辑数据库
 *
 * @author pdkst
 * @since 2025-03-28 22:59:01
 */
public class EditDataSourcesUI extends AbstractDialog {
    private JPanel mainPanel;
    private JComboBox<DataBaseTypeEnum> databaseTypeComboBox;
    private JTextField hostTf;
    private JTextField portTf;
    private JTextField databaseNameTf;
    private JTextField userNameTf;
    private JPasswordField passwordTf;

    private JButton saveBtn;
    private JButton testBtn;
    private JTextField urlTf;

    private final Project project;
    private final DatabaseListStateService databaseListStateService;

    /**
     * 正在编辑的数据库
     */
    private final DatabaseSensitiveProperties editDatabase;


    public EditDataSourcesUI(@NotNull Project project,
                             @Nullable DatabaseSensitiveProperties editDatabase,
                             RefreshListener... refreshListeners) {
        super(project, refreshListeners);
        init();

        this.project = project;
        this.editDatabase = toNewInstance(editDatabase);
        this.databaseListStateService = DatabaseListStateService.getInstance();

        // 初始化界面数据
        initData();

        // 创建事件监听器
        initActionListener(project);
    }

    private static @Nullable DatabaseSensitiveProperties toNewInstance(@Nullable DatabaseSensitiveProperties editDatabase) {
        if (editDatabase == null) {
            return null;
        }
        return new DatabaseSensitiveProperties(editDatabase);
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
     */
    private void initData() {

        // 设置数据库类型下拉框
        for (DataBaseTypeEnum databaseType : DataBaseTypeEnum.values()) {
            databaseTypeComboBox.addItem(databaseType);
        }

        // 初始化数据
        if (Objects.nonNull(editDatabase)) {
            databaseTypeComboBox.setSelectedItem(
                    DataBaseTypeEnum.getEnumByDatabaseType(editDatabase.getDatabaseType()));
            hostTf.setText(StringUtils.trim(editDatabase.getHost()));
            portTf.setText(String.valueOf(editDatabase.getPort()));
            databaseNameTf.setText(StringUtils.trim(editDatabase.getDatabaseName()));
            userNameTf.setText(StringUtils.trim(editDatabase.getUserName()));

            passwordTf.setText(editDatabase.getPassword());

            if (StringUtils.isNotBlank(editDatabase.getUrl())) {
                urlTf.setText(editDatabase.getUrl());
            } else {
                urlTf.setText(buildURL(hostTf.getText(), portTf.getText(), databaseNameTf.getText(), ""));
            }
        }
    }

    /**
     * 构建数据库连接url
     *
     * @param host         host
     * @param port         port
     * @param dataBaseName 数据库名称
     * @return 数据库连接url
     */
    private String buildURL(String host, String port, String dataBaseName, String propertiesStr) {
        final DataBaseTypeEnum selectedItem = (DataBaseTypeEnum) databaseTypeComboBox.getSelectedItem();
        String url = "jdbc:" + selectedItem.getDatabaseType() + "://" + host + ":" + port + "/" + dataBaseName;
        if (StringUtils.isNotBlank(propertiesStr)) {
            return url + "?" + propertiesStr;
        }
        return url;
    }

    /**
     * 例如："jdbc:mysql://localhost:3306/xxxx?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior
     * =convertToNull&useSSL=false"
     * 转为数据库配置信息
     *
     * @param url      url
     * @param userName 用户名
     * @return 数据库配置信息
     */
    private DatabaseProperties parseDatabaseProperties(String url, String userName) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        // 使用正则命名分组解析
        final Pattern pattern = Pattern.compile("jdbc:(?<databaseType>\\w+)://(?<host>[\\w.]+):(?<port>\\d+)/(?<databaseName>\\w+)(\\?(?<properties>.*))?");
        final Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            return null;
        }
        final String databaseType = matcher.group("databaseType");
        final String host = matcher.group("host");
        final String port = matcher.group("port");
        final String databaseName = matcher.group("databaseName");

        // 属性
        DatabaseProperties databaseWithOutPwd = new DatabaseProperties();
        databaseWithOutPwd.setDatabaseType(ObjectUtils.defaultIfNull(StringUtils.lowerCase(databaseType), DataBaseTypeEnum.MySQL.getDatabaseType()));
        databaseWithOutPwd.setHost(host);
        databaseWithOutPwd.setPort(Integer.parseInt(port));
        databaseWithOutPwd.setDatabaseName(databaseName);
        databaseWithOutPwd.setUserName(userName);
        return databaseWithOutPwd;
    }

    /**
     * 提取属性信息字符串
     *
     * @param url jdbc url
     * @return 属性信息
     */
    private String extractPropertiesStr(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }

        // 属性
        String[] propertiesSplit = url.split("\\?");
        if (propertiesSplit.length > 1) {
            return propertiesSplit[1];
        }
        return "";
    }

    /**
     * 创建事件监听器
     *
     * @param project 项目
     */
    private void initActionListener(Project project) {
        databaseTypeComboBox.addActionListener(e -> {
            final DataBaseTypeEnum selectedItem = (DataBaseTypeEnum) databaseTypeComboBox.getSelectedItem();
            if (Objects.isNull(selectedItem)) {
                return;
            }
            resetUrl();
        });

        hostTf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                resetUrl();
            }
        });

        portTf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                resetUrl();
            }
        });

        databaseNameTf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                resetUrl();
            }
        });

        urlTf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                DatabaseProperties databaseWithOutPwd = parseDatabaseProperties(urlTf.getText(), userNameTf.getText());
                if (Objects.isNull(databaseWithOutPwd)) {
                    databaseTypeComboBox.setSelectedItem(DataBaseTypeEnum.MySQL);
                    hostTf.setText("");
                    portTf.setText("");
                    databaseNameTf.setText("");
                    return;
                } else {
                    final String databaseType = databaseWithOutPwd.getDatabaseType();
                    databaseTypeComboBox.setSelectedItem(DataBaseTypeEnum.getEnumByDatabaseType(databaseType));
                    hostTf.setText(StringUtils.isNotBlank(databaseWithOutPwd.getHost()) ? StringUtils.trim(
                            databaseWithOutPwd.getHost()) : "");
                    portTf.setText(Objects.nonNull(databaseWithOutPwd.getPort()) ? String.valueOf(
                            databaseWithOutPwd.getPort()) : "");
                    databaseNameTf.setText(
                            StringUtils.isNotBlank(databaseWithOutPwd.getDatabaseName()) ? StringUtils.trim(
                                    databaseWithOutPwd.getDatabaseName()) : "");
                }
            }
        });


        // 测试连接
        testBtn.addActionListener(e -> {
            DatabaseSensitiveProperties formDatabase = getFormDatabase();
            if (Objects.isNull(formDatabase) || !testConnectionDB(formDatabase)) {
                MyMessages.showWarningDialog(project, "数据库连接错误，请检查配置.", "Warning");
            } else {
                MyMessages.showInfoMessage(project, "Connection successful!", "Info");
            }
        });

        // 保存
        saveBtn.addActionListener(e -> {

            DatabaseSensitiveProperties formDatabase = getFormDatabase();

            // 连接数据库测试
            if (!testConnectionDB(formDatabase)) {
                MyMessages.showWarningDialog(project, "数据库连接错误，请检查配置.", "Warning");
                return;
            }
            databaseListStateService.replaceByIdentify(formDatabase);

            // 刷新列表
            triggerRefresh();

            // 隐藏
            EditDataSourcesUI.this.dispose();
        });
    }

    private void resetUrl() {
        String propertiesStr = extractPropertiesStr(urlTf.getText());
        urlTf.setText(buildURL(hostTf.getText(), portTf.getText(), databaseNameTf.getText(), propertiesStr));
    }

    /**
     * 获取表单数据库配置信息
     *
     * @return 数据库
     */
    private DatabaseSensitiveProperties getFormDatabase() {
        DatabaseProperties databaseWithOutPwd = parseDatabaseProperties(urlTf.getText(),
                StringUtils.trim(userNameTf.getText()));
        if (Objects.isNull(databaseWithOutPwd)) {
            return null;
        }
        DatabaseSensitiveProperties database = new DatabaseSensitiveProperties();
        final String databaseType = databaseWithOutPwd.getDatabaseType();
        database.setDatabaseType(StringUtils.firstNonBlank(databaseType, DataBaseTypeEnum.MySQL.getDatabaseType()));
        database.setHost(databaseWithOutPwd.getHost());
        database.setPort(databaseWithOutPwd.getPort());
        database.setDatabaseName(databaseWithOutPwd.getDatabaseName());
        database.setUserName(databaseWithOutPwd.getUserName());
        database.setPassword(passwordTf.getText());
        return database;
    }

    /**
     * 测试连接数据库
     *
     * @param databaseWithPwd 数据库
     */
    private boolean testConnectionDB(DatabaseSensitiveProperties databaseWithPwd) {
        final Database mySql = DatabaseHelper.getMySql(databaseWithPwd);
        return mySql.testConnection();
    }

    @Override
    public void triggerRefresh(Object... args) {
        super.triggerRefresh(args);
        databaseListStateService.save();
    }
}
