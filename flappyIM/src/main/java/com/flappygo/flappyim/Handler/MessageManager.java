package com.flappygo.flappyim.Handler;

import android.os.Message;

import com.flappygo.flappyim.Models.Server.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/******
 * message manager
 */
public class MessageManager {

    //单例holder
    private static final class InstanceHolder {
        static final MessageManager instance = new MessageManager();
    }

    //获取单例
    public static MessageManager getInstance() {
        return MessageManager.InstanceHolder.instance;
    }

    //用于加锁
    private final Byte[] lock = new Byte[1];

    //消息的handler
    private final HandlerMessage handlerMessage = new HandlerMessage();

    //会话的handler
    private final HandlerSession handlerSession = new HandlerSession();

    //回调
    private final ConcurrentHashMap<String, HandlerSendCall> sendHandlers = new ConcurrentHashMap<>();

    public HandlerMessage getHandlerMessage() {
        return handlerMessage;
    }

    public HandlerSession getHandlerSession() {
        return handlerSession;
    }

    //消息发送回调
    public synchronized HandlerSendCall getHandlerSendCall(String messageID) {
        synchronized (lock) {
            return sendHandlers.get(messageID);
        }
    }

    //添加消息发送回调
    public void addHandlerSendCall(String messageID, HandlerSendCall call) {
        synchronized (lock) {
            sendHandlers.put(messageID, call);
        }
    }

    //成功
    public synchronized void messageSendSuccess(ChatMessage chatMessage) {
        synchronized (lock) {
            HandlerSendCall call = sendHandlers.get(chatMessage.getMessageId());
            if (call != null) {
                Message message = call.obtainMessage(HandlerSendCall.SEND_SUCCESS);
                message.obj = chatMessage;
                call.sendMessage(message);
                sendHandlers.remove(chatMessage.getMessageId());
            }
        }
    }

    //单条失败
    public void messageSendFailure(ChatMessage chatMessage, Exception ex) {
        synchronized (lock) {
            HandlerSendCall call = sendHandlers.get(chatMessage.getMessageId());
            if (call != null) {
                Message message = call.obtainMessage(HandlerSendCall.SEND_FAILURE);
                message.obj = ex;
                call.sendMessage(message);
                sendHandlers.remove(chatMessage.getMessageId());
                notifyMessageFailure(call.getChatMessage());
            }
        }
    }

    //全部失败
    public void messageSendFailureAll() {
        synchronized (lock) {
            for (String key : sendHandlers.keySet()) {
                HandlerSendCall call = sendHandlers.get(key);
                if (call != null) {
                    Message message = call.obtainMessage(HandlerSendCall.SEND_FAILURE);
                    message.obj = new Exception("消息通道已关闭");
                    call.sendMessage(message);
                    sendHandlers.remove(call.getChatMessage().getMessageId());
                    notifyMessageFailure(call.getChatMessage());
                }
            }
            sendHandlers.clear();
        }
    }


    //消息发送监听
    public void notifyMessageSend(ChatMessage chatMessage) {
        Message msg = new Message();
        msg.what = HandlerMessage.MSG_SEND;
        msg.obj = chatMessage;
        this.handlerMessage.sendMessage(msg);
    }

    //消息接收监听
    public void notifyMessageReceive(ChatMessage chatMessage, ChatMessage formerMessage) {
        Message msg = new Message();
        if (formerMessage == null) {
            msg.what = HandlerMessage.MSG_RECEIVE;
        } else {
            msg.what = HandlerMessage.MSG_UPDATE;
        }
        msg.obj = chatMessage;
        this.handlerMessage.sendMessage(msg);
    }

    //消息错误监听
    public void notifyMessageFailure(ChatMessage chatMessage) {
        Message msg = new Message();
        msg.what = HandlerMessage.MSG_FAILED;
        msg.obj = chatMessage;
        this.handlerMessage.sendMessage(msg);
    }

    //获取所有没发送的消息列表
    public List<ChatMessage> getAllUnSendMessages() {
        synchronized (lock) {
            List<ChatMessage> retList = new ArrayList<>();
            for (String key : sendHandlers.keySet()) {
                retList.add(sendHandlers.get(key).getChatMessage());
            }
            return retList;
        }
    }

}
