package com.flappygo.flappyim.DataBase;

import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_IMG;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_TEXT;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_VIDEO;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_VOICE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.SEND_STATE_FAILURE;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_ACTION;
import static com.flappygo.flappyim.Models.Server.ChatMessage.MSG_TYPE_SYSTEM;

import com.flappygo.flappyim.DataBase.Models.ChatSessionMember;
import com.flappygo.flappyim.DataBase.Models.ChatSessionData;
import com.flappygo.flappyim.Models.Request.ChatAction;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Datas.DataManager;
import com.flappygo.flappyim.Tools.StringTool;

import android.database.sqlite.SQLiteDatabase;

import com.flappygo.flappyim.FlappyImService;
import com.flappygo.flappyim.Tools.TimeTool;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    public void open() {
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

    //通用的数据库操作模板方法
    private <T> T executeDbOperation(DbOperation<T> operation) {
        return executeDbOperation(operation, null);
    }

    //通用的数据库操作模板方法，支持默认值
    private <T> T executeDbOperation(DbOperation<T> operation, T defaultValue) {
        open();
        try {
            ChatUser chatUser = DataManager.getInstance().getLoginUser();
            if (chatUser != null) {
                return operation.execute(chatUser);
            } else {
                return defaultValue;
            }
        } finally {
            close();
        }
    }

    // 数据库操作接口
    @FunctionalInterface
    private interface DbOperation<T> {
        T execute(ChatUser chatUser);
    }

    /**
     * 辅助方法：如果值不为空，则将其放入 ContentValues
     *
     * @param values ContentValues 对象
     * @param key    键
     * @param value  值
     */
    private void putIfNotNull(ContentValues values, String key, Object value) {
        if (value != null) {
            if (value instanceof String) {
                values.put(key, (String) value);
            } else if (value instanceof Integer) {
                values.put(key, (Integer) value);
            } else if (value instanceof Long) {
                values.put(key, (Long) value);
            } else if (value instanceof Boolean) {
                values.put(key, (Boolean) value);
            } else if (value instanceof Float) {
                values.put(key, (Float) value);
            } else if (value instanceof Double) {
                values.put(key, (Double) value);
            } else {
                throw new IllegalArgumentException("Unsupported type for ContentValues");
            }
        }
    }

    /******
     * 清空正在发送中的消息为发送失败
     */
    public void clearSendingMessage() {
        executeDbOperation(chatUser -> {
            ContentValues values = new ContentValues();
            values.put("messageSendState", SEND_STATE_FAILURE);
            db.update(DataBaseConfig.TABLE_MESSAGE, values, "messageSendState = 0", null);
            return true;
        });
    }


    /******
     * 插入单条消息
     * @param chatMessage  消息
     */
    public boolean insertMessage(ChatMessage chatMessage) {
        if (chatMessage.getMessageSessionOffset() != null) {
            updateSessionOffset(
                    chatMessage.getMessageSessionId(),
                    chatMessage.getMessageSessionOffset()
            );
        }


        return executeDbOperation(user -> {
            ContentValues values = new ContentValues();
            putIfNotNull(values, "messageId", chatMessage.getMessageId());
            putIfNotNull(values, "messageSessionId", chatMessage.getMessageSessionId());
            putIfNotNull(values, "messageSessionType", chatMessage.getMessageSessionType());
            putIfNotNull(values, "messageSessionOffset", chatMessage.getMessageSessionOffset());
            putIfNotNull(values, "messageTableOffset", chatMessage.getMessageTableOffset());
            putIfNotNull(values, "messageType", chatMessage.getMessageType());
            putIfNotNull(values, "messageSendId", chatMessage.getMessageSendId());
            putIfNotNull(values, "messageSendExtendId", chatMessage.getMessageSendExtendId());
            putIfNotNull(values, "messageReceiveId", chatMessage.getMessageReceiveId());
            putIfNotNull(values, "messageReceiveExtendId", chatMessage.getMessageReceiveExtendId());
            putIfNotNull(values, "messageContent", chatMessage.getMessageContent());
            putIfNotNull(values, "messageSendState", chatMessage.getMessageSendState());
            putIfNotNull(values, "messageReadState", chatMessage.getMessageReadState());
            putIfNotNull(values, "messagePinState", chatMessage.getMessagePinState());
            putIfNotNull(values, "messageSecret", chatMessage.getMessageSecret());
            putIfNotNull(values, "messageDate", TimeTool.dateToStr(chatMessage.getMessageDate()));
            putIfNotNull(values, "deleteDate", TimeTool.dateToStr(chatMessage.getDeleteDate()));

            putIfNotNull(values, "messageReplyMsgId", chatMessage.getMessageReplyMsgId());
            putIfNotNull(values, "messageReplyMsgType", chatMessage.getMessageReplyMsgType());
            putIfNotNull(values, "messageReplyUserId", chatMessage.getMessageReplyUserId());
            putIfNotNull(values, "messageReplyMsgContent", chatMessage.getMessageReplyMsgContent());

            putIfNotNull(values, "messageForwardTitle", chatMessage.getMessageForwardTitle());
            putIfNotNull(values, "messageRecallUserId", chatMessage.getMessageRecallUserId());
            putIfNotNull(values, "messageAtUserIds", chatMessage.getMessageAtUserIds());
            putIfNotNull(values, "messageReadUserIds", chatMessage.getMessageReadUserIds());
            putIfNotNull(values, "messageDeleteUserIds", chatMessage.getMessageDeleteUserIds());

            ChatMessage formerMsg = getMessageById(chatMessage.getMessageId());

            ///用户
            values.put("messageInsertUser", user.getUserExtendId());
            values.put("isDelete", chatMessage.getIsDelete());
            values.put("messageStamp", formerMsg != null ?
                    Long.toString(formerMsg.getMessageStamp()) :
                    Long.toString(System.currentTimeMillis()));

            return db.insertWithOnConflict(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            ) > 0;
        }, false);
    }

    /******
     * 更新消息状态
     * @param messageId        消息ID
     * @param sendState        发送状态
     */
    public boolean updateMessageSendState(String messageId, String sendState) {
        return executeDbOperation(chatUser -> {
            ContentValues values = new ContentValues();
            values.put("messageSendState", sendState);
            return db.update(
                    DataBaseConfig.TABLE_MESSAGE,
                    values,
                    "messageInsertUser=? and messageId=? ",
                    new String[]{
                            chatUser.getUserExtendId(),
                            messageId
                    }
            ) > 0;
        });
    }

    /******
     * 更新消息已读(ACTION消息/系统消息、的已读状态不做处理)
     * @param userId        用户ID
     * @param sessionId     会话ID
     * @param tableOffset 表序号
     */
    private void updateMessageRead(String userId, String sessionId, String tableOffset) {
        executeDbOperation(chatUser -> {

            //SQL
            String sql = "UPDATE " + DataBaseConfig.TABLE_MESSAGE + " " +
                    "SET messageReadState = 1, " +
                    "messageReadUserIds = IFNULL(messageReadUserIds, '') || CASE WHEN messageReadUserIds IS NULL OR messageReadUserIds = '' THEN '' ELSE ',' END || ? " +
                    "WHERE messageInsertUser = ? AND " +
                    "messageSendId != ? AND " +
                    "messageType NOT IN (?, ?) AND " +
                    "messageSessionId = ? AND " +
                    "messageTableOffset <= ?";

            //Prepare the statement with the actual values
            Object[] args = new Object[]{
                    //New user ID to add
                    userId,
                    //messageInsertUser
                    chatUser.getUserExtendId(),
                    //messageSendId (to exclude)
                    userId,
                    //First messageType to exclude
                    MSG_TYPE_SYSTEM,
                    //Second messageType to exclude
                    MSG_TYPE_ACTION,
                    //messageSessionId
                    sessionId,
                    //messageTableOffset
                    tableOffset
            };

            //Execute the update
            db.execSQL(sql, args);

            return true;
        });
    }

    /******
     * 更新消息已读(系统消息的已读状态不做处理)
     * @param messageId        消息ID
     */
    private void updateMessageReadByMsgId(String messageId) {
        executeDbOperation(chatUser -> {
            ContentValues values = new ContentValues();
            values.put("messageReadState", 1);
            db.update(
                    DataBaseConfig.TABLE_MESSAGE,
                    values,
                    "messageInsertUser=? and " +
                            "messageId==? ",
                    new String[]{
                            chatUser.getUserExtendId(),
                            messageId,
                    }
            );
            return true;
        });
    }


    /******
     * 获取未读消息数量
     * @param sessionID 会话ID
     * @return 未读消息数量
     */
    public int getSessionMessageUnReadCount(String sessionID) {
        return executeDbOperation(chatUser -> {
            String countQuery = "SELECT COUNT(*) FROM " + DataBaseConfig.TABLE_MESSAGE +
                    " WHERE messageInsertUser = ? " +
                    "and messageSessionId = ? " +
                    "and messageSendId != ? " +
                    "and messageReadState = 0 " +
                    "and (messageRecallUserId is null or messageRecallUserId == '')" +
                    "and (messageDeleteUserIds not like ?)" +
                    String.format(Locale.US, "and messageType != %d ", MSG_TYPE_SYSTEM) +
                    String.format(Locale.US, "and messageType != %d", MSG_TYPE_ACTION);
            Cursor cursor = db.rawQuery(countQuery, new String[]{
                    chatUser.getUserExtendId(),
                    sessionID,
                    chatUser.getUserId(),
                    "%" + chatUser.getUserId() + "%"
            });
            int count = 0;
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
            return count;
        }, 0);
    }

    /*****
     * @param sessionId 会话ID
     * @return 返回数据
     */
    @SuppressLint("Range")
    public boolean getSessionIsTempDelete(String sessionId) {
        return executeDbOperation(chatUser -> {

            long latestMessageOffset = 0;
            long latestMessageOffsetDelete = 0;

            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageSessionId = ? and messageInsertUser = ? and messageType != ? and isDelete != 1 ",
                    new String[]{
                            sessionId,
                            chatUser.getUserExtendId(),
                            Integer.toString(MSG_TYPE_ACTION),
                    },
                    null,
                    null,
                    "messageTableOffset DESC,messageStamp DESC LIMIT 1"
            );
            if (cursor.moveToFirst()) {
                latestMessageOffset = cursor.getLong(cursor.getColumnIndex("messageSessionOffset"));
                cursor.close();
            } else {
                cursor.close();
            }

            cursor = db.query(
                    DataBaseConfig.TABLE_SESSION_MEMBER,
                    null,
                    "sessionId = ? and userId = ? and sessionInsertUser= ?",
                    new String[]{
                            sessionId,
                            chatUser.getUserId(),
                            chatUser.getUserExtendId()
                    },
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                latestMessageOffsetDelete = cursor.getLong(cursor.getColumnIndex("sessionMemberLatestDelete"));
                cursor.close();
            } else {
                cursor.close();
            }

            return (latestMessageOffsetDelete >= latestMessageOffset && latestMessageOffset != 0);
        });
    }


    /******
     * 插入多个会话
     * @param  sessionModelList  会话列表
     */
    public void insertSessions(List<ChatSessionData> sessionModelList) {
        if (sessionModelList == null || sessionModelList.isEmpty()) {
            return;
        }
        executeDbOperation(chatUser -> {
            db.beginTransaction();
            try {
                for (ChatSessionData sessionModel : sessionModelList) {
                    insertSession(sessionModel);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            return true;
        });
    }

    /******
     * 获取用户的所有会话列表
     * @return 所有的会话数据
     */
    @SuppressLint("Range")
    public List<ChatSessionData> getUserSessions() {
        return executeDbOperation(chatUser -> {
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_SESSION,
                    null,
                    "sessionInsertUser=? ",
                    new String[]{
                            chatUser.getUserExtendId()
                    },
                    null,
                    null,
                    null
            );

            List<ChatSessionData> sessionList = new ArrayList<>();
            if (!cursor.moveToFirst()) {
                cursor.close();
                return sessionList;
            }
            while (!cursor.isAfterLast()) {
                ChatSessionData info = new ChatSessionData();
                info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
                info.setSessionExtendId(cursor.getString(cursor.getColumnIndex("sessionExtendId")));
                info.setSessionType(cursor.getInt(cursor.getColumnIndex("sessionType")));
                info.setSessionInfo(cursor.getString(cursor.getColumnIndex("sessionInfo")));
                info.setSessionName(cursor.getString(cursor.getColumnIndex("sessionName")));
                info.setSessionImage(cursor.getString(cursor.getColumnIndex("sessionImage")));
                info.setSessionOffset(cursor.getLong(cursor.getColumnIndex("sessionOffset")));
                info.setSessionStamp(cursor.getLong(cursor.getColumnIndex("sessionStamp")));
                info.setSessionCreateDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionCreateDate"))));
                info.setSessionCreateUser(cursor.getString(cursor.getColumnIndex("sessionCreateUser")));
                info.setIsDelete(cursor.getInt(cursor.getColumnIndex("sessionDeleted")));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionDeletedDate"))));
                info.setUnReadMessageCount(getSessionMessageUnReadCount(info.getSessionId()));
                info.setDeleteTemp(getSessionIsTempDelete(info.getSessionId()));
                info.setUsers(getSessionMemberList(info.getSessionId()));
                sessionList.add(info);
                cursor.moveToNext();
            }
            cursor.close();
            return sessionList;
        }, new ArrayList<>());
    }

    /******
     * 插入数据
     * @param session 会话
     */
    public void insertSession(ChatSessionData session) {
        executeDbOperation(user -> {
            ContentValues values = new ContentValues();
            putIfNotNull(values, "sessionId", session.getSessionId());
            putIfNotNull(values, "sessionExtendId", session.getSessionExtendId());
            putIfNotNull(values, "sessionType", session.getSessionType());
            putIfNotNull(values, "sessionInfo", session.getSessionInfo());
            putIfNotNull(values, "sessionName", session.getSessionName());
            putIfNotNull(values, "sessionImage", session.getSessionImage());
            putIfNotNull(values, "sessionOffset", session.getSessionOffset());
            putIfNotNull(values, "sessionStamp", session.getSessionStamp());
            putIfNotNull(values, "sessionCreateDate", TimeTool.dateToStr(session.getSessionCreateDate()));
            putIfNotNull(values, "sessionCreateUser", session.getSessionCreateUser());
            putIfNotNull(values, "sessionDeleted", session.getIsDelete());
            putIfNotNull(values, "sessionDeletedDate", TimeTool.dateToStr(session.getDeleteDate()));
            values.put("sessionInsertUser", user.getUserExtendId());

            db.insertWithOnConflict(
                    DataBaseConfig.TABLE_SESSION,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );

            if (session.getUsers() != null && !session.getUsers().isEmpty()) {
                for (ChatSessionMember memberModel : session.getUsers()) {
                    insertSessionMember(memberModel);
                }
            }
            return true;
        });
    }

    /******
     * 获取当前用户的会话
     * @param sessionId  会话ID
     * @return 会话
     */
    @SuppressLint("Range")
    public ChatSessionData getUserSessionById(String sessionId) {
        return executeDbOperation(chatUser -> {
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
            if (cursor.moveToFirst()) {
                ChatSessionData info = new ChatSessionData();
                info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
                info.setSessionExtendId(cursor.getString(cursor.getColumnIndex("sessionExtendId")));
                info.setSessionType(cursor.getInt(cursor.getColumnIndex("sessionType")));
                info.setSessionInfo(cursor.getString(cursor.getColumnIndex("sessionInfo")));
                info.setSessionName(cursor.getString(cursor.getColumnIndex("sessionName")));
                info.setSessionImage(cursor.getString(cursor.getColumnIndex("sessionImage")));
                info.setSessionOffset(cursor.getLong(cursor.getColumnIndex("sessionOffset")));
                info.setSessionStamp(cursor.getLong(cursor.getColumnIndex("sessionStamp")));
                info.setSessionCreateDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionCreateDate"))));
                info.setSessionCreateUser(cursor.getString(cursor.getColumnIndex("sessionCreateUser")));
                info.setIsDelete(cursor.getInt(cursor.getColumnIndex("sessionDeleted")));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionDeletedDate"))));
                info.setUnReadMessageCount(getSessionMessageUnReadCount(sessionId));
                info.setDeleteTemp(getSessionIsTempDelete(info.getSessionId()));
                info.setUsers(getSessionMemberList(info.getSessionId()));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        });
    }

    /******
     * 获取当前用户的会话
     * @param sessionExtendID  会话外部ID
     * @return 会话
     */
    @SuppressLint("Range")
    public ChatSessionData getUserSessionByExtendId(String sessionExtendID) {
        return executeDbOperation(chatUser -> {
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_SESSION,
                    null,
                    "sessionExtendId=? and sessionInsertUser=? ",
                    new String[]{sessionExtendID, chatUser.getUserExtendId()},
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                ChatSessionData info = new ChatSessionData();
                info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
                info.setSessionExtendId(cursor.getString(cursor.getColumnIndex("sessionExtendId")));
                info.setSessionType(cursor.getInt(cursor.getColumnIndex("sessionType")));
                info.setSessionInfo(cursor.getString(cursor.getColumnIndex("sessionInfo")));
                info.setSessionName(cursor.getString(cursor.getColumnIndex("sessionName")));
                info.setSessionImage(cursor.getString(cursor.getColumnIndex("sessionImage")));
                info.setSessionOffset(cursor.getLong(cursor.getColumnIndex("sessionOffset")));
                info.setSessionStamp(cursor.getLong(cursor.getColumnIndex("sessionStamp")));
                info.setSessionCreateDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionCreateDate"))));
                info.setSessionCreateUser(cursor.getString(cursor.getColumnIndex("sessionCreateUser")));
                info.setIsDelete(cursor.getInt(cursor.getColumnIndex("sessionDeleted")));
                info.setDeleteDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionDeletedDate"))));
                info.setUnReadMessageCount(getSessionMessageUnReadCount(info.getSessionId()));
                info.setDeleteTemp(getSessionIsTempDelete(info.getSessionId()));
                info.setUsers(getSessionMemberList(info.getSessionId()));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        });
    }

    /******
     * 删除用户会话
     * @param sessionId 会话ID
     */
    public void deleteUserSession(String sessionId) {
        executeDbOperation(chatUser -> {
            ContentValues values = new ContentValues();
            values.put("sessionDeleted", 1);
            db.update(
                    DataBaseConfig.TABLE_SESSION,
                    values,
                    "sessionId = ? and sessionInsertUser = ?",
                    new String[]{sessionId, chatUser.getUserExtendId()}
            );
            return true;
        });
    }

    /******
     * 插入会话用户
     * @param member 会话用户
     */
    public void insertSessionMember(ChatSessionMember member) {
        executeDbOperation(user -> {
            ContentValues values = new ContentValues();
            putIfNotNull(values, "userId", member.getUserId());
            putIfNotNull(values, "userExtendId", member.getUserExtendId());
            putIfNotNull(values, "userName", member.getUserName());
            putIfNotNull(values, "userAvatar", member.getUserAvatar());
            putIfNotNull(values, "userData", member.getUserData());
            putIfNotNull(values, "userCreateDate", TimeTool.dateToStr(member.getUserCreateDate()));
            putIfNotNull(values, "userLoginDate", TimeTool.dateToStr(member.getUserLoginDate()));
            putIfNotNull(values, "sessionId", member.getSessionId());
            putIfNotNull(values, "sessionMemberLatestRead", member.getSessionMemberLatestRead());
            putIfNotNull(values, "sessionMemberLatestDelete", member.getSessionMemberLatestDelete());
            putIfNotNull(values, "sessionMemberMarkName", member.getSessionMemberMarkName());
            putIfNotNull(values, "sessionMemberMute", member.getSessionMemberMute());
            putIfNotNull(values, "sessionMemberPinned", member.getSessionMemberPinned());
            putIfNotNull(values, "sessionJoinDate", TimeTool.dateToStr(member.getSessionJoinDate()));
            putIfNotNull(values, "sessionLeaveDate", TimeTool.dateToStr(member.getSessionLeaveDate()));
            putIfNotNull(values, "isLeave", member.getIsLeave());
            values.put("sessionInsertUser", user.getUserExtendId());
            db.insertWithOnConflict(
                    DataBaseConfig.TABLE_SESSION_MEMBER,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
            return true;
        });
    }

    /******
     * 获取会话用户
     * @param sessionId 会话ID
     * @param memberId  用户ID
     */
    @SuppressLint("Range")
    public ChatSessionMember getSessionMember(String sessionId, String memberId) {
        return executeDbOperation(chatUser -> {
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
            if (!cursor.moveToFirst()) {
                cursor.close();
                return null;
            }
            if (!cursor.isAfterLast()) {
                ChatSessionMember info = new ChatSessionMember();
                info.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
                info.setUserExtendId(cursor.getString(cursor.getColumnIndex("userExtendId")));
                info.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                info.setUserAvatar(cursor.getString(cursor.getColumnIndex("userAvatar")));
                info.setUserData(cursor.getString(cursor.getColumnIndex("userData")));
                info.setUserCreateDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("userCreateDate"))));
                info.setUserLoginDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("userLoginDate"))));
                info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
                info.setSessionMemberLatestRead(cursor.getLong(cursor.getColumnIndex("sessionMemberLatestRead")));
                info.setSessionMemberLatestDelete(cursor.getLong(cursor.getColumnIndex("sessionMemberLatestDelete")));
                info.setSessionMemberMarkName(cursor.getString(cursor.getColumnIndex("sessionMemberMarkName")));
                info.setSessionMemberMute(cursor.getInt(cursor.getColumnIndex("sessionMemberMute")));
                info.setSessionMemberPinned(cursor.getInt(cursor.getColumnIndex("sessionMemberPinned")));
                info.setSessionJoinDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionJoinDate"))));
                info.setSessionLeaveDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionLeaveDate"))));
                info.setIsLeave(cursor.getInt(cursor.getColumnIndex("isLeave")));
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        });
    }

    /******
     * 获取会话用户列表
     * @param sessionId 会话ID
     */
    @SuppressLint("Range")
    public List<ChatSessionMember> getSessionMemberList(String sessionId) {
        return executeDbOperation(chatUser -> {
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
            List<ChatSessionMember> list = new ArrayList<>();
            if (!cursor.moveToFirst()) {
                cursor.close();
                return list;
            }
            while (!cursor.isAfterLast()) {
                ChatSessionMember info = new ChatSessionMember();
                info.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
                info.setUserExtendId(cursor.getString(cursor.getColumnIndex("userExtendId")));
                info.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                info.setUserAvatar(cursor.getString(cursor.getColumnIndex("userAvatar")));
                info.setUserData(cursor.getString(cursor.getColumnIndex("userData")));
                info.setUserCreateDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("userCreateDate"))));
                info.setUserLoginDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("userLoginDate"))));
                info.setSessionId(cursor.getString(cursor.getColumnIndex("sessionId")));
                info.setSessionMemberLatestRead(cursor.getLong(cursor.getColumnIndex("sessionMemberLatestRead")));
                info.setSessionMemberLatestDelete(cursor.getLong(cursor.getColumnIndex("sessionMemberLatestDelete")));
                info.setSessionMemberMarkName(cursor.getString(cursor.getColumnIndex("sessionMemberMarkName")));
                info.setSessionMemberMute(cursor.getInt(cursor.getColumnIndex("sessionMemberMute")));
                info.setSessionMemberPinned(cursor.getInt(cursor.getColumnIndex("sessionMemberPinned")));
                info.setSessionJoinDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionJoinDate"))));
                info.setSessionLeaveDate(TimeTool.strToDate(cursor.getString(cursor.getColumnIndex("sessionLeaveDate"))));
                info.setIsLeave(cursor.getInt(cursor.getColumnIndex("isLeave")));
                list.add(info);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        }, new ArrayList<>());
    }

    //获取最近的一条消息
    @SuppressLint("Range")
    public ChatMessage getMessageById(String messageId) {
        return executeDbOperation(chatUser -> {
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageId = ? and messageInsertUser = ?",
                    new String[]{messageId, chatUser.getUserExtendId()},
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                ChatMessage info = new ChatMessage(cursor);
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        });
    }

    //获取最近的一条消息sessionOffset
    @SuppressLint("Range")
    public Long getSessionOffsetLatest(String messageSessionId) {
        return executeDbOperation(chatUser -> {
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageSessionId = ? and messageInsertUser = ? and messageType != ? and messageSendState in (1,2,3,4) ",
                    new String[]{
                            messageSessionId,
                            chatUser.getUserExtendId(),
                            Integer.toString(MSG_TYPE_ACTION),
                    },
                    null,
                    null,
                    "messageTableOffset desc,messageStamp desc limit 1"
            );
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndex("messageSessionOffset"));
            }
            cursor.close();
            return Long.valueOf(0);
        });
    }

    //获取最近的一条消息
    @SuppressLint("Range")
    public ChatMessage getSessionLatestMessage(String messageSessionId) {
        return executeDbOperation(chatUser -> {
            //当前用户
            ChatSessionMember chatSessionMember = getSessionMember(messageSessionId, chatUser.getUserId());
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageSessionId = ? " +
                            "and messageInsertUser = ? " +
                            "and messageType != ? " +
                            "and messageSessionOffset > ? " +
                            "and isDelete != 1 ",
                    new String[]{
                            messageSessionId,
                            chatUser.getUserExtendId(),
                            Integer.toString(MSG_TYPE_ACTION),
                            chatSessionMember != null ? chatSessionMember.getSessionMemberLatestDelete().toString() : "0",
                    },
                    null,
                    null,
                    "messageTableOffset DESC,messageStamp DESC LIMIT 1"
            );
            if (cursor.moveToFirst()) {
                ChatMessage info = new ChatMessage(cursor);
                cursor.close();
                return info;
            }
            cursor.close();
            return null;
        });
    }


    //获取会话之前的消息列表
    @SuppressLint("Range")
    public List<ChatMessage> getSessionFormerMessages(String messageSessionId, String messageID, int size) {
        return executeDbOperation(chatUser -> {
            //消息
            ChatMessage chatMessage = getMessageById(messageID);

            //当前用户
            ChatSessionMember chatSessionMember = getSessionMember(messageSessionId, chatUser.getUserId());

            //查询比它小的消息
            List<ChatMessage> retMessages = new ArrayList<>();
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageSessionId = ? " +
                            "and (messageTableOffset < ? or (messageTableOffset = ? and messageStamp < ?)) " +
                            "and messageSessionOffset > ? " +
                            "and messageInsertUser = ? " +
                            "and messageType != ? " +
                            "and isDelete != 1 ",
                    new String[]{
                            messageSessionId,
                            chatMessage.getMessageTableOffset().toString(),
                            chatMessage.getMessageTableOffset().toString(),
                            chatMessage.getMessageStamp().toString(),
                            chatSessionMember != null ? chatSessionMember.getSessionMemberLatestDelete().toString() : "0",
                            chatUser.getUserExtendId(),
                            Integer.toString(MSG_TYPE_ACTION),
                    },
                    null,
                    null,
                    "messageTableOffset desc,messageStamp desc limit " + size
            );
            //全部数据转换
            if (!cursor.moveToFirst()) {
                cursor.close();
            } else {
                while (!cursor.isAfterLast()) {
                    ChatMessage info = new ChatMessage(cursor);
                    retMessages.add(info);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return retMessages;
        }, new ArrayList<>());
    }


    //获取会话之前的消息列表
    @SuppressLint("Range")
    public List<ChatMessage> getSessionNewerMessages(String messageSessionId, String messageID, int size) {
        return executeDbOperation(chatUser -> {
            //消息
            ChatMessage chatMessage = getMessageById(messageID);

            //查询比它大的消息
            List<ChatMessage> retMessages = new ArrayList<>();
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageSessionId = ? " +
                            "and (messageTableOffset > ? or (messageTableOffset = ? and messageStamp > ?)) " +
                            "and messageInsertUser = ? " +
                            "and messageType != ? " +
                            "and isDelete != 1 ",
                    new String[]{
                            messageSessionId,
                            chatMessage.getMessageTableOffset().toString(),
                            chatMessage.getMessageTableOffset().toString(),
                            chatMessage.getMessageStamp().toString(),
                            chatUser.getUserExtendId(),
                            Integer.toString(MSG_TYPE_ACTION),
                    },
                    null,
                    null,
                    "messageTableOffset DESC,messageStamp desc limit " + size
            );

            //全部数据转换
            if (!cursor.moveToFirst()) {
                cursor.close();
            } else {
                while (!cursor.isAfterLast()) {
                    ChatMessage info = new ChatMessage(cursor);
                    retMessages.add(info);
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return retMessages;
        }, new ArrayList<>());
    }


    ///搜索文本消息
    @SuppressLint("Range")
    public List<ChatMessage> searchTextMessageList(String text, String sessionId, String messageID, int size) {
        return executeDbOperation(chatUser -> {

            //查询比它小的消息
            List<ChatMessage> retMessages = new ArrayList<>();

            //查询语句
            String queryStr = "1=1 ";
            List<String> paramList = new ArrayList<>();

            //会话不为空
            if (!StringTool.isEmpty(sessionId)) {
                ChatSessionMember chatSessionMember = getSessionMember(sessionId, chatUser.getUserId());
                queryStr += "and messageSessionId = ? ";
                queryStr += "and messageSessionOffset > ? ";
                paramList.add(sessionId);
                paramList.add(chatSessionMember != null ? chatSessionMember.getSessionMemberLatestDelete().toString() : "0");
            }

            //查询文本
            if (!StringTool.isEmpty(text)) {
                queryStr += "and messageContent like ? ";
                paramList.add("%" + text + "%");
            }

            //消息ID
            if (!StringTool.isEmpty(messageID)) {
                ChatMessage chatMessage = getMessageById(messageID);
                queryStr += "and (messageTableOffset < ? or (messageTableOffset = ? and messageStamp < ?)) ";
                paramList.add(chatMessage.getMessageTableOffset().toString());
                paramList.add(chatMessage.getMessageTableOffset().toString());
                paramList.add(chatMessage.getMessageStamp().toString());
            }

            //插入者
            queryStr += "and messageInsertUser = ? ";
            paramList.add(chatUser.getUserExtendId());

            //消息类型
            queryStr += "and messageType = ? and isDelete != 1 ";
            paramList.add(Integer.toString(MSG_TYPE_TEXT));

            //查询
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    queryStr,
                    paramList.toArray(new String[0]),
                    null,
                    null,
                    "messageTableOffset desc,messageStamp desc limit " + size
            );

            //全部数据转换
            if (!cursor.moveToFirst()) {
                cursor.close();
            } else {
                while (!cursor.isAfterLast()) {
                    ChatMessage info = new ChatMessage(cursor);
                    retMessages.add(info);
                    cursor.moveToNext();
                }
                cursor.close();
            }

            //返回的消息
            return retMessages;
        }, new ArrayList<>());
    }


    ///搜索文本消息
    @SuppressLint("Range")
    public List<ChatMessage> searchImageMessageList(String sessionId, String messageID, int size) {
        return executeDbOperation(chatUser -> {

            //查询比它小的消息
            List<ChatMessage> retMessages = new ArrayList<>();

            //查询语句
            String queryStr = "1=1 ";
            List<String> paramList = new ArrayList<>();

            //会话不为空
            if (!StringTool.isEmpty(sessionId)) {
                ChatSessionMember chatSessionMember = getSessionMember(sessionId, chatUser.getUserId());
                queryStr += "and messageSessionId = ? ";
                queryStr += "and messageSessionOffset > ? ";
                paramList.add(sessionId);
                paramList.add(chatSessionMember != null ? chatSessionMember.getSessionMemberLatestDelete().toString() : "0");
            }

            //消息ID
            if (!StringTool.isEmpty(messageID)) {
                ChatMessage chatMessage = getMessageById(messageID);
                queryStr += "and (messageTableOffset < ? or (messageTableOffset = ? and messageStamp < ?)) ";
                paramList.add(chatMessage.getMessageTableOffset().toString());
                paramList.add(chatMessage.getMessageTableOffset().toString());
                paramList.add(chatMessage.getMessageStamp().toString());
            }

            //插入者
            queryStr += "and messageInsertUser = ? ";
            paramList.add(chatUser.getUserExtendId());

            //消息类型
            queryStr += "and messageType = ? and isDelete != 1 ";
            paramList.add(Integer.toString(MSG_TYPE_IMG));

            //查询
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    queryStr,
                    paramList.toArray(new String[0]),
                    null,
                    null,
                    "messageTableOffset desc,messageStamp desc limit " + size
            );

            //全部数据转换
            if (!cursor.moveToFirst()) {
                cursor.close();
            } else {
                while (!cursor.isAfterLast()) {
                    ChatMessage info = new ChatMessage(cursor);
                    retMessages.add(info);
                    cursor.moveToNext();
                }
                cursor.close();
            }

            //返回的消息
            return retMessages;
        }, new ArrayList<>());
    }


    ///搜索视频消息
    @SuppressLint("Range")
    public List<ChatMessage> searchVideoMessageList(String sessionId, String messageID, int size) {
        return executeDbOperation(chatUser -> {

            //查询比它小的消息
            List<ChatMessage> retMessages = new ArrayList<>();

            //查询语句
            String queryStr = "1=1 ";
            List<String> paramList = new ArrayList<>();

            //会话不为空
            if (!StringTool.isEmpty(sessionId)) {
                ChatSessionMember chatSessionMember = getSessionMember(sessionId, chatUser.getUserId());
                queryStr += "and messageSessionId = ? ";
                queryStr += "and messageSessionOffset > ? ";
                paramList.add(sessionId);
                paramList.add(chatSessionMember != null ? chatSessionMember.getSessionMemberLatestDelete().toString() : "0");
            }

            //消息ID
            if (!StringTool.isEmpty(messageID)) {
                ChatMessage chatMessage = getMessageById(messageID);
                queryStr += "and (messageTableOffset < ? or (messageTableOffset = ? and messageStamp < ?)) ";
                paramList.add(chatMessage.getMessageTableOffset().toString());
                paramList.add(chatMessage.getMessageTableOffset().toString());
                paramList.add(chatMessage.getMessageStamp().toString());
            }

            //插入者
            queryStr += "and messageInsertUser = ? ";
            paramList.add(chatUser.getUserExtendId());

            //消息类型
            queryStr += "and messageType = ? and isDelete != 1 ";
            paramList.add(Integer.toString(MSG_TYPE_VIDEO));

            //查询
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    queryStr,
                    paramList.toArray(new String[0]),
                    null,
                    null,
                    "messageTableOffset desc,messageStamp desc limit " + size
            );

            //全部数据转换
            if (!cursor.moveToFirst()) {
                cursor.close();
            } else {
                while (!cursor.isAfterLast()) {
                    ChatMessage info = new ChatMessage(cursor);
                    retMessages.add(info);
                    cursor.moveToNext();
                }
                cursor.close();
            }

            //返回的消息
            return retMessages;
        }, new ArrayList<>());
    }

    ///搜索语音消息
    @SuppressLint("Range")
    public List<ChatMessage> searchVoiceMessageList(String sessionId, String messageID, int size) {
        return executeDbOperation(chatUser -> {

            //查询比它小的消息
            List<ChatMessage> retMessages = new ArrayList<>();

            //查询语句
            String queryStr = "1=1 ";
            List<String> paramList = new ArrayList<>();

            //会话不为空
            if (!StringTool.isEmpty(sessionId)) {
                ChatSessionMember chatSessionMember = getSessionMember(sessionId, chatUser.getUserId());
                queryStr += "and messageSessionId = ? ";
                queryStr += "and messageSessionOffset > ? ";
                paramList.add(sessionId);
                paramList.add(chatSessionMember != null ? chatSessionMember.getSessionMemberLatestDelete().toString() : "0");
            }

            //消息ID
            if (!StringTool.isEmpty(messageID)) {
                ChatMessage chatMessage = getMessageById(messageID);
                queryStr += "and (messageTableOffset < ? or (messageTableOffset = ? and messageStamp < ?)) ";
                paramList.add(chatMessage.getMessageTableOffset().toString());
                paramList.add(chatMessage.getMessageTableOffset().toString());
                paramList.add(chatMessage.getMessageStamp().toString());
            }

            //插入者
            queryStr += "and messageInsertUser = ? ";
            paramList.add(chatUser.getUserExtendId());

            //消息类型
            queryStr += "and messageType = ? and isDelete != 1 ";
            paramList.add(Integer.toString(MSG_TYPE_VOICE));

            //查询
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    queryStr,
                    paramList.toArray(new String[0]),
                    null,
                    null,
                    "messageTableOffset desc,messageStamp desc limit " + size
            );

            //全部数据转换
            if (!cursor.moveToFirst()) {
                cursor.close();
            } else {
                while (!cursor.isAfterLast()) {
                    ChatMessage info = new ChatMessage(cursor);
                    retMessages.add(info);
                    cursor.moveToNext();
                }
                cursor.close();
            }

            //返回的消息
            return retMessages;
        }, new ArrayList<>());
    }


    //更新还未处理的消息
    @SuppressLint("Range")
    public List<ChatMessage> getNotActionSystemMessageBySessionId(String sessionID) {
        return executeDbOperation(chatUser -> {
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageType = 0 and messageReadState = 0 and messageSessionId = ? and messageInsertUser = ?",
                    new String[]{sessionID, chatUser.getUserExtendId()},
                    null,
                    null,
                    "messageTableOffset DESC"
            );
            List<ChatMessage> list = new ArrayList<>();
            if (!cursor.moveToFirst()) {
                cursor.close();
                return list;
            }
            while (!cursor.isAfterLast()) {
                ChatMessage info = new ChatMessage(cursor);
                list.add(info);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        }, new ArrayList<>());
    }

    //获取所有还未做处理的系统消息
    @SuppressLint("Range")
    public List<ChatMessage> getNotActionSystemMessage() {
        return executeDbOperation(chatUser -> {
            List<ChatMessage> list = new ArrayList<>();
            Cursor cursor = db.query(
                    DataBaseConfig.TABLE_MESSAGE,
                    null,
                    "messageReadState = 0 and messageType = ? and messageInsertUser = ?",
                    new String[]{
                            Integer.toString(MSG_TYPE_SYSTEM),
                            chatUser.getUserExtendId()
                    },
                    null,
                    null,
                    "messageTableOffset ASC"
            );
            if (!cursor.moveToFirst()) {
                cursor.close();
                return list;
            }
            while (!cursor.isAfterLast()) {
                ChatMessage info = new ChatMessage(cursor);
                list.add(info);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        }, new ArrayList<>());
    }

    /******
     * 处理动作消息
     * @param chatMessage 消息
     */
    public void handleActionMessageUpdate(ChatMessage chatMessage) {
        ChatUser chatUser = DataManager.getInstance().getLoginUser();
        if (chatUser == null) {
            return;
        }
        if (chatMessage.getMessageType() != ChatMessage.MSG_TYPE_ACTION) {
            return;
        }
        ChatAction action = chatMessage.getChatAction();
        switch (action.getActionType()) {
            case ChatMessage.ACTION_TYPE_MSG_RECALL: {
                String userId = action.getActionIds().get(0);
                String messageId = action.getActionIds().get(2);
                updateMessageRecall(userId, messageId);
                break;
            }
            case ChatMessage.ACTION_TYPE_MSG_DELETE: {
                String userId = action.getActionIds().get(0);
                String messageId = action.getActionIds().get(2);
                updateMessageDelete(userId, messageId);
                break;
            }
            case ChatMessage.ACTION_TYPE_SESSION_READ: {
                String userId = action.getActionIds().get(0);
                String sessionId = action.getActionIds().get(1);
                String tableOffset = action.getActionIds().get(2);
                updateMessageRead(userId, sessionId, tableOffset);
                updateSessionMemberLatestRead(sessionId, userId, tableOffset);
                break;
            }
            case ChatMessage.ACTION_TYPE_SESSION_MUTE: {
                String userId = action.getActionIds().get(0);
                String sessionId = action.getActionIds().get(1);
                String mute = action.getActionIds().get(2);
                updateSessionMemberMute(sessionId, userId, mute);
                break;
            }
            case ChatMessage.ACTION_TYPE_SESSION_PIN: {
                String userId = action.getActionIds().get(0);
                String sessionId = action.getActionIds().get(1);
                String pinned = action.getActionIds().get(2);
                updateSessionMemberPinned(sessionId, userId, pinned);
                break;
            }
            case ChatMessage.ACTION_TYPE_SESSION_DELETE_TEMP: {
                String userId = action.getActionIds().get(0);
                String sessionId = action.getActionIds().get(1);
                String sessionOffset = action.getActionIds().get(2);
                updateSessionDeleteTemp(sessionId, userId, sessionOffset);
                break;
            }
            case ChatMessage.ACTION_TYPE_SESSION_DELETE_PERMANENT: {
                String userId = action.getActionIds().get(0);
                String sessionId = action.getActionIds().get(1);
                String sessionOffset = action.getActionIds().get(2);
                updateSessionDeletePermanent(sessionId, userId, sessionOffset);
                break;
            }
        }
        updateMessageReadByMsgId(chatMessage.getMessageId());
    }

    /******
     * 撤回消息就是完全删除
     * @param messageId    消息ID
     */
    public void updateMessageRecall(String userId, String messageId) {
        executeDbOperation(chatUser -> {
            ContentValues values = new ContentValues();
            values.put("isDelete", 1);
            values.put("messageRecallUserId", userId);
            values.put("messageReadState", 1);
            db.update(
                    DataBaseConfig.TABLE_MESSAGE,
                    values,
                    "messageInsertUser=? and messageId = ?",
                    new String[]{
                            chatUser.getUserExtendId(),
                            messageId,
                    }
            );
            return true;
        });
    }

    /******
     * 设置删除消息，删除消息时，整个消息不删除，
     * 只是在messageDeleteUserIds中增加delete
     * @param messageId    消息ID
     */
    public void updateMessageDelete(String userId, String messageId) {
        ChatMessage message = getMessageById(messageId);
        if (message == null || message.getIsDelete() == 1) {
            return;
        }

        message.setIsDelete(0);
        Set<String> userIdList = new HashSet<>(StringTool.splitStr(message.getMessageDeleteUserIds(), ","));
        userIdList.add(userId);
        message.setMessageDeleteUserIds(StringTool.joinListStr(new ArrayList<>(userIdList), ","));
        message.setMessageReadState(1);
        insertMessage(message);
    }

    /******
     * 更新用户消息最近已读
     * @param sessionId     会话ID
     * @param sessionOffset 会话Offset
     */
    private void updateSessionOffset(String sessionId, Long sessionOffset) {
        executeDbOperation(chatUser -> {
            String sql = "UPDATE " + DataBaseConfig.TABLE_SESSION +
                    " SET sessionOffset = MAX(sessionOffset, ?) " +
                    " WHERE sessionId = ? AND sessionInsertUser = ?";
            db.execSQL(sql, new Object[]{sessionOffset, sessionId, chatUser.getUserExtendId()});
            return true;
        });
    }

    /******
     * 更新用户消息最近已读
     * @param userId        用户ID
     * @param sessionId     会话ID
     * @param tableOffset 表序号
     */
    private void updateSessionMemberLatestRead(String sessionId, String userId, String tableOffset) {
        ChatSessionMember memberModel = getSessionMember(sessionId, userId);
        if (memberModel != null) {
            memberModel.setSessionMemberLatestRead(StringTool.strToLong(tableOffset));
            insertSessionMember(memberModel);
        }
    }

    /******
     * 更新会话用户mute
     * @param userId 用户id
     * @param sessionId 会话id
     * @param mute 是否静音
     */
    private void updateSessionMemberMute(String sessionId, String userId, String mute) {
        ChatSessionMember memberModel = getSessionMember(sessionId, userId);
        if (memberModel != null) {
            memberModel.setSessionMemberMute(StringTool.strToInt(mute, 0));
            insertSessionMember(memberModel);
        }
    }

    /******
     * 更新会话用户mute
     * @param userId 用户id
     * @param sessionId 会话id
     * @param pinned 是否置顶
     */
    private void updateSessionMemberPinned(String sessionId, String userId, String pinned) {
        ChatSessionMember memberModel = getSessionMember(sessionId, userId);
        if (memberModel != null) {
            memberModel.setSessionMemberPinned(StringTool.strToInt(pinned, 0));
            insertSessionMember(memberModel);
        }
    }

    /******
     * 更新会话用户pinned
     * @param sessionId        会话id
     * @param userId           用户id
     * @param sessionOffset    是否置顶
     */
    private void updateSessionDeleteTemp(String sessionId, String userId, String sessionOffset) {
        ChatSessionMember memberModel = getSessionMember(sessionId, userId);
        if (memberModel != null) {
            memberModel.setSessionMemberLatestDelete(StringTool.strToLong(sessionOffset));
            insertSessionMember(memberModel);
        }
    }

    /******
     * 更新会话用户permanent
     * @param sessionId        会话id
     * @param userId           用户id
     * @param sessionOffset    是否置顶
     */
    private void updateSessionDeletePermanent(String sessionId, String userId, String sessionOffset) {
        ChatSessionData session = getUserSessionById(sessionId);
        if (session != null) {
            session.setIsDelete(1);
            insertSession(session);
        }
    }

}