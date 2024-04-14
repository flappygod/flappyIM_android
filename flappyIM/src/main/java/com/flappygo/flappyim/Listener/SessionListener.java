package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.DataBase.Models.SessionModel;

/******
 * 会话监听
 */
public interface SessionListener {

    //更新
    void sessionUpdate(SessionModel session);

    //删除
    void sessionDelete(SessionModel session);
}
