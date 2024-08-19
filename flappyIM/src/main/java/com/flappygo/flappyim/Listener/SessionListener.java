package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.DataBase.Models.SessionModel;

import java.util.List;

/******
 * 会话监听
 */
public interface SessionListener {

    //会话列表接收到
    void sessionReceiveList(List<SessionModel> session);

    //会话接收到
    void sessionReceive(SessionModel session);


    //删除
    void sessionDelete(SessionModel session);
}
