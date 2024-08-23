package com.flappygo.flappyim.DataBase.Models;

import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.Tools.TimeTool;

import java.io.Serializable;
import java.util.List;


/******
 * 群聊的会话
 */
public class ChatSessionData extends ChatSession implements Serializable {

    //构造器
    public ChatSessionData() {
    }

    //构造器
    public ChatSessionData(Flappy.Session session) {
        setSessionId(Long.toString(session.getSessionId()));
        setSessionExtendId(session.getSessionExtendId());
        setSessionType(session.getSessionType());
        setSessionInfo(session.getSessionInfo());
        setSessionName(session.getSessionName());
        setSessionImage(session.getSessionImage());
        setSessionOffset(StringTool.strToLong(session.getSessionOffset()));
        setSessionStamp(session.getSessionStamp());
        setSessionCreateUser(session.getSessionCreateUser());
        setSessionCreateDate(TimeTool.strToDate(session.getSessionCreateDate()));
        setIsDelete(session.getIsDelete());
        setDeleteDate(TimeTool.strToDate(session.getDeleteDate()));
        setUsers(
                GsonTool.jsonStrToModels(session.getUsers(), ChatSessionMember.class)
        );
    }

    //用户信息
    List<ChatSessionMember> users;

    //未读消息数量
    int unReadMessageCount;

    //临时删除
    boolean isDeleteTemp;

    //获取用户
    public List<ChatSessionMember> getUsers() {
        return users;
    }

    //设置用户
    public void setUsers(List<ChatSessionMember> users) {
        this.users = users;
    }

    //未读消息数量
    public int getUnReadMessageCount() {
        return unReadMessageCount;
    }

    //设置未读消息数量
    public void setUnReadMessageCount(int unReadMessageCount) {
        this.unReadMessageCount = unReadMessageCount;
    }

    //是否临时删除
    public boolean isDeleteTemp() {
        return isDeleteTemp;
    }

    //是否临时删除
    public void setDeleteTemp(boolean deleteTemp) {
        isDeleteTemp = deleteTemp;
    }
}
