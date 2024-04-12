package com.flappygo.flappyim;

import com.flappygo.flappyim.ApiServer.Base.BaseListParseCallBack;
import com.flappygo.flappyim.Listener.NotificationClickListener;
import com.flappygo.flappyim.ApiServer.Base.BaseParseCallback;
import com.flappygo.flappyim.Thread.NettyThreadDeadListener;
import com.flappygo.flappyim.ApiServer.OkHttp.OkHttpClient;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Service.FlappySocketService;
import com.flappygo.flappyim.Listener.KickedOutListener;
import com.flappygo.flappyim.Holder.HolderLoginCallback;
import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.Handler.ChannelMsgHandler;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Session.FlappySessionData;
import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.Session.FlappyChatSession;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Listener.SessionListener;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Tools.NotificationTool;
import com.flappygo.flappyim.Push.PushMsgLanPack;
import com.flappygo.flappyim.Config.FlappyConfig;
import com.flappygo.flappyim.Thread.NettyThread;
import com.flappygo.flappyim.Datas.FlappyIMCode;
import com.flappygo.flappyim.Push.ConfigPushMsg;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.Push.PushSetting;
import com.flappygo.flappyim.Tools.RunTool;
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
import java.util.Objects;
import java.util.HashMap;
import java.util.List;

import static com.flappygo.flappyim.Models.Server.ChatRoute.PUSH_PRIVACY_TYPE_NORMAL;
import static com.flappygo.flappyim.Models.Server.ChatRoute.PUSH_PRIVACY_TYPE_HIDE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_CUSTOM;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_LOCATE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_SYSTEM;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_VOICE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_VIDEO;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_FILE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_TEXT;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_IMG;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_JSON_ERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NET_ERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NOT_LOGIN;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_FAILURE;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_EXPIRED;


/******
 * FlappyImService总服务
 */
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


    /******
     * 设置踢下线的监听
     */
    public void setKickedOutListener(KickedOutListener listener) {
        kickedOutListener = listener;
        ChatUser user = DataManager.getInstance().getLoginUser();
        if (user != null && user.isLogin() == 0) {
            if (kickedOutListener != null) {
                kickedOutListener.kickedOut();
            }
        }
    }


    /******
     * 消息被点击监听
     */
    public void setNotificationClickListener(NotificationClickListener listener) {
        notificationClickListener = listener;
        notifyClicked();
    }

    /******
     * 获取消息被点击的监听
     * @return 监听
     */
    public NotificationClickListener getNotificationClickListener() {
        return notificationClickListener;
    }

    /******
     * 通知被点击
     */
    public void notifyClicked() {
        String str = DataManager.getInstance().getNotificationClick();
        if (notificationClickListener != null && str != null) {
            ChatMessage message = GsonTool.jsonStringToModel(str, ChatMessage.class);
            notificationClickListener.notificationClicked(message);
            DataManager.getInstance().removeNotificationClick();
        }
    }


    /******
     * 自动Http重新登录和自动Netty重新登录
     * @param delayMillis 等待时间
     */
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

    /******
     * 检查重新登录netty
     * @param delayMillis 等待时间
     * @param responseLogin 登录信息
     */
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


    /******
     * 用于检测
     */
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            ///http重连
            if (msg.what == AUTO_LOGIN_HTTP) {
                autoLogin();
            }
            ///netty重连
            else if (msg.what == AUTO_LOGIN_NETTY) {
                autoLoginNetty((ResponseLogin) msg.obj);
            }
        }
    };


    /*******
     * 网络连接状态监听
     */
    BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
                checkAutoLoginHttp(0);
            }
        }
    };


    /******
     * 设置推送语言信息
     * @param languages 语言信息
     */
    public void setPushMsgLanPack(HashMap<String, PushMsgLanPack> languages) {
        if (!languages.isEmpty()) {
            ConfigPushMsg.languageMaps = languages;
        }
    }


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
                getAppContext().registerReceiver(netReceiver, timeFilter);
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
                getAppContext().unregisterReceiver(netReceiver);
                receiverRegistered = false;
            }
        }
    }


    /******
     * 本地通知弹窗
     */
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

        @Override
        public void messageReadOther(String sessionId, String readerId, String tableSequence) {

        }

        @Override
        public void messageReadSelf(String sessionId, String readerId, String tableSequence) {

        }

        @Override
        public void messageDelete(String messageId) {

        }
    };


    /******
     * 发送本地通知
     * @param chatMessage 消息
     */
    private void sendNotification(ChatMessage chatMessage) {
        PushSetting pushSetting = DataManager.getInstance().getPushSetting();
        //push settings
        if (pushSetting == null) {
            return;
        }
        //免打扰模式
        if (StringTool.strToInt(pushSetting.getRoutePushNoDisturb(), 0) == 1) {
            return;
        }
        //正在后台
        if (RunTool.isBackground(FlappyImService.this.getAppContext())) {
            NotificationTool util = new NotificationTool(FlappyImService.this.getAppContext());
            String privacy = pushSetting.getRoutePushPrivacy();
            String language = pushSetting.getRoutePushLanguage();
            //推送类型普通
            if (StringTool.strToInt(privacy, 0) == PUSH_PRIVACY_TYPE_NORMAL) {
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_TEXT) {
                    util.sendNotification(
                            chatMessage,
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getTitle(),
                            chatMessage.getChatText()
                    );
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_IMG) {
                    util.sendNotification(
                            chatMessage,
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getTitle(),
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getImgMsg()
                    );
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_VOICE) {
                    util.sendNotification(
                            chatMessage,
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getTitle(),
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getVoiceMsg()
                    );
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_LOCATE) {
                    util.sendNotification(
                            chatMessage,
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getTitle(),
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getLocateMsg()
                    );
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_VIDEO) {
                    util.sendNotification(
                            chatMessage,
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getTitle(),
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getVideoMsg()
                    );
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_FILE) {
                    util.sendNotification(
                            chatMessage,
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getTitle(),
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getFileMsg()
                    );
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_SYSTEM) {
                    util.sendNotification(
                            chatMessage,
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getTitle(),
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getSysMsg()
                    );
                }
                if (chatMessage.getMessageType().intValue() == MSG_TYPE_CUSTOM) {
                    util.sendNotification(
                            chatMessage,
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getTitle(),
                            Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getGeneralMsg()
                    );
                }
            }
            //推送类型隐藏
            else if (StringTool.strToInt(privacy, 0) == PUSH_PRIVACY_TYPE_HIDE) {
                util.sendNotification(
                        chatMessage,
                        Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getTitle(),
                        Objects.requireNonNull(ConfigPushMsg.getLanguagePushMsg(language)).getGeneralMsg()
                );
            }
        }
    }


    /******
     * 获取上下文
     * @return 上下文
     */
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
        Database database = Database.getInstance().open();
        database.clearSendingMessage();
        database.close();
    }

    /******
     * 初始化
     * @param appContext 上下文
     * @param serverPath Im服务器地址
     * @param uploadPath 资源服务器地址
     */
    public void init(Context appContext,
                     String serverPath,
                     String uploadPath
    ) {
        //获取application
        this.appContext = appContext.getApplicationContext();
        //更新服务器地址和资源文件上传地址
        FlappyConfig.getInstance().setServerUrl(serverPath, uploadPath);
        //添加总体的监听,定义全局防止多次重复添加这个监听
        HolderMessageSession.getInstance().addGlobalMessageListener(messageListener);
        //清空当前正在发送的消息
        Database database = Database.getInstance().open();
        database.clearSendingMessage();
        database.close();
    }

    /******
     * 推送的平台
     * @param flatForm 推送平台
     */
    public void setPushPlatform(String flatForm) {
        FlappyConfig.getInstance().pushPlat = flatForm;
    }


    /******
     * 正式开启服务
     */
    public void startServer() {
        initReceiver();
        checkAutoLoginHttp(0);
    }

    /******
     * 停止服务
     */
    public void stopServer() {
        removeReceiver();
        FlappySocketService.getInstance().offline();
    }


    /******
     * 注册设备推送Token
     * @param deviceToken 设备Token
     */
    public void registerDeviceToken(String deviceToken) {
        DataManager.getInstance().savePushId(deviceToken);
        updateDeviceToken(deviceToken);
    }


    /******
     * 更新推送设备信息
     * @param deviceToken  设备Token
     */
    private void updateDeviceToken(String deviceToken) {
        //没有登录不处理
        if (DataManager.getInstance().getLoginUser() == null || DataManager.getInstance().getLoginUser().isLogin() == 0) {
            return;
        }
        //创建这个HashMap
        HashMap<String, String> hashMap = new HashMap<>();
        //外部用户ID
        hashMap.put("userExtendID", DataManager.getInstance().getLoginUser().getUserExtendId());
        //设备ID
        hashMap.put("device", FlappyConfig.getInstance().device);
        //设备ID
        hashMap.put("pushId", deviceToken);
        //进行callBack
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().changePush(),
                hashMap,
                new BaseParseCallback<PushSetting>(PushSetting.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<PushSetting> model, String tag) {
                        resendUpdateDeviceToken(deviceToken);
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        resendUpdateDeviceToken(deviceToken);
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        resendUpdateDeviceToken(deviceToken);
                    }

                    @Override
                    public void stateTrue(PushSetting setting, String tag) {
                        DataManager.getInstance().savePushSetting(setting);
                    }
                }
        );
    }


    /******
     * 设置推送设置
     * @param pushSettings  推送设置
     * @param callback      回调
     */
    public void changePushSettings(PushSetting pushSettings,
                                   final FlappyIMCallback<PushSetting> callback) {

        ///没有登录直接设置
        if (DataManager.getInstance().getLoginUser() == null || DataManager.getInstance().getLoginUser().isLogin() == 0) {
            //保存推送信息
            DataManager.getInstance().savePushSetting(pushSettings);
            if (callback != null) {
                callback.success(DataManager.getInstance().getPushSetting());
            }
            return;
        }

        //创建这个HashMap
        HashMap<String, String> hashMap = new HashMap<>();
        //外部用户ID
        hashMap.put("userExtendID", DataManager.getInstance().getLoginUser().getUserExtendId());
        //设备ID
        hashMap.put("device", FlappyConfig.getInstance().device);
        //语言
        if (pushSettings.getRoutePushLanguage() != null) {
            hashMap.put("pushLanguage", pushSettings.getRoutePushLanguage());
        }
        //隐私
        if (pushSettings.getRoutePushPrivacy() != null) {
            hashMap.put("pushPrivacy", pushSettings.getRoutePushPrivacy());
        }
        //免打扰
        if (pushSettings.getRoutePushNoDisturb() != null) {
            hashMap.put("pushNoDisturb", pushSettings.getRoutePushNoDisturb());
        }
        //免打扰
        if (pushSettings.getRoutePushType() != null) {
            hashMap.put("pushType", pushSettings.getRoutePushType());
        }
        //进行callBack
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().changePush(),
                hashMap,
                new BaseParseCallback<PushSetting>(PushSetting.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<PushSetting> model, String tag) {
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
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(RESULT_NET_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(PushSetting setting, String tag) {
                        DataManager.getInstance().savePushSetting(setting);
                        if (callback != null) {
                            callback.success(DataManager.getInstance().getPushSetting());
                        }
                    }
                }
        );
    }

    /******
     * 获取推送设置
     * @return 推送设置
     */
    public PushSetting getPushSettings() {
        return DataManager.getInstance().getPushSetting();
    }


    /******
     * 重新注册设备ID
     * @param deviceToken 设备ID
     */
    private void resendUpdateDeviceToken(String deviceToken) {
        ///直到成功为止
        final Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                updateDeviceToken(deviceToken);
            }
        };
        handler.sendMessageDelayed(
                handler.obtainMessage(1),
                FlappyConfig.getInstance().autoLoginSpace
        );
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
        HashMap<String, String> hashMap = new HashMap<>();
        //设置index
        hashMap.put("userExtendID", userID);
        //用户名称
        hashMap.put("userName", userName);
        //用户头像
        hashMap.put("userAvatar", userAvatar);
        //用户名称
        hashMap.put("userData", userData);
        //进行callBack
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().register(),
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
        HashMap<String, String> hashMap = new HashMap<>();
        //设置index
        hashMap.put("userExtendID", userID);
        //用户名称
        hashMap.put("userName", userName);
        //用户头像
        hashMap.put("userAvatar", userAvatar);
        //用户名称
        hashMap.put("userData", userData);
        //进行callBack
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().updateUser(),
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

    /******
     * 用户登录
     * @param userExtendID  外部ID
     * @param callback      回调
     */
    public void login(String userExtendID, final FlappyIMCallback<ResponseLogin> callback) {
        synchronized (this) {
            //可以登录
            if (!checkLoginEnable(callback)) {
                return;
            }
            isRunningLogin = true;
            //创建这个HashMap
            HashMap<String, String> hashMap = new HashMap<>();
            //用户ID不用传了
            hashMap.put("userID", "");
            //外部用户ID
            hashMap.put("userExtendID", StringTool.ToNotNullStr(userExtendID));
            //设备ID
            hashMap.put("device", FlappyConfig.getInstance().device);
            //设备ID
            hashMap.put("pushId", DataManager.getInstance().getPushId());
            //设备ID
            hashMap.put("pushPlat", FlappyConfig.getInstance().pushPlat);
            //进行callBack
            OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().login(),
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
                            loginNetty(response, callback);
                        }
                    }
            );
        }
    }


    /******
     * 自动登录netty
     * @param response  登录信息
     * @param callback  回调
     */
    private void loginNetty(ResponseLogin response, FlappyIMCallback<ResponseLogin> callback) {
        synchronized (this) {

            //重置次数
            NettyThreadDeadListener.reset();
            //转换
            String loginReqUDID = Long.toString(System.currentTimeMillis());
            //添加登录回调
            HolderLoginCallback.getInstance().addLoginCallBack(loginReqUDID, new FlappyIMCallback<ResponseLogin>() {
                @Override
                public void success(ResponseLogin data) {
                    isRunningLogin = false;
                    if (callback != null) {
                        PushSetting setting = new PushSetting();
                        setting.setRoutePushPrivacy(response.getRoute().getRoutePushPrivacy());
                        setting.setRoutePushNoDisturb(response.getRoute().getRoutePushNoDisturb());
                        setting.setRoutePushLanguage(response.getRoute().getRoutePushLanguage());
                        setting.setRoutePushType(response.getRoute().getRoutePushType().toString());
                        DataManager.getInstance().savePushSetting(setting);
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
                    new NettyThreadDeadListener() {
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


    /******
     * 重新自动登录
     */
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
            HashMap<String, String> hashMap = new HashMap<>();
            //用户ID
            hashMap.put("userID", user.getUserId());
            //设备ID
            hashMap.put("device", FlappyConfig.getInstance().device);
            //设备ID
            hashMap.put("pushId", StringTool.getDeviceIDNumber(getAppContext()));
            //设备ID
            hashMap.put("pushPlat", FlappyConfig.getInstance().pushPlat);
            //进行callBack
            OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().autoLogin(),
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

    /******
     * 自动登录netty
     * @param response 登录信息
     */
    private void autoLoginNetty(ResponseLogin response) {
        synchronized (this) {
            //再次检查用户
            ChatUser user = DataManager.getInstance().getLoginUser();
            if (user == null || user.isLogin() == 0) {
                return;
            }

            //用户不一致，那么也不行
            if (!response.getUser().getUserId().equals(user.getUserId())) {
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
            NettyThreadDeadListener.reset();

            //保存数据
            ChatUser chatUser = DataManager.getInstance().getLoginUser();
            chatUser.setLogin(1);
            chatUser.setUserAvatar(response.getUser().getUserAvatar());
            chatUser.setUserName(response.getUser().getUserName());
            chatUser.setUserData(response.getUser().getUserData());
            DataManager.getInstance().saveLoginUser(chatUser);
            //转换
            String loginReqUDID = Long.toString(System.currentTimeMillis());
            //添加登录回调
            HolderLoginCallback.getInstance().addLoginCallBack(loginReqUDID, new FlappyIMCallback<ResponseLogin>() {
                @Override
                public void success(ResponseLogin data) {

                    //保存设置
                    PushSetting pushSetting = new PushSetting();
                    pushSetting.setRoutePushLanguage(response.getRoute().getRoutePushLanguage());
                    pushSetting.setRoutePushPrivacy(response.getRoute().getRoutePushPrivacy());
                    pushSetting.setRoutePushNoDisturb(response.getRoute().getRoutePushNoDisturb());
                    pushSetting.setRoutePushType(response.getRoute().getRoutePushType().toString());
                    DataManager.getInstance().savePushSetting(pushSetting);

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
                    response,
                    new NettyThreadDeadListener() {
                        //断线重连，使用http的方式，也许服务器的ip已经发生了变化
                        @Override
                        public void threadDeadRetryHttp() {
                            checkAutoLoginHttp(FlappyConfig.getInstance().autoLoginSpace);
                        }

                        //断线重连，先试用netty的方式，防止http请求被过多的调用造成问题
                        @Override
                        protected void threadDeadRetryNetty() {
                            checkAutoLoginNetty(FlappyConfig.getInstance().autoLoginSpace, response);
                        }
                    }
            );
        }
    }


    /******
     * 注销当前的登录
     * @param callback 回调
     */
    public void logout(final FlappyIMCallback<String> callback) {
        synchronized (this) {
            //用户未登录
            if (checkLogin(callback)) {
                return;
            }
            //可以登录
            if (!checkLoginEnable(callback)) {
                return;
            }
            //创建这个HashMap
            HashMap<String, String> hashMap = new HashMap<>();
            //用户ID不用传了
            hashMap.put("userID", "");
            //外部用户ID
            hashMap.put("userExtendID", DataManager.getInstance().getLoginUser().getUserExtendId());
            //设备ID
            hashMap.put("device", FlappyConfig.getInstance().device);
            //推送ID
            hashMap.put("pushId", StringTool.getDeviceIDNumber(getAppContext()));
            //推送平台
            hashMap.put("pushPlat", FlappyConfig.getInstance().pushPlat);

            //进行callBack
            OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().logout(),
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

    /******
     * 退出登录
     */
    private void logoutNetty() {
        synchronized (this) {
            FlappySocketService.getInstance().offline();
            DataManager.getInstance().clearLoginUser();
        }
    }


    /******
     * 创建会话
     * @param peerUser  对方ID
     * @param callback  回调
     */
    public void createSingleSession(final String peerUser, final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (checkLogin(callback)) {
            return;
        }
        //检查聊天对象
        if (checkPeerUserNotAvailable(peerUser, callback)) {
            return;
        }
        //创建这个HashMap
        HashMap<String, String> hashMap = new HashMap<>();
        //用户ID
        hashMap.put("userOne", DataManager.getInstance().getLoginUser().getUserExtendId());
        //外部用户ID
        hashMap.put("userTwo", peerUser);
        //调用
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().createSingleSession(),
                hashMap,
                new BaseParseCallback<FlappySessionData>(FlappySessionData.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<FlappySessionData> model, String tag) {
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
                    public void stateTrue(FlappySessionData data, String tag) {
                        if (callback != null) {
                            callback.success(new FlappyChatSession(data));
                        }
                    }
                }
        );
    }


    /******
     * 获取单聊会话
     * @param peerUser 对方ID
     * @param callback 回调
     */
    public void getSingleSession(final String peerUser, final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (checkLogin(callback)) {
            return;
        }
        //检查聊天对象
        if (checkPeerUserNotAvailable(peerUser, callback)) {
            return;
        }
        //根据默认规则拼接出session的extendId
        String extendID = StringTool.getTwoUserString(
                peerUser,
                DataManager.getInstance().getLoginUser().getUserExtendId()
        );
        //数据库
        Database database = Database.getInstance().open();
        //获取数据
        FlappySessionData data = database.getUserSessionByExtendID(extendID);
        //关闭
        database.close();
        //返回session
        if (data != null) {
            callback.success(new FlappyChatSession(data));
        } else {
            //从网络获取数据
            getSingleSessionHttp(peerUser, callback);
        }
    }


    /******
     * 获取单聊会话
     * @param peerUser 对方ID
     * @param callback 回调
     */
    public void getSingleSessionHttp(final String peerUser, final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (checkLogin(callback)) {
            return;
        }
        //检查聊天对象
        if (checkPeerUserNotAvailable(peerUser, callback)) {
            return;
        }
        //创建这个HashMap
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userOne", DataManager.getInstance().getLoginUser().getUserExtendId());
        hashMap.put("userTwo", peerUser);
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().getSingleSession(),
                hashMap,
                new BaseParseCallback<FlappySessionData>(FlappySessionData.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<FlappySessionData> model, String tag) {
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
                    public void stateTrue(FlappySessionData data, String tag) {
                        //执行回调
                        if (callback != null) {
                            callback.success(new FlappyChatSession(data));
                        }
                    }
                }
        );
    }


    /******
     * 创建群组会话
     * @param users     用户ID列表
     * @param groupID   群组ID
     * @param groupName 群组名称
     * @param callback  回调
     */
    public void createGroupSession(List<String> users,
                                   String groupID,
                                   String groupName,
                                   final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (checkLogin(callback)) {
            return;
        }
        //检查用户
        if (!checkUsers(users, callback)) {
            return;
        }

        //创建这个HashMap
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("users", GsonTool.jsonArrayListStr(users));
        hashMap.put("createUser", DataManager.getInstance().getLoginUser().getUserId());
        hashMap.put("extendID", groupID);
        hashMap.put("sessionName", groupName);

        //调用
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().createGroupSession(),
                hashMap,
                new BaseParseCallback<FlappySessionData>(FlappySessionData.class) {

                    @Override
                    protected void stateFalse(BaseApiModel<FlappySessionData> model, String tag) {
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
                    public void stateTrue(FlappySessionData data, String tag) {
                        if (callback != null) {
                            callback.success(new FlappyChatSession(data));
                        }
                    }
                }
        );
    }


    /******
     * 获取群组的会话
     * @param extendID 群组ID
     * @param callback 回调
     */
    public void getSessionByExtendID(String extendID, final FlappyIMCallback<FlappyChatSession> callback) {
        if (checkLogin(callback)) {
            return;
        }
        Database database = Database.getInstance().open();
        FlappySessionData data = database.getUserSessionByExtendID(extendID);
        database.close();
        if (data == null) {
            getSessionByExtendIDHttp(extendID, callback);
            return;
        }
        callback.success(new FlappyChatSession(data));
    }


    /******
     * 获取群组的会话
     * @param extendID 群组ID
     * @param callback 回调
     */
    public void getSessionByExtendIDHttp(String extendID, final FlappyIMCallback<FlappyChatSession> callback) {
        //检查登录
        if (checkLogin(callback)) {
            return;
        }
        //创建这个HashMap
        HashMap<String, String> hashMap = new HashMap<>();
        //用户ID
        hashMap.put("extendID", extendID);
        //调用
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().getSessionByExtendID(),
                hashMap,
                new BaseParseCallback<FlappySessionData>(FlappySessionData.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<FlappySessionData> model, String tag) {
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
                    public void stateTrue(FlappySessionData data, String tag) {
                        if (callback != null) {
                            callback.success(new FlappyChatSession(data));
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


    /******
     * 通过用户ID获取session
     * @param callback 回调
     */
    public void getUserSessions(final FlappyIMCallback<List<FlappyChatSession>> callback) {
        //检查登录
        if (checkLogin(callback)) {
            return;
        }
        //数据库
        Database database = Database.getInstance().open();
        List<FlappySessionData> data = database.getUserSessions();
        database.close();

        //数据为空，去网上拿
        if (data == null || data.isEmpty()) {
            getUserSessionsHttp(callback);
            return;
        }

        //数据不为空
        List<FlappyChatSession> sessions = new ArrayList<>();

        //创建FlappyChatSession对象
        for (int s = 0; s < data.size(); s++) {
            sessions.add(new FlappyChatSession(data.get(s)));
        }

        //进行最后一条消息整体排序
        Collections.sort(sessions, new Comparator<FlappyChatSession>() {
            @Override
            public int compare(FlappyChatSession one, FlappyChatSession two) {
                if (one.getSession().getSessionType().intValue() == ChatSession.TYPE_SYSTEM) {
                    return -1;
                }
                if (two.getSession().getSessionType().intValue() == ChatSession.TYPE_SYSTEM) {
                    return 1;
                }
                ChatMessage msgOne = one.getLatestMessage();
                ChatMessage msgTwo = two.getLatestMessage();
                if (msgOne == null) {
                    return 1;
                }
                if (msgTwo == null) {
                    return -1;
                }
                return Long.compare(
                        msgTwo.getMessageTableSeq().longValue(),
                        msgOne.getMessageTableSeq().longValue()
                );
            }
        });

        //返回成功
        callback.success(sessions);
    }


    /******
     * 通过用户ID获取session
     * @param callback 回调
     */
    public void getUserSessionsHttp(final FlappyIMCallback<List<FlappyChatSession>> callback) {

        //用户未登录
        if (checkLogin(callback)) {
            return;
        }
        //创建这个HashMap
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userExtendID", DataManager.getInstance().getLoginUser().getUserExtendId());
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().getUserSessions(),
                hashMap,
                new BaseListParseCallBack<FlappySessionData>(FlappySessionData.class) {
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
                    public void stateTrue(List<FlappySessionData> data, String tag) {
                        if (callback != null) {
                            List<FlappyChatSession> sessions = new ArrayList<>();
                            for (int s = 0; s < data.size(); s++) {
                                sessions.add(new FlappyChatSession(data.get(s)));
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
                                    ChatMessage msgOne = one.getLatestMessage();
                                    ChatMessage msgTwo = two.getLatestMessage();
                                    if (msgOne == null) {
                                        return 1;
                                    }
                                    if (msgTwo == null) {
                                        return -1;
                                    }
                                    return Long.compare(
                                            msgTwo.getMessageTableSeq().longValue(),
                                            msgOne.getMessageTableSeq().longValue()
                                    );
                                }
                            });
                            callback.success(sessions);
                        }
                    }
                }
        );
    }


    /******
     * 向群组中添加用户
     * @param userID   用户ID
     * @param groupID  群组ID
     * @param callback 回调
     */
    public void addUserToSession(
            String userID,
            String groupID,
            final FlappyIMCallback<String> callback) {

        //用户未登录
        if (checkLogin(callback)) {
            return;
        }

        //创建这个HashMap
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userID", userID);
        hashMap.put("extendID", groupID);
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().addUserToSession(),
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


    /******
     * 删除群组中的用户
     * @param userID   用户ID
     * @param groupID  群组ID
     * @param callback 回调
     */
    public void delUserInSession(
            String userID,
            String groupID,
            final FlappyIMCallback<String> callback) {
        //用户未登录
        if (checkLogin(callback)) {
            return;
        }
        //创建这个HashMap
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userID", userID);
        hashMap.put("extendID", groupID);
        OkHttpClient.getInstance().postParam(FlappyConfig.getInstance().delUserInSession(),
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

    /******
     * 检查聊天对象ID是否合法
     * @param peerUser 对方ID
     * @param callback 回调
     * @return 检查结果
     */
    private boolean checkPeerUserNotAvailable(String peerUser, FlappyIMCallback<FlappyChatSession> callback) {
        if (StringTool.isEmpty(peerUser)) {
            if (callback != null) {
                callback.failure(new Exception("Peer user id is empty"), Integer.parseInt(RESULT_FAILURE));
            }
            return true;
        }
        if (peerUser.equals(DataManager.getInstance().getLoginUser().getUserExtendId())) {
            if (callback != null) {
                callback.failure(new Exception("Can't chat with your self"), Integer.parseInt(RESULT_NOT_LOGIN));
            }
            return true;
        }
        return false;
    }

    /******
     * 检查用户ID列表是否合法
     * @param users 对方ID
     * @param callback 回调
     * @return 成功、失败
     */
    private boolean checkUsers(List<String> users, FlappyIMCallback<FlappyChatSession> callback) {
        if (users == null || users.size() == 0) {
            if (callback != null) {
                callback.failure(new Exception("Users is empty"), Integer.parseInt(RESULT_FAILURE));
            }
            return false;
        }
        return true;
    }

    /******
     * 检查用户的登录状态
     * @param callback 回调
     * @return 成功、失败
     */
    private boolean checkLogin(FlappyIMCallback callback) {
        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null) {
                callback.failure(new Exception("Not login"), Integer.parseInt(RESULT_NOT_LOGIN));
            }
            return true;
        }
        return false;
    }

    /******
     * 检查用户是否正在登录
     * @param callback 检查登录可用
     * @return 是否可用
     */
    private boolean checkLoginEnable(FlappyIMCallback callback) {
        if (isRunningLogin) {
            if (callback != null) {
                callback.failure(new Exception("Other thread is running login"), Integer.parseInt(RESULT_FAILURE));
            }
            return false;
        }
        return true;
    }

    /******
     * 添加全局的监听
     * @param listener 监听
     */
    public void addGlobalMessageListener(MessageListener listener) {
        HolderMessageSession.getInstance().addGlobalMessageListener(listener);
    }

    /******
     * 移除全局的监听
     * @param listener 监听
     */
    public void removeGlobalMessageListener(MessageListener listener) {
        HolderMessageSession.getInstance().removeGlobalMessageListener(listener);
    }

    /******
     * 添加会话监听
     * @param listener 监听
     */
    public void addSessionListener(SessionListener listener) {
        HolderMessageSession.getInstance().addSessionListener(listener);
    }

    /******
     * 移除会话监听
     * @param listener 监听
     */
    public void removeSessionListener(SessionListener listener) {
        HolderMessageSession.getInstance().removeSessionListener(listener);
    }


    /******
     * 判断当前是否是登录的状态
     * @return 是否登录
     */
    public boolean isLogin() {
        ChatUser user = DataManager.getInstance().getLoginUser();
        return user != null && user.isLogin() != 0;
    }

    /******
     * 判断当前是否是在线的状态
     * @return 是否在线
     */
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
