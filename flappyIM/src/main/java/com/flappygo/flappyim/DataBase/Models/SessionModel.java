package com.flappygo.flappyim.DataBase.Models;

import com.flappygo.flappyim.Models.Server.ChatSession;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Models.Protoc.Flappy;
import com.flappygo.flappyim.Tools.StringTool;
import com.flappygo.flappyim.Tools.TimeTool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/******
 * 群聊的会话
 */
public class SessionModel extends ChatSession implements Serializable {

    //构造器
    public SessionModel() {
    }

    //构造器
    public SessionModel(Flappy.Session session) {
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
                GsonTool.jsonStrToModels(session.getUsers(), SessionMemberModel.class)
        );
    }

    //用户信息
    List<SessionMemberModel> users;

    //未读消息数量
    int unReadMessageCount;

    //获取用户
    public List<SessionMemberModel> getUsers() {
        return users;
    }

    //设置用户
    public void setUsers(List<SessionMemberModel> users) {
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
}
