package com.zhketech.sip.app.project.client.beans;

/**
 * Created by Root on 2018/6/15.
 */

public class LoginBean {
    String username;
    String pass;
    String ip;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LoginBean(String username, String pass, String ip) {
        this.username = username;
        this.pass = pass;
        this.ip = ip;
    }

    public LoginBean() {
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "username='" + username + '\'' +
                ", pass='" + pass + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
