package com.glc.lockclient;

import android.app.Application;

import com.glc.lockclient.utils.HeartbeatManager;
import com.glc.lockclient.utils.HeartbeatTask;

public class BaseApplication extends Application {
    private HeartbeatManager heartbeatManager;
    private HeartbeatTask heartbeatTask;
    // 静态实例
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // 初始化
        heartbeatManager = new HeartbeatManager();
        heartbeatTask = new HeartbeatTask(heartbeatManager);
        heartbeatTask.start();
    }

    // 获取全局的 BaseApplication 实例
    public static BaseApplication getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Application is not initialized");
        }
        return instance;
    }

    public HeartbeatManager getHeartbeatManager() {
        return heartbeatManager;
    }

    public void setHeartbeatManager(HeartbeatManager heartbeatManager) {
        this.heartbeatManager = heartbeatManager;
    }

    public HeartbeatTask getHeartbeatTask() {
        return heartbeatTask;
    }

    public void setHeartbeatTask(HeartbeatTask heartbeatTask) {
        this.heartbeatTask = heartbeatTask;
    }
}
