package com.flappygo.flappyim.Models.Request;

import java.util.List;

/******
 * 系统动作消息
 */
public class ChatAction {

    //系统动作消息  1.消息已读回执   2.删除消息回执
    private int actionType;

    private List<String> actionIds;

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public List<String> getActionIds() {
        return actionIds;
    }

    public void setActionIds(List<String> actionIds) {
        this.actionIds = actionIds;
    }
}
