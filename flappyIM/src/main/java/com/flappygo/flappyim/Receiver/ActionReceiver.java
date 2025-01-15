package com.flappygo.flappyim.Receiver;

import android.content.BroadcastReceiver;
import android.content.pm.ResolveInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import static android.content.Intent.CATEGORY_LAUNCHER;

import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.FlappyImService;

import java.util.List;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class ActionReceiver extends BroadcastReceiver {

    private static final String TAG = "ActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 跳转主界面
        handleMainActivityLaunch(context);

        // 通知消息发送
        handleNotificationMessage(context, intent);
    }

    // 处理主界面跳转
    private void handleMainActivityLaunch(Context context) {
        try {
            List<ResolveInfo> activities = getLauncherActivities(context);
            if (!activities.isEmpty()) {
                launchMainActivity(context, activities.get(0));
            }
        } catch (Exception ex) {
            Log.e(TAG, "主界面跳转失败", ex);
        }
    }

    // 获取所有的Launcher界面
    private List<ResolveInfo> getLauncherActivities(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setPackage(context.getPackageName());
        intent.addCategory(CATEGORY_LAUNCHER);
        return context.getPackageManager().queryIntentActivities(intent, 0);
    }

    // 启动主界面
    private void launchMainActivity(Context context, ResolveInfo resolveInfo) {
        Intent main = new Intent();
        main.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(main);
    }

    // 处理通知消息
    private void handleNotificationMessage(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (bundle != null && bundle.getString("msg") != null) {
                String msg = bundle.getString("msg");
                DataManager.getInstance().saveNotificationClick(context, msg);
                FlappyImService.getInstance().checkNotificationClick();
            }
        } catch (Exception ex) {
            Log.e(TAG, "消息保存失败", ex);
        }
    }
}