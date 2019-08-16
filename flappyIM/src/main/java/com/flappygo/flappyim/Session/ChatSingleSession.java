package com.flappygo.flappyim.Session;

import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Callback.FlappyIMCallback;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Holder.HolderMessageRecieve;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Models.Request.ChatImage;
import com.flappygo.flappyim.Models.Request.ChatVoice;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Tools.IDGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//单聊的会话
public class ChatSingleSession extends FlappyBaseSession {

    //保留的监听列表
    private List<MessageListener> listenerList = new ArrayList<>();


    //设置消息的监听,新收到消息都会在这里
    public void addMessageListener(MessageListener messageListener) {
        //添加监听
        HolderMessageRecieve.getInstance().addMessageListener(messageListener, session.getSessionId());
        listenerList.add(messageListener);
    }

    //移除当前会话的监听
    public void removeListener(MessageListener messageListener) {
        HolderMessageRecieve.getInstance().removeMessageListener(messageListener, session.getSessionId());
        listenerList.remove(messageListener);
    }

    //始终都要移除它，防止内存泄漏
    public void finalize() {
        close();
    }

    //session使用完成之后，请务必关闭防止内存泄漏
    public void close() {
        for (int s = 0; s < listenerList.size(); s++) {
            HolderMessageRecieve.getInstance().removeMessageListener(listenerList.get(s), session.getSessionId());
        }
        listenerList.clear();
    }

    //会话
    private SessionData session;


    public SessionData getSession() {
        return session;
    }

    public void setSession(SessionData session) {
        this.session = session;
    }


    private ChatUser getMine(){
        return DataManager.getInstance().getLoginUser();
    }

    private ChatUser getPeer(){
        for(int s=0;s<getSession().getUsers().size();s++){
            if(!getSession().getUsers().get(s).getUserId().equals(getMine().getUserId())){
                return getSession().getUsers().get(s);
            }
        }
        return null;
    }


    //发送消息
    public ChatMessage sendText(String text,
                                FlappyIMCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerator.generateCommomID());
        //设置
        msg.setMessageSession(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSend(getMine().getUserId());
        //发送者
        msg.setMessageSendExtendid(getMine().getUserExtendId());
        //接收者
        msg.setMessageRecieve(getPeer().getUserId());
        //接收者
        msg.setMessageSendExtendid(getPeer().getUserExtendId());
        //类型
        msg.setMessageType(new BigDecimal(ChatMessage.MSG_TYPE_TEXT));
        //设置内容
        msg.setMessageContent(text);
        //时间
        msg.setMessageDate(new Date());
        //插入数据
        insertMessage(msg);
        //发送消息
        sendMessage(msg, callback);
        //返回消息
        return msg;
    }

    //发送本地图片
    public ChatMessage sendLocalImage(String path,
                                      final FlappyIMCallback<ChatMessage> callback) {

        //创建消息
        final ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerator.generateCommomID());
        //设置
        msg.setMessageSession(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSend(getMine().getUserId());
        //发送者
        msg.setMessageSendExtendid(getMine().getUserExtendId());
        //接收者
        msg.setMessageRecieve(getPeer().getUserId());
        //接收者
        msg.setMessageSendExtendid(getPeer().getUserExtendId());
        //类型
        msg.setMessageType(new BigDecimal(ChatMessage.MSG_TYPE_IMG));
        //消息
        ChatImage chatImage = new ChatImage();
        //发送地址
        chatImage.setSendPath(path);
        //设置内容
        msg.setMessageContent(GsonTool.modelToString(chatImage, ChatImage.class));
        //时间
        msg.setMessageDate(new Date());
        //插入数据
        insertMessage(msg);
        //上传图片并发送信息
        uploadImageAndSend(msg, callback);

        return msg;

    }


    //发送图片
    public ChatMessage sendImage(ChatImage image,
                                 FlappyIMCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerator.generateCommomID());
        //设置
        msg.setMessageSession(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSend(getMine().getUserId());
        //发送者
        msg.setMessageSendExtendid(getMine().getUserExtendId());
        //接收者
        msg.setMessageRecieve(getPeer().getUserId());
        //接收者
        msg.setMessageSendExtendid(getPeer().getUserExtendId());
        //类型
        msg.setMessageType(new BigDecimal(ChatMessage.MSG_TYPE_IMG));
        //设置内容
        msg.setMessageContent(GsonTool.modelToString(image, ChatImage.class));
        //时间
        msg.setMessageDate(new Date());
        //插入数据
        insertMessage(msg);
        //发送消息
        sendMessage(msg, callback);
        return msg;
    }


    //发送本地的音频
    public ChatMessage sendLocalVoice(String path,
                                      final FlappyIMCallback<ChatMessage> callback) {


        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerator.generateCommomID());
        //设置
        msg.setMessageSession(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSend(getMine().getUserId());
        //发送者
        msg.setMessageSendExtendid(getMine().getUserExtendId());
        //接收者
        msg.setMessageRecieve(getPeer().getUserId());
        //接收者
        msg.setMessageSendExtendid(getPeer().getUserExtendId());
        //类型
        msg.setMessageType(new BigDecimal(ChatMessage.MSG_TYPE_VOICE));
        //创建语音
        ChatVoice chatVoice = new ChatVoice();
        //设置语音的本地地址
        chatVoice.setSendPath(path);
        //设置内容
        msg.setMessageContent(GsonTool.modelToString(chatVoice, ChatVoice.class));
        //时间
        msg.setMessageDate(new Date());
        //插入数据
        insertMessage(msg);
        //上传并发送
        uploadVoiceAndSend(msg, callback);
        //返回消息体
        return msg;
    }

    //发送语音消息
    public ChatMessage sendVoice(
            ChatVoice image,
            final FlappyIMCallback<ChatMessage> callback) {
        //创建消息
        final ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerator.generateCommomID());
        //设置
        msg.setMessageSession(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSend(getMine().getUserId());
        //发送者
        msg.setMessageSendExtendid(getMine().getUserExtendId());
        //接收者
        msg.setMessageRecieve(getPeer().getUserId());
        //接收者
        msg.setMessageSendExtendid(getPeer().getUserExtendId());
        //类型
        msg.setMessageType(new BigDecimal(ChatMessage.MSG_TYPE_VOICE));
        //设置内容
        msg.setMessageContent(GsonTool.modelToString(image, ChatVoice.class));
        //时间
        msg.setMessageDate(new Date());
        //插入数据
        insertMessage(msg);
        //发送消息
        sendMessage(msg, callback);

        return msg;
    }

    //重发消息
    public void resendMessage(final ChatMessage chatMessage, final FlappyIMCallback<ChatMessage> callback) {
        //重新发送
        if (chatMessage.getMessageType().intValue() == Integer.parseInt(ChatMessage.MSG_TYPE_TEXT)) {
            sendMessage(chatMessage, callback);
        } else if (chatMessage.getMessageType().intValue() == Integer.parseInt(ChatMessage.MSG_TYPE_IMG)) {
            uploadImageAndSend(chatMessage, callback);
        } else if (chatMessage.getMessageType().intValue() == Integer.parseInt(ChatMessage.MSG_TYPE_VOICE)) {
            uploadVoiceAndSend(chatMessage, callback);
        }
    }

    //获取最后一条消息
    public ChatMessage getLatestMessage() {
        return Database.getInstance().getLatestMessage(getSession().getSessionId());
    }


    //获取列表
    public List<ChatMessage> getMessagesByOffset(String offset, int size) {
        return Database.getInstance().getLatestMessage(getSession().getSessionId(), offset, size);
    }

}
