package io.github.pdkst.idea.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import io.github.pdkst.idea.plugin.ui.SelectTableUI;

/**
 * 代码生成Action
 *
 * @author pdkst
 * @since 2025年4月12日12:28:03
 */
public class GeneratorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 项目
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        // 显示配置窗体
        SelectTableUI generatorSettingUI = new SelectTableUI(project);
        generatorSettingUI.show();
    }
}
