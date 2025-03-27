package io.github.pdkst.idea.plugin.common.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TableInfoTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(Boolean.TRUE.equals(value));
        checkBox.setHorizontalAlignment(JCheckBox.CENTER);
        if (isSelected) {
            checkBox.setForeground(table.getSelectionForeground());
            checkBox.setBackground(table.getSelectionBackground());
        } else {
            checkBox.setForeground(table.getForeground());
            checkBox.setBackground(table.getBackground());
        }
        return checkBox;
    }
}
