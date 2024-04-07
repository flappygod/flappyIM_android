package com.flappygo.flappyim.Tools;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/******
 * 运行工具类
 */
public class RunningTool {

    //判断是否在后台
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i("前台", appProcess.processName);
                    return false;
                } else {
                    Log.i("后台", appProcess.processName);
                    return true;
                }
            }
        }
        return false;
    }
}
