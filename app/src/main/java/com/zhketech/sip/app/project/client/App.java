package com.zhketech.sip.app.project.client;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import com.zhketech.sip.app.project.client.linphone.LinphoneService;

import java.util.LinkedList;
import java.util.List;

import static android.content.Intent.ACTION_MAIN;

public class App extends Application {

    private static App mMyApplition;
    private static List<Activity> activityList = new LinkedList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApplition = this;
        startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
    }
    public static App getInstance() {
        return mMyApplition;
    }

    /**
     * 移除Activity
     */
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    /**
     * 添加activity
     * */
    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }
    /**
     * 移除所有的activity
     */
    public static void exit() {
        for (Activity activity : activityList) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        System.exit(0);
    }


}
