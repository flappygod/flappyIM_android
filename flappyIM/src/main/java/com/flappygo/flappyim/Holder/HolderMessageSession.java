package com.flappygo.flappyim.Holder;

import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Listener.SessionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//收到消息的
public class HolderMessageSession {


    //接收消息的监听列表
    private HashMap<String, List<MessageListener>> msgListeners = new HashMap<>();

    //会话更新的监听
    private List<SessionListener> sessionListeners = new ArrayList<>();

    //单例模式
    private static HolderMessageSession instacne;

    //单例manager
    public static HolderMessageSession getInstance() {
        if (instacne == null) {
            synchronized (HolderMessageSession.class) {
                if (instacne == null) {
                    instacne = new HolderMessageSession();
                }
            }
        }
        return instacne;
    }

    //监听
    public HashMap<String, List<MessageListener>> getMsgListeners() {
        return msgListeners;
    }

    //获取监听
    public List<SessionListener> getSessionListeners() {
        return sessionListeners;
    }

    //添加监听
    public void addSessionListener(SessionListener sessionListener) {
        if (!sessionListeners.contains(sessionListener)) {
            sessionListeners.add(sessionListener);
        }
    }

    //移除监听
    public boolean removeSessionListener(SessionListener sessionListener) {
        return sessionListeners.remove(sessionListener);
    }

    //添加总的监听
    public void addGloableMessageListener(MessageListener listener) {
        //获取统一的监听
        List<MessageListener> messageListeners = msgListeners.get("");
        //如果为空
        if (messageListeners == null) {
            //则创建
            messageListeners = new ArrayList<>();
            //并设置监听列表
            msgListeners.put("", messageListeners);
        }
        //当前的
        if (listener != null) {
            //不包含，添加
            if (!messageListeners.contains(listener)) {
                messageListeners.add(listener);
            }
        }
    }

    //添加总的监听
    public void removeGloableMessageListener(MessageListener listener) {
        //获取所有
        List<MessageListener> messageListeners = msgListeners.get("");
        //为空创建
        if (messageListeners == null) {
            //设置
            messageListeners = new ArrayList<>();
            //空的
            msgListeners.put("", messageListeners);
        }
        //监听移除
        if (listener != null) {
            messageListeners.remove(listener);
        }
    }

    //添加监听
    public void addMessageListener(MessageListener listener, String sessionID) {
        //session的监听
        List<MessageListener> messageListeners = msgListeners.get(sessionID);
        //为空创建
        if (messageListeners == null) {
            //创建
            messageListeners = new ArrayList<>();
            //添加
            msgListeners.put(sessionID, messageListeners);
        }
        //不为空
        if (listener != null) {
            if(!messageListeners.contains(listener)){
                messageListeners.add(listener);
            }
        }
    }

    //移除某个监听
    public void removeMessageListener(MessageListener listener, String sessionID) {
        //移除监听
        List<MessageListener> messageListeners = msgListeners.get(sessionID);
        //监听
        if (messageListeners == null) {
            //监听
            messageListeners = new ArrayList<>();
            //监听
            msgListeners.put(sessionID, messageListeners);
        }
        //移除
        if (listener != null) {
            messageListeners.remove(listener);
        }
    }

}
