package com.flappygo.flappyim.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

import com.flappygo.flappyim.ApiServer.Base.BaseParseCallback;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Config.FlappyConfig;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Handler.HandlerLoginCallback;
import com.flappygo.flappyim.Holder.HolderLoginCallback;
import com.flappygo.flappyim.Listener.KnickedOutListener;
import com.flappygo.flappyim.Listener.NotificationClickListener;
import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Thread.NettyThread;
import com.flappygo.flappyim.Thread.NettyThreadDead;
import com.flappygo.flappyim.Tools.NetTool;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.lilin.lxhttpclient.LXHttpClient;

import java.util.HashMap;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_KNICKED;


//应用的服务
public class FlappyService extends Object {

    //Channel NAME IM通知服务
    private String channelName = "IM通知服务";

    //Channel ID 必须保证唯一
    private String channelID = "com.flappygo.flappyim.channel";

    //线程
    private NettyThread clientThread;

    //当前的服务实例
    private static FlappyService instance;

    //被踢下线的监听
    private KnickedOutListener knickedOutListener;

    //监听
    private NotificationClickListener notificationClickListener;


    //上下文
    private Context mContext;

    //当前服务是否注册
    private boolean recieverRegistered = false;

    //上下文
    private FlappyService() {
    }

    //赋值上下文
    public void init(Context context) {
        this.mContext = context;
    }

    //开启服务
    public boolean startService() {
        if (instance != null) {
            //初始化接收器
            instance.initReceiver();
            //开始服务
            instance.startServer(null,
                    null,
                    null,
                    null,
                    null);
            return true;
        }
        return false;
    }

    //开启服务
    public boolean startService(ChatUser user,
                                String serverAddress,
                                String serverPort,
                                String uuid,
                                ResponseLogin response) {
        if (instance != null) {
            //初始化接收器
            instance.initReceiver();
            //开始服务
            instance.startServer(user,
                    serverAddress,
                    serverPort,
                    uuid,
                    response);
            return true;
        }
        return false;
    }

    //销毁
    public void stopService() {
        //下线
        offline();
        //释放
        synchronized (this) {
            if (recieverRegistered) {
                mContext.unregisterReceiver(netReceiver);
                recieverRegistered = false;
            }
        }
    }

    //获取当前开启的服务
    public static FlappyService getInstance() {
        if (instance == null) {
            synchronized (FlappyService.class) {
                if (instance == null) {
                    instance = new FlappyService();
                }
            }
        }
        return instance;
    }

    //获取当前服务的线程
    public NettyThread getClientThread() {
        synchronized (this) {
            return clientThread;
        }
    }

    //当前是否在线
    public boolean isOnline() {
        NettyThread thread = getClientThread();
        if (thread != null && thread.isConnected()) {
            return true;
        }
        return false;
    }


    //设置踢下线的监听
    public void setKnickedOutListener(KnickedOutListener listener) {
        //监听
        knickedOutListener = listener;
        //获取用户数据
        ChatUser user = DataManager.getInstance().getLoginUser();
        //已经被踢下线了，不要挣扎了
        if (user != null && user.isLogin() == 0) {
            //如果不为空
            if (knickedOutListener != null) {
                knickedOutListener.knickedOut();
            }
        }
    }

    //设置消息被点击的监听
    public void setNotificationClickListener(NotificationClickListener listener) {
        notificationClickListener = listener;
        notifyClicked();
    }

    //获取消息被点击的监听
    public NotificationClickListener getNotificationClickListener() {
        return notificationClickListener;
    }

    //通知被点击
    public void notifyClicked() {
        //消息
        String str = DataManager.getInstance().getNotificationClick();
        if (notificationClickListener != null && str != null) {
            ChatMessage message = GsonTool.jsonObjectToModel(str, ChatMessage.class);
            notificationClickListener.notificationClicked(message);
            DataManager.getInstance().removeNotificationClick();
        }
    }

    //开始
    private void startServer(ChatUser user,
                             String serverAddress,
                             String serverPort,
                             String uuid,
                             ResponseLogin response) {
        //判断是否是新登录
        boolean flag = testNewLogin(user,
                serverAddress,
                serverPort,
                uuid,
                response);
        //不是新登录
        if (!flag) {
            //检查是否需要自动登录并登陆
            testAutoLogin(0);
        }
    }

    //检查当前是否是新登录，如果是
    private boolean testNewLogin(ChatUser user,
                                 String serverAddress,
                                 String serverPort,
                                 String uuid,
                                 ResponseLogin response) {
        //重新发起链接
        if (user != null && serverAddress != null && serverPort != null && uuid != null && response != null) {
            startConnect(user, serverAddress, serverPort, uuid, response);
            return true;
        }
        return false;
    }

    //检查是否需要重新登录
    private void testAutoLogin(int delauMilis) {
        //如果不是新登录查看旧的是否登录了
        ChatUser user = DataManager.getInstance().getLoginUser();
        //之前已经登录了，那么我们开始断线重连
        if (user != null && user.isLogin() == 1) {
            //当前网络在线
            if (NetTool.isConnected(mContext)) {
                //移除消息
                handler.removeMessages(1);
                //等待一秒后继续连接
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
        //新增
        synchronized (this) {
            if (!recieverRegistered) {
                IntentFilter timeFilter = new IntentFilter();
                timeFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
                timeFilter.addAction("android.net.ethernet.STATE_CHANGE");
                timeFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                timeFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
                timeFilter.addAction("android.net.wifi.STATE_CHANGE");
                timeFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                mContext.registerReceiver(netReceiver, timeFilter);
                recieverRegistered = true;
            }
        }
    }

    //根据当前的信息重新连接
    private void startConnect(ChatUser user,
                              String serverIP,
                              String serverPort,
                              String uuid,
                              ResponseLogin loginResponse) {
        //之前的先下线
        offline();

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
                new NettyThreadDead() {
                    @Override
                    public void threadDead() {
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
            if (NetTool.isConnected(mContext)) {
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
        hashMap.put("device", FlappyConfig.getInstance().device);
        //设备ID
        hashMap.put("pushid", StringTool.getDeviceUnicNumber(mContext));
        //设备ID
        hashMap.put("pushplat", FlappyConfig.getInstance().pushPlat);

        //进行callBack
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().autoLogin,
                hashMap,
                new BaseParseCallback<ResponseLogin>(ResponseLogin.class) {
                    @Override
                    protected void stateFalse(BaseApiModel model, String tag) {
                        //当前的用户已经被踢下线了
                        if (model.getResultCode().equals(RESULT_KNICKED)) {

                            //设置登录状态
                            ChatUser user = DataManager.getInstance().getLoginUser();
                            //当前没有登录
                            user.setLogin(0);
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

                        //保存推送设置
                        DataManager.getInstance().savePushType(StringTool.decimalToStr(response.getRoute().getRoutePushType()));


                        //自动登录成功，我们拿到了相应的数据
                        startConnect(response.getUser(),
                                response.getServerIP(),
                                response.getServerPort(),
                                Long.toString(System.currentTimeMillis()),
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
        synchronized (this) {
            //假如之前存在连接
            if (clientThread != null) {
                //之前的账号下线
                clientThread.offline();
                //清空
                clientThread = null;
            }
        }
    }

}
