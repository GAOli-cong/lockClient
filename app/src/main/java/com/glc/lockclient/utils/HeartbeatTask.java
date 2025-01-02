package com.glc.lockclient.utils;

import android.os.Handler;

public class HeartbeatTask {
    private final Handler handler = new Handler();
    private final HeartbeatManager heartbeatManager;

    public HeartbeatTask(HeartbeatManager manager) {
        this.heartbeatManager = manager;
    }

    // 定时检测设备状态
    private final Runnable checkOfflineDevicesTask = new Runnable() {
        @Override
        public void run() {
            heartbeatManager.checkAndCleanOfflineDevices(); // 检查离线设备
            handler.postDelayed(this, 5000); // 每5秒检查一次
        }
    };

    // 启动任务
    public void start() {
        handler.post(checkOfflineDevicesTask);
    }

    // 停止任务
    public void stop() {
        handler.removeCallbacks(checkOfflineDevicesTask);
    }
}
