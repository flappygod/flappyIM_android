package com.flappygo.flappyim.Handler;

import static com.flappygo.flappyim.Holder.HolderMessageSession.globalMsgTag;

import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Listener.MessageListener;

import android.os.Handler;
import android.os.Message;
import android.os.Looper;

import java.util.List;

public class HandlerMessage extends Handler {

    //消息发送失败
    public static final int MSG_FAILED = 9;

    //消息开始发送
    public static final int MSG_SENDING = 0;

    //收到新的消息了
    public static final int MSG_RECEIVE = 1;

    //消息的状态更新
    public static final int MSG_UPDATE = 2;

    //对方消息已读
    public static final int MSG_READ_OTHER = 3;

    //自身消息已读
    public static final int MSG_READ_SELF = 4;

    //消息删除
    public static final int MSG_DELETE = 5;

    //Handle message
    public HandlerMessage() {
        super(Looper.getMainLooper());
    }


    //执行消息
    @SuppressWarnings("unchecked")
    public void handleMessage(Message message) {
        //消息被创建
        if (message.what == MSG_SENDING) {
            ChatMessage chatMessage = (ChatMessage) message.obj;
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                if (chatMessage.getMessageSession().equals(key) || key.equals(globalMsgTag)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageSend(chatMessage);
                    }
                }
            }
        }
        //消息接收
        if (message.what == MSG_FAILED) {
            ChatMessage chatMessage = (ChatMessage) message.obj;
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                if (chatMessage.getMessageSession().equals(key) || key.equals(globalMsgTag)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageFailed(chatMessage);
                    }
                }
            }
        }
        //消息更新
        if (message.what == MSG_UPDATE) {
            ChatMessage chatMessage = (ChatMessage) message.obj;
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                if (chatMessage.getMessageSession().equals(key) || key.equals(globalMsgTag)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageUpdate(chatMessage);
                    }
                }
            }
        }
        //消息接收
        if (message.what == MSG_RECEIVE) {
            ChatMessage chatMessage = (ChatMessage) message.obj;
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                if (chatMessage.getMessageSession().equals(key) || key.equals(globalMsgTag)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageReceived(chatMessage);
                    }
                }
            }
        }
        //消息删除
        if (message.what == MSG_DELETE) {
            ChatMessage chatMessage = (ChatMessage) message.obj;
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                if (chatMessage.getMessageSession().equals(key) || key.equals(globalMsgTag)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageDelete(chatMessage);
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
