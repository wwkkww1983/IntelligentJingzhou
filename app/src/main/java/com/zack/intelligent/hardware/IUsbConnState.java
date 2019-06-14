package com.zack.intelligent.hardware;

public interface IUsbConnState {
    void onUsbConnected();//USB已连接

	void onUsbPermissionDenied();//USB授权

	void onDeviceNotFound();//设备未找到
}
