package com.flappygo.flappyim.Tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.R;
import com.flappygo.flappyim.Reciver.ActionReceiver;
import com.flappygo.flappyim.Service.FlappyService;

public class NotificationUtil extends ContextWrapper {

    private NotificationManager mManager;
    public static final String sID = "channel_1";
    public static final String sName = "channel_name_1";

    public NotificationUtil(Context context) {
        super(context);
    }

    public void sendNotification(ChatMessage chatMessage, String title, String content) {
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            Notification notification = getNotification_26(chatMessage, title, content).build();
            getmManager().notify(1, notification);
        } else {
            Notification notification = getNotification_25(chatMessage, title, content).build();
            getmManager().notify(1, notification);
        }
    }

    private NotificationManager getmManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(sID, sName, NotificationManager.IMPORTANCE_HIGH);
        getmManager().createNotificationChannel(channel);
    }


    /**
     * 获取图标 bitmap
     *
     * @param context
     */
    public static synchronized Bitmap getBitmap(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        //xxx根据自己的情况获取drawable
        Drawable d = packageManager.getApplicationIcon(applicationInfo);
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap bm = bd.getBitmap();
        return bm;
    }

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


    public PendingIntent getPendingIntent(ChatMessage chatMessage) {
        Intent openintent = new Intent(this, ActionReceiver.class);
        openintent.putExtra("msg", GsonTool.modelToString(chatMessage, ChatMessage.class));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(FlappyService.getInstance().getApplicationContext(),
                0, openintent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
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