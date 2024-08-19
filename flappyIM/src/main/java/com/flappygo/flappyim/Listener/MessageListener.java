package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.Models.Server.ChatMessage;

import java.util.List;

/******
 * 消息的监听
 */
public interface MessageListener {

    //发送消息
    void messageSend(ChatMessage chatMessage);

    //发送消息
    void messageFailed(ChatMessage chatMessage);

    //消息接收(列表，登录或者重新登录时)
    void messageReceiveList(List<ChatMessage> chatMessage);

    //消息接收
    void messageReceive(ChatMessage chatMessage);

    //消息被删除
    void messageDelete(ChatMessage messageId);

    //消息已读
    void messageReadOther(String sessionId, String readerId, String tableOffset);

    //消息已读
    void messageReadSelf(String sessionId, String readerId, String tableOffset);

}
