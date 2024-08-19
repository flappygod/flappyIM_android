package com.flappygo.flappyim.Handler;

import static com.flappygo.flappyim.Holder.HolderMessageSession.globalMsgTag;

import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Listener.MessageListener;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.os.Looper;

import java.util.List;
import java.util.Map;

public class HandlerMessage extends Handler {

    //消息发送失败
    public static final int MSG_FAILED = -1;

    //消息开始发送
    public static final int MSG_SENDING = 0;

    //收到新的消息了
    public static final int MSG_RECEIVE = 1;

    //消息列表接收到了
    public static final int MSG_RECEIVE_LIST = 2;

    //消息的状态更新
    public static final int MSG_UPDATE = 3;

    //对方消息已读
    public static final int MSG_READ_OTHER = 4;

    //自身消息已读
    public static final int MSG_READ_SELF = 5;

    //消息删除
    public static final int MSG_DELETE = 6;

    //Handle message
    public HandlerMessage() {
        super(Looper.getMainLooper());
    }


    //执行消息
    @SuppressWarnings("unchecked")
    public void handleMessage(Message message) {
        if (message.obj == null) {
            return;
        }
        //消息被创建
        if (message.what == MSG_SENDING) {
            //获取消息和监听器映射
            ChatMessage chatMessage = (ChatMessage) message.obj;
            Map<String, List<MessageListener>> msgListeners = HolderMessageSession.getInstance().getMsgListeners();
            // 遍历监听器映射的键
            for (Map.Entry<String, List<MessageListener>> entry : msgListeners.entrySet()) {
                String key = entry.getKey();
                List<MessageListener> messageListeners = entry.getValue();
                //检查消息会话ID是否匹配或全局消息标签是否匹配
                if (messageListeners != null && (chatMessage.getMessageSessionId().equals(key) || key.equals(globalMsgTag))) {
                    for (MessageListener listener : messageListeners) {
                        listener.messageSend(chatMessage);
                    }
                }
            }
        }
        //消息接收
        if (message.what == MSG_FAILED) {
            //获取消息和监听器映射
            ChatMessage chatMessage = (ChatMessage) message.obj;
            Map<String, List<MessageListener>> msgListeners = HolderMessageSession.getInstance().getMsgListeners();
            //遍历监听器映射的键
            for (Map.Entry<String, List<MessageListener>> entry : msgListeners.entrySet()) {
                String key = entry.getKey();
                List<MessageListener> messageListeners = entry.getValue();
                if (messageListeners != null && (chatMessage.getMessageSessionId().equals(key) || key.equals(globalMsgTag))) {
                    for (MessageListener listener : messageListeners) {
                        listener.messageFailed(chatMessage);
                    }
                }
            }
        }
        //消息更新
        if (message.what == MSG_UPDATE) {
            //获取消息和监听器映射
            ChatMessage chatMessage = (ChatMessage) message.obj;
            Map<String, List<MessageListener>> msgListeners = HolderMessageSession.getInstance().getMsgListeners();
            //遍历监听器映射的键
            for (Map.Entry<String, List<MessageListener>> entry : msgListeners.entrySet()) {
                String key = entry.getKey();
                List<MessageListener> messageListeners = entry.getValue();
                //检查消息会话 ID 是否匹配或全局消息标签是否匹配，并且监听器列表不为空
                if (messageListeners != null && (chatMessage.getMessageSessionId().equals(key) || key.equals(globalMsgTag))) {
                    //通知所有监听器
                    for (MessageListener listener : messageListeners) {
                        listener.messageUpdate(chatMessage);
                    }
                }
            }
        }
        //消息接收
        if (message.what == MSG_RECEIVE_LIST) {
            //消息列表
            List<ChatMessage> chatMessageList = (List<ChatMessage>) message.obj;
            Map<String, List<MessageListener>> msgListeners = HolderMessageSession.getInstance().getMsgListeners();
            for (Map.Entry<String, List<MessageListener>> entry : msgListeners.entrySet()) {
                String key = entry.getKey();
                List<MessageListener> messageListeners = entry.getValue();
                //全局
                if (key.equals(globalMsgTag)) {
                    for (MessageListener listener : messageListeners) {
                        listener.messageListReceived(chatMessageList);
                    }
                }
                //会话
                else {
                    List<ChatMessage> sessionMessageList = new ArrayList<>();
                    for (ChatMessage msg : chatMessageList) {
                        if (msg.getMessageSessionId().equals(key)) {
                            sessionMessageList.add(msg);
                        }
                    }
                    if (messageListeners != null && !sessionMessageList.isEmpty()) {
                        for (MessageListener listener : messageListeners) {
                            listener.messageListReceived(sessionMessageList);
                        }
                    }
                }
            }
        }
        //消息接收
        if (message.what == MSG_RECEIVE) {
            // 获取消息和监听器映射
            ChatMessage chatMessage = (ChatMessage) message.obj;
            Map<String, List<MessageListener>> msgListeners = HolderMessageSession.getInstance().getMsgListeners();
            // 遍历监听器映射的键
            for (Map.Entry<String, List<MessageListener>> entry : msgListeners.entrySet()) {
                String key = entry.getKey();
                List<MessageListener> messageListeners = entry.getValue();
                // 检查消息会话 ID 是否匹配或全局消息标签是否匹配，并且监听器列表不为空
                if ((chatMessage.getMessageSessionId().equals(key) || key.equals(globalMsgTag)) && messageListeners != null) {
                    for (MessageListener listener : messageListeners) {
                        listener.messageReceived(chatMessage);
                    }
                }
            }
        }
        //消息删除
        if (message.what == MSG_DELETE) {
            //获取消息和监听器映射
            ChatMessage chatMessage = (ChatMessage) message.obj;
            Map<String, List<MessageListener>> msgListeners = HolderMessageSession.getInstance().getMsgListeners();
            //遍历监听器映射的键
            for (Map.Entry<String, List<MessageListener>> entry : msgListeners.entrySet()) {
                String key = entry.getKey();
                List<MessageListener> messageListeners = entry.getValue();
                //检查消息会话 ID 是否匹配或全局消息标签是否匹配，并且监听器列表不为空
                if (messageListeners != null && (chatMessage.getMessageSessionId().equals(key) || key.equals(globalMsgTag))) {
                    //使用传统的 for 循环通知所有监听器
                    for (int i = 0; i < messageListeners.size(); i++) {
                        messageListeners.get(i).messageDelete(chatMessage);
                    }
                }
            }
        }
        //对方消息已读
        if (message.what == MSG_READ_OTHER) {
            List<String> chatMessage = (List<String>) message.obj;
            String sessionId = chatMessage.get(0);
            String readerId = chatMessage.get(1);
            String messageTableOffset = chatMessage.get(2);
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                if (sessionId.equals(key) || key.equals(globalMsgTag)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageReadOther(sessionId, readerId, messageTableOffset);
                    }
                }
            }
        }
        //自身消息已读
        if (message.what == MSG_READ_SELF) {
            List<String> chatMessage = (List<String>) message.obj;
            String sessionId = chatMessage.get(0);
            String readerId = chatMessage.get(1);
            String messageTableOffset = chatMessage.get(2);
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                if (sessionId.equals(key) || key.equals(globalMsgTag)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageReadSelf(sessionId, readerId, messageTableOffset);
                    }
                }
            }
        }
    }

}
