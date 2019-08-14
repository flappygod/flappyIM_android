package com.flappygo.flappyim.Holder;

import com.flappygo.flappyim.Callback.FlappyIMCallback;

import java.util.concurrent.ConcurrentHashMap;

public class HolderLoginCallback {

    //登录的回调
    private ConcurrentHashMap<Long, FlappyIMCallback> loginCallbacks = new ConcurrentHashMap<>();

    //单例模式
    private static HolderLoginCallback instacne;

    //单例manager
    public static HolderLoginCallback getInstance() {
        if (instacne == null) {
            synchronized (HolderLoginCallback.class) {
                if (instacne == null) {
                    instacne = new HolderLoginCallback();
                }
            }
        }
        return instacne;
    }

    //登录成功的回调
    public void addLoginCallBack(long uuid, FlappyIMCallback callBack) {
        //保存当前的回调
        loginCallbacks.put(uuid, callBack);
    }

    //返回回调
    public FlappyIMCallback getLoginCallBack(long uuid) {
        //获取之前加入的回调
        FlappyIMCallback callback = loginCallbacks.get(uuid);
        //只执行一次，移除掉
        loginCallbacks.remove(uuid);
        //返回
        return callback;
    }


}
