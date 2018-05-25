package com.zhketech.sip.app.project.client.activity;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.adapter.RecyclerViewGridAdapter;
import com.zhketech.sip.app.project.client.beans.SipClient;
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
 * sip组状态
 */

public class SipGroupActivity extends AppCompatActivity {

    @BindView(R.id.sip_group_recyclearview)
    public RecyclerView sip_group_recyclearview;
    Context mContext;

    public static final String SIP_LIST = "http://192.168.0.60:8080/openapi/localuser/list?{%22syskey%22:%22123456%22}";
    List<SipClient> mList = new ArrayList<>();

    SpaceItemDecoration sp;

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

        getHttpdata();
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
                            Logutils.i("Mlist:" + mList.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RecyclerViewGridAdapter recyclerViewGridAdapter = new RecyclerViewGridAdapter(mContext, mList);
                                    sip_group_recyclearview.setAdapter(recyclerViewGridAdapter);
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
                                    gridLayoutManager.setReverseLayout(false);
                                    gridLayoutManager.setOrientation(GridLayout.VERTICAL);
                                    sip_group_recyclearview.addItemDecoration(sp);
                                    sip_group_recyclearview.setLayoutManager(gridLayoutManager);
                                    recyclerViewGridAdapter.setItemClickListener(new RecyclerViewGridAdapter.MyItemClickListener() {
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

}
