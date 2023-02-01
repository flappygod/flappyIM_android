package com.flappygo.flappyim.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Tools.DateTimeTool;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Tools.StringTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//数据库操作
public class Database {

    // 数据库db
    private SQLiteDatabase db;
    // 数据库helper
    public DatabaseHelper dbHelper;
    // 数据库操作锁
    private static final byte[] lock = new byte[1];

    //Database
    public Database() {
        dbHelper = new DatabaseHelper(FlappyImService.getInstance().getAppContext(),
                DataBaseConfig.DB_NAME,
                null,
                DataBaseConfig.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    //关闭的时候关闭helper和数据库，并置空
    public void close() {
        db.close();
        dbHelper.close();
        db = null;
        dbHelper = null;
    }

    //插入单条消息
    public boolean insertMessage(ChatMessage chatMessage) {
        synchronized (lock) {
            //检查是否有记录
            Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE, null,
                    "messageId=?",
                    new String[]{chatMessage.getMessageId()},
                    null,
                    null,
                    null);
            //没有记录
            if (!cursor.moveToFirst()) {
                cursor.close();
                ContentValues values = new ContentValues();
                if (chatMessage.getMessageId() != null)
                    values.put("messageId", chatMessage.getMessageId());
                if (chatMessage.getMessageSession() != null)
                    values.put("messageSession", chatMessage.getMessageSession());
                if (chatMessage.getMessageSessionType() != null)
                    values.put("messageSessionType", StringTool.decimalToInt(chatMessage.getMessageSessionType()));
                if (chatMessage.getMessageSessionOffset() != null)
                    values.put("messageSessionOffset", StringTool.decimalToInt(chatMessage.getMessageSessionOffset()));
                if (chatMessage.getMessageTableSeq() != null)
                    values.put("messageTableSeq", StringTool.decimalToInt(chatMessage.getMessageTableSeq()));
                if (chatMessage.getMessageType() != null)
                    values.put("messageType", StringTool.decimalToInt(chatMessage.getMessageType()));
                if (chatMessage.getMessageSendId() != null)
                    values.put("messageSendId", chatMessage.getMessageSendId());
                if (chatMessage.getMessageSendExtendId() != null)
                    values.put("messageSendExtendId", chatMessage.getMessageSendExtendId());
                if (chatMessage.getMessageReceiveId() != null)
                    values.put("messageReceiveId", chatMessage.getMessageReceiveId());
                if (chatMessage.getMessageReceiveExtendId() != null)
                    values.put("messageReceiveExtendId", chatMessage.getMessageReceiveExtendId());
                if (chatMessage.getMessageContent() != null)
                    values.put("messageContent", chatMessage.getMessageContent());
                if (chatMessage.getMessageSendState() != null)
                    values.put("messageSendState", StringTool.decimalToInt(chatMessage.getMessageSendState()));
                if (chatMessage.getMessageReadState() != null)
                    values.put("messageReadState", StringTool.decimalToInt(chatMessage.getMessageReadState()));
                if (chatMessage.getIsDelete() != null)
                    values.put("isDelete", StringTool.decimalToInt(chatMessage.getIsDelete()));
                if (chatMessage.getMessageDate() != null)
                    values.put("messageDate", DateTimeTool.dateToStr(chatMessage.getMessageDate()));
                if (chatMessage.getDeleteDate() != null)
                    values.put("deleteDate", DateTimeTool.dateToStr(chatMessage.getDeleteDate()));
                {
                    values.put("messageStamp", System.currentTimeMillis());
                }
                long ret = db.insert(DataBaseConfig.TABLE_MESSAGE,null, values);
                return ret > 0;
            } else {
                cursor.close();
                //代表消息已经发送了
                ContentValues values = new ContentValues();
                if (chatMessage.getMessageId() != null)
                    values.put("messageId", chatMessage.getMessageId());
                if (chatMessage.getMessageSession() != null)
                    values.put("messageSession", chatMessage.getMessageSession());
                if (chatMessage.getMessageSessionType() != null)
                    values.put("messageSessionType", StringTool.decimalToInt(chatMessage.getMessageSessionType()));
                if (chatMessage.getMessageSessionOffset() != null)
                    values.put("messageSessionOffset", StringTool.decimalToInt(chatMessage.getMessageSessionOffset()));
                if (chatMessage.getMessageTableSeq() != null)
                    values.put("messageTableSeq", StringTool.decimalToInt(chatMessage.getMessageTableSeq()));
                if (chatMessage.getMessageType() != null)
                    values.put("messageType", StringTool.decimalToInt(chatMessage.getMessageType()));
                if (chatMessage.getMessageSendId() != null)
                    values.put("messageSendId", chatMessage.getMessageSendId());
                if (chatMessage.getMessageSendExtendId() != null)
                    values.put("messageSendExtendId", chatMessage.getMessageSendExtendId());
                if (chatMessage.getMessageReceiveId() != null)
                    values.put("messageReceiveId", chatMessage.getMessageReceiveId());
                if (chatMessage.getMessageReceiveExtendId() != null)
                    values.put("messageReceiveExtendId", chatMessage.getMessageReceiveExtendId());
                if (chatMessage.getMessageContent() != null)
                    values.put("messageContent", chatMessage.getMessageContent());
                if (chatMessage.getMessageSendState() != null)
                    values.put("messageSendState", StringTool.decimalToInt(chatMessage.getMessageSendState()));
                if (chatMessage.getMessageReadState() != null)
                    values.put("messageReadState", StringTool.decimalToInt(chatMessage.getMessageReadState()));
                if (chatMessage.getIsDelete() != null)
                    values.put("isDelete", StringTool.decimalToInt(chatMessage.getIsDelete()));
                if (chatMessage.getMessageDate() != null)
                    values.put("messageDate", DateTimeTool.dateToStr(chatMessage.getMessageDate()));
                if (chatMessage.getDeleteDate() != null)
                    values.put("deleteDate", DateTimeTool.dateToStr(chatMessage.getDeleteDate()));
                //更新消息信息
                long ret = db.update(DataBaseConfig.TABLE_MESSAGE,values,"messageId=?", new String[]{chatMessage.getMessageId()});
                return ret > 0;
            }
        }
    }

    //插入一个列表的消息
    public boolean insertMessages(List<ChatMessage> messages) {
        if (messages == null || messages.size() == 0) {
            return true;
        }
        db.beginTransaction();
        boolean totalSuccess = true;
        for (int s = 0; s < messages.size(); s++) {
            boolean flag = insertMessage(messages.get(s));
            if (!flag) {
                totalSuccess = false;
            }
        }
        if (totalSuccess) {
            db.setTransactionSuccessful();
        }
        db.endTransaction();
        return totalSuccess;
    }

    //插入数据
    public boolean insertSession(SessionData session) {
        synchronized (lock) {

            //当前的ID
            String currentUserID = DataManager.getInstance().getLoginUser().getUserExtendId();

            //检查是否有记录
            Cursor cursor = db.query(DataBaseConfig.TABLE_SESSION, null,
                    "sessionId=? and sessionInsertUser=? ",
                    new String[]{session.getSessionId(), currentUserID},
                    null,
                    null,
                    null);

            //没有记录
            if (!cursor.moveToFirst()) {
                cursor.close();
                ContentValues values = new ContentValues();
                if (session.getSessionId() != null)
                    values.put("sessionId", session.getSessionId());
                if (session.getSessionExtendId() != null)
                    values.put("sessionExtendId", session.getSessionExtendId());
                if (session.getSessionType() != null)
                    values.put("sessionType", StringTool.decimalToInt(session.getSessionType()));
                if (session.getSessionInfo() != null)
                    values.put("sessionInfo", session.getSessionInfo());
                if (session.getSessionName() != null)
                    values.put("sessionName", session.getSessionName());
                if (session.getSessionImage() != null)
                    values.put("sessionImage", session.getSessionImage());
                if (session.getSessionOffset() != null)
                    values.put("sessionOffset", session.getSessionOffset());
                if (session.getSessionStamp() != null)
                    values.put("sessionStamp", StringTool.decimalToInt(session.getSessionStamp()));
                if (session.getSessionCreateDate() != null)
                    values.put("sessionCreateDate", DateTimeTool.dateToStr(session.getSessionCreateDate()));
                if (session.getSessionCreateUser() != null)
                    values.put("sessionCreateUser", session.getSessionCreateUser());
                if (session.getIsDelete() != null)
                    values.put("sessionDeleted", StringTool.decimalToInt(session.getIsDelete()));
                if (session.getDeleteDate() != null)
                    values.put("sessionDeletedDate", DateTimeTool.dateToStr(session.getDeleteDate()));
                if (session.getUsers() != null)
                    values.put("users", GsonTool.modelToString(session.getUsers(), ChatUser.class));
                //插入者
                values.put("sessionInsertUser", DataManager.getInstance().getLoginUser().getUserExtendId());

                long ret = db.insert(DataBaseConfig.TABLE_SESSION, null, values);
                return ret > 0;
            } else {
                cursor.close();

                ContentValues values = new ContentValues();

                if (session.getSessionType() != null)
                    values.put("sessionType", StringTool.decimalToInt(session.getSessionType()));
                if (session.getSessionInfo() != null)
                    values.put("sessionInfo", session.getSessionInfo());
                if (session.getSessionName() != null)
                    values.put("sessionName", session.getSessionName());
                if (session.getSessionImage() != null)
                    values.put("sessionImage", session.getSessionImage());
                if (session.getSessionOffset() != null)
                    values.put("sessionOffset", session.getSessionOffset());
                if (session.getSessionStamp() != null)
                    values.put("sessionStamp", StringTool.decimalToInt(session.getSessionStamp()));
                if (session.getSessionCreateDate() != null)
                    values.put("sessionCreateDate", DateTimeTool.dateToStr(session.getSessionCreateDate()));
                if (session.getSessionCreateUser() != null)
                    values.put("sessionCreateUser", session.getSessionCreateUser());
                if (session.getIsDelete() != null)
                    values.put("sessionDeleted", StringTool.decimalToInt(session.getIsDelete()));
                if (session.getDeleteDate() != null)
                    values.put("sessionDeletedDate", DateTimeTool.dateToStr(session.getDeleteDate()));
                if (session.getUsers() != null)
                    values.put("users", GsonTool.modelToString(session.getUsers(), ChatUser.class));
                //插入者
                values.put("sessionInsertUser", DataManager.getInstance().getLoginUser().getUserExtendId());

                //更新消息信息
                long ret = db.update(DataBaseConfig.TABLE_SESSION,
                        values,
                        "sessionId=? and sessionInsertUser=? ",
                        new String[]{session.getSessionId(), currentUserID}
                );

                return ret > 0;
            }
        }
    }

    //插入多个会话
    public boolean insertSessions(List<SessionData> sessionData) {

        if (sessionData == null || sessionData.size() == 0) {
            return true;
        }

        db.beginTransaction();
        boolean totalSuccess = true;
        for (int s = 0; s < sessionData.size(); s++) {
            boolean flag = insertSession(sessionData.get(s));
            if (!flag) {
                totalSuccess = false;
            }
        }
        if (totalSuccess) {
            db.setTransactionSuccessful();
        }
        db.endTransaction();
        return totalSuccess;
    }

    //获取当前用户的会话
    @SuppressLint("Range")
    public SessionData getUserSessionByExtendID(String sessionExtendID) {
        synchronized (lock) {

            Cursor cursor = db.query(DataBaseConfig.TABLE_SESSION, null,
                    "sessionExtendId=? and sessionInsertUser=? ",
                    new String[]{sessionExtendID, DataManager.getInstance().getLoginUser().getUserExtendId()}, null, null, null);

            //获取数据
            if (cursor.moveToFirst()) {
                SessionData info = new SessionData();
                info.setSessionId(cursor.getString(cursor
                        .getColumnIndex("sessionId")));
                info.setSessionExtendId(cursor.getString(cursor
                        .getColumnIndex("sessionExtendId")));
                info.setSessionType(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("sessionType"))));
                info.setSessionInfo(cursor.getString(cursor
                        .getColumnIndex("sessionInfo")));
                info.setSessionName(cursor.getString(cursor
                        .getColumnIndex("sessionName")));
                info.setSessionImage(cursor.getString(cursor
                        .getColumnIndex("sessionImage")));
                info.setSessionOffset(cursor.getString(cursor
                        .getColumnIndex("sessionOffset")));
                info.setSessionStamp(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("sessionStamp"))));
                info.setSessionCreateDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("sessionCreateDate"))));
                info.setSessionCreateUser(cursor.getString(cursor
                        .getColumnIndex("sessionCreateUser")));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("sessionDeleted"))));
                info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("sessionDeletedDate"))));

                info.setUsers(GsonTool.jsonArrayToModels(cursor.getString(cursor
                        .getColumnIndex("users")), ChatUser.class));

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

            List<SessionData> sessions = new ArrayList<>();

            Cursor cursor = db.query(DataBaseConfig.TABLE_SESSION, null,
                    "sessionInsertUser=? ",
                    new String[]{DataManager.getInstance().getLoginUser().getUserId()},
                    null,
                    null,
                    null);
            //获取数据
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    SessionData info = new SessionData();
                    info.setSessionId(cursor.getString(cursor
                            .getColumnIndex("sessionId")));
                    info.setSessionExtendId(cursor.getString(cursor
                            .getColumnIndex("sessionExtendId")));
                    info.setSessionType(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("sessionType"))));
                    info.setSessionInfo(cursor.getString(cursor
                            .getColumnIndex("sessionInfo")));
                    info.setSessionName(cursor.getString(cursor
                            .getColumnIndex("sessionName")));
                    info.setSessionImage(cursor.getString(cursor
                            .getColumnIndex("sessionImage")));
                    info.setSessionOffset(cursor.getString(cursor
                            .getColumnIndex("sessionOffset")));
                    info.setSessionStamp(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("sessionStamp"))));
                    info.setSessionCreateDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("sessionCreateDate"))));
                    info.setSessionCreateUser(cursor.getString(cursor
                            .getColumnIndex("sessionCreateUser")));
                    info.setIsDelete(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("sessionDeleted"))));
                    info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("sessionDeletedDate"))));
                    info.setUsers(GsonTool.jsonArrayToModels(cursor.getString(cursor
                            .getColumnIndex("users")), ChatUser.class));
                    sessions.add(info);
                    cursor.moveToNext();
                }
            }

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
                    info.setMessageId(cursor.getString(cursor
                            .getColumnIndex("messageId")));
                    info.setMessageSession(cursor.getString(cursor
                            .getColumnIndex("messageSession")));
                    info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSessionType"))));
                    info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSessionOffset"))));
                    info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageTableSeq"))));
                    info.setMessageType(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageType"))));
                    info.setMessageSendId(cursor.getString(cursor
                            .getColumnIndex("messageSendId")));
                    info.setMessageSendExtendId(cursor.getString(cursor
                            .getColumnIndex("messageSendExtendId")));
                    info.setMessageReceiveId(cursor.getString(cursor
                            .getColumnIndex("messageReceiveId")));
                    info.setMessageReceiveExtendId(cursor.getString(cursor
                            .getColumnIndex("messageReceiveExtendId")));
                    info.setMessageContent(cursor.getString(cursor
                            .getColumnIndex("messageContent")));
                    info.setMessageSendState(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSendState"))));
                    info.setMessageReadState(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageReadState"))));
                    info.setIsDelete(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("isDelete"))));
                    info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                            .getColumnIndex("messageStamp"))));
                    info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("messageDate"))));
                    info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("deleteDate"))));
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
                info.setMessageId(cursor.getString(cursor
                        .getColumnIndex("messageId")));
                info.setMessageSession(cursor.getString(cursor
                        .getColumnIndex("messageSession")));
                info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSessionType"))));
                info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSessionOffset"))));
                info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageTableSeq"))));
                info.setMessageType(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageType"))));
                info.setMessageSendId(cursor.getString(cursor
                        .getColumnIndex("messageSendId")));
                info.setMessageSendExtendId(cursor.getString(cursor
                        .getColumnIndex("messageSendExtendId")));
                info.setMessageReceiveId(cursor.getString(cursor
                        .getColumnIndex("messageReceiveId")));
                info.setMessageReceiveExtendId(cursor.getString(cursor
                        .getColumnIndex("messageReceiveExtendId")));
                info.setMessageContent(cursor.getString(cursor
                        .getColumnIndex("messageContent")));
                info.setMessageSendState(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSendState"))));
                info.setMessageReadState(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageReadState"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("isDelete"))));
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                        .getColumnIndex("messageStamp"))));
                info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDate"))));
                info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("deleteDate"))));
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
            List<ChatMessage> sessionSeqMessages = getSessionSeqMessages(messageSession, chatMessage.getMessageTableSeq().toString());

            //之前的消息
            for (int s = 0; s < sessionSeqMessages.size(); s++) {
                if (sessionSeqMessages.get(s).getMessageStamp().intValue() < chatMessage.getMessageStamp().intValue()) {
                    chatMessages.add(sessionSeqMessages.get(s));
                }
            }

            //获取此数据之前的数据列表集
            List<ChatMessage> list = new ArrayList<>();
            //获取这条消息之前的消息，并且不包含自身
            Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageSession = ? and messageTableSeq < ? ",
                    new String[]{messageSession, chatMessage.getMessageTableSeq().toString()},
                    null,
                    null,
                    "messageTableSeq DESC,messageStamp DESC LIMIT " + size);
            //获取数据
            if (cursor.moveToFirst())
                while (!cursor.isAfterLast()) {
                    ChatMessage info = new ChatMessage();
                    info.setMessageId(cursor.getString(cursor
                            .getColumnIndex("messageId")));
                    info.setMessageSession(cursor.getString(cursor
                            .getColumnIndex("messageSession")));
                    info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSessionType"))));
                    info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSessionOffset"))));
                    info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageTableSeq"))));
                    info.setMessageType(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageType"))));
                    info.setMessageSendId(cursor.getString(cursor
                            .getColumnIndex("messageSendId")));
                    info.setMessageSendExtendId(cursor.getString(cursor
                            .getColumnIndex("messageSendExtendId")));
                    info.setMessageReceiveId(cursor.getString(cursor
                            .getColumnIndex("messageReceiveId")));
                    info.setMessageReceiveExtendId(cursor.getString(cursor
                            .getColumnIndex("messageReceiveExtendId")));
                    info.setMessageContent(cursor.getString(cursor
                            .getColumnIndex("messageContent")));
                    info.setMessageSendState(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSendState"))));
                    info.setMessageReadState(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageReadState"))));
                    info.setIsDelete(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("isDelete"))));
                    info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                            .getColumnIndex("messageStamp"))));
                    info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("messageDate"))));
                    info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("deleteDate"))));
                    list.add(info);
                    cursor.moveToNext();
                }
            cursor.close();
            //消息列表
            chatMessages.addAll(list);

            if (chatMessages.size() > size) {
                List<ChatMessage> memList = new ArrayList<>();
                for (int s = 0; s < size; s++) {
                    memList.add(chatMessages.get(s));
                }
                chatMessages = memList;
            }

            return chatMessages;
        }

    }

    //更新还未处理的消息
    @SuppressLint("Range")
    public List<ChatMessage> getNotActionSystemMessage(String sessionID) {

        synchronized (lock) {

            List<ChatMessage> list = new ArrayList<>();

            //获取这条消息之前的消息，并且不包含自身
            Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageType = 0 and messageReadState = 0 and messageSession=?",
                    new String[]{sessionID},
                    null,
                    null,
                    "messageTableSeq DESC");

            //获取数据
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    ChatMessage info = new ChatMessage();
                    info.setMessageId(cursor.getString(cursor
                            .getColumnIndex("messageId")));
                    info.setMessageSession(cursor.getString(cursor
                            .getColumnIndex("messageSession")));
                    info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSessionType"))));
                    info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSessionOffset"))));
                    info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageTableSeq"))));
                    info.setMessageType(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageType"))));
                    info.setMessageSendId(cursor.getString(cursor
                            .getColumnIndex("messageSendId")));
                    info.setMessageSendExtendId(cursor.getString(cursor
                            .getColumnIndex("messageSendExtendId")));
                    info.setMessageReceiveId(cursor.getString(cursor
                            .getColumnIndex("messageReceiveId")));
                    info.setMessageReceiveExtendId(cursor.getString(cursor
                            .getColumnIndex("messageReceiveExtendId")));
                    info.setMessageContent(cursor.getString(cursor
                            .getColumnIndex("messageContent")));
                    info.setMessageSendState(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSendState"))));
                    info.setMessageReadState(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageReadState"))));
                    info.setIsDelete(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("isDelete"))));
                    info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                            .getColumnIndex("messageStamp"))));
                    info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("messageDate"))));
                    info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("deleteDate"))));
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
            List<ChatMessage> list = new ArrayList<>();

            //获取这条消息之前的消息，并且不包含自身
            Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageType = 0 and messageReadState = 0 ",
                    null,
                    null,
                    null,
                    "messageTableSeq DESC");

            //获取数据
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    ChatMessage info = new ChatMessage();
                    info.setMessageId(cursor.getString(cursor
                            .getColumnIndex("messageId")));
                    info.setMessageSession(cursor.getString(cursor
                            .getColumnIndex("messageSession")));
                    info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSessionType"))));
                    info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSessionOffset"))));
                    info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageTableSeq"))));
                    info.setMessageType(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageType"))));
                    info.setMessageSendId(cursor.getString(cursor
                            .getColumnIndex("messageSendId")));
                    info.setMessageSendExtendId(cursor.getString(cursor
                            .getColumnIndex("messageSendExtendId")));
                    info.setMessageReceiveId(cursor.getString(cursor
                            .getColumnIndex("messageReceiveId")));
                    info.setMessageReceiveExtendId(cursor.getString(cursor
                            .getColumnIndex("messageReceiveExtendId")));
                    info.setMessageContent(cursor.getString(cursor
                            .getColumnIndex("messageContent")));
                    info.setMessageSendState(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSendState"))));
                    info.setMessageReadState(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageReadState"))));
                    info.setIsDelete(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("isDelete"))));
                    info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                            .getColumnIndex("messageStamp"))));
                    info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("messageDate"))));
                    info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("deleteDate"))));
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
            Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageSession = ?",
                    new String[]{messageSession},
                    null,
                    null,
                    "messageTableSeq DESC,messageStamp DESC LIMIT 1");

            //获取数据
            if (cursor.moveToFirst()) {
                ChatMessage info = new ChatMessage();
                info.setMessageId(cursor.getString(cursor
                        .getColumnIndex("messageId")));
                info.setMessageSession(cursor.getString(cursor
                        .getColumnIndex("messageSession")));
                info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSessionType"))));
                info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSessionOffset"))));
                info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageTableSeq"))));
                info.setMessageType(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageType"))));
                info.setMessageSendId(cursor.getString(cursor
                        .getColumnIndex("messageSendId")));
                info.setMessageSendExtendId(cursor.getString(cursor
                        .getColumnIndex("messageSendExtendId")));
                info.setMessageReceiveId(cursor.getString(cursor
                        .getColumnIndex("messageReceiveId")));
                info.setMessageReceiveExtendId(cursor.getString(cursor
                        .getColumnIndex("messageReceiveExtendId")));
                info.setMessageContent(cursor.getString(cursor
                        .getColumnIndex("messageContent")));
                info.setMessageSendState(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSendState"))));
                info.setMessageReadState(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageReadState"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("isDelete"))));
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                        .getColumnIndex("messageStamp"))));
                info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDate"))));
                info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("deleteDate"))));
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

        synchronized (lock) {
            //查询最近一条消息
            Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "messageTableSeq DESC,messageStamp DESC LIMIT 1");

            //获取数据
            if (cursor.moveToFirst()) {
                ChatMessage info = new ChatMessage();
                info.setMessageId(cursor.getString(cursor
                        .getColumnIndex("messageId")));
                info.setMessageSession(cursor.getString(cursor
                        .getColumnIndex("messageSession")));
                info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSessionType"))));
                info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSessionOffset"))));
                info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageTableSeq"))));
                info.setMessageType(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageType"))));
                info.setMessageSendId(cursor.getString(cursor
                        .getColumnIndex("messageSendId")));
                info.setMessageSendExtendId(cursor.getString(cursor
                        .getColumnIndex("messageSendExtendId")));
                info.setMessageReceiveId(cursor.getString(cursor
                        .getColumnIndex("messageReceiveId")));
                info.setMessageReceiveExtendId(cursor.getString(cursor
                        .getColumnIndex("messageReceiveExtendId")));
                info.setMessageContent(cursor.getString(cursor
                        .getColumnIndex("messageContent")));
                info.setMessageSendState(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSendState"))));
                info.setMessageReadState(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageReadState"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("isDelete"))));
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                        .getColumnIndex("messageStamp"))));
                info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDate"))));
                info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("deleteDate"))));
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
            //消息更新
            Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageId = ?",
                    new String[]{messageID},
                    null,
                    null,
                    null);
            //获取数据
            if (cursor.moveToFirst()) {
                ChatMessage info = new ChatMessage();
                info.setMessageId(cursor.getString(cursor
                        .getColumnIndex("messageId")));
                info.setMessageSession(cursor.getString(cursor
                        .getColumnIndex("messageSession")));
                info.setMessageSessionType(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSessionType"))));
                info.setMessageSessionOffset(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSessionOffset"))));
                info.setMessageTableSeq(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageTableSeq"))));
                info.setMessageType(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageType"))));
                info.setMessageSendId(cursor.getString(cursor
                        .getColumnIndex("messageSendId")));
                info.setMessageSendExtendId(cursor.getString(cursor
                        .getColumnIndex("messageSendExtendId")));
                info.setMessageReceiveId(cursor.getString(cursor
                        .getColumnIndex("messageReceiveId")));
                info.setMessageReceiveExtendId(cursor.getString(cursor
                        .getColumnIndex("messageReceiveExtendId")));
                info.setMessageContent(cursor.getString(cursor
                        .getColumnIndex("messageContent")));
                info.setMessageSendState(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSendState"))));
                info.setMessageReadState(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageReadState"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("isDelete"))));
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                        .getColumnIndex("messageStamp"))));
                info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDate"))));
                info.setDeleteDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("deleteDate"))));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        }
    }


}
