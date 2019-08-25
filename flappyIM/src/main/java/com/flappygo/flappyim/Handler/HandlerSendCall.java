package com.flappygo.flappyim.Handler;


import android.os.Handler;
import android.os.Message;

import com.flappygo.flappyim.Callback.FlappyIMCallback;

//用户发送消息时候的handler
public class HandlerSendCall extends Handler {


    //发送失败
    public static final int SEND_FAILURE = 0;

    //发送成功
    public static final int SEND_SUCCESS = 1;


    //真实的回调
    FlappyIMCallback<String> callback;

    private String messageID;

    //构造器
    public HandlerSendCall(FlappyIMCallback<String> callback,String messageID) {
        this.callback = callback;
        this.messageID=messageID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    //执行消息
    public void handleMessage(Message message) {
        if (message.what == SEND_SUCCESS) {
            //成功
            if (callback != null) {
                callback.success((String) message.obj);
            }
        } else {
            //失败
            if (callback != null) {
                callback.failure((Exception) message.obj, message.what);
            }
        }
    }

}
