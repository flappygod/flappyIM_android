package com.flappygo.flappyim.DataBase;


import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_FAILURE;

import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Models.Request.ChatAction;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Handler.HandlerSession;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Handler.MessageNotifyManager;
import com.flappygo.flappyim.Tools.DateTimeTool;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Tools.StringTool;

import android.database.sqlite.SQLiteDatabase;

import com.flappygo.flappyim.FlappyImService;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.os.Message;

import java.util.List;

/******
 * 数据库操作
 */
public class Database {


    //数据库helper
    private static DatabaseHelper dbHelper;

    //数据库db
    private static SQLiteDatabase db;

    //数据库操作锁
    private static int openCount = 0;

    //单例
    private static final class InstanceHolder {
        static final Database instance = new Database();
    }

    //单例
    public static Database getInstance() {
        return Database.InstanceHolder.instance;
    }

    //单例
    private Database() {
    }

    //加锁
    private static final Object lock = new Object();

    //打开数据库
    public Database open() {
        synchronized (lock) {
            openCount++;
            if (db == null && dbHelper == null) {
                dbHelper = new DatabaseHelper(
                        FlappyImService.getInstance().getAppContext(),
                        DataBaseConfig.DB_NAME,
                        null,
                        DataBaseConfig.DB_VERSION
                );
                db = dbHelper.getWritableDatabase();
            }
            return this;
        }
    }

    //关闭数据库
    public void close() {
        synchronized (lock) {
            openCount--;
            if (openCount == 0) {
                db.close();
                dbHelper.close();
                db = null;
                dbHelper = null;
            }
        }
    }

    /******
     * 清空正在发送中的消息为发送失败
     */
    public void clearSendingMessage() {
        ContentValues values = new ContentValues();
        values.put("messageSendState", SEND_STATE_FAILURE);
        db.update(
                DataBaseConfig.TABLE_MESSAGE,
                values,
                "messageSendState = 0",
                null
        );
    }

    /******
     * 插入单条消息
     * @param chatMessage  消息
     */
    public void insertMessage(ChatMessage chatMessage) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return;
        }

        //检查是否有记录
        Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE, null,
                "messageId=? and messageInsertUser=?",
                new String[]{
                        chatMessage.getMessageId(),
                        chatUser.getUserExtendId()
                },
                null,
                null,
                null
        );

        //没有记录就插入数据
        if (!cursor.moveToFirst()) {
            cursor.close();
            ContentValues values = new ContentValues();
            if (chatMessage.getMessageId() != null) {
                values.put("messageId", chatMessage.getMessageId());
            }
            if (chatMessage.getMessageSession() != null) {
                values.put("messageSession", chatMessage.getMessageSession());
            }
            if (chatMessage.getMessageSessionType() != null) {
                values.put("messageSessionType", StringTool.decimalToInt(chatMessage.getMessageSessionType()));
            }
            if (chatMessage.getMessageSessionOffset() != null) {
                values.put("messageSessionOffset", StringTool.decimalToInt(chatMessage.getMessageSessionOffset()));
            }
            if (chatMessage.getMessageTableSeq() != null) {
                values.put("messageTableSeq", StringTool.decimalToInt(chatMessage.getMessageTableSeq()));
            }
            if (chatMessage.getMessageType() != null) {
                values.put("messageType", StringTool.decimalToInt(chatMessage.getMessageType()));
            }
            if (chatMessage.getMessageSendId() != null) {
                values.put("messageSendId", chatMessage.getMessageSendId());
            }
            if (chatMessage.getMessageSendExtendId() != null) {
                values.put("messageSendExtendId", chatMessage.getMessageSendExtendId());
            }
            if (chatMessage.getMessageReceiveId() != null) {
                values.put("messageReceiveId", chatMessage.getMessageReceiveId());
            }
            if (chatMessage.getMessageReceiveExtendId() != null) {
                values.put("messageReceiveExtendId", chatMessage.getMessageReceiveExtendId());
            }
            if (chatMessage.getMessageContent() != null) {
                values.put("messageContent", chatMessage.getMessageContent());
            }
            if (chatMessage.getMessageSendState() != null) {
                values.put("messageSendState", StringTool.decimalToInt(chatMessage.getMessageSendState()));
            }
            if (chatMessage.getMessageReadState() != null) {
                values.put("messageReadState", StringTool.decimalToInt(chatMessage.getMessageReadState()));
            }
            if (chatMessage.getMessageSecretSend() != null) {
                values.put("messageSecretSend", chatMessage.getMessageSecretSend());
            }
            if (chatMessage.getMessageSecretReceive() != null) {
                values.put("messageSecretReceive", chatMessage.getMessageSecretReceive());
            }
            if (chatMessage.getMessageDate() != null) {
                values.put("messageDate", DateTimeTool.dateToStr(chatMessage.getMessageDate()));
            }
            if (chatMessage.getIsDelete() != null) {
                values.put("isDelete", StringTool.decimalToInt(chatMessage.getIsDelete()));
            }
            if (chatMessage.getDeleteDate() != null) {
                values.put("deleteDate", DateTimeTool.dateToStr(chatMessage.getDeleteDate()));
            }
            values.put("messageInsertUser", chatUser.getUserExtendId());
            //消息的时间戳
            values.put("messageStamp", System.currentTimeMillis());
            //插入消息数据
            db.insert(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    values
            );
        }
        ///已经存在记录就更新数据
        else {
            cursor.close();
            ContentValues values = new ContentValues();
            if (chatMessage.getMessageId() != null) {
                values.put("messageId", chatMessage.getMessageId());
            }
            if (chatMessage.getMessageSession() != null) {
                values.put("messageSession", chatMessage.getMessageSession());
            }
            if (chatMessage.getMessageSessionType() != null) {
                values.put("messageSessionType", StringTool.decimalToInt(chatMessage.getMessageSessionType()));
            }
            if (chatMessage.getMessageSessionOffset() != null) {
                values.put("messageSessionOffset", StringTool.decimalToInt(chatMessage.getMessageSessionOffset()));
            }
            if (chatMessage.getMessageTableSeq() != null) {
                values.put("messageTableSeq", StringTool.decimalToInt(chatMessage.getMessageTableSeq()));
            }
            if (chatMessage.getMessageType() != null) {
                values.put("messageType", StringTool.decimalToInt(chatMessage.getMessageType()));
            }
            if (chatMessage.getMessageSendId() != null) {
                values.put("messageSendId", chatMessage.getMessageSendId());
            }
            if (chatMessage.getMessageSendExtendId() != null) {
                values.put("messageSendExtendId", chatMessage.getMessageSendExtendId());
            }
            if (chatMessage.getMessageReceiveId() != null) {
                values.put("messageReceiveId", chatMessage.getMessageReceiveId());
            }
            if (chatMessage.getMessageReceiveExtendId() != null) {
                values.put("messageReceiveExtendId", chatMessage.getMessageReceiveExtendId());
            }
            if (chatMessage.getMessageContent() != null) {
                values.put("messageContent", chatMessage.getMessageContent());
            }
            if (chatMessage.getMessageSendState() != null) {
                values.put("messageSendState", StringTool.decimalToInt(chatMessage.getMessageSendState()));
            }
            if (chatMessage.getMessageReadState() != null) {
                values.put("messageReadState", StringTool.decimalToInt(chatMessage.getMessageReadState()));
            }
            if (chatMessage.getMessageSecretSend() != null) {
                values.put("messageSecretSend", chatMessage.getMessageSecretSend());
            }
            if (chatMessage.getMessageSecretReceive() != null) {
                values.put("messageSecretReceive", chatMessage.getMessageSecretReceive());
            }
            if (chatMessage.getMessageDate() != null) {
                values.put("messageDate", DateTimeTool.dateToStr(chatMessage.getMessageDate()));
            }
            if (chatMessage.getIsDelete() != null) {
                values.put("isDelete", StringTool.decimalToInt(chatMessage.getIsDelete()));
            }
            if (chatMessage.getDeleteDate() != null) {
                values.put("deleteDate", DateTimeTool.dateToStr(chatMessage.getDeleteDate()));
            }
            //更新消息信息
            db.update(
                    DataBaseConfig.TABLE_MESSAGE,
                    values,
                    "messageId=?",
                    new String[]{chatMessage.getMessageId()
                    });
        }
    }


    /******
     * 处理动作消息
     * @param chatMessage 消息
     */
    public void handleActionMessageUpdate(ChatMessage chatMessage) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return;
        }
        //不是动作类型
        if (chatMessage.getMessageType().intValue() != ChatMessage.MSG_TYPE_ACTION) {
            return;
        }
        ChatAction action = chatMessage.getChatAction();
        switch (action.getActionType()) {
            //消息已读
            case ChatMessage.ACTION_TYPE_READ: {
                //获取TableSequence
                String userId = action.getActionIds().get(0);
                //获取会话ID
                String sessionId = action.getActionIds().get(1);
                //获取TableSequence
                String tableSequence = action.getActionIds().get(2);
                //更新消息已读
                updateMessageRead(userId, sessionId, tableSequence);
                //更新会话任务最新已读
                updateSessionMemberLatestRead(userId, sessionId, tableSequence);
                break;
            }
            //消息删除
            case ChatMessage.ACTION_TYPE_DELETE: {
                //获取会话ID
                String sessionId = action.getActionIds().get(1);
                //获取TableSequence
                String messageId = action.getActionIds().get(2);
                //删除消息
                updateMessageDelete(sessionId, messageId);
                break;
            }
        }
    }

    /******
     * 更新消息已读
     * @param userId        用户ID
     * @param sessionId     会话ID
     * @param tableSequence 表序号
     */
    private void updateMessageRead(String userId,
                                   String sessionId,
                                   String tableSequence) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return;
        }
        //设置已读消息
        ContentValues values = new ContentValues();
        //设置已读
        values.put("messageReadState", 1);
        //更新已读消息
        db.update(
                DataBaseConfig.TABLE_MESSAGE,
                values,
                "messageInsertUser=? and messageSendId!=? and messageSession=? and messageTableSeq <= ? ",
                new String[]{
                        chatUser.getUserExtendId(),
                        userId,
                        sessionId,
                        tableSequence,
                }
        );
    }

    /******
     * 更新消息已读
     * @param sessionId     会话ID
     * @param messageId     消息ID
     */
    private void updateMessageDelete(String sessionId,
                                     String messageId) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return;
        }
        //设置已读消息
        ContentValues values = new ContentValues();
        //设置已读
        values.put("isDelete", 1);
        //更新已读消息
        db.update(
                DataBaseConfig.TABLE_MESSAGE,
                values,
                "messageInsertUser=? and messageSession=? and messageId = ?",
                new String[]{
                        chatUser.getUserExtendId(),
                        sessionId,
                        messageId,
                }
        );
    }

    /******
     * 更新用户消息最近已读
     * @param userId        用户ID
     * @param sessionId     会话ID
     * @param tableSequence 表序号
     */
    private void updateSessionMemberLatestRead(String userId, String sessionId, String tableSequence) {
        //会话Data
        SessionData sessionData = getUserSessionByID(sessionId);
        //更新会话最近已读
        List<ChatUser> chatUserList = sessionData.getUsers();
        for (ChatUser user : chatUserList) {
            if (user.getUserId().equals(userId)) {
                user.setSessionMemberLatestRead(tableSequence);
            }
        }
        insertSession(sessionData, MessageNotifyManager.getInstance().getHandlerSession());
    }

    /******
     * 插入一个列表的消息
     * @param messages 消息列表
     */
    public void insertMessages(List<ChatMessage> messages) {
        if (messages == null || messages.size() == 0) {
            return;
        }
        db.beginTransaction();
        for (ChatMessage msg : messages) {
            insertMessage(msg);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /******
     * 插入数据
     * @param session        会话
     * @param handlerSession 插入会话后的handler
     * @return 是否成功
     */
    public boolean insertSession(SessionData session,
                                 HandlerSession handlerSession) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return false;
        }

        //检查是否有记录
        Cursor cursor = db.query(
                DataBaseConfig.TABLE_SESSION,
                null,
                "sessionId=? and sessionInsertUser=? ",
                new String[]{session.getSessionId(), chatUser.getUserExtendId()},
                null,
                null,
                null
        );

        //没有记录
        if (!cursor.moveToFirst()) {
            cursor.close();
            ContentValues values = new ContentValues();
            if (session.getSessionId() != null) {
                values.put("sessionId", session.getSessionId());
            }
            if (session.getSessionExtendId() != null) {
                values.put("sessionExtendId", session.getSessionExtendId());
            }
            if (session.getSessionType() != null) {
                values.put("sessionType", StringTool.decimalToInt(session.getSessionType()));
            }
            if (session.getSessionInfo() != null) {
                values.put("sessionInfo", session.getSessionInfo());
            }
            if (session.getSessionName() != null) {
                values.put("sessionName", session.getSessionName());
            }
            if (session.getSessionImage() != null) {
                values.put("sessionImage", session.getSessionImage());
            }
            if (session.getSessionOffset() != null) {
                values.put("sessionOffset", session.getSessionOffset());
            }
            if (session.getSessionStamp() != null) {
                values.put("sessionStamp", StringTool.decimalToLong(session.getSessionStamp()));
            }
            if (session.getSessionCreateDate() != null) {
                values.put("sessionCreateDate", DateTimeTool.dateToStr(session.getSessionCreateDate()));
            }
            if (session.getSessionCreateUser() != null) {
                values.put("sessionCreateUser", session.getSessionCreateUser());
            }
            if (session.getIsDelete() != null) {
                values.put("sessionDeleted", StringTool.decimalToInt(session.getIsDelete()));
            }
            if (session.getDeleteDate() != null) {
                values.put("sessionDeletedDate", DateTimeTool.dateToStr(session.getDeleteDate()));
            }
            if (session.getUsers() != null) {
                values.put("users", GsonTool.modelToString(session.getUsers(), ChatUser.class));
            }
            values.put("sessionInsertUser", chatUser.getUserExtendId());
            //插入数据
            long ret = db.insert(DataBaseConfig.TABLE_SESSION, null, values);
            if (ret > 0) {
                Message msg = new Message();
                msg.what = HandlerSession.SESSION_UPDATE;
                msg.obj = session;
                handlerSession.sendMessage(msg);
                return true;
            }
            return false;
        } else {
            cursor.close();
            ContentValues values = new ContentValues();
            if (session.getSessionType() != null) {
                values.put("sessionType", StringTool.decimalToInt(session.getSessionType()));
            }
            if (session.getSessionInfo() != null) {
                values.put("sessionInfo", session.getSessionInfo());
            }
            if (session.getSessionName() != null) {
                values.put("sessionName", session.getSessionName());
            }
            if (session.getSessionImage() != null) {
                values.put("sessionImage", session.getSessionImage());
            }
            if (session.getSessionOffset() != null) {
                values.put("sessionOffset", session.getSessionOffset());
            }
            if (session.getSessionStamp() != null) {
                values.put("sessionStamp", StringTool.decimalToLong(session.getSessionStamp()));
            }
            if (session.getSessionCreateDate() != null) {
                values.put("sessionCreateDate", DateTimeTool.dateToStr(session.getSessionCreateDate()));
            }
            if (session.getSessionCreateUser() != null) {
                values.put("sessionCreateUser", session.getSessionCreateUser());
            }
            if (session.getIsDelete() != null) {
                values.put("sessionDeleted", StringTool.decimalToInt(session.getIsDelete()));
            }
            if (session.getDeleteDate() != null) {
                values.put("sessionDeletedDate", DateTimeTool.dateToStr(session.getDeleteDate()));
            }
            if (session.getUsers() != null) {
                values.put("users", GsonTool.modelToString(session.getUsers(), ChatUser.class));
            }
            //插入者
            values.put("sessionInsertUser", chatUser.getUserExtendId());
            //更新消息信息
            long ret = db.update(
                    DataBaseConfig.TABLE_SESSION,
                    values,
                    "sessionId=? and sessionInsertUser=? ",
                    new String[]{session.getSessionId(), chatUser.getUserExtendId()}
            );
            //更新成功
            if (ret > 0) {
                Message msg = new Message();
                msg.what = HandlerSession.SESSION_UPDATE;
                msg.obj = session;
                handlerSession.sendMessage(msg);
                return true;
            }
            return false;
        }
    }

    /******
     * 插入多个会话
     * @param  sessionDataList  会话列表
     */
    public void insertSessions(List<SessionData> sessionDataList) {
        if (sessionDataList == null || sessionDataList.size() == 0) {
            return;
        }
        db.beginTransaction();
        for (SessionData sessionData : sessionDataList) {
            insertSession(sessionData, MessageNotifyManager.getInstance().getHandlerSession());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /******
     * 获取未读消息数量
     * @param sessionID 会话ID
     * @return 未读消息数量
     */
    public int getNotReadSessionMessageCountBySessionId(String sessionID) {
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        String countQuery = "SELECT COUNT(*) FROM " + DataBaseConfig.TABLE_MESSAGE +
                " WHERE messageInsertUser = ? " +
                "and messageSession = ? " +
                "and messageSendId != ? " +
                "and messageReadState = 0 " +
                "and messageType != 0 " +
                "and messageType != 8";
        Cursor cursor = db.rawQuery(countQuery, new String[]{chatUser.getUserExtendId(), sessionID, chatUser.getUserId()});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /******
     * 获取当前用户的会话
     * @param sessionId  会话ID
     * @return 会话
     */
    @SuppressLint("Range")
    public SessionData getUserSessionByID(String sessionId) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return null;
        }
        //请求数据
        Cursor cursor = db.query(
                DataBaseConfig.TABLE_SESSION,
                null,
                "sessionId=? and sessionInsertUser=? ",
                new String[]{sessionId, chatUser.getUserExtendId()},
                null,
                null,
                null
        );
        //获取数据
        if (cursor.moveToFirst()) {
            SessionData info = new SessionData();
            info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
            info.setSessionExtendId(cursor.getString(cursor.getColumnIndex("sessionExtendId")));
            info.setSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionType"))));
            info.setSessionInfo(cursor.getString(cursor.getColumnIndex("sessionInfo")));
            info.setSessionName(cursor.getString(cursor.getColumnIndex("sessionName")));
            info.setSessionImage(cursor.getString(cursor.getColumnIndex("sessionImage")));
            info.setSessionOffset(cursor.getString(cursor.getColumnIndex("sessionOffset")));
            info.setSessionStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("sessionStamp"))));
            info.setSessionCreateDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionCreateDate"))));
            info.setSessionCreateUser(cursor.getString(cursor.getColumnIndex("sessionCreateUser")));
            info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionDeleted"))));
            info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionDeletedDate"))));
            info.setUsers(GsonTool.jsonArrayToModels(cursor.getString(cursor.getColumnIndex("users")), ChatUser.class));
            info.setUnReadMessageCount(getNotReadSessionMessageCountBySessionId(sessionId));
            cursor.close();
            return info;
        }
        cursor.close();
        return null;
    }

    /******
     * 获取当前用户的会话
     * @param sessionExtendID  会话外部ID
     * @return 会话
     */
    @SuppressLint("Range")
    public SessionData getUserSessionByExtendID(String sessionExtendID) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return null;
        }
        //请求数据
        Cursor cursor = db.query(
                DataBaseConfig.TABLE_SESSION,
                null,
                "sessionExtendId=? and sessionInsertUser=? ",
                new String[]{sessionExtendID, chatUser.getUserExtendId()},
                null,
                null,
                null
        );
        //获取数据
        if (cursor.moveToFirst()) {
            SessionData info = new SessionData();
            info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
            info.setSessionExtendId(cursor.getString(cursor.getColumnIndex("sessionExtendId")));
            info.setSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionType"))));
            info.setSessionInfo(cursor.getString(cursor.getColumnIndex("sessionInfo")));
            info.setSessionName(cursor.getString(cursor.getColumnIndex("sessionName")));
            info.setSessionImage(cursor.getString(cursor.getColumnIndex("sessionImage")));
            info.setSessionOffset(cursor.getString(cursor.getColumnIndex("sessionOffset")));
            info.setSessionStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("sessionStamp"))));
            info.setSessionCreateDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionCreateDate"))));
            info.setSessionCreateUser(cursor.getString(cursor.getColumnIndex("sessionCreateUser")));
            info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionDeleted"))));
            info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionDeletedDate"))));
            //转换为array
            info.setUsers(GsonTool.jsonArrayToModels(cursor.getString(cursor.getColumnIndex("users")), ChatUser.class));
            info.setUnReadMessageCount(getNotReadSessionMessageCountBySessionId(info.getSessionId()));
            cursor.close();
            return info;
        }
        cursor.close();
        return null;
    }

    /******
     * 获取用户的所有会话列表
     * @return 所有的会话数据
     */
    @SuppressLint("Range")
    public List<SessionData> getUserSessions() {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return new ArrayList<>();
        }
        //获取用户的会话
        Cursor cursor = db.query(
                DataBaseConfig.TABLE_SESSION,
                null,
                "sessionInsertUser=? ",
                new String[]{chatUser.getUserExtendId()},
                null,
                null,
                null);

        //获取数据
        List<SessionData> sessions = new ArrayList<>();
        //没有就关闭
        if (!cursor.moveToFirst()) {
            cursor.close();
            return sessions;
        }
        while (!cursor.isAfterLast()) {
            SessionData info = new SessionData();
            info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
            info.setSessionExtendId(cursor.getString(cursor.getColumnIndex("sessionExtendId")));
            info.setSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionType"))));
            info.setSessionInfo(cursor.getString(cursor.getColumnIndex("sessionInfo")));
            info.setSessionName(cursor.getString(cursor.getColumnIndex("sessionName")));
            info.setSessionImage(cursor.getString(cursor.getColumnIndex("sessionImage")));
            info.setSessionOffset(cursor.getString(cursor.getColumnIndex("sessionOffset")));
            info.setSessionStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("sessionStamp"))));
            info.setSessionCreateDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionCreateDate"))));
            info.setSessionCreateUser(cursor.getString(cursor.getColumnIndex("sessionCreateUser")));
            info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionDeleted"))));
            info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionDeletedDate"))));
            info.setUsers(GsonTool.jsonArrayToModels(cursor.getString(cursor.getColumnIndex("users")), ChatUser.class));
            info.setUnReadMessageCount(getNotReadSessionMessageCountBySessionId(info.getSessionId()));
            sessions.add(info);
            cursor.moveToNext();
        }
        cursor.close();
        return sessions;
    }


    /******
     * 获取当前这个messageTableSeq的所有消息
     * @param messageSession  会话ID
     * @param messageTableSeq 表序号
     * @return 获取消息，这个消息可能有多条，主要是没发成功的
     */
    @SuppressLint("Range")
    private List<ChatMessage> getSessionSeqMessages(String messageSession, String messageTableSeq) {

        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return new ArrayList<>();
        }
        List<ChatMessage> list = new ArrayList<>();
        //获取这条消息之前的消息，并且不包含自身
        Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                null,
                "messageSession = ? and messageTableSeq = ? and messageInsertUser = ? and messageType !=8 ",
                new String[]{
                        messageSession,
                        messageTableSeq,
                        chatUser.getUserExtendId()
                },
                null,
                null,
                "messageStamp DESC");
        //没有就关闭
        if (!cursor.moveToFirst()) {
            cursor.close();
            return list;
        }
        while (!cursor.isAfterLast()) {
            ChatMessage info = new ChatMessage();
            info.setMessageId(cursor.getString(cursor.getColumnIndex("messageId")));
            info.setMessageSession(cursor.getString(cursor.getColumnIndex("messageSession")));
            info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionType"))));
            info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionOffset"))));
            info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageTableSeq"))));
            info.setMessageType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageType"))));
            info.setMessageSendId(cursor.getString(cursor.getColumnIndex("messageSendId")));
            info.setMessageSendExtendId(cursor.getString(cursor.getColumnIndex("messageSendExtendId")));
            info.setMessageReceiveId(cursor.getString(cursor.getColumnIndex("messageReceiveId")));
            info.setMessageReceiveExtendId(cursor.getString(cursor.getColumnIndex("messageReceiveExtendId")));
            info.setMessageContent(cursor.getString(cursor.getColumnIndex("messageContent")));
            info.setMessageSendState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSendState"))));
            info.setMessageReadState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageReadState"))));

            info.setMessageSecretSend(cursor.getString(cursor.getColumnIndex("messageSecretSend")));
            info.setMessageSecretReceive(cursor.getString(cursor.getColumnIndex("messageSecretReceive")));

            info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
            info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
            info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
            info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
            list.add(info);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    //获取会话之前的消息列表
    @SuppressLint("Range")
    public List<ChatMessage> getSessionFormerMessages(String messageSession, String messageID, int size) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return new ArrayList<>();
        }
        //首先查询所有的这个seq的消息，可能有很多发送失败的消息，而这里的消息也是经过排序好的
        ChatMessage chatMessage = getMessageByID(messageID, false);
        List<ChatMessage> chatMessages = new ArrayList<>();
        List<ChatMessage> sessionSeqMessages = getSessionSeqMessages(
                messageSession,
                chatMessage.getMessageTableSeq().toString()
        );
        for (int s = 0; s < sessionSeqMessages.size(); s++) {
            if (sessionSeqMessages.get(s).getMessageStamp().intValue() < chatMessage.getMessageStamp().intValue()) {
                chatMessages.add(sessionSeqMessages.get(s));
            }
        }
        //获取此数据之前的数据列表集
        List<ChatMessage> list = new ArrayList<>();
        Cursor cursor = db.query(
                DataBaseConfig.TABLE_MESSAGE,
                null,
                "messageSession = ? and messageTableSeq < ? and messageInsertUser = ? and messageType != 8",
                new String[]{
                        messageSession,
                        chatMessage.getMessageTableSeq().toString(),
                        chatUser.getUserExtendId(),
                },
                null,
                null,
                "messageTableSeq DESC,messageStamp DESC LIMIT " + size
        );
        //没有就关闭
        if (!cursor.moveToFirst()) {
            cursor.close();
            return list;
        }
        //获取数据
        while (!cursor.isAfterLast() && list.size() < size) {
            ChatMessage info = new ChatMessage();
            info.setMessageId(cursor.getString(cursor.getColumnIndex("messageId")));
            info.setMessageSession(cursor.getString(cursor.getColumnIndex("messageSession")));
            info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionType"))));
            info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionOffset"))));
            info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageTableSeq"))));
            info.setMessageType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageType"))));
            info.setMessageSendId(cursor.getString(cursor.getColumnIndex("messageSendId")));
            info.setMessageSendExtendId(cursor.getString(cursor.getColumnIndex("messageSendExtendId")));
            info.setMessageReceiveId(cursor.getString(cursor.getColumnIndex("messageReceiveId")));
            info.setMessageReceiveExtendId(cursor.getString(cursor.getColumnIndex("messageReceiveExtendId")));
            info.setMessageContent(cursor.getString(cursor.getColumnIndex("messageContent")));
            info.setMessageSendState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSendState"))));
            info.setMessageReadState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageReadState"))));

            info.setMessageSecretSend(cursor.getString(cursor.getColumnIndex("messageSecretSend")));
            info.setMessageSecretReceive(cursor.getString(cursor.getColumnIndex("messageSecretReceive")));

            info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
            info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
            info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
            info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
            list.add(info);
            cursor.moveToNext();
        }
        cursor.close();
        chatMessages.addAll(list);
        if (chatMessages.size() > size) {
            chatMessages = chatMessages.subList(0, size);
        }
        return chatMessages;
    }


    //获取最近的一条消息
    @SuppressLint("Range")
    public ChatMessage getSessionLatestMessage(String messageSession) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return null;
        }
        //查询最近一条消息
        Cursor cursor = db.query(
                DataBaseConfig.TABLE_MESSAGE,
                null,
                "messageSession = ? and messageInsertUser = ? and messageType != 8",
                new String[]{messageSession, chatUser.getUserExtendId()},
                null,
                null,
                "messageTableSeq DESC,messageStamp DESC LIMIT 1"
        );
        //获取数据
        if (cursor.moveToFirst()) {
            ChatMessage info = new ChatMessage();
            info.setMessageId(cursor.getString(cursor.getColumnIndex("messageId")));
            info.setMessageSession(cursor.getString(cursor.getColumnIndex("messageSession")));
            info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionType"))));
            info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionOffset"))));
            info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageTableSeq"))));
            info.setMessageType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageType"))));
            info.setMessageSendId(cursor.getString(cursor.getColumnIndex("messageSendId")));
            info.setMessageSendExtendId(cursor.getString(cursor.getColumnIndex("messageSendExtendId")));
            info.setMessageReceiveId(cursor.getString(cursor.getColumnIndex("messageReceiveId")));
            info.setMessageReceiveExtendId(cursor.getString(cursor.getColumnIndex("messageReceiveExtendId")));
            info.setMessageContent(cursor.getString(cursor.getColumnIndex("messageContent")));
            info.setMessageSendState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSendState"))));
            info.setMessageReadState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageReadState"))));

            info.setMessageSecretSend(cursor.getString(cursor.getColumnIndex("messageSecretSend")));
            info.setMessageSecretReceive(cursor.getString(cursor.getColumnIndex("messageSecretReceive")));

            info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
            info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
            info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
            info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
            cursor.close();
            return info;
        }
        cursor.close();
        return null;
    }

    /******
     * 通过消息ID获取消息
     * @param messageID 消息ID
     * @return 消息
     */
    @SuppressLint("Range")
    public ChatMessage getMessageByID(String messageID, boolean showActionMsg) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return null;
        }
        Cursor cursor;
        //获取当前用户的消息
        if (showActionMsg) {
            cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageId = ? and messageInsertUser = ?",
                    new String[]{messageID, chatUser.getUserExtendId()},
                    null,
                    null,
                    null
            );
        } else {
            cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageId = ? and messageInsertUser = ? and messageType != 8",
                    new String[]{messageID, chatUser.getUserExtendId()},
                    null,
                    null,
                    null
            );
        }
        //获取
        if (cursor.moveToFirst()) {
            ChatMessage info = new ChatMessage();
            info.setMessageId(cursor.getString(cursor.getColumnIndex("messageId")));
            info.setMessageSession(cursor.getString(cursor.getColumnIndex("messageSession")));
            info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionType"))));
            info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionOffset"))));
            info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageTableSeq"))));
            info.setMessageType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageType"))));
            info.setMessageSendId(cursor.getString(cursor.getColumnIndex("messageSendId")));
            info.setMessageSendExtendId(cursor.getString(cursor.getColumnIndex("messageSendExtendId")));
            info.setMessageReceiveId(cursor.getString(cursor.getColumnIndex("messageReceiveId")));
            info.setMessageReceiveExtendId(cursor.getString(cursor.getColumnIndex("messageReceiveExtendId")));
            info.setMessageContent(cursor.getString(cursor.getColumnIndex("messageContent")));
            info.setMessageSendState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSendState"))));
            info.setMessageReadState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageReadState"))));

            info.setMessageSecretSend(cursor.getString(cursor.getColumnIndex("messageSecretSend")));
            info.setMessageSecretReceive(cursor.getString(cursor.getColumnIndex("messageSecretReceive")));

            info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
            info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
            info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
            info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
            cursor.close();
            return info;
        }
        cursor.close();
        return null;
    }


    //更新还未处理的消息
    @SuppressLint("Range")
    public List<ChatMessage> getNotActionSystemMessageBySession(String sessionID) {

        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return new ArrayList<>();
        }
        //获取session中未读的系统消息
        Cursor cursor = db.query(
                DataBaseConfig.TABLE_MESSAGE,
                null,
                "messageType = 0 and messageReadState = 0 and messageSession = ? and messageInsertUser = ?",
                new String[]{sessionID, chatUser.getUserExtendId()},
                null,
                null,
                "messageTableSeq DESC"
        );
        //获取数据
        List<ChatMessage> list = new ArrayList<>();
        //没有就关闭
        if (!cursor.moveToFirst()) {
            cursor.close();
            return list;
        }
        //获取所有数据
        while (!cursor.isAfterLast()) {
            ChatMessage info = new ChatMessage();
            info.setMessageId(cursor.getString(cursor.getColumnIndex("messageId")));
            info.setMessageSession(cursor.getString(cursor.getColumnIndex("messageSession")));
            info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionType"))));
            info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionOffset"))));
            info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageTableSeq"))));
            info.setMessageType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageType"))));
            info.setMessageSendId(cursor.getString(cursor.getColumnIndex("messageSendId")));
            info.setMessageSendExtendId(cursor.getString(cursor.getColumnIndex("messageSendExtendId")));
            info.setMessageReceiveId(cursor.getString(cursor.getColumnIndex("messageReceiveId")));
            info.setMessageReceiveExtendId(cursor.getString(cursor.getColumnIndex("messageReceiveExtendId")));
            info.setMessageContent(cursor.getString(cursor.getColumnIndex("messageContent")));
            info.setMessageSendState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSendState"))));
            info.setMessageReadState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageReadState"))));

            info.setMessageSecretSend(cursor.getString(cursor.getColumnIndex("messageSecretSend")));
            info.setMessageSecretReceive(cursor.getString(cursor.getColumnIndex("messageSecretReceive")));

            info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
            info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
            info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
            info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
            list.add(info);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    //获取所有还未做处理的系统消息
    @SuppressLint("Range")
    public List<ChatMessage> getNotActionSystemMessage() {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return new ArrayList<>();
        }
        //当前用户的消息拿出来
        List<ChatMessage> list = new ArrayList<>();
        //获取这条消息之前的消息，并且不包含自身
        Cursor cursor = db.query(
                DataBaseConfig.TABLE_MESSAGE,
                null,
                "messageType = 0 and messageReadState = 0 and messageInsertUser = ?",
                new String[]{chatUser.getUserExtendId()},
                null,
                null,
                "messageTableSeq DESC"
        );
        //没有就关闭
        if (!cursor.moveToFirst()) {
            cursor.close();
            return list;
        }
        //获取所有数据
        while (!cursor.isAfterLast()) {
            ChatMessage info = new ChatMessage();
            info.setMessageId(cursor.getString(cursor.getColumnIndex("messageId")));
            info.setMessageSession(cursor.getString(cursor.getColumnIndex("messageSession")));
            info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionType"))));
            info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSessionOffset"))));
            info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageTableSeq"))));
            info.setMessageType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageType"))));
            info.setMessageSendId(cursor.getString(cursor.getColumnIndex("messageSendId")));
            info.setMessageSendExtendId(cursor.getString(cursor.getColumnIndex("messageSendExtendId")));
            info.setMessageReceiveId(cursor.getString(cursor.getColumnIndex("messageReceiveId")));
            info.setMessageReceiveExtendId(cursor.getString(cursor.getColumnIndex("messageReceiveExtendId")));
            info.setMessageContent(cursor.getString(cursor.getColumnIndex("messageContent")));
            info.setMessageSendState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageSendState"))));
            info.setMessageReadState(new BigDecimal(cursor.getInt(cursor.getColumnIndex("messageReadState"))));

            info.setMessageSecretSend(cursor.getString(cursor.getColumnIndex("messageSecretSend")));
            info.setMessageSecretReceive(cursor.getString(cursor.getColumnIndex("messageSecretReceive")));

            info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
            info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
            info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
            info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
            list.add(info);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
}
