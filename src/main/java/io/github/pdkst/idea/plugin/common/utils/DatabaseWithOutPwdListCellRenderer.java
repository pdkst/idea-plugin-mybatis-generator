package io.github.pdkst.idea.plugin.common.utils;

import com.caojx.idea.plugin.common.pojo.DatabaseProperties;

import javax.swing.*;
import java.awt.*;

public class DatabaseWithOutPwdListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value != null) {
            DatabaseProperties database = (DatabaseProperties) value;
            setText(database.getIdentifierName());
        }
        return component;
    }
}
