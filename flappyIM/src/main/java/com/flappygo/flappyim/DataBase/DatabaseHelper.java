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
                // 发送类型
                "messageDeletedDate" + " varchar,"
                + " primary key (messageId)" + ")");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // /用于升级数据库
        if (newVersion != oldVersion) {

            try {
                db.execSQL("DROP TABLE IF EXISTS " + DataBaseConfig.TABLE_MESSAGE + ";");
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
                        // 发送类型
                        "messageDeletedDate" + " varchar,"
                        + " primary key (messageId)" + ")");


                db.setVersion(newVersion);

            } catch (Exception ex) {

                System.out.println(ex.getCause());
            }


        }
    }
}
