package io.github.pdkst.idea.plugin.common.utils;


/**
 * @author pdkst.zhang
 * @since 2025/03/28 14:04
 */
@FunctionalInterface
public interface RefreshListener {
    /**
     * 刷新
     */
    void refresh(Object... args);
}
