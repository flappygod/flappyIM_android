package com.flappygo.flappyim.Tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络相关帮助类
 *
 * @author FuLei
 */
public class NetTool {

    /**
     * 没有网络
     */
    public static final int UNCONNECTED = -1;
    /**
     * WIFI网络
     */
    public static final int TYPE_WIFI = 0;
    /**
     * 2G网络
     */
    public static final int TYPE_2G = 1;
    /**
     * 3G网络
     */
    public static final int TYPE_3G = 2;
    /**
     * 4G网络
     */
    public static final int TYPE_4G = 3;
    /**
     * 网线连接
     */
    public static final int TYPE_ETHERNET = 4;
    /**
     * 未知网络类型
     */
    public static final int TYPE_UNKNOW = 5;

    //是否有网络的回调
    public interface NetWorkCallBack {
        //有网络
        void netWorkOn();

        //没有网络
        void netWorkOff();
    }


    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static void isNetConnected(Context context, final NetWorkCallBack callBack) {
        if (context != null) {
            // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                callBack.netWorkOff();
            } else {
                //handle
                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 0) {
                            callBack.netWorkOff();
                        } else if (msg.what == 1) {
                            callBack.netWorkOn();
                        }
                    }
                };
                // 获取NetworkInfo对象
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()
                        && networkInfo.isConnected()) {
                    new Thread() {
                        public void run() {
                            try {
                                Process p = Runtime.getRuntime().exec(
                                        "ping -c 1 -w 3  119.75.217.109");
                                int status = p.waitFor();
                                if (status == 0) {
                                    Message msg = handler.obtainMessage(1);
                                    handler.sendMessage(msg);
                                } else {
                                    Message msg = handler.obtainMessage(0);
                                    handler.sendMessage(msg);
                                }
                            } catch (Exception e) {
                                Message msg = handler.obtainMessage(0);
                                handler.sendMessage(msg);
                            }
                        }

                        ;

                    }.start();
                } else {
                    callBack.netWorkOff();
                }
            }
        }
    }

    /**
     * 判断手机的连接状态
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        // 连接管理器
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取连接的网络信息
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {// 2g_3g_4g连上
            return true;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {// wifi连上
            return true;
        } else {
            return false;
        }
    }

    public static boolean isWifiConnected(Context context) {

        // 连接管理器
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取连接的网络信息
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        // wifi连上
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */

    public static int getNetType(Context context) {
        int netType = UNCONNECTED;
        // 获取网络管理Manager对象
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取手机管理Manager对象
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        // 获取活动的网络状态信息
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo == null) {// 没有网络
            netType = UNCONNECTED;
        } else {
            // 网络类型代码
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                // 手机网络
                switch (tm.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:// ~50-100 Kbps 2G网络
                        netType = TYPE_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_CDMA:// ~14-64 Kbps 2G网络
                        netType = TYPE_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_EDGE:// ~50-100 Kbps 2G网络
                        netType = TYPE_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS:// ~100 Kbps 2G网络
                        netType = TYPE_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:// ~ 400-1000 Kbps
                        // 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:// ~ 600-1400 Kbps
                        // 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:// ~ 2-14 Mbps 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPA:// ~ 700-1700 kbps 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 Mbps 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // ~ 400-7000 Kbps 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:// 未知网络
                        netType = TYPE_UNKNOW;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netType = TYPE_4G;
                        break;
                    default:
                        netType = TYPE_2G;
                }
            } else if (ConnectivityManager.TYPE_ETHERNET == type) {
                netType = TYPE_ETHERNET;
            }
        }
        return netType;
    }


    /**
     * 显示网络提示，如果没有网络连接则进入网络设置界面
     *
     * @param context
     */
    public static void showTips(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("无网络");
        builder.setMessage("您的手机当前无网络，是否进行设置？");
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 如果没有网络连接，则进入网络设置界面
                // context.startActivity(new
                // Intent(Settings.ACTION_WIRELESS_SETTINGS));
                context.startActivity(new Intent(
                        android.provider.Settings.ACTION_SETTINGS));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }



    public static String getLocalIpAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            }
            //当前使用无线网络
            else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //得到IPV4地址
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}