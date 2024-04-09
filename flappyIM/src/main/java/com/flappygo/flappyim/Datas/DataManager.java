package com.flappygo.flappyim.Datas;


import com.flappygo.flappyim.Session.FlappyChatSession;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.Push.PushSetting;
import com.flappygo.flappyim.FlappyImService;

import android.content.SharedPreferences;
import android.content.Context;

public class DataManager {

    //单例
    private static final class InstanceHolder {
        static final DataManager instance = new DataManager();
    }

    //单例
    public static DataManager getInstance() {
        return InstanceHolder.instance;
    }

    // 首选项名称
    private final static String PREFERENCE_NAME = "com.flappy.im.data";

    // 用户信息保存
    private final static String KEY_FOR_USER = "com.flappy.im.data.KEY_FOR_USER";

    // 推送ID的保存
    private final static String KEY_FOR_PUSH_ID = "com.flappy.im.data.KEY_FOR_PUSH_ID";

    // 推送设置信息
    private final static String KEY_FOR_PUSH_SETTING = "com.flappy.im.data.KEY_FOR_PUSH_SETTING";

    // 消息被点击
    private final static String KEY_FOR_MESSAGE_CLICK = "com.flappy.im.data.KEY_FOR_MESSAGE_CLICK";


    //进行缓存
    private ChatUser chatUser;

    //保存用户信息
    public void saveLoginUser(ChatUser user) {
        chatUser = user;
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_FOR_USER, GsonTool.modelToString(user, ChatUser.class));
        editor.apply();
    }


    //获取保存的用户信息
    public ChatUser getLoginUser() {
        //如果不为空返回
        if (chatUser != null) {
            return chatUser;
        }
        //获取首选项
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        //获取到设置信息
        String setting = mSharedPreferences.getString(KEY_FOR_USER, null);
        if (setting == null) {
            return null;
        }
        //转换为设置
        ChatUser model = GsonTool.jsonStringToModel(setting, ChatUser.class);
        //进行缓存
        chatUser = model;
        //返回配置信息
        return model;
    }


    //保存用户信息
    public void savePushSetting(PushSetting setting) {

        PushSetting update = getPushSetting();
        update = (update == null) ? new PushSetting() : update;
        update.setRoutePushType(setting.getRoutePushType() == null ? update.getRoutePushType() : setting.getRoutePushType());
        update.setRoutePushLanguage(setting.getRoutePushLanguage() == null ? update.getRoutePushLanguage() : setting.getRoutePushLanguage());
        update.setRoutePushNoDisturb(setting.getRoutePushNoDisturb() == null ? update.getRoutePushNoDisturb() : setting.getRoutePushNoDisturb());
        update.setRoutePushPrivacy(setting.getRoutePushPrivacy() == null ? update.getRoutePushPrivacy() : setting.getRoutePushPrivacy());
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_FOR_PUSH_SETTING, GsonTool.modelToString(update, PushSetting.class));
        editor.apply();
    }


    //获取推送设置信息
    public PushSetting getPushSetting() {
        //获取首选项
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        //获取到设置信息
        String setting = mSharedPreferences.getString(KEY_FOR_PUSH_SETTING, null);
        if (setting == null) {
            return null;
        }
        //返回配置信息
        return GsonTool.jsonStringToModel(setting, PushSetting.class);
    }


    //清空当前的用户信息，用户已经退出登录了
    public void clearLoginUser() {
        chatUser = null;
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(KEY_FOR_USER);
        editor.apply();
    }

    //保存用户的推送ID
    public void savePushId(String pushId) {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_FOR_PUSH_ID, pushId);
        editor.apply();
    }

    //获取用户的推送ID
    public String getPushId() {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        return mSharedPreferences.getString(KEY_FOR_PUSH_ID, StringTool.getDeviceIDNumber(FlappyImService.getInstance().getAppContext()));
    }

    //保存会话
    public void saveChatSession(String key, FlappyChatSession chatSession) {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, GsonTool.modelToString(chatSession, FlappyChatSession.class));
        editor.apply();
    }

    //获取会话
    public FlappyChatSession getChatSession(String key) {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        String str = mSharedPreferences.getString(key, null);
        return GsonTool.jsonStringToModel(str, FlappyChatSession.class);
    }


    //保存消息被点击事件
    public void saveNotificationClick(String message) {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_FOR_MESSAGE_CLICK, message);
        editor.apply();
    }

    //获取消息被点击事件
    public String getNotificationClick() {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        return mSharedPreferences.getString(KEY_FOR_MESSAGE_CLICK, null);
    }

    //移除消息被点击事件
    public void removeNotificationClick() {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(KEY_FOR_MESSAGE_CLICK);
        editor.apply();
    }


}
