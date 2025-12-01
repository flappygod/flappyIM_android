package com.flappygo.flappyim.Datas;

/******
 * 错误码
 */
public class FlappyIMCode {

    //请求失败
    public final static int RESULT_FAILURE = 0;

    //请求成功
    public final static int RESULT_SUCCESS = 1;

    //已经被踢下线了
    public final static int RESULT_EXPIRED = 2;

    //已经被踢下线了
    public final static int RESULT_JSON_ERROR = 3;

    //已经被踢下线了
    public final static int RESULT_NET_ERROR = 4;

    //当前用户未登录
    public final static int RESULT_NOT_LOGIN = 5;

    //当前用户未登录
    public final static int RESULT_PARSE_ERROR = 6;

    //当前用户未登录
    public final static int RESULT_DATABASE_ERROR = 7;


    //会话用户被禁用
    public final static int RESULT_SESSION_MEMBER_UNABLE = 8;

    //会话被禁用
    public final static int RESULT_SESSION_UNABLE = 9;

    //会话被删除
    public final static int RESULT_SESSION_DELETED = 10;

}
