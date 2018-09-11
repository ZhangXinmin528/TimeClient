package com.zxm.timeclient;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zxm.libclient.SocketClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private EditText mIpEt;
    private EditText mPortEt;
    private EditText mMsgEt;
    private TextView mInfoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParamsAndValues();
        initViews();
    }

    private void initParamsAndValues() {
        mContext = this;
    }

    private void initViews() {
        mIpEt = findViewById(R.id.et_input_ip);
        mPortEt = findViewById(R.id.et_inout_port);
        mMsgEt = findViewById(R.id.et_inout_msg);
        findViewById(R.id.btn_build_connect).setOnClickListener(this);
        findViewById(R.id.btn_build_close).setOnClickListener(this);
        findViewById(R.id.btn_build_reconnect).setOnClickListener(this);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_post_face).setOnClickListener(this);

        mInfoTv = findViewById(R.id.tv_info);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_build_connect:
                //客户端并建立连接
                final String ip = mIpEt.getText().toString().trim();
                final String port = mPortEt.getText().toString().trim();
                SocketClient
                        .getInstance()
                        .onConfig(ip, Integer.parseInt(port))
                        .onConnect();
                break;
            //断开连接
            case R.id.btn_build_close:
                break;
            //重新连接
            case R.id.btn_build_reconnect:
                break;
            case R.id.btn_start://发送开始命令
                break;
            case R.id.btn_exit://发送退出命令
                break;
            case R.id.btn_post_face://发送图片
                break;
        }
    }

}
