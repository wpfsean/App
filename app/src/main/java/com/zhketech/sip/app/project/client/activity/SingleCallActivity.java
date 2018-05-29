package com.zhketech.sip.app.project.client.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhketech.sip.app.project.client.MainActivity;
import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.beans.DeviceInfor;
import com.zhketech.sip.app.project.client.beans.SipBean;
import com.zhketech.sip.app.project.client.beans.VideoBen;
import com.zhketech.sip.app.project.client.global.AppConfig;
import com.zhketech.sip.app.project.client.linphone.LinphoneService;
import com.zhketech.sip.app.project.client.linphone.PhoneBean;
import com.zhketech.sip.app.project.client.linphone.PhoneServiceCallBack;
import com.zhketech.sip.app.project.client.linphone.PhoneVoiceUtils;
import com.zhketech.sip.app.project.client.linphone.Utility;
import com.zhketech.sip.app.project.client.rtsp.RtspServer;
import com.zhketech.sip.app.project.client.rtsp.media.VideoMediaCodec;
import com.zhketech.sip.app.project.client.rtsp.media.h264data;
import com.zhketech.sip.app.project.client.rtsp.record.Constant;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.SharedPreferencesUtils;
import com.zhketech.sip.app.project.client.utils.ToastUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerView;

/**
 * 播打电话界面
 *
 */


public class SingleCallActivity extends AppCompatActivity implements View.OnClickListener,Camera.PreviewCallback, SurfaceHolder.Callback {


    @BindView(R.id.secodary_surfacevie)
    public SurfaceView secodary_surfacevie;

    @BindView(R.id.main_progressbar)
    public ProgressBar main_progressbar;
    @BindView(R.id.secondary_progressbar)
    public ProgressBar secondary_progressbar;

    @BindView(R.id.show_call_time)
    public TextView show_call_time;

    @BindView(R.id.btn_handup_icon)
    public ImageButton hangupButton;

    @BindView(R.id.btn_mute)
    public ImageButton btn_mute;

    @BindView(R.id.btn_volumeadd)
    public ImageButton btn_volumeadd;

    @BindView(R.id.btn_camera)
    public ImageButton btn_camera;

    @BindView(R.id.btn_volumelow)
    public ImageButton btn_volumelow;
    //显示网络状态
    @BindView(R.id.icon_network)
    public ImageView network_pic;
    //显示电量
    @BindView(R.id.icon_electritity_show)
    public ImageView icon_electritity_information;
    List<SipBean> sipListResources = new ArrayList<>();
    AudioManager mAudioManager = null;
    Context mContext;
    NodePlayer nodePlayer;
    NodePlayerView np;
    boolean isCall = true;//来源是打电话还是接电话，true为打电话，false为接电话
    String userName = "wpf";
    boolean isVideo = false;
    String rtsp ="";//可视电话的视频地址
    boolean isSilent = false;//是否静音
    private Boolean isCallConnected = false;//是否已接通
    boolean mWorking = false;
    //计时的子线程
    Thread mThread = null;
    int num = 0;

    //视频录制的硬编码参数
    private static int queuesize = 10;
    public static ArrayBlockingQueue<h264data> h264Queue = new ArrayBlockingQueue<>(queuesize);
    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<>(queuesize);
    private RtspServer mRtspServer;
    private String RtspAddress;
    private SurfaceHolder surfaceHolder;//小窗口
    private Camera mCamera;
    private VideoMediaCodec mVideoMediaCodec;
   // private AvcEncoder avcEncoder;
    private boolean isRecording = false;
    private static int cameraId = 0;//默认后置摄像头
    boolean isBandService = false;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    num++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            show_call_time.setText(getTime(num) + "");
                        }
                    });
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideStatusBar();
        setContentView(R.layout.activity_single_call);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initPageData();

    }

    private void initPageData() {
        isCall = this.getIntent().getBooleanExtra("isCall", true);//是打电话还是接电话
        userName  = this.getIntent().getStringExtra("userName");//对方号码
        isVideo = this.getIntent().getBooleanExtra("isVideo",false);//是可视频电话，还是语音电话


        sipListResources = new ArrayList<>();
        String result = (String) SharedPreferencesUtils.getObject(mContext, "sip_result", "");
        if (result.equals("") || result == null) {
            return;
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<List<SipBean>>() {
            }.getType();
            List<SipBean> alterSamples = new ArrayList<SipBean>();
            alterSamples = gson.fromJson(result, type);

            if (alterSamples.size() > 0 && alterSamples != null) {
                sipListResources = alterSamples;
                if (!TextUtils.isEmpty(userName)){
                    for (SipBean s:sipListResources){
                        if (s.getNumber().equals(userName)){
                            rtsp = s.getRtsp();
                            break;
                        }
                    }
                }
                Logutils.i("///");
                if (isVideo){
                    nodePlayer.setInputUrl(rtsp);
                    nodePlayer.setAudioEnable(false);
                    nodePlayer.setReceiveAudio(false);
                    nodePlayer.start();
                    initCall(userName);
                }else {
                    initCall(userName);
                }
            } else {
                sipListResources = null;
            }
        }

        if (!isCall){
            isCallConnected = true;
            threadStart();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hangupButton.setBackgroundResource(R.drawable.btn_hangup_select);
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

      //  bindService(intent, mRtspServiceConnection, Context.BIND_AUTO_CREATE);

        unbindService(mRtspServiceConnection);
    }

    private void initView() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        nodePlayer = new NodePlayer(this);
        np = (NodePlayerView)findViewById(R.id.main_view);
        np.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
       // np.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFit);
        nodePlayer.setPlayerView(np);

        hangupButton.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_volumelow.setOnClickListener(this);
        secodary_surfacevie.setZOrderOnTop(true);
        btn_mute.setOnClickListener(this);
        btn_volumeadd.setOnClickListener(this);

        surfaceHolder = secodary_surfacevie.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFixedSize(Constant.VIDEO_WIDTH, Constant.VIDEO_HEIGHT);
        surfaceHolder.setKeepScreenOn(true);

        RtspAddress = "rtsp://"+ SharedPreferencesUtils.getObject(mContext,AppConfig.IP_NAVITE,"")+":"+RtspServer.DEFAULT_RTSP_PORT;
        mVideoMediaCodec = new VideoMediaCodec();
        if (RtspAddress != null) {
            SharedPreferencesUtils.putObject(mContext,AppConfig.NATIVE_RTSP,RtspAddress);
            Log.i("tag", "地址: " + RtspAddress);
        }
    }
    public void initCall(String number){
        PhoneBean bean = new PhoneBean();
        if (!TextUtils.isEmpty(userName)){
            bean.userName = userName;
        }
        String host = (String) SharedPreferencesUtils.getObject(mContext, AppConfig.SIP_SERVER,"");
        if (!TextUtils.isEmpty(host)){
            bean.host = host;
        }
        PhoneVoiceUtils.getInstance().startSingleCallingTo(bean);
        PhoneVoiceUtils.getInstance().toggleSpeaker(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_handup_icon:

                if (isCallConnected){
                    PhoneVoiceUtils.getInstance().hangUp();
                    show_call_time.setText("00:00");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hangupButton.setBackgroundResource(R.drawable.btn_answer_select);
                        }
                    });
                }

                break;
            case R.id.btn_mute:
                if (!isSilent){
                    PhoneVoiceUtils.getInstance().toggleMicro(true);
                    btn_mute.setBackgroundResource(R.mipmap.btn_mute_pressed);
                    isSilent = true;
                }else {
                    PhoneVoiceUtils.getInstance().toggleMicro(false);
                    btn_mute.setBackgroundResource(R.mipmap.btn_voicetube_pressed);
                    isSilent = false;
                }

                break;
            //前后摄像头的转换
            case R.id.btn_camera:

                if (cameraId == 0) {
                    cameraId = 1;
                } else {
                    cameraId = 0;
                }
                initCamera();
                try {
                    play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_volumeadd:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                break;
            //音量减
            case R.id.btn_volumelow:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                break;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        LinphoneService.addCallBack(new PhoneServiceCallBack() {
            @Override
            public void callConnected() {
                isCallConnected = true;

                if (isCallConnected){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            show_call_time.setText("00:00");
                            hangupButton.setBackgroundResource(R.drawable.btn_hangup_select);
                        }
                    });
                    threadStart();
                }
            }

            @Override
            public void callReleased() {
                ToastUtils.showShort("通话结束");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hangupButton.setBackgroundResource(R.drawable.btn_answer_select);
                        show_call_time.setText("00:00");
                    }
                });
                threadStop();

            }

            @Override
            public void callRing() {
                Logutils.i("正在响.....");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        show_call_time.setTextSize(22);
                        show_call_time.setText("正在振铃....");
                        show_call_time.setTextColor(0xffff00ff);
                        hangupButton.setBackgroundResource(R.drawable.btn_hangup_select);
                        isCallConnected = true;

                    }
                });
            }
            @Override
            public void callhangup() {
                Logutils.i(" 对方挂了.....");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        threadStop();
                        hangupButton.setBackgroundResource(R.drawable.btn_answer_select);
                        show_call_time.setText("00:00");
                    }
                });
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideStatusBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nodePlayer != null){
            if (nodePlayer.isPlaying()){
                nodePlayer.pause();
                nodePlayer.stop();
                nodePlayer.release();
                nodePlayer = null;
            }
        }
    }

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


    /**
     * 计时线程开启
     */
    public void threadStart() {
        mWorking = true;
        if (mThread != null && mThread.isAlive()) {
            Logutils.i("start: thread is alive");
        } else {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mWorking) {
                        try {
                            Thread.sleep(1 * 1000);
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            mThread.start();
        }
    }

    /**
     * 计时线程停止
     */
    public void threadStop() {
        if (mWorking) {
            if (mThread != null && mThread.isAlive()) {
                mThread.interrupt();
                mThread = null;
            }
            show_call_time.setText("00:00");
            mWorking = false;
        }
    }

    /**
     * int转成时间 00:00
     */
    public static String getTime(int num) {
        if (num < 10) {
            return "00:0" + num;
        }
        if (num < 60) {
            return "00:" + num;
        }
        if (num < 3600) {
            int minute = num / 60;
            num = num - minute * 60;
            if (minute < 10) {
                if (num < 10) {
                    return "0" + minute + ":0" + num;
                }
                return "0" + minute + ":" + num;
            }
            if (num < 10) {
                return minute + ":0" + num;
            }
            return minute + ":" + num;
        }
        int hour = num / 3600;
        int minute = (num - hour * 3600) / 60;
        num = num - hour * 3600 - minute * 60;
        if (hour < 10) {
            if (minute < 10) {
                if (num < 10) {
                    return "0" + hour + ":0" + minute + ":0" + num;
                }
                return "0" + hour + ":0" + minute + ":" + num;
            }
            if (num < 10) {
                return "0" + hour + minute + ":0" + num;
            }
            return "0" + hour + minute + ":" + num;
        }
        if (minute < 10) {
            if (num < 10) {
                return hour + ":0" + minute + ":0" + num;
            }
            return hour + ":0" + minute + ":" + num;
        }
        if (num < 10) {
            return hour + minute + ":0" + num;
        }
        return hour + minute + ":" + num;
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //前后摄像头的数据采集,根据前后进行相应的视频流旋转
//        Log.d("views","data:  "+data.length);
        if (cameraId == 0) {
            data = VideoMediaCodec.rotateYUVDegree90(data, Constant.VIDEO_WIDTH, Constant.VIDEO_HEIGHT);
        } else {
            data = VideoMediaCodec.rotateYUV420Degree270(data, Constant.VIDEO_WIDTH, Constant.VIDEO_HEIGHT);
        }
        putYUVData(data, data.length);

    }


    public void putYUVData(byte[] buffer, int length) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
    }

    private RtspServer.CallbackListener mRtspCallbackListener = new RtspServer.CallbackListener() {

        @Override
        public void onError(RtspServer server, Exception e, int error) {
            // We alert the user that the port is already used by another app.
            if (error == RtspServer.ERROR_BIND_FAILED) {
                new AlertDialog.Builder(SingleCallActivity.this)
                        .setTitle("Port already in use !")
                        .setMessage("You need to choose another port for the RTSP server !")
                        .show();
            }
        }


        @Override
        public void onMessage(RtspServer server, int message) {
            if (message == RtspServer.MESSAGE_STREAMING_STARTED) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SingleCallActivity.this, "RTSP STREAM STARTED", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (message == RtspServer.MESSAGE_STREAMING_STOPPED) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SingleCallActivity.this, "RTSP STREAM STOPPED", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
    private ServiceConnection mRtspServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRtspServer = ((RtspServer.LocalBinder) service).getService();
            mRtspServer.addCallbackListener(mRtspCallbackListener);
            mRtspServer.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCamera();
        try {
            play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    /**
     * put数据
     *
     * @param buffer
     * @param type
     * @param ts
     */
    public static void putData(byte[] buffer, int type, long ts) {
        if (h264Queue.size() >= queuesize) {
            h264Queue.poll();
        }
        h264data data = new h264data();
        data.data = buffer;
        data.type = type;
        data.ts = ts;
        h264Queue.add(data);
    }

    /**
     * 初始化相机参数
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mVideoMediaCodec.prepare();
        mVideoMediaCodec.isRun(true);
        try {
            mCamera = Camera.open(cameraId);
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mCamera.setDisplayOrientation(0);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode("off");
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPreviewFrameRate(15);
        parameters.setPreviewSize(Constant.VIDEO_WIDTH, Constant.VIDEO_HEIGHT);
        mCamera.setParameters(parameters);
        mCamera.setPreviewCallback(this);
    }

    private void play() throws IOException {
        mCamera.startPreview();
        if (RtspAddress != null && !RtspAddress.isEmpty()) {
            isRecording = true;
            Intent intent = new Intent(this, RtspServer.class);
            bindService(intent, mRtspServiceConnection, Context.BIND_AUTO_CREATE);
            isBandService = true;
        }
        new Thread() {
            @Override
            public void run() {
                mVideoMediaCodec.getBuffers();
            }
        }.start();
    }


}
