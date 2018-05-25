package com.zhketech.sip.app.project.client.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.adapter.VideoResourcesListAda;
import com.zhketech.sip.app.project.client.beans.VideoBen;
import com.zhketech.sip.app.project.client.callbacks.BatteryReceiver;
import com.zhketech.sip.app.project.client.callbacks.SendAlarmToServer;
import com.zhketech.sip.app.project.client.callbacks.WifiInformationReceiver;
import com.zhketech.sip.app.project.client.utils.ControlPtz;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.PageModel;
import com.zhketech.sip.app.project.client.utils.SharedPreferencesUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerView;

/**
 * 单屏播放模式
 *
 * @author wpf
 * @email wpfsean@126.com
 */
public class SingleScreenActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {


    //The rightmost nine buttons views
    @BindView(R.id.icon_btn1)
    public ImageButton btn_foursplitscreen;
    @BindView(R.id.icon_btn2)
    public ImageButton btn_amplification;
    @BindView(R.id.icon_btn3)
    public ImageButton btn_list;
    @BindView(R.id.icon_btn4)
    public ImageButton btn_left;
    @BindView(R.id.icon_btn5)
    public ImageButton btn_refresh;
    @BindView(R.id.icon_btn6)
    public ImageButton btn_right;
    @BindView(R.id.icon_btn7)
    public ImageButton btn_alarm;
    @BindView(R.id.icon_btn8)
    public ImageButton btn_stopplay;
    @BindView(R.id.icon_btn9)
    public ImageButton btn_return;


    //PTZ control function keys views
    @BindView(R.id.button_down_show)
    public ImageButton button_down;
    @BindView(R.id.button_up_show)
    public ImageButton button_up;
    @BindView(R.id.button_left_show)
    public ImageButton button_left;
    @BindView(R.id.button_right_show)
    public ImageButton button_right;
    @BindView(R.id.button_enlarge_show)
    public ImageButton button_enlarge_show;
    @BindView(R.id.button_narrow_show)
    public ImageButton button_narrow_show;


    //Display text for playing video information view
    @BindView(R.id.show_surfaceview_information)
    public TextView show_surfaceview_information;

    //Loading bar
    @BindView(R.id.single_first_progressbar)
    public ProgressBar single_first_progressbar;
    //Video loading information views
    @BindView(R.id.single_first_text)
    public TextView single_first_text;

    //最上面标题栏的布局
    @BindView(R.id.icone_relativtelayout_title)
    public RelativeLayout icone_relativtelayout_title;
    //播放器最外层的布局
    @BindView(R.id.single_surfaceview_relativelayout)
    public RelativeLayout single_surfaceview_relativelayout;

    //the parent of listview
    @BindView(R.id.relativelayout_listview)
    public RelativeLayout relativelayout_listview;

    //show video for listview
    @BindView(R.id.show_listresources)
    public ListView show_listresources;
    //The parent layout of PTZ control keys
    @BindView(R.id.show_relativelayout_all_button)
    public RelativeLayout show_relativelayout_all_button;
    //PTZ control key layout
    @BindView(R.id.relativelayout_control)
    public RelativeLayout relativelayout_control;
    //Electricity icon
    @BindView(R.id.icon_electritity_show)
    public ImageView icon_electritity_information;
    //Wifi icon
    @BindView(R.id.icon_network)
    public ImageView icon_network;
    Vibrator mVibrator = null;
    Context mContext = null;
    //存储所有的videoBean的集合
    List<VideoBen> listData = new ArrayList<VideoBen>();
    //next page and previous get listdata
    List<VideoBen> subList = new ArrayList<VideoBen>();
    BatteryReceiver batteryReceiver = null;
    WifiInformationReceiver wifiInformationReceiver = null;
    //for page number
    int position = 0;
    PageModel pm;

    //plyaer
    NodePlayer nodePlayer;
    //player view
    NodePlayerView npv1;

    boolean isSupportPtz = false;
    String ptz_url = "";
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_screen);
        ButterKnife.bind(this);
        mContext = this;
        hideStatusBar();
        initViewAndListern();
        initDisplayInformation();
        initRetrieveVideoData();
    }

    /**
     * get video resources from shareperfences
     */
    private void initRetrieveVideoData() {
        String result = (String) SharedPreferencesUtils.getObject(mContext, "video_result", "");
        if (result.equals("") || result == null) {
            return;
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<List<VideoBen>>() {
            }.getType();
            List<VideoBen> alterSamples = new ArrayList<VideoBen>();
            alterSamples = gson.fromJson(result, type);
            listData = alterSamples;
            if (listData != null && listData.size() > 0) {
                pm = new PageModel(listData, 1);
            }
        }
    }

    /**
     * show wifi and battery informatin on ui
     */
    private void initDisplayInformation() {
        //广播监听电池电量
        batteryReceiver = new BatteryReceiver(new BatteryReceiver.GetDataListener() {
            @Override
            public void getResult(Integer level) {
                int batteryLevel = level.intValue();
                if (batteryLevel >= 75 && batteryLevel <= 100) {
                    updateUi(icon_electritity_information, R.mipmap.icon_electricity_a);
                }
                if (batteryLevel >= 50 && batteryLevel < 75) {
                    updateUi(icon_electritity_information, R.mipmap.icon_electricity_b);
                }
                if (batteryLevel >= 25 && batteryLevel < 50) {
                    updateUi(icon_electritity_information, R.mipmap.icon_electricity_c);
                }
                if (batteryLevel >= 0 && batteryLevel < 25) {
                    updateUi(icon_electritity_information, R.mipmap.icon_electricity_disable);
                }

            }
        });
        mContext.registerReceiver(batteryReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        wifiInformationReceiver = new WifiInformationReceiver(new WifiInformationReceiver.GetDataListern() {
            @Override
            public void getDataResult(Integer num) {
                int wifiNum = num.intValue();
                if (wifiNum > -50 && wifiNum < 0) {
                    updateUi(icon_network, R.mipmap.icon_network);
                } else if (wifiNum > -70 && wifiNum <= -50) {
                    updateUi(icon_network, R.mipmap.icon_network_a);
                } else if (wifiNum < -70) {
                    updateUi(icon_network, R.mipmap.icon_network_b);
                } else if (wifiNum == -200) {
                    updateUi(icon_network, R.mipmap.icon_network_disable);
                }

            }
        });
        registerReceiver(wifiInformationReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    /**
     * init view and listern
     */
    private void initViewAndListern() {
        mVibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        nodePlayer = new NodePlayer(this);
        npv1 = (NodePlayerView) findViewById(R.id.single_vew);
        npv1.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        nodePlayer.setPlayerView(npv1);

        btn_foursplitscreen.setOnClickListener(this);
        btn_amplification.setOnClickListener(this);
        btn_list.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        btn_alarm.setOnClickListener(this);
        btn_stopplay.setOnClickListener(this);
        btn_return.setOnClickListener(this);
        icone_relativtelayout_title.setOnClickListener(this);
        single_surfaceview_relativelayout.setOnClickListener(this);

        //控制云台的功能键盘
        button_down.setOnTouchListener(this);
        button_up.setOnTouchListener(this);
        button_left.setOnTouchListener(this);
        button_right.setOnTouchListener(this);
        button_enlarge_show.setOnTouchListener(this);
        button_narrow_show.setOnTouchListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //show this page and get url for playing video
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url) && url != null) {
            show_surfaceview_information.setTextSize(10);
            show_surfaceview_information.setTextColor(0xffffff00);
            for (VideoBen v : listData) {
                if (v.getRtsp().equals(url)) {
                    Logutils.i("v:" + v.toString());
                    subList.add(v);
                    isSupportPtz = v.isSuporrtPtz();
                    ptz_url = v.getPtz_url();
                    token = v.getToken();
                    show_surfaceview_information.setText("监控:" + v.getName());
                    break;
                }
            }

            try {
                nodePlayer.setInputUrl(url);
                nodePlayer.setAudioEnable(false);
                nodePlayer.setReceiveAudio(false);
                nodePlayer.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        single_first_progressbar.setVisibility(View.GONE);
                        single_first_text.setVisibility(View.GONE);
                    }
                }, 1 * 1000);
            } catch (Exception e) {
                Logutils.i("player_error:" + e.getMessage());
            }


            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).getRtsp().equals(url)) {
                    position = i;
                    return;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logutils.i("single onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logutils.i("single onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logutils.i("single onRestart");
        hideStatusBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //this methed to unregister wifi and battery
        this.unregisterReceiver(wifiInformationReceiver);
        this.unregisterReceiver(batteryReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_btn1://多屏模式
                mVibrator.vibrate(200);
                finishPage();
                break;
            case R.id.icon_btn2://多屏模式
                mVibrator.vibrate(200);
                finishPage();
                break;
            case R.id.icon_btn3:
                mVibrator.vibrate(200);
                initListViewData();
                break;
            case R.id.icon_btn4://上页
                mVibrator.vibrate(200);
                lastPage();
                break;
            case R.id.icon_btn5:
                mVibrator.vibrate(200);
                if (nodePlayer != null) {
                    if (!nodePlayer.isPlaying()) {
                        Logutils.i("restart");
                        nodePlayer.start();
                        single_first_text.setVisibility(View.GONE);
                        single_first_progressbar.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.icon_btn6:
                mVibrator.vibrate(200);
                nextPage();
                break;
            case R.id.icon_btn7:
                mVibrator.vibrate(200);

                VideoBen videoBen = subList.get(0);
                if (videoBen != null){
                    alarmFromVideoBen(videoBen);
                }
//                String url1 = show_surfaceview_information.getText().toString().trim();
//                VideoBen alarmVideoBen = null;
//                for (VideoBen vv : listData) {
//                    if (url1.contains(vv.getIp())) {
//                        alarmVideoBen = vv;
//                    }
//                }
//                Logutils.i("bean:" + alarmVideoBen.toString());
//                alarmFromVideoBen(alarmVideoBen);
                break;
            case R.id.icon_btn8:
                mVibrator.vibrate(200);
                if (nodePlayer != null) {
                    if (nodePlayer.isPlaying()) {
                        Logutils.i("pause");
                        nodePlayer.pause();
                        nodePlayer.stop();
                        single_first_text.setVisibility(View.VISIBLE);
                        single_first_text.setText("Pause");
                    }
                }
                break;
            case R.id.icon_btn9:
                mVibrator.vibrate(200);
                finishPage();
                break;
            case R.id.icone_relativtelayout_title:
                hideRelativelayout();
                break;
            case R.id.single_surfaceview_relativelayout:
                hideRelativelayout();
                break;
        }
    }

    /**
     * finish  this page
     */
    private void finishPage() {
        if (nodePlayer != null) {
            if (nodePlayer.isPlaying()) {
                nodePlayer.pause();
                nodePlayer.stop();
            }
            nodePlayer.release();
            nodePlayer = null;
        }
        SingleScreenActivity.this.finish();
    }

    /**
     * hide title and statu bar
     */
    protected void hideStatusBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * update ui icon
     */
    public void updateUi(final ImageView imageView, final int n) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setBackgroundResource(n);
            }
        });
    }

    /**
     * init new player for playing video
     */
    public void initPlayer(String url) {
        single_first_text.setText("Loading...");
        if (nodePlayer != null) {
            nodePlayer.pause();
            nodePlayer.stop();
        }
        nodePlayer.setPlayerView(npv1);
        nodePlayer.setAudioEnable(false);
        nodePlayer.setReceiveAudio(false);
        nodePlayer.setInputUrl(url);
        nodePlayer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                single_first_progressbar.setVisibility(View.GONE);
                single_first_text.setVisibility(View.GONE);
            }
        }, 1 * 1000);
    }

    /**
     * init list data
     */
    private void initListViewData() {

        if (listData.size() > 0) {
            relativelayout_listview.setVisibility(View.VISIBLE);
            show_relativelayout_all_button.setVisibility(View.GONE);
            relativelayout_control.setVisibility(View.GONE);

            final VideoResourcesListAda ada = new VideoResourcesListAda(listData, mContext);
            View footView = (View) LayoutInflater.from(this).inflate(R.layout.footview, null);
            RelativeLayout refresh_relativelayout_data = (RelativeLayout) footView.findViewById(R.id.refresh_relativelayout_data);
            if (show_listresources.getFooterViewsCount() == 0) {
                show_listresources.addFooterView(footView);
            }
            show_listresources.setAdapter(ada);
            ada.notifyDataSetChanged();
            refresh_relativelayout_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ada.notifyDataSetChanged();
                    initListViewData();
                }
            });

            //list item监听
            show_listresources.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    relativelayout_listview.setVisibility(View.GONE);
                    show_relativelayout_all_button.setVisibility(View.VISIBLE);
                    relativelayout_control.setVisibility(View.VISIBLE);

                    single_first_progressbar.setVisibility(View.VISIBLE);
                    single_first_text.setVisibility(View.VISIBLE);

                    position = i;
                    subList.add(listData.get(i));
                    isSupportPtz = listData.get(i).isSuporrtPtz();
                    ptz_url = listData.get(i).getPtz_url();
                    token = subList.get(0).getToken();
                    String rtsp = listData.get(position).getRtsp();
                    initPlayer(rtsp);
                    show_surfaceview_information.setText(listData.get(position).getName());

                }
            });
        } else {
            showToast("未加载到数据...");
        }
    }


    /**
     * hide parent of listview
     */
    public void hideRelativelayout() {
        relativelayout_listview.setVisibility(View.GONE);
        show_relativelayout_all_button.setVisibility(View.VISIBLE);
        relativelayout_control.setVisibility(View.VISIBLE);
    }


    /**
     * show Toast methed
     */
    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SingleScreenActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * send alarm to server
     */
    private void alarmFromVideoBen(VideoBen videoBen) {
        SendAlarmToServer sendAlarmToServer = new SendAlarmToServer(videoBen);
        new Thread(sendAlarmToServer).start();
    }

    /**
     * previous page
     */
    private void lastPage() {
        position--;
        single_first_progressbar.setVisibility(View.VISIBLE);
        single_first_text.setVisibility(View.VISIBLE);
        if (pm.isHasPreviousPage()) {
            subList = pm.getObjects(position);
            if (subList != null && subList.size() > 0) {
                Logutils.i("v:" + subList.get(0).toString());
                isSupportPtz = subList.get(0).isSuporrtPtz();
                ptz_url = subList.get(0).getPtz_url();
                token = subList.get(0).getToken();
                show_surfaceview_information.setText(subList.get(0).getName());
                initPlayer(subList.get(0).getRtsp());
            }
        } else {
            Toast.makeText(SingleScreenActivity.this, "前面没有了", Toast.LENGTH_SHORT).show();
            single_first_progressbar.setVisibility(View.GONE);
            single_first_text.setVisibility(View.GONE);
        }
    }

    /**
     * next page
     */
    private void nextPage() {
        position++;
        single_first_progressbar.setVisibility(View.VISIBLE);
        single_first_text.setVisibility(View.VISIBLE);
        if (pm.isHasNextPage()) {
            subList = pm.getObjects(position);
            if (subList != null && subList.size() > 0) {
                Logutils.i("v:" + subList.get(0).toString());
                isSupportPtz = subList.get(0).isSuporrtPtz();
                ptz_url = subList.get(0).getPtz_url();
                token = subList.get(0).getToken();
                show_surfaceview_information.setText(subList.get(0).getName());
                initPlayer(subList.get(0).getRtsp());
            }
        } else {
            Toast.makeText(SingleScreenActivity.this, "最后了", Toast.LENGTH_SHORT).show();
            single_first_progressbar.setVisibility(View.GONE);
            single_first_text.setVisibility(View.GONE);
        }

    }


    /**
     * touch and test ptz_features
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {


            case R.id.button_left_show://left ptz

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ControlPtz controlPtz = new ControlPtz(ptz_url, token, "stop", 0.00, 0.00);
                    controlPtz.start();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mVibrator.vibrate(200);
                    if (isSupportPtz == true) {
                        ControlPtz controlPtz = new ControlPtz(ptz_url, token, "left", -0.02, 0.00);
                        controlPtz.start();
                    } else {
                        showToast("此路视频不支持ptz");
                    }
                }
                break;

            case R.id.button_right_show://right ptz
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ControlPtz controlPtz = new ControlPtz(ptz_url, token, "stop", 0.00, 0.00);
                    controlPtz.start();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mVibrator.vibrate(200);
                    if (isSupportPtz == true) {
                        ControlPtz controlPtz = new ControlPtz(ptz_url, token, "right", 0.02, 0.00);
                        controlPtz.start();
                    } else {
                        showToast("此路视频不支持ptz");
                    }
                }
                break;


            case R.id.button_down_show://top ptz
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ControlPtz controlPtz = new ControlPtz(ptz_url, token, "stop", 0.00, 0.00);
                    controlPtz.start();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mVibrator.vibrate(200);
                    if (isSupportPtz == true) {
                        ControlPtz controlPtz = new ControlPtz(ptz_url, token, "top", 0.00, 0.01);
                        controlPtz.start();
                    } else {
                        showToast("此路视频不支持ptz");
                    }
                }
                break;


            case R.id.button_up_show:// blow ptz
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ControlPtz controlPtz = new ControlPtz(ptz_url, token, "stop", 0.00, 0.00);
                    controlPtz.start();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mVibrator.vibrate(200);
                    if (isSupportPtz == true) {
                        ControlPtz controlPtz = new ControlPtz(ptz_url, token, "below", 0.00, -0.01);
                        controlPtz.start();
                    } else {
                        showToast("此路视频不支持ptz");
                    }
                }
                break;

            case R.id.button_enlarge_show: // zoom_big
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ControlPtz controlPtz = new ControlPtz(ptz_url, token, "stop", 0.00, 0.00);
                    controlPtz.start();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mVibrator.vibrate(200);
                    if (isSupportPtz == true) {
                        ControlPtz controlPtz = new ControlPtz(ptz_url, token, "zoom_b", 0.3, 0.00);
                        controlPtz.start();
                    } else {
                        showToast("此路视频不支持ptz");
                    }
                }
                break;
            case R.id.button_narrow_show://zoom_small
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ControlPtz controlPtz = new ControlPtz(ptz_url, token, "stop", 0.00, 0.00);
                    controlPtz.start();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mVibrator.vibrate(200);
                    if (isSupportPtz == true) {
                        ControlPtz controlPtz = new ControlPtz(ptz_url, token, "zoom_s", -0.3, 0.00);
                        controlPtz.start();
                    } else {
                        showToast("此路视频不支持ptz");
                    }
                }
                break;
        }

        return false;
    }


}
