package com.flappygo.flappyim.Handler;

import static com.flappygo.flappyim.Holder.HolderMessageSession.globalMsgTag;

import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Listener.MessageListener;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

public class HandlerMessage extends Handler {

    //消息发送失败
    public static final int MSG_FAILED = 9;

    //消息开始发送
    public static final int MSG_SEND = 0;

    //收到新的消息了
    public static final int MSG_RECEIVE = 1;

    //消息的状态更新
    public static final int MSG_UPDATE = 2;

    //消息已读
    public static final int MSG_READ = 3;

    //消息删除
    public static final int MSG_DELETE = 4;

    //Handle message
    public HandlerMessage() {
        super(Looper.getMainLooper());
    }

    //handle message
    public HandlerMessage(Looper looper) {
        super(looper);
    }

    //执行消息
    public void handleMessage(Message message) {
        //消息被创建
        if (message.what == MSG_SEND) {
            ChatMessage chatMessage = (ChatMessage) message.obj;
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                if (chatMessage.getMessageSession().equals(key)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageSend(chatMessage);
                    }
                }
                if (key.equals(globalMsgTag)) {
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
                if (chatMessage.getMessageSession().equals(key)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageFailed(chatMessage);
                    }
                }
                if (key.equals(globalMsgTag)) {
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
                if (chatMessage.getMessageSession().equals(key)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageUpdate(chatMessage);
                    }
                }
                if (key.equals(globalMsgTag)) {
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
                if (chatMessage.getMessageSession().equals(key)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageReceived(chatMessage);
                    }
                }
                if (key.equals(globalMsgTag)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageReceived(chatMessage);
                    }
                }
            }
        }
        //消息已读
        if (message.what == MSG_READ) {
            List<String> chatMessage = (List<String>) message.obj;
            String sessionId = chatMessage.get(0);
            String messageTableSeq = chatMessage.get(1);
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                if (sessionId.equals(key)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageRead(messageTableSeq);
                    }
                }
                if (key.equals(globalMsgTag)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageRead(messageTableSeq);
                    }
                }
            }
        }

        //消息删除
        if (message.what == MSG_DELETE) {
            List<String> chatMessage = (List<String>) message.obj;
            String sessionId = chatMessage.get(0);
            String messageId = chatMessage.get(1);
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                if (sessionId.equals(key)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageDelete(messageId);
                    }
                }
                if (key.equals(globalMsgTag)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageDelete(messageId);
                    }
                }
            }
        }
    }

}
