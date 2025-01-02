package com.glc.lockclient.utils;

import java.util.concurrent.ConcurrentHashMap;

public class HeartbeatManager {
    private final ConcurrentHashMap<String, Long> deviceHeartbeatMap = new ConcurrentHashMap<>();
    private static final long HEARTBEAT_TIMEOUT = 10000; // 超时时间：10秒

    Listener listener;
    // 接收心跳消息
    public void receiveHeartbeat(String deviceId) {
        deviceHeartbeatMap.put(deviceId, System.currentTimeMillis());
    }

    // 检查设备是否在线
    public boolean isDeviceOnline(String deviceId) {
        Long lastHeartbeat = deviceHeartbeatMap.get(deviceId);
        if (lastHeartbeat == null) {
            return false;
        }
        return System.currentTimeMillis() - lastHeartbeat <= HEARTBEAT_TIMEOUT;
    }

    // 清理离线设备
    public void checkAndCleanOfflineDevices() {
        long currentTime = System.currentTimeMillis();
        for (String deviceId : deviceHeartbeatMap.keySet()) {
            Long lastHeartbeat = deviceHeartbeatMap.get(deviceId);
            if (lastHeartbeat == null || currentTime - lastHeartbeat > HEARTBEAT_TIMEOUT) {
                System.out.println("Device " + deviceId + " is offline.");
                deviceHeartbeatMap.remove(deviceId);
                listener.off();
            }
        }
    }



    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

   public interface Listener{
        void off();
    }
}
