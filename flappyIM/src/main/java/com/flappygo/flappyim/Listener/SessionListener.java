package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.Models.Server.ChatMessage;
import com.flappygo.flappyim.Session.FlappyChatSession;

public interface SessionListener {

    //创建
    void sessionCreate(FlappyChatSession session);

    //更新
    void sessionUpdate(FlappyChatSession session);

}
