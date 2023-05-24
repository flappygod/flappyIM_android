package com.flappygo.flappyim.Handler;

import android.os.Handler;
import android.os.Message;

import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.Models.Response.ResponseLogin;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NET_ERROR;

//登陆的回调
public class HandlerLoginCallback extends Handler {


    //登录成功
    public final static int LOGIN_SUCCESS = 1;

    //登录失败
    public final static int LOGIN_FAILURE = 0;


    //真实的回调
    private FlappyIMCallback<ResponseLogin> callback;

    //登录信息
    private ResponseLogin loginResponse;


    public ResponseLogin getLoginResponse() {
        return loginResponse;
    }

    //返回
    public HandlerLoginCallback(FlappyIMCallback<ResponseLogin> callback, ResponseLogin loginResponse) {
        this.callback = callback;
        this.loginResponse = loginResponse;
    }


    public void handleMessage(Message message) {
        //成功
        if (message.what == LOGIN_SUCCESS) {
            if (callback != null) {
                callback.success(loginResponse);
                callback = null;
            }
        }
        //失败
        else if (message.what == LOGIN_FAILURE) {
            if (callback != null) {
                callback.failure((Exception) message.obj, Integer.parseInt(RESULT_NET_ERROR));
                callback = null;
            }
        }
    }
}
