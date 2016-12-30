package com.tianma.netdetector.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tianma.netdetector.R;
import com.tianma.netdetector.lib.NetworkType;

public class MainActivity extends BaseActivity {

    private TextView networkStateTv;
    private View netErrorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        networkStateTv = (TextView) findViewById(R.id.network_state_text);
        netErrorView = findViewById(R.id.net_error_view);
    }

    @Override
    protected boolean needRegisterNetworkChangeObserver() {
        return true;
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        networkStateTv.setText(networkType.toString());
        netErrorView.setVisibility(View.GONE);
    }

    @Override
    public void onNetDisconnected() {
        networkStateTv.setText("No network");
        netErrorView.setVisibility(View.VISIBLE);
    }
}
