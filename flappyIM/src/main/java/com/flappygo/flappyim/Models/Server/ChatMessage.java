package com.flappygo.flappyim.Models.Server;


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

import org.json.JSONException;
import java.math.BigDecimal;
import android.util.Base64;
import org.json.JSONObject;
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


    /***信息更新***/
    //什么也不做
    public final static int SYSTEM_MSG_NOTHING = 0;

    //更新单条会话(所有数据)
    public final static int SYSTEM_MSG_UPDATE_SESSION = 1;

    //用户信息更新(获取)
    public final static int SYSTEM_MSG_UPDATE_MEMBER = 2;

    //用户信息更新(删除)
    public final static int SYSTEM_MSG_DELETE_MEMBER = 3;

    //用户信息增加(获取)
    public final static int SYSTEM_MSG_ADD_MEMBER = 4;


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
    public ChatMessage(Flappy.Message msg, String secret) {
        messageId = msg.getMessageId();
        messageSession = String.valueOf(msg.getMessageSession());
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

        ///不为空时设置
        messageSecretSend = msg.getMessageSecretSend();
        if (!StringTool.isEmpty(msg.getMessageSecretSend())) {
            try {
                messageSecretSend = AESTool.DecryptECB(
                        msg.getMessageSecretSend(),
                        secret
                );
            } catch (Exception ex) {
                messageSecretSend = msg.getMessageSecretSend();
            }
        }

        isDelete = new BigDecimal(msg.getIsDelete());
        messageDate = TimeTool.strToDate(msg.getMessageDate());
        deleteDate = TimeTool.strToDate(msg.getDeleteDate());
    }


    ///转换为json数据
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("messageId",messageId);
        jsonObject.put("messageSession",messageSession);
        jsonObject.put("messageSessionType",messageSessionType);
        jsonObject.put("messageSessionOffset",messageSessionOffset);
        jsonObject.put("messageTableSeq",messageTableSeq);
        jsonObject.put("messageType",messageType);
        jsonObject.put("messageSendId",messageSendId);
        jsonObject.put("messageSendExtendId",messageSendExtendId);
        jsonObject.put("messageReceiveId",messageReceiveId);
        jsonObject.put("messageReceiveExtendId",messageReceiveExtendId);
        jsonObject.put("messageSendState",messageSendState);
        jsonObject.put("messageReadState",messageReadState);
        jsonObject.put("messageSecretSend",messageSecretSend);
        jsonObject.put("messageContent",messageContent);

        ///转换为json数据
        switch (messageType.intValue()){
            case  MSG_TYPE_SYSTEM:
                jsonObject.put("messageData",GsonTool.modelToString(getChatSystem(),ChatSystem.class));
                break;
            case  MSG_TYPE_TEXT:
                jsonObject.put("messageData",GsonTool.modelToString(getChatText(),String.class));
                break;
            case  MSG_TYPE_IMG:
                jsonObject.put("messageData",GsonTool.modelToString(getChatImage(),ChatImage.class));
                break;
            case  MSG_TYPE_VOICE:
                jsonObject.put("messageData",GsonTool.modelToString(getChatVoice(),ChatVoice.class));
                break;
            case  MSG_TYPE_LOCATE:
                jsonObject.put("messageData",GsonTool.modelToString(getChatLocation(),ChatLocation.class));
                break;
            case  MSG_TYPE_VIDEO:
                jsonObject.put("messageData",GsonTool.modelToString(getChatVideo(),ChatVideo.class));
                break;
            case  MSG_TYPE_FILE:
                jsonObject.put("messageData",GsonTool.modelToString(getChatFile(),ChatFile.class));
                break;
            case  MSG_TYPE_CUSTOM:
                jsonObject.put("messageData",GsonTool.modelToString(getChatCustom(),String.class));
                break;
            case  MSG_TYPE_ACTION:
                jsonObject.put("messageData",GsonTool.modelToString(getChatAction(),ChatAction.class));
                break;
        }

        jsonObject.put("isDelete",isDelete);
        jsonObject.put("messageDate",messageDate);
        jsonObject.put("deleteDate",deleteDate);
        return jsonObject;
    }



    //转换为protoc消息
    public Flappy.Message toProtocMessage(Flappy.Message.Builder msgBuilder, String secret) {
        //转换消息
        if (getMessageId() != null)
            msgBuilder.setMessageId(getMessageId());
        if (getMessageSession() != null)
            msgBuilder.setMessageSession(StringTool.strToLong(getMessageSession()));
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
        if (!StringTool.isEmpty(getMessageSecretSend())) {
            try {
                msgBuilder.setMessageSecretSend(AESTool.EncryptECB(getMessageSecretSend(), secret));
            } catch (Exception exception) {
                msgBuilder.setMessageSecretSend(getMessageSecretSend());
            }
        }
        if (getMessageDate() != null)
            msgBuilder.setMessageDate(TimeTool.dateToStr(getMessageDate()));
        if (getIsDelete() != null)
            msgBuilder.setIsDelete(StringTool.decimalToInt(getIsDelete()));
        if (getDeleteDate() != null)
            msgBuilder.setDeleteDate(TimeTool.dateToStr(getDeleteDate()));

        return msgBuilder.build();
    }


    /******
     * 设置系统消息
     * @param chatSystem 系统消息
     */
    public void setChatSystem(ChatSystem chatSystem) {
        messageType = new BigDecimal(ChatMessage.MSG_TYPE_SYSTEM);
        setMessageContent(encrypt(chatSystem, ChatSystem.class, null));
    }

    /******
     * 获取系统消息
     * @return 系统消息
     */
    public ChatSystem getChatSystem() {
        if (getMessageType().intValue() == MSG_TYPE_SYSTEM) {
            return decrypt(ChatSystem.class);
        }
        return null;
    }

    /******
     * 设置动作消息
     * @param chatAction 动作消息
     */
    public void setChatAction(ChatAction chatAction) {
        messageType = new BigDecimal(ChatMessage.MSG_TYPE_ACTION);
        setMessageContent(encrypt(chatAction, ChatAction.class, null));
    }

    /******
     * 获取动作消息
     * @return 动作消息
     */
    public ChatAction getChatAction() {
        if (getMessageType().intValue() == MSG_TYPE_ACTION) {
            return decrypt(ChatAction.class);
        }
        return null;
    }

    /******
     * 设置文本消息
     * @param text 文本消息，加密
     */
    public void setChatText(String text) {
        String secret = IDGenerateTool.getRandomStr(16);
        messageType = new BigDecimal(ChatMessage.MSG_TYPE_TEXT);
        setMessageContent(encrypt(text, String.class, secret));
    }

    /******
     * 获取文本消息
     * @return 文本消息
     */
    public String getChatText() {
        if (getMessageType().intValue() == MSG_TYPE_TEXT) {
            return decrypt(String.class);
        }
        return null;
    }


    /******
     * 设置图片消息
     * @param chatImage 图片消息
     */
    public void setChatImage(ChatImage chatImage) {
        String secret = IDGenerateTool.getRandomStr(16);
        messageType = new BigDecimal(ChatMessage.MSG_TYPE_IMG);
        setMessageContent(encrypt(chatImage, ChatImage.class, secret));
    }

    /******
     * 获取图片消息
     * @return 图片消息
     */
    public ChatImage getChatImage() {
        if (getMessageType().intValue() == MSG_TYPE_IMG) {
            return decrypt(ChatImage.class);
        }
        return null;
    }

    /******
     * 设置语音消息
     * @param chatVoice 语音消息
     */
    public void setChatVoice(ChatVoice chatVoice) {
        String secret = IDGenerateTool.getRandomStr(16);
        messageType = new BigDecimal(ChatMessage.MSG_TYPE_VOICE);
        setMessageContent(encrypt(chatVoice, ChatVoice.class, secret));
    }

    /******
     * 获取语音消息
     * @return 语音消息
     */
    public ChatVoice getChatVoice() {
        if (getMessageType().intValue() == MSG_TYPE_VOICE) {
            return decrypt(ChatVoice.class);
        }
        return null;
    }

    /******
     * 设置定位消息
     * @param chatLocation 定位消息
     */
    public void setChatLocation(ChatLocation chatLocation) {
        String secret = IDGenerateTool.getRandomStr(16);
        messageType = new BigDecimal(ChatMessage.MSG_TYPE_LOCATE);
        setMessageContent(encrypt(chatLocation, ChatLocation.class, secret));
    }

    /******
     * 获取定位消息
     * @return 定位消息
     */
    public ChatLocation getChatLocation() {
        //获取位置消息
        if (getMessageType().intValue() == MSG_TYPE_LOCATE) {
            return decrypt(ChatLocation.class);
        }
        return null;
    }

    /******
     * 设置视频消息
     * @param chatVideo 视频消息
     */
    public void setChatVideo(ChatVideo chatVideo) {
        String secret = IDGenerateTool.getRandomStr(16);
        messageType = new BigDecimal(ChatMessage.MSG_TYPE_VIDEO);
        setMessageContent(encrypt(chatVideo, ChatVideo.class, secret));
    }

    /******
     * 获取视频消息
     * @return 视频消息
     */
    public ChatVideo getChatVideo() {
        if (getMessageType().intValue() == MSG_TYPE_VIDEO) {
            return decrypt(ChatVideo.class);
        }
        return null;
    }

    /******
     * 设置文件消息
     * @param chatFile 文件消息
     */
    public void setChatFile(ChatFile chatFile) {
        String secret = IDGenerateTool.getRandomStr(16);
        messageType = new BigDecimal(ChatMessage.MSG_TYPE_FILE);
        setMessageContent(encrypt(chatFile, ChatFile.class, secret));
    }

    /******
     * 获取文件消息
     * @return 文件消息
     */
    public ChatFile getChatFile() {
        if (getMessageType().intValue() == MSG_TYPE_FILE) {
            return decrypt(ChatFile.class);
        }
        return null;
    }

    /******
     * 设置自定义消息
     * @param text 自定义消息
     */
    public void setChatCustom(String text) {
        String secret = IDGenerateTool.getRandomStr(16);
        messageType = new BigDecimal(ChatMessage.MSG_TYPE_CUSTOM);
        setMessageContent(encrypt(text, String.class, secret));
    }

    /******
     * 获取自定义消息
     * @return 自定义消息
     */
    public String getChatCustom() {
        if (getMessageType().intValue() == MSG_TYPE_CUSTOM) {
            return decrypt(String.class);
        }
        return null;
    }


    /*******
     * 加密数据
     * @param data   数据
     * @param tClass 类对象
     * @param secret 秘钥
     * @return 加密字符串
     * @param <T> 类型
     */
    private <T> String encrypt(T data, Class<T> tClass, String secret) {
        try {
            ///设置发送秘钥
            setMessageSecretSend(secret);
            ///设置json字符串
            String jsonStr = GsonTool.modelToString(data, tClass);
            ///空的
            if (StringTool.isEmpty(secret)) {
                //默认Base64解密
                return Base64.encodeToString(jsonStr.getBytes(), Base64.DEFAULT).replace("\n", "");
            } else {
                ///加密数据
                return AESTool.EncryptECB(jsonStr, secret);
            }
        } catch (Exception exception) {
            return null;
        }
    }

    /*******
     * 解密数据
     * @param tClass 类型
     * @return 加密数据
     */
    private <T> T decrypt(Class<T> tClass) {
        try {
            ///先获取接收的秘钥
            String secret = getMessageSecretSend();
            ///获取数据
            String data = getMessageContent();
            ///没有秘钥
            if (StringTool.isEmpty(secret)) {
                String jsonData = new String(Base64.decode(data.getBytes(), Base64.DEFAULT)).replace("\n", "");
                return GsonTool.jsonStringToModel(jsonData, tClass);
            }
            ///解密数据
            String jsonData = AESTool.DecryptECB(data, secret);
            ///返回对象
            return GsonTool.jsonStringToModel(jsonData, tClass);
        } catch (Exception exception) {
            return null;
        }
    }

}