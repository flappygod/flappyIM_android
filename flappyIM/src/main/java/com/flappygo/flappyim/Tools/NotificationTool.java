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
    public static final String CHANNEL_ID = "channel_1";
    public static final String CHANNEL_NAME = "channel_name_1";

    public NotificationTool(Context context) {
        super(context);
    }

    public void sendNotification(ChatMessage chatMessage, String title, String content) {
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            notification = getNotificationForOreoAndAbove(chatMessage, title, content).build();
        } else {
            notification = getNotificationForPreOreo(chatMessage, title, content).build();
        }
        getManager().notify(1, notification);
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    /**
     * 获取应用图标的Bitmap
     *
     * @param context 上下文
     * @return Bitmap
     */
    public static synchronized Bitmap getBitmap(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            Drawable drawable = packageManager.getApplicationIcon(applicationInfo);
            return drawableToBitmap(drawable);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Drawable转Bitmap
     *
     * @param drawable Drawable对象
     * @return Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    @SuppressLint("NotificationTrampoline")
    private NotificationCompat.Builder getNotificationForPreOreo(ChatMessage chatMessage, String title, String content) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                .setBigContentTitle(title)
                .setSummaryText(content)
                .bigPicture(getBitmap(getApplicationContext()));

        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(getBitmap(getApplicationContext()))
                .setSmallIcon(R.drawable.nothing)
                .setContentIntent(getPendingIntent(chatMessage))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(bigPictureStyle)
                .setAutoCancel(true);
    }

    private PendingIntent getPendingIntent(ChatMessage chatMessage) {
        Intent intent = new Intent(this, ActionReceiver.class);
        intent.putExtra("msg", GsonTool.modelToJsonStr(chatMessage));
        return PendingIntent.getBroadcast(
                FlappyImService.getInstance().getAppContext(),
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification.Builder getNotificationForOreoAndAbove(ChatMessage chatMessage, String title, String content) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(getBitmap(getApplicationContext()))
                .setSmallIcon(R.drawable.nothing)
                .setContentIntent(getPendingIntent(chatMessage))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new Notification.BigPictureStyle().bigPicture(getBitmap(getApplicationContext())))
                .setAutoCancel(true);
    }
}