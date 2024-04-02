package com.flappygo.flappyim.Models.Request;

import java.io.Serializable;

//系统消息
public class ChatSystem implements Serializable {

    //什么也不做
    public static final int ACTION_UPDATE_DO_NOTHING =0;

    //当前会话需要更新至版本
    public static final int ACTION_UPDATE_ONE_SESSION =1;

    //所有会话都需要更新
    public static final int ACTION_UPDATE_ALL_SESSION =2;

    //系统动作的文本
    private int sysAction;

    //用于会话展示的文本
    private String sysTitle;

    //用于会话展示的文本
    private String sysBody;

    //系统动作的数据
    private String sysData;

    //系统动作的数据
    private String sysTime;

    public int getSysAction() {
        return sysAction;
    }

    public void setSysAction(int sysAction) {
        this.sysAction = sysAction;
    }

    public String getSysTitle() {
        return sysTitle;
    }

    public void setSysTitle(String sysTitle) {
        this.sysTitle = sysTitle;
    }

    public String getSysBody() {
        return sysBody;
    }

    public void setSysBody(String sysBody) {
        this.sysBody = sysBody;
    }

    public String getSysData() {
        return sysData;
    }

    public void setSysData(String sysData) {
        this.sysData = sysData;
    }

    public String getSysTime() {
        return sysTime;
    }

    public void setSysTime(String sysTime) {
        this.sysTime = sysTime;
    }
}
