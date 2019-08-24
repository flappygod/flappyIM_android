package com.flappygo.flappyim.Callback;

//发送消息的返回信息
public interface FlappySendCallback<T> {

    //成功
    void success(T data);

    //失败
    void failure(T data, Exception ex, int code);

}
