package io.github.pdkst.idea.plugin.common.utils;

import io.github.pdkst.idea.plugin.common.pojo.DatabaseSensitiveProperties;

import javax.swing.*;
import java.awt.*;

public class DatabaseWithOutPwdListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value != null) {
            DatabaseSensitiveProperties database = (DatabaseSensitiveProperties) value;
            setText(database.getIdentifierName());
        }
        return component;
    }


}
