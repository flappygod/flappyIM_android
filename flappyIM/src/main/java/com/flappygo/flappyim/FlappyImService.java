package com.flappygo.flappyim;

import com.flappygo.flappyim.ApiServer.Base.BaseListParseCallBack;
import com.flappygo.flappyim.Listener.NotificationClickListener;
import com.flappygo.flappyim.ApiServer.Base.BaseParseCallback;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.Service.FlappySocketService;
import com.flappygo.flappyim.Listener.KickedOutListener;
import com.flappygo.flappyim.Holder.HolderLoginCallback;
import com.flappygo.flappyim.Handler.ChannelMsgHandler;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.Session.FlappyChatSession;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Listener.SessionListener;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Thread.NettyThreadDead;
import com.flappygo.flappyim.Tools.NotificationUtil;
import com.flappygo.lilin.lxhttpclient.LXHttpClient;
import com.flappygo.flappyim.Config.FlappyConfig;
import com.flappygo.flappyim.Thread.NettyThread;
import com.flappygo.flappyim.Datas.FlappyIMCode;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Tools.RunninTool;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.Tools.NetTool;

import android.content.BroadcastReceiver;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.content.IntentFilter;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import android.os.Message;
import android.os.Handler;
import android.os.Looper;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_LOCATE;
import static com.flappygo.flappyim.Models.Server.ChatRoute.PUSH_TYPE_NORMAL;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_VOICE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_VIDEO;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_TEXT;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_IMG;
import static com.flappygo.flappyim.Models.Server.ChatRoute.PUSH_TYPE_HIDE;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_JSON_ERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NET_ERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NOT_LOGIN;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_FAILURE;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_EXPIRED;


//服务
public class FlappyImService {

    //单例模式
    private static final class InstanceHolder {
        static final FlappyImService instance = new FlappyImService();
    }

    //单例Manager
    public static FlappyImService getInstance() {
        return InstanceHolder.instance;
    }

    //自动HTTP登录
    private static final int AUTO_LOGIN_HTTP = 1;

    //自动登录Netty
    private static final int AUTO_LOGIN_NETTY = 2;

    //上下文
    private Context appContext;

    //是否显示Notification
    private boolean showNotification;

    //当前服务是否注册
    private volatile boolean receiverRegistered = false;

    //当前是否正在登录
    private volatile boolean isRunningLogin = false;

    //当前是否正在自动登录
    private volatile boolean isRunningAutoLogin = false;

    //被踢下线的监听
    private KickedOutListener kickedOutListener;

    //通知被点击的监听
    private NotificationClickListener notificationClickListener;


    /****************
     * 设置踢下线的监听
     ****************/
    public void setKickedOutListener(KickedOutListener listener) {
        //监听
        kickedOutListener = listener;
        //获取用户数据
        ChatUser user = DataManager.getInstance().getLoginUser();
        //已经被踢下线了，不要挣扎了
        if (user != null && user.isLogin() == 0) {
            //如果不为空
            if (kickedOutListener != null) {
                kickedOutListener.kickedOut();
            }
        }
    }


    /****************
     * 消息被点击监听
     ****************/
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


    /****************
     * 自动Http重新登录和自动Netty重新登录
     ****************/
    //检查是否需要重新登录
    public void checkAutoLoginHttp(int delayMillis) {
        //如果不是新登录查看旧的是否登录了
        ChatUser user = DataManager.getInstance().getLoginUser();
        //之前已经登录了，那么我们开始断线重连
        if (user != null && user.isLogin() == 1 && NetTool.isConnected(getAppContext())) {
            //移除消息
            handler.removeMessages(AUTO_LOGIN_HTTP);
            //等待一点时间后继续
            if (delayMillis == 0) {
                handler.sendEmptyMessage(AUTO_LOGIN_HTTP);
            } else {
                handler.sendEmptyMessageDelayed(AUTO_LOGIN_HTTP, delayMillis);
            }
        }
    }

    //检查重新登录netty
    public void checkAutoLoginNetty(int delayMillis, ResponseLogin responseLogin) {
        //如果不是新登录查看旧的是否登录了
        ChatUser user = DataManager.getInstance().getLoginUser();
        //已经登录且网络正常，那么我们开始断线重连
        if (user != null && user.isLogin() == 1 && NetTool.isConnected(getAppContext())) {
            //移除消息
            handler.removeMessages(AUTO_LOGIN_NETTY);
            Message message = handler.obtainMessage(AUTO_LOGIN_NETTY, responseLogin);
            //发送
            if (delayMillis == 0) {
                handler.sendMessage(message);
            } else {
                handler.sendMessageDelayed(message, delayMillis);
            }
        }
    }


    //用于检测
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            //http重连
            if (msg.what == AUTO_LOGIN_HTTP) {
                autoLogin();
            }
            //netty重连
            else if (msg.what == AUTO_LOGIN_NETTY) {
                autoLoginNetty((ResponseLogin) msg.obj);
            }
        }
    };


    /****************
     * 网络连接状态监听
     ****************/
    //判断当前是否连接
    BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                checkAutoLoginHttp(0);
            }
        }
    };


    //注册网络监听的广播
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
                getAppContext().registerReceiver(netReceiver, timeFilter);
                receiverRegistered = true;
            }
        }
    }

    //移除接收器
    private void removeReceiver() {
        synchronized (this) {
            if (receiverRegistered) {
                getAppContext().unregisterReceiver(netReceiver);
                receiverRegistered = false;
            }
        }
    }


    /****************
     * 本地通知弹窗
     ****************/
    private final MessageListener messageListener = new MessageListener() {
        @Override
        public void messageSend(ChatMessage chatMessage) {

        }

        @Override
        public void messageFailed(ChatMessage chatMessage) {

        }

        @Override
        public void messageUpdate(ChatMessage chatMessage) {

        }

        @Override
        public void messageReceived(ChatMessage chatMessage) {
            sendNotification(chatMessage);
        }
    };


    //设置notification
    public void setNotification(boolean flag) {
        this.showNotification = flag;
    }

    //发送本地通知
    private void sendNotification(ChatMessage chatMessage) {
        //不显示notification
        if (!showNotification) {
            return;
        }
        //正在后台
        if (RunninTool.isBackground(FlappyImService.this.getAppContext())) {
            //上下文
            NotificationUtil util = new NotificationUtil(FlappyImService.this.getAppContext());
            //普通
            if (StringTool.strToDecimal(DataManager.getInstance().getPushType()).intValue() == PUSH_TYPE_NORMAL) {
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_TEXT) {
                    util.sendNotification(chatMessage, "消息提醒", chatMessage.getChatText());
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_IMG) {
                    util.sendNotification(chatMessage, "消息提醒", "您有一条图片消息");
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_VOICE) {
                    util.sendNotification(chatMessage, "消息提醒", "您有一条语音消息");
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_LOCATE) {
                    util.sendNotification(chatMessage, "消息提醒", "您有一条位置消息");
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_VIDEO) {
                    util.sendNotification(chatMessage, "消息提醒", "您有一条视频消息");
                }
            } else if (StringTool.strToDecimal(DataManager.getInstance().getPushType()).intValue() == PUSH_TYPE_HIDE) {
                util.sendNotification(chatMessage, "消息提醒", "您有一条新的消息");
            }
        }
    }


    //获取上下文
    public Context getAppContext() {
        if (appContext == null) {
            throw new RuntimeException("flappy im not init,call init first");
        }
        return appContext;
    }


    /******
     * 初始化
     * @param appContext 上下文
     */
    public void init(Context appContext) {
        //初始化上下文
        this.appContext = appContext.getApplicationContext();
        //添加总体的监听
        HolderMessageSession.getInstance().addGlobalMessageListener(messageListener);
        //清空当前正在发送的消息
        Database database = new Database();
        database.clearSendingMessage();
        database.close();
    }

    /******
     * 初始化
     * @param appContext 上下文
     * @param serverPath Im服务器地址
     * @param uploadPath 资源服务器地址
     */
    public void init(Context appContext, String serverPath, String uploadPath) {
        //获取application
        this.appContext = appContext.getApplicationContext();
        //更新服务器地址和资源文件上传地址
        FlappyConfig.getInstance().setServerUrl(serverPath, uploadPath);
        //添加总体的监听,定义全局防止多次重复添加这个监听
        HolderMessageSession.getInstance().addGlobalMessageListener(messageListener);
        //清空当前正在发送的消息
        Database database = new Database();
        database.clearSendingMessage();
        database.close();
    }

    /*******
     * 推送的平台
     * @param flatForm 推送平台
     */
    public void setPushPlatform(String flatForm) {
        FlappyConfig.getInstance().pushPlat = flatForm;
    }


    /*******
     * 正式开启服务
     */
    public void startServer() {
        initReceiver();
        checkAutoLoginHttp(0);
    }

    /*******
     * 停止服务
     */
    public void stopServer() {
        removeReceiver();
        FlappySocketService.getInstance().offline();
    }


    /**************
     * 创建用户账户
     * @param userID      用户ID
     * @param userName    用户姓名
     * @param userData    用户数据
     * @param userAvatar  用户头像
     * @param callback    回调
     */
    public void createAccount(String userID,
                              String userName,
                              String userAvatar,
                              String userData,
                              final FlappyIMCallback<String> callback) {

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        //设置index
        hashMap.put("userExtendID", userID);
        //用户名称
        hashMap.put("userName", userName);
        //用户头像
        hashMap.put("userAvatar", userAvatar);
        //用户名称
        hashMap.put("userData", userData);
        //进行callBack
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().register,
                hashMap,
                new BaseParseCallback<String>(String.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<String> model, String tag) {
                        if (callback != null) {
                            callback.failure(
                                    new Exception(model.getMsg()),
                                    Integer.parseInt(model.getCode())
                            );
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(RESULT_JSON_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(String s, String tag) {
                        if (callback != null) {
                            callback.success(s);
                        }
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(RESULT_NET_ERROR));
                        }
                    }
                }
        );
    }

    /**************
     * 更新用户账户
     * @param userID      用户ID
     * @param userName    用户姓名
     * @param userData    用户数据
     * @param userAvatar  用户头像
     * @param callback    回调
     */
    public void updateAccount(String userID,
                              String userName,
                              String userAvatar,
                              String userData,
                              final FlappyIMCallback<String> callback) {
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        //设置index
        hashMap.put("userExtendID", userID);
        //用户名称
        hashMap.put("userName", userName);
        //用户头像
        hashMap.put("userAvatar", userAvatar);
        //用户名称
        hashMap.put("userData", userData);
        //进行callBack
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().updateUser,
                hashMap,
                new BaseParseCallback<String>(String.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<String> model, String tag) {
                        if (callback != null) {
                            callback.failure(
                                    new Exception(model.getMsg()),
                                    Integer.parseInt(model.getCode())
                            );
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(RESULT_JSON_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(String s, String tag) {
                        if (callback != null) {
                            callback.success(s);
                        }
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(RESULT_NET_ERROR));
                        }
                    }
                }
        );
    }

    //这里就代表登录了
    public void login(String userExtendID, final FlappyIMCallback<ResponseLogin> callback) {
        synchronized (this) {
            //可以登录
            if (!checkLoginEnable(callback)) {
                return;
            }
            isRunningLogin = true;
            //创建这个HashMap
            HashMap<String, Object> hashMap = new HashMap<>();
            //用户ID不用传了
            hashMap.put("userID", "");
            //外部用户ID
            hashMap.put("userExtendID", StringTool.ToNotNullStr(userExtendID));
            //设备ID
            hashMap.put("device", FlappyConfig.getInstance().device);
            //设备ID
            hashMap.put("pushid", StringTool.getDeviceUnicNumber(getAppContext()));
            //设备ID
            hashMap.put("pushplat", FlappyConfig.getInstance().pushPlat);
            //进行callBack
            LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().login,
                    hashMap,
                    new BaseParseCallback<ResponseLogin>(ResponseLogin.class) {
                        @Override
                        protected void stateFalse(BaseApiModel<ResponseLogin> model, String tag) {
                            isRunningLogin = false;
                            if (callback != null) {
                                callback.failure(
                                        new Exception(model.getMsg()),
                                        Integer.parseInt(model.getCode())
                                );
                            }
                        }

                        @Override
                        protected void jsonError(Exception e, String tag) {
                            isRunningLogin = false;
                            if (callback != null) {
                                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                            }
                        }

                        @Override
                        protected void netError(Exception e, String tag) {
                            isRunningLogin = false;
                            if (callback != null) {
                                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                            }
                        }

                        @Override
                        public void stateTrue(ResponseLogin response, String tag) {
                            //netty登录(Socket)
                            loginNetty(response, callback);
                        }
                    }
            );
        }
    }


    //自动登录netty
    private void loginNetty(ResponseLogin response, FlappyIMCallback<ResponseLogin> callback) {
        synchronized (this) {
            //保存设置
            DataManager.getInstance().savePushType(StringTool.decimalToStr(response.getRoute().getRoutePushType()));
            //重置次数
            NettyThreadDead.reset();
            //转换
            String loginReqUDID = Long.toString(System.currentTimeMillis());
            //添加登录回调
            HolderLoginCallback.getInstance().addLoginCallBack(loginReqUDID, new FlappyIMCallback<ResponseLogin>() {
                @Override
                public void success(ResponseLogin data) {
                    isRunningLogin = false;
                    if (callback != null) {
                        callback.success(data);
                    }
                }

                @Override
                public void failure(Exception ex, int code) {
                    isRunningLogin = false;
                    if (callback != null) {
                        callback.failure(ex, code);
                    }
                }
            });
            //开启服务
            FlappySocketService.getInstance().startConnect(
                    loginReqUDID,
                    response,
                    new NettyThreadDead() {
                        @Override
                        //断线重连，使用http的方式，也许服务器的ip已经发生了变化
                        public void threadDeadRetryHttp() {
                            checkAutoLoginHttp(FlappyConfig.getInstance().autoLoginSpace);
                        }

                        @Override
                        //断线重连，先试用netty的方式，防止http请求被过多的调用造成问题
                        protected void threadDeadRetryNetty() {
                            checkAutoLoginNetty(FlappyConfig.getInstance().autoLoginSpace, response);
                        }
                    }
            );
        }
    }


    //重新自动登录
    private void autoLogin() {
        synchronized (this) {

            //再次检查用户
            ChatUser user = DataManager.getInstance().getLoginUser();
            if (user == null || user.isLogin() == 0) {
                return;
            }

            //自动登录必须是没有在登录状态的情况下
            if (isRunningLogin) {
                return;
            }

            //正在自动登录无需多次自动登录
            if (isRunningAutoLogin) {
                return;
            }

            //当前已经是登录状态了，无需处理
            if (isOnline()) {
                return;
            }

            //如果当前的网络是连接上了的
            if (!NetTool.isConnected(getAppContext())) {
                return;
            }

            //正在自动登录模式
            isRunningAutoLogin = true;
            //创建这个HashMap
            HashMap<String, Object> hashMap = new HashMap<>();
            //用户ID
            hashMap.put("userID", user.getUserId());
            //设备ID
            hashMap.put("device", FlappyConfig.getInstance().device);
            //设备ID
            hashMap.put("pushid", StringTool.getDeviceUnicNumber(getAppContext()));
            //设备ID
            hashMap.put("pushplat", FlappyConfig.getInstance().pushPlat);
            //进行callBack
            LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().autoLogin,
                    hashMap,
                    new BaseParseCallback<ResponseLogin>(ResponseLogin.class) {
                        //出现异常
                        @Override
                        protected void stateFalse(BaseApiModel<ResponseLogin> model, String tag) {
                            //当前不在自动登录状态了
                            isRunningAutoLogin = false;
                            //当前的用户已经被踢下线了
                            if (model.getCode().equals(RESULT_EXPIRED)) {
                                //设置登录状态
                                ChatUser user = DataManager.getInstance().getLoginUser();
                                //当前没有登录
                                user.setLogin(0);
                                //清空用户数据
                                DataManager.getInstance().saveLoginUser(user);
                                //当前已经被踢下线了
                                if (kickedOutListener != null) {
                                    kickedOutListener.kickedOut();
                                }
                            } else {
                                //重新登录
                                checkAutoLoginHttp(FlappyConfig.getInstance().autoLoginSpace);
                            }
                        }

                        //自动登录
                        @Override
                        protected void jsonError(Exception e, String tag) {
                            //当前不在自动登录状态了
                            isRunningAutoLogin = false;
                            //当前不在自动登录状态了
                            checkAutoLoginHttp(FlappyConfig.getInstance().autoLoginSpace);
                        }

                        //测试并自动登录
                        @Override
                        protected void netError(Exception e, String tag) {
                            //当前不在自动登录状态了
                            isRunningAutoLogin = false;
                            checkAutoLoginHttp(FlappyConfig.getInstance().autoLoginSpace);
                        }

                        //重置
                        @Override
                        public void stateTrue(ResponseLogin response, String tag) {
                            //当前不在自动登录状态了
                            isRunningAutoLogin = false;
                            autoLoginNetty(response);
                        }
                    }
            );
        }
    }

    //自动登录netty
    private void autoLoginNetty(ResponseLogin responseInfo) {
        synchronized (this) {
            //再次检查用户
            ChatUser user = DataManager.getInstance().getLoginUser();
            if (user == null || user.isLogin() == 0) {
                return;
            }

            //用户不一致，那么也不行
            if (!responseInfo.getUser().getUserId().equals(user.getUserId())) {
                return;
            }

            //自动登录必须是没有在登录状态的情况下
            if (isRunningLogin) {
                return;
            }

            //正在自动登录无需多次自动登录
            if (isRunningAutoLogin) {
                return;
            }

            //当前已经是登录状态了，无需处理
            if (isOnline()) {
                return;
            }

            //如果当前的网络是连接上了的
            if (!NetTool.isConnected(getAppContext())) {
                return;
            }

            //正在自动登录
            isRunningAutoLogin = true;

            //重置死亡状态
            NettyThreadDead.reset();
            //保存推送设置
            DataManager.getInstance().savePushType(StringTool.decimalToStr(responseInfo.getRoute().getRoutePushType()));

            //保存数据
            ChatUser chatUser = DataManager.getInstance().getLoginUser();
            chatUser.setLogin(1);
            chatUser.setUserAvatar(responseInfo.getUser().getUserAvatar());
            chatUser.setUserName(responseInfo.getUser().getUserName());
            chatUser.setUserData(responseInfo.getUser().getUserData());
            DataManager.getInstance().saveLoginUser(chatUser);
            //转换
            String loginReqUDID = Long.toString(System.currentTimeMillis());
            //添加登录回调
            HolderLoginCallback.getInstance().addLoginCallBack(loginReqUDID, new FlappyIMCallback<ResponseLogin>() {
                @Override
                public void success(ResponseLogin data) {
                    isRunningAutoLogin = false;
                }

                @Override
                public void failure(Exception ex, int code) {
                    isRunningAutoLogin = false;
                }
            });
            //开始链接
            FlappySocketService.getInstance().startConnect(
                    loginReqUDID,
                    responseInfo,
                    new NettyThreadDead() {
                        //断线重连，使用http的方式，也许服务器的ip已经发生了变化
                        @Override
                        public void threadDeadRetryHttp() {
                            checkAutoLoginHttp(FlappyConfig.getInstance().autoLoginSpace);
                        }

                        //断线重连，先试用netty的方式，防止http请求被过多的调用造成问题
                        @Override
                        protected void threadDeadRetryNetty() {
                            checkAutoLoginNetty(FlappyConfig.getInstance().autoLoginSpace, responseInfo);
                        }
                    }
            );
        }
    }


    //注销当前的登录
    public void logout(final FlappyIMCallback<String> callback) {
        synchronized (this) {
            //用户未登录
            if (!checkLogin(callback)) {
                return;
            }
            //可以登录
            if (!checkLoginEnable(callback)) {
                return;
            }
            //创建这个HashMap
            HashMap<String, Object> hashMap = new HashMap<>();
            //用户ID不用传了
            hashMap.put("userID", "");
            //外部用户ID
            hashMap.put("userExtendID", DataManager.getInstance().getLoginUser().getUserExtendId());
            //设备ID
            hashMap.put("device", FlappyConfig.getInstance().device);
            //推送ID
            hashMap.put("pushid", StringTool.getDeviceUnicNumber(getAppContext()));
            //推送平台
            hashMap.put("pushplat", FlappyConfig.getInstance().pushPlat);

            //进行callBack
            LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().logout,
                    hashMap,
                    new BaseParseCallback<String>(String.class) {
                        @Override
                        protected void stateFalse(BaseApiModel<String> model, String tag) {
                            if (callback != null) {
                                callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                            }
                        }

                        @Override
                        protected void jsonError(Exception e, String tag) {
                            if (callback != null) {
                                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                            }
                        }

                        @Override
                        protected void netError(Exception e, String tag) {
                            if (callback != null) {
                                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                            }
                        }

                        @Override
                        public void stateTrue(String response, String tag) {
                            logoutNetty();
                            if (callback != null) {
                                callback.success(response);
                            }
                        }
                    }
            );
        }
    }

    //退出登录
    private void logoutNetty() {
        synchronized (this) {
            FlappySocketService.getInstance().offline();
            DataManager.getInstance().clearLoginUser();
        }
    }


    //创建会话
    public void createSingleSession(final String peerUser, final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (!checkLogin(callback)) {
            return;
        }
        //检查聊天对象
        if (!checkPeerUser(peerUser, callback)) {
            return;
        }
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        //用户ID
        hashMap.put("userOne", DataManager.getInstance().getLoginUser().getUserExtendId());
        //外部用户ID
        hashMap.put("userTwo", peerUser);
        //调用
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().createSingleSession,
                hashMap,
                new BaseParseCallback<SessionData>(SessionData.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<SessionData> model, String tag) {
                        if (callback != null) {
                            callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                        }
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(SessionData data, String tag) {
                        FlappyChatSession session = new FlappyChatSession();
                        session.setSession(data);
                        if (callback != null) {
                            callback.success(session);
                        }
                    }
                }
        );
    }


    //获取单聊会话
    public void getSingleSession(final String peerUser, final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (!checkLogin(callback)) {
            return;
        }
        //检查聊天对象
        if (!checkPeerUser(peerUser, callback)) {
            return;
        }
        FlappyChatSession chatSession = new FlappyChatSession();
        //根据默认规则拼接出session的extendId
        String extendID = StringTool.getTwoUserString(
                peerUser,
                DataManager.getInstance().getLoginUser().getUserExtendId()
        );
        //数据库
        Database database = new Database();
        //获取数据
        SessionData data = database.getUserSessionByExtendID(extendID);
        //关闭
        database.close();
        //返回session
        if (data != null) {
            chatSession.setSession(data);
            callback.success(chatSession);
        } else {
            //从网络获取数据
            getSingleSessionHttp(peerUser, callback);
        }
    }


    //获取单聊会话
    public void getSingleSessionHttp(final String peerUser, final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (!checkLogin(callback)) {
            return;
        }
        //检查聊天对象
        if (!checkPeerUser(peerUser, callback)) {
            return;
        }
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userOne", DataManager.getInstance().getLoginUser().getUserExtendId());
        hashMap.put("userTwo", peerUser);
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().getSingleSession,
                hashMap,
                new BaseParseCallback<SessionData>(SessionData.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<SessionData> model, String tag) {
                        if (callback != null) {
                            callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                        }
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(SessionData data, String tag) {
                        //会话
                        FlappyChatSession session = new FlappyChatSession();
                        //设置名称
                        session.setSession(data);
                        //执行回调
                        if (callback != null) {
                            callback.success(session);
                        }
                    }
                }
        );
    }


    //创建群组会话
    public void createGroupSession(List<String> users,
                                   String groupID,
                                   String groupName,
                                   final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (!checkLogin(callback)) {
            return;
        }
        //检查用户
        if (!checkUsers(users, callback)) {
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("users", GsonTool.jsonArrayListStr(users));
        hashMap.put("createUser", DataManager.getInstance().getLoginUser().getUserId());
        hashMap.put("extendID", groupID);
        hashMap.put("sessionName", groupName);

        //调用
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().createGroupSession,
                hashMap,
                new BaseParseCallback<SessionData>(SessionData.class) {

                    @Override
                    protected void stateFalse(BaseApiModel<SessionData> model, String tag) {
                        if (callback != null) {
                            callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                        }
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(SessionData data, String tag) {
                        FlappyChatSession session = new FlappyChatSession();
                        session.setSession(data);
                        if (callback != null) {
                            callback.success(session);
                        }
                    }
                }
        );
    }


    //获取群组的会话
    public void getSessionByExtendID(String extendID, final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (!checkLogin(callback)) {
            return;
        }
        //数据库
        FlappyChatSession chatSession = new FlappyChatSession();
        Database database = new Database();
        SessionData data = database.getUserSessionByExtendID(extendID);
        database.close();
        if (data != null) {
            chatSession.setSession(data);
            callback.success(chatSession);
        } else {
            getSessionByExtendIDHttp(extendID, callback);
        }
    }


    //获取群组的会话
    public void getSessionByExtendIDHttp(String extendID, final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (!checkLogin(callback)) {
            return;
        }
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        //用户ID
        hashMap.put("extendID", extendID);
        //调用
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().getSessionByExtendID,
                hashMap,
                new BaseParseCallback<SessionData>(SessionData.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<SessionData> model, String tag) {
                        if (callback != null) {
                            callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(SessionData data, String tag) {
                        FlappyChatSession session = new FlappyChatSession();
                        session.setSession(data);
                        if (callback != null) {
                            callback.success(session);
                        }
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                        }
                    }
                }
        );
    }


    //通过用户ID获取session
    public void getUserSessions(final FlappyIMCallback<List<FlappyChatSession>> callback) {
        //检查登录
        if (!checkLogin(callback)) {
            return;
        }
        //数据库
        Database database = new Database();
        List<SessionData> data = database.getUserSessions();
        database.close();
        //获取所有会话
        if (data != null && !data.isEmpty()) {
            List<FlappyChatSession> sessions = new ArrayList<>();
            for (int s = 0; s < data.size(); s++) {
                FlappyChatSession chatSession = new FlappyChatSession();
                chatSession.setSession(data.get(s));
                sessions.add(chatSession);
            }
            Collections.sort(sessions, new Comparator<FlappyChatSession>() {
                @Override
                public int compare(FlappyChatSession one, FlappyChatSession two) {
                    if (one.getSession().getSessionType().intValue() == ChatSession.TYPE_SYSTEM) {
                        return -1;
                    }
                    if (two.getSession().getSessionType().intValue() == ChatSession.TYPE_SYSTEM) {
                        return 1;
                    }
                    if (one.getLatestMessage().getMessageTableSeq().longValue() >
                            two.getLatestMessage().getMessageTableSeq().longValue()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            callback.success(sessions);
        } else {
            getUserSessionsHttp(callback);
        }
    }


    //通过用户ID获取session
    public void getUserSessionsHttp(final FlappyIMCallback<List<FlappyChatSession>> callback) {

        //用户未登录
        if (!checkLogin(callback)) {
            return;
        }
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userExtendID", DataManager.getInstance().getLoginUser().getUserExtendId());
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().getUserSessions,
                hashMap,
                new BaseListParseCallBack<SessionData>(SessionData.class) {
                    @Override
                    public void stateFalse(String message, String tag) {
                        if (callback != null) {
                            callback.failure(new Exception(message), Integer.parseInt(tag));
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                        }
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                        }
                    }

                    @Override
                    protected void signError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(RESULT_NET_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(List<SessionData> data, String tag) {
                        if (callback != null) {
                            List<FlappyChatSession> sessions = new ArrayList<>();
                            for (int s = 0; s < data.size(); s++) {
                                FlappyChatSession chatSession = new FlappyChatSession();
                                chatSession.setSession(data.get(s));
                                sessions.add(chatSession);
                            }
                            Collections.sort(sessions, new Comparator<FlappyChatSession>() {
                                @Override
                                public int compare(FlappyChatSession one, FlappyChatSession two) {
                                    if (one.getSession().getSessionType().intValue() == ChatSession.TYPE_SYSTEM) {
                                        return -1;
                                    }
                                    if (two.getSession().getSessionType().intValue() == ChatSession.TYPE_SYSTEM) {
                                        return 1;
                                    }
                                    if (one.getLatestMessage().getMessageTableSeq().longValue() >
                                            two.getLatestMessage().getMessageTableSeq().longValue()) {
                                        return -1;
                                    } else {
                                        return 1;
                                    }
                                }
                            });
                            callback.success(sessions);
                        }
                    }
                }
        );
    }


    //群组中
    public void addUserToSession(
            String userID,
            String groupID,
            final FlappyIMCallback<String> callback) {

        //用户未登录
        if (!checkLogin(callback)) {
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", userID);
        hashMap.put("extendID", groupID);
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().addUserToSession,
                hashMap,
                new BaseParseCallback<String>(String.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<String> model, String tag) {
                        if (callback != null) {
                            callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                        }
                    }


                    @Override
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(String data, String tag) {
                        if (callback != null) {
                            callback.success(data);
                        }
                    }
                }
        );
    }


    //群组中
    public void delUserInSession(
            String userID,
            String groupID,
            final FlappyIMCallback<String> callback) {

        //用户未登录
        if (!checkLogin(callback)) {
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", userID);
        hashMap.put("extendID", groupID);
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().delUserInSession,
                hashMap,
                new BaseParseCallback<String>(String.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<String> model, String tag) {
                        if (callback != null) {
                            callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                        }
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(String data, String tag) {
                        if (callback != null) {
                            callback.success(data);
                        }
                    }

                }
        );
    }

    //检查聊天对象ID是否合法
    private boolean checkPeerUser(String peerUser, FlappyIMCallback callback) {
        if (StringTool.isEmpty(peerUser)) {
            if (callback != null) {
                callback.failure(new Exception("Peer user id is empty"), Integer.parseInt(RESULT_FAILURE));
            }
            return false;
        }
        if (peerUser.equals(DataManager.getInstance().getLoginUser().getUserExtendId())) {
            if (callback != null) {
                callback.failure(new Exception("Can't chat with your self"), Integer.parseInt(RESULT_NOT_LOGIN));
            }
            return false;
        }
        return true;
    }

    //检查用户ID列表是否合法
    private boolean checkUsers(List<String> users, FlappyIMCallback callback) {
        if (users == null || users.size() == 0) {
            if (callback != null) {
                callback.failure(new Exception("Users is empty"), Integer.parseInt(RESULT_FAILURE));
            }
            return false;
        }
        return true;
    }

    //检查用户的登录状态
    private boolean checkLogin(FlappyIMCallback callback) {
        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null) {
                callback.failure(new Exception("Not login"), Integer.parseInt(RESULT_NOT_LOGIN));
            }
            return false;
        }
        return true;
    }

    //检查用户是否正在登录
    private boolean checkLoginEnable(FlappyIMCallback callback) {
        if (isRunningLogin) {
            if (callback != null) {
                callback.failure(new Exception("Other thread is running login"), Integer.parseInt(RESULT_FAILURE));
            }
            return false;
        }
        return true;
    }

    //添加全局的监听
    public void addGlobalMessageListener(MessageListener listener) {
        HolderMessageSession.getInstance().addGlobalMessageListener(listener);
    }

    //移除全局的监听
    public void removeGlobalMessageListener(MessageListener listener) {
        HolderMessageSession.getInstance().removeGlobalMessageListener(listener);
    }

    //添加会话监听
    public void addSessionListener(SessionListener listener) {
        HolderMessageSession.getInstance().addSessionListener(listener);
    }

    //移除会话监听
    public void removeSessionListener(SessionListener listener) {
        HolderMessageSession.getInstance().removeSessionListener(listener);
    }


    //判断当前是否是登录的状态
    public boolean isLogin() {
        ChatUser user = DataManager.getInstance().getLoginUser();
        return user != null && user.isLogin() != 0;
    }

    //判断当前是否是在线的状态
    public boolean isOnline() {
        NettyThread thread = FlappySocketService.getInstance().getClientThread();
        if (thread == null) {
            return false;
        }
        ChannelMsgHandler msgHandler = thread.getChannelMsgHandler();
        if (msgHandler == null) {
            return false;
        }
        return msgHandler.isActive;
    }
}
