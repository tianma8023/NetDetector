package com.tianma.netdetector.activity;

import android.support.v7.app.AppCompatActivity;

import com.tianma.netdetector.lib.NetStateChangeObserver;
import com.tianma.netdetector.lib.NetStateChangeReceiver;
import com.tianma.netdetector.lib.NetworkType;

/**
 * @author Tianma at 2016/12/28
 */
public class BaseActivity extends AppCompatActivity implements NetStateChangeObserver {

    @Override
    protected void onResume() {
        super.onResume();
        if (needRegisterNetworkChangeObserver()) {
            NetStateChangeReceiver.registerObserver(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (needRegisterNetworkChangeObserver()) {
            NetStateChangeReceiver.unregisterObserver(this);
        }
    }

    /**
     * 是否需要注册网络变化的Observer,如果不需要监听网络变化,则返回false;否则返回true.默认返回false
     */
    protected boolean needRegisterNetworkChangeObserver() {
        return false;
    }

    @Override
    public void onNetDisconnected() {
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
    }
}
