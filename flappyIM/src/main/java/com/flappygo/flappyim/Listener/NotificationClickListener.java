package com.flappygo.flappyim.Listener;

import com.flappygo.flappyim.Models.Server.ChatMessage;

/*******
 * 通知消息被点击时候的监听
 */
public interface NotificationClickListener {

    //notification clicked
    void notificationClicked(ChatMessage chatMessage);

}
