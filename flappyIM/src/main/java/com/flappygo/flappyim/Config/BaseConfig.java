package com.flappygo.flappyim.Config;

import com.flappygo.flappyim.FlappyImService;

public class BaseConfig {


    //单例模式
    private static BaseConfig instacne;

    public static BaseConfig getInstance() {
        if (instacne == null) {
            synchronized (BaseConfig.class) {
                if (instacne == null) {
                    instacne = new BaseConfig();
                }
            }
        }
        return instacne;
    }

    //安卓设备
    public static String device = "android";


    //链接的http服务器器的地址192.168.124.105
    public String serverUrl = "http://192.168.124.105";
    //public String serverUrl = "http://49.234.106.91";

    //上传地址
    public String serverUploadUrl = serverUrl;

    //上传文件的地址
    public String uploadUrl = serverUploadUrl + "/upload/fileUpload";

    //创建账户
    public String register = serverUrl + "/api/register";

    //登录接口的地址
    public String login = serverUrl + "/api/login";

    //退出登录的接口地址
    public String logout = serverUrl + "/api/logout";

    //自动登录
    public String autoLogin = serverUrl + "/api/autoLogin";

    //创建单聊会话
    public String createSingleSession = serverUrl + "/api/createSingleSession";

    //获取单聊会话
    public String getSingleSession = serverUrl + "/api/getSingleSession";

    //创建群组会话
    public String createGroupSession = serverUrl + "/api/createGroupSession";

    //获取群组会话
    public String getSessionByID = serverUrl + "/api/getSessionByID";

    //获取用户的所有会话
    public String getUserSessions = serverUrl + "/api/getUserSessions";

    //向会话中添加人员
    public String addUserToSession = serverUrl + "/api/addUserToSession";

    //向会话中移除人员
    public String delUserInSession = serverUrl + "/api/delUserInSession";


    //服务器的地址
    public void setServerUrl(String serverUrl, String serverUploadUrl) {

        if (serverUrl == null||serverUploadUrl==null) {
            throw new RuntimeException("服务器地址不能为空");
        }

        //服务器地址
        this.serverUrl = serverUrl;
        //资源文件上传地址
        this.serverUploadUrl = serverUploadUrl;


        //上传文件的地址
        uploadUrl = serverUploadUrl + "/upload/fileUpload";

        //创建账户
        register = serverUrl + "/api/register";

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
        getSessionByID = serverUrl + "/api/getSessionByID";

        //获取用户的所有会话
        getUserSessions = serverUrl + "/api/getUserSessions";

        //向会话中添加人员
        addUserToSession = serverUrl + "/api/addUserToSession";

        //向会话中移除人员
        delUserInSession = serverUrl + "/api/delUserInSession";

    }
}
