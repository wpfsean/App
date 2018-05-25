package com.zhketech.sip.app.project.client;

import android.app.Application;
import android.content.Intent;
import com.zhketech.sip.app.project.client.linphone.LinphoneService;

import static android.content.Intent.ACTION_MAIN;

public class App extends Application {

    private static App mMyApplition;

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApplition = this;
        startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
    }

    public static App getInstance() {
        return mMyApplition;
    }
}
