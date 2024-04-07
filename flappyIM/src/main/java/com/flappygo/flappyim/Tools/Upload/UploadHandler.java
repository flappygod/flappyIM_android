package com.flappygo.flappyim.Tools.Upload;

import android.os.Handler;
import android.os.Message;
import android.os.Looper;

/******
 * 上传handler
 */
public class UploadHandler extends Handler {

    //Handle message
    public UploadHandler(ProgressCallBack callBack) {
        super(Looper.getMainLooper());
        this.callBack = callBack;
    }

    //handle message
    public UploadHandler(Looper looper, ProgressCallBack callBack) {
        super(looper);
        this.callBack = callBack;
    }

    //回调
    private final ProgressCallBack callBack;

    //回调
    public interface ProgressCallBack {
        void uploading(int progress);
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
