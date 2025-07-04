package com.flappygo.flappyim.Session;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_PARSE_ERROR;

import com.flappygo.flappyim.DataBase.Models.ChatSessionData;
import com.flappygo.flappyim.Tools.Generate.IDGenerateTool;
import com.flappygo.flappyim.Callback.FlappySendCallback;
import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Models.Request.ChatLocation;
import com.flappygo.flappyim.Tools.Upload.ImageReadTool;
import com.flappygo.flappyim.Models.Request.ChatAction;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Request.ChatVideo;
import com.flappygo.flappyim.Models.Request.ChatImage;
import com.flappygo.flappyim.Models.Request.ChatVoice;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Tools.Upload.ImageReadWH;
import com.flappygo.flappyim.Models.Request.ChatFile;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.DataBase.Database;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Tools.VideoTool;

import android.media.MediaMetadataRetriever;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.io.File;

/******
 * 单聊的会话
 */
public class FlappyChatSession extends FlappyBaseSession {

    //保留的监听列表
    private final List<MessageListener> listenerList = new ArrayList<>();

    //会话
    private final ChatSessionData session;


    /******
     * 通过session data 创建
     * @param session session对象
     */
    public FlappyChatSession(ChatSessionData session) {
        this.session = session;
    }


    /******
     * 获取会话数据
     * @return 会话数据
     */
    public ChatSessionData getSession() {
        return session;
    }


    /******
     * 获取要发送的ID
     * @return 对方ID
     */
    private String getPeerID() {
        switch (getSession().getSessionType()) {
            ///群聊会话
            case ChatSessionData.TYPE_GROUP:
                return getSession().getSessionId();
            ///单聊会话
            case ChatSessionData.TYPE_SINGLE: {
                ChatUser loginUser = DataManager.getInstance().getLoginUser();
                for (ChatUser chatUser : getSession().getUsers()) {
                    if (!chatUser.getUserId().equals(loginUser.getUserId())) {
                        return chatUser.getUserId();
                    }
                }
                break;
            }
            ///系统会话
            case ChatSessionData.TYPE_SYSTEM:
                return "0";
            default:
                throw new RuntimeException("账号错误，聊天对象丢失");
        }
        return null;
    }

    /******
     * 获取对方的extendID
     * @return 对方ID
     */
    private String getPeerExtendID() {
        switch (getSession().getSessionType()) {
            ///群聊会话
            case ChatSessionData.TYPE_GROUP:
                return getSession().getSessionExtendId();
            ///单聊会话
            case ChatSessionData.TYPE_SINGLE: {
                ChatUser loginUser = DataManager.getInstance().getLoginUser();
                for (ChatUser chatUser : getSession().getUsers()) {
                    if (!chatUser.getUserId().equals(loginUser.getUserId())) {
                        return chatUser.getUserExtendId();
                    }
                }
                break;
            }
            ///系统会话
            case ChatSessionData.TYPE_SYSTEM:
                return "0";
            default:
                throw new RuntimeException("账号错误，聊天对象丢失");
        }
        return null;
    }


    /******
     * 始终都要移除它，防止内存泄漏
     */
    protected void finalize() {
        close();
    }


    /******
     * session使用完成之后，请务必关闭防止内存泄漏
     */
    public void close() {
        for (int s = 0; s < listenerList.size(); s++) {
            HolderMessageSession.getInstance().removeMessageListener(listenerList.get(s), session.getSessionId());
        }
        listenerList.clear();
    }


    /******
     * 设置消息的监听,新收到消息都会在这里
     * @param messageListener 监听
     */
    public void addMessageListener(MessageListener messageListener) {
        //添加监听
        HolderMessageSession.getInstance().addMessageListener(messageListener, session.getSessionId());
        listenerList.add(messageListener);
    }

    /******
     * 移除当前会话的监听
     * @param messageListener 监听
     */
    public void removeListener(MessageListener messageListener) {
        HolderMessageSession.getInstance().removeMessageListener(messageListener, session.getSessionId());
        listenerList.remove(messageListener);
    }

    /******
     * 发送文本消息
     * @param text  文本消息
     * @param callback 回调
     * @return 消息
     */
    public ChatMessage sendText(String text, FlappySendCallback<ChatMessage> callback) {
        return sendReplyText(text, null, callback);
    }

    /******
     * 发送文本消息
     * @param text  文本消息
     * @param replyMsg 回复的消息
     * @param callback 回调
     * @return 消息
     */
    public ChatMessage sendReplyText(String text, ChatMessage replyMsg, FlappySendCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //设置内容
        msg.setChatText(text);
        //时间
        msg.setMessageDate(new Date());
        //回复的消息
        if (replyMsg != null) {
            msg.setMessageReplyMsgId(replyMsg.getMessageId());
            msg.setMessageReplyMsgType(replyMsg.getMessageType());
            msg.setMessageReplyMsgContent(replyMsg.getMessageContent());
            msg.setMessageReplyUserId(replyMsg.getMessageSendId());
        }
        //发送消息
        sendMessage(msg, callback);
        //返回消息
        return msg;
    }

    /******
     * 发送本地图片
     * @param path     本地图片路径
     * @param callback  回调
     * @return 消息
     */
    public ChatMessage sendLocalImage(String path, final FlappySendCallback<ChatMessage> callback) {
        //创建消息
        final ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //消息
        ChatImage chatImage = new ChatImage();
        //发送地址
        chatImage.setSendPath(path);

        try {
            //图片大小
            ImageReadWH imageSize = ImageReadTool.getImageSize(path);
            //设置宽度
            chatImage.setWidth(Integer.toString(imageSize.getWidth()));
            //设置高度
            chatImage.setHeight(Integer.toString(imageSize.getHeight()));
        } catch (Exception ex) {
            callback.failure(msg, ex, Integer.parseInt(RESULT_PARSE_ERROR));
            return msg;
        }

        //设置内容
        msg.setChatImage(chatImage);
        //时间
        msg.setMessageDate(new Date());
        //上传图片并发送信息
        uploadImageAndSend(msg, callback);
        //send image
        return msg;
    }


    /******
     * 发送图片
     * @param image 图片消息体
     * @param callback 回调
     * @return 消息
     */
    public ChatMessage sendImage(ChatImage image, FlappySendCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //设置内容
        msg.setChatImage(image);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        return msg;
    }

    /******
     * 发送本地的音频
     * @param path     路径
     * @param callback 回调
     * @return 消息
     */
    public ChatMessage sendLocalVoice(String path, final FlappySendCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //创建语音
        ChatVoice chatVoice = new ChatVoice();
        //设置语音的本地地址
        chatVoice.setSendPath(path);
        //释放
        try {
            //获取音频长度
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            //在获取前，设置文件路径（应该只能是本地路径）
            retriever.setDataSource(path);
            //长度
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            //发送哦
            chatVoice.setSeconds(duration);
            //释放
            retriever.release();
        } catch (Exception ex) {
            callback.failure(msg, ex, Integer.parseInt(RESULT_PARSE_ERROR));
            return msg;
        }
        //设置内容
        msg.setChatVoice(chatVoice);
        //时间
        msg.setMessageDate(new Date());
        //上传并发送
        uploadVoiceAndSend(msg, callback);
        //返回消息体
        return msg;
    }

    /******
     * 发送语音消息
     * @param chatVoice 语音消息
     * @param callback  回调
     * @return 消息
     */
    public ChatMessage sendVoice(ChatVoice chatVoice, final FlappySendCallback<ChatMessage> callback) {
        //创建消息
        final ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //设置内容
        msg.setChatVoice(chatVoice);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        return msg;
    }


    /******
     * 发送定位信息
     * @param location 定位
     * @param callback 回调
     * @return 消息
     */
    public ChatMessage sendLocation(ChatLocation location, final FlappySendCallback<ChatMessage> callback) {
        //创建消息
        final ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //设置内容
        msg.setChatLocation(location);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        return msg;
    }

    /******
     * 发送短视频
     * @param path 本地视频地址
     * @param callback 回调
     * @return 消息
     */
    public ChatMessage sendLocalVideo(String path, final FlappySendCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //创建语音
        ChatVideo chatVideo = new ChatVideo();
        //设置语音的本地地址
        chatVideo.setSendPath(path);
        //初始化视频数据
        try {
            //获取到图片的bitmap
            VideoTool.VideoInfo info = VideoTool.getVideoInfo(FlappyImService.getInstance().getAppContext(), 512, path);
            //封面地址
            chatVideo.setCoverSendPath(info.getOverPath());
            //时长
            chatVideo.setDuration(info.getDuration());
            //宽度
            chatVideo.setWidth(info.getWidth());
            //高度
            chatVideo.setHeight(info.getHeight());
        } catch (Exception ex) {
            callback.failure(msg, ex, Integer.parseInt(RESULT_PARSE_ERROR));
            return msg;
        }
        //设置内容
        msg.setChatVideo(chatVideo);
        //时间
        msg.setMessageDate(new Date());
        //上传并发送
        uploadVideoAndSend(msg, callback);
        //返回消息体
        return msg;
    }

    /******
     * 发送视频信息
     * @param chatVideo 视频
     * @param callback  回调
     * @return 消息
     */
    public ChatMessage sendVideo(ChatVideo chatVideo, final FlappySendCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //设置内容
        msg.setChatVideo(chatVideo);
        //时间
        msg.setMessageDate(new Date());
        //上传并发送
        sendMessage(msg, callback);
        //返回消息体
        return msg;
    }


    /******
     * 发送本地的音频
     * @param path 路径
     * @param name 名称
     * @param callback 回调
     * @return 消息
     */
    public ChatMessage sendLocalFile(String path, String name, final FlappySendCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //创建语音
        ChatFile chatFile = new ChatFile();
        //设置文件的本地地址
        chatFile.setSendPath(path);
        //设置文件大小设置
        File file = new File(path);
        if (file.exists()) {
            chatFile.setFileSize(Long.toString(file.length()));
        }
        //设置文件的名称
        chatFile.setFileName(name);
        //设置内容
        msg.setChatFile(chatFile);
        //时间
        msg.setMessageDate(new Date());
        //上传并发送
        uploadFileAndSend(msg, callback);
        //返回消息体
        return msg;
    }

    /******
     * 发送文件消息
     * @param chatFile 文件消息
     * @param callback 回调
     * @return 消息
     */
    public ChatMessage sendFile(ChatFile chatFile, final FlappySendCallback<ChatMessage> callback) {
        //创建消息
        final ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //设置内容
        msg.setChatFile(chatFile);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        return msg;
    }

    /******
     * 发送消息
     * @param text 文本
     * @param callback 回调
     * @return 消息
     */
    public ChatMessage sendCustom(String text, FlappySendCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());
        //设置内容
        msg.setChatCustom(text);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        //返回消息
        return msg;
    }


    /******
     * 设置消息已读sequence
     * @param  callback 回调
     * @return 消息
     */

    public ChatMessage readSessionMessage(FlappySendCallback<ChatMessage> callback) {

        //未读为零
        if (getUnReadMessageCount() == 0) {
            if (callback != null) {
                callback.success(null);
            }
            return null;
        }

        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());

        //读取消息的action消息
        ChatAction chatAction = new ChatAction();
        chatAction.setActionType(ChatMessage.ACTION_TYPE_SESSION_READ);
        chatAction.setActionIds(new ArrayList<>(
                Arrays.asList(
                        DataManager.getInstance().getLoginUser().getUserId(),
                        session.getSessionId(),
                        getLatestMessage().getMessageTableOffset().toString()
                )
        ));

        //设置内容
        msg.setChatAction(chatAction);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        //返回消息
        return msg;
    }


    /******
     * 设置会话静音
     * @param mute  静音
     * @param callback Action回调消息
     * @return 返回消息
     */
    public ChatMessage setSessionMute(int mute, FlappySendCallback<ChatMessage> callback) {

        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());

        //读取消息的action消息
        ChatAction chatAction = new ChatAction();
        chatAction.setActionType(ChatMessage.ACTION_TYPE_SESSION_MUTE);
        chatAction.setActionIds(new ArrayList<>(
                Arrays.asList(
                        DataManager.getInstance().getLoginUser().getUserId(),
                        session.getSessionId(),
                        Integer.toString(mute)
                )
        ));

        //设置内容
        msg.setChatAction(chatAction);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        //返回消息
        return msg;
    }


    /******
     * 设置会话静音
     * @param pinned  置顶
     * @param callback Action回调消息
     * @return 返回消息
     */
    public ChatMessage setSessionPinned(int pinned, FlappySendCallback<ChatMessage> callback) {

        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());

        //读取消息的action消息
        ChatAction chatAction = new ChatAction();
        chatAction.setActionType(ChatMessage.ACTION_TYPE_SESSION_PIN);
        chatAction.setActionIds(new ArrayList<>(
                Arrays.asList(
                        DataManager.getInstance().getLoginUser().getUserId(),
                        session.getSessionId(),
                        Integer.toString(pinned)
                )
        ));

        //设置内容
        msg.setChatAction(chatAction);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        //返回消息
        return msg;
    }


    /******
     * 删除会话，删除后
     * @param permanent 永久删除
     * @param callback  回调
     * @return 删除会话的消息
     */
    public ChatMessage setSessionDelete(boolean permanent, FlappySendCallback<ChatMessage> callback) {

        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());

        //message
        ChatMessage message = getLatestMessage();
        int sessionOffset = (message != null ? message.getMessageSessionOffset().intValue() : 0);


        //读取消息的action消息
        ChatAction chatAction = new ChatAction();
        chatAction.setActionType(permanent ? ChatMessage.ACTION_TYPE_SESSION_DELETE_PERMANENT : ChatMessage.ACTION_TYPE_SESSION_DELETE_TEMP);
        chatAction.setActionIds(new ArrayList<>(
                Arrays.asList(
                        DataManager.getInstance().getLoginUser().getUserId(),
                        session.getSessionId(),
                        Long.toString(sessionOffset)
                )
        ));

        //设置内容
        msg.setChatAction(chatAction);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        //返回消息
        return msg;
    }


    /******
     * 撤回已读消息
     * @param messageId 消息ID
     * @param callback  回调
     * @return 消息
     */
    public ChatMessage recallMessageById(String messageId, FlappySendCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());

        //读取消息的action消息
        ChatAction chatAction = new ChatAction();
        chatAction.setActionType(ChatMessage.ACTION_TYPE_MSG_RECALL);
        chatAction.setActionIds(new ArrayList<>(
                Arrays.asList(
                        DataManager.getInstance().getLoginUser().getUserId(),
                        session.getSessionId(),
                        messageId
                )
        ));

        //设置内容
        msg.setChatAction(chatAction);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        //返回消息
        return msg;
    }


    /******
     * 删除消息
     * @param messageId 消息ID
     * @param callback  回调
     */
    public ChatMessage deleteMessageById(String messageId, FlappySendCallback<ChatMessage> callback) {
        //创建消息
        ChatMessage msg = new ChatMessage();
        //生成一个消息的ID
        msg.setMessageId(IDGenerateTool.generateCommonID());
        //设置
        msg.setMessageSessionId(session.getSessionId());
        //类型
        msg.setMessageSessionType(session.getSessionType());
        //发送者
        msg.setMessageSendId(DataManager.getInstance().getLoginUser().getUserId());
        //发送者
        msg.setMessageSendExtendId(DataManager.getInstance().getLoginUser().getUserExtendId());
        //接收者
        msg.setMessageReceiveId(getPeerID());
        //接收者
        msg.setMessageReceiveExtendId(getPeerExtendID());

        //读取消息的action消息
        ChatAction chatAction = new ChatAction();
        chatAction.setActionType(ChatMessage.ACTION_TYPE_MSG_DELETE);
        chatAction.setActionIds(new ArrayList<>(
                Arrays.asList(
                        DataManager.getInstance().getLoginUser().getUserId(),
                        session.getSessionId(),
                        messageId
                )
        ));

        //设置内容
        msg.setChatAction(chatAction);
        //时间
        msg.setMessageDate(new Date());
        //发送消息
        sendMessage(msg, callback);
        //返回消息
        return msg;
    }


    /******
     * 通过消息ID重发消息
     * @param messageId 消息ID
     * @param callback  回调
     */
    public void resendMessageById(String messageId, FlappySendCallback<ChatMessage> callback) {
        resendMessage(Database.getInstance().getMessageById(messageId), callback);
    }

    /******
     * 重发消息
     * @param chatMessage 消息
     * @param callback    回调
     */
    public void resendMessage(ChatMessage chatMessage, FlappySendCallback<ChatMessage> callback) {
        //文本消息
        if (chatMessage.getMessageType() == ChatMessage.MSG_TYPE_TEXT) {
            sendMessage(chatMessage, callback);
        }
        //图片消息
        else if (chatMessage.getMessageType() == ChatMessage.MSG_TYPE_IMG) {
            uploadImageAndSend(chatMessage, callback);
        }
        //语音消息
        else if (chatMessage.getMessageType() == ChatMessage.MSG_TYPE_VOICE) {
            uploadVoiceAndSend(chatMessage, callback);
        }
        //定位消息
        else if (chatMessage.getMessageType() == ChatMessage.MSG_TYPE_LOCATE) {
            sendMessage(chatMessage, callback);
        }
        //视频消息
        else if (chatMessage.getMessageType() == ChatMessage.MSG_TYPE_VIDEO) {
            uploadVideoAndSend(chatMessage, callback);
        }
        //文件消息
        else if (chatMessage.getMessageType() == ChatMessage.MSG_TYPE_FILE) {
            uploadFileAndSend(chatMessage, callback);
        }
        //自定义消息
        else if (chatMessage.getMessageType() == ChatMessage.MSG_TYPE_CUSTOM) {
            sendMessage(chatMessage, callback);
        }
    }


    /******
     * 获取最后一条消息
     * @return 消息
     */
    public ChatMessage getLatestMessage() {
        return Database.getInstance().getSessionLatestMessage(getSession().getSessionId());
    }

    /******
     * 通过消息ID获取消息
     * @return 消息
     */
    public ChatMessage getMessageById(String messageId) {
        return Database.getInstance().getMessageById(messageId);
    }

    /******
     * 获取这条消息之前的消息
     * @param messageId 消息ID
     * @param size      大小
     * @return 消息列表
     */
    public List<ChatMessage> getFormerMessages(String messageId, int size) {
        return Database.getInstance().getSessionFormerMessages(getSession().getSessionId(), messageId, size);
    }


    /******
     * 获取这条消息之后的消息
     * @param messageId 消息ID
     * @param size      大小
     * @return 消息列表
     */
    public List<ChatMessage> getNewerMessages(String messageId, int size) {
        return Database.getInstance().getSessionNewerMessages(getSession().getSessionId(), messageId, size);
    }


    /******
     * 搜索这条消息之前的消息
     * @param text        文本匹配
     * @param messageId   消息ID
     * @param size        大小
     * @return 搜索结果
     */
    public List<ChatMessage> searchTextMessage(String text, String messageId, int size) {
        return Database.getInstance().searchTextMessageList(text, getSession().getSessionId(), messageId, size);
    }


    /******
     * 搜索这条消息之前的消息
     * @param messageId   消息ID
     * @param size        大小
     * @return 搜索结果
     */
    public List<ChatMessage> searchImageMessage(String messageId, int size) {
        return Database.getInstance().searchImageMessageList(getSession().getSessionId(), messageId, size);
    }


    /******
     * 搜索这条消息之前的消息
     * @param messageId   消息ID
     * @param size        大小
     * @return 搜索结果
     */
    public List<ChatMessage> searchVideoMessage(String messageId, int size) {
        return Database.getInstance().searchVideoMessageList(getSession().getSessionId(), messageId, size);
    }


    /******
     * 搜索这条消息之前的消息
     * @param messageId   消息ID
     * @param size        大小
     * @return 搜索结果
     */
    public List<ChatMessage> searchVoiceMessage(String messageId, int size) {
        return Database.getInstance().searchVoiceMessageList(getSession().getSessionId(), messageId, size);
    }


    /******
     * 获取未读消息的数量
     * @return 未读消息数量
     */
    public int getUnReadMessageCount() {
        return Database.getInstance().getSessionMessageUnReadCount(getSession().getSessionId());
    }

}
