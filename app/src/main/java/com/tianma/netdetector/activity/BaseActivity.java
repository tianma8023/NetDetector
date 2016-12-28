package com.tianma.netdetector.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tianma.netdetector.lib.NetStateChangeObserver;
import com.tianma.netdetector.lib.NetStateChangeReceiver;
import com.tianma.netdetector.lib.NetworkType;

/**
 * @author Tianma at 2016/12/28
 */

public class BaseActivity extends AppCompatActivity implements NetStateChangeObserver {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkChangeObserver();
    }

    /**
     * 注册网络变化Observer
     */
    protected void registerNetworkChangeObserver() {
        NetStateChangeReceiver.registerObserver(this);
    }

    /**
     * 取消网络变化Observer注册
     */
    protected void unregisterNetworkChangeObserver() {
        NetStateChangeReceiver.unregisterObserver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterNetworkChangeObserver();
    }


    @Override
    public void onNetDisconnected() {

    }

    @Override
    public void onNetConnected(NetworkType networkType) {

    }
}
