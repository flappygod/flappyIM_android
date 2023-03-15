package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Session.FlappyChatSession;

public interface SessionListener {

    //更新
    void sessionUpdate(SessionData session);

}
