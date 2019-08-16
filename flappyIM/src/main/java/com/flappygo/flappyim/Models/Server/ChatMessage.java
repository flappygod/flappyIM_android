package com.flappygo.flappyim.Models.Server;

import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Tools.DateTimeTool;
import com.flappygo.flappyim.Tools.StringTool;

import java.math.BigDecimal;
import java.util.Date;

public class ChatMessage {



    //消息被创建
    public final static int SEND_STATE_CREATE=0;
    //消息已经送达服务器
    public final static int SEND_STATE_SENDED=1;
    //消息已经推送给客户端
    public final static int SEND_STATE_PUSHED=2;
    //消息已经到达
    public final static int SEND_STATE_REACHED=3;
    //消息发送失败
    public final static int SEND_STATE_FAILURE=9;




    //文本消息
    public final static String MSG_TYPE_TEXT = "1";
    //图片消息
    public final static String MSG_TYPE_IMG = "2";
    //语音消息
    public final static String MSG_TYPE_VOICE = "3";
    //表情消息
    public final static String MSG_TYPE_EMOJ = "4";
    //红包消息
    public final static String MSG_TYPE_REDBACKET = "5";


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


    //消息
    public ChatMessage(Flappy.Message msg) {
        //数据
        messageId = msg.getMessageId();
        messageSession = msg.getMessageSession();
        messageSessionType = new BigDecimal(msg.getMessageSessionType());
        messageSessionOffset = new BigDecimal(msg.getMessageSessionOffset());
        messageTableSeq = StringTool.strToDecimal(msg.getMessageTableSeq());
        messageType = new BigDecimal(msg.getMessageType());
        messageSend = msg.getMessageSend();
        messageSendExtendid =msg.getMessageSendExtendid();
        messageRecieve = msg.getMessageRecieve();
        messageRecieveExtendid=msg.getMessageRecieveExtendid();
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
            msgBuilder.setMessageTableSeq(StringTool.decimalToStr(getMessageTableSeq()));
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
}