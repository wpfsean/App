package com.zhketech.sip.app.project.client.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.global.AppConfig;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.PhoneUtils;
import com.zhketech.sip.app.project.client.utils.SharedPreferencesUtils;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置中心界面
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    //手动注册一个sip到服务器
    @BindView(R.id.register_sip)
    public Button register_sip;

    //查看权限按钮
    @BindView(R.id.setting_permission)
    public Button setting_permission;

    //显示信息的tv
    @BindView(R.id.show_information_register)
    public TextView show_information;
    Context mContext = null;
    StringBuilder stringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideStatusBar();
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mContext = this;
        initViewAndListern();
    }


    @Override
    protected void onStart() {
        super.onStart();
        stringBuilder.append(
                "本机信息：\n sip_Name:" + SharedPreferencesUtils.getObject(mContext, AppConfig.SIP_NAME_NAVITE, "")
                        + "\n sip_Number:" + SharedPreferencesUtils.getObject(mContext, AppConfig.SIP_NUMBER_NAVITE, "")
                        + "\n sip_Pass:" + SharedPreferencesUtils.getObject(mContext, AppConfig.SIP_PASS_NAVITE, "")
                        + "\n 本机Ip:" + SharedPreferencesUtils.getObject(mContext, AppConfig.IP_NAVITE, "")
                        + "\n 服务器Ip:" + AppConfig.IP_SERVER
                        + "\n Sip服务器Ip：" + SharedPreferencesUtils.getObject(mContext, AppConfig.SIP_SERVER, "")
                        + "\n 本机Rtsp:" + SharedPreferencesUtils.getObject(mContext, AppConfig.NATIVE_RTSP, "")
                        + "\n 开锁信息:" + SharedPreferencesUtils.getObject(mContext, AppConfig.BOX_STATUS, "")
                        + "\n 最后报警状态：" + AppConfig.ALARM_STATUS
                        + "\n 本机的Guiid：" + SharedPreferencesUtils.getObject(mContext, AppConfig.GUID_NATIVE, "")
                        + "\n 本机Name：" + SharedPreferencesUtils.getObject(mContext, AppConfig.NAME_NATIVE, "")
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(stringBuilder.toString())) {
            show_information.setText(stringBuilder.toString());
        } else {
            show_information.setText("未获取到本机信息");
        }
    }


    /**
     * 初始化控件
     */
    private void initViewAndListern() {
        register_sip.setOnClickListener(this);
        setting_permission.setOnClickListener(this);
        stringBuilder = new StringBuilder();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        hideStatusBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stringBuilder.delete(0, stringBuilder.length());
    }

    /**
     * 隐藏状态栏和actionBar
     */
    protected void hideStatusBar() {
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.INVISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.register_sip:


                break;

            case R.id.setting_permission:
                PhoneUtils.goToSetting(mContext);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        System.gc();
    }
}
