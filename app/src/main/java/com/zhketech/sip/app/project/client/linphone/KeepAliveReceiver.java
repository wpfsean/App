package com.zhketech.sip.app.project.client.linphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.linphone.mediastream.Log;
public class KeepAliveReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!LinphoneService.isReady()) {
            return;
        } else {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
                LinphoneManager.getLc().enableKeepAlive(true);
            } else if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
                LinphoneManager.getLc().enableKeepAlive(false);
            }
        }

    }

}
