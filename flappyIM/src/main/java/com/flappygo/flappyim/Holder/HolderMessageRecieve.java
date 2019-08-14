package com.flappygo.flappyim.Holder;

import com.flappygo.flappyim.Listener.MessageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//收到消息的
public class HolderMessageRecieve {


    //接收消息的监听列表
    private HashMap<String, List<MessageListener>> listeners = new HashMap<>();

    //单例模式
    private static HolderMessageRecieve instacne;

    //单例manager
    public static HolderMessageRecieve getInstance() {
        if (instacne == null) {
            synchronized (HolderMessageRecieve.class) {
                if (instacne == null) {
                    instacne = new HolderMessageRecieve();
                }
            }
        }
        return instacne;
    }

    //监听
    public HashMap<String, List<MessageListener>> getListeners() {
        return listeners;
    }

    //添加总的监听
    public void addGloableMessageListener(MessageListener listener) {
        //获取统一的监听
        List<MessageListener> messageListeners = listeners.get("");
        //如果为空
        if (messageListeners == null) {
            //则创建
            messageListeners = new ArrayList<>();
            //并设置监听列表
            listeners.put("", messageListeners);
        }
        //当前的
        if (listener != null) {
            messageListeners.add(listener);
        }
    }

    //添加总的监听
    public void removeGloableMessageListener(MessageListener listener) {
        //获取所有
        List<MessageListener> messageListeners = listeners.get("");
        //为空创建
        if (messageListeners == null) {
            //设置
            messageListeners = new ArrayList<>();
            //空的
            listeners.put("", messageListeners);
        }
        //监听移除
        if (listener != null) {
            messageListeners.remove(listener);
        }
    }

    //添加监听
    public void addMessageListener(MessageListener listener, String sessionID) {
        //session的监听
        List<MessageListener> messageListeners = listeners.get(sessionID);
        //为空创建
        if (messageListeners == null) {
            //创建
            messageListeners = new ArrayList<>();
            //添加
            listeners.put(sessionID, messageListeners);
        }
        //不为空
        if (listener != null) {
            messageListeners.add(listener);
        }
    }

    //移除某个监听
    public void removeMessageListener(MessageListener listener, String sessionID) {
        //移除监听
        List<MessageListener> messageListeners = listeners.get(sessionID);
        //监听
        if (messageListeners == null) {
            //监听
            messageListeners = new ArrayList<>();
            //监听
            listeners.put(sessionID, messageListeners);
        }
        //移除
        if (listener != null) {
            messageListeners.remove(listener);
        }
    }

}
