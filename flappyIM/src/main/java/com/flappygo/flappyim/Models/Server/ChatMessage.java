package com.flappygo.flappyim.Models.Server;

import android.util.Base64;

import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Models.Request.ChatImage;
import com.flappygo.flappyim.Models.Request.ChatLocation;
import com.flappygo.flappyim.Models.Request.ChatSystem;
import com.flappygo.flappyim.Models.Request.ChatVideo;
import com.flappygo.flappyim.Models.Request.ChatVoice;
import com.flappygo.flappyim.Tools.DateTimeTool;
import com.flappygo.flappyim.Tools.StringTool;

import java.math.BigDecimal;
import java.util.Date;

public class ChatMessage {


    //消息被创建
    public final static int SEND_STATE_CREATE = 0;
    //消息已经送达服务器
    public final static int SEND_STATE_SENDED = 1;
    //消息已经到达
    public final static int SEND_STATE_REACHED = 3;
    //消息发送失败
    public final static int SEND_STATE_FAILURE = 9;


    //系统消息
    public final static int MSG_TYPE_SYSTEM = 0;
    //文本消息
    public final static int MSG_TYPE_TEXT = 1;
    //图片消息
    public final static int MSG_TYPE_IMG = 2;
    //语音消息
    public final static int MSG_TYPE_VOICE = 3;
    //位置信息
    public final static int MSG_TYPE_LOCATE = 4;
    //小视频消息
    public final static int MSG_TYPE_VIDEO = 5;


    public ChatMessage() {

    }


    private String messageId;

    private String messageSession;

    private BigDecimal messageSessionType;

    private BigDecimal messageSessionOffset;

    private BigDecimal messageTableSeq;

    private BigDecimal messageType;

    private String messageSendId;

    private String messageSendExtendId;

    private String messageReceiveId;

    private String messageReceiveExtendId;

    private String messageContent;

    private BigDecimal messageSendState;

    private BigDecimal messageReadState;

    private Date messageDate;

    private BigDecimal isDelete;

    private BigDecimal messageStamp;

    private Date deleteDate;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId == null ? null : messageId.trim();
    }

    public String getMessageSession() {
        return messageSession;
    }

    public void setMessageSession(String messageSession) {
        this.messageSession = messageSession == null ? null : messageSession.trim();
    }

    public BigDecimal getMessageSessionType() {
        return messageSessionType;
    }

    public void setMessageSessionType(BigDecimal messageSessionType) {
        this.messageSessionType = messageSessionType;
    }

    public BigDecimal getMessageSessionOffset() {
        return messageSessionOffset;
    }

    public void setMessageSessionOffset(BigDecimal messageSessionOffset) {
        this.messageSessionOffset = messageSessionOffset;
    }

    public BigDecimal getMessageTableSeq() {
        return messageTableSeq;
    }

    public void setMessageTableSeq(BigDecimal messageTableSeq) {
        this.messageTableSeq = messageTableSeq;
    }

    public BigDecimal getMessageType() {
        return messageType;
    }

    public void setMessageType(BigDecimal messageType) {
        this.messageType = messageType;
    }

    public String getMessageSendId() {
        return messageSendId;
    }

    public void setMessageSendId(String messageSendId) {
        this.messageSendId = messageSendId == null ? null : messageSendId.trim();
    }

    public String getMessageSendExtendId() {
        return messageSendExtendId;
    }

    public void setMessageSendExtendId(String messageSendExtendId) {
        this.messageSendExtendId = messageSendExtendId == null ? null : messageSendExtendId.trim();
    }

    public String getMessageReceiveId() {
        return messageReceiveId;
    }

    public void setMessageReceiveId(String messageReceiveId) {
        this.messageReceiveId = messageReceiveId == null ? null : messageReceiveId.trim();
    }

    public String getMessageReceiveExtendId() {
        return messageReceiveExtendId;
    }

    public void setMessageReceiveExtendId(String messageReceiveExtendId) {
        this.messageReceiveExtendId = messageReceiveExtendId == null ? null : messageReceiveExtendId.trim();
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent == null ? null : messageContent.trim();
    }

    public BigDecimal getMessageSendState() {
        return messageSendState;
    }

    public void setMessageSendState(BigDecimal messageSendState) {
        this.messageSendState = messageSendState;
    }

    public BigDecimal getMessageReadState() {
        return messageReadState;
    }

    public void setMessageReadState(BigDecimal messageReadState) {
        this.messageReadState = messageReadState;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public BigDecimal getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(BigDecimal isDelete) {
        this.isDelete = isDelete;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public BigDecimal getMessageStamp() {
        return messageStamp;
    }

    public void setMessageStamp(BigDecimal messageStamp) {
        this.messageStamp = messageStamp;
    }

    //消息
    public ChatMessage(Flappy.Message msg) {
        //数据
        messageId = msg.getMessageId();
        messageSession = msg.getMessageSession();
        messageSessionType = new BigDecimal(msg.getMessageSessionType());
        messageSessionOffset = new BigDecimal(msg.getMessageSessionOffset());
        messageTableSeq = new BigDecimal(msg.getMessageTableSeq());
        messageType = new BigDecimal(msg.getMessageType());
        messageSendId = Long.toString(msg.getMessageSendId());
        messageSendExtendId = msg.getMessageSendExtendId();
        messageReceiveId = Long.toString(msg.getMessageReceiveId());
        messageReceiveExtendId = msg.getMessageReceiveExtendId();
        messageContent = msg.getMessageContent();
        messageSendState = new BigDecimal(msg.getMessageSendState());
        messageReadState = new BigDecimal(msg.getMessageReadState());
        isDelete = new BigDecimal(msg.getIsDelete());
        messageDate = DateTimeTool.strToDate(msg.getMessageDate());
        deleteDate = DateTimeTool.strToDate(msg.getDeleteDate());
    }

    //转换为protoc消息
    public Flappy.Message toProtocMessage(Flappy.Message.Builder msgBuilder) {
        //转换消息
        if (getMessageId() != null)
            msgBuilder.setMessageId(getMessageId());
        if (getMessageSession() != null)
            msgBuilder.setMessageSession(getMessageSession());
        if (getMessageSessionType() != null)
            msgBuilder.setMessageSessionType(StringTool.decimalToInt(getMessageSessionType()));
        if (getMessageSessionOffset() != null)
            msgBuilder.setMessageSessionOffset(StringTool.decimalToInt(getMessageSessionOffset()));
        if (getMessageTableSeq() != null)
            msgBuilder.setMessageTableSeq(StringTool.decimalToInt(getMessageTableSeq()));
        if (getMessageType() != null)
            msgBuilder.setMessageType(StringTool.decimalToInt(getMessageType()));
        if (getMessageSendId() != null)
            msgBuilder.setMessageSendId(StringTool.strToLong(getMessageSendId()));
        if (getMessageSendExtendId() != null)
            msgBuilder.setMessageSendExtendId(getMessageSendExtendId());
        if (getMessageReceiveId() != null)
            msgBuilder.setMessageReceiveId(StringTool.strToLong(getMessageReceiveId()));
        if (getMessageReceiveExtendId() != null)
            msgBuilder.setMessageReceiveExtendId(getMessageReceiveExtendId());
        if (getMessageContent() != null)
            msgBuilder.setMessageContent(getMessageContent());
        if (getMessageSendState() != null)
            msgBuilder.setMessageSendState(StringTool.decimalToInt(getMessageSendState()));
        if (getMessageReadState() != null)
            msgBuilder.setMessageReadState(StringTool.decimalToInt(getMessageReadState()));
        if (getMessageDate() != null)
            msgBuilder.setMessageDate(DateTimeTool.dateToStr(getMessageDate()));
        if (getIsDelete() != null)
            msgBuilder.setIsDelete(StringTool.decimalToInt(getIsDelete()));
        if (getDeleteDate() != null)
            msgBuilder.setDeleteDate(DateTimeTool.dateToStr(getDeleteDate()));

        return msgBuilder.build();
    }

    //设置文本
    public void setChatText(String text) {
        //设置文本消息
        if (text != null) {
            String strBase64 = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
            setMessageContent(strBase64);
        }
    }

    //获取聊天文本
    public String getChatText() {
        //获取文本消息
        if (getMessageType().intValue() == MSG_TYPE_TEXT) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return new String(Base64.decode(str.getBytes(), Base64.DEFAULT));
            }
        }
        return null;
    }


    //设置聊天视频信息
    public void setChatSystem(ChatSystem chatSystem) {
        //设置文本消息
        if (chatSystem != null) {
            String strBase64 = Base64.encodeToString(GsonTool.modelToString(chatSystem, ChatSystem.class).getBytes(), Base64.DEFAULT);
            setMessageContent(strBase64);
        }
    }

    //获取系统消息
    public ChatSystem getChatSystem() {
        //获取视频消息
        if (getMessageType().intValue() == MSG_TYPE_SYSTEM) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return GsonTool.jsonObjectToModel(new String(Base64.decode(str.getBytes(), Base64.DEFAULT)), ChatSystem.class);
            }
        }
        return null;
    }

    //设置聊天图片
    public void setChatImage(ChatImage chatImage) {
        //设置文本消息
        if (chatImage != null) {
            String strBase64 = Base64.encodeToString(GsonTool.modelToString(chatImage, ChatImage.class).getBytes(), Base64.DEFAULT);
            setMessageContent(strBase64);
        }
    }

    //获取聊天图片
    public ChatImage getChatImage() {
        //获取图片消息
        if (getMessageType().intValue() == MSG_TYPE_IMG) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return GsonTool.jsonObjectToModel(new String(Base64.decode(str.getBytes(), Base64.DEFAULT)), ChatImage.class);
            }
        }
        return null;
    }

    //设置聊天语音
    public void setChatVoice(ChatVoice chatVoice) {
        //设置文本消息
        if (chatVoice != null) {
            String strBase64 = Base64.encodeToString(GsonTool.modelToString(chatVoice, ChatVoice.class).getBytes(), Base64.DEFAULT);
            setMessageContent(strBase64);
        }
    }

    //获取聊天语音
    public ChatVoice getChatVoice() {
        //获取语音消息
        if (getMessageType().intValue() == MSG_TYPE_VOICE) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return GsonTool.jsonObjectToModel(new String(Base64.decode(str.getBytes(), Base64.DEFAULT)), ChatVoice.class);
            }
        }
        return null;
    }


    //设置聊天视频信息
    public void setChatVideo(ChatVideo chatVideo) {
        //设置文本消息
        if (chatVideo != null) {
            String strBase64 = Base64.encodeToString(GsonTool.modelToString(chatVideo, ChatVideo.class).getBytes(), Base64.DEFAULT);
            setMessageContent(strBase64);
        }
    }

    //获取视频信息
    public ChatVideo getChatVideo() {
        //获取视频消息
        if (getMessageType().intValue() == MSG_TYPE_VIDEO) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return GsonTool.jsonObjectToModel(new String(Base64.decode(str.getBytes(), Base64.DEFAULT)), ChatVideo.class);
            }
        }
        return null;
    }


    //设置聊天语音
    public void setChatLocation(ChatLocation chatLocation) {
        //设置文本消息
        if (chatLocation != null) {
            String strBase64 = Base64.encodeToString(GsonTool.modelToString(chatLocation, ChatLocation.class).getBytes(), Base64.DEFAULT);
            setMessageContent(strBase64);
        }
    }

    //获取定位信息
    public ChatLocation getChatLocation() {
        //获取位置消息
        if (getMessageType().intValue() == MSG_TYPE_LOCATE) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return GsonTool.jsonObjectToModel(new String(Base64.decode(str.getBytes(), Base64.DEFAULT)), ChatLocation.class);
            }
        }
        return null;
    }


}