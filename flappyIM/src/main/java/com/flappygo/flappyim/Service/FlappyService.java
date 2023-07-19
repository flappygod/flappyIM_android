package com.flappygo.flappyim.Service;


import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_EXPIRED;

import com.flappygo.flappyim.Listener.NotificationClickListener;
import com.flappygo.flappyim.ApiServer.Base.BaseParseCallback;
import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.Handler.HandlerLoginCallback;
import com.flappygo.flappyim.Listener.KnickedOutListener;
import com.flappygo.flappyim.Holder.HolderLoginCallback;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Thread.NettyThreadDead;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.lilin.lxhttpclient.LXHttpClient;
import com.flappygo.flappyim.Config.FlappyConfig;
import com.flappygo.flappyim.Thread.NettyThread;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.Tools.NetTool;

import android.content.BroadcastReceiver;
import android.net.ConnectivityManager;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;


//应用的服务
public class FlappyService {

    //当前的服务实例
    private static final class InstanceHolder {
        static final FlappyService instance = new FlappyService();
    }

    //获取当前开启的服务
    public static FlappyService getInstance() {
        return InstanceHolder.instance;
    }

    //上下文
    private Context mContext;

    //自动HTTP登录
    private static final int AUTO_LOGIN_HTTP = 1;

    //自动登录Netty
    private static final int AUTO_LOGIN_NETTY = 2;

    //线程
    private NettyThread clientThread;

    //被踢下线的监听
    private KnickedOutListener knickedOutListener;

    //监听
    private NotificationClickListener notificationClickListener;

    //当前正在登录
    public boolean isLoading = false;

    //当前服务是否注册
    private boolean receiverRegistered = false;

    //赋值上下文
    public void init(Context context) {
        mContext = context;
    }

    /******
     * 判断当前是否连接
     */
    BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                checkAutoLoginHttp(100);
            }
        }
    };

    /******
     * 注册网络监听的广播
     */
    private void initReceiver() {
        synchronized (this) {
            if (!receiverRegistered) {
                IntentFilter timeFilter = new IntentFilter();
                timeFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
                timeFilter.addAction("android.net.ethernet.STATE_CHANGE");
                timeFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                timeFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
                timeFilter.addAction("android.net.wifi.STATE_CHANGE");
                timeFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                mContext.registerReceiver(netReceiver, timeFilter);
                receiverRegistered = true;
            }
        }
    }

    /******
     * 移除接收器
     */
    private void removeReceiver() {
        synchronized (this) {
            if (receiverRegistered) {
                mContext.unregisterReceiver(netReceiver);
                receiverRegistered = false;
            }
        }
    }

    /******
     * 开启服务,如果用户已经登录的情况下自动登录
     */
    public void startService() {
        //初始化接收器
        InstanceHolder.instance.initReceiver();
        //开始服务
        InstanceHolder.instance.startServer(null, null);
    }


    /******
     * 开启服务,并完成用户登录
     */
    public void startService(String uuid, ResponseLogin response) {
        //初始化接收器
        InstanceHolder.instance.initReceiver();
        //开始服务
        InstanceHolder.instance.startServer(uuid, response);
    }

    /******
     * 关闭服务并下线
     */
    public void stopService() {
        //移除网络监听器
        InstanceHolder.instance.removeReceiver();
        //下线
        InstanceHolder.instance.offline();
    }


    /******
     * 获取当前服务的线程
     * @return 服务线程
     */
    public NettyThread getClientThread() {
        synchronized (this) {
            return clientThread;
        }
    }

    /******
     * 获取当前是否是在线状态
     * @return 服务线程
     */
    public boolean isOnline() {
        NettyThread thread = getClientThread();
        return thread != null && thread.isConnected();
    }


    //设置踢下线的监听
    public void setKickedOutListener(KnickedOutListener listener) {
        //监听
        knickedOutListener = listener;
        //获取用户数据
        ChatUser user = DataManager.getInstance().getLoginUser();
        //已经被踢下线了，不要挣扎了
        if (user != null && user.isLogin() == 0) {
            //如果不为空
            if (knickedOutListener != null) {
                knickedOutListener.kickedOut();
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
        String str = DataManager.getInstance().getNotificationClick();
        if (notificationClickListener != null && str != null) {
            ChatMessage message = GsonTool.jsonObjectToModel(str, ChatMessage.class);
            notificationClickListener.notificationClicked(message);
            DataManager.getInstance().removeNotificationClick();
        }
    }

    //开始
    private void startServer(String uuid, ResponseLogin response) {
        //判断数据完全的时候，才执行
        if (uuid != null &&
                response != null &&
                response.getUser() != null &&
                response.getServerIP() != null &&
                response.getServerPort() != null) {
            //开启connect
            startConnect(uuid, response);
        } else {
            //发现不存在就自动登录
            checkAutoLoginHttp(100);
        }
    }

    //检查是否需要重新登录
    private void checkAutoLoginHttp(int delayMillis) {
        //如果不是新登录查看旧的是否登录了
        ChatUser user = DataManager.getInstance().getLoginUser();
        //之前已经登录了，那么我们开始断线重连
        if (user != null && user.isLogin() == 1) {
            //当前网络在线
            if (NetTool.isConnected(mContext)) {
                //移除消息
                handler.removeMessages(AUTO_LOGIN_HTTP);
                //等待一秒后继续连接
                handler.sendEmptyMessageDelayed(AUTO_LOGIN_HTTP, delayMillis);
            }
        }
    }

    //检查重新登录netty
    private void checkAutoLoginNetty(int delayMillis, ResponseLogin responseLogin) {
        //如果不是新登录查看旧的是否登录了
        ChatUser user = DataManager.getInstance().getLoginUser();
        //之前已经登录了，那么我们开始断线重连
        if (user != null && user.isLogin() == 1) {
            //当前网络在线
            if (NetTool.isConnected(mContext)) {
                //移除消息
                handler.removeMessages(AUTO_LOGIN_NETTY);
                //检查并重新登录
                Message message = handler.obtainMessage(AUTO_LOGIN_NETTY, responseLogin);
                //等待一秒后继续连接
                handler.sendMessageDelayed(message, delayMillis);
            }
        }
    }

    //用于检测
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            //如果当前的网络是连接上了的
            if (NetTool.isConnected(mContext)) {
                //http重连
                if (msg.what == AUTO_LOGIN_HTTP) {
                    if (!FlappyService.getInstance().isLoading) {
                        autoLogin();
                    }
                }
                //netty重连
                else if (msg.what == AUTO_LOGIN_NETTY) {
                    if (!FlappyService.getInstance().isLoading) {
                        ResponseLogin responseLogin = (ResponseLogin) msg.obj;
                        startConnect(Long.toString(System.currentTimeMillis()), responseLogin);
                    }
                }
            }
        }
    };

    //重新自动登录
    private void autoLogin() {
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
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
                    //出现异常
                    @Override
                    protected void stateFalse(BaseApiModel<ResponseLogin> model, String tag) {
                        //当前的用户已经被踢下线了
                        if (model.getCode().equals(RESULT_EXPIRED)) {
                            //设置登录状态
                            ChatUser user = DataManager.getInstance().getLoginUser();
                            //当前没有登录
                            user.setLogin(0);
                            //清空用户数据
                            DataManager.getInstance().saveLoginUser(user);
                            //当前已经被踢下线了
                            if (knickedOutListener != null) {
                                knickedOutListener.kickedOut();
                            }
                        } else {
                            //重新登录
                            checkAutoLoginHttp(FlappyConfig.getInstance().autoLoginSpace);
                        }
                    }

                    //自动登录
                    @Override
                    protected void jsonError(Exception e, String tag) {
                        checkAutoLoginHttp(FlappyConfig.getInstance().autoLoginSpace);
                    }

                    //测试并自动登录
                    @Override
                    protected void netError(Exception e, String tag) {
                        checkAutoLoginHttp(FlappyConfig.getInstance().autoLoginSpace);
                    }

                    //重置
                    @Override
                    public void stateTrue(ResponseLogin response, String tag) {
                        NettyThreadDead.reset();
                        //保存推送设置
                        DataManager.getInstance().savePushType(StringTool.decimalToStr(response.getRoute().getRoutePushType()));
                        //自动登录成功，我们拿到了相应的数据
                        if (!FlappyService.getInstance().isLoading) {
                            startConnect(Long.toString(System.currentTimeMillis()), response);
                        }
                    }
                }, null);
    }


    //根据当前的信息重新连接
    private void startConnect(String uuid, final ResponseLogin loginResponse) {

        synchronized (this) {

            //之前的先下线
            if (clientThread != null) {
                clientThread.offline();
                clientThread = null;
            }

            //装饰登录
            HandlerLoginCallback loginCallback = new HandlerLoginCallback(
                    HolderLoginCallback.getInstance().getLoginCallBack(uuid),
                    loginResponse
            );

            //死亡消息
            NettyThreadDead nettyThreadDead = new NettyThreadDead() {
                @Override
                public void threadDeadRetryHttp() {
                    //断线重连，使用http的方式，也许服务器的ip已经发生了变化
                    checkAutoLoginHttp(100);
                }

                @Override
                protected void threadDeadRetryNetty() {
                    //断线重连，先试用netty的方式，防止http请求被过多的调用造成问题
                    checkAutoLoginNetty(
                            FlappyConfig.getInstance().autoLoginSpace,
                            loginResponse
                    );
                }
            };


            //创建新的线程
            clientThread = new NettyThread(
                    //获取用户
                    loginResponse.getUser(),
                    //服务端的IP
                    loginResponse.getServerIP(),
                    //端口
                    StringTool.strToInt(loginResponse.getServerPort(), 11211),
                    //登录回调
                    loginCallback,
                    //回调
                    nettyThreadDead
            );

            //开始这个线程
            clientThread.start();
        }
    }


    //下线了
    public void offline() {
        synchronized (this) {
            if (clientThread != null) {
                clientThread.offline();
                clientThread = null;
            }
        }
    }

}
