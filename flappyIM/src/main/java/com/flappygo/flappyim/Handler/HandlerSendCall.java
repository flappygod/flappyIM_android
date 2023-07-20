package com.flappygo.flappyim.Handler;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.Models.Server.ChatMessage;

//用户发送消息时候的handler
public class HandlerSendCall extends Handler {


    //发送失败
    public static final int SEND_FAILURE = 0;

    //发送成功
    public static final int SEND_SUCCESS = 1;


    //真实的回调
    FlappySendCallback<ChatMessage> callback;

    //消息内容
    private ChatMessage chatMessage;

    //构造器
    public HandlerSendCall(FlappySendCallback<ChatMessage> callback, ChatMessage message) {
        super();
        this.callback = callback;
        this.chatMessage = message;
    }


    //handle message
    public HandlerSendCall(Looper looper, FlappySendCallback<ChatMessage> callback, ChatMessage message) {
        super(looper);
        this.callback = callback;
        this.chatMessage = message;
    }


    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    //执行消息
    public void handleMessage(Message msg) {
        if (msg.what == SEND_SUCCESS) {
            //成功
            if (callback != null) {
                //消息状态更新了
                callback.success((ChatMessage) msg.obj);
            }
        } else {
            //失败
            if (callback != null) {
                callback.failure(chatMessage, (Exception) msg.obj, msg.what);
            }
        }
    }

}
