package io.github.pdkst.idea.plugin.common.utils;


import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author pdkst.zhang
 * @since 2025/03/28 14:04
 */
@Data
public class RefreshDispatcher {
    private static final RefreshDispatcher INSTANCE = new RefreshDispatcher();
    private List<RefreshListener> refreshList;

    public RefreshDispatcher() {
        refreshList = new ArrayList<>();
    }

    public RefreshDispatcher(List<RefreshListener> refreshList) {
        this.refreshList = refreshList;
    }

    public void addListener(RefreshListener... refreshListeners) {
        refreshList.addAll(Arrays.asList(refreshListeners));
    }

    public void removeListener(RefreshListener refreshListener) {
        refreshList.remove(refreshListener);
    }

    public void triggerRefresh(Object... args) {
        refreshList.forEach(refreshListener -> refreshListener.refresh(args));
    }
}
