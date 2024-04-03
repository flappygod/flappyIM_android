package com.flappygo.flappyim.Models.Response;

import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Tools.DateTimeTool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class SessionData extends ChatSession implements Serializable {

    //constructor
    public SessionData() {
    }

    //session data
    public SessionData(Flappy.Session session) {
        setSessionId(Long.toString(session.getSessionId()));
        setSessionExtendId(session.getSessionExtendId());
        setSessionType(new BigDecimal(session.getSessionType()));
        setSessionInfo(session.getSessionInfo());
        setSessionName(session.getSessionName());
        setSessionImage(session.getSessionImage());
        setSessionOffset(session.getSessionOffset());
        setSessionStamp(new BigDecimal(session.getSessionStamp()));
        setSessionCreateUser(session.getSessionCreateUser());
        setSessionCreateDate(DateTimeTool.strToDate(session.getSessionCreateDate()));
        setIsDelete(new BigDecimal(session.getIsDelete()));
        setDeleteDate(DateTimeTool.strToDate(session.getDeleteDate()));
        setUsers(GsonTool.jsonArrayToModels(session.getUsers(), ChatUser.class));
    }

    //用户信息
    List<ChatUser> users;

    //未读消息数量
    int unReadMessageCount;

    //获取用户
    public List<ChatUser> getUsers() {
        return users;
    }

    //设置用户
    public void setUsers(List<ChatUser> users) {
        this.users = users;
    }

    public int getUnReadMessageCount() {
        return unReadMessageCount;
    }

    public void setUnReadMessageCount(int unReadMessageCount) {
        this.unReadMessageCount = unReadMessageCount;
    }
}
