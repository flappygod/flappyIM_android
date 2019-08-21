package com.flappygo.flappyim.Reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Listener.NotificationClickListener;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Service.FlappyService;
import com.google.gson.Gson;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class ActionReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String msg = bundle.getString("msg");
            ChatMessage chatMessage = GsonTool.jsonObjectToModel(msg, ChatMessage.class);
            NotificationClickListener listener = FlappyService.getNotificationClickListener();
            if (listener != null) {
                listener.notificationClicked(chatMessage);
            }
        } catch (Exception e) {
            //打印消息
            System.out.println(e.getMessage());
        }
    }


}
