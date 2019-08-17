package com.flappygo.flappyim.Service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;

import com.flappygo.flappyim.ApiServer.Base.BaseParseCallback;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.Callback.FlappyDeadCallback;
import com.flappygo.flappyim.Config.BaseConfig;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Handler.HandlerLoginCallback;
import com.flappygo.flappyim.Holder.HolderLoginCallback;
import com.flappygo.flappyim.Listener.KnickedOutListener;
import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Thread.NettyThread;
import com.flappygo.flappyim.Tools.NetTool;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.lilin.lxhttpclient.LXHttpClient;

import java.util.HashMap;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_KNICKED;


//应用的服务
public class FlappyService extends Service {

    //当前的服务实例
    private static FlappyService instance;

    //被踢下线的监听
    private static KnickedOutListener knickedOutListener;

    //返回
    public static FlappyService getInstance() {
        return instance;
    }

    //获取当前服务的线程
    public NettyThread getClientThread() {
        return clientThread;
    }

    //线程
    private NettyThread clientThread;


    //设置踢下线的监听
    public static void setKnickedOutListener(KnickedOutListener listener) {
        //监听
        knickedOutListener = listener;
        //获取用户数据
        ChatUser user = DataManager.getInstance().getLoginUser();
        //已经被踢下线了，不要挣扎了
        if (user != null && user.isLogin() == false) {
            //如果不为空
            if(knickedOutListener!=null) {
                knickedOutListener.knickedOut();
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //当前
        instance = this;
        //开始
        startForeground(1, new Notification());
        //接收
        initReceiver();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //判断是否是新登录
        boolean flag = testNewLogin(intent);
        //不是新登录
        if (!flag) {
            //检查是否需要自动登录并登陆
            testAutoLogin(0);
        }
        //默认
        return super.onStartCommand(intent, flags, startId);
    }


    //检查当前是否是新登录，如果是
    private boolean testNewLogin(Intent intent) {
        if (intent != null) {
            //获取传递过来的服务器地址
            String serverAddress = intent.getStringExtra("serverAddress");
            //获取传递过来的端口号码
            String serverPort = intent.getStringExtra("serverPort");
            //获取数据
            ResponseLogin response = (ResponseLogin) intent.getSerializableExtra("data");
            //用户
            ChatUser user = (ChatUser) intent.getSerializableExtra("user");
            //获取回调的uuid
            long uuid = intent.getLongExtra("uuid", 0);
            //重新发起链接
            if (serverAddress != null && serverPort != null && response != null) {
                startConnect(user, serverAddress, serverPort, uuid, response);
                return true;
            }
        }
        return false;
    }

    //检查是否需要重新登录
    private void testAutoLogin(int delauMilis) {
        //如果不是新登录查看旧的是否登录了
        ChatUser user = DataManager.getInstance().getLoginUser();
        //之前已经登录了，那么我们开始断线重连
        if (user != null && user.isLogin()) {
            if (NetTool.isConnected(getApplicationContext())) {
                handler.removeMessages(1);
                handler.sendEmptyMessageDelayed(1, delauMilis);
            }
        }
    }


    //判断当前是否连接
    BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                //检查是否需要重新登录
                testAutoLogin(100);
            }
        }
    };

    /**
     * 注册网络监听的广播
     */
    private void initReceiver() {
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
        timeFilter.addAction("android.net.ethernet.STATE_CHANGE");
        timeFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        timeFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        timeFilter.addAction("android.net.wifi.STATE_CHANGE");
        timeFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netReceiver, timeFilter);
    }


    @Override
    public void onDestroy() {
        //注销广播的监听
        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
            netReceiver = null;
        }
        //假如之前存在连接
        if (clientThread != null) {
            //之前的账号下线
            clientThread.offline();
            //清空
            clientThread = null;
        }
        super.onDestroy();
    }

    //根据当前的信息重新连接
    private synchronized void startConnect(ChatUser user, String serverIP, String serverPort, long uuid, ResponseLogin loginResponse) {

        //假如之前存在连接
        if (clientThread != null) {
            //之前的账号下线
            clientThread.offline();
        }

        //创建新的线程
        clientThread = new NettyThread(
                user,
                //服务端的IP
                serverIP,
                //端口
                Integer.parseInt(serverPort),
                //装饰下吧，难得
                new HandlerLoginCallback(HolderLoginCallback.getInstance().getLoginCallBack(uuid), loginResponse),
                //回调
                new FlappyDeadCallback() {
                    @Override
                    public void dead() {
                        //检查是否需要重新登录
                        testAutoLogin(100);
                    }
                });
        //开始这个线程
        clientThread.start();
    }

    //用于检测
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //如果当前的网络是连接上了的
            if (NetTool.isConnected(getApplicationContext())) {
                //自动登录
                autoLogin();
            }
        }
    };


    //重新自动登录
    private void autoLogin() {
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID
        hashMap.put("userID", DataManager.getInstance().getLoginUser().getUserId());
        //设备ID
        hashMap.put("device", BaseConfig.device);
        //设备ID
        hashMap.put("pushid", StringTool.getDeviceUnicNumber(getApplicationContext()));

        //进行callBack
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().autoLogin,
                hashMap,
                new BaseParseCallback<ResponseLogin>(ResponseLogin.class) {
                    @Override
                    protected void stateFalse(BaseApiModel model, String tag) {
                        //当前的用户已经被踢下线了
                        if (model.getResultCode().equals(RESULT_KNICKED)) {

                            //设置登录状态
                            ChatUser user = DataManager.getInstance().getLoginUser();
                            //当前没有登录
                            user.setLogin(false);
                            //清空用户数据
                            DataManager.getInstance().saveLoginUser(user);

                            //当前已经被踢下线了
                            if (knickedOutListener != null) {
                                //只执行一次
                                knickedOutListener.knickedOut();
                            }
                        } else {
                            //重新登录
                            testAutoLogin(5 * 1000);
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        testAutoLogin(5 * 1000);
                    }

                    @Override
                    public void stateTrue(ResponseLogin response, String tag) {
                        //自动登录成功，我们拿到了相应的数据
                        startConnect(response.getUser(),
                                response.getServerIP(),
                                response.getServerPort(),
                                0,
                                response);
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        testAutoLogin(5 * 1000);
                    }
                }, null);
    }

    //下线了
    public void offline() {
        //假如之前存在连接
        if (clientThread != null) {
            //之前的账号下线
            clientThread.offline();
            //清空
            clientThread = null;
        }
    }

}
