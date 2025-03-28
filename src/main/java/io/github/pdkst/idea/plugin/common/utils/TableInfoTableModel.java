package io.github.pdkst.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.pojo.TableInfo;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableInfoTableModel extends DefaultTableModel {
    private static final String[] TABLE_COLUMN_NAME = {"选择", "表名", "注释"};
    private List<TableInfo> dataList = new ArrayList<>();
    private Set<TableInfo> selectedSet = new HashSet<>();

    public TableInfoTableModel() {
        super(null, TABLE_COLUMN_NAME);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // 只允许编辑第一列
        return column == 0;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (columnIndex == 0) ? Boolean.class : String.class;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
        if (column == 0) {
            TableInfo tableInfo = dataList.get(row);
            if ((boolean) aValue) {
                selectedSet.add(tableInfo);
                refreshData();
            }
        }
    }

    /**
     * 清空数据的方法
     */
    public void clearData() {
        if (dataList.isEmpty()) {
            return;
        }
        dataList = new ArrayList<>();
        // 清空选中集合
        selectedSet = new HashSet<>();
        refreshData();
    }

    public void setDataList(List<TableInfo> tableInfos) {
        dataList = new ArrayList<>(tableInfos);
        refreshData();
    }

    private void refreshData() {
        setDataVector(buildDataArray(), TABLE_COLUMN_NAME);
    }

    private Object[][] buildDataArray() {
        Object[][] rowArray = new Object[dataList.size()][3];
        for (int i = 0; i < dataList.size(); i++) {
            TableInfo tableInfo = dataList.get(i);
            rowArray[i][0] = selectedSet.contains(tableInfo); // 检查是否在选中集合中
            rowArray[i][1] = tableInfo.getName();
            rowArray[i][2] = tableInfo.getComment();
        }
        return rowArray;
    }

    public List<String> getSelectedTableNames() {
        if (selectedSet == null || selectedSet.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> selectedTableNames = new ArrayList<>();
        for (TableInfo selectedRow : selectedSet) {
            selectedTableNames.add(selectedRow.getName());
        }
        return selectedTableNames;
    }
}
