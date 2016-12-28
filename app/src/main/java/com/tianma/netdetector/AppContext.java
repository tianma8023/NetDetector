package com.tianma.netdetector;

import android.app.Application;

import com.tianma.netdetector.lib.NetStateChangeReceiver;

/**
 * @author Tianma at 2016/12/28
 */

public class AppContext extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        // 注册BroadcastReceiver
        NetStateChangeReceiver.registerReceiver(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 取消BroadcastReceiver注册
        NetStateChangeReceiver.unregisterReceiver(this);
    }
}
