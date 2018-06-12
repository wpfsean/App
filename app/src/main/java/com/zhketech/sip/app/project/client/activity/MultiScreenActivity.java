package com.zhketech.sip.app.project.client.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.PageModel;
import com.zhketech.sip.app.project.client.utils.SharedPreferencesUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.nodemedia.NodePlayerView;

/**
 * 测试 页面
 * <p>
 * 测试 rtsp未解析到的情况
 */
public class MultiScreenActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {


    //第一个surfaceview所在的上布局
    @BindView(R.id.first_surfaceview_relativelayout)
    public RelativeLayout first_surfaceview_relativelayout;
    //第一个progressbar
    @BindView(R.id.first_progressbar)
    public ProgressBar first_progressbar;
    //第一个loading
    @BindView(R.id.firs_show_loading)
    public TextView first_show_loadind;

    @BindView(R.id.first_window_zoom)
    public ImageButton first_window_zoom;

    @BindView(R.id.get_surfaceview_video_picture_first)
    public ImageButton get_surfaceview_video_picture_first;
    ///////////////////////////////////////////////////////////

    //第二个surfaceview所在的上布局
    @BindView(R.id.second_surfaceview_relativelayout)
    public RelativeLayout second_surfaceview_relativelayout;
    //第二个progressbar
    @BindView(R.id.second_progressbar)
    public ProgressBar second_progressbar;
    //第二个loading
    @BindView(R.id.second_show_loading)
    public TextView second_show_loadind;
    ///////////////////////////////////////////////////////////////////////////////

    //第三个surfaceview所在的上布局
    @BindView(R.id.third_surfaceview_relativelayout)
    public RelativeLayout third_surfaceview_relativelayout;
    //第三个progressbar
    @BindView(R.id.third_progressbar)
    public ProgressBar third_progressbar;
    //第三个loading
    @BindView(R.id.third_show_loading)
    public TextView third_show_loadind;
    ///////////////////////////////////////////////////////////////////////////////

    //第四个surfaceview所在的上布局
    @BindView(R.id.fourth_surfaceview_relativelayout)
    public RelativeLayout fourth_surfaceview_relativelayout;
    //第四个progressbar
    @BindView(R.id.fourth_progressbar)
    public ProgressBar fourth_progressbar;
    //第四个loading
    @BindView(R.id.fourth_show_loading)
    public TextView fourth_show_loadind;
    ///////////////////////////////////////////////////////////////////////////////


    //最右侧九个按键
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

    //方向键四个按钮
    @BindView(R.id.button_down_show)
    public ImageButton button_down;
    @BindView(R.id.button_up_show)
    public ImageButton button_up;
    @BindView(R.id.button_left_show)
    public ImageButton button_left;
    @BindView(R.id.button_right_show)
    public ImageButton button_right;

    //放大缩小 功能键盘
    @BindView(R.id.button_enlarge_show)
    public ImageButton button_enlarge;
    @BindView(R.id.button_narrow_show)
    public ImageButton button_narrow;
    //显示网络状态
    @BindView(R.id.icon_network)
    public ImageView network_pic;
    //显示电量
    @BindView(R.id.icon_electritity_show)
    public ImageView icon_electritity_information;

    //显示当前的连接状态
    @BindView(R.id.icon_connection_show)
    public ImageView icon_connection;


    //九按钮所在的父布局
    @BindView(R.id.show_relativelayout_all_button)
    public RelativeLayout show_relativelayout_all_button;
    //方向键所在的布局
    @BindView(R.id.relativelayout_control)
    public RelativeLayout relativelayout_control;
    //listview所 在的父布局
    @BindView(R.id.relativelayout_listview)
    public RelativeLayout relativelayout_listview;

    //展示视频数据的listview
    @BindView(R.id.show_listresources)
    public ListView show_listresources;


    //四个视频最外层的父布局
    @BindView(R.id.four_surfaceview_parent_relativelayout)
    public RelativeLayout four_surfaceview_parent_relativelayout;

    //最上面标题栏的布局
    @BindView(R.id.icone_relativtelayout_title)
    public RelativeLayout icone_relativtelayout_title;

    //第一个哨位台名称描述
    @BindView(R.id.first_text)
    public TextView first_text;

    @BindView(R.id.second_text)
    public TextView second_text;

    @BindView(R.id.third_text)
    public TextView third_text;

    @BindView(R.id.fourth_text)
    public TextView fourth_text;


    @BindView(R.id.fr1)
    public FrameLayout fr1;

    @BindView(R.id.fr2)
    public FrameLayout fr2;

    @BindView(R.id.fr3)
    public FrameLayout fr3;

    @BindView(R.id.fr4)
    public FrameLayout fr4;

    List<VideoBen> listResources = new ArrayList<VideoBen>();
    //存放rtsp地址集合
    List<String> rtspResources = new ArrayList<String>();
    List<VideoBen> subList = null;
    //电量
    BatteryReceiver batteryReceiver = null;
    //获取wifi信息强弱的回调
    WifiInformationReceiver wifiInformationReceiver = null;

    int videoCurrentPage = 1;//当前第几页

    //判断这四个视频 中否被选中
    boolean firstViewSelect = false;
    boolean secondViewSelect = false;
    boolean thirdViewSelect = false;
    boolean fourthViewSelect = false;
    PageModel pm;//分页加载器
    Vibrator mVibrator = null;
    Context mContext = null;
    //四个播放器
    NodePlayer np1, np2, np3, np4;
    //四个显示播放内容的view
    NodePlayerView npv1, npv2, npv3, npv4;

    boolean isSuporrtPtz = false;
    String ptz_url = "";
    String token = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logutils.i("create");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideStatusBar();
        setContentView(R.layout.activity_videos);
        mContext = this;
        ButterKnife.bind(this);
        //实例一个震动对象
        mVibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        //初始化控件并对相亲的控件加监听
        initViewAndListern();
        //显示电量及wifi强度的图标变化
        initDisplayInformation();
        //获取要播放的video对象数据
        initRetrieveVideoData();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (np1 != null){
            np1.setNodePlayerDelegate(new NodePlayerDelegate() {
                @Override
                public void onEventCallback(NodePlayer player, int event, String msg) {
                    Logutils.i("np1-->>:"+event+"\n"+msg);
                    if (event == 1003 || event == 1004){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                first_show_loadind.setVisibility(View.VISIBLE);
                                first_show_loadind.setText("正在重新连接");
                            }
                        });
                    }
                }
            });
        }

        if (np2 != null){
            np2.setNodePlayerDelegate(new NodePlayerDelegate() {
                @Override
                public void onEventCallback(NodePlayer player, int event, String msg) {
                    Logutils.i("np2-->>:"+event+"\n"+msg);
                    if (event == 1003 || event == 1004){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                second_show_loadind.setVisibility(View.VISIBLE);
                                second_show_loadind.setText("正在重新连接");
                            }
                        });
                    }
                }
            });
        }

        if (np3 != null){
            np3.setNodePlayerDelegate(new NodePlayerDelegate() {
                @Override
                public void onEventCallback(NodePlayer player, int event, String msg) {
                    Logutils.i("np3-->>:"+event+"\n"+msg);
                    if (event == 1003 || event == 1004){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                third_show_loadind.setVisibility(View.VISIBLE);
                                third_show_loadind.setText("正在重新连接");
                            }
                        });
                    }
                }
            });
        }

        if (np4 != null){
            np4.setNodePlayerDelegate(new NodePlayerDelegate() {
                @Override
                public void onEventCallback(NodePlayer player, int event, String msg) {
                    Logutils.i("np4-->>:"+event+"\n"+msg);
                    if (event == 1003 || event == 1004){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fourth_show_loadind.setVisibility(View.VISIBLE);
                                fourth_show_loadind.setText("正在重新连接");
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logutils.i("Multi.Pause");
        //全部暂停
        if (np1 != null && np1.isPlaying()) {
            np1.pause();
        }
        if (np2 != null && np2.isPlaying()) {
            np2.pause();
        }
        if (np3 != null && np3.isPlaying()) {
            np3.pause();
        }
        if (np4 != null && np4.isPlaying()) {
            np4.pause();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideStatusBar();
        first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
        second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
        third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
        fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);

        if (subList != null && subList.size() > 0) {

            if (np1 != null) {
                np1.start();
            }
            if (np2 != null) {
                np2.start();
            }
            if (np3 != null) {
                np3.start();
            }
            if (np4 != null) {
                np4.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(batteryReceiver);
        this.unregisterReceiver(wifiInformationReceiver);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fr1://第个个surfaceview选中
                if (firstViewSelect == false) {
                    //选中时显示的ui
                    first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_select_shape);
                    firstViewSelect = true;
                    get_surfaceview_video_picture_first.setVisibility(View.VISIBLE);
                    first_window_zoom.setVisibility(View.VISIBLE);
                    if (subList != null && subList.size() > 0) {
                        isSuporrtPtz = subList.get(0).isSuporrtPtz();
                        ptz_url = subList.get(0).getPtz_url();
                        token = subList.get(0).getToken();
                    }
                } else {
                    //未选中的ui
                    first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                    firstViewSelect = false;
                    get_surfaceview_video_picture_first.setVisibility(View.GONE);
                    first_window_zoom.setVisibility(View.GONE);
                }
                relativelayout_listview.setVisibility(View.GONE);
                show_relativelayout_all_button.setVisibility(View.VISIBLE);
                relativelayout_control.setVisibility(View.VISIBLE);
                second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                break;
            case R.id.fr2://第二个surfaceview选中
                if (secondViewSelect == false) {
                    second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_select_shape);
                    secondViewSelect = true;
                    if (subList != null && subList.size() > 0){
                        isSuporrtPtz = subList.get(1).isSuporrtPtz();
                        ptz_url = subList.get(1).getPtz_url();
                        token = subList.get(1).getToken();
                    }

                } else {
                    second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                    secondViewSelect = false;
                }
                relativelayout_listview.setVisibility(View.GONE);
                show_relativelayout_all_button.setVisibility(View.VISIBLE);
                relativelayout_control.setVisibility(View.VISIBLE);
                first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                break;

            case R.id.fr3://第三个surfaceview选中
                if (thirdViewSelect == false) {
                    third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_select_shape);
                    thirdViewSelect = true;
                    if (subList != null && subList.size() > 0) {
                        isSuporrtPtz = subList.get(2).isSuporrtPtz();
                        ptz_url = subList.get(2).getPtz_url();
                        token = subList.get(2).getToken();
                    }
                } else {
                    third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                    thirdViewSelect = false;
                }
                relativelayout_listview.setVisibility(View.GONE);
                show_relativelayout_all_button.setVisibility(View.VISIBLE);
                relativelayout_control.setVisibility(View.VISIBLE);
                second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                break;
            case R.id.fr4://第四个surfaceview选中
                if (fourthViewSelect == false) {
                    fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_select_shape);
                    fourthViewSelect = true;
                    if (subList != null && subList.size() > 0) {
                        isSuporrtPtz = subList.get(3).isSuporrtPtz();
                        ptz_url = subList.get(3).getPtz_url();
                        token = subList.get(3).getToken();
                    }
                } else {
                    fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                    fourthViewSelect = false;
                }
                relativelayout_listview.setVisibility(View.GONE);
                show_relativelayout_all_button.setVisibility(View.VISIBLE);
                relativelayout_control.setVisibility(View.VISIBLE);
                second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                break;


            case R.id.get_surfaceview_video_picture_first: //第一个view下面的截图按钮
                //    screenShots(first_surfaceview_relativelayout);
                break;
            case R.id.icon_btn1://单屏模式
                if (firstViewSelect) {
                    whichWindownSelect(firstViewSelect);
                } else if (secondViewSelect) {
                    whichWindownSelect(secondViewSelect);
                } else if (thirdViewSelect) {
                    whichWindownSelect(thirdViewSelect);
                } else if (fourthViewSelect) {
                    whichWindownSelect(fourthViewSelect);
                }
                mVibrator.vibrate(200);
                break;
            case R.id.icon_btn2: //根据选中的窗口进行视频放大
                mVibrator.vibrate(200);
                if (firstViewSelect) {
                    whichWindownSelect(firstViewSelect);
                } else if (secondViewSelect) {
                    whichWindownSelect(secondViewSelect);
                } else if (thirdViewSelect) {
                    whichWindownSelect(thirdViewSelect);
                } else if (fourthViewSelect) {
                    whichWindownSelect(fourthViewSelect);
                }
                break;
            case R.id.icon_btn3://加载listview列表资源
                mVibrator.vibrate(200);
                initListViewData();
                break;
            case R.id.icon_btn4://向上翻页
                mVibrator.vibrate(200);
                lastPage();
                break;
            case R.id.icon_btn5://根据选中窗口重新加载视频
                mVibrator.vibrate(200);
                if (firstViewSelect) {
                    secondViewSelect = false;
                    thirdViewSelect = false;
                    fourthViewSelect = false;
                    firstViewSelect = false;
                    if (np1 != null) {
                        np1.start();
                    }
                    first_show_loadind.setVisibility(View.GONE);
                    first_show_loadind.setVisibility(View.GONE);
                    first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                } else if (secondViewSelect) {
                    firstViewSelect = false;
                    thirdViewSelect = false;
                    fourthViewSelect = false;
                    secondViewSelect = false;
                    if (np2 != null) {
                        np2.start();
                    }
                    second_show_loadind.setVisibility(View.GONE);
                    second_show_loadind.setVisibility(View.GONE);
                    second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                } else if (thirdViewSelect) {
                    firstViewSelect = false;
                    secondViewSelect = false;
                    fourthViewSelect = false;
                    thirdViewSelect = false;
                    if (np3 != null) {
                        np3.start();
                    }
                    third_show_loadind.setVisibility(View.GONE);
                    third_show_loadind.setVisibility(View.GONE);
                    third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                } else if (fourthViewSelect) {
                    firstViewSelect = false;
                    secondViewSelect = false;
                    thirdViewSelect = false;
                    fourthViewSelect = false;
                    if (np4 != null) {
                        np4.start();
                    }
                    fourth_show_loadind.setVisibility(View.GONE);
                    fourth_show_loadind.setVisibility(View.GONE);
                    fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                }
                break;
            case R.id.icon_btn6://向下翻页
                mVibrator.vibrate(200);
                nextPage();
                break;
            case R.id.icon_btn7://根据选中的窗口发送报警报文到服务器
                mVibrator.vibrate(200);
                Logutils.i("infor:" + firstViewSelect + "\t" + secondViewSelect + "\t" + thirdViewSelect + "\t" + fourthViewSelect);
                //选中某个窗口上报报警报文
                if (firstViewSelect) {
                    alarmFromVideoBen(subList.get(0));
                    firstViewSelect = false;
                } else if (secondViewSelect) {
                    alarmFromVideoBen(subList.get(1));
                    secondViewSelect = false;
                } else if (thirdViewSelect) {
                    alarmFromVideoBen(subList.get(2));
                    thirdViewSelect = false;
                } else if (fourthViewSelect) {
                    alarmFromVideoBen(subList.get(3));
                    fourthViewSelect = false;
                } else {
                    Toast.makeText(MultiScreenActivity.this, "未先中窗口..", Toast.LENGTH_SHORT).show();
                }
                //延时1秒后提示报警报文已发送
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MultiScreenActivity.this, "已发出报警报文", Toast.LENGTH_SHORT).show();
                    }
                }, 1 * 1000);
                break;
            case R.id.icon_btn8://根据选中的窗口暂停
                mVibrator.vibrate(200);
                Logutils.i("status:" + firstViewSelect + "\t" + secondViewSelect + "\t" + thirdViewSelect + "\t" + fourthViewSelect);
                if (firstViewSelect) {
                    secondViewSelect = false;
                    thirdViewSelect = false;
                    fourthViewSelect = false;
                    if (np1 != null) {
                        if (np1.isPlaying()) {
                            np1.pause();
                        }
                    }
                    first_show_loadind.setVisibility(View.VISIBLE);
                    first_show_loadind.setText("已停止");
                } else if (secondViewSelect) {
                    firstViewSelect = false;
                    thirdViewSelect = false;
                    fourthViewSelect = false;
                    if (np2 != null) {
                        if (np2.isPlaying()) {
                            np2.pause();
                        }
                    }
                    second_show_loadind.setVisibility(View.VISIBLE);
                    second_show_loadind.setText("已停止");
                } else if (thirdViewSelect) {
                    firstViewSelect = false;
                    secondViewSelect = false;
                    fourthViewSelect = false;
                    if (np3 != null) {
                        if (np3.isPlaying()) {
                            np3.pause();
                        }
                    }
                    third_show_loadind.setVisibility(View.VISIBLE);
                    third_show_loadind.setText("已停止");
                } else if (fourthViewSelect) {
                    firstViewSelect = false;
                    secondViewSelect = false;
                    thirdViewSelect = false;
                    if (np4 != null) {
                        if (np4.isPlaying()) {
                            np4.pause();
                        }
                    }
                    fourth_show_loadind.setVisibility(View.VISIBLE);
                    fourth_show_loadind.setText("已停止");
                }
                break;
            case R.id.icon_btn9://finish本页面
                mVibrator.vibrate(200);
                this.finish();

                break;
            //四个方向功能键盘
            case R.id.button_down_show:
                mVibrator.vibrate(200);
                break;
            case R.id.button_up_show:
                mVibrator.vibrate(200);
                break;
            case R.id.button_left_show:
                mVibrator.vibrate(200);
                break;
            case R.id.button_right_show:
                mVibrator.vibrate(200);
                break;
            //放大缩小功能 键盘
            case R.id.button_enlarge_show:
                mVibrator.vibrate(200);
                break;
            case R.id.button_narrow_show:
                mVibrator.vibrate(200);
                break;
            //点击父布局使listview所在在布局消失
            case R.id.four_surfaceview_parent_relativelayout:
                show_relativelayout_all_button.setVisibility(View.VISIBLE);
                relativelayout_control.setVisibility(View.VISIBLE);
                relativelayout_listview.setVisibility(View.GONE);
                break;

            case R.id.single_surfaceview_parent_relativelayout:
                show_relativelayout_all_button.setVisibility(View.VISIBLE);
                relativelayout_control.setVisibility(View.VISIBLE);
                relativelayout_listview.setVisibility(View.GONE);
                break;
            case R.id.icone_relativtelayout_title:
                show_relativelayout_all_button.setVisibility(View.VISIBLE);
                relativelayout_control.setVisibility(View.VISIBLE);
                relativelayout_listview.setVisibility(View.GONE);
                break;
        }

    }

    /**
     * 隐藏状态栏
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


    /**
     * 监听
     */
    private void initViewAndListern() {


        //对九个按键加监听
        btn_foursplitscreen.setOnClickListener(this);
        btn_amplification.setOnClickListener(this);
        btn_list.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        btn_alarm.setOnClickListener(this);
        btn_stopplay.setOnClickListener(this);
        btn_return.setOnClickListener(this);
        //四个方向键加监听
        button_down.setOnTouchListener(this);
        button_up.setOnTouchListener(this);
        button_left.setOnTouchListener(this);
        button_right.setOnTouchListener(this);
        //放大 缩小 键
        button_enlarge.setOnClickListener(this);
        button_narrow.setOnClickListener(this);
        //四个surfaceview加监听

        //三个最外层的监听
        four_surfaceview_parent_relativelayout.setOnClickListener(this);
        icone_relativtelayout_title.setOnClickListener(this);
        //四个视频域加监听

        //第一个surfaceview的裁剪监听
        get_surfaceview_video_picture_first.setOnClickListener(this);


        np1 = new NodePlayer(this);
        np2 = new NodePlayer(this);
        np3 = new NodePlayer(this);
        np4 = new NodePlayer(this);

        //查询播放视图
        npv1 = (NodePlayerView) findViewById(R.id.first_view);
        npv2 = (NodePlayerView) findViewById(R.id.second_view);
        npv3 = (NodePlayerView) findViewById(R.id.third_view);
        npv4 = (NodePlayerView) findViewById(R.id.fourth_view);

        //设置视图渲染器模式
        npv1.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        npv2.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        npv3.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        npv4.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);

        //将播放视图绑定到播放器
        np1.setPlayerView(npv1);
        np2.setPlayerView(npv2);
        np3.setPlayerView(npv3);
        np4.setPlayerView(npv4);

        fr1.setOnClickListener(this);
        fr2.setOnClickListener(this);
        fr3.setOnClickListener(this);
        fr4.setOnClickListener(this);


    }

    /**
     * 电量 wifi监听
     */
    private void initDisplayInformation() {

        //注册广播监听电池电量
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
                    updateUi(network_pic, R.mipmap.icon_network);
                } else if (wifiNum > -70 && wifiNum <= -50) {
                    updateUi(network_pic, R.mipmap.icon_network_a);
                } else if (wifiNum < -70) {
                    updateUi(network_pic, R.mipmap.icon_network_b);
                } else if (wifiNum == -200) {
                    updateUi(network_pic, R.mipmap.icon_network_disable);
                }
            }
        });
        registerReceiver(wifiInformationReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    /**
     * 加载数据
     */
    private void initRetrieveVideoData() {
        //取出本地保存的sharepreferences的video信息
        String result = (String) SharedPreferencesUtils.getObject(mContext, "video_result", "");
        if (result.equals("") || result == null) {
            Logutils.i("no get Data");
            return;
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<List<VideoBen>>() {
            }.getType();
            List<VideoBen> alterSamples = new ArrayList<VideoBen>();
            alterSamples = gson.fromJson(result, type);
            listResources = alterSamples;
//            Logutils.i("list:" + listResources.size());
//
//            for (VideoBen v : listResources) {
//                Logutils.i("v:" + v.toString());
//            }

            if (listResources != null && listResources.size() > 0) {
                pm = new PageModel(listResources, 4);
                subList = pm.getObjects(videoCurrentPage);
                if (subList != null && subList.size() > 0) {
                    showVideoInformation(subList);
                    np1.setInputUrl(subList.get(0).getRtsp());
                    np1.setAudioEnable(false);
                    np1.setReceiveAudio(false);
                    np1.start();
                    np2.setInputUrl(subList.get(1).getRtsp());
                    np2.setAudioEnable(false);
                    np2.setReceiveAudio(false);
                    np2.start();
                    np3.setInputUrl(subList.get(2).getRtsp());
                    np3.setAudioEnable(false);
                    np3.setReceiveAudio(false);
                    np3.start();
                    np4.setInputUrl(subList.get(3).getRtsp());
                    np4.setAudioEnable(false);
                    np4.setReceiveAudio(false);
                    np4.start();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideLoadingInfor();
                        }
                    }, 3 * 1000);

                } else {
                    Logutils.i("截取失败...");
                }
            } else {
                Logutils.i("listResources size 0");
            }
        }
    }

    /**
     * 更改ui
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
     * 显示视频当前名称
     */
    private void showVideoInformation(List<VideoBen> subList) {
        first_text.setText(subList.get(0).getName());
        second_text.setText(subList.get(1).getName());
        third_text.setText(subList.get(2).getName());
        fourth_text.setText(subList.get(3).getName());
    }

    /**
     * 得到当前播放 的rtsp
     */
    public void getRtsp(final List<VideoBen> v) {
        for (VideoBen vv : v) {
            String rtsp = vv.getRtsp();
            Logutils.i("rtsp" + rtsp);
            if (!TextUtils.isEmpty(rtsp)) {
                rtspResources.add(rtsp);
            } else {
                rtspResources.add("no rtsp url");
            }
        }
    }

    /**
     * 初始化播放器
     */
    public void initPlayer(List<VideoBen> subList) {
        if (subList.size() > 0) {

            if (np1 != null) {
                np1.pause();
                np1.stop();
                np1.setPlayerView(npv1);
                np1.setAudioEnable(false);
                np1.setReceiveAudio(false);
                np1.setInputUrl(subList.get(0).getRtsp());
                np1.start();
            }

            if (np2 != null) {
                np2.pause();
                np2.stop();
                np2.setPlayerView(npv2);
                np2.setAudioEnable(false);
                np2.setReceiveAudio(false);
                np2.setInputUrl(subList.get(1).getRtsp());
                np2.start();
            }

            if (np3 != null) {
                np3.pause();
                np3.stop();
                np3.setPlayerView(npv3);
                np3.setAudioEnable(false);
                np3.setReceiveAudio(false);
                np3.setInputUrl(subList.get(2).getRtsp());
                np3.start();
            }

            if (np4 != null) {
                np4.pause();
                np4.stop();
                np4.setPlayerView(npv4);
                np4.setAudioEnable(false);
                np4.setReceiveAudio(false);
                np4.setInputUrl(subList.get(3).getRtsp());
                np4.start();
            }


        } else {
            Logutils.i("subList集合为null");
        }

    }

    /**
     * 下页
     * 向下翻页时要考虑显示条数会小于4的情况
     */
    private void nextPage() {
        videoCurrentPage++;
        if (pm != null && pm.isHasNextPage()) {
            showLoadingInfor();
            List<VideoBen> nextData = pm.getObjects(videoCurrentPage);
            subList = nextData;
            if (subList != null && subList.size() > 0) {
                if (subList.size() == 4) {
                    for ( VideoBen v:subList){
                        Logutils.i("vvvvvvvvv----->:"+v.toString());
                    }
                    showVideoInformation(subList);
                    initPlayer(subList);
                    delayHideLoading(3);
                } else if (subList.size() == 1) {
                    if (np1 != null) {
                        np1.pause();
                        np1.stop();
                        np1.setPlayerView(npv1);
                        np1.setAudioEnable(false);
                        np1.setReceiveAudio(false);
                        np1.setInputUrl(subList.get(0).getRtsp());
                        np1.start();
                        first_text.setText(subList.get(0).getName());
                    }
                    if (np2 != null) {
                        np2.pause();
                        np2.stop();
                        np2.release();
                        second_text.setText("");
                    }
                    if (np3 != null) {
                        np3.pause();
                        np3.stop();
                        np3.release();
                        third_text.setText("");
                    }
                    if (np4 != null) {
                        np4.pause();
                        np4.stop();
                        np4.release();
                        fourth_text.setText("");
                    }

                    Logutils.i("1");
                    delayHideLoading(3);

                } else if (subList.size() == 2) {
                    if (np1 != null) {
                        np1.pause();
                        np1.stop();
                        np1.setPlayerView(npv1);
                        np1.setAudioEnable(false);
                        np1.setReceiveAudio(false);
                        np1.setInputUrl(subList.get(0).getRtsp());
                        np1.start();
                        first_text.setText(subList.get(0).getName());
                    }
                    if (np2 != null) {
                        np2.pause();
                        np2.stop();
                        np2.setPlayerView(npv2);
                        np2.setAudioEnable(false);
                        np2.setReceiveAudio(false);
                        np2.setInputUrl(subList.get(1).getRtsp());
                        np2.start();
                        second_text.setText(subList.get(1).getName());
                    }
                    if (np3 != null) {
                        np3.pause();
                        np3.stop();
                        np3.release();
                        third_text.setText("");
                    }
                    if (np4 != null) {
                        np4.pause();
                        np4.stop();
                        np4.release();
                        fourth_text.setText("");
                    }
                    Logutils.i("2");
                    delayHideLoading(3);
                } else if (subList.size() == 3) {

                    if (np1 != null) {
                        np1.pause();
                        np1.stop();
                        np1.setPlayerView(npv1);
                        np1.setAudioEnable(false);
                        np1.setReceiveAudio(false);
                        np1.setInputUrl(subList.get(0).getRtsp());
                        np1.start();
                        first_text.setText(subList.get(0).getName());
                    }
                    if (np2 != null) {
                        np2.pause();
                        np2.stop();
                        np2.setPlayerView(npv2);
                        np2.setAudioEnable(false);
                        np2.setReceiveAudio(false);
                        np2.setInputUrl(subList.get(1).getRtsp());
                        np2.start();
                        second_text.setText(subList.get(1).getName());
                    }
                    if (np3 != null) {
                        np3.pause();
                        np3.stop();
                        np3.setPlayerView(npv3);
                        np3.setAudioEnable(false);
                        np3.setReceiveAudio(false);
                        np3.setInputUrl(subList.get(2).getRtsp());
                        np3.start();
                        third_text.setText(subList.get(2).getName());
                    }
                    if (np4 != null) {
                        np4.pause();
                        np4.stop();
                        np4.release();
                        fourth_text.setText("");
                    }
                    Logutils.i("3");
                    delayHideLoading(3);
                }
            }
        } else {
            Logutils.i("最后一页");
            return;
        }
    }

    /**
     * 上页
     * <p>
     * 向上翻页时不考虑页面条数会小于4的情况
     */
    private void lastPage() {

        videoCurrentPage--;
        if (pm != null && pm.isHasPreviousPage()) {
            showLoadingInfor();
            List<VideoBen> nextData = pm.getObjects(videoCurrentPage);
            subList = nextData;
            Logutils.i("v:" + videoCurrentPage);
            Logutils.i("sub:" + subList.size());
            if (subList != null && subList.size() > 0) {
                if (subList.size() == 4) {
                    for ( VideoBen v:subList){
                        Logutils.i("vvvvvvvvv----->:"+v.toString());
                    }
                    Logutils.i("4");
                    showVideoInformation(subList);
                    initPlayer(subList);
                    delayHideLoading(3);
                }
            }
        } else {
            Logutils.i("最前面的一页");
            return;
        }
    }

    /**
     * 圆形进度条及提示显示
     */
    public void showLoadingInfor() {
        first_progressbar.setVisibility(View.VISIBLE);
        first_show_loadind.setVisibility(View.VISIBLE);
        first_show_loadind.setText("Loading...");
        second_progressbar.setVisibility(View.VISIBLE);
        second_show_loadind.setVisibility(View.VISIBLE);
        second_show_loadind.setText("Loading...");
        third_progressbar.setVisibility(View.VISIBLE);
        third_show_loadind.setVisibility(View.VISIBLE);
        third_show_loadind.setText("Loading...");
        fourth_progressbar.setVisibility(View.VISIBLE);
        fourth_show_loadind.setVisibility(View.VISIBLE);
        fourth_show_loadind.setText("Loading...");
    }

    /**
     * 圆形进度条及提示隐藏
     */
    public void hideLoadingInfor() {
        first_progressbar.setVisibility(View.GONE);
        first_show_loadind.setVisibility(View.GONE);
        second_progressbar.setVisibility(View.GONE);
        second_show_loadind.setVisibility(View.GONE);
        third_progressbar.setVisibility(View.GONE);
        third_show_loadind.setVisibility(View.GONE);
        fourth_progressbar.setVisibility(View.GONE);
        fourth_show_loadind.setVisibility(View.GONE);

    }


    /**
     * 延时隐藏进度条和信息
     */
    public void delayHideLoading(int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadingInfor();
            }
        }, time * 1000);
    }

    /**
     * 加载list
     */
    private void initListViewData() {
        if (listResources.size() > 0) {
            relativelayout_listview.setVisibility(View.VISIBLE);
            show_relativelayout_all_button.setVisibility(View.GONE);
            relativelayout_control.setVisibility(View.GONE);
            final VideoResourcesListAda ada = new VideoResourcesListAda(listResources, mContext);
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

            show_listresources.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    // initOneRtsp(listResources.get(i));

                    relativelayout_listview.setVisibility(View.GONE);
                    show_relativelayout_all_button.setVisibility(View.VISIBLE);
                    relativelayout_control.setVisibility(View.VISIBLE);
                    VideoBen v = listResources.get(i);
                    if (firstViewSelect) {
                        first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                        firstViewSelect = false;

                        first_text.setText(v.getName());
                        first_progressbar.setVisibility(View.VISIBLE);
                        first_show_loadind.setVisibility(View.VISIBLE);
                        if (np1 != null) {
                            np1.pause();
                            np1.stop();
                            np1.setPlayerView(npv1);
                            np1.setAudioEnable(false);
                            np1.setReceiveAudio(false);
                            np1.setInputUrl(v.getRtsp());
                            np1.start();
                            first_text.setText(v.getName());
                            delayHideLoading(3);
                        }

                    } else if (secondViewSelect == true) {
                        second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                        secondViewSelect = false;
                        second_text.setText(v.getName());
                        second_progressbar.setVisibility(View.VISIBLE);
                        second_show_loadind.setVisibility(View.VISIBLE);

                        if (np2 != null) {
                            np2.pause();
                            np2.stop();
                            np2.setPlayerView(npv2);
                            np2.setAudioEnable(false);
                            np2.setReceiveAudio(false);
                            np2.setInputUrl(v.getRtsp());
                            np2.start();
                            second_text.setText(v.getName());
                            delayHideLoading(3);
                        }
                    } else if (thirdViewSelect == true) {
                        third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                        thirdViewSelect = false;
                        third_text.setText(v.getName());
                        third_progressbar.setVisibility(View.VISIBLE);
                        third_show_loadind.setVisibility(View.VISIBLE);
                        if (np3 != null) {
                            np3.pause();
                            np3.stop();
                            np3.setPlayerView(npv3);
                            np3.setAudioEnable(false);
                            np3.setReceiveAudio(false);
                            np3.setInputUrl(v.getRtsp());
                            np3.start();
                            fourth_text.setText(v.getName());
                            delayHideLoading(3);
                        }
                    } else if (fourthViewSelect == true) {
                        fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
                        fourthViewSelect = false;
                        fourth_text.setText(v.getName());
                        fourth_progressbar.setVisibility(View.VISIBLE);
                        fourth_show_loadind.setVisibility(View.VISIBLE);
                        if (np4 != null) {
                            np4.pause();
                            np4.stop();
                            np4.setPlayerView(npv4);
                            np4.setAudioEnable(false);
                            np4.setReceiveAudio(false);
                            np4.setInputUrl(v.getRtsp());
                            np4.start();
                            fourth_text.setText(v.getName());
                            delayHideLoading(3);
                        }
                    } else {
                        Toast.makeText(MultiScreenActivity.this, "未选中窗口...", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        } else {
            Logutils.i("no data");
        }
    }

    /**
     * 向服务器发送报警
     */
    private void alarmFromVideoBen(VideoBen videoBen) {
        SendAlarmToServer sendAlarmToServer = new SendAlarmToServer(videoBen);
        new Thread(sendAlarmToServer).start();
    }

    /**
     * 选中窗口后放大
     */
    public void whichWindownSelect(boolean isSelected) {
        Intent intent = new Intent(mContext, SingleScreenActivity.class);
        if (firstViewSelect == true) {
            first_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
            if (subList != null && subList.size() > 0) {
                intent.putExtra("url", subList.get(0).getRtsp());
            }
            firstViewSelect = false;
        } else if (secondViewSelect == true) {
            second_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
            if (subList != null && subList.size() > 0) {
                intent.putExtra("url", subList.get(1).getRtsp());
            }
            secondViewSelect = false;
        } else if (thirdViewSelect == true) {
            third_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
            if (subList != null && subList.size() > 0) {
                intent.putExtra("url", subList.get(2).getRtsp());
            }
            thirdViewSelect = false;
        } else if (fourthViewSelect == true) {
            fourth_surfaceview_relativelayout.setBackgroundResource(R.drawable.video_relativelayout_bg_shape);
            if (subList != null && subList.size() > 0) {
                intent.putExtra("url", subList.get(3).getRtsp());
            }
            fourthViewSelect = false;
        }
        mContext.startActivity(intent);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
            case R.id.button_down_show:
                if (firstViewSelect) {

//                    if (isSuporrtPtz == true) {
////                        if (MotionEvent.ACTION_DOWN == event.getAction()) {
////                            ControlPtz controlPtz = new ControlPtz(ptz_url, token, "top", 0.00, 0.01);
////                            controlPtz.start();
////                        } else if (MotionEvent.ACTION_UP == event.getAction()) {
////                            ControlPtz controlPtz = new ControlPtz(ptz_url, token, "stop", 0.00, 0.00);
////                            controlPtz.start();
////                        }
//                    }

                } else if (secondViewSelect) {
                    Logutils.i("second selected"+isSuporrtPtz);

                } else if (thirdViewSelect) {
                    Logutils.i("third selected"+isSuporrtPtz);

                } else if (fourthViewSelect) {
                    Logutils.i("fourth selected"+isSuporrtPtz);

                } else {
                    Logutils.i("没选中窗口....");
                }
                break;
            case R.id.button_up_show:
                break;
            case R.id.button_left_show:
                break;
            case R.id.button_right_show:
                break;
        }

        return false;
    }
}
