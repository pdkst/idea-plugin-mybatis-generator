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

    /**
     * 刷新监听器
     */
    @Delegate
    private final RefreshDispatcher refreshDispatcher = new RefreshDispatcher();

    public AbstractDialog(@Nullable Project project) {
        super(project);
    }

    public AbstractDialog(@Nullable Project project, RefreshListener... listener) {
        super(project);
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
