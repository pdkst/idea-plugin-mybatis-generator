package io.github.pdkst.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.pojo.DatabaseProperties;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pdkst
 * @since 2025/03/28
 */
public class DatabaseTableModel extends DefaultTableModel {
    private final List<DatabaseProperties> databases = new ArrayList<>();

    /**
     * 表头
     */
    private static final String[] TABLE_COLUMN_NAME = {"database", "host", "port", "type"};

    public DatabaseTableModel() {
        super(null, TABLE_COLUMN_NAME);
    }

    public void setDatabases(List<DatabaseProperties> databases) {
        this.databases.clear();
        this.databases.addAll(databases);
        setDataVector(buildDataArray(), TABLE_COLUMN_NAME);
    }

    private Object[][] buildDataArray() {
        Object[][] rowArray = new Object[databases.size()][4];
        for (int i = 0; i < databases.size(); i++) {
            DatabaseProperties database = databases.get(i);
            rowArray[i][0] = database.getDatabaseName();
            rowArray[i][1] = database.getHost();
            rowArray[i][2] = database.getPort();
            rowArray[i][3] = database.getDatabaseType();
        }
        return rowArray;
    }
}
