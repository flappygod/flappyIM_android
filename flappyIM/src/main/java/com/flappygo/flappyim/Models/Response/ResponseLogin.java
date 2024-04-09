package com.flappygo.flappyim.Models.Response;


import com.flappygo.flappyim.Models.Server.ChatRoute;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Session.FlappySessionData;

import java.io.Serializable;
import java.util.List;

//登录的返回消息
public class ResponseLogin implements Serializable {

    //构造器
    public ResponseLogin(String serverStr) {
        String[] str = serverStr.split(":");
        //服务器地址
        serverIP = str[0];
        //服务器端口
        serverPort = str[1];
        //注册到服务器上的topic
        serverTopic = str[2];
        //注册到服务器上的group id
        serverGroup = str[3];
    }

    //允许连接的服务器IP
    private String serverIP;

    //允许连接的服务器端口
    private String serverPort;

    //服务器使用的topic
    private String serverTopic;

    //服务器所属的组
    private String serverGroup;

    //用户信息
    private ChatUser user;

    //上个用户的route信息
    private ChatRoute route;

    //用户会话
    private List<FlappySessionData> sessions;


    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public ChatUser getUser() {
        return user;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

    public String getServerTopic() {
        return serverTopic;
    }

    public void setServerTopic(String serverTopic) {
        this.serverTopic = serverTopic;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup;
    }

    public ChatRoute getRoute() {
        return route;
    }

    public void setRoute(ChatRoute route) {
        this.route = route;
    }

    public List<FlappySessionData> getSessions() {
        return sessions;
    }

    public void setSessions(List<FlappySessionData> sessions) {
        this.sessions = sessions;
    }
}
