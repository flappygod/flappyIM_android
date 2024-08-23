package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.DataBase.Models.ChatSessionData;

import java.util.List;

/******
 * 会话监听
 */
public interface SessionListener {

    //会话列表接收到
    void sessionReceiveList(List<ChatSessionData> session);

    //会话接收到
    void sessionReceive(ChatSessionData session);


    //删除
    void sessionDelete(ChatSessionData session);
}
