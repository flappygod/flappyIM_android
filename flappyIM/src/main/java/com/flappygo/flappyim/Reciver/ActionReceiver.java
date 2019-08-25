package com.flappygo.flappyim.Reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Service.FlappyService;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.CATEGORY_LAUNCHER;

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

        //跳转主界面
        try {
            List<ResolveInfo> activitys = getActivities(context);
            //跳转进入主界面
            if (activitys.size() > 0) {
                Intent main = new Intent();
                main.setClassName(activitys.get(0).activityInfo.packageName,
                        activitys.get(0).activityInfo.name);
                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(main);
            }
        } catch (Exception ex) {
            Log.e("主界面跳转失败", ex.getMessage());
        }

        //通知消息发送
        try {
            //获取到相应的
            Bundle bundle = intent.getExtras();
            //消息
            String msg = bundle.getString("msg");
            //通知消息
            if (msg != null) {
                //缓存消息
                DataManager.getInstance().saveNotificationClick(msg);
                //保存这个消息，直到设置回调或则其他
                FlappyService.getInstance().notifyClicked();
            }
        } catch (Exception ex) {
            //打印消息
            Log.e("消息保存失败", ex.getMessage());
        }
    }


    //获取所有的Launcher界面
    private ArrayList<ResolveInfo> getActivities(Context ctx) {
        ArrayList<ResolveInfo> result = new ArrayList<ResolveInfo>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setPackage(ctx.getPackageName());
        intent.addCategory(CATEGORY_LAUNCHER);
        for (ResolveInfo info : ctx.getPackageManager().queryIntentActivities(intent, 0)) {
            result.add(info);
        }
        return result;
    }

}
