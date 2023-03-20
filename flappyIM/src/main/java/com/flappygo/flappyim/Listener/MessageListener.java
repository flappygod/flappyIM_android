package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.Models.Server.ChatMessage;

public interface MessageListener {

    //发送消息
    void messageCreate(ChatMessage chatMessage);

    //消息更新
    void messageUpdate(ChatMessage chatMessage);

    //消息接收
    void messageReceived(ChatMessage chatMessage);

}
