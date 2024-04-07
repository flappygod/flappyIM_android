package com.flappygo.flappyim.Handler;

import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.Models.Server.ChatMessage;

import android.os.Message;
import android.os.Handler;
import android.os.Looper;

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
        super(Looper.getMainLooper());
        this.callback = callback;
        this.chatMessage = message;
    }

    //获取消息
    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    //设置消息
    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    //执行消息
    public void handleMessage(Message msg) {
        //成功
        if (msg.what == SEND_SUCCESS) {
            if (callback != null) {
                callback.success((ChatMessage) msg.obj);
            }
        }
        //失败
        else {
            if (callback != null) {
                callback.failure(chatMessage, (Exception) msg.obj, msg.what);
            }
        }
    }

}
