package com.flappygo.flappyim.Callback;


//IM的回调
public interface FlappyIMCallback<T> {


    //成功
    void success(T data);

    //失败
    void failure(Exception ex, int code);

}
