package com.flappygo.flappyim.Config;

public class BaseConfig {

    //安卓设备
    public final static String device = "android";




    //链接的http服务器器的地址192.168.124.105
    //public final static String serverUrl = "http://192.168.124.105";
    public final static String serverUrl = "http://49.234.106.91";

    //上传文件的地址
    public final static String uploadUrl=serverUrl+"/upload/fileUpload";

    //创建账户
    public final static String register = serverUrl + "/api/register";

    //登录接口的地址
    public final static String login = serverUrl + "/api/login";

    //退出登录的接口地址
    public final static String logout = serverUrl + "/api/logout";

    //自动登录
    public final static String autoLogin = serverUrl + "/api/autoLogin";

    //创建会话
    public final static String createSession = serverUrl + "/api/createSession";


    //创建群组会话
    public final static String createGroupSession = serverUrl + "/api/createGroupSession";

    //获取群组会话
    public final static String getGroupSession = serverUrl + "/api/getGroupSession";

    //向会话中添加人员
    public final static String addUserToSession = serverUrl + "/api/addUserToSession";

    //向会话中移除人员
    public final static String delUserInSession = serverUrl + "/api/delUserInSession";


    //发送消息
    public final static String sendMessage= serverUrl + "/api/sendMessage";

}
