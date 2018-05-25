package com.zhketech.sip.app.project.client.linphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.linphone.mediastream.Log;

public class KeepAliveHandler extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (LinphoneManager.getLcIfManagerNotDestroyedOrNull() != null) {
			//first refresh registers
			LinphoneManager.getLc().refreshRegisters();
			//make sure iterate will have enough time, device will not sleep until exit from this method
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			
		}

	}

}
