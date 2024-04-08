package com.flappygo.flappyim.Models.Server;

import android.util.Base64;

import com.flappygo.flappyim.Models.Request.ChatLocation;
import com.flappygo.flappyim.Models.Request.ChatAction;
import com.flappygo.flappyim.Models.Request.ChatSystem;
import com.flappygo.flappyim.Models.Request.ChatVideo;
import com.flappygo.flappyim.Models.Request.ChatVoice;
import com.flappygo.flappyim.Models.Request.ChatImage;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Request.ChatFile;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Tools.DateTimeTool;
import com.flappygo.flappyim.Tools.StringTool;

import java.math.BigDecimal;
import java.util.Date;


public class ChatMessage {


    //消息被创建
    public final static int SEND_STATE_CREATE = 0;
    //消息已经送达服务器
    public final static int SEND_STATE_SENT = 1;
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
    //短视频消息
    public final static int MSG_TYPE_FILE = 6;
    //自定义消息
    public final static int MSG_TYPE_CUSTOM = 7;
    //动作消息
    public final static int MSG_TYPE_ACTION = 8;

    //已读消息
    public final static int ACTION_TYPE_READ = 1;
    //删除消息
    public final static int ACTION_TYPE_DELETE = 2;

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

    private String messageSecretSend;

    private String messageSecretReceive;

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

    public String getMessageSecretSend() {
        return messageSecretSend;
    }

    public void setMessageSecretSend(String messageSecretSend) {
        this.messageSecretSend = messageSecretSend;
    }

    public String getMessageSecretReceive() {
        return messageSecretReceive;
    }

    public void setMessageSecretReceive(String messageSecretReceive) {
        this.messageSecretReceive = messageSecretReceive;
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
        messageSecretSend = msg.getMessageSecretSend();
        messageSecretReceive = msg.getMessageSecretReceive();
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
        if (getMessageSecretSend() != null)
            msgBuilder.setMessageSecretSend(getMessageSecretSend());
        if (getMessageSecretReceive() != null)
            msgBuilder.setMessageSecretReceive(getMessageSecretReceive());
        if (getMessageDate() != null)
            msgBuilder.setMessageDate(DateTimeTool.dateToStr(getMessageDate()));
        if (getIsDelete() != null)
            msgBuilder.setIsDelete(StringTool.decimalToInt(getIsDelete()));
        if (getDeleteDate() != null)
            msgBuilder.setDeleteDate(DateTimeTool.dateToStr(getDeleteDate()));

        return msgBuilder.build();
    }

    //设置文本消息
    public void setChatText(String text) {
        if (text != null) {
            messageType = new BigDecimal(ChatMessage.MSG_TYPE_TEXT);
            setMessageContent(encrypt(text, null));
        }
    }

    //获取文本消息
    public String getChatText() {
        if (getMessageType().intValue() == MSG_TYPE_TEXT) {
            return decrypt(getMessageContent(), null);
        }
        return null;
    }

    //设置系统消息
    public void setChatSystem(ChatSystem chatSystem) {
        if (chatSystem != null) {
            messageType = new BigDecimal(ChatMessage.MSG_TYPE_SYSTEM);
            setMessageContent(encrypt(GsonTool.modelToString(chatSystem, ChatSystem.class), null));
        }
    }

    //获取系统消息
    public ChatSystem getChatSystem() {
        if (getMessageType().intValue() == MSG_TYPE_SYSTEM) {
            String content = decrypt(getMessageContent(), null);
            return GsonTool.jsonStringToModel(content, ChatSystem.class);
        }
        return null;
    }

    //设置图片消息
    public void setChatImage(ChatImage chatImage) {
        if (chatImage != null) {
            messageType = new BigDecimal(ChatMessage.MSG_TYPE_IMG);
            setMessageContent(encrypt(GsonTool.modelToString(chatImage, ChatImage.class), null));
        }
    }

    //获取图片消息
    public ChatImage getChatImage() {
        if (getMessageType().intValue() == MSG_TYPE_IMG) {
            String content = decrypt(getMessageContent(), null);
            return GsonTool.jsonStringToModel(content, ChatImage.class);
        }
        return null;
    }

    //设置语音消息
    public void setChatVoice(ChatVoice chatVoice) {
        if (chatVoice != null) {
            messageType = new BigDecimal(ChatMessage.MSG_TYPE_VOICE);
            setMessageContent(encrypt(GsonTool.modelToString(chatVoice, ChatVoice.class), null));
        }
    }

    //获取语音消息
    public ChatVoice getChatVoice() {
        if (getMessageType().intValue() == MSG_TYPE_VOICE) {
            String content = decrypt(getMessageContent(), null);
            return GsonTool.jsonStringToModel(content, ChatVoice.class);
        }
        return null;
    }

    //设置定位消息
    public void setChatLocation(ChatLocation chatLocation) {
        //设置文本消息
        if (chatLocation != null) {
            messageType = new BigDecimal(ChatMessage.MSG_TYPE_LOCATE);
            setMessageContent(encrypt(GsonTool.modelToString(chatLocation, ChatLocation.class), null));
        }
    }

    //获取定位消息
    public ChatLocation getChatLocation() {
        //获取位置消息
        if (getMessageType().intValue() == MSG_TYPE_LOCATE) {
            String content = decrypt(getMessageContent(), null);
            return GsonTool.jsonStringToModel(content, ChatLocation.class);
        }
        return null;
    }

    //设置视频消息
    public void setChatVideo(ChatVideo chatVideo) {
        //设置文本消息
        if (chatVideo != null) {
            messageType = new BigDecimal(ChatMessage.MSG_TYPE_VIDEO);
            setMessageContent(encrypt(GsonTool.modelToString(chatVideo, ChatVideo.class), null));
        }
    }

    //获取视频消息
    public ChatVideo getChatVideo() {
        if (getMessageType().intValue() == MSG_TYPE_VIDEO) {
            String content = decrypt(getMessageContent(), null);
            return GsonTool.jsonStringToModel(content, ChatVideo.class);
        }
        return null;
    }

    //设置文件消息
    public void setChatFile(ChatFile chatFile) {
        if (chatFile != null) {
            messageType = new BigDecimal(ChatMessage.MSG_TYPE_FILE);
            setMessageContent(encrypt(GsonTool.modelToString(chatFile, ChatFile.class), null));
        }
    }

    //获取文件消息
    public ChatFile getChatFile() {
        if (getMessageType().intValue() == MSG_TYPE_FILE) {
            String content = decrypt(getMessageContent(), null);
            return GsonTool.jsonStringToModel(content, ChatFile.class);
        }
        return null;
    }

    //设置自定义消息
    public void setChatCustom(String text) {
        if (text != null) {
            messageType = new BigDecimal(ChatMessage.MSG_TYPE_CUSTOM);
            setMessageContent(encrypt(text, null));
        }
    }

    //获取自定义消息
    public String getChatCustom() {
        if (getMessageType().intValue() == MSG_TYPE_CUSTOM) {
            return decrypt(getMessageContent(), null);
        }
        return null;
    }

    //设置文件消息
    public void setChatAction(ChatAction chatAction) {
        if (chatAction != null) {
            messageType = new BigDecimal(ChatMessage.MSG_TYPE_ACTION);
            setMessageContent(encrypt(GsonTool.modelToString(chatAction, ChatAction.class), null));
        }
    }

    //获取文件消息
    public ChatAction getChatAction() {
        if (getMessageType().intValue() == MSG_TYPE_ACTION) {
            String content = decrypt(getMessageContent(), null);
            return GsonTool.jsonStringToModel(content, ChatAction.class);
        }
        return null;
    }

    /*******
     * 加密数据
     * @param data 数据
     * @param key  秘钥
     * @return 加密数据
     */
    private String encrypt(String data, String key) {
        if (StringTool.isEmpty(data)) {
            return null;
        }
        String retStr = Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
        return retStr.replace("\n", "");
    }

    /*******
     * 解密数据
     * @param data 数据
     * @param key  秘钥
     * @return 加密数据
     */
    private String decrypt(String data, String key) {
        if (StringTool.isEmpty(data)) {
            return null;
        }
        String retStr = new String(Base64.decode(data.getBytes(), Base64.DEFAULT));
        return retStr.replace("\n", "");
    }

}