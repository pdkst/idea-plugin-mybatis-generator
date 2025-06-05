package io.github.pdkst.idea.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.pdkst.idea.plugin.common.utils.RefreshDispatcher;
import io.github.pdkst.idea.plugin.common.utils.RefreshListener;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;

/**
 * @author pdkst
 * @since 2025/03/28
 */
public abstract class AbstractDialog extends DialogWrapper implements RefreshListener {
    private final Project project;
    /**
     * 刷新监听器
     */
    @Delegate
    private final RefreshDispatcher refreshDispatcher = new RefreshDispatcher();

    public AbstractDialog(@Nullable Project project) {
        super(project);
        this.project = project;
    }

    public AbstractDialog(@Nullable Project project, RefreshListener... listener) {
        this(project);
        refreshDispatcher.addListener(listener);
    }

    @Override
    public void refresh(Object... args) {
        // 被刷新时候执行
    }

    public void triggerRefresh(Object... args) {
        // 触发刷新时候执行
        refreshDispatcher.triggerRefresh(args);
    }
}
