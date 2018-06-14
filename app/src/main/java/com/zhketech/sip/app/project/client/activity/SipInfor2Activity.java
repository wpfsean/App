package com.zhketech.sip.app.project.client.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.adapter.SipInforRecyclerViewGridAdapter;
import com.zhketech.sip.app.project.client.adapter.VideoResourcesListAda;
import com.zhketech.sip.app.project.client.beans.SipBean;
import com.zhketech.sip.app.project.client.beans.SipClient;
import com.zhketech.sip.app.project.client.callbacks.SipRequestCallback;
import com.zhketech.sip.app.project.client.global.AppConfig;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.SharedPreferencesUtils;
import com.zhketech.sip.app.project.client.utils.SipHttpUtils;
import com.zhketech.sip.app.project.client.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class SipInfor2Activity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.gridview)
    public GridView gridview;

    @BindView(R.id.sip_group_lastpage_layout)
    ImageButton sip_group_lastpage_layout;

    @BindView(R.id.sip_group_nextpage_layout)
    ImageButton sip_group_nextpage_layout;

    @BindView(R.id.sip_group_refresh_layout)
    ImageButton sip_group_refresh_layout;

    @BindView(R.id.sip_group_back_layout)
    ImageButton sip_group_back_layout;

    @BindView(R.id.voice_intercom_icon_layout)
    public ImageButton voice_button;

    @BindView(R.id.video_intercom_layout)
    public ImageButton video_button;


    Context mContext;
    List<SipClient> mList = new ArrayList<>();
    List<SipBean> sipListResources = new ArrayList<>();
    List<SipClient> adapterList = new ArrayList<>();
    MyAdapter ada = null;
    int selected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideStatusBar();
        setContentView(R.layout.activity_sip_infor2);
        ButterKnife.bind(this);
        mContext = this;
        initViewAndListern();
        int group_id = getIntent().getIntExtra("group_id", 0);
        if (group_id != 0) {
            SipRequestCallback sipRequestCallback = new SipRequestCallback(mContext, group_id + "", new SipRequestCallback.SipListern() {
                @Override
                public void getDataListern(List<SipBean> sipList) {

                    if (sipList != null && sipList.size() > 0) {
                        sipListResources = sipList;
                        getHttpdata();
                        Logutils.i("sipListResources:----->>>:" + sipListResources.toString());
                    }
                }
            });
            new Thread(sipRequestCallback).start();
        }

        //定时器每三秒刷新一下数据
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getHttpdata();
            }
        };
        timer.schedule(timerTask, 0, 3000);

    }

    private void initViewAndListern() {
        sip_group_lastpage_layout.setOnClickListener(this);
        sip_group_nextpage_layout.setOnClickListener(this);
        sip_group_refresh_layout.setOnClickListener(this);
        sip_group_back_layout.setOnClickListener(this);
        voice_button.setOnClickListener(this);
        video_button.setOnClickListener(this);
    }

    public void getHttpdata() {

        if (mList != null && mList.size() > 0) {
            mList.clear();
        }

        SipHttpUtils sipHttpUtils = new SipHttpUtils(AppConfig.SIP_LIST_URL, new SipHttpUtils.GetHttpData() {
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

                                    if (adapterList != null && adapterList.size() > 0) {
                                        adapterList.clear();
                                    }

                                    for (int i = 0; i < mList.size(); i++) {
                                        for (int j = 0; j < sipListResources.size(); j++) {
                                            if (mList.get(i).getUsrname().equals(sipListResources.get(j).getNumber())) {
                                                SipClient sipClient = new SipClient();
                                                sipClient.setState(mList.get(i).getState());
                                                sipClient.setUsrname(mList.get(i).getUsrname());
                                                adapterList.add(sipClient);
                                            }
                                        }
                                    }
                                    if (adapterList != null && adapterList.size() > 0) {

                                        if (ada != null) {
                                            ada = null;
                                        }
                                        ada = new MyAdapter(mContext);
                                        gridview.setAdapter(ada);
                                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                ada.setSeclection(position);
                                                ada.notifyDataSetChanged();
                                                Logutils.i("Position:" + position);
//
//                                                if (mList.get(position).getState().equals("1")) {
//                                                    Logutils.i("Position////:" + position);
                                                    selected = position;
//                                                    Logutils.i("selected:" + selected);
//                                                }

                                            }
                                        });
                                    }
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
    public void onClick(View v) {
        Intent intent = new Intent(this, SingleCallActivity.class);
        switch (v.getId()) {
            case R.id.sip_group_back_layout:
                SipInfor2Activity.this.finish();
                break;
            case R.id.sip_group_lastpage_layout:
                ToastUtils.showShort("无数据");
                break;
            case R.id.sip_group_nextpage_layout:
                ToastUtils.showShort("无数据");
                break;
            case R.id.sip_group_refresh_layout:
                getHttpdata();
                ToastUtils.showShort("已加载最新数据");
                break;
            case R.id.video_intercom_layout:

                if (adapterList != null && adapterList.size() > 0) {
                    intent.putExtra("isCall", true);
                    if (selected != -1) {
                        if (adapterList.get(selected) != null) {
                            intent.putExtra("userName", adapterList.get(selected).getUsrname());
                            intent.putExtra("isVideo", true);
                        }
                    }
                    startActivity(intent);
                }

                break;
            case R.id.voice_intercom_icon_layout:

                if (adapterList != null && adapterList.size() > 0) {
                    intent.putExtra("isCall", true);
                    if (selected != -1) {
                        if (adapterList.get(selected) != null) {
                            if (adapterList.get(selected) != null)
                                intent.putExtra("userName", adapterList.get(selected).getUsrname());
                            startActivity(intent);
                        }
                    }
                }
                break;
        }
    }

    class MyAdapter extends BaseAdapter {
        private int clickTemp = -1;
        private LayoutInflater layoutInflater;

        public MyAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return adapterList.size();
        }

        @Override
        public Object getItem(int position) {
            return adapterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setSeclection(int position) {
            clickTemp = position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.sipstatus_item, null);
                viewHolder.item_name = (TextView) convertView.findViewById(R.id.item_name);
                viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.item_layout);
                viewHolder.main_layout = convertView.findViewById(R.id.sipstatus_main_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String native_name = (String) SharedPreferencesUtils.getObject(mContext, AppConfig.SIP_NAME_NAVITE,"");
            if (!TextUtils.isEmpty(native_name)) {
                if (adapterList.get(position).getUsrname().equals(native_name)) {
                    viewHolder.item_name.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线
                    viewHolder.item_name.setTextColor(0xffDC143C);
                }
            }
            if (adapterList.get(position).getState().equals("0")) {
                viewHolder.item_name.setText("哨位名:" + adapterList.get(position).getUsrname());
                viewHolder.linearLayout.setBackgroundResource(R.mipmap.btn_lixian);
            } else if (adapterList.get(position).getState().equals("1")) {
                viewHolder.item_name.setText("哨位名:" + adapterList.get(position).getUsrname());
                viewHolder.linearLayout.setBackgroundResource(R.drawable.sip_call_select_bg);
            } else if (adapterList.get(position).getState().equals("2")) {
                viewHolder.item_name.setText("哨位名:" + adapterList.get(position).getUsrname());
                viewHolder.linearLayout.setBackgroundResource(R.mipmap.btn_zhenling);
            } else if (adapterList.get(position).getState().equals("3")) {
                viewHolder.item_name.setText("哨位名:" + adapterList.get(position).getUsrname());
                viewHolder.linearLayout.setBackgroundResource(R.mipmap.btn_tonghua);
            }

            if (clickTemp == position) {
                if (adapterList.get(position).getState().equals("1")&& !adapterList.get(position).getUsrname().equals(native_name))
                    viewHolder.main_layout.setBackgroundResource(R.drawable.sip_selected_bg);
            } else {
                viewHolder.main_layout.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }

        class ViewHolder {
            TextView item_name;
            LinearLayout main_layout;
            LinearLayout linearLayout;
        }
    }
    //hide status
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

