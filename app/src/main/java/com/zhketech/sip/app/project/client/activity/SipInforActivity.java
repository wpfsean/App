package com.zhketech.sip.app.project.client.activity;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.adapter.SipInforRecyclerViewGridAdapter;
import com.zhketech.sip.app.project.client.beans.SipBean;
import com.zhketech.sip.app.project.client.beans.SipClient;
import com.zhketech.sip.app.project.client.beans.SipGroupBean;
import com.zhketech.sip.app.project.client.callbacks.SipRequestCallback;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.SipHttpUtils;
import com.zhketech.sip.app.project.client.utils.SpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 点击分sipgroup后，进入的详细 的sip界面
 */


public class SipInforActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.sip_group_recyclearview)
    public RecyclerView recy;

    @BindView(R.id.sip_group_back_layout)
    public ImageButton back;

    Context mContext;
    SpaceItemDecoration sp;
    public static final String SIP_LIST = "http://192.168.0.60:8080/openapi/localuser/list?{%22syskey%22:%22123456%22}";
    List<SipClient> mList = new ArrayList<>();
    List<SipBean> sipListResources = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideStatusBar();
        setContentView(R.layout.activity_sip_group);
        ButterKnife.bind(this);
        mContext = this;
        back.setOnClickListener(this);
        int group_id = getIntent().getIntExtra("group_id", 0);
        sp = new SpaceItemDecoration(10, 10);
        if (group_id != 0) {
            SipRequestCallback sipRequestCallback = new SipRequestCallback(mContext, group_id + "", new SipRequestCallback.SipListern() {
                @Override
                public void getDataListern(List<SipBean> sipList) {
                    if (sipList != null && sipList.size() > 0) {
                        sipListResources = sipList;
                        Logutils.i("sipList:" + sipList.size() + "\n" + sipList.toString());
                        getHttpdata();
                    }
                }
            });
            new Thread(sipRequestCallback).start();
        }

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Logutils.i("infor");
                getHttpdata();
            }
        };
        timer.schedule(timerTask, 0, 3000);
    }

    public void getHttpdata() {

        if (mList != null && mList.size() > 0) {
            mList.clear();
        }

        SipHttpUtils sipHttpUtils = new SipHttpUtils(SIP_LIST, new SipHttpUtils.GetHttpData() {
            @Override
            public void httpData(String result) {
                if (!TextUtils.isEmpty(result)) {
                    if (!result.contains("Execption") && !result.contains("code != 200")) {
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String username = jsonObject.getString("usrname");
                                String description = jsonObject.getString("description");
                                String dispname = jsonObject.getString("dispname");
                                String addr = jsonObject.getString("addr");
                                String state = jsonObject.getString("state");
                                String userAgent = jsonObject.getString("userAgent");
                                SipClient sipClient = new SipClient(username, description, dispname, addr, state, userAgent);
                                mList.add(sipClient);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SipInforRecyclerViewGridAdapter recyclerViewGridAdapter = new SipInforRecyclerViewGridAdapter(mContext, mList, sipListResources);
                                    recy.setAdapter(recyclerViewGridAdapter);
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
                                    gridLayoutManager.setReverseLayout(false);
                                    gridLayoutManager.setOrientation(GridLayout.VERTICAL);
                                    recy.removeItemDecoration(sp);
                                    recy.addItemDecoration(sp);
                                    recy.setLayoutManager(gridLayoutManager);
                                    recyclerViewGridAdapter.setItemClickListener(new SipInforRecyclerViewGridAdapter.MyItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            Logutils.i("positon:" + position);
                                        }
                                    });
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        sipHttpUtils.start();
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

        switch (v.getId()){
            case R.id.sip_group_back_layout:
                this.finish();
                break;
        }

    }
}
