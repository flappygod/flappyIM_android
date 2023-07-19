package com.flappygo.flappyim.Config;

public class FlappyConfig {

    //安卓设备
    final public String device = "Android";

    //当前是正式环境
    public String pushPlat = "Google";

    //12秒
    final public int IdleSeconds = 12;

    //自动登录间隔时间
    final public int autoLoginSpace = 1000 * 6;

    //自动重试netty次数
    final public int autoRetryNetty = 3;

    //链接的http服务器器的地址192.168.124.105
    //public String serverUrl = "http://192.168.124.127";
    public String serverUrl = "http://49.234.106.91";

    //上传地址
    //public String serverUploadUrl = "http://192.168.124.127";
    public String serverUploadUrl = "http://49.234.106.91";

    //单例模式
    private static final class InstanceHolder {
        static final FlappyConfig instance = new FlappyConfig();
    }

    //单例模式
    public static FlappyConfig getInstance() {
        return InstanceHolder.instance;
    }

    //单例模式
    private FlappyConfig() {
        setServerUrl("http://49.234.106.91", "http://49.234.106.91");
    }


    //上传文件的地址
    public String fileUpload;

    //上传视频的地址
    public String videoUpload;

    //创建账户
    public String register;

    //更新账户
    public String updateUser;

    //登录接口的地址
    public String login;

    //退出登录的接口地址
    public String logout;

    //自动登录
    public String autoLogin;

    //创建单聊会话
    public String createSingleSession;

    //获取单聊会话
    public String getSingleSession;

    //创建群组会话
    public String createGroupSession;

    //获取群组会话
    public String getSessionByExtendID;

    //获取用户的所有会话
    public String getUserSessions;

    //向会话中添加人员
    public String addUserToSession;

    //向会话中移除人员
    public String delUserInSession;


    //服务器的地址
    public void setServerUrl(String serverUrl, String serverUploadUrl) {

        //不能为空
        if (serverUrl == null || serverUploadUrl == null) {
            throw new RuntimeException("服务器地址不能为空");
        }

        //服务器地址
        this.serverUrl = serverUrl;

        //资源文件上传地址
        this.serverUploadUrl = serverUploadUrl;

        //上传文件的地址
        fileUpload = serverUploadUrl + "/upload/fileUpload";

        //视频文件上传的地址
        videoUpload = serverUploadUrl + "/upload/videoUpload";

        //创建账户
        register = serverUrl + "/api/register";

        //update user
        updateUser = serverUrl + "/api/updateUser";

        //登录接口的地址
        login = serverUrl + "/api/login";

        //退出登录的接口地址
        logout = serverUrl + "/api/logout";

        //自动登录
        autoLogin = serverUrl + "/api/autoLogin";

        //创建单聊会话
        createSingleSession = serverUrl + "/api/createSingleSession";

        //获取单聊会话
        getSingleSession = serverUrl + "/api/getSingleSession";

        //创建群组会话
        createGroupSession = serverUrl + "/api/createGroupSession";

        //获取群组会话
        getSessionByExtendID = serverUrl + "/api/getSessionByID";

        //获取用户的所有会话
        getUserSessions = serverUrl + "/api/getUserSessions";

        //向会话中添加人员
        addUserToSession = serverUrl + "/api/addUserToSession";

        //向会话中移除人员
        delUserInSession = serverUrl + "/api/delUserInSession";

    }

}
