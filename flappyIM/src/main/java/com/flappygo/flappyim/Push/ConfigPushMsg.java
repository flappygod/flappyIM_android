package com.flappygo.flappyim.Push;

import java.util.HashMap;

///推送信息语言配置
public class ConfigPushMsg {

    //语言包推送放这里
    public static HashMap<String, PushMsgLanPack> languageMaps = new HashMap<String, PushMsgLanPack>() {{
        put("zh", new PushMsgLanPack(
                "消息提醒",
                "您有一条系统消息",
                "您有一条图片消息",
                "您有一条语音消息",
                "您有一条位置消息",
                "您有一条视频消息",
                "您有一条文件消息",
                "您有一条新消息"
        ));
    }};


    //获取推送消息
    public static PushMsgLanPack getLanguagePushMsg(String language) {
        PushMsgLanPack lanPack = languageMaps.get(language);
        if (lanPack != null) {
            return lanPack;
        }
        for (String key : languageMaps.keySet()) {
            return languageMaps.get(key);
        }
        return null;
    }


    //获取推送消息配置
    public static PushMsgLanPack getLanguagePushMsg() {
        for (String key : languageMaps.keySet()) {
            return languageMaps.get(key);
        }
        return null;
    }

}




