package com.example.apputil.sync;

/**
 * 监听
 */
public interface ISyncService {
    void addListener(String dataId, String group, SyncListener listener);

    boolean publish(String dataId, String group, String content);

    void removeListener(String dataId, String group, SyncListener listener);
}
