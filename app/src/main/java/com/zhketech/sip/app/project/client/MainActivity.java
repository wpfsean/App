package com.zhketech.sip.app.project.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.zhketech.sip.app.project.client.activity.SingleCallActivity;
import com.zhketech.sip.app.project.client.activity.SipGroupActivity;
import com.zhketech.sip.app.project.client.activity.StatusActivity;
import com.zhketech.sip.app.project.client.linphone.Utility;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.signle_phone).setOnClickListener(this);
        findViewById(R.id.more_phone).setOnClickListener(this);
        TextView username = (TextView) findViewById(R.id.showusernaem);
        String showtext = "用户名：" + Utility.getUsername() + " \n密码：" + Utility.getPassword() + " \nHOST:" + Utility.getHost();
        username.setText(showtext);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signle_phone:
                Intent intent = new Intent(this, SingleCallActivity.class);
                intent.putExtra("isCall", true);
                startActivity(intent);
                break;
            case R.id.more_phone://多人语音
                Intent intent1 = new Intent(this, SipGroupActivity.class);
                startActivity(intent1);
                break;
        }
    }


}
