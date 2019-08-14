package com.flappygo.flappyim.Tools.Upload;

import android.os.Handler;
import android.os.Message;

/**
 * Created by yang on 2017/2/9.
 */
public class UploadHandler extends Handler {
    //回调
    private ProgressCallBack callBack;
    //回调
    public interface ProgressCallBack {
        void uploading(int progress);
    }
    //上传handler
    public UploadHandler(ProgressCallBack callBack) {
        this.callBack = callBack;
    }
    //消息
    public void handleMessage(Message message) {
        if (message.what == 0) {
            int progress = message.arg1;
            if (callBack != null) {
                callBack.uploading(progress);
            }
        }
    }

}
