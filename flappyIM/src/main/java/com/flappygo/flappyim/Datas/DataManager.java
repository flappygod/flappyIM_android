package com.flappygo.flappyim.Datas;


import com.flappygo.flappyim.Models.Settings.BroadcastMsgSetting;
import com.flappygo.flappyim.Session.FlappyChatSession;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Server.ChatUser;
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
    private final static String PREFERENCE_NAME = "com.flappygo.flappyim.data";

    // 用户信息保存
    private final static String KEY_FOR_USER = "com.flappygo.flappyim.data.KEY_FOR_USER";

    // 推送方式的保存
    private final static String KEY_FOR_PUSH_TYPE = "com.flappygo.flappyim.data.KEY_FOR_PUSH_TYPE";

    // 消息被点击
    private final static String KEY_FOR_MESSAGE_CLICK = "com.flappygo.flappyim.data.KEY_FOR_MESSAGE_CLICK";

    // 消息被点击
    private final static String KEY_FOR_SETTING_BROADCAST = "com.flappygo.flappyim.data.KEY_FOR_SETTING_BROADCAST";

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
        ChatUser model = GsonTool.jsonObjectToModel(setting, ChatUser.class);
        //进行缓存
        chatUser = model;
        //返回配置信息
        return model;
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


    //保存用户的推送类型
    public void savePushType(String pushType) {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_FOR_PUSH_TYPE, pushType);
        editor.apply();
    }

    //获取用户的推送类型
    public String getPushType() {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        //获取到设置信息
        return mSharedPreferences.getString(KEY_FOR_PUSH_TYPE, "0");
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
        //获取到设置信息
        String str = mSharedPreferences.getString(key, null);
        //返回本地的会话数据
        return GsonTool.jsonObjectToModel(str, FlappyChatSession.class);
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
        //返回本地的会话数据
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

    //保存广播设置
    public void saveBroadCastSetting(BroadcastMsgSetting setting) {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_FOR_SETTING_BROADCAST, GsonTool.modelToString(setting, BroadcastMsgSetting.class));
        editor.apply();
    }

    //获取广播设置
    public BroadcastMsgSetting getBroadCastSetting() {
        SharedPreferences mSharedPreferences = FlappyImService.getInstance().getAppContext().getSharedPreferences(
                PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        //获取到设置信息
        String str = mSharedPreferences.getString(KEY_FOR_SETTING_BROADCAST, null);
        //空的
        if (str == null) {
            return null;
        }
        //返回本地的会话数据
        return GsonTool.jsonObjectToModel(str, BroadcastMsgSetting.class);
    }

}
