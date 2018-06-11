package com.zhketech.sip.app.project.client.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhketech.sip.app.project.client.MainActivity;
import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.beans.AlarmBen;
import com.zhketech.sip.app.project.client.beans.DeviceInfor;
import com.zhketech.sip.app.project.client.beans.SipBean;
import com.zhketech.sip.app.project.client.beans.VideoBen;
import com.zhketech.sip.app.project.client.callbacks.AmmoRequestCallBack;
import com.zhketech.sip.app.project.client.callbacks.ReceiveServerMess;
import com.zhketech.sip.app.project.client.callbacks.ReceiverServerAlarm;
import com.zhketech.sip.app.project.client.callbacks.RequestVideoSourcesThread;
import com.zhketech.sip.app.project.client.callbacks.ResolveRtsp;
import com.zhketech.sip.app.project.client.callbacks.SipRequestCallback;
import com.zhketech.sip.app.project.client.global.AppConfig;
import com.zhketech.sip.app.project.client.linphone.LinphoneService;
import com.zhketech.sip.app.project.client.linphone.PhoneServiceCallBack;
import com.zhketech.sip.app.project.client.linphone.PhoneVoiceUtils;
import com.zhketech.sip.app.project.client.linphone.SipStatusCallBack;
import com.zhketech.sip.app.project.client.linphone.Utility;
import com.zhketech.sip.app.project.client.service.SendheartService;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.PhoneUtils;
import com.zhketech.sip.app.project.client.utils.SharedPreferencesUtils;
import com.zhketech.sip.app.project.client.utils.ToastUtils;


import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 主界面（包括6个功能按钮）
 * 1.音频对讲
 * 2.电话
 * 3.设置
 * 4.视频
 * 5.报警
 * 6.申请供弹
 */
public class Main extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.main_relativeLayout)
    public RelativeLayout main_relativeLayout;

    //显示时间
    @BindView(R.id.main_incon_time)
    public TextView main_incon_time;

    //显示日期
    @BindView(R.id.main_icon_date)
    public TextView main_icon_date;

    //对讲
    @BindView(R.id.button_intercom)
    public ImageButton button_intercom;
    //电话
    @BindView(R.id.button_phone)
    public ImageButton button_phone;
    //设置
    @BindView(R.id.button_setup)
    public ImageButton button_setup;
    //视频
    @BindView(R.id.button_video)
    public ImageButton button_video;
    //报警
    @BindView(R.id.button_alarm)
    public ImageButton button_alarm;
    //申请供弹
    @BindView(R.id.button_applyforplay)
    public ImageButton button_appplyforplay;
    //震动对象
    Vibrator mVibrator = null;
    Context mContext = null;
    List<SipBean> sipResources = new ArrayList<>();
    List<SipBean> sipListResourcesRtsp = new ArrayList<>();

    List<VideoBen> videoResources = new ArrayList<>();
    List<VideoBen> videoListResourcesRtsp = new ArrayList<>();
    String rtsp = "";
    String rtsp1 = "";

    int num = 1;

    /**
     * 接收子线程发来的消息
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AppConfig.SMS_FLAGE://短信
                    final String mes = (String) msg.obj;
                    Logutils.i("sms：" + mes);
                    initAlerdialog(mes, null);
                    break;
                case AppConfig.ALERT_FLAGE://报警报文
                    AlarmBen alarmBen = (AlarmBen) msg.obj;
                    Logutils.i("alarm:" + alarmBen.toString());
                    if (alarmBen != null) initAlerdialog("", alarmBen);
                    else Logutils.i("数据失败");
                    break;
                case 3://主页面时的时间日期
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat timeD = new SimpleDateFormat("HH:mm:ss");
                    main_incon_time.setText(timeD.format(date));
                    SimpleDateFormat dateD = new SimpleDateFormat("MM月dd日 EEE");
                    main_icon_date.setText(dateD.format(date));
                    break;
                case AppConfig.VIDEO_SOURCES_FLAGE://videoResources
                    List<VideoBen> mList = (List<VideoBen>) msg.obj;
                    if (mList != null && mList.size() > 0) {
                        videoResources = mList;
                        Logutils.i("videoResources:" + videoResources.size());
                    }
                    if (videoResources != null && videoResources.size() > 0) {
                        for (VideoBen v : videoResources) {
                            Logutils.i("v:" + v.toString());
                            resolveRtspUrl(v);
                        }
                    } else {
                        Logutils.i("VideoSources:" + "null");
                    }

                    break;

                case 1001: //获取带rtsp的videoResources
                    //通过bundle获取数据
                    Bundle bundle = msg.getData();
                    String url = bundle.getString("url");
                    VideoBen v = (VideoBen) bundle.getSerializable("videoben");
                    DeviceInfor device = (DeviceInfor) bundle.getSerializable("device");
                    //判断解析rtsp是否正确
                    if (!url.equals("error") && url != null) {
                        if (!url.contains("@")) {
                            //拼加带用户和密码的rtsp地址
                            String[] flage = url.split("//");
                            String header = flage[0];
                            String footer = flage[1];
                            String newUrl = header + "//" + v.getUsername() + ":" + v.getPassword() + "@" + footer;
                            rtsp = newUrl;
                        }
                    } else {
                        rtsp = "rtsp://admin:pass@192.168.0.93:554/H264?ch=1&subtype=1&proto=Onvif";
                    }

                    if (device.isSuporrtPtz() == true) {
                        v.setPtz_url(device.getPtz_url());
                        v.setSuporrtPtz(true);
                    }
                    Logutils.i("token:" + device.getToken());
                    v.setToken(device.getToken());
                    v.setRtsp(rtsp);
                    videoListResourcesRtsp.add(v);
                    if (videoListResourcesRtsp.size() == videoResources.size()) {
                        Gson gson = new Gson();
                        String json1 = gson.toJson(videoListResourcesRtsp);
                        if (json1 != null && !TextUtils.isEmpty(json1)) {
                            SharedPreferencesUtils.putObject(mContext, "video_result", json1);
                            Logutils.i("video资源已保存" + videoListResourcesRtsp.toString());
                        } else {
                            Logutils.i("Tag:" + "video未添加到本地");
                        }
                    }
                    break;

                case AppConfig.SIP_SOURCES_FLAGE://sipResources
                    List<SipBean> sipBeansList = (List<SipBean>) msg.obj;
                    if (sipBeansList != null && sipBeansList.size() > 0) {
                        sipResources = sipBeansList;
                        Logutils.i("sipResources...." + sipResources.size());
                        if (sipResources != null && sipResources.size() > 0) {
                            registeSipToServer();
                            for (SipBean s : sipResources) {
                                resolveSipRtspUrl(s);
                            }
                        }
                    } else {
                        Logutils.i("未解析到sip资源");
                    }
                    break;
                case 1003:
                    Bundle rtsp_bundle = msg.getData();
                    String rtsp_url = rtsp_bundle.getString("rtsp_url");
                    VideoBen rtsp_videoben = (VideoBen) rtsp_bundle.getSerializable("videobean");
                    rtsp_videoben.setRtsp(rtsp_url);
                    videoListResourcesRtsp.add(rtsp_videoben);

                    break;

                case 1002://解析sip里面的rtsp
                    Bundle sipBundle = msg.getData();
                    String sipUrl = sipBundle.getString("url");
                    SipBean mSipbean = (SipBean) sipBundle.getSerializable("sipBean");
                    DeviceInfor sipDevice = (DeviceInfor) sipBundle.getSerializable("device");
                    if (!sipUrl.equals("error") && sipUrl != null) {
                        if (!sipUrl.contains("@")) {
                            //拼加带用户和密码的rtsp地址
                            String[] flage = sipUrl.split("//");
                            String header = flage[0];
                            String footer = flage[1];
                            String newUrl = header + "//" + mSipbean.getVideoBen().getUsername() + ":" + mSipbean.getVideoBen().getPassword() + "@" + footer;
                            rtsp1 = newUrl;

                        }
                    } else {
                        //未解析到rtsp时就添加一个默认的rtsp(方便测试)
                        rtsp1 = "rtsp://admin:pass@192.168.0.93:554/H264?ch=1&subtype=1&proto=Onvif";
                    }
                    if (sipDevice.isSuporrtPtz() == true) {
                        mSipbean.setSuporrtPtz(true);
                        mSipbean.setPtz_url(sipDevice.getPtz_url());
                    }
                    mSipbean.setToken(sipDevice.getToken());
                    mSipbean.setRtsp(rtsp1);
                    sipListResourcesRtsp.add(mSipbean);
                    if (sipResources.size() == sipListResourcesRtsp.size()) {
                        //  Logutils.i("Tag:" + "哈哈，把Sip的rtsp解析完了------" + new Date().toString());
                        // Logutils.i("Tag:" + "哈哈，把Sip的rtsp解析完了------" + sipListResourcesRtsp.toString());
                        Gson gson = new Gson();
                        String json2 = gson.toJson(sipListResourcesRtsp);
                        if (json2 != null && !TextUtils.isEmpty(json2))
                            SharedPreferencesUtils.putObject(mContext, "sip_result", json2);
                        Logutils.i("sip资源已保存" + sipListResourcesRtsp.toString());
                        //LogU.info(sipListResourcesRtsp.toString());
                    } else {
                        //Logutils.i("Tag:" + "Sip资源未添加到本地");
                    }
                    break;

                case AppConfig.GET_AMMOREQUEST_INFOR:

                    String result = (String) msg.obj;
                    Toast.makeText(Main.this, "开锁信息:" + result, Toast.LENGTH_SHORT).show();


                    break;
            }
        }
    };

    //整个项目可能用到的权限
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.USE_SIP,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE
    };
    //存放未同意 的权限
    List<String> mPermissionList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideStatusBar();
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        mContext = this;
        //初始化控件
        initPageViewAndListern();
        //判断当前的版本是否大于6.0，用于权限的 申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //申请权限
            requestPermission();
        } else {
            //不用申请权限，加载数据
            initPageData();
        }
    }


    /**
     * 解析rtsp(根据deviceType: ONVIF　ＲTSP )
     */
    public synchronized void resolveRtspUrl(final VideoBen v) {
        if (v.getDevicetype().equals("ONVIF")) {
            DeviceInfor deviceInfor = new DeviceInfor();
            deviceInfor.setUsername(v.getUsername());
            deviceInfor.setPassword(v.getPassword());
            deviceInfor.setChannel(v.getChannel());
            deviceInfor.setServiceURL("http://" + v.getIp() + "/onvif/device_service");
            ResolveRtsp resolveRtsp = new ResolveRtsp(deviceInfor);
            resolveRtsp.setOnHttpSoapListener(new ResolveRtsp.OnHttpSoapListener() {
                @Override
                public void OnHttpSoapDone(DeviceInfor camera, String uri, boolean success) {
                    Message message = new Message();
                    message.what = 1001;
                    Bundle bundle = new Bundle();
                    bundle.putString("url", uri);
                    bundle.putSerializable("videoben", v);
                    bundle.putSerializable("device", camera);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            });
            resolveRtsp.start();
        } else if (v.getDevicetype().equals("RTSP")) {
            String rtsp_url = "rtsp://" + v.getUsername() + ":" + v.getPassword() + "@" + v.getIp() + ":" + v.getPort() + "/" + v.getChannel();
            Message message = new Message();
            message.what = 1003;
            Bundle bundle = new Bundle();
            bundle.putString("rtsp_url", rtsp_url);
            bundle.putSerializable("videobean", v);
            message.setData(bundle);
            handler.sendMessage(message);
        }


    }


    /**
     * 解析sip资源中的rtsp地址
     *
     * @param sipBean
     */
    public void resolveSipRtspUrl(final SipBean sipBean) {

        DeviceInfor deviceInfor = new DeviceInfor();
        deviceInfor.setUsername(sipBean.getVideoBen().getUsername());
        deviceInfor.setPassword(sipBean.getVideoBen().getPassword());
        deviceInfor.setChannel(sipBean.getVideoBen().getChannel());
        deviceInfor.setServiceURL("http://" + sipBean.getVideoBen().getIp() + "/onvif/device_service");
        //Logutils.i("deviceInfor:"+deviceInfor.toString());
        ResolveRtsp resolveRtsp = new ResolveRtsp(deviceInfor);
        resolveRtsp.setOnHttpSoapListener(new ResolveRtsp.OnHttpSoapListener() {
            @Override
            public void OnHttpSoapDone(DeviceInfor camera, String uri, boolean success) {
                Message message = new Message();
                message.what = 1002;
                Bundle bundle = new Bundle();
                bundle.putString("url", uri);
                bundle.putSerializable("sipBean", sipBean);
                bundle.putSerializable("device", camera);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
        resolveRtsp.start();
    }

    /**
     * 初始化本页面的数据
     */
    private void initPageData() {
        String native_ip = PhoneUtils.displayIpAddress(mContext);
        if (!TextUtils.isEmpty(native_ip) && native_ip != null) {
            SharedPreferencesUtils.putObject(mContext, AppConfig.IP_NAVITE, native_ip);
        } else {
            Logutils.i("未获取到本机的ip信息");
        }

        //起线程用于时间显示
        TimeThread timeThread = new TimeThread();
        new Thread(timeThread).start();
        //初始化一个震动对象
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        PhoneUtils.keepScreenOn(mContext, true);
    }


    /**
     * 权限申请
     */
    private void requestPermission() {
        mPermissionList.clear();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        } /** * 判断存储委授予权限的集合是否为空 */
        if (!mPermissionList.isEmpty()) {
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(Main.this, permissions, 1);
        } else {
            //未授予的权限为空，表示都授予了 // 后续操作...
            initPageData();
        }
    }

    boolean mShowRequestPermission = true;//用户是否禁止权限

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(Main.this, permissions[i]);
                        if (showRequestPermission) {//
                            requestPermission();//重新申请权限
                            return;
                        } else {
                            mShowRequestPermission = false;//已经禁止
                            String permisson = permissions[i];
                            android.util.Log.i("TAG", "permisson:" + permisson);
                        }
                    }
                }
                initPageData();
                break;
            default:
                break;
        }
    }


    /**
     * 接收到短消息时弹出dialog
     *
     * @param ms 短消息
     */
    public void initAlerdialog(final String ms, final AlarmBen alarmBen) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                if (ms != null && !TextUtils.isEmpty(ms) && alarmBen == null)
                    builder.setTitle("短消息：").setMessage(ms).setNegativeButton("Cancal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                else if (alarmBen != null && TextUtils.isEmpty(ms))
                    builder.setTitle("报警报文:").setMessage(alarmBen.toString()).setNegativeButton("Cancal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                ;
                builder.create().show();
            }
        });
    }



    /**
     * 初始化控件或监听
     */
    private void initPageViewAndListern() {
        button_intercom.setOnClickListener(this);
        button_phone.setOnClickListener(this);
        button_setup.setOnClickListener(this);
        button_video.setOnClickListener(this);
        button_alarm.setOnClickListener(this);
        button_appplyforplay.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();

        LinphoneService.addSipStatusCallBack(new SipStatusCallBack() {
            @Override
            public void registrationState(LinphoneCore.RegistrationState registrationState) {

                Logutils.i("infor:"+registrationState.toString());
            }
        });

    }


    /**
     * 注册sip到服务器
     */
    private void registeSipToServer() {
        String phoneIp = (String) SharedPreferencesUtils.getObject(mContext, AppConfig.IP_NAVITE, "");
        Logutils.i("phone_ip");
        if (!TextUtils.isEmpty(phoneIp) && phoneIp != "") {
            for (SipBean s : sipResources) {
                if (s.getIp().equals(phoneIp)) {
                    String sipNumber = s.getNumber();
                    String sipServer = s.getSipserver();
                    String sipName = s.getSipname();
                    String sipPass = s.getSippass();

                    if (!TextUtils.isEmpty(sipServer) && !TextUtils.isEmpty(sipName) && !TextUtils.isEmpty(sipPass)) {

                        LinphoneService.addCallBack(new PhoneServiceCallBack() {
                            @Override
                            public void registrationState(LinphoneCore.RegistrationState registrationState) {
                                if ("RegistrationOk".equals(registrationState.toString())) {
                                    Logutils.i("已注册成功");
                                } else if ("RegistrationFailed".equals(registrationState.toString())){
                                    Logutils.i("注册失败");
                                }
                            }
                        });
                        Utility.setUsername(sipNumber);
                        Utility.setPassword(sipPass);
                        Utility.setHost(sipServer);
                        PhoneVoiceUtils.getInstance().setAudiPort(7078);
                        try {
                            PhoneVoiceUtils.getInstance().registerUserAuth(Utility.getUsername(), Utility.getPassword(), Utility.getHost());
                        } catch (LinphoneCoreException e) {
                            e.printStackTrace();
                            ToastUtils.showShort("LinphoneCoreException:" + e.toString());
                        }

                    } else {
                        Logutils.i("信息缺失");
                    }
                    break;
                } else {
                    //   Logutils.i("未获取到为本机注册的信息");
                }
            }
        } else {
            Logutils.i("未获取到本机的ip");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Logutils.i("start__time://////////////////////////////////////////" + new Date().toString());

        /**
         * 启动线程接收服务器发送过来的数据
         */
        ReceiveServerMess receiveServerMess = new ReceiveServerMess(new ReceiveServerMess.GetSmsListern() {
            @Override
            public void getSmsContent(String ms) {
                Message smsMess = new Message();
                smsMess.what = AppConfig.SMS_FLAGE;
                if (!TextUtils.isEmpty(ms) && !ms.equals("fail"))
                    smsMess.obj = ms;
                handler.sendMessage(smsMess);
            }
        });
        new Thread(receiveServerMess).start();

        /**
         * 回调获取服务器端发来的报警报文
         */
        ReceiverServerAlarm receiverServerAlarm = new ReceiverServerAlarm(new ReceiverServerAlarm.GetAlarmFromServerListern() {
            @Override
            public void getListern(AlarmBen alarmBen, String flage) {
                Message alarmMess = new Message();
                alarmMess.what = AppConfig.ALERT_FLAGE;
                if (flage.equals("success"))
                    alarmMess.obj = alarmBen;
                else
                    alarmMess.obj = null;
                handler.sendMessage(alarmMess);
            }
        });
        new Thread(receiverServerAlarm).start();

        /**
         * 获取Video资源列表
         */
        RequestVideoSourcesThread requestVideoSourcesThread = new RequestVideoSourcesThread(mContext, new RequestVideoSourcesThread.GetDataListener() {
            @Override
            public void getResult(List<VideoBen> devices) {
                Message message = new Message();
                message.what = AppConfig.VIDEO_SOURCES_FLAGE;
                message.obj = devices;
                handler.sendMessage(message);
            }
        });
        new Thread(requestVideoSourcesThread).start();

        /**
         * 获取Sip资源列表
         */
        SipRequestCallback sipRequestCallback = new SipRequestCallback(mContext, "0", new SipRequestCallback.SipListern() {
            @Override
            public void getDataListern(List<SipBean> mList) {
                Message message = new Message();
                message.what = AppConfig.SIP_SOURCES_FLAGE;
                message.obj = mList;
                handler.sendMessage(message);
            }
        });
        new Thread(sipRequestCallback).start();

        startService(new Intent(this, SendheartService.class));
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
    protected void onRestart() {
        super.onRestart();
        hideStatusBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stopService(new Intent(this, SendheartService.class));
        PhoneUtils.keepScreenOn(mContext, false);
        mVibrator = null;
        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideStatusBar();
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.button_intercom:
                intent.setClass(mContext, SipGroupActivity.class);
                mContext.startActivity(intent);
                mVibrator.vibrate(200);
                break;
            case R.id.button_phone:

                mVibrator.vibrate(200);
                break;
            case R.id.button_setup:
                intent.setClass(mContext, SettingActivity.class);
                mContext.startActivity(intent);
                mVibrator.vibrate(200);
                break;
            case R.id.button_video:
                //视频
                intent.setClass(mContext, MultiScreenActivity.class);
                mContext.startActivity(intent);
                mVibrator.vibrate(200);
                break;
            case R.id.button_alarm://跳转到报警页面
                //报警
//                intent.setClass(mContext, AlarmActivity.class);
//                mContext.startActivity(intent);
                mVibrator.vibrate(200);
                break;
            case R.id.button_applyforplay:  //申请供弹
                mVibrator.vibrate(200);
                AmmoRequestCallBack ammoRequestCallBack = new AmmoRequestCallBack(new AmmoRequestCallBack.AmmoCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            Message message = new Message();
                            message.what = AppConfig.GET_AMMOREQUEST_INFOR;
                            message.obj = result;
                            handler.sendMessage(message);
                        } else {
                            Logutils.i("AmmoRequest:" + result);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (!TextUtils.isEmpty(error)) {
                            Message message = new Message();
                            message.what = AppConfig.GET_AMMOREQUEST_INFOR;
                            message.obj = error;
                            handler.sendMessage(message);
                        } else {
                            Logutils.i("AmmoRequest_error:" + error);
                        }
                    }
                }, mContext);
                ammoRequestCallBack.start();
                break;
        }
    }

    /**
     * 显示时间的线程
     */
    class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }


}
