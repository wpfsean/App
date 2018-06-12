package com.zhketech.sip.app.project.client.global;

import com.zhketech.sip.app.project.client.utils.PhoneUtils;

/**
 * Created by Root on 2018/5/16.
 */

public class AppConfig {

    public AppConfig(){
        throw  new UnsupportedOperationException("not can constructed");
    }

    //获取开锁申请标识
    public static final int GET_AMMOREQUEST_INFOR =191;

    //权限申请的标识
    public static final int PERMISSION_FLAGE= 100;

    //接收SMS的标识
    public static final int SMS_FLAGE= 1;

    //接收报警报文的标识
    public static final int ALERT_FLAGE= 2;

    //获取视频资源的标识
    public static final int VIDEO_SOURCES_FLAGE= 4;

    //获取Sip资源的标识
    public static final int SIP_SOURCES_FLAGE= 5;



    public static final String USERNAME = "admin";
    public static final String PASS = "pass";



    //子弹箱的状态
    public static  final String BOX_STATUS = "box_status";

    //服务器返回的报警状态
    public static  int ALARM_STATUS = 0;

    //本机的rtsp地址
    public static final String NATIVE_RTSP = "native_rtsp";

    //服务器ip
    public static final String IP_SERVER = "192.168.0.16";

    //本机的ip
    public static final String IP_NAVITE = "native_ip";
    //本机name
    public static final String SIP_NAME_NAVITE = "native_name";
    //本机number
    public static final String SIP_NUMBER_NAVITE = "native_number";
    //本机pass
    public static final String SIP_PASS_NAVITE = "native_pass";
    //sip服务器地址
    public static final String SIP_SERVER = "sip_server";


    //本机的guid（{1ae41588-0a4e-4838-bef5-5980e322ef54}）
    public static final String GUID_NATIVE = "guid_native";
    //本机的设备名称（WPF设备）
    public static final String NAME_NATIVE ="name_native";

    //发送报警和申请开箱的服务器ip
    public static final String  ALERM_SERVER = "192.168.0.12";

    //发送报警和申请开箱的服务器的端口
    public static final int  ALERM_PORT = 2000;

    //获取值班室数据
    public static final String DUTY_ROOM_URL = "http://192.168.0.16/dutyRoomData.php";
   // public static final String DUTY_ROOM_URL = "https://bjwesk.com/zhketech/dutyRoomData.php";


}
