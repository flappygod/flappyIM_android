package com.flappygo.flappyim.DataBase;


import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_FAILURE;

import com.flappygo.flappyim.DataBase.Models.SessionMemberModel;
import com.flappygo.flappyim.Handler.MessageNotifyManager;
import com.flappygo.flappyim.DataBase.Models.SessionModel;
import com.flappygo.flappyim.Models.Request.ChatAction;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Handler.HandlerSession;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Tools.StringTool;

import android.database.sqlite.SQLiteDatabase;

import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Tools.TimeTool;

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

    //打开数据库
    public Database open() {
        synchronized (this) {
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
        synchronized (this) {
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
        open();
        try {
            ContentValues values = new ContentValues();
            values.put("messageSendState", SEND_STATE_FAILURE);
            db.update(
                    DataBaseConfig.TABLE_MESSAGE,
                    values,
                    "messageSendState = 0",
                    null
            );
        } finally {
            close();
        }
    }


    /******
     * 插入一个列表的消息
     * @param messages 消息列表
     */
    public void insertMessages(List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        open();
        try {
            db.beginTransaction();
            for (ChatMessage msg : messages) {
                insertMessage(msg);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close();
        }
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

        open();
        try {
            //创建插入信息
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
            if (chatMessage.getMessageDate() != null) {
                values.put("messageDate", TimeTool.dateToStr(chatMessage.getMessageDate()));
            }
            if (chatMessage.getIsDelete() != null) {
                values.put("isDelete", StringTool.decimalToInt(chatMessage.getIsDelete()));
            }
            if (chatMessage.getDeleteDate() != null) {
                values.put("deleteDate", TimeTool.dateToStr(chatMessage.getDeleteDate()));
            }

            //插入用户
            values.put("messageInsertUser", chatUser.getUserExtendId());

            //消息Stamp
            values.put("messageStamp", System.currentTimeMillis());

            //插入消息数据
            db.insertWithOnConflict(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
        } finally {
            close();
        }
    }

    /******
     * 更新消息已读(系统消息的已读状态不做处理)
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
        open();
        try {
            //设置已读消息
            ContentValues values = new ContentValues();
            //设置已读
            values.put("messageReadState", 1);
            //更新已读消息
            db.update(
                    DataBaseConfig.TABLE_MESSAGE,
                    values,
                    "messageInsertUser=? and messageSendId!=? and messageType != 0 and messageSession=? and messageTableSeq <= ? ",
                    new String[]{
                            chatUser.getUserExtendId(),
                            userId,
                            sessionId,
                            tableSequence,
                    }
            );
        } finally {
            close();
        }
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
        open();
        try {
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
        } finally {
            close();
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
     * 获取未读消息数量
     * @param sessionID 会话ID
     * @return 未读消息数量
     */
    public int getNotReadSessionMessageCountBySessionId(String sessionID) {
        open();
        try {
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
        } finally {
            close();
        }
    }


    /******
     * 插入多个会话
     * @param  sessionModelList  会话列表
     */
    public void insertSessions(List<SessionModel> sessionModelList) {
        if (sessionModelList == null || sessionModelList.isEmpty()) {
            return;
        }
        open();
        try {
            db.beginTransaction();
            for (SessionModel sessionModel : sessionModelList) {
                insertSession(sessionModel, MessageNotifyManager.getInstance().getHandlerSession());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close();
        }
    }

    /******
     * 插入数据
     * @param session        会话
     * @param handlerSession 插入会话后的handler
     */
    public void insertSession(SessionModel session, HandlerSession handlerSession) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return;
        }


        open();
        try {
            //创建会话信息
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
                values.put("sessionCreateDate", TimeTool.dateToStr(session.getSessionCreateDate()));
            }
            if (session.getSessionCreateUser() != null) {
                values.put("sessionCreateUser", session.getSessionCreateUser());
            }
            if (session.getIsDelete() != null) {
                values.put("sessionDeleted", StringTool.decimalToInt(session.getIsDelete()));
            }
            if (session.getDeleteDate() != null) {
                values.put("sessionDeletedDate", TimeTool.dateToStr(session.getDeleteDate()));
            }
            values.put("sessionInsertUser", chatUser.getUserExtendId());

            //插入数据
            db.insertWithOnConflict(
                    DataBaseConfig.TABLE_SESSION,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );

            //插入用户数据
            if (session.getUsers() != null && !session.getUsers().isEmpty()) {
                for (SessionMemberModel memberModel : session.getUsers()) {
                    insertSessionMember(memberModel);
                }
            }
        } finally {
            close();
        }


        //通知消息更新
        Message msg = new Message();
        msg.what = HandlerSession.SESSION_UPDATE;
        msg.obj = session;
        handlerSession.sendMessage(msg);
    }


    /******
     * 获取当前用户的会话
     * @param sessionId  会话ID
     * @return 会话
     */
    @SuppressLint("Range")
    public SessionModel getUserSessionByID(String sessionId) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return null;
        }
        open();
        try {
            //请求数据
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_SESSION,
                    null,
                    "sessionId=? and sessionInsertUser=? ",
                    new String[]{
                            sessionId,
                            chatUser.getUserExtendId()
                    },
                    null,
                    null,
                    null
            );
            //获取数据
            if (cursor.moveToFirst()) {
                SessionModel info = new SessionModel();
                info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
                info.setSessionExtendId(cursor.getString(cursor.getColumnIndex("sessionExtendId")));
                info.setSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionType"))));
                info.setSessionInfo(cursor.getString(cursor.getColumnIndex("sessionInfo")));
                info.setSessionName(cursor.getString(cursor.getColumnIndex("sessionName")));
                info.setSessionImage(cursor.getString(cursor.getColumnIndex("sessionImage")));
                info.setSessionOffset(cursor.getString(cursor.getColumnIndex("sessionOffset")));
                info.setSessionStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("sessionStamp"))));
                info.setSessionCreateDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionCreateDate"))));
                info.setSessionCreateUser(cursor.getString(cursor.getColumnIndex("sessionCreateUser")));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionDeleted"))));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionDeletedDate"))));
                info.setUnReadMessageCount(getNotReadSessionMessageCountBySessionId(sessionId));
                info.setUsers(getSessionMemberList(info.getSessionId()));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        } finally {
            close();
        }
    }

    /******
     * 删除用户会话
     * @param sessionId 会话ID
     */
    public void deleteUserSession(String sessionId) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return;
        }
        open();
        try {
            //删除会话数据
            db.delete(
                    DataBaseConfig.TABLE_SESSION,
                    "sessionId=? and sessionInsertUser=?",
                    new String[]{
                            sessionId,
                            chatUser.getUserExtendId()
                    }
            );
            //删除会话消息
            db.delete(
                    DataBaseConfig.TABLE_MESSAGE,
                    "messageSession=? and messageInsertUser=?",
                    new String[]{
                            sessionId,
                            chatUser.getUserExtendId()
                    }
            );
        } finally {
            close();
        }
    }


    /******
     * 插入会话用户
     * @param member 会话用户
     */
    public void insertSessionMember(SessionMemberModel member) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return;
        }

        open();
        try {
            //创建values
            ContentValues values = new ContentValues();
            if (member.getUserId() != null) {
                values.put("userId", member.getUserId());
            }
            if (member.getUserExtendId() != null) {
                values.put("userExtendId", member.getUserExtendId());
            }
            if (member.getUserName() != null) {
                values.put("userName", member.getUserName());
            }
            if (member.getUserAvatar() != null) {
                values.put("userAvatar", member.getUserAvatar());
            }
            if (member.getUserData() != null) {
                values.put("userData", member.getUserData());
            }
            if (member.getUserCreateDate() != null) {
                values.put("userCreateDate", TimeTool.dateToStr(member.getUserCreateDate()));
            }
            if (member.getUserLoginDate() != null) {
                values.put("userLoginDate", TimeTool.dateToStr(member.getUserLoginDate()));
            }
            if (member.getSessionId() != null) {
                values.put("sessionId", member.getSessionId());
            }
            if (member.getSessionMemberLatestRead() != null) {
                values.put("sessionMemberLatestRead", member.getSessionMemberLatestRead());
            }
            if (member.getSessionMemberMarkName() != null) {
                values.put("sessionMemberMarkName", member.getSessionMemberMarkName());
            }
            if (member.getSessionMemberNoDisturb() != null) {
                values.put("sessionMemberNoDisturb", member.getSessionMemberNoDisturb());
            }
            if (member.getSessionJoinDate() != null) {
                values.put("sessionJoinDate", TimeTool.dateToStr(member.getSessionJoinDate()));
            }
            if (member.getSessionLeaveDate() != null) {
                values.put("sessionLeaveDate", TimeTool.dateToStr(member.getSessionLeaveDate()));
            }
            if (member.getIsLeave() != null) {
                values.put("isLeave", member.getIsLeave());
            }
            values.put("sessionInsertUser", chatUser.getUserExtendId());
            //没有记录
            db.insertWithOnConflict(
                    DataBaseConfig.TABLE_SESSION_MEMBER,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
        } finally {
            close();
        }
    }

    /******
     * 获取会话用户
     * @param sessionId 会话ID
     * @param memberId  用户ID
     */
    @SuppressLint("Range")
    public SessionMemberModel getSessionMember(String sessionId, String memberId) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return null;
        }
        open();
        try {
            //获取session中未读的系统消息
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_SESSION_MEMBER,
                    null,
                    "sessionId = ? and userId = ? and sessionInsertUser= ?",
                    new String[]{
                            sessionId,
                            memberId,
                            chatUser.getUserExtendId()
                    },
                    null,
                    null,
                    null
            );
            //没有就关闭
            if (!cursor.moveToFirst()) {
                cursor.close();
                return null;
            }
            //获取所有数据
            if (!cursor.isAfterLast()) {
                SessionMemberModel info = new SessionMemberModel();
                info.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
                info.setUserExtendId(cursor.getString(cursor.getColumnIndex("userExtendId")));
                info.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                info.setUserAvatar(cursor.getString(cursor.getColumnIndex("userAvatar")));
                info.setUserData(cursor.getString(cursor.getColumnIndex("userData")));
                info.setUserCreateDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("userCreateDate"))));
                info.setUserLoginDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("userLoginDate"))));
                info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
                info.setSessionMemberLatestRead(cursor.getString(cursor.getColumnIndex("sessionMemberLatestRead")));
                info.setSessionMemberMarkName(cursor.getString(cursor.getColumnIndex("sessionMemberMarkName")));
                info.setSessionMemberNoDisturb(cursor.getInt(cursor.getColumnIndex("sessionMemberNoDisturb")));
                info.setSessionJoinDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionJoinDate"))));
                info.setSessionLeaveDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionLeaveDate"))));
                info.setIsLeave(cursor.getInt(cursor.getColumnIndex("isLeave")));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        } finally {
            close();
        }
    }

    /******
     * 获取会话用户列表
     * @param sessionId 会话ID
     */
    @SuppressLint("Range")
    public List<SessionMemberModel> getSessionMemberList(String sessionId) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return new ArrayList<>();
        }
        open();
        try {
            //获取session中未读的系统消息
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_SESSION_MEMBER,
                    null,
                    "sessionId = ? and sessionInsertUser= ?",
                    new String[]{
                            sessionId,
                            chatUser.getUserExtendId()
                    },
                    null,
                    null,
                    null
            );
            //获取数据
            List<SessionMemberModel> list = new ArrayList<>();
            //没有就关闭
            if (!cursor.moveToFirst()) {
                cursor.close();
                return list;
            }
            //获取所有数据
            while (!cursor.isAfterLast()) {
                SessionMemberModel info = new SessionMemberModel();
                info.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
                info.setUserExtendId(cursor.getString(cursor.getColumnIndex("userExtendId")));
                info.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                info.setUserAvatar(cursor.getString(cursor.getColumnIndex("userAvatar")));
                info.setUserData(cursor.getString(cursor.getColumnIndex("userData")));
                info.setUserCreateDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("userCreateDate"))));
                info.setUserLoginDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("userLoginDate"))));
                info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
                info.setSessionMemberLatestRead(cursor.getString(cursor.getColumnIndex("sessionMemberLatestRead")));
                info.setSessionMemberMarkName(cursor.getString(cursor.getColumnIndex("sessionMemberMarkName")));
                info.setSessionMemberNoDisturb(cursor.getInt(cursor.getColumnIndex("sessionMemberNoDisturb")));
                info.setSessionJoinDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionJoinDate"))));
                info.setSessionLeaveDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionLeaveDate"))));
                info.setIsLeave(cursor.getInt(cursor.getColumnIndex("isLeave")));
                list.add(info);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } finally {
            close();
        }

    }


    /******
     * 更新用户消息最近已读
     * @param userId        用户ID
     * @param sessionId     会话ID
     * @param tableSequence 表序号
     */
    private void updateSessionMemberLatestRead(String userId, String sessionId, String tableSequence) {
        //会话Data
        open();
        try {
            SessionMemberModel memberModel = getSessionMember(sessionId, userId);
            if (memberModel == null) {
                return;
            }
            memberModel.setSessionMemberLatestRead(tableSequence);
            insertSessionMember(memberModel);
        } finally {
            close();
        }
    }


    /******
     * 获取当前用户的会话
     * @param sessionExtendID  会话外部ID
     * @return 会话
     */
    @SuppressLint("Range")
    public SessionModel getUserSessionByExtendID(String sessionExtendID) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return null;
        }
        open();
        try {
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
                SessionModel info = new SessionModel();
                info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
                info.setSessionExtendId(cursor.getString(cursor.getColumnIndex("sessionExtendId")));
                info.setSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionType"))));
                info.setSessionInfo(cursor.getString(cursor.getColumnIndex("sessionInfo")));
                info.setSessionName(cursor.getString(cursor.getColumnIndex("sessionName")));
                info.setSessionImage(cursor.getString(cursor.getColumnIndex("sessionImage")));
                info.setSessionOffset(cursor.getString(cursor.getColumnIndex("sessionOffset")));
                info.setSessionStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("sessionStamp"))));
                info.setSessionCreateDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionCreateDate"))));
                info.setSessionCreateUser(cursor.getString(cursor.getColumnIndex("sessionCreateUser")));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionDeleted"))));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionDeletedDate"))));
                info.setUnReadMessageCount(getNotReadSessionMessageCountBySessionId(info.getSessionId()));
                info.setUsers(getSessionMemberList(info.getSessionId()));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        } finally {
            close();
        }
    }

    /******
     * 获取用户的所有会话列表
     * @return 所有的会话数据
     */
    @SuppressLint("Range")
    public List<SessionModel> getUserSessions() {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return new ArrayList<>();
        }
        open();
        try {
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
            List<SessionModel> sessions = new ArrayList<>();
            //没有就关闭
            if (!cursor.moveToFirst()) {
                cursor.close();
                return sessions;
            }
            while (!cursor.isAfterLast()) {
                SessionModel info = new SessionModel();
                info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
                info.setSessionExtendId(cursor.getString(cursor.getColumnIndex("sessionExtendId")));
                info.setSessionType(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionType"))));
                info.setSessionInfo(cursor.getString(cursor.getColumnIndex("sessionInfo")));
                info.setSessionName(cursor.getString(cursor.getColumnIndex("sessionName")));
                info.setSessionImage(cursor.getString(cursor.getColumnIndex("sessionImage")));
                info.setSessionOffset(cursor.getString(cursor.getColumnIndex("sessionOffset")));
                info.setSessionStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("sessionStamp"))));
                info.setSessionCreateDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionCreateDate"))));
                info.setSessionCreateUser(cursor.getString(cursor.getColumnIndex("sessionCreateUser")));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("sessionDeleted"))));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionDeletedDate"))));
                info.setUnReadMessageCount(getNotReadSessionMessageCountBySessionId(info.getSessionId()));
                info.setUsers(getSessionMemberList(info.getSessionId()));
                sessions.add(info);
                cursor.moveToNext();
            }
            cursor.close();
            return sessions;
        } finally {
            close();
        }

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
        open();
        try {
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
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                info.setMessageDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                list.add(info);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } finally {
            close();
        }

    }


    //获取会话之前的消息列表
    @SuppressLint("Range")
    public List<ChatMessage> getSessionFormerMessages(String messageSession, String messageID, int size) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return new ArrayList<>();
        }
        open();

        try {
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
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                info.setMessageDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                list.add(info);
                cursor.moveToNext();
            }
            cursor.close();
            chatMessages.addAll(list);
            if (chatMessages.size() > size) {
                chatMessages = chatMessages.subList(0, size);
            }
            return chatMessages;
        } finally {
            close();
        }


    }


    //获取最近的一条消息
    @SuppressLint("Range")
    public ChatMessage getSessionLatestMessage(String messageSession) {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return null;
        }
        open();

        //查询最近一条消息
        try {
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
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                info.setMessageDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        } finally {
            close();
        }
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
        open();
        try {
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
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                info.setMessageDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        } finally {
            close();
        }
    }


    //更新还未处理的消息
    @SuppressLint("Range")
    public List<ChatMessage> getNotActionSystemMessageBySession(String sessionID) {

        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return new ArrayList<>();
        }
        open();
        try {
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
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                info.setMessageDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                list.add(info);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } finally {
            close();
        }
    }


    //获取所有还未做处理的系统消息
    @SuppressLint("Range")
    public List<ChatMessage> getNotActionSystemMessage() {
        //检查用户是否登录了
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return new ArrayList<>();
        }
        open();
        try {
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
                    "messageTableSeq ASC"
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
                info.setMessageStamp(new BigDecimal(cursor.getLong(cursor.getColumnIndex("messageStamp"))));
                info.setMessageDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("messageDate"))));
                info.setIsDelete(new BigDecimal(cursor.getInt(cursor.getColumnIndex("isDelete"))));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("deleteDate"))));
                list.add(info);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } finally {
            close();
        }
    }
}
