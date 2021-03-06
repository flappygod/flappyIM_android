package com.flappygo.flappyim.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**********************
 * @author:李俊霖
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        // 创建数据库文件夹,上下文，名称、CursorFactory类型 、版本
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库表
        db.execSQL("CREATE TABLE " + DataBaseConfig.TABLE_MESSAGE + "("
                +
                // 消息ID
                "messageId" + " varchar ,"
                +
                //消息的会话
                "messageSession" + " varchar,"
                +
                // 消息的会话类型
                "messageSessionType" + " integer,"
                +
                //会话的offset
                "messageSessionOffset" + " integer,"
                +
                // 当前消息的offset
                "messageTableSeq" + " integer,"
                +
                // 消息类型
                "messageType" + " integer,"
                +
                // 发送ID
                "messageSend" + " varchar,"
                +
                // 发送者外部ID
                "messageSendExtendid" + " varchar,"
                +
                // 消息接收者ID
                "messageRecieve" + " varchar,"
                +
                // 消息接收外部ID
                "messageRecieveExtendid" + " varchar,"
                +
                // 发送内容
                "messageContent" + " varchar,"
                +
                // 发送状态
                "messageSended" + " integer,"
                +
                // 是否已读
                "messageReaded" + " integer,"
                +
                // 是否被删除
                "messageDeleted" + " integer,"
                +
                // 发送日期
                "messageDate" + " varchar,"
                +
                // 时间戳
                "messageStamp" + " integer,"
                +
                // 发送类型
                "messageDeletedDate" + " varchar,"
                + " primary key (messageId)" + ")");


        // 创建数据库表
        db.execSQL("CREATE TABLE " + DataBaseConfig.TABLE_SESSION + "("
                +
                // 会话ID
                "sessionId" + " varchar ,"
                +
                //会话外部ID
                "sessionExtendId" + " varchar,"
                +
                // 会话类型
                "sessionType" + " integer,"
                +
                //会话名称
                "sessionName" + " varchar,"
                +
                // 会话图片
                "sessionImage" + " varchar,"
                +
                // 会话当前offset
                "sessionOffset" + " varchar,"
                +
                // 会话更新时间戳
                "sessionStamp" + " integer,"
                +
                // 会话创建者
                "sessionCreateDate" + " varchar,"
                +
                // 会话创建者
                "sessionCreateUser" + " varchar,"
                +
                // 是否删除
                "sessionDeleted" + " integer,"
                +
                // 删除时间
                "sessionDeletedDate" + " varchar,"
                +
                // 会话用户列表
                "users" + " varchar,"
                +
                // 当前插入的用户
                "sessionInsertUser" + " varchar,"
                + " primary key (sessionId,sessionInsertUser)" + ")");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // /用于升级数据库
        if (newVersion != oldVersion) {

            try {
                db.execSQL("DROP TABLE IF EXISTS " + DataBaseConfig.TABLE_MESSAGE + ";");
                db.execSQL("DROP TABLE IF EXISTS " + DataBaseConfig.TABLE_SESSION + ";");
                // 创建数据库表
                db.execSQL("CREATE TABLE " + DataBaseConfig.TABLE_MESSAGE + "("
                        +
                        // 消息的类型
                        "messageId" + " varchar ,"
                        +
                        //当前登录用户的id
                        "messageSession" + " varchar,"
                        +
                        // 消息内容
                        "messageSessionType" + " integer,"
                        +
                        // 消息类型
                        "messageSessionOffset" + " integer,"
                        +
                        // 来自用户
                        "messageTableSeq" + " integer,"
                        +
                        // 本地时间
                        "messageType" + " integer,"
                        +
                        // 消息id
                        "messageSend" + " varchar,"
                        +
                        // 消息id
                        "messageSendExtendid" + " varchar,"
                        +
                        // 消息类型
                        "messageRecieve" + " varchar,"
                        +
                        // 消息类型
                        "messageRecieveExtendid" + " varchar,"
                        +
                        // 发送flag
                        "messageContent" + " varchar,"
                        +
                        // 发送类型
                        "messageSended" + " integer,"
                        +
                        // 发送类型
                        "messageReaded" + " integer,"
                        +
                        // 发送类型
                        "messageDeleted" + " integer,"
                        +
                        // 发送类型
                        "messageDate" + " varchar,"
                        +
                        // 时间戳
                        "messageStamp" + " integer,"
                        +
                        // 发送类型
                        "messageDeletedDate" + " varchar,"
                        + " primary key (messageId)" + ")");

                // 创建数据库表
                db.execSQL("CREATE TABLE " + DataBaseConfig.TABLE_SESSION + "("
                        +
                        // 会话ID
                        "sessionId" + " varchar ,"
                        +
                        //会话外部ID
                        "sessionExtendId" + " varchar,"
                        +
                        // 会话类型
                        "sessionType" + " integer,"
                        +
                        //会话名称
                        "sessionName" + " varchar,"
                        +
                        // 会话图片
                        "sessionImage" + " varchar,"
                        +
                        // 会话当前offset
                        "sessionOffset" + " varchar,"
                        +
                        // 会话更新时间戳
                        "sessionStamp" + " integer,"
                        +
                        // 会话创建者
                        "sessionCreateDate" + " varchar,"
                        +
                        // 会话创建者
                        "sessionCreateUser" + " varchar,"
                        +
                        // 是否删除
                        "sessionDeleted" + " integer,"
                        +
                        // 删除时间
                        "sessionDeletedDate" + " varchar,"
                        +
                        // 会话用户列表
                        "users" + " varchar,"
                        +
                        // 当前插入的用户
                        "sessionInsertUser" + " varchar,"
                        + " primary key (sessionId,sessionInsertUser)" + ")");


                db.setVersion(newVersion);

            } catch (Exception ex) {

                System.out.println(ex.getCause());
            }


        }
    }
}
