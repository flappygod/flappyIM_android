package com.flappygo.flappyim.DataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Tools.DateTimeTool;
import com.flappygo.flappyim.Tools.StringTool;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    //数据库
    private static Database instance;

    private Database() {
        dbHelper = new DatabaseHelper(FlappyImService.getInstance().getAppContext(),
                DataBaseConfig.DB_NAME,
                null,
                DataBaseConfig.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /************************
     * 使用单例类加锁可以防止数据库被锁
     ************************/
    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    /********************************
     * 关闭的时候关闭helper和数据库，并置空
     ********************************/
    public void Close() {
        db.close();
        dbHelper.close();
        db = null;
        dbHelper = null;
        instance = null;
    }

    //判断是否存在消息
    public boolean isMessageContain(ChatMessage chatMessage) {
        //检查是否有记录
        Cursor cursor = db.query(DataBaseConfig.TABLE_MESSAGE,
                null,
                "messageId=?",
                new String[]{chatMessage.getMessageId()},
                null,
                null,
                null);
        //没有记录
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        cursor.close();

        return true;
    }

    //消息
    public boolean insertMessage(ChatMessage chatMessage) {
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
            //代表消息已经发送了
            ContentValues values = new ContentValues();
            //更新消息的发送状态
            values.put("messageSended", StringTool.decimalToInt(chatMessage.getMessageSended()));
            //更新消息的序号
            values.put("messageTableSeq", StringTool.decimalToInt(chatMessage.getMessageTableSeq()));
            //更新消息的世界
            values.put("messageDate", DateTimeTool.dateToStr(chatMessage.getMessageDate()));
            //更新消息信息
            db.update(DataBaseConfig.TABLE_MESSAGE,
                    values,
                    "messageId=?",
                    new String[]{chatMessage.getMessageId()}
            );
            cursor.close();
        }
        return false;
    }


    //保存消息
    public boolean saveOrUpdateMessage(List<ChatMessage> messageList) {

        try {
            //开启事务
            db.beginTransaction();
            //设置
            for (int s = 0; s < messageList.size(); s++) {
                //消息
                ChatMessage chatMessage = messageList.get(s);
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
                    if (ret <= 0) {
                        return false;
                    }
                } else {
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


                    long ret = db.update(DataBaseConfig.TABLE_MESSAGE,
                            values,
                            "messageId=?",
                            new String[]{chatMessage.getMessageId()});

                    if (ret <= 0) {
                        return false;
                    }
                }
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction(); //处理完成
        }
    }

    //获取所有的消息
    public List<ChatMessage> getAllMessages() {
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

    //获取当前这个messageTableSeq 的所有消息
    public List<ChatMessage> getSessionSequeceMessages(String messageSession, String messageTableSeq) {
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

        if(retMsgs.size()>size){
            List<ChatMessage> memList=new ArrayList<>();
            for(int s=0;s<size;s++){
                memList.add(retMsgs.get(s));
            }
            retMsgs=memList;
        }

        return retMsgs;
    }

    //获取最近的一条消息
    public ChatMessage getSessionLatestMessage() {

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


    //获取最近的一条消息
    public ChatMessage getSessionLatestMessage(String messageSession) {

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


    //通过消息ID获取消息
    public ChatMessage getMessageByID(String messageID) {
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
