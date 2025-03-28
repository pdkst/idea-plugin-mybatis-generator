package io.github.pdkst.idea.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.pdkst.idea.plugin.common.utils.RefreshDispatcher;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;

/**
 * @author pdkst
 * @since 2025/03/28
 */
public abstract class AbstractDialog extends DialogWrapper {

    /**
     * 刷新监听器
     */
    @Delegate
    private final RefreshDispatcher refreshDispatcher = new RefreshDispatcher();

    public AbstractDialog(@Nullable Project project) {
        super(project);
    }
}
