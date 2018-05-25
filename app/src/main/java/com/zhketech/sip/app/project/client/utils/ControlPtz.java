package com.zhketech.sip.app.project.client.utils;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Root on 2018/5/19.
 *
 * 去于云台控制中心
 * parms:
 * ptz_url
 * token
 *
 *
 */

public class ControlPtz implements Runnable {

    //移动指令
    public static final String PTZ_MOVE = "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"><s:Header/><s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><ContinuousMove xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\"><ProfileToken>%s</ProfileToken><Velocity><PanTilt x=\"%s\" y=\"%s\" space=\"http://www.onvif.org/ver10/tptz/PanTiltSpaces/VelocityGenericSpace\" xmlns=\"http://www.onvif.org/ver10/schema\"/></Velocity></ContinuousMove></s:Body></s:Envelope>";
    //缩放指令
    public static final String PTZ_ZOOM = "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"><s:Header/><s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><ContinuousMove xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\"><ProfileToken>%s</ProfileToken><Velocity><Zoom x=\"%s\" space=\"http://www.onvif.org/ver10/tptz/ZoomSpaces/VelocityGenericSpace\" xmlns=\"http://www.onvif.org/ver10/schema\"/></Velocity></ContinuousMove></s:Body></s:Envelope>";
    //停止指令
    public static final String PTZ_STOP = "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\"><s:Header/><s:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><Stop xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\"><ProfileToken>%s</ProfileToken><PanTilt>%s</PanTilt><Zoom>%s</Zoom></Stop></s:Body></s:Envelope>";
    HttpURLConnection mUrlConn;
    String ptz_url;
    String token;
    String flage;
    double x;
    double y;

    public ControlPtz(String ptz_url, String token, String flage, double x, double y) {
        this.ptz_url = ptz_url;
        this.token = token;
        this.flage = flage;
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
        synchronized (this) {//同步锁，防止同时发送指令
            switch (flage) {
                case "left":
                    moveCamera();
                    break;
                case "right":
                    moveCamera();
                    break;

                case "top":
                    moveCamera();
                    break;
                case "below":
                    moveCamera();
                    break;
                case "zoom_b":
                    zoomCamera();
                    break;
                case "zoom_s":
                    zoomCamera();
                    break;
                case "stop":
                    stopCamera();
                    break;

            }
        }
    }

    //
    public void start() {
        new Thread(this).start();
    }

    /**
     * 移动摄像头
     */
    public void moveCamera() {
        try {
            initConn(ptz_url);
            mUrlConn.connect();
            String content = String.format(PTZ_MOVE, token, x, y);
            Logutils.i(content);
            mUrlConn.getOutputStream().write(content.getBytes());
            InputStream inStream = mUrlConn.getInputStream();
            String res = inputStream2String(inStream);
        } catch (Exception e) {
            Logutils.i("Http_Exception:" + e.getMessage());
        }
    }

    /**
     * 缩放摄像头
     */
    public void zoomCamera() {
        try {
            initConn(ptz_url);
            mUrlConn.connect();
            String content = String.format(PTZ_ZOOM, token, x);
            Logutils.i(content);
            mUrlConn.getOutputStream().write(content.getBytes());
            InputStream inStream = mUrlConn.getInputStream();
            String res = inputStream2String(inStream);
            Logutils.i("result:" + res);
        } catch (Exception e) {
            Logutils.i("Http_Exception:" + e.getMessage());
        }
    }

    /**
     * 停止摄像头动作
     */
    public void stopCamera() {
        try {
            initConn(ptz_url);
            mUrlConn.connect();
            String content = String.format(PTZ_STOP, token, true, true);
            Logutils.i(content);
            mUrlConn.getOutputStream().write(content.getBytes());
            InputStream inStream = mUrlConn.getInputStream();
            String res = inputStream2String(inStream);
            Logutils.i("result:" + res);
        } catch (Exception e) {
            Logutils.i("Http_Exception:" + e.getMessage());
        }
    }


    private void initConn(String url) {
        try {
            URL url1 = new URL(url);
            mUrlConn = (HttpURLConnection) url1.openConnection();
            mUrlConn.setDoInput(true);
            mUrlConn.setDoOutput(true);
            mUrlConn.setRequestMethod("POST");
            mUrlConn.setUseCaches(false);
            mUrlConn.setInstanceFollowRedirects(true);
            mUrlConn.setRequestProperty("Content-Type",
                    "application/soap+xml; charset=utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String inputStream2String(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
}
