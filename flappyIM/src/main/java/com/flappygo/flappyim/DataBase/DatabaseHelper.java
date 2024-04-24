package com.flappygo.flappyim.DataBase;

import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;


//数据库创建
public class DatabaseHelper extends SQLiteOpenHelper {

    //创建消息表
    public final String createTableMessage = "CREATE TABLE " + DataBaseConfig.TABLE_MESSAGE + "(" +
            // 消息ID
            "messageId" + " varchar ," +
            //消息的会话
            "messageSession" + " varchar," +
            // 消息的会话类型
            "messageSessionType" + " integer," +
            //会话的offset
            "messageSessionOffset" + " integer," +
            // 当前消息的offset
            "messageTableSeq" + " integer," +
            // 消息类型
            "messageType" + " integer," +
            // 发送ID
            "messageSendId" + " varchar," +
            // 发送者外部ID
            "messageSendExtendId" + " varchar," +
            // 消息接收者ID
            "messageReceiveId" + " varchar," +
            // 消息接收外部ID
            "messageReceiveExtendId" + " varchar," +
            // 发送内容
            "messageContent" + " varchar," +
            // 发送状态
            "messageSendState" + " integer," +
            // 是否已读
            "messageReadState" + " integer," +
            // 消息发送秘钥
            "messageSecretSend" + " varchar," +
            // 消息接收秘钥
            "messageSecretReceive" + " varchar," +
            // 是否被删除
            "isDelete" + " integer," +
            // 发送日期
            "messageDate" + " varchar," +
            // 时间戳
            "messageStamp" + " integer," +
            // 发送类型
            "deleteDate" + " varchar," +
            // 当前插入的用户
            "messageInsertUser" + " varchar,"
            // 消息ID
            + " primary key (messageId,messageInsertUser)" + ")";


    //创建会话表
    public final String createTableSession = "CREATE TABLE " + DataBaseConfig.TABLE_SESSION + "(" +
            // 会话ID
            "sessionId" + " varchar ," +
            //会话外部ID
            "sessionExtendId" + " varchar," +
            // 会话类型
            "sessionType" + " integer," +
            // 会话信息
            "sessionInfo" + " varchar," +
            //会话名称
            "sessionName" + " varchar," +
            // 会话图片
            "sessionImage" + " varchar," +
            // 会话当前offset
            "sessionOffset" + " varchar," +
            // 会话更新时间戳
            "sessionStamp" + " integer," +
            // 会话创建者
            "sessionCreateDate" + " varchar," +
            // 会话创建者
            "sessionCreateUser" + " varchar," +
            // 是否删除
            "sessionDeleted" + " integer," +
            // 删除时间
            "sessionDeletedDate" + " varchar," +
            // 当前插入的用户
            "sessionInsertUser" + " varchar,"
            // 消息ID
            + " primary key (sessionId,sessionInsertUser)" + ")";


    //创建会话用户表
    public final String createTableSessionMember = "CREATE TABLE " + DataBaseConfig.TABLE_SESSION_MEMBER + "(" +
            //用户ID
            "userId" + " varchar ," +
            //用户外部ID
            "userExtendId" + " varchar," +
            //用户名称
            "userName" + " varchar," +
            //用户头像
            "userAvatar" + " varchar," +
            //用户数据
            "userData" + " varchar," +
            //用户创建时间
            "userCreateDate" + " varchar," +
            //用户登录时间
            "userLoginDate" + " varchar," +
            //会话ID
            "sessionId" + " varchar," +
            //用户最近阅读
            "sessionMemberLatestRead" + " varchar," +
            //用户在会话中的附加名称
            "sessionMemberMarkName" + " varchar," +
            //用户设置会话免打扰
            "sessionMemberNoDisturb" + " integer," +
            //用户加入会话的时间
            "sessionJoinDate" + " varchar," +
            //用户离开会话的时间
            "sessionLeaveDate" + " varchar," +
            //用户是否离开会话
            "isLeave" + " integer," +
            //当前插入的用户
            "sessionInsertUser" + " varchar,"
            // 消息ID
            + " primary key (userId,sessionId,sessionInsertUser)" + ")";


    //data helper
    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库表
        db.execSQL(createTableMessage);
        db.execSQL(createTableSession);
        db.execSQL(createTableSessionMember);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //用于升级数据库
        if (newVersion != oldVersion) {
            try {
                // 创建数据库表
                db.execSQL("DROP TABLE IF EXISTS " + DataBaseConfig.TABLE_MESSAGE + ";");
                db.execSQL("DROP TABLE IF EXISTS " + DataBaseConfig.TABLE_SESSION + ";");
                db.execSQL("DROP TABLE IF EXISTS " + DataBaseConfig.TABLE_SESSION_MEMBER + ";");
                db.execSQL(createTableMessage);
                db.execSQL(createTableSession);
                db.execSQL(createTableSessionMember);
                db.setVersion(newVersion);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
