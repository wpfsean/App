package com.zhketech.sip.app.project.client.callbacks;

import android.content.Context;
import android.text.TextUtils;

import com.zhketech.sip.app.project.client.global.AppConfig;
import com.zhketech.sip.app.project.client.utils.ByteUtils;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Root on 2018/5/16.
 * <p>
 * 用于申请开箱的回调
 */

public class AmmoRequestCallBack implements Runnable {
    AmmoCallBack listern;
    Context mContext;

    public AmmoRequestCallBack(AmmoCallBack listern ,Context mContext) {
        this.mContext = mContext;
        this.listern = listern;
    }

    @Override
    public void run() {
        synchronized (this) {
            Socket socket = null;
            byte[] request = new byte[68];
            //数据头
            String flage = "ReqB";
            byte[] flage1 = flage.getBytes();
            System.arraycopy(flage1, 0, request, 0, flage1.length);
            //版本号
            request[4] = 0;
            request[5] = 0;
            request[6] = 0;
            request[7] = 1;

            //请求的action
            request[8] = 0;
            request[9] = 0;
            request[10] = 0;
            request[11] = 0;
            System.out.println(Arrays.toString(request));

            //ulReserved1
            int ulReserved1 = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
            System.out.println(ulReserved1);
            byte[] flage2 = ByteUtils.toByteArray(ulReserved1);
            System.out.println(Arrays.toString(flage2));
            System.arraycopy(flage2, 0, request, 12, flage2.length);

            //ulReserved2
            int ulReserved2 = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
            System.out.println(ulReserved2);
            byte[] flage3 = ByteUtils.toByteArray(ulReserved2);
            System.out.println(Arrays.toString(flage3));
            System.arraycopy(flage3, 0, request, 16, flage3.length);

            byte[] sender = new byte[48];
            String guid = (String) SharedPreferencesUtils.getObject(mContext, AppConfig.GUID_NATIVE,"");
            if (!TextUtils.isEmpty(guid)){
                byte[] guidByte = guid.getBytes();
                System.arraycopy(guidByte,0,sender,0,guidByte.length);
                System.arraycopy(sender,0,request,20,48);
            }

            //打印请求数据主体
            System.out.println(Arrays.toString(request));
            try {
                socket = new Socket(AppConfig.ALERM_SERVER, AppConfig.ALERM_PORT);
                OutputStream os = socket.getOutputStream();
                os.write(request);
                os.flush();

                InputStream in = socket.getInputStream();
                byte[] headers = new byte[20];
                int read = in.read(headers);
                //解析action
                byte[] action = new byte[4];
                for (int i = 0; i < 4; i++) {
                    action[i] = headers[i + 8];
                }

                int status = ByteUtils.bytesToInt(action, 0);
                AppConfig.BOX_STATUS = status;
                Logutils.i("开箱状态：" + status);

                if (listern != null){
                    listern.onSuccess(status+"");
                }
            } catch (Exception e) {

                if (listern != null){
                    listern.onError("Exception:"+e.getMessage());
                }
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                        socket = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public interface AmmoCallBack {
        void onSuccess(String result);

        void onError(String error);
    }

    public void start(){
        new Thread(this).start();
    }
}
