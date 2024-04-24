package com.flappygo.flappyim.Config;

/******
 * 基础配置项
 */
public class FlappyConfig {

    //安卓设备
    public final String device = "Android";

    //12秒
    public final int IdleSeconds = 12;

    //自动登录间隔时间
    public final int autoLoginSpace = 1000 * 6;

    //自动重试netty次数
    public final int autoRetryNetty = 3;

    //当前是正式环境
    public String pushPlat = "Google";

    //链接的http服务器器的地址192.168.124.105
    public String serverUrl;

    //上传地址
    public String serverUploadUrl;

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
        setServerUrl("http://139.224.204.128", "139.224.204.128");
    }

    //上传文件的地址
    public String fileUpload() {
        return serverUploadUrl + "/upload/fileUpload";
    }

    //上传视频的地址
    public String videoUpload() {
        return serverUploadUrl + "/upload/videoUpload";
    }

    //创建账户
    public String register() {
        return serverUrl + "/api/register";
    }

    //更新账户
    public String updateUser() {
        return serverUrl + "/api/updateUser";
    }

    //登录接口的地址
    public String login() {
        return serverUrl + "/api/login";
    }

    //更新推送信息
    public String changePush() {
        return serverUrl + "/api/changePush";
    }

    //退出登录的接口地址
    public String logout() {
        return serverUrl + "/api/logout";
    }

    //自动登录
    public String autoLogin() {
        return serverUrl + "/api/autoLogin";
    }

    //创建单聊会话
    public String createSingleSession() {
        return serverUrl + "/api/createSingleSession";
    }

    //获取单聊会话
    public String getSingleSession() {
        return serverUrl + "/api/getSingleSession";
    }

    //创建群组会话
    public String createGroupSession() {
        return serverUrl + "/api/createGroupSession";
    }

    //获取群组会话
    public String getSessionByExtendID() {
        return serverUrl + "/api/getSessionByExtendID";
    }

    //获取用户的所有会话
    public String getUserSessions() {
        return serverUrl + "/api/getUserSessions";
    }

    //向会话中添加人员
    public String addUserToSession() {
        return serverUrl + "/api/addUserToSession";
    }

    //向会话中移除人员
    public String delUserInSession() {
        return serverUrl + "/api/delUserInSession";
    }

    /******
     * 服务器的地址
     * @param serverUrl       服务器地址
     * @param serverUploadUrl 上传文件地址
     */
    public void setServerUrl(String serverUrl, String serverUploadUrl) {
        //不能为空
        if (serverUrl == null || serverUploadUrl == null) {
            throw new RuntimeException("服务器地址不能为空");
        }
        //服务器地址
        this.serverUrl = serverUrl;

        //资源文件上传地址
        this.serverUploadUrl = serverUploadUrl;
    }

}
