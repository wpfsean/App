package com.zhketech.sip.app.project.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.adapter.RecyclerViewGridAdapter;
import com.zhketech.sip.app.project.client.beans.SipGroupBean;
import com.zhketech.sip.app.project.client.callbacks.SipGroupResourcesCallback;
import com.zhketech.sip.app.project.client.global.AppConfig;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.SipHttpUtils;
import com.zhketech.sip.app.project.client.utils.SpaceItemDecoration;
import com.zhketech.sip.app.project.client.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * sip组状态
 */

public class SipGroupActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.sip_group_recyclearview)
    public RecyclerView sip_group_recyclearview;
    @BindView(R.id.sip_group_finish_icon)
    public ImageButton sip_group_finish_icon;
    @BindView(R.id.video_calls_duty_room_intercom_layout)
    public ImageButton video_calls_duty_room_intercom_layout;
    @BindView(R.id.voice_calls_duty_room_intercom_layout)
    public ImageButton voice_calls_duty_room_intercom_layout;
    @BindView(R.id.sip_group_refresh_layout)
    public ImageButton sip_group_refresh_layout;


    Context mContext;
    List<SipGroupBean> mList = new ArrayList<>();
    SpaceItemDecoration sp;
    boolean isShowGrouLayout = false;
    String callNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideStatusBar();
        setContentView(R.layout.activity_sip_group);
        ButterKnife.bind(this);
        mContext = this;
        sp = new SpaceItemDecoration(5, 20);
        initPageData();
    }

    private void initPageData() {
        sip_group_finish_icon.setOnClickListener(this);
        video_calls_duty_room_intercom_layout.setOnClickListener(this);
        voice_calls_duty_room_intercom_layout.setOnClickListener(this);
        sip_group_refresh_layout.setOnClickListener(this);
        getGroupStatusData();
        getDutyRoomData();
    }

    public void getGroupStatusData() {
        if (mList != null && mList.size() > 0) {
            mList.clear();
        }
        SipGroupResourcesCallback sipGroupResourcesCallback = new SipGroupResourcesCallback(new SipGroupResourcesCallback.SipGroupDataCallback() {
            @Override
            public void callbackSuccessData(List<SipGroupBean> dataList) {
                if (dataList != null && dataList.size() > 0) {
                    mList = dataList;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerViewGridAdapter recyclerViewGridAdapter = new RecyclerViewGridAdapter(mContext, mList);
                            sip_group_recyclearview.setAdapter(recyclerViewGridAdapter);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
                            gridLayoutManager.setReverseLayout(false);
                            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
                          //  sip_group_recyclearview.addItemDecoration(sp);
                            sip_group_recyclearview.setLayoutManager(gridLayoutManager);
                            recyclerViewGridAdapter.setItemClickListener(new RecyclerViewGridAdapter.MyItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Logutils.i("positon:" + position);
                                    int group_id = mList.get(position).getGroup_id();
                                    Intent intent = new Intent();
                                    intent.putExtra("group_id", group_id);
                                    intent.setClass(SipGroupActivity.this, SipInfor2Activity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                } else {
                    ToastUtils.showShort("未从服务器上获取到sip数据...");
                }
            }

            @Override
            public void callbackFailData(String infor) {
                if (!TextUtils.isEmpty(infor)) {
                    if (infor.contains("Execption")) {
                        //                      ToastUtils.showShort("请求数据异常,未请求到数据");
                    }
                }
            }
        });
        sipGroupResourcesCallback.start();
        isShowGrouLayout = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideStatusBar();
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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SingleCallActivity.class);
        switch (v.getId()) {
            case R.id.sip_group_finish_icon:
                SipGroupActivity.this.finish();
                break;
            case R.id.video_calls_duty_room_intercom_layout:

                if (!TextUtils.isEmpty(callNumber)) {
                    intent.putExtra("isCall", true);
                    intent.putExtra("userName", callNumber);
                    intent.putExtra("isVideo", true);
                    startActivity(intent);
                }
                break;
            case R.id.voice_calls_duty_room_intercom_layout:

                if (!TextUtils.isEmpty(callNumber)) {
                    intent.putExtra("isCall", true);
                    intent.putExtra("userName", callNumber);
                    intent.putExtra("isVideo", false);
                    startActivity(intent);
                }

                break;
            case R.id.sip_group_refresh_layout:
                getGroupStatusData();
                Toast.makeText(SipGroupActivity.this,"已刷新",Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void getDutyRoomData() {
        SipHttpUtils httpUtils = new SipHttpUtils(AppConfig.DUTY_ROOM_URL, new SipHttpUtils.GetHttpData() {
            @Override
            public void httpData(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        JSONObject data = jsonArray.getJSONObject(0);
                        String name = data.getString("name");
                        String number = data.getString("number");
                        String server = data.getString("server");
                        if (!TextUtils.isEmpty(number))
                            callNumber = number;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        httpUtils.start();
    }
}
