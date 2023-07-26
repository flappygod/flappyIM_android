package com.flappygo.flappyim.Handler;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NET_ERROR;

import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.Callback.FlappyIMCallback;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


//登陆的回调
public class HandlerLoginCallback extends Handler {


    //登录成功
    public final static int LOGIN_SUCCESS = 1;

    //登录失败
    public final static int LOGIN_FAILURE = 0;


    //真实的回调
    private FlappyIMCallback<ResponseLogin> callback;

    //登录信息
    private final ResponseLogin loginResponse;

    //获取返回callback
    public ResponseLogin getLoginResponse() {
        return loginResponse;
    }

    //返回
    public HandlerLoginCallback(FlappyIMCallback<ResponseLogin> callback,
                                ResponseLogin loginResponse) {
        super(Looper.getMainLooper());
        this.callback = callback;
        this.loginResponse = loginResponse;
    }


    //返回
    public HandlerLoginCallback(Looper looper,
                                FlappyIMCallback<ResponseLogin> callback,
                                ResponseLogin loginResponse) {
        super(looper);
        this.callback = callback;
        this.loginResponse = loginResponse;
    }


    //设置登录失败
    public void loginFailure(Exception exception) {
        Message message = this.obtainMessage(LOGIN_FAILURE, exception);
        sendMessage(message);
    }

    //设置登录成功
    public void loginSuccess() {
        Message message = this.obtainMessage(LOGIN_SUCCESS);
        sendMessage(message);
    }


    //处理消息
    public void handleMessage(Message message) {
        //登录成功
        if (message.what == LOGIN_SUCCESS) {
            if (callback != null) {
                callback.success(loginResponse);
                callback = null;
            }
        }
        //登录失败
        else if (message.what == LOGIN_FAILURE) {
            if (callback != null) {
                callback.failure((Exception) message.obj, Integer.parseInt(RESULT_NET_ERROR));
                callback = null;
            }
        }
    }
}
