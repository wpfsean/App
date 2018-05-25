package com.zhketech.sip.app.project.client.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhketech.sip.app.project.client.MainActivity;
import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.linphone.LinphoneManager;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListener;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneInfoMessage;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PublishState;
import org.linphone.core.SubscriptionState;

import java.nio.ByteBuffer;

/**
 * 状态监听
 */


public class StatusActivity extends AppCompatActivity {

    ImageView statusLed;
    TextView statusText;
    Context mContext;
    LinphoneCoreListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mContext = this;
        statusLed = (ImageView) this.findViewById(R.id.image);
        statusText = (TextView) this.findViewById(R.id.status_s);
        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();

        if (lc != null) {
            Log.i("TAG", "not null");


            listener = new LinphoneCoreListener.LinphoneListener() {
                @Override
                public void authInfoRequested(LinphoneCore linphoneCore, String s, String s1, String s2) {

                }

                @Override
                public void callStatsUpdated(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCallStats linphoneCallStats) {

                }

                @Override
                public void newSubscriptionRequest(LinphoneCore linphoneCore, LinphoneFriend linphoneFriend, String s) {

                }

                @Override
                public void notifyPresenceReceived(LinphoneCore linphoneCore, LinphoneFriend linphoneFriend) {

                }

                @Override
                public void textReceived(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom, LinphoneAddress linphoneAddress, String s) {

                }

                @Override
                public void dtmfReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, int i) {

                }

                @Override
                public void notifyReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneAddress linphoneAddress, byte[] bytes) {

                }

                @Override
                public void transferState(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCall.State state) {

                }

                @Override
                public void infoReceived(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneInfoMessage linphoneInfoMessage) {

                }

                @Override
                public void subscriptionStateChanged(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, SubscriptionState subscriptionState) {

                }

                @Override
                public void publishStateChanged(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, PublishState publishState) {

                }

                @Override
                public void show(LinphoneCore linphoneCore) {

                }

                @Override
                public void displayStatus(LinphoneCore linphoneCore, String s) {

                }

                @Override
                public void displayMessage(LinphoneCore linphoneCore, String s) {

                }

                @Override
                public void displayWarning(LinphoneCore linphoneCore, String s) {

                }

                @Override
                public void fileTransferProgressIndication(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, int i) {

                }

                @Override
                public void fileTransferRecv(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, byte[] bytes, int i) {

                }

                @Override
                public int fileTransferSend(LinphoneCore linphoneCore, LinphoneChatMessage linphoneChatMessage, LinphoneContent linphoneContent, ByteBuffer byteBuffer, int i) {
                    return 0;
                }

                @Override
                public void callEncryptionChanged(LinphoneCore linphoneCore, LinphoneCall linphoneCall, boolean b, String s) {

                }

                @Override
                public void callState(LinphoneCore linphoneCore, LinphoneCall linphoneCall, LinphoneCall.State state, String s) {

                }

                @Override
                public void isComposingReceived(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom) {

                }

                @Override
                public void globalState(LinphoneCore linphoneCore, LinphoneCore.GlobalState globalState, String s) {

                }

                @Override
                public void uploadProgressIndication(LinphoneCore linphoneCore, int i, int i1) {

                }

                @Override
                public void uploadStateChanged(LinphoneCore linphoneCore, LinphoneCore.LogCollectionUploadState logCollectionUploadState, String s) {

                }

                @Override
                public void messageReceived(LinphoneCore linphoneCore, LinphoneChatRoom linphoneChatRoom, LinphoneChatMessage linphoneChatMessage) {

                }

                @Override
                public void notifyReceived(LinphoneCore linphoneCore, LinphoneEvent linphoneEvent, String s, LinphoneContent linphoneContent) {

                }

                @Override
                public void registrationState(LinphoneCore linphoneCore, LinphoneProxyConfig linphoneProxyConfig, LinphoneCore.RegistrationState registrationState, String s) {


                    Log.i("TAG", "//////////////////");

                    if (linphoneCore.getProxyConfigList() == null) {
                        statusText.setText("no_account");
                        Log.i("TAG", "\t\t\t\t\tstatusText.setText(getString(R.string.no_account));\n");
                    }

                    if (linphoneCore.getDefaultProxyConfig() != null && linphoneCore.getDefaultProxyConfig().equals(linphoneProxyConfig)) {
                        statusLed.setImageResource(getStatusIconResource(registrationState, true));
                        statusText.setText(getStatusIconText(registrationState));
                    } else if (linphoneCore.getDefaultProxyConfig() == null) {
                        statusLed.setImageResource(getStatusIconResource(registrationState, true));
                        statusText.setText(getStatusIconText(registrationState));
                    }


                }

                @Override
                public void configuringStatus(LinphoneCore linphoneCore, LinphoneCore.RemoteProvisioningState remoteProvisioningState, String s) {

                }
            };

            lc.addListener(listener);

        } else {
            Log.i("TAG", "null");
        }


    }


    private String getStatusIconText(LinphoneCore.RegistrationState state) {

        try {
            if (state == LinphoneCore.RegistrationState.RegistrationOk && LinphoneManager.getLcIfManagerNotDestroyedOrNull().getDefaultProxyConfig().isRegistered()) {
                return mContext.getString(R.string.status_connected);
            } else if (state == LinphoneCore.RegistrationState.RegistrationProgress) {
                return mContext.getString(R.string.status_in_progress);
            } else if (state == LinphoneCore.RegistrationState.RegistrationFailed) {
                return mContext.getString(R.string.status_error);
            } else {
                return mContext.getString(R.string.status_not_connected);
            }
        } catch (Exception e) {
            org.linphone.mediastream.Log.e(e);
        }

        return mContext.getString(R.string.status_not_connected);
    }

    private int getStatusIconResource(LinphoneCore.RegistrationState state, boolean isDefaultAccount) {
        try {
            LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
            boolean defaultAccountConnected = (isDefaultAccount && lc != null && lc.getDefaultProxyConfig() != null && lc.getDefaultProxyConfig().isRegistered()) || !isDefaultAccount;
            if (state == LinphoneCore.RegistrationState.RegistrationOk && defaultAccountConnected) {
                return R.drawable.led_connected;
            } else if (state == LinphoneCore.RegistrationState.RegistrationProgress) {
                return R.drawable.led_inprogress;
            } else if (state == LinphoneCore.RegistrationState.RegistrationFailed) {
                return R.drawable.led_error;
            } else {
                return R.drawable.led_disconnected;
            }
        } catch (Exception e) {
            org.linphone.mediastream.Log.e(e);
        }
        return R.drawable.led_disconnected;
    }

}
