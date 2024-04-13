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


    /***更新类型***/
    //什么也不做
    public final static int UPDATE_DO_NOTHING = 0;

    //更新单条会话(所有数据)
    public final static int UPDATE_SESSION_ALL = 1;

    //用户信息更新(获取)
    public final static int UPDATE_SESSION_MEMBER_GET = 2;

    //用户信息更新(删除)
    public final static int UPDATE_SESSION_MEMBER_DEL = 3;


}
