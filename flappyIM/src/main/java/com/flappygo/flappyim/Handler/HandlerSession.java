package com.flappygo.flappyim.Handler;

import android.os.Handler;
import android.os.Message;

import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Listener.SessionListener;
import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Session.FlappyChatSession;

//会话更新的监听
public class HandlerSession extends Handler {

    //收到新的消息了
    public static final int SESSION_UPDATE = 1;


    //执行消息
    public void handleMessage(Message message) {
        if (message.what == SESSION_UPDATE) {
            SessionData sessionData = (SessionData) message.obj;
            //遍历
            for(int s = 0; s<HolderMessageSession.getInstance().getSessionListeners().size(); s++){
                SessionListener listener=HolderMessageSession.getInstance().getSessionListeners().get(s);
                FlappyChatSession chatSession=new FlappyChatSession();
                chatSession.setSession(sessionData);
                listener.sessionUpdate(chatSession);
            }
        }
    }

}
