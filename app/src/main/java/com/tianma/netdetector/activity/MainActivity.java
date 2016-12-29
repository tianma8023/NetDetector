package com.tianma.netdetector.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.tianma.netdetector.R;
import com.tianma.netdetector.lib.NetworkType;

public class MainActivity extends BaseActivity {

    private TextView networkStateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        networkStateTv = (TextView) findViewById(R.id.network_state_text);
    }

    @Override
    protected boolean needRegisterNetworkChangeObserver() {
        return true;
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        networkStateTv.setText(networkType.toString());
    }

    @Override
    public void onNetDisconnected() {
        networkStateTv.setText("No network");
    }
}
