package com.flappygo.flappyim.Models.Request.Base;


public class FlappyRequest {


    /*请求类型*/
    //登录消息
    public final static int REQ_LOGIN = 1;

    //请求消息
    public final static int REQ_MSG = 2;

    //心跳消息
    public final static int REQ_PING = 3;

    //消息回执
    public final static int REQ_RECEIPT = 4;

    //更新消息
    public final static int REQ_UPDATE = 5;


    /*回执类型*/
    //消息接收回执
    public final static int RECEIPT_MSG_ARRIVE = 1;

    //消息读取回执
    public final static int RECEIPT_MSG_READ = 2;


    /*更新类型*/
    //更新信息
    public final static int UPDATE_SESSION_SINGLE = 1;


}
