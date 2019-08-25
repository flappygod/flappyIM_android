package com.flappygo.flappyim.Reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Datas.DataManager;
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
            //获取到相应的
            Bundle bundle = intent.getExtras();
            //消息
            String msg = bundle.getString("msg");
            //消息通
            DataManager.getInstance().saveNotificationClick(msg);
            //保存这个消息，直到设置回调或则其他
            FlappyService.getInstance().notifyClicked();
        } catch (Exception e) {
            //打印消息
            System.out.println(e.getMessage());
        }
    }


}
