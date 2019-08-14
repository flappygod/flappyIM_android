package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.Models.Server.ChatMessage;

public interface MessageListener {

    //收到消息
    void messageRecieved(ChatMessage chatMessage);

}
