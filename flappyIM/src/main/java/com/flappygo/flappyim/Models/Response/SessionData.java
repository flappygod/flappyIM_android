package com.flappygo.flappyim.Models.Response;

import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.Models.Server.ChatUser;
import com.flappygo.flappyim.Tools.DateTimeTool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class SessionData extends ChatSession implements Serializable {

    List<ChatUser> users;

    public SessionData(){

    }

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
        setUsers(GsonTool.jsonArrayToModels(session.getUsers(),ChatUser.class));

    }

    public List<ChatUser> getUsers() {
        return users;
    }

    public void setUsers(List<ChatUser> users) {
        this.users = users;
    }
}
