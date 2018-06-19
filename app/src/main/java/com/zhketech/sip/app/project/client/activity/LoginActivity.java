package com.zhketech.sip.app.project.client.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Toast;

import com.zhketech.sip.app.project.client.R;
import com.zhketech.sip.app.project.client.beans.LoginBean;
import com.zhketech.sip.app.project.client.utils.LoginThread;
import com.zhketech.sip.app.project.client.utils.Logutils;
import com.zhketech.sip.app.project.client.utils.SharedPreferencesUtils;
import com.zhketech.sip.app.project.client.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edit_username_layout)
    EditText username;
    @BindView(R.id.edit_userpass_layout)
    EditText userpass;

    @BindView(R.id.remember_pass_layout)
    Checkable remeemberPass;

    @BindView(R.id.auto_login_layout)
    Checkable autoLogin;


    boolean isRemember;
    boolean isAuto;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;

        //取出上次成功登陆的用户名和密码
        String logined_name = (String) SharedPreferencesUtils.getObject(context,"username","");
        String logined_pass = (String) SharedPreferencesUtils.getObject(context,"userpass","");
        //输入框显示用户名和密码
        username.setText(logined_name);
        userpass.setText(logined_pass);
        //判断是否是自动 登录，如果是自动登录就直接跳转到主页面
        boolean isAutoLogin = (boolean) SharedPreferencesUtils.getObject(context,"auto",false);
        if (isAutoLogin == true){
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this,Main.class);
            context.startActivity(intent);
        }
    }

    @OnClick(R.id.userlogin_button_layout)
    public void login(View viwe) {
        //判断是否选中记住密码和自动登录的复选框
        isRemember = remeemberPass.isChecked();
        isAuto = autoLogin.isChecked();
        Logutils.i("..\n" + isRemember + "\n" + isAuto);
        //获取用户名和密码
        final String name = username.getText().toString().trim();
        final String pass = userpass.getText().toString().trim();
        //判断是否为空
        if ((!TextUtils.isEmpty(name)) && (!TextUtils.isEmpty(pass))) {
            //new新建的实体类
            LoginBean loginBean = new LoginBean();
            loginBean.setUsername(name);
            loginBean.setPass(pass);
            loginBean.setIp("19.0.0.79");
            //登录
            LoginThread loginThread = new LoginThread(LoginActivity.this, loginBean, new LoginThread.IsLoginListern() {
                @Override
                public void loginStatus(String status) {
                    String result = status;
                    if (!TextUtils.isEmpty(result)) {
                        if (result.equals("success")) {//成功
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                                }
                            });

                            //如果复选了就存储相关的信息
                            if (isRemember == true){
                                SharedPreferencesUtils.putObject(context,"username",name);
                                SharedPreferencesUtils.putObject(context,"userpass",pass);
                            }
                            if (isAuto){
                                SharedPreferencesUtils.putObject(context,"auto",true);
                            }

                                try {
                                    Thread.sleep(2 * 1000);
                                    Intent intent = new Intent();
                                    intent.setClass(LoginActivity.this, Main.class);
                                    startActivity(intent);
                                } catch (InterruptedException e) {
                                    Logutils.e("Login error");
                                }
                        } else {//失败
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            });
            loginThread.start();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShort("Null");
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LoginActivity.this.finish();
    }
}
