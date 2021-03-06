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

    private String messageSend;

    private String messageSendExtendid;

    private String messageRecieve;

    private String messageRecieveExtendid;

    private String messageContent;

    private BigDecimal messageSended;

    private BigDecimal messageReaded;

    private Date messageDate;

    private BigDecimal messageDeleted;

    private BigDecimal messageStamp;

    private Date messageDeletedDate;

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

    public String getMessageSend() {
        return messageSend;
    }

    public void setMessageSend(String messageSend) {
        this.messageSend = messageSend == null ? null : messageSend.trim();
    }

    public String getMessageSendExtendid() {
        return messageSendExtendid;
    }

    public void setMessageSendExtendid(String messageSendExtendid) {
        this.messageSendExtendid = messageSendExtendid == null ? null : messageSendExtendid.trim();
    }

    public String getMessageRecieve() {
        return messageRecieve;
    }

    public void setMessageRecieve(String messageRecieve) {
        this.messageRecieve = messageRecieve == null ? null : messageRecieve.trim();
    }

    public String getMessageRecieveExtendid() {
        return messageRecieveExtendid;
    }

    public void setMessageRecieveExtendid(String messageRecieveExtendid) {
        this.messageRecieveExtendid = messageRecieveExtendid == null ? null : messageRecieveExtendid.trim();
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent == null ? null : messageContent.trim();
    }

    public BigDecimal getMessageSended() {
        return messageSended;
    }

    public void setMessageSended(BigDecimal messageSended) {
        this.messageSended = messageSended;
    }

    public BigDecimal getMessageReaded() {
        return messageReaded;
    }

    public void setMessageReaded(BigDecimal messageReaded) {
        this.messageReaded = messageReaded;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public BigDecimal getMessageDeleted() {
        return messageDeleted;
    }

    public void setMessageDeleted(BigDecimal messageDeleted) {
        this.messageDeleted = messageDeleted;
    }

    public Date getMessageDeletedDate() {
        return messageDeletedDate;
    }

    public void setMessageDeletedDate(Date messageDeletedDate) {
        this.messageDeletedDate = messageDeletedDate;
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
        messageSend = msg.getMessageSend();
        messageSendExtendid = msg.getMessageSendExtendid();
        messageRecieve = msg.getMessageRecieve();
        messageRecieveExtendid = msg.getMessageRecieveExtendid();
        messageContent = msg.getMessageContent();
        messageSended = new BigDecimal(msg.getMessageSended());
        messageReaded = new BigDecimal(msg.getMessageReaded());
        messageDeleted = new BigDecimal(msg.getMessageDeleted());
        messageDate = DateTimeTool.strToDate(msg.getMessageDate());
        messageDeletedDate = DateTimeTool.strToDate(msg.getMessageDeletedDate());
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
        if (getMessageSend() != null)
            msgBuilder.setMessageSend(getMessageSend());
        if (getMessageSendExtendid() != null)
            msgBuilder.setMessageSendExtendid(getMessageSendExtendid());
        if (getMessageRecieve() != null)
            msgBuilder.setMessageRecieve(getMessageRecieve());
        if (getMessageRecieveExtendid() != null)
            msgBuilder.setMessageRecieveExtendid(getMessageRecieveExtendid());
        if (getMessageContent() != null)
            msgBuilder.setMessageContent(getMessageContent());
        if (getMessageSended() != null)
            msgBuilder.setMessageSended(StringTool.decimalToInt(getMessageSended()));
        if (getMessageReaded() != null)
            msgBuilder.setMessageReaded(StringTool.decimalToInt(getMessageReaded()));
        if (getMessageDate() != null)
            msgBuilder.setMessageDate(DateTimeTool.dateToStr(getMessageDate()));
        if (getMessageDeleted() != null)
            msgBuilder.setMessageDeleted(StringTool.decimalToInt(getMessageDeleted()));
        if (getMessageDeletedDate() != null)
            msgBuilder.setMessageDeletedDate(DateTimeTool.dateToStr(getMessageDeletedDate()));

        return msgBuilder.build();
    }

    //设置文本
    public void setChatText(String text) {
        //设置文本消息
        if (getMessageType().intValue() == MSG_TYPE_TEXT) {
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

    //获取聊天图片
    public ChatImage getChatImage() {
        //获取图片消息
        if (getMessageType().intValue() == MSG_TYPE_IMG) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return GsonTool.jsonObjectToModel(str, ChatImage.class);
            }
        }
        return null;
    }

    //获取聊天语音
    public ChatVoice getChatVoice() {
        //获取语音消息
        if (getMessageType().intValue() == MSG_TYPE_VOICE) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return GsonTool.jsonObjectToModel(str, ChatVoice.class);
            }
        }
        return null;
    }

    //获取定位信息
    public ChatLocation getChatLocation() {
        //获取位置消息
        if (getMessageType().intValue() == MSG_TYPE_LOCATE) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return GsonTool.jsonObjectToModel(str, ChatLocation.class);
            }
        }
        return null;
    }

    //获取视频信息
    public ChatVideo getChatVideo() {
        //获取视频消息
        if (getMessageType().intValue() == MSG_TYPE_VIDEO) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return GsonTool.jsonObjectToModel(str, ChatVideo.class);
            }
        }
        return null;
    }

    //获取系统消息
    public ChatSystem getChatSystem(){
        //获取视频消息
        if (getMessageType().intValue() == MSG_TYPE_SYSTEM) {
            String str = getMessageContent();
            if (!StringTool.isEmpty(str)) {
                return GsonTool.jsonObjectToModel(str, ChatSystem.class);
            }
        }
        return null;
    }

}