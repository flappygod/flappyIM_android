package com.flappygo.flappyim.Models.Request;

import java.io.Serializable;

//系统消息
public class ChatSystem implements Serializable {

    //需要更新单个会话
    public static final int ACTION_UPDATE_ONESESSION=1;

    //需要更新全部会话
    public static final int ACTION_UPDATE_ALLSESSION=2;

    //用于会话展示的文本
    private  String  sysText;

    //系统动作的文本
    private  int  sysAction;

    //系统动作的数据
    private  String  sysActionData;

    public String getSysText() {
        return sysText;
    }

    public void setSysText(String sysText) {
        this.sysText = sysText;
    }

    public int getSysAction() {
        return sysAction;
    }

    public void setSysAction(int sysAction) {
        this.sysAction = sysAction;
    }

    public String getSysActionData() {
        return sysActionData;
    }

    public void setSysActionData(String sysActionData) {
        this.sysActionData = sysActionData;
    }
}
