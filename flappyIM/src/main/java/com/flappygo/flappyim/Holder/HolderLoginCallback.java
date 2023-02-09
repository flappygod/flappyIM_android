package com.flappygo.flappyim.Holder;

import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.Models.Response.ResponseLogin;

import java.util.concurrent.ConcurrentHashMap;

public class HolderLoginCallback {

    //登录的回调
    private final ConcurrentHashMap<String, FlappyIMCallback<ResponseLogin>> loginCallbacks = new ConcurrentHashMap<>();

    //单例模式
    private static HolderLoginCallback instance;

    //单例manager
    public static HolderLoginCallback getInstance() {
        if (instance == null) {
            synchronized (HolderLoginCallback.class) {
                if (instance == null) {
                    instance = new HolderLoginCallback();
                }
            }
        }
        return instance;
    }

    //登录成功的回调
    public void addLoginCallBack(String uuid, FlappyIMCallback<ResponseLogin> callBack) {
        //保存当前的回调
        loginCallbacks.put(uuid, callBack);
    }

    //返回回调
    public FlappyIMCallback<ResponseLogin> getLoginCallBack(String uuid) {
        //获取之前加入的回调
        FlappyIMCallback<ResponseLogin> callback = loginCallbacks.get(uuid);
        //只执行一次，移除掉
        loginCallbacks.remove(uuid);
        //返回
        return callback;
    }


}
