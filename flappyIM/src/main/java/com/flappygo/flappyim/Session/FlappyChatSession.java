package com.flappygo.flappyim.Session;

import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Holder.HolderMessageRecieve;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Models.Request.ChatImage;
import com.flappygo.flappyim.Models.Request.ChatLocation;
import com.flappygo.flappyim.Models.Request.ChatVideo;
import com.flappygo.flappyim.Models.Request.ChatVoice;
import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Tools.IDGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//单聊的会话
public class FlappyChatSession extends FlappyBaseSession {

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

    //获取会话数据
    public SessionData getSession() {
        return session;
    }

    //设置会话数据
    public void setSession(SessionData session) {
        this.session = session;
    }

    //获取自己
    private ChatUser getMine() {
        return DataManager.getInstance().getLoginUser();
    }

    //获取要发送的ID
    private String getPeerID() {
        //如果是群聊，返回会话ID
        if (getSession().getSessionType().intValue() == SessionData.TYPE_GROUP) {
            return getSession().getSessionId();
        }
        //如果是单聊，返回用户ID
        else if (getSession().getSessionType().intValue() == SessionData.TYPE_SINGLE) {
            for (int s = 0; s < getSession().getUsers().size(); s++) {
                if (!getSession().getUsers().get(s).getUserId().equals(getMine().getUserId())) {
                    return getSession().getUsers().get(s).getUserId();
                }
            }
        }
        throw new RuntimeException("账号错误，聊天对象丢失");
    }

    //获取对方的extendID
    private String getPeerExtendID() {
        //如果是群聊，返回会话ID
        if (getSession().getSessionType().intValue() == SessionData.TYPE_GROUP) {
            return getSession().getSessionExtendId();
        }
        //如果是单聊，返回用户ID
        else if (getSession().getSessionType().intValue() == SessionData.TYPE_SINGLE) {
            for (int s = 0; s < getSession().getUsers().size(); s++) {
                if (!getSession().getUsers().get(s).getUserId().equals(getMine().getUserId())) {
                    return getSession().getUsers().get(s).getUserExtendId();
                }
            }
        }
        throw new RuntimeException("账号错误，聊天对象丢失");
    }


    //发送消息
    public ChatMessage sendText(String text,
                                FlappySendCallback<ChatMessage> callback) {
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
        msg.setMessageRecieve(getPeerID());
        //接收者
        msg.setMessageRecieveExtendid(getPeerExtendID());
        //类型
        msg.setMessageType(new BigDecimal(ChatMessage.MSG_TYPE_TEXT));
        //设置内容
        msg.setChatText(text);
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
                                      final FlappySendCallback<ChatMessage> callback) {

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
        msg.setMessageRecieve(getPeerID());
        //接收者
        msg.setMessageRecieveExtendid(getPeerExtendID());
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
                                 FlappySendCallback<ChatMessage> callback) {
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
        msg.setMessageRecieve(getPeerID());
        //接收者
        msg.setMessageRecieveExtendid(getPeerExtendID());
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
                                      final FlappySendCallback<ChatMessage> callback) {


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
        msg.setMessageRecieve(getPeerID());
        //接收者
        msg.setMessageRecieveExtendid(getPeerExtendID());
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
            final FlappySendCallback<ChatMessage> callback) {
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
        msg.setMessageRecieve(getPeerID());
        //接收者
        msg.setMessageRecieveExtendid(getPeerExtendID());
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


    //发送短视频
    public ChatMessage senLocalVideo(String path, final FlappySendCallback<ChatMessage> callback) {
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
        msg.setMessageRecieve(getPeerID());
        //接收者
        msg.setMessageRecieveExtendid(getPeerExtendID());
        //类型
        msg.setMessageType(new BigDecimal(ChatMessage.MSG_TYPE_VIDEO));
        //创建语音
        ChatVideo chatVoice = new ChatVideo();
        //设置语音的本地地址
        chatVoice.setSendPath(path);
        //设置内容
        msg.setMessageContent(GsonTool.modelToString(chatVoice, ChatVideo.class));
        //时间
        msg.setMessageDate(new Date());
        //插入数据
        insertMessage(msg);
        //上传并发送
        uploadVideoAndSend(msg, callback);
        //返回消息体
        return msg;
    }

    //发送视频信息
    public ChatMessage sendVideo(ChatVideo chatVideo, final FlappySendCallback<ChatMessage> callback) {
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
        msg.setMessageRecieve(getPeerID());
        //接收者
        msg.setMessageRecieveExtendid(getPeerExtendID());
        //类型
        msg.setMessageType(new BigDecimal(ChatMessage.MSG_TYPE_VIDEO));
        //设置内容
        msg.setMessageContent(GsonTool.modelToString(chatVideo, ChatVideo.class));
        //时间
        msg.setMessageDate(new Date());
        //插入数据
        insertMessage(msg);
        //上传并发送
        sendMessage(msg, callback);
        //返回消息体
        return msg;
    }


    //发送定位信息
    public ChatMessage sendLocation(ChatLocation loaction,
                                    final FlappySendCallback<ChatMessage> callback) {
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
        msg.setMessageRecieve(getPeerID());
        //接收者
        msg.setMessageRecieveExtendid(getPeerExtendID());
        //类型
        msg.setMessageType(new BigDecimal(ChatMessage.MSG_TYPE_LOCATE));
        //设置内容
        msg.setMessageContent(GsonTool.modelToString(loaction, ChatLocation.class));
        //时间
        msg.setMessageDate(new Date());
        //插入数据
        insertMessage(msg);
        //发送消息
        sendMessage(msg, callback);

        return msg;
    }

    //重发消息
    public void resendMessage(final ChatMessage chatMessage, final FlappySendCallback<ChatMessage> callback) {
        //重新发送
        if (chatMessage.getMessageType().intValue() == ChatMessage.MSG_TYPE_TEXT) {
            //发送消息
            sendMessage(chatMessage, callback);
        } else if (chatMessage.getMessageType().intValue() == ChatMessage.MSG_TYPE_IMG) {
            //上传文件并发送
            uploadImageAndSend(chatMessage, callback);
        } else if (chatMessage.getMessageType().intValue() == ChatMessage.MSG_TYPE_VOICE) {
            //上传语音并发送
            uploadVoiceAndSend(chatMessage, callback);
        } else if (chatMessage.getMessageType().intValue() == ChatMessage.MSG_TYPE_VIDEO) {
            //上传短视频并发送
            uploadVideoAndSend(chatMessage, callback);
        } else if (chatMessage.getMessageType().intValue() == ChatMessage.MSG_TYPE_LOCATE) {
            //上传语音并发送
            sendMessage(chatMessage, callback);
        }
    }

    //获取最后一条消息
    public ChatMessage getLatestMessage() {
        Database database=new Database();
        ChatMessage chatMessage=database.getSessionLatestMessage(getSession().getSessionId());
        database.close();
        return chatMessage;
    }

    //获取这条消息之前的消息
    public List<ChatMessage> getFormerMessages(String  messageId, int size) {
        Database database=new Database();
        List<ChatMessage> chatMessages=database.getSessionLatestMessage(getSession().getSessionId(),
                messageId,
                size);
        database.close();
        return chatMessages;
    }

}
