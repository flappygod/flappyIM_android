package com.flappygo.flappyim.Holder;

import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Listener.SessionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*****
 * 消息接收Holder
 */
public class HolderMessageSession {

    //全局消息tag
    public static String globalMsgTag = "globalMsgTag";

    //接收消息的监听列表
    private final HashMap<String, List<MessageListener>> msgListeners = new HashMap<>();

    //会话更新的监听
    private final List<SessionListener> sessionListeners = new ArrayList<>();

    //单例模式
    private static final class InstanceHolder {
        static final HolderMessageSession instance = new HolderMessageSession();
    }

    //单例manager
    public static HolderMessageSession getInstance() {
        return InstanceHolder.instance;
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
    public void removeSessionListener(SessionListener sessionListener) {
        sessionListeners.remove(sessionListener);
    }

    //添加总的监听
    public void addGlobalMessageListener(MessageListener listener) {
        //获取统一的监听
        List<MessageListener> messageListeners = msgListeners.computeIfAbsent(globalMsgTag, k -> new ArrayList<>());
        if (listener != null) {
            //不包含，添加
            if (!messageListeners.contains(listener)) {
                messageListeners.add(listener);
            }
        }
    }

    //添加总的监听
    public void removeGlobalMessageListener(MessageListener listener) {
        //获取所有
        List<MessageListener> messageListeners = msgListeners.computeIfAbsent(globalMsgTag, k -> new ArrayList<>());
        if (listener != null) {
            messageListeners.remove(listener);
        }
    }

    //添加监听
    public void addMessageListener(MessageListener listener, String sessionID) {
        //session的监听
        List<MessageListener> messageListeners = msgListeners.computeIfAbsent(sessionID, k -> new ArrayList<>());
        if (listener != null) {
            if (!messageListeners.contains(listener)) {
                messageListeners.add(listener);
            }
        }
    }

    //移除某个监听
    public void removeMessageListener(MessageListener listener, String sessionID) {
        //移除监听
        List<MessageListener> messageListeners = msgListeners.computeIfAbsent(sessionID, k -> new ArrayList<>());
        if (listener != null) {
            messageListeners.remove(listener);
        }
    }

}
