package com.flappygo.flappyim.Tools;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

/**
 * 网络相关帮助类
 *
 * @author FuLei
 */
public class NetTool {


    /******
     * 判断手机的连接状态
     * @param context 上下文
     * @return 是否成功
     */
    public static boolean isConnected(Context context) {
        // 连接管理器
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取连接的网络信息
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.getType() == ConnectivityManager.TYPE_MOBILE ||
                networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }


}