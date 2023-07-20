package com.flappygo.flappyim.DataBase;


import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Handler.HandlerSession;
import com.flappygo.flappyim.Models.Server.ChatUser;
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

    //数据库db
    private SQLiteDatabase db;

    //数据库helper
    public DatabaseHelper dbHelper;

    //数据库操作锁
    private static final byte[] lock = new byte[1];

    /*******
     * 创建数据库
     */
    public Database() {
        dbHelper = new DatabaseHelper(
                FlappyImService.getInstance().getAppContext(),
                DataBaseConfig.DB_NAME,
                null,
                DataBaseConfig.DB_VERSION
        );
        db = dbHelper.getWritableDatabase();
    }

    /******
     * 关闭的时候关闭helper和数据库，并置空
     */
    public void close() {
        db.close();
        dbHelper.close();
        db = null;
        dbHelper = null;
    }

    /*****
     * 插入单条消息
     * @param chatMessage  消息
     * @return 返回是否成功
     */
    public boolean insertMessage(ChatMessage chatMessage) {
        synchronized (lock) {

            //检查用户是否登录了
            ChatUser chatUser = DataManager.getInstance().getLoginUser();
            if (chatUser == null) {
                return false;
            }

            //检查是否有记录
            Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE, null,
                    "messageId=? and messageInsertUser=?",
                    new String[]{
                            chatMessage.getMessageId(),
                            chatUser.getUserId()
                    },
                    null,
                    null,
                    null);
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
                if (chatMessage.getMessageDate() != null) {
                    values.put("messageDate", DateTimeTool.dateToStr(chatMessage.getMessageDate()));
                }
                if (chatMessage.getIsDelete() != null) {
                    values.put("isDelete", StringTool.decimalToInt(chatMessage.getIsDelete()));
                }
                if (chatMessage.getDeleteDate() != null) {
                    values.put("deleteDate", DateTimeTool.dateToStr(chatMessage.getDeleteDate()));
                }
                values.put("messageInsertUser", chatUser.getUserId());
                //消息的时间戳
                values.put("messageStamp", System.currentTimeMillis());
                //插入消息数据
                long ret = db.insert(
                        DataBaseConfig.TABLE_MESSAGE,
                        null,
                        values
                );
                return ret > 0;
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
                long ret = db.update(
                        DataBaseConfig.TABLE_MESSAGE,
                        values,
                        "messageId=?",
                        new String[]{chatMessage.getMessageId()
                        });
                return ret > 0;
            }
        }
    }

    /*******
     * 插入一个列表的消息
     * @param messages 消息列表
     * @return 插入结果
     */
    public boolean insertMessages(List<ChatMessage> messages) {
        if (messages == null || messages.size() == 0) {
            return true;
        }
        db.beginTransaction();
        for (ChatMessage msg : messages) {
            boolean insertFlag = insertMessage(msg);
            if (!insertFlag) {
                db.endTransaction();
                return false;
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    /*********
     * 插入数据
     * @param session  会话
     * @param handlerSession 插入会话后的handler
     * @return 是否成功
     */
    public boolean insertSession(SessionData session, HandlerSession handlerSession) {
        synchronized (lock) {

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
                    new String[]{session.getSessionId(), chatUser.getUserId()},
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
                values.put("sessionInsertUser", chatUser.getUserId());
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
                values.put("sessionInsertUser", chatUser.getUserId());
                //更新消息信息
                long ret = db.update(
                        DataBaseConfig.TABLE_SESSION,
                        values,
                        "sessionId=? and sessionInsertUser=? ",
                        new String[]{session.getSessionId(), chatUser.getUserId()}
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
    }

    /**********
     * 插入多个会话
     * @param sessionDataList  会话列表
     * @param handlerSession   插入会话的handler
     * @return 是否成功
     */
    public boolean insertSessions(List<SessionData> sessionDataList, HandlerSession handlerSession) {
        if (sessionDataList == null || sessionDataList.size() == 0) {
            return true;
        }
        db.beginTransaction();
        for (SessionData sessionData : sessionDataList) {
            boolean flag = insertSession(sessionData, handlerSession);
            if (!flag) {
                db.endTransaction();
                return false;
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        return true;
    }

    //获取当前用户的会话
    @SuppressLint("Range")
    public SessionData getUserSessionByExtendID(String sessionExtendID) {
        synchronized (lock) {

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
                    new String[]{sessionExtendID, chatUser.getUserId()},
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
                info.setUsers(
                        GsonTool.jsonArrayToModels(
                                cursor.getString(cursor.getColumnIndex("users")),
                                ChatUser.class
                        )
                );
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        }
    }

    //获取用户的所有会话列表
    @SuppressLint("Range")
    public List<SessionData> getUserSessions() {
        synchronized (lock) {

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
                    new String[]{chatUser.getUserId()},
                    null,
                    null,
                    null);

            //获取数据
            List<SessionData> sessions = new ArrayList<>();
            if (cursor.moveToFirst()) {
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
                    sessions.add(info);
                    cursor.moveToNext();
                }
            }
            ///关闭句柄
            cursor.close();
            return sessions;
        }
    }


    //获取所有的消息
    @SuppressLint("Range")
    public List<ChatMessage> getAllMessages() {
        synchronized (lock) {
            List<ChatMessage> list = new ArrayList<>();
            //消息更新
            Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "messageTableSeq DESC,messageStamp DESC");
            //获取数据
            if (cursor.moveToFirst()) {
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
                    info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                    info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                    info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                    info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                    list.add(info);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return list;
        }
    }

    //获取当前这个messageTableSeq 的所有消息
    @SuppressLint("Range")
    private List<ChatMessage> getSessionSeqMessages(String messageSession, String messageTableSeq) {
        List<ChatMessage> list = new ArrayList<>();
        //获取这条消息之前的消息，并且不包含自身
        Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                null,
                "messageSession = ? and messageTableSeq = ? ",
                new String[]{messageSession, messageTableSeq},
                null,
                null,
                "messageStamp DESC");
        //获取数据
        if (cursor.moveToFirst()) {
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
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                list.add(info);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;
    }


    //获取会话之前的消息列表
    @SuppressLint("Range")
    public List<ChatMessage> getSessionLatestMessage(String messageSession, String messageID, int size) {

        //首先查询到这个消息
        ChatMessage chatMessage = getMessageByID(messageID);

        //加锁
        synchronized (lock) {
            //返回的列表
            List<ChatMessage> chatMessages = new ArrayList<>();
            //然后查询出与它相同的数据
            List<ChatMessage> sessionSeqMessages = getSessionSeqMessages(
                    messageSession,
                    chatMessage.getMessageTableSeq().toString()
            );
            //之前的消息
            for (int s = 0; s < sessionSeqMessages.size(); s++) {
                if (sessionSeqMessages.get(s).getMessageStamp().intValue() < chatMessage.getMessageStamp().intValue()) {
                    chatMessages.add(sessionSeqMessages.get(s));
                }
            }
            //获取此数据之前的数据列表集
            List<ChatMessage> list = new ArrayList<>();
            //获取这条消息之前的消息，并且不包含自身
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageSession = ? and messageTableSeq < ? ",
                    new String[]{messageSession, chatMessage.getMessageTableSeq().toString()},
                    null,
                    null,
                    "messageTableSeq DESC,messageStamp DESC LIMIT " + size
            );
            //获取数据
            if (cursor.moveToFirst()) {
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
                    info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                    info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                    info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                    info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                    list.add(info);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            chatMessages.addAll(list);
            if (chatMessages.size() > size) {
                chatMessages = chatMessages.subList(0, size);
            }
            return chatMessages;
        }

    }

    //更新还未处理的消息
    @SuppressLint("Range")
    public List<ChatMessage> getNotActionSystemMessage(String sessionID) {

        synchronized (lock) {

            List<ChatMessage> list = new ArrayList<>();

            //获取session中未读的系统消息
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageType = 0 and messageReadState = 0 and messageSession=?",
                    new String[]{sessionID},
                    null,
                    null,
                    "messageTableSeq DESC"
            );

            //获取数据
            if (cursor.moveToFirst()) {
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
                    info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                    info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                    info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                    info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                    list.add(info);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return list;
        }
    }

    //获取所有还未做处理的系统消息
    @SuppressLint("Range")
    public List<ChatMessage> getNotActionSystemMessage() {

        synchronized (lock) {

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
                    new String[]{chatUser.getUserId()},
                    null,
                    null,
                    "messageTableSeq DESC"
            );

            //获取数据
            if (cursor.moveToFirst()) {
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
                    info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                    info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                    info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                    info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                    list.add(info);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return list;
        }
    }


    //获取最近的一条消息
    @SuppressLint("Range")
    public ChatMessage getSessionLatestMessage(String messageSession) {

        synchronized (lock) {
            //查询最近一条消息
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageSession = ?",
                    new String[]{messageSession},
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

    }


    //获取所有的消息中最近的一条消息
    @SuppressLint("Range")
    public ChatMessage getLatestMessage() {

        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return null;
        }

        //查询最近一条消息
        synchronized (lock) {
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageInsertUser = ?",
                    new String[]{chatUser.getUserId()},
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
    }

    //通过消息ID获取消息
    @SuppressLint("Range")
    public ChatMessage getMessageByID(String messageID) {
        synchronized (lock) {
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageId = ?",
                    new String[]{messageID},
                    null,
                    null,
                    null
            );
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
    }

}
