package com.flappygo.flappyim.Handler;

import android.os.Handler;
import android.os.Message;

import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Models.Server.ChatMessage;

import java.util.List;

public class HandlerMessage extends Handler {

    //收到新的消息了
    public static final int MSG_RECEIVE = 1;

    //消息的状态更新
    public static final int MSG_UPDATE = 2;

    //执行消息
    public void handleMessage(Message message) {
        //暂时我们都统一处理了他
        if (message.what == MSG_RECEIVE || message.what == MSG_UPDATE) {
            ChatMessage chatMessage = (ChatMessage) message.obj;
            //遍历
            for (String key : HolderMessageSession.getInstance().getMsgListeners().keySet()) {
                //只接受一个消息
                if (chatMessage.getMessageSession().equals(key)) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageReceived(chatMessage);
                    }
                }
                //所有消息都接收
                if (key.equals("")) {
                    List<MessageListener> messageListeners = HolderMessageSession.getInstance().getMsgListeners().get(key);
                    for (int x = 0; messageListeners != null && x < messageListeners.size(); x++) {
                        messageListeners.get(x).messageReceived(chatMessage);
                    }
                }
            }
        }
    }

}
