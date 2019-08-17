package com.flappygo.flappyim;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.flappygo.flappyim.ApiServer.Base.BaseListParseCallBack;
import com.flappygo.flappyim.ApiServer.Base.BaseParseCallback;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.Datas.FlappyIMCode;
import com.flappygo.flappyim.Config.BaseConfig;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Holder.HolderLoginCallback;
import com.flappygo.flappyim.Holder.HolderMessageRecieve;
import com.flappygo.flappyim.Listener.KnickedOutListener;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.Session.FlappyBaseSession;
import com.flappygo.flappyim.Session.SessionData;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Service.FlappyService;
import com.flappygo.flappyim.Session.FlappyChatSession;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.lilin.lxhttpclient.LXHttpClient;

import java.util.HashMap;
import java.util.List;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_JSONERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NETERROR;
import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NOTLOGIN;


//服务
public class FlappyImService {

    //单例模式
    private static FlappyImService instacne;

    //上下文
    private Context appContext;

    /********
     * 单例manager
     * @return
     */
    public static FlappyImService getInstance() {
        if (instacne == null) {
            synchronized (FlappyImService.class) {
                if (instacne == null) {
                    instacne = new FlappyImService();
                }
            }
        }
        return instacne;
    }

    //初始化
    public void init(Context appContext) {
        this.appContext = appContext.getApplicationContext();
        //创建intent
        Intent intent = new Intent();
        //设置服务名称
        intent.setClass(getAppContext(), FlappyService.class);
        //开启服务
        getAppContext().startService(intent);
    }

    //初始化
    public void init(Context appContext,String serverPath,String  uploadPath) {

        //更新服务器地址和资源文件上传地址
        BaseConfig.getInstance().setServerUrl(serverPath,uploadPath);

        this.appContext = appContext.getApplicationContext();
        //创建intent
        Intent intent = new Intent();
        //设置服务名称
        intent.setClass(getAppContext(), FlappyService.class);
        //开启服务
        getAppContext().startService(intent);
    }


    //获取上下文
    public Context getAppContext() {
        //没有初始化就报错
        if (appContext == null) {
            throw new RuntimeException("flappyim not init,call init first");
        }
        //返回
        return appContext;
    }


    //创建用户账户
    public void createAccount(String userID,
                              String userName,
                              String userHead,
                              final FlappyIMCallback<String> callback) {

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //设置index
        hashMap.put("userExtendID", userID);
        //用户名称
        hashMap.put("userName", userName);
        //用户头像
        hashMap.put("userHead", userHead);
        //进行callBack
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().register,
                hashMap,
                new BaseParseCallback<String>(String.class) {
                    @Override
                    protected void stateFalse(BaseApiModel model, String tag) {
                        //失败
                        if(callback!=null)
                        callback.failure(new Exception(model.getResultMessage()), Integer.parseInt(model.getResultCode()));
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        //解析失败
                        if(callback!=null)
                        callback.failure(e, Integer.parseInt(RESULT_JSONERROR));
                    }

                    @Override
                    public void stateTrue(String s, String tag) {
                        //这里代表注册账户成功
                        if(callback!=null)
                        callback.success(s);
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if(callback!=null)
                        callback.failure(e, Integer.parseInt(RESULT_NETERROR));
                    }
                }, null);
    }


    //这里就代表登录了
    public void login(String userExtendID, final FlappyIMCallback<ResponseLogin> callback) {

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID不用传了
        hashMap.put("userID", "");
        //外部用户ID
        hashMap.put("userExtendID", StringTool.ToNotNullStr(userExtendID));
        //设备ID
        hashMap.put("device", BaseConfig.device);
        //设备ID
        hashMap.put("pushid", StringTool.getDeviceUnicNumber(getAppContext()));


        //进行callBack
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().login,
                hashMap,
                new BaseParseCallback<ResponseLogin>(ResponseLogin.class) {
                    @Override
                    protected void stateFalse(BaseApiModel model, String tag) {
                        //失败
                        if(callback!=null)
                        callback.failure(new Exception(model.getResultMessage()), Integer.parseInt(model.getResultCode()));
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        //解析失败
                        if(callback!=null)
                        callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSONERROR));
                    }

                    @Override
                    public void stateTrue(ResponseLogin response, String tag) {
                        //这里代表注册账户成功,但是我们还没有登录IM,所以不能够返回登录成功
                        //callback.success(s);
                        //创建intent
                        Intent intent = new Intent();

                        //生成一个时间戳，用户保证多次重复请求的情况
                        long uuid = System.currentTimeMillis();

                        //设置服务名称
                        intent.setClass(getAppContext(), FlappyService.class);
                        //设置服务器的地址
                        intent.putExtra("serverAddress", response.getServerIP());
                        //设置服务器的端口
                        intent.putExtra("serverPort", response.getServerPort());
                        //设置id
                        intent.putExtra("uuid", uuid);
                        //返回的数据
                        intent.putExtra("data", response);
                        //用户数据传递
                        intent.putExtra("user", response.getUser());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            getAppContext().startForegroundService(intent);
                        } else {
                            //开启服务
                            getAppContext().startService(intent);
                        }
                        //设置登录的回调
                        if(callback!=null)
                        HolderLoginCallback.getInstance().addLoginCallBack(uuid, callback);

                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if(callback!=null)
                        callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NETERROR));
                    }
                }, null);
    }


    //创建会话
    public void createSingleSession(final String userTwo, final FlappyIMCallback<FlappyChatSession> callback) {
        if(DataManager.getInstance().getLoginUser()==null){
            if(callback!=null)
            callback.failure(new Exception("当前用户未登录"),Integer.parseInt(RESULT_NOTLOGIN));
            return;
        }
        //判断是否为空
        if (StringTool.isEmpty(userTwo)) {
            throw new RuntimeException("账户ID不能为空");
        }
        //创建extendid
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
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().createSingleSession, hashMap, new BaseParseCallback<SessionData>(SessionData.class) {
            @Override
            protected void stateFalse(BaseApiModel model, String tag) {
                //失败
                if(callback!=null)
                callback.failure(new Exception(model.getResultMessage()), Integer.parseInt(model.getResultCode()));
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSONERROR));
            }

            @Override
            public void stateTrue(SessionData data, String tag) {
                FlappyChatSession session = new FlappyChatSession();
                session.setSession(data);
                if(callback!=null)
                callback.success(session);
            }

            @Override
            protected void netError(Exception e, String tag) {
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NETERROR));
            }
        }, null);
    }


    //获取单聊会话
    public void getSingleSession(final String userTwo, final FlappyIMCallback<FlappyChatSession> callback) {
        if(DataManager.getInstance().getLoginUser()==null){
            if(callback!=null)
            callback.failure(new Exception("当前用户未登录"),Integer.parseInt(RESULT_NOTLOGIN));
            return;
        }
        //判断是否为空
        if (StringTool.isEmpty(userTwo)) {
            throw new RuntimeException("账户ID不能为空");
        }
        //创建extendid
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
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().getSingleSession, hashMap, new BaseParseCallback<SessionData>(SessionData.class) {
            @Override
            protected void stateFalse(BaseApiModel model, String tag) {
                //失败
                if(callback!=null)
                callback.failure(new Exception(model.getResultMessage()), Integer.parseInt(model.getResultCode()));
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSONERROR));
            }

            @Override
            public void stateTrue(SessionData data, String tag) {
                FlappyChatSession session = new FlappyChatSession();
                session.setSession(data);
                if(callback!=null)
                callback.success(session);
            }

            @Override
            protected void netError(Exception e, String tag) {
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NETERROR));
            }
        }, null);

    }


    //创建群组会话
    public void createGroupSession(List<String> users,
                                   String groupID,
                                   String groupName,
                                   final FlappyIMCallback<FlappyChatSession> callback) {

        if(DataManager.getInstance().getLoginUser()==null){
            if(callback!=null)
            callback.failure(new Exception("当前用户未登录"),Integer.parseInt(RESULT_NOTLOGIN));
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID
        hashMap.put("users", GsonTool.jsonArrayListStr(users));
        //外部用户ID
        hashMap.put("createUser", DataManager.getInstance().getLoginUser().getUserId());
        //外部的群组ID
        hashMap.put("extendID", groupID);
        //外部会话的名称
        hashMap.put("sessionName", groupName);

        //调用
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().createGroupSession, hashMap, new BaseParseCallback<SessionData>(SessionData.class) {
            @Override
            protected void stateFalse(BaseApiModel model, String tag) {
                //失败
                if(callback!=null)
                callback.failure(new Exception(model.getResultMessage()), Integer.parseInt(model.getResultCode()));
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSONERROR));
            }

            @Override
            public void stateTrue(SessionData data, String tag) {
                FlappyChatSession session = new FlappyChatSession();
                session.setSession(data);
                if(callback!=null)
                callback.success(session);
            }

            @Override
            protected void netError(Exception e, String tag) {
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NETERROR));
            }
        }, null);
    }


    //获取群组的会话
    public void getSessionByID(String groupID,
                               final FlappyIMCallback<FlappyChatSession> callback) {

        if(DataManager.getInstance().getLoginUser()==null){
            if(callback!=null)
            callback.failure(new Exception("当前用户未登录"),Integer.parseInt(RESULT_NOTLOGIN));
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID
        hashMap.put("extendID", groupID);
        //调用
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().getSessionByID, hashMap, new BaseParseCallback<SessionData>(SessionData.class) {
            @Override
            protected void stateFalse(BaseApiModel model, String tag) {
                //失败
                if(callback!=null)
                callback.failure(new Exception(model.getResultMessage()), Integer.parseInt(model.getResultCode()));
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSONERROR));
            }

            @Override
            public void stateTrue(SessionData data, String tag) {

                FlappyChatSession session = new FlappyChatSession();
                session.setSession(data);
                if(callback!=null)
                callback.success(session);
            }

            @Override
            protected void netError(Exception e, String tag) {
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NETERROR));
            }
        }, null);
    }


    //通过用户ID获取session
    public void getUserSessions(final FlappyIMCallback<List<ChatSession>> callback) {

        if(DataManager.getInstance().getLoginUser()==null){
            if(callback!=null)
            callback.failure(new Exception("当前用户未登录"),Integer.parseInt(RESULT_NOTLOGIN));
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID
        hashMap.put("userExtendID", DataManager.getInstance().getLoginUser().getUserExtendId());
        //调用
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().getUserSessions, hashMap, new BaseListParseCallBack<ChatSession>(ChatSession.class) {

            @Override
            public void stateFalse(String message, String tag) {
                //失败
                if(callback!=null)
                callback.failure(new Exception(message), Integer.parseInt(tag));
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSONERROR));
            }

            @Override
            public void stateTrue(List<ChatSession> data, String tag) {
                if(callback!=null)
                callback.success(data);
            }

            @Override
            protected void netError(Exception e, String tag) {
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NETERROR));
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

        if(DataManager.getInstance().getLoginUser()==null){
            if(callback!=null)
            callback.failure(new Exception("当前用户未登录"),Integer.parseInt(RESULT_NOTLOGIN));
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID
        hashMap.put("userID", userID);
        //群组的ID
        hashMap.put("extendID", groupID);
        //调用
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().addUserToSession, hashMap, new BaseParseCallback<String>(String.class) {
            @Override
            protected void stateFalse(BaseApiModel model, String tag) {
                //失败
                if(callback!=null)
                callback.failure(new Exception(model.getResultMessage()), Integer.parseInt(model.getResultCode()));
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSONERROR));
            }

            @Override
            public void stateTrue(String data, String tag) {
                if(callback!=null)
                callback.success(data);
            }

            @Override
            protected void netError(Exception e, String tag) {
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NETERROR));
            }
        }, null);
    }


    //群组中
    public void delUserInSession(
            String userID,
            String groupID,
            final FlappyIMCallback<String> callback) {

        if(DataManager.getInstance().getLoginUser()==null){
            if(callback!=null)
            callback.failure(new Exception("当前用户未登录"),Integer.parseInt(RESULT_NOTLOGIN));
            return;
        }

        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID
        hashMap.put("userID", userID);
        //群组的ID
        hashMap.put("extendID", groupID);
        //调用
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().delUserInSession, hashMap, new BaseParseCallback<String>(String.class) {
            @Override
            protected void stateFalse(BaseApiModel model, String tag) {
                //失败
                if(callback!=null)
                callback.failure(new Exception(model.getResultMessage()), Integer.parseInt(model.getResultCode()));
            }

            @Override
            protected void jsonError(Exception e, String tag) {
                //解析失败
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSONERROR));
            }

            @Override
            public void stateTrue(String data, String tag) {
                if(callback!=null)
                callback.success(data);
            }

            @Override
            protected void netError(Exception e, String tag) {
                if(callback!=null)
                callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NETERROR));
            }
        }, null);
    }


    //注销当前的登录
    public void logout(final FlappyIMCallback<String> callback) {

        if(DataManager.getInstance().getLoginUser()==null){
            if(callback!=null)
            callback.failure(new Exception("当前用户未登录"),Integer.parseInt(RESULT_NOTLOGIN));
            return;
        }

        //先关闭当前的长连接
        if (FlappyService.getInstance() != null) {
            //关闭长连接下线
            FlappyService.getInstance().offline();
        }
        //创建这个HashMap
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        //用户ID不用传了
        hashMap.put("userID", "");
        //外部用户ID
        hashMap.put("userExtendID", DataManager.getInstance().getLoginUser().getUserExtendId());
        //设备ID
        hashMap.put("device", BaseConfig.device);
        //设备ID
        hashMap.put("pushid", StringTool.getDeviceUnicNumber(getAppContext()));

        //进行callBack
        LXHttpClient.getInstacne().postParam(BaseConfig.getInstance().logout,
                hashMap,
                new BaseParseCallback<String>(String.class) {
                    @Override
                    protected void stateFalse(BaseApiModel model, String tag) {
                        //失败
                        if(callback!=null)
                        callback.failure(new Exception(model.getResultMessage()), Integer.parseInt(model.getResultCode()));
                    }

                    @Override
                    protected void jsonError(Exception e, String tag) {
                        //解析失败
                        if(callback!=null)
                        callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_JSONERROR));
                    }

                    @Override
                    public void stateTrue(String response, String tag) {
                        //清空当期的用户数据信息
                        DataManager.getInstance().clearUser();
                        //退出登录成功
                        if(callback!=null)
                        callback.success(response);
                    }

                    @Override
                    protected void netError(Exception e, String tag) {
                        if(callback!=null)
                        callback.failure(e, Integer.parseInt(FlappyIMCode.RESULT_NETERROR));
                    }
                }, null);
    }


    //添加全局的监听
    public void addGloableMessageListener(MessageListener listener) {
        HolderMessageRecieve.getInstance().addGloableMessageListener(listener);
    }

    //移除全局的监听
    public void removeGloableMessageListener(MessageListener listener) {
        HolderMessageRecieve.getInstance().removeGloableMessageListener(listener);
    }

    //设置被踢下线的监听
    public void setKnickedOutListener(KnickedOutListener listener) {
        FlappyService.setKnickedOutListener(listener);
    }

    //判断当前是否是登录的状态
    public boolean isLogin() {
        //存在账号
        ChatUser user = DataManager.getInstance().getLoginUser();
        //为空或者被踢下线
        if (user == null || user.isLogin() == false) {
            return false;
        }
        return true;
    }

}
