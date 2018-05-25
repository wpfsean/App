package com.zhketech.sip.app.project.client.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.zhketech.sip.app.project.client.callbacks.SendHearToServer;
import com.zhketech.sip.app.project.client.utils.ByteUtils;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.PhoneUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 广播接收定时任务用于发送心跳
 * Created by Root on 2018/4/27.
 */

public class HttpRequest extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        List<String> points = new ArrayList<String>();
        String deviceId = PhoneUtils.getPhoneInfo(context, 1);
        if (!TextUtils.isEmpty(deviceId) && deviceId != null) {
            points.add(deviceId);
        } else {
            points.add("111111");
        }
        long stamp = PhoneUtils.dateStamp();
        byte[] timeByte = ByteUtils.longToBytes(stamp);
        SendHearToServer hearToServer = new SendHearToServer(points, timeByte);
        new Thread(hearToServer).start();
        Logutils.i("time:" + new Date().toString());
        Intent i = new Intent(context, SendheartService.class);
        context.startService(i);
    }
}
