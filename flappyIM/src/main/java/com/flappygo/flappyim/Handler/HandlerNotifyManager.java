package com.flappygo.flappyim.Handler;


import com.flappygo.flappyim.DataBase.Models.SessionModel;
import com.flappygo.flappyim.Models.Request.ChatAction;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.DataBase.Database;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

import android.os.Message;

import java.util.Objects;

import java.util.Arrays;
import java.util.List;

/******
 * 消息管理器
 */
public class HandlerNotifyManager {

    //单例holder
    private static final class InstanceHolder {
        static final HandlerNotifyManager instance = new HandlerNotifyManager();
    }

    //获取单例
    public static HandlerNotifyManager getInstance() {
        return HandlerNotifyManager.InstanceHolder.instance;
    }

    //用于加锁
    private final Byte[] lock = new Byte[1];

    //消息的handler
    private final HandlerMessage handlerMessage = new HandlerMessage();

    //会话的handler
    private final HandlerSession handlerSession = new HandlerSession();

    //回调
    private final ConcurrentHashMap<String, HandlerSendCall> sendCallBackHandlers = new ConcurrentHashMap<>();

    //获取Handler Message
    public HandlerMessage getHandlerMessage() {
        return handlerMessage;
    }

    //获取Handler session
    public HandlerSession getHandlerSession() {
        return handlerSession;
    }

    /******
     * 消息发送回调
     * @param messageID 消息ID
     * @return HandlerSendCall
     */
    public synchronized HandlerSendCall getSendCallbackHandler(String messageID) {
        synchronized (lock) {
            return sendCallBackHandlers.get(messageID);
        }
    }

    /******
     * 添加消息发送回调
     * @param messageID 消息ID
     * @param call      回调
     */
    public void addSendCallbackHandler(String messageID, HandlerSendCall call) {
        synchronized (lock) {
            sendCallBackHandlers.put(messageID, call);
        }
    }

    /******
     * 获取所有没发送的消息列表
     * @return 当前缓存的所有没有发送成功的消息
     */
    public List<ChatMessage> getUnSendCallbackHandlers() {
        synchronized (lock) {
            List<ChatMessage> retList = new ArrayList<>();
            for (String key : sendCallBackHandlers.keySet()) {
                retList.add(Objects.requireNonNull(sendCallBackHandlers.get(key)).getChatMessage());
            }
            return retList;
        }
    }

    /******
     * 消息发送成功
     * @param chatMessage 消息
     */
    public synchronized void handleSendSuccessCallback(ChatMessage chatMessage) {
        synchronized (lock) {
            HandlerSendCall call = sendCallBackHandlers.get(chatMessage.getMessageId());
            if (call != null) {
                Message message = call.obtainMessage(HandlerSendCall.SEND_SUCCESS);
                message.obj = chatMessage;
                call.sendMessage(message);
                sendCallBackHandlers.remove(chatMessage.getMessageId());
            }
        }
    }

    /******
     * 单条失败
     * @param chatMessage 消息发送失败
     * @param ex 错误信息
     */
    public void handleSendFailureCallback(ChatMessage chatMessage, Exception ex) {
        synchronized (lock) {
            HandlerSendCall call = sendCallBackHandlers.get(chatMessage.getMessageId());
            if (call != null) {
                Message message = call.obtainMessage(HandlerSendCall.SEND_FAILURE);
                message.obj = ex;
                call.sendMessage(message);
                sendCallBackHandlers.remove(chatMessage.getMessageId());
            }
        }
    }

    /******
     * 全部失败
     */
    public void handleSendFailureAllCallback() {
        synchronized (lock) {
            for (String key : sendCallBackHandlers.keySet()) {
                HandlerSendCall call = sendCallBackHandlers.get(key);
                if (call != null) {
                    Message message = call.obtainMessage(HandlerSendCall.SEND_FAILURE);
                    message.obj = new Exception("消息通道已关闭");
                    call.sendMessage(message);
                    sendCallBackHandlers.remove(call.getChatMessage().getMessageId());
                }
            }
            sendCallBackHandlers.clear();
        }
    }

    /******
     * 消息已读回执,对方的阅读消息存在的时候才会执行
     * @param chatMessage 消息
     */
    public void handleMessageAction(ChatMessage chatMessage) {
        //动作消息处理
        if (chatMessage.getMessageType().intValue() == ChatMessage.MSG_TYPE_ACTION) {
            //执行数据库更新操作
            Database.getInstance().handleActionMessageUpdate(chatMessage);
            //获取对象
            ChatAction chatAction = chatMessage.getChatAction();
            //操作类型
            switch (chatAction.getActionType()) {
                ///插入的情况下，代表新增，进行通知
                case ChatMessage.ACTION_TYPE_DELETE_MSG:
                case ChatMessage.ACTION_TYPE_RECALL_MSG: {
                    Message msg = new Message();
                    msg.what = HandlerMessage.MSG_DELETE;
                    msg.obj = Database.getInstance().getMessageById(chatAction.getActionIds().get(2));
                    handlerMessage.sendMessage(msg);
                    break;
                }
                ///插入的情况下，代表已读，进行通知
                case ChatMessage.ACTION_TYPE_READ_SESSION: {
                    //自身已读
                    if (DataManager.getInstance().getLoginUser().getUserId().equals(chatAction.getActionIds().get(0))) {
                        Message msg = new Message();
                        msg.what = HandlerMessage.MSG_READ_SELF;
                        msg.obj = new ArrayList<>(Arrays.asList(
                                chatAction.getActionIds().get(1),
                                chatAction.getActionIds().get(0),
                                chatAction.getActionIds().get(2)
                        ));
                        handlerMessage.sendMessage(msg);
                    }
                    //对方已读
                    else {
                        Message msg = new Message();
                        msg.what = HandlerMessage.MSG_READ_OTHER;
                        msg.obj = new ArrayList<>(Arrays.asList(
                                chatAction.getActionIds().get(1),
                                chatAction.getActionIds().get(0),
                                chatAction.getActionIds().get(2)
                        ));
                        handlerMessage.sendMessage(msg);
                    }
                    break;
                }
                ///会话用户更新了
                case ChatMessage.ACTION_TYPE_MUTE_SESSION:
                case ChatMessage.ACTION_TYPE_PINNED_SESSION:
                    Message msg = new Message();
                    msg.what = HandlerSession.SESSION_UPDATE;
                    msg.obj = Database.getInstance().getUserSessionById(chatAction.getActionIds().get(1));
                    handlerSession.sendMessage(msg);
            }
        }
    }

    /******
     * 消息发送监听
     * @param chatMessage 消息
     */
    public void notifyMessageSendInsert(ChatMessage chatMessage) {
        Message msg = new Message();
        msg.what = HandlerMessage.MSG_SENDING;
        msg.obj = chatMessage;
        this.handlerMessage.sendMessage(msg);
    }

    /******
     * 消息接收监听
     * @param chatMessage   消息
     * @param formerMessage 之前的消息
     */
    public void notifyMessageReceive(ChatMessage chatMessage, ChatMessage formerMessage) {
        Message msg = new Message();
        msg.what = (formerMessage == null) ? HandlerMessage.MSG_RECEIVE : HandlerMessage.MSG_UPDATE;
        msg.obj = chatMessage;
        this.handlerMessage.sendMessage(msg);
    }

    /******
     * 消息错误监听
     * @param chatMessage 消息
     */
    public void notifyMessageFailure(ChatMessage chatMessage) {
        Message msg = new Message();
        msg.what = HandlerMessage.MSG_FAILED;
        msg.obj = chatMessage;
        this.handlerMessage.sendMessage(msg);
    }

    /******
     * 消息删除的监听
     * @param chatMessage 消息
     */
    public void notifyMessageDelete(ChatMessage chatMessage) {
        Message msg = new Message();
        msg.what = HandlerMessage.MSG_DELETE;
        msg.obj = chatMessage;
        this.handlerMessage.sendMessage(msg);
    }

    /******
     * 通知会话有更新
     * @param session 会话
     */
    public void notifySessionUpdate(SessionModel session) {
        Message msg = new Message();
        msg.what = HandlerSession.SESSION_UPDATE;
        msg.obj = session;
        handlerSession.sendMessage(msg);
    }

    /******
     * 通知会话列表有更新
     * @param sessionList 会话列表
     */
    public void notifySessionListUpdate(List<SessionModel> sessionList) {
        for (SessionModel session : sessionList) {
            notifySessionUpdate(session);
        }
    }

}
