package com.flappygo.flappyim.Handler;

import android.os.Handler;
import android.os.Message;

import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.Models.Response.ResponseLogin;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_NETERROR;

//登陆的回调
public class HandlerLoginCallback extends Handler {


    //登录成功
    public final static int LOGIN_SUCCESS = 1;

    //登录失败
    public final static int LOGIN_FAILURE = 0;


    //真实的回调
    private FlappyIMCallback callback;

    //登录信息
    private ResponseLogin loginResponse;

    //返回
    public HandlerLoginCallback(FlappyIMCallback callback, ResponseLogin loginResponse) {
        this.callback = callback;
        this.loginResponse = loginResponse;
    }


    public void handleMessage(Message message) {
        //成功
        if (message.what == LOGIN_SUCCESS) {
            //成功
            if (callback != null) {
                //执行成功的回调
                callback.success(loginResponse);
                //只执行一次
                callback = null;
            }
        }
        //失败
        else if (message.what == LOGIN_FAILURE) {
            //失败
            if (callback != null) {
                //执行失败的回调
                callback.failure((Exception) message.obj, Integer.parseInt(RESULT_NETERROR));
                //同样只执行一次
                callback = null;
            }
        }
    }
}
