package com.flappygo.flappyim.Models.Server;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.flappygo.flappyim.Tools.Generate.IDGenerateTool;
import com.flappygo.flappyim.Models.Request.ChatLocation;
import com.flappygo.flappyim.Models.Request.ChatAction;
import com.flappygo.flappyim.Models.Request.ChatSystem;
import com.flappygo.flappyim.Models.Request.ChatVideo;
import com.flappygo.flappyim.Models.Request.ChatVoice;
import com.flappygo.flappyim.Models.Request.ChatImage;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Request.ChatFile;
import com.flappygo.flappyim.Tools.Secret.AESTool;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.Tools.TimeTool;

import java.util.Date;


public class ChatMessage {


    /***消息状态***/
    //消息被创建
    public final static int SEND_STATE_SENDING = 0;
    //消息已经送达服务器
    public final static int SEND_STATE_SENT = 1;
    //消息已经到达
    public final static int SEND_STATE_REACHED = 3;
    //消息发送失败
    public final static int SEND_STATE_FAILURE = 9;


    /***消息类型***/
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


    /***信息更新***/
    //制作提醒
    public final static int SYSTEM_MSG_NOTICE = 0;
    //用户信息更新(获取)
    public final static int SYSTEM_MSG_MEMBER_UPDATE = 1;
    //用户信息更新(删除)
    public final static int SYSTEM_MSG_MEMBER_DELETE = 2;
    //用户信息增加(获取)
    public final static int SYSTEM_MSG_MEMBER_ADD = 3;
    //更新单条会话(所有数据)
    public final static int SYSTEM_MSG_SESSION_UPDATE = 11;
    //会话Enable
    public final static int SYSTEM_MSG_SESSION_ENABLE = 12;
    //会话Disable
    public final static int SYSTEM_MSG_SESSION_DISABLE = 13;
    //完全删除会话
    public final static int SYSTEM_MSG_SESSION_DELETE = 14;
    //更新单条会话(仅会话信息，不包含人员信息)
    public final static int SYSTEM_MSG_SESSION_UPDATE_INFO = 15;



    /***动作消息***/
    //撤回消息
    public final static int ACTION_TYPE_MSG_RECALL = 1;
    //删除消息
    public final static int ACTION_TYPE_MSG_DELETE = 2;
    //会话已读
    public final static int ACTION_TYPE_SESSION_READ = 3;
    //会话静音
    public final static int ACTION_TYPE_SESSION_MUTE = 4;
    //会话置顶
    public final static int ACTION_TYPE_SESSION_PIN = 5;
    //会话删除临时
    public final static int ACTION_TYPE_SESSION_DELETE_TEMP = 6;


    /****所有消息的AT*****/
    public final static String MESSAGE_AT_ALL = "all";




    public ChatMessage() {
        messageSecret = IDGenerateTool.getRandomStr(16);
    }


    // Constructor
    @SuppressLint("Range")
    public ChatMessage(Cursor cursor) {
        ///ID区
        this.messageId = cursor.getString(cursor.getColumnIndex("messageId"));
        this.messageSessionId = cursor.getString(cursor.getColumnIndex("messageSessionId"));
        this.messageSessionType = cursor.getInt(cursor.getColumnIndex("messageSessionType"));
        this.messageSessionOffset = cursor.getLong(cursor.getColumnIndex("messageSessionOffset"));
        this.messageTableOffset = cursor.getLong(cursor.getColumnIndex("messageTableOffset"));

        ///发送接收区
        this.messageType = cursor.getInt(cursor.getColumnIndex("messageType"));
        this.messageSendId = cursor.getString(cursor.getColumnIndex("messageSendId"));
        this.messageSendExtendId = cursor.getString(cursor.getColumnIndex("messageSendExtendId"));
        this.messageReceiveId = cursor.getString(cursor.getColumnIndex("messageReceiveId"));
        this.messageReceiveExtendId = cursor.getString(cursor.getColumnIndex("messageReceiveExtendId"));

        ///状态区
        this.messageSendState = cursor.getInt(cursor.getColumnIndex("messageSendState"));
        this.messageReadState = cursor.getInt(cursor.getColumnIndex("messageReadState"));
        this.messagePinState = cursor.getInt(cursor.getColumnIndex("messagePinState"));
        this.messageStamp = cursor.getLong(cursor.getColumnIndex("messageStamp"));

        ///回复区
        this.messageReplyMsgId = cursor.getString(cursor.getColumnIndex("messageReplyMsgId"));
        this.messageReplyMsgType = cursor.getInt(cursor.getColumnIndex("messageReplyMsgType"));
        this.messageReplyUserId = cursor.getString(cursor.getColumnIndex("messageReplyUserId"));
        this.messageForwardTitle = cursor.getString(cursor.getColumnIndex("messageForwardTitle"));

        ///操作ID区
        this.messageRecallUserId = cursor.getString(cursor.getColumnIndex("messageRecallUserId"));
        this.messageAtUserIds = cursor.getString(cursor.getColumnIndex("messageAtUserIds"));
        this.messageReadUserIds = cursor.getString(cursor.getColumnIndex("messageReadUserIds"));
        this.messageDeleteUserIds = cursor.getString(cursor.getColumnIndex("messageDeleteUserIds"));

        ///基本区
        this.messageDate = TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate")));
        this.isDelete = cursor.getInt(cursor.getColumnIndex("isDelete"));
        this.deleteDate = TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate")));

        ///内容区
        this.messageContent = cursor.getString(cursor.getColumnIndex("messageContent"));
        this.messageReplyMsgContent = cursor.getString(cursor.getColumnIndex("messageReplyMsgContent"));
        this.messageSecret = cursor.getString(cursor.getColumnIndex("messageSecret"));
    }


    private String messageId;

    private String messageSessionId;

    private int messageSessionType;

    private long messageSessionOffset;

    private long messageTableOffset;

    private int messageType;

    private String messageSendId;

    private String messageSendExtendId;

    private String messageReceiveId;

    private String messageReceiveExtendId;

    private String messageContent;

    private int messageSendState;

    private int messageReadState;

    private int messagePinState;

    private String messageSecret;

    private Date messageDate;

    private int isDelete;

    private String messageReplyMsgId;

    private int messageReplyMsgType;

    private String messageReplyMsgContent;

    private String messageReplyUserId;

    private String messageForwardTitle;

    private String messageRecallUserId;

    private String messageAtUserIds;

    private String messageReadUserIds;

    private String messageDeleteUserIds;


    private long messageStamp;

    private Date deleteDate;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId == null ? null : messageId.trim();
    }

    public String getMessageSessionId() {
        return messageSessionId;
    }

    public void setMessageSessionId(String messageSessionId) {
        this.messageSessionId = messageSessionId == null ? null : messageSessionId.trim();
    }

    public Integer getMessageSessionType() {
        return messageSessionType;
    }

    public void setMessageSessionType(Integer messageSessionType) {
        this.messageSessionType = (messageSessionType != null) ? messageSessionType : 0;
    }

    public Long getMessageSessionOffset() {
        return messageSessionOffset;
    }

    public void setMessageSessionOffset(Long messageSessionOffset) {
        this.messageSessionOffset = (messageSessionOffset != null) ? messageSessionOffset : 0L;
    }

    public Long getMessageTableOffset() {
        return messageTableOffset;
    }

    public void setMessageTableOffset(Long messageTableOffset) {
        this.messageTableOffset = (messageTableOffset != null) ? messageTableOffset : 0L;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = (messageType != null) ? messageType : 0;
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

    public Integer getMessageSendState() {
        return messageSendState;
    }

    public void setMessageSendState(Integer messageSendState) {
        this.messageSendState = (messageSendState != null) ? messageSendState : 0;
    }

    public Integer getMessageReadState() {
        return messageReadState;
    }

    public void setMessageReadState(Integer messageReadState) {
        this.messageReadState = (messageReadState != null) ? messageReadState : 0;
    }

    public String getMessageSecret() {
        return messageSecret;
    }

    public void setMessageSecret(String messageSecret) {
        this.messageSecret = messageSecret;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = (isDelete != null) ? isDelete : 0;
    }

    public String getMessageReplyMsgId() {
        return messageReplyMsgId;
    }

    public void setMessageReplyMsgId(String messageReplyMsgId) {
        this.messageReplyMsgId = messageReplyMsgId;
    }

    public Integer getMessageReplyMsgType() {
        return messageReplyMsgType;
    }

    public void setMessageReplyMsgType(Integer messageReplyMsgType) {
        this.messageReplyMsgType = (messageReplyMsgType != null) ? messageReplyMsgType : 0;
    }

    public String getMessageReplyMsgContent() {
        return messageReplyMsgContent;
    }

    public void setMessageReplyMsgContent(String messageReplyMsgContent) {
        this.messageReplyMsgContent = messageReplyMsgContent;
    }

    public String getMessageReplyUserId() {
        return messageReplyUserId;
    }

    public void setMessageReplyUserId(String messageReplyUserId) {
        this.messageReplyUserId = messageReplyUserId;
    }

    public String getMessageRecallUserId() {
        return messageRecallUserId;
    }

    public void setMessageRecallUserId(String messageRecallUserId) {
        this.messageRecallUserId = messageRecallUserId;
    }

    public String getMessageAtUserIds() {
        return messageAtUserIds;
    }

    public void setMessageAtUserIds(String messageAtUserIds) {
        this.messageAtUserIds = messageAtUserIds;
    }

    public String getMessageReadUserIds() {
        return messageReadUserIds;
    }

    public void setMessageReadUserIds(String messageReadUserIds) {
        this.messageReadUserIds = messageReadUserIds;
    }

    public String getMessageDeleteUserIds() {
        return messageDeleteUserIds;
    }

    public void setMessageDeleteUserIds(String messageDeleteUserIds) {
        this.messageDeleteUserIds = messageDeleteUserIds;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public Long getMessageStamp() {
        return messageStamp;
    }

    public void setMessageStamp(Long messageStamp) {
        this.messageStamp = (messageStamp != null) ? messageStamp : 0L;
    }

    public Integer getMessagePinState() {
        return messagePinState;
    }

    public void setMessagePinState(Integer messagePinState) {
        this.messagePinState = messagePinState;
    }

    public String getMessageForwardTitle() {
        return messageForwardTitle;
    }

    public void setMessageForwardTitle(String messageForwardTitle) {
        this.messageForwardTitle = messageForwardTitle;
    }

    /******
     * 设置系统消息
     * @param chatSystem 系统消息
     */
    public void setChatSystem(ChatSystem chatSystem) {
        messageType = ChatMessage.MSG_TYPE_SYSTEM;
        setMessageContent(modelToStr(chatSystem));
    }

    /******
     * 获取系统消息
     * @return 系统消息
     */
    public ChatSystem getChatSystem() {
        if (getMessageType() == MSG_TYPE_SYSTEM) {
            return strToModel(ChatSystem.class);
        }
        return null;
    }

    /******
     * 设置动作消息
     * @param chatAction 动作消息
     */
    public void setChatAction(ChatAction chatAction) {
        messageType = ChatMessage.MSG_TYPE_ACTION;
        setMessageContent(modelToStr(chatAction));
    }

    /******
     * 获取动作消息
     * @return 动作消息
     */
    public ChatAction getChatAction() {
        if (getMessageType() == MSG_TYPE_ACTION) {
            return strToModel(ChatAction.class);
        }
        return null;
    }

    /******
     * 设置文本消息
     * @param text 文本消息，加密
     */
    public void setChatText(String text) {
        messageType = ChatMessage.MSG_TYPE_TEXT;
        setMessageContent(modelToStr(text));
    }

    /******
     * 获取文本消息
     * @return 文本消息
     */
    public String getChatText() {
        if (getMessageType() == MSG_TYPE_TEXT) {
            return strToModel(String.class);
        }
        return null;
    }


    /******
     * 设置图片消息
     * @param chatImage 图片消息
     */
    public void setChatImage(ChatImage chatImage) {
        messageType = ChatMessage.MSG_TYPE_IMG;
        setMessageContent(modelToStr(chatImage));
    }

    /******
     * 获取图片消息
     * @return 图片消息
     */
    public ChatImage getChatImage() {
        if (getMessageType() == MSG_TYPE_IMG) {
            return strToModel(ChatImage.class);
        }
        return null;
    }

    /******
     * 设置语音消息
     * @param chatVoice 语音消息
     */
    public void setChatVoice(ChatVoice chatVoice) {
        messageType = ChatMessage.MSG_TYPE_VOICE;
        setMessageContent(modelToStr(chatVoice));
    }

    /******
     * 获取语音消息
     * @return 语音消息
     */
    public ChatVoice getChatVoice() {
        if (getMessageType() == MSG_TYPE_VOICE) {
            return strToModel(ChatVoice.class);
        }
        return null;
    }

    /******
     * 设置定位消息
     * @param chatLocation 定位消息
     */
    public void setChatLocation(ChatLocation chatLocation) {
        messageType = ChatMessage.MSG_TYPE_LOCATE;
        setMessageContent(modelToStr(chatLocation));
    }

    /******
     * 获取定位消息
     * @return 定位消息
     */
    public ChatLocation getChatLocation() {
        //获取位置消息
        if (getMessageType() == MSG_TYPE_LOCATE) {
            return strToModel(ChatLocation.class);
        }
        return null;
    }

    /******
     * 设置视频消息
     * @param chatVideo 视频消息
     */
    public void setChatVideo(ChatVideo chatVideo) {
        messageType = ChatMessage.MSG_TYPE_VIDEO;
        setMessageContent(modelToStr(chatVideo));
    }

    /******
     * 获取视频消息
     * @return 视频消息
     */
    public ChatVideo getChatVideo() {
        if (getMessageType() == MSG_TYPE_VIDEO) {
            return strToModel(ChatVideo.class);
        }
        return null;
    }

    /******
     * 设置文件消息
     * @param chatFile 文件消息
     */
    public void setChatFile(ChatFile chatFile) {
        messageType = ChatMessage.MSG_TYPE_FILE;
        setMessageContent(modelToStr(chatFile));
    }

    /******
     * 获取文件消息
     * @return 文件消息
     */
    public ChatFile getChatFile() {
        if (getMessageType() == MSG_TYPE_FILE) {
            return strToModel(ChatFile.class);
        }
        return null;
    }

    /******
     * 设置自定义消息
     * @param text 自定义消息
     */
    public void setChatCustom(String text) {
        messageType = ChatMessage.MSG_TYPE_CUSTOM;
        setMessageContent(modelToStr(text));
    }

    /******
     * 获取自定义消息
     * @return 自定义消息
     */
    public String getChatCustom() {
        if (getMessageType() == MSG_TYPE_CUSTOM) {
            return strToModel(String.class);
        }
        return null;
    }


    /*******
     * 加密数据
     * @param data   数据
     * @return 加密字符串
     * @param <T> 类型
     */
    private <T> String modelToStr(T data) {
        return GsonTool.modelToJsonStr(data);
    }

    /*******
     * 解密数据
     * @param tClass 类型
     * @return 加密数据
     */
    private <T> T strToModel(Class<T> tClass) {
        try {
            return GsonTool.jsonStrToModel(getMessageContent(), tClass);
        } catch (Exception exception) {
            return null;
        }
    }


    /******
     * Flappy.Message          转换为ChatMessage
     * @param msg              protoc消息
     * @param channelSecret    通道秘钥
     */
    public ChatMessage(Flappy.Message msg, String channelSecret) {


        ///ID区
        messageId = msg.getMessageId();
        messageSessionId = String.valueOf(msg.getMessageSessionId());
        messageSessionType = msg.getMessageSessionType();
        messageSessionOffset = msg.getMessageSessionOffset();
        messageTableOffset = msg.getMessageTableOffset();

        ///发送接收区
        messageType = msg.getMessageType();
        messageSendId = Long.toString(msg.getMessageSendId());
        messageSendExtendId = msg.getMessageSendExtendId();
        messageReceiveId = Long.toString(msg.getMessageReceiveId());
        messageReceiveExtendId = msg.getMessageReceiveExtendId();

        ///状态区
        messageSendState = msg.getMessageSendState();
        messageReadState = msg.getMessageReadState();
        messagePinState = msg.getMessagePinState();


        ///回复区
        messageReplyMsgId = msg.getMessageReplyMsgId();
        messageReplyMsgType = msg.getMessageReplyMsgType();
        messageReplyUserId = msg.getMessageReplyUserId();
        messageForwardTitle = msg.getMessageForwardTitle();


        ///操作ID区
        messageRecallUserId = msg.getMessageRecallUserId();
        messageAtUserIds = msg.getMessageAtUserIds();
        messageReadUserIds = msg.getMessageReadUserIds();
        messageDeleteUserIds = msg.getMessageDeleteUserIds();

        ///基本区
        messageDate = TimeTool.strToDate(msg.getMessageDate());
        isDelete = msg.getIsDelete();
        deleteDate = TimeTool.strToDate(msg.getDeleteDate());

        //解析秘钥及数据
        messageSecret = AESTool.DecryptECBNoThrow(
                msg.getMessageSecret(),
                channelSecret
        );
        messageContent = AESTool.DecryptECBNoThrow(
                msg.getMessageContent(),
                messageSecret
        );
        messageReplyMsgContent = AESTool.DecryptECBNoThrow(
                msg.getMessageReplyMsgContent(),
                messageSecret
        );

    }


    /******
     * 转换为protoc消息
     * @param msgBuilder     消息builder
     * @param channelSecret  通道秘钥
     * @return 加密为Protoc消息
     */
    public Flappy.Message toProtocMessage(Flappy.Message.Builder msgBuilder, String channelSecret) {
        //转换消息
        if (getMessageId() != null)
            msgBuilder.setMessageId(getMessageId());
        if (getMessageSessionId() != null)
            msgBuilder.setMessageSessionId(StringTool.strToLong(getMessageSessionId()));
        if (getMessageSessionType() != null)
            msgBuilder.setMessageSessionType(getMessageSessionType());
        if (getMessageSessionOffset() != null)
            msgBuilder.setMessageSessionOffset(getMessageSessionOffset());
        if (getMessageTableOffset() != null)
            msgBuilder.setMessageTableOffset(getMessageTableOffset());
        if (getMessageType() != null)
            msgBuilder.setMessageType(getMessageType());
        if (getMessageSendId() != null)
            msgBuilder.setMessageSendId(StringTool.strToLong(getMessageSendId()));
        if (getMessageSendExtendId() != null)
            msgBuilder.setMessageSendExtendId(getMessageSendExtendId());
        if (getMessageReceiveId() != null)
            msgBuilder.setMessageReceiveId(StringTool.strToLong(getMessageReceiveId()));
        if (getMessageReceiveExtendId() != null)
            msgBuilder.setMessageReceiveExtendId(getMessageReceiveExtendId());

        ///状态区
        if (getMessageSendState() != null)
            msgBuilder.setMessageSendState(getMessageSendState());
        if (getMessageReadState() != null)
            msgBuilder.setMessageReadState(getMessageReadState());
        if (getMessagePinState() != null)
            msgBuilder.setMessagePinState(getMessagePinState());


        ///回复区
        if (getMessageReplyMsgId() != null)
            msgBuilder.setMessageReplyMsgId(getMessageReplyMsgId());
        if (getMessageReplyMsgType() != null)
            msgBuilder.setMessageReplyMsgType(getMessageReplyMsgType());
        if (getMessageReplyUserId() != null)
            msgBuilder.setMessageReplyUserId(getMessageReplyUserId());
        if (getMessageForwardTitle() != null)
            msgBuilder.setMessageForwardTitle(getMessageForwardTitle());

        ///ID区
        if (getMessageRecallUserId() != null)
            msgBuilder.setMessageRecallUserId(getMessageRecallUserId());
        if (getMessageAtUserIds() != null)
            msgBuilder.setMessageAtUserIds(getMessageAtUserIds());
        if (getMessageReadUserIds() != null)
            msgBuilder.setMessageReadUserIds(getMessageReadUserIds());
        if (getMessageDeleteUserIds() != null)
            msgBuilder.setMessageDeleteUserIds(getMessageDeleteUserIds());

        ///基本
        if (getMessageDate() != null)
            msgBuilder.setMessageDate(TimeTool.dateToStr(getMessageDate()));
        if (getIsDelete() != null)
            msgBuilder.setIsDelete(getIsDelete());
        if (getDeleteDate() != null)
            msgBuilder.setDeleteDate(TimeTool.dateToStr(getDeleteDate()));

        //生成临时秘钥,并加密内容
        if (getMessageContent() != null) {
            msgBuilder.setMessageContent(
                    AESTool.EncryptECBNoThrow(getMessageContent(), getMessageSecret())
            );
        }
        if (getMessageReplyMsgContent() != null) {
            msgBuilder.setMessageReplyMsgContent(
                    AESTool.EncryptECBNoThrow(getMessageReplyMsgContent(), getMessageSecret())
            );
        }
        if (getMessageSecret() != null) {
            msgBuilder.setMessageSecret(
                    AESTool.EncryptECBNoThrow(getMessageSecret(), channelSecret)
            );
        }
        return msgBuilder.build();
    }

}