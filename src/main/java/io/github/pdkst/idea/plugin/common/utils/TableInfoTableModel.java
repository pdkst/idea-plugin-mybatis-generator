package io.github.pdkst.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.pojo.TableInfo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableInfoTableModel extends AbstractTableModel {
    private final String[] columnNames = {"选择", "表名", "注释"};
    private final List<TableInfo> dataList = new ArrayList<>();
    private final Set<TableInfo> selectedSet = new HashSet<>(); // 新增 Set 用于保存选中的值

    @Override
    public int getRowCount() {
        return dataList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TableInfo tableInfo = dataList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return selectedSet.contains(tableInfo); // 检查是否在选中集合中
            case 1:
                return tableInfo.getName();
            case 2:
                return tableInfo.getComment();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            TableInfo tableInfo = dataList.get(rowIndex);
            if ((Boolean) aValue) {
                selectedSet.add(tableInfo); // 添加到选中集合
            } else {
                selectedSet.remove(tableInfo); // 从选中集合中移除
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0; // 只有第一列可编辑
    }

    /**
     * 清空数据的方法
     */
    public void clearData() {
        if (dataList.isEmpty()) {
            return;
        }
        int oldSize = dataList.size();
        dataList.clear();
        selectedSet.clear(); // 清空选中集合
        // 通知表格模型数据已经改变
        fireTableRowsDeleted(0, oldSize - 1);
    }

    public void addData(TableInfo tableInfo) {
        dataList.add(tableInfo);
        fireTableRowsInserted(dataList.size() - 1, dataList.size() - 1);
    }

    public List<String> getSelectedTableNames(int[] selectedRows) {
        if (selectedRows == null || selectedRows.length == 0) {
            return new ArrayList<>();
        }
        List<String> selectedTableNames = new ArrayList<>();
        for (int row : selectedRows) {
            TableInfo tableInfo = dataList.get(row);
            selectedTableNames.add(tableInfo.getName());
        }
        return selectedTableNames;
    }
}
