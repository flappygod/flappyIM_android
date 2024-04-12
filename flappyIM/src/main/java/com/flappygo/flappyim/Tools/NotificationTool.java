package com.flappygo.flappyim.Tools;

import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Receiver.ActionReceiver;
import com.flappygo.flappyim.FlappyImService;

import androidx.core.app.NotificationCompat;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.RequiresApi;

import android.content.ContextWrapper;
import android.media.RingtoneManager;
import android.graphics.PixelFormat;
import android.app.PendingIntent;
import android.app.Notification;

import com.flappygo.flappyim.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.content.Intent;
import android.os.Build;


/******
 * 通知工具类
 */
public class NotificationTool extends ContextWrapper {

    private NotificationManager mManager;
    public static final String sID = "channel_1";
    public static final String sName = "channel_name_1";

    public NotificationTool(Context context) {
        super(context);
    }

    public void sendNotification(ChatMessage chatMessage, String title, String content) {
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            Notification notification = getNotification_26(chatMessage, title, content).build();
            getManager().notify(1, notification);
        } else {
            Notification notification = getNotification_25(chatMessage, title, content).build();
            getManager().notify(1, notification);
        }
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(sID, sName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }


    /**
     * 获取图标 bitmap
     *
     * @param context content
     */
    public static synchronized Bitmap getBitmap(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        //xxx根据自己的情况获取drawable
        assert applicationInfo != null;
        return drawableToBitmap(packageManager.getApplicationIcon(applicationInfo));
    }

    //drawable转成bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        //设置bounds
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    @SuppressLint("NotificationTrampoline")
    public NotificationCompat.Builder getNotification_25(ChatMessage chatMessage, String title, String content) {

        // 以下是展示大图的通知
        androidx.core.app.NotificationCompat.BigPictureStyle style = new androidx.core.app.NotificationCompat.BigPictureStyle();
        style.setBigContentTitle(title);
        style.setSummaryText(content);
        style.bigPicture(getBitmap(getApplicationContext()));
        // 以下是展示多文本通知
        androidx.core.app.NotificationCompat.BigTextStyle style1 = new androidx.core.app.NotificationCompat.BigTextStyle();
        style1.setBigContentTitle(title);
        style1.bigText(content);
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(getBitmap(getApplicationContext()))
                .setSmallIcon(R.drawable.nothing)
                .setContentIntent(getPendingIntent(chatMessage))
                .setSound(RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(style)
                .setAutoCancel(true);
    }

    //获取跳转
    public PendingIntent getPendingIntent(ChatMessage chatMessage) {
        Intent openintent = new Intent(this, ActionReceiver.class);
        openintent.putExtra("msg", GsonTool.modelToString(chatMessage, ChatMessage.class));
        return PendingIntent.getBroadcast(
                FlappyImService.getInstance().getAppContext(),
                0,
                openintent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification_26(ChatMessage chatMessage, String title, String content) {
        return new Notification.Builder(getApplicationContext(), sID)
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(getBitmap(getApplicationContext()))
                .setSmallIcon(R.drawable.nothing)
                .setContentIntent(getPendingIntent(chatMessage))
                .setSound(RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new Notification.BigPictureStyle().bigPicture(getBitmap(getApplicationContext())))
                .setNumber(1)
                .setAutoCancel(true);
    }
}