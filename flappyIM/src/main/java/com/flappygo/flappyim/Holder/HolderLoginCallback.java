package com.flappygo.flappyim.Holder;

import com.flappygo.flappyim.Models.Response.ResponseLogin;
import com.flappygo.flappyim.Callback.FlappyIMCallback;

import java.util.concurrent.ConcurrentHashMap;

/******
 * 登录回调Holder
 */
public class HolderLoginCallback {

    //单例模式
    private static final class InstanceHolder {
        static final HolderLoginCallback instance = new HolderLoginCallback();
    }

    //单例manager
    public static HolderLoginCallback getInstance() {
        return InstanceHolder.instance;
    }

    //登录的回调
    private final ConcurrentHashMap<String, FlappyIMCallback<ResponseLogin>> loginCallbacks = new ConcurrentHashMap<>();


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
