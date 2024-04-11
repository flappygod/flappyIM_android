package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.Session.FlappySessionData;

/******
 * 会话监听
 */
public interface SessionListener {

    //更新
    void sessionUpdate(FlappySessionData session);

}
