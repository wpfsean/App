package com.zhketech.sip.app.project.client.rtsp.record;

import android.content.Context;

import com.zhketech.sip.app.project.client.rtsp.media.VideoMediaCodec;


/**
 * Created by qingli on 2018/3/26
 * 摄像机数据编码线程
 */

public class CameraRecord extends Thread{
  private Context context;
  private VideoMediaCodec videoMediaCodec;

  public CameraRecord(Context context){
      this.context=context;
      videoMediaCodec = new VideoMediaCodec();
  }



    @Override
    public void run() {
      videoMediaCodec.prepare();
      videoMediaCodec.isRun(true);
      videoMediaCodec.getBuffers();
    }
}
