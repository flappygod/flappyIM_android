package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.Models.Server.ChatMessage;

public interface MessageListener {

    //发送消息
    void messageSend(ChatMessage chatMessage);

    //发送消息
    void messageFailed(ChatMessage chatMessage);

    //消息更新
    void messageUpdate(ChatMessage chatMessage);

    //消息接收
    void messageReceived(ChatMessage chatMessage);

    //消息接收
    void messageRead(String tableSequence);

    //消息被删除
    void messageDelete(String messageId);
}
