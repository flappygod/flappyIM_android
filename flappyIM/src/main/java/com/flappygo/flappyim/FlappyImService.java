package com.flappygo.flappyim;

import com.flappygo.flappyim.ApiServer.Base.BaseListParseCallBack;
import com.flappygo.flappyim.ApiServer.Base.BaseParseCallback;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.FlappyIMCode;
import com.flappygo.flappyim.Config.FlappyConfig;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Holder.HolderLoginCallback;
import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Listener.KnickedOutListener;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Listener.NotificationClickListener;
import com.flappygo.flappyim.Listener.SessionListener;
import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Service.FlappyService;
import com.flappygo.flappyim.Session.FlappyChatSession;
import com.flappygo.flappyim.Thread.NettyThreadDead;
import com.flappygo.flappyim.Tools.NotificationUtil;
import com.flappygo.flappyim.Tools.RunninTool;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.lilin.lxhttpclient.LXHttpClient;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_JSON_ERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NET_ERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NOT_LOGIN;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_IMG;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_LOCATE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_TEXT;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_VIDEO;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_VOICE;
import static com.flappygo.flappyim.Models.Server.ChatRoute.PUSH_TYPE_HIDE;
import static com.flappygo.flappyim.Models.Server.ChatRoute.PUSH_TYPE_NORMAL;


//服务
public class FlappyImService {

    //上下文
    private Context appContext;

    //是否显示notification
    private boolean showNotification;

    //消息的监听
    private MessageListener messageListener = chatMessage -> {
        //发送本地通知
        sendNotification(chatMessage);
    };


    //获取上下文
    public Context getAppContext() {
        //没有初始化就报错
        if (appContext == null) {
            throw new RuntimeException("flappy im not init,call init first");
        }
        //返回
        return appContext;
    }

    //初始化
    public void init(Context appContext) {
        //初始化上下文
        this.appContext = appContext.getApplicationContext();
        //初始化context
        FlappyService.getInstance().init(appContext.getApplicationContext());
        //添加总体的监听
        HolderMessageSession.getInstance().addGloableMessageListener(messageListener);
    }

    //初始化
    public void init(Context appContext, String serverPath, String uploadPath) {
        //获取application
        this.appContext = appContext.getApplicationContext();
        //初始化context
        FlappyService.getInstance().init(appContext.getApplicationContext());
        //更新服务器地址和资源文件上传地址
        FlappyConfig.getInstance().setServerUrl(serverPath, uploadPath);
        //添加总体的监听,定义全局防止多次重复添加这个监听
        HolderMessageSession.getInstance().addGloableMessageListener(messageListener);
    }

    //推送的平台
    public void setPushPlatform(String flatForm) {
        FlappyConfig.getInstance().pushPlat = flatForm;
    }

    //单例模式
    private static final class InstanceHolder {
        static final FlappyImService instance = new FlappyImService();
    }

    //单例manager
    public static FlappyImService getInstance() {
        return InstanceHolder.instance;
    }

    //正式开启服务
    public void startServer() {
        //开启服务
        FlappyService.getInstance().startService();
    }

    //停止服务
    public void stopServer() {
        FlappyService.getInstance().stopService();
    }

    //设置notification
    public void setNotification(boolean flag) {
        this.showNotification = flag;
    }

    //发送本地通知
    private void sendNotification(ChatMessage chatMessage) {
        if (!showNotification) {
            return;
        }
        //正在后台
        if (RunninTool.isBackground(FlappyImService.this.appContext)) {
            //上下文
            NotificationUtil util = new NotificationUtil(FlappyImService.this.appContext);
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


    //创建用户账户
    public void createAccount(String userID,
                              String userName,
                              String userAvatar,
                              final FlappyIMCallback<String> callback) {

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        //设置index
        hashMap.put("userExtendID", userID);
        //用户名称
        hashMap.put("userName", userName);
        //用户头像
        hashMap.put("userAvatar", userAvatar);
        //进行callBack
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().register,
                hashMap,
                new BaseParseCallback<String>(String.class) {
                    @Override
                    protected void stateFalse(BaseApiModel model, String tag) {
                        //失败
                        if (callback != null) {
                            callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        //解析失败
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(RESULT_JSON_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(String s, String tag) {
                        //这里代表注册账户成功
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
                }, null);
    }


    //这里就代表登录了
    public void login(String userExtendID, final FlappyIMCallback<ResponseLogin> callback) {

        synchronized (FlappyService.getInstance()) {
            //正在登录
            FlappyService.getInstance().isLoading = true;

            //创建这个HashMap
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
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

                            FlappyService.getInstance().isLoading = false;
                            //失败
                            if (callback != null) {
                                callback.failure(new Exception(model.getMsg()),
                                        Integer.parseInt(model.getCode()));
                            }
                        }

                        @Override
                        protected void jsonError(Exception e, String tag) {
                            FlappyService.getInstance().isLoading = false;
                            //解析失败
                            if (callback != null) {
                                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                            }
                        }

                        @Override
                        public void stateTrue(ResponseLogin response, String tag) {
                            //生成一个时间戳，用户保证多次重复请求的情况
                            long uuid = System.currentTimeMillis();
                            //转换
                            String str = Long.toString(uuid);
                            //保存设置
                            DataManager.getInstance().savePushType(StringTool.decimalToStr(response.getRoute().getRoutePushType()));
                            //添加登录回调
                            HolderLoginCallback.getInstance().addLoginCallBack(str, new FlappyIMCallback<ResponseLogin>() {
                                @Override
                                public void success(ResponseLogin data) {
                                    if (callback != null) {
                                        callback.success(data);
                                    }
                                    FlappyService.getInstance().isLoading = false;
                                }

                                @Override
                                public void failure(Exception ex, int code) {
                                    if (callback != null) {
                                        callback.failure(ex, code);
                                    }
                                    FlappyService.getInstance().isLoading = false;
                                }
                            });
                            //重置
                            NettyThreadDead.reset();
                            //开启服务
                            FlappyService.getInstance().startService(str, response);
                        }

                        @Override
                        protected void netError(Exception e, String tag) {
                            FlappyService.getInstance().isLoading = false;
                            if (callback != null) {
                                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                            }
                        }
                    }, null);
        }
    }


    //创建会话
    public void createSingleSession(final String userTwo, final FlappyIMCallback<FlappyChatSession> callback) {
        //用户未登录
        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null)
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            return;
        }
        //判断是否为空
        if (StringTool.isEmpty(userTwo)) {
            throw new RuntimeException("账户ID不能为空");
        }
        //创建extend id
        if (userTwo.equals(DataManager.getInstance().getLoginUser().getUserExtendId())) {
            throw new RuntimeException("创建session账户不能是当前账户");
        }
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID
        hashMap.put("userOne", DataManager.getInstance().getLoginUser().getUserExtendId());
        //外部用户ID
        hashMap.put("userTwo", userTwo);
        //调用
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().createSingleSession, hashMap, new BaseParseCallback<SessionData>(SessionData.class) {
            @Override
            protected void stateFalse(BaseApiModel model, String tag) {
                //失败
                if (callback != null) {
                    callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                }
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
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
        }, null);
    }


    //获取两个用户的关联ID
    private static String getTwoUserString(String userOne, String userTwo) {
        List<String> strList = new ArrayList<>();
        strList.add(userOne);
        strList.add(userTwo);
        Collections.sort(strList);
        return strList.get(0) + "-" + strList.get(1);
    }


    //获取单聊会话
    public void getSingleSession(final String userTwo, final FlappyIMCallback<FlappyChatSession> callback) {
        //用户未登录
        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null)
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            return;
        }
        //判断是否为空
        if (StringTool.isEmpty(userTwo)) {
            throw new RuntimeException("账户ID不能为空");
        }
        //创建extend id
        if (userTwo.equals(DataManager.getInstance().getLoginUser().getUserExtendId())) {
            throw new RuntimeException("获取的session不能是自己");
        }

        FlappyChatSession chatSession = new FlappyChatSession();
        //用户
        List<String> user = new ArrayList<>();
        //用户
        user.add(userTwo);
        //添加
        user.add(DataManager.getInstance().getLoginUser().getUserExtendId());
        //排序
        Collections.sort(user);
        //ID
        String extendID = user.get(0) + "-" + user.get(1);
        //数据库
        Database database = new Database();
        SessionData data = database.getUserSessionByExtendID(extendID);
        database.close();

        if (data != null) {
            chatSession.setSession(data);
            callback.success(chatSession);
        } else {
            getSingleSessionHttp(userTwo, callback);
        }
    }


    //获取单聊会话
    public void getSingleSessionHttp(final String userTwo, final FlappyIMCallback<FlappyChatSession> callback) {
        //用户未登录
        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null)
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            return;
        }
        //判断是否为空
        if (StringTool.isEmpty(userTwo)) {
            throw new RuntimeException("账户ID不能为空");
        }
        //创建extend id
        if (userTwo.equals(DataManager.getInstance().getLoginUser().getUserExtendId())) {
            throw new RuntimeException("创建session账户不能是当前账户");
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID
        hashMap.put("userOne", DataManager.getInstance().getLoginUser().getUserExtendId());
        //外部用户ID
        hashMap.put("userTwo", userTwo);
        //调用
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().getSingleSession, hashMap, new BaseParseCallback<SessionData>(SessionData.class) {
            @Override
            protected void stateFalse(BaseApiModel model, String tag) {
                //失败
                if (callback != null) {
                    callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                }
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if (callback != null) {
                    callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
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

            @Override
            protected void netError(Exception e, String tag) {
                if (callback != null) {
                    callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                }
            }
        }, null);
    }


    //创建群组会话
    public void createGroupSession(List<String> users,
                                   String groupID,
                                   String groupName,
                                   final FlappyIMCallback<FlappyChatSession> callback) {

        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null)
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        //用户ID
        hashMap.put("users", GsonTool.jsonArrayListStr(users));
        //外部用户ID
        hashMap.put("createUser", DataManager.getInstance().getLoginUser().getUserId());
        //外部的群组ID
        hashMap.put("extendID", groupID);
        //外部会话的名称
        hashMap.put("sessionName", groupName);

        //调用
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().createGroupSession, hashMap, new BaseParseCallback<SessionData>(SessionData.class) {

            @Override
            protected void stateFalse(BaseApiModel<SessionData> model, String tag) {
                //失败
                if (callback != null) {
                    callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                }
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
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
        }, null);
    }


    //获取群组的会话
    public void getSessionByExtendID(String extendID,
                                     final FlappyIMCallback<FlappyChatSession> callback) {

        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null)
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            return;
        }
        FlappyChatSession chatSession = new FlappyChatSession();
        //数据库
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
    public void getSessionByExtendIDHttp(String extendID,
                                         final FlappyIMCallback<FlappyChatSession> callback) {

        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null)
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID
        hashMap.put("extendID", extendID);
        //调用
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().getSessionByExtendID, hashMap, new BaseParseCallback<SessionData>(SessionData.class) {
            @Override
            protected void stateFalse(BaseApiModel<SessionData> model, String tag) {
                //失败
                if (callback != null) {
                    callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                }
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
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
        }, null);
    }


    //通过用户ID获取session
    public void getUserSessions(final FlappyIMCallback<List<FlappyChatSession>> callback) {
        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null) {
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            }
            return;
        }
        //数据库
        Database database = new Database();
        List<SessionData> data = database.getUserSessions();
        database.close();
        //获取所有会话
        if (data == null) {
            List<FlappyChatSession> sessions = new ArrayList<>();
            for (int s = 0; s < data.size(); s++) {
                FlappyChatSession chatSession = new FlappyChatSession();
                chatSession.setSession(data.get(s));
                sessions.add(chatSession);
            }
            callback.success(sessions);
        } else {
            getUserSessionsHttp(callback);
        }
    }


    //通过用户ID获取session
    public void getUserSessionsHttp(final FlappyIMCallback<List<FlappyChatSession>> callback) {

        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null)
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            return;
        }
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        //用户ID
        hashMap.put("userExtendID", DataManager.getInstance().getLoginUser().getUserExtendId());
        //调用
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().getUserSessions, hashMap, new BaseListParseCallBack<SessionData>(SessionData.class) {

            @Override
            public void stateFalse(String message, String tag) {
                //失败
                if (callback != null) {
                    callback.failure(new Exception(message), Integer.parseInt(tag));
                }
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if (callback != null) {
                    callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
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
                    callback.success(sessions);
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

            }
        }, null);
    }


    //群组中
    public void addUserToSession(
            String userID,
            String groupID,
            final FlappyIMCallback<String> callback) {

        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null)
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        //用户ID
        hashMap.put("userID", userID);
        //群组的ID
        hashMap.put("extendID", groupID);
        //调用
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().addUserToSession, hashMap, new BaseParseCallback<String>(String.class) {
            @Override
            protected void stateFalse(BaseApiModel<String> model, String tag) {
                //失败
                if (callback != null) {
                    callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                }
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if (callback != null) {
                    callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                }
            }

            @Override
            public void stateTrue(String data, String tag) {
                if (callback != null) {
                    callback.success(data);
                }
            }

            @Override
            protected void netError(Exception e, String tag) {
                if (callback != null) {
                    callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                }
            }
        }, null);
    }


    //群组中
    public void delUserInSession(
            String userID,
            String groupID,
            final FlappyIMCallback<String> callback) {

        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null) {
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            }
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        //用户ID
        hashMap.put("userID", userID);
        //群组的ID
        hashMap.put("extendID", groupID);
        //调用
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().delUserInSession, hashMap, new BaseParseCallback<String>(String.class) {
            @Override
            protected void stateFalse(BaseApiModel<String> model, String tag) {
                //失败
                if (callback != null) {
                    callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                }
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if (callback != null) {
                    callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                }
            }

            @Override
            public void stateTrue(String data, String tag) {
                if (callback != null) {
                    callback.success(data);
                }
            }

            @Override
            protected void netError(Exception e, String tag) {
                if (callback != null) {
                    callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                }
            }
        }, null);
    }


    //注销当前的登录
    public void logout(final FlappyIMCallback<String> callback) {
        if (DataManager.getInstance().getLoginUser() == null) {
            if (callback != null) {
                callback.failure(new Exception("当前用户未登录"), Integer.parseInt(RESULT_NOT_LOGIN));
            }
            return;
        }
        //先关闭当前的长连接
        if (FlappyService.getInstance() != null) {
            //关闭长连接下线
            FlappyService.getInstance().offline();
        }
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<>();
        //用户ID不用传了
        hashMap.put("userID", "");
        //外部用户ID
        hashMap.put("userExtendID", DataManager.getInstance().getLoginUser().getUserExtendId());
        //设备ID
        hashMap.put("device", FlappyConfig.getInstance().device);
        //设备ID
        hashMap.put("pushid", StringTool.getDeviceUnicNumber(getAppContext()));
        //设备ID
        hashMap.put("pushplat", FlappyConfig.getInstance().pushPlat);

        //进行callBack
        LXHttpClient.getInstacne().postParam(FlappyConfig.getInstance().logout,
                hashMap,
                new BaseParseCallback<String>(String.class) {
                    @Override
                    protected void stateFalse(BaseApiModel<String> model, String tag) {
                        //失败
                        if (callback != null) {
                            callback.failure(new Exception(model.getMsg()), Integer.parseInt(model.getCode()));
                        }
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        //解析失败
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSON_ERROR));
                        }
                    }

                    @Override
                    public void stateTrue(String response, String tag) {
                        //清空当期的用户数据信息
                        DataManager.getInstance().clearUser();
                        //退出登录成功
                        if (callback != null) {
                            callback.success(response);
                        }
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if (callback != null) {
                            callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NET_ERROR));
                        }
                    }
                }, null);
    }


    //添加全局的监听
    public void addGlobalMessageListener(MessageListener listener) {
        HolderMessageSession.getInstance().addGloableMessageListener(listener);
    }

    //移除全局的监听
    public void removeGlobalMessageListener(MessageListener listener) {
        HolderMessageSession.getInstance().removeGloableMessageListener(listener);
    }

    //添加会话监听
    public void addSessionListener(SessionListener listener) {
        HolderMessageSession.getInstance().addSessionListener(listener);
    }

    //移除会话监听
    public void removeSessionListener(SessionListener listener) {
        HolderMessageSession.getInstance().removeSessionListener(listener);
    }

    //设置被踢下线的监听
    public void setKickedOutListener(KnickedOutListener listener) {
        if (FlappyService.getInstance() != null) {
            FlappyService.getInstance().setKickedOutListener(listener);
        }
    }

    //设置通知消息被点击的监听
    public void setNotificationClickListener(NotificationClickListener listener) {
        if (FlappyService.getInstance() != null) {
            FlappyService.getInstance().setNotificationClickListener(listener);
        }
    }

    //判断当前是否是登录的状态
    public boolean isLogin() {
        //存在账号
        ChatUser user = DataManager.getInstance().getLoginUser();
        //为空或者被踢下线
        if (user == null || user.isLogin() == 0) {
            return false;
        }
        return true;
    }

    //当前是否在线
    public boolean isOnline() {
        if (FlappyService.getInstance() != null) {
            return FlappyService.getInstance().isOnline();
        }
        return false;
    }
}
