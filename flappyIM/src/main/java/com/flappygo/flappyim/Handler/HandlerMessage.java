package com.flappygo.flappyim.Handler;

import android.os.Handler;
import android.os.Message;

import com.flappygo.flappyim.Holder.HolderMessageRecieve;
import com.flappygo.flappyim.Listener.MessageListener;
import com.flappygo.flappyim.Models.Server.ChatMessage;

import java.util.List;

public class HandlerMessage extends Handler {

    //收到新的消息了
    public static final int MSG_RECIVE = 1;


    //执行消息
    public void handleMessage(Message message) {
        if (message.what == MSG_RECIVE) {
            ChatMessage chatMessage = (ChatMessage) message.obj;
            //遍历
            for (String key : HolderMessageRecieve.getInstance().getListeners().keySet()) {
                //只接受一个消息
                if (chatMessage.getMessageSession().equals(key)) {
                    List<MessageListener> mems = HolderMessageRecieve.getInstance().getListeners().get(key);
                    for (int x = 0; x < mems.size(); x++) {
                        mems.get(x).messageRecieved(chatMessage);
                    }
                }
                //所有消息都接收
                if (key.equals("")) {
                    List<MessageListener> mems = HolderMessageRecieve.getInstance().getListeners().get(key);
                    for (int x = 0; x < mems.size(); x++) {
                        mems.get(x).messageRecieved(chatMessage);
                    }
                }
            }
        }
    }

}
