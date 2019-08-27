package com.flappygo.flappyim.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Tools.DateTimeTool;
import com.flappygo.flappyim.Tools.StringTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**************************
 * Title: EtongDatabase Package: com.etong.mall.database Description: 数据库
 *
 * @author:李俊霖
 * @version:2014-10-28下午
 * @since 1.0
 */
public class Database {

    // 数据库db
    private SQLiteDatabase db;
    // 数据库helper
    public DatabaseHelper dbHelper;

    private static byte[] lock = new byte[1];

    public Database() {
        dbHelper = new DatabaseHelper(FlappyImService.getInstance().getAppContext(),
                DataBaseConfig.DB_NAME,
                null,
                DataBaseConfig.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }


    /********************************
     * 关闭的时候关闭helper和数据库，并置空
     ********************************/
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
                    new String[]{chatMessage.getMessageId()}, null, null, null);
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
                if (chatMessage.getMessageSend() != null)
                    values.put("messageSend", chatMessage.getMessageSend());
                if (chatMessage.getMessageSend() != null)
                    values.put("messageSendExtendid", chatMessage.getMessageSendExtendid());
                if (chatMessage.getMessageRecieve() != null)
                    values.put("messageRecieve", chatMessage.getMessageRecieve());
                if (chatMessage.getMessageRecieveExtendid() != null)
                    values.put("messageRecieveExtendid", chatMessage.getMessageRecieveExtendid());
                if (chatMessage.getMessageContent() != null)
                    values.put("messageContent", chatMessage.getMessageContent());
                if (chatMessage.getMessageSended() != null)
                    values.put("messageSended", StringTool.decimalToInt(chatMessage.getMessageSended()));
                if (chatMessage.getMessageReaded() != null)
                    values.put("messageReaded", StringTool.decimalToInt(chatMessage.getMessageReaded()));
                if (chatMessage.getMessageDeleted() != null)
                    values.put("messageDeleted", StringTool.decimalToInt(chatMessage.getMessageDeleted()));
                if (chatMessage.getMessageDate() != null)
                    values.put("messageDate", DateTimeTool.dateToStr(chatMessage.getMessageDate()));
                if (chatMessage.getMessageDeletedDate() != null)
                    values.put("messageDeletedDate", DateTimeTool.dateToStr(chatMessage.getMessageDeletedDate()));
                {
                    values.put("messageStamp", System.currentTimeMillis());
                }
                long ret = db.insert(DataBaseConfig.TABLE_MESSAGE,
                        null,
                        values);
                if (ret > 0) {
                    return true;
                }
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
                if (chatMessage.getMessageSend() != null)
                    values.put("messageSend", chatMessage.getMessageSend());
                if (chatMessage.getMessageSend() != null)
                    values.put("messageSendExtendid", chatMessage.getMessageSendExtendid());
                if (chatMessage.getMessageRecieve() != null)
                    values.put("messageRecieve", chatMessage.getMessageRecieve());
                if (chatMessage.getMessageRecieveExtendid() != null)
                    values.put("messageRecieveExtendid", chatMessage.getMessageRecieveExtendid());
                if (chatMessage.getMessageContent() != null)
                    values.put("messageContent", chatMessage.getMessageContent());
                if (chatMessage.getMessageSended() != null)
                    values.put("messageSended", StringTool.decimalToInt(chatMessage.getMessageSended()));
                if (chatMessage.getMessageReaded() != null)
                    values.put("messageReaded", StringTool.decimalToInt(chatMessage.getMessageReaded()));
                if (chatMessage.getMessageDeleted() != null)
                    values.put("messageDeleted", StringTool.decimalToInt(chatMessage.getMessageDeleted()));
                if (chatMessage.getMessageDate() != null)
                    values.put("messageDate", DateTimeTool.dateToStr(chatMessage.getMessageDate()));
                if (chatMessage.getMessageDeletedDate() != null)
                    values.put("messageDeletedDate", DateTimeTool.dateToStr(chatMessage.getMessageDeletedDate()));
                //更新消息信息
                long ret = db.update(DataBaseConfig.TABLE_MESSAGE,
                        values,
                        "messageId=?",
                        new String[]{chatMessage.getMessageId()}
                );
                if (ret > 0) {
                    return true;
                }
            }
            return false;
        }
    }

    //插入一个列表的消息
    public boolean insertMessages(List<ChatMessage> messages) {
        db.beginTransaction();
        boolean totalSuccess = true;
        for (int s = 0; s < messages.size(); s++) {
            boolean flag = insertMessage(messages.get(s));
            if (flag == false) {
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
            String nowerUserID = DataManager.getInstance().getLoginUser().getUserExtendId();

            //检查是否有记录
            Cursor cursor = db.query(DataBaseConfig.TABLE_SESSION, null,
                    "sessionId=? and sessionInsertUser=? ",
                    new String[]{session.getSessionId(), nowerUserID}, null, null, null);

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
                if (session.getSessionDeleted() != null)
                    values.put("sessionDeleted", StringTool.decimalToInt(session.getSessionDeleted()));
                if (session.getSessionDeletedDate() != null)
                    values.put("sessionDeletedDate", DateTimeTool.dateToStr(session.getSessionDeletedDate()));
                if (session.getUsers() != null)
                    values.put("users", GsonTool.modelToString(session.getUsers(), ChatUser.class));
                //插入者
                values.put("sessionInsertUser", DataManager.getInstance().getLoginUser().getUserExtendId());

                long ret = db.insert(DataBaseConfig.TABLE_SESSION,
                        null,
                        values);
                if (ret > 0) {
                    return true;
                }
            } else {
                cursor.close();

                ContentValues values = new ContentValues();

                if (session.getSessionType() != null)
                    values.put("sessionType", StringTool.decimalToInt(session.getSessionType()));
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
                if (session.getSessionDeleted() != null)
                    values.put("sessionDeleted", StringTool.decimalToInt(session.getSessionDeleted()));
                if (session.getSessionDeletedDate() != null)
                    values.put("sessionDeletedDate", DateTimeTool.dateToStr(session.getSessionDeletedDate()));
                if (session.getUsers() != null)
                    values.put("users", GsonTool.modelToString(session.getUsers(), ChatUser.class));
                //插入者
                values.put("sessionInsertUser", DataManager.getInstance().getLoginUser().getUserExtendId());

                //更新消息信息
                long ret = db.update(DataBaseConfig.TABLE_SESSION,
                        values,
                        "sessionId=? and sessionInsertUser=? ",
                        new String[]{session.getSessionId(), nowerUserID}
                );

                if (ret > 0) {
                    return true;
                }
            }
            return false;
        }
    }

    //插入多个会话
    public boolean insertSessions(List<SessionData> sessionData) {
        db.beginTransaction();
        boolean totalSuccess = true;
        for (int s = 0; s < sessionData.size(); s++) {
            boolean flag = insertSession(sessionData.get(s));
            if (flag == false) {
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
    public SessionData getUserSessionsByExtend(String sessionExtendID) {
        synchronized (lock) {

            Cursor cursor = db.query(DataBaseConfig.TABLE_SESSION, null,
                    "sessionExtendId=? and sessionInsertUser=? ",
                    new String[]{sessionExtendID, DataManager.getInstance().getLoginUser().getUserId()}, null, null, null);

            //获取数据
            if (cursor.moveToFirst()) {
                SessionData info = new SessionData();
                info.setSessionId(cursor.getString(cursor
                        .getColumnIndex("sessionId")));
                info.setSessionExtendId(cursor.getString(cursor
                        .getColumnIndex("sessionExtendId")));
                info.setSessionType(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("sessionType"))));
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
                info.setSessionDeleted(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("sessionDeleted"))));
                info.setSessionDeletedDate(DateTimeTool.strToDate(cursor.getString(cursor
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


    public List<SessionData> getUserSessions() {
        synchronized (lock) {

            List<SessionData> sessions = new ArrayList<>();

            Cursor cursor = db.query(DataBaseConfig.TABLE_SESSION, null,
                    "sessionInsertUser=? ",
                    new String[]{DataManager.getInstance().getLoginUser().getUserId()}, null, null, null);
            //获取数据
            if (cursor.moveToFirst())
                while (!cursor.isAfterLast()) {
                    SessionData info = new SessionData();
                    info.setSessionId(cursor.getString(cursor
                            .getColumnIndex("sessionId")));
                    info.setSessionExtendId(cursor.getString(cursor
                            .getColumnIndex("sessionExtendId")));
                    info.setSessionType(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("sessionType"))));
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
                    info.setSessionDeleted(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("sessionDeleted"))));
                    info.setSessionDeletedDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("sessionDeletedDate"))));

                    info.setUsers(GsonTool.jsonArrayToModels(cursor.getString(cursor
                            .getColumnIndex("users")), ChatUser.class));
                    sessions.add(info);
                    cursor.moveToNext();
                }
            cursor.close();

            return sessions;
        }
    }


    //获取所有的消息
    public List<ChatMessage> getAllMessages() {
        synchronized (lock) {
            List<ChatMessage> list = new ArrayList<ChatMessage>();
            //消息更新
            Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "messageTableSeq DESC,messageStamp DESC");
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
                    info.setMessageSend(cursor.getString(cursor
                            .getColumnIndex("messageSend")));
                    info.setMessageSendExtendid(cursor.getString(cursor
                            .getColumnIndex("messageSendExtendid")));
                    info.setMessageRecieve(cursor.getString(cursor
                            .getColumnIndex("messageRecieve")));
                    info.setMessageRecieveExtendid(cursor.getString(cursor
                            .getColumnIndex("messageRecieveExtendid")));
                    info.setMessageContent(cursor.getString(cursor
                            .getColumnIndex("messageContent")));
                    info.setMessageSended(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSended"))));
                    info.setMessageReaded(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageReaded"))));
                    info.setMessageDeleted(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageDeleted"))));
                    info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                            .getColumnIndex("messageStamp"))));
                    info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("messageDate"))));
                    info.setMessageDeletedDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("messageDeletedDate"))));
                    list.add(info);
                    cursor.moveToNext();
                }
            cursor.close();
            return list;
        }
    }

    //获取当前这个messageTableSeq 的所有消息
    private List<ChatMessage> getSessionSequeceMessages(String messageSession, String messageTableSeq) {
        List<ChatMessage> list = new ArrayList<ChatMessage>();
        //获取这条消息之前的消息，并且不包含自身
        Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                null,
                "messageSession = ? and messageTableSeq = ? ",
                new String[]{messageSession, messageTableSeq},
                null,
                null,
                "messageStamp DESC");
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
                info.setMessageSend(cursor.getString(cursor
                        .getColumnIndex("messageSend")));
                info.setMessageSendExtendid(cursor.getString(cursor
                        .getColumnIndex("messageSendExtendid")));
                info.setMessageRecieve(cursor.getString(cursor
                        .getColumnIndex("messageRecieve")));
                info.setMessageRecieveExtendid(cursor.getString(cursor
                        .getColumnIndex("messageRecieveExtendid")));
                info.setMessageContent(cursor.getString(cursor
                        .getColumnIndex("messageContent")));
                info.setMessageSended(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSended"))));
                info.setMessageReaded(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageReaded"))));
                info.setMessageDeleted(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageDeleted"))));
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                        .getColumnIndex("messageStamp"))));
                info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDate"))));
                info.setMessageDeletedDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDeletedDate"))));
                list.add(info);
                cursor.moveToNext();
            }
        cursor.close();
        return list;
    }


    //获取最近的一条消息
    public List<ChatMessage> getSessionLatestMessage(String messageSession, String messageID, int size) {

        synchronized (lock) {
            //返回的列表
            List<ChatMessage> retMsgs = new ArrayList<>();

            //首先查询到这个消息
            ChatMessage chatMessage = getMessageByID(messageID);

            //然后查询出与它相同的数据
            List<ChatMessage> sequenceMsgs = getSessionSequeceMessages(messageSession, chatMessage.getMessageTableSeq().toString());

            //之前的消息
            for (int s = 0; s < sequenceMsgs.size(); s++) {
                if (sequenceMsgs.get(s).getMessageStamp().intValue() < chatMessage.getMessageStamp().intValue()) {
                    retMsgs.add(sequenceMsgs.get(s));
                }
            }

            //获取此数据之前的数据列表集
            List<ChatMessage> list = new ArrayList<ChatMessage>();
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
                    info.setMessageSend(cursor.getString(cursor
                            .getColumnIndex("messageSend")));
                    info.setMessageSendExtendid(cursor.getString(cursor
                            .getColumnIndex("messageSendExtendid")));
                    info.setMessageRecieve(cursor.getString(cursor
                            .getColumnIndex("messageRecieve")));
                    info.setMessageRecieveExtendid(cursor.getString(cursor
                            .getColumnIndex("messageRecieveExtendid")));
                    info.setMessageContent(cursor.getString(cursor
                            .getColumnIndex("messageContent")));
                    info.setMessageSended(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageSended"))));
                    info.setMessageReaded(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageReaded"))));
                    info.setMessageDeleted(new BigDecimal(cursor.getInt(cursor
                            .getColumnIndex("messageDeleted"))));
                    info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                            .getColumnIndex("messageStamp"))));
                    info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("messageDate"))));
                    info.setMessageDeletedDate(DateTimeTool.strToDate(cursor.getString(cursor
                            .getColumnIndex("messageDeletedDate"))));
                    list.add(info);
                    cursor.moveToNext();
                }
            cursor.close();
            //消息列表
            retMsgs.addAll(list);

            if (retMsgs.size() > size) {
                List<ChatMessage> memList = new ArrayList<>();
                for (int s = 0; s < size; s++) {
                    memList.add(retMsgs.get(s));
                }
                retMsgs = memList;
            }

            return retMsgs;
        }

    }

    //获取最近的一条消息
    public ChatMessage getSessionLatestMessage() {

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
                info.setMessageSend(cursor.getString(cursor
                        .getColumnIndex("messageSend")));
                info.setMessageSendExtendid(cursor.getString(cursor
                        .getColumnIndex("messageSendExtendid")));
                info.setMessageRecieve(cursor.getString(cursor
                        .getColumnIndex("messageRecieve")));
                info.setMessageRecieveExtendid(cursor.getString(cursor
                        .getColumnIndex("messageRecieveExtendid")));
                info.setMessageContent(cursor.getString(cursor
                        .getColumnIndex("messageContent")));
                info.setMessageSended(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSended"))));
                info.setMessageReaded(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageReaded"))));
                info.setMessageDeleted(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageDeleted"))));
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                        .getColumnIndex("messageStamp"))));
                info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDate"))));
                info.setMessageDeletedDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDeletedDate"))));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        }
    }


    //获取最近的一条消息
    public ChatMessage getSessionLatestMessage(String messageSession) {

        synchronized (this) {
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
                info.setMessageSend(cursor.getString(cursor
                        .getColumnIndex("messageSend")));
                info.setMessageSendExtendid(cursor.getString(cursor
                        .getColumnIndex("messageSendExtendid")));
                info.setMessageRecieve(cursor.getString(cursor
                        .getColumnIndex("messageRecieve")));
                info.setMessageRecieveExtendid(cursor.getString(cursor
                        .getColumnIndex("messageRecieveExtendid")));
                info.setMessageContent(cursor.getString(cursor
                        .getColumnIndex("messageContent")));
                info.setMessageSended(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSended"))));
                info.setMessageReaded(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageReaded"))));
                info.setMessageDeleted(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageDeleted"))));
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                        .getColumnIndex("messageStamp"))));
                info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDate"))));
                info.setMessageDeletedDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDeletedDate"))));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        }

    }


    //通过消息ID获取消息
    public ChatMessage getMessageByID(String messageID) {
        synchronized (this) {
            ChatMessage info = null;
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
                info = new ChatMessage();
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
                info.setMessageSend(cursor.getString(cursor
                        .getColumnIndex("messageSend")));
                info.setMessageSendExtendid(cursor.getString(cursor
                        .getColumnIndex("messageSendExtendid")));
                info.setMessageRecieve(cursor.getString(cursor
                        .getColumnIndex("messageRecieve")));
                info.setMessageRecieveExtendid(cursor.getString(cursor
                        .getColumnIndex("messageRecieveExtendid")));
                info.setMessageContent(cursor.getString(cursor
                        .getColumnIndex("messageContent")));
                info.setMessageSended(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageSended"))));
                info.setMessageReaded(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageReaded"))));
                info.setMessageDeleted(new BigDecimal(cursor.getInt(cursor
                        .getColumnIndex("messageDeleted"))));
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor
                        .getColumnIndex("messageStamp"))));
                info.setMessageDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDate"))));
                info.setMessageDeletedDate(DateTimeTool.strToDate(cursor.getString(cursor
                        .getColumnIndex("messageDeletedDate"))));
                cursor.close();
                return info;
            }
            cursor.close();
            return info;
        }
    }


}
