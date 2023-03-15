package com.flappygo.flappyim.Handler;

import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Session.FlappyChatSession;
import com.flappygo.flappyim.Listener.SessionListener;
import android.os.Handler;
import android.os.Message;

//会话更新的监听
public class HandlerSession extends Handler {

    //收到新的消息了
    public static final int SESSION_CREATE = 1;

    //消息更新了
    public static final int SESSION_UPDATE = 2;


    //执行消息
    public void handleMessage(Message message) {
        //会话被创建
        if (message.what == SESSION_CREATE) {
            SessionData sessionData = (SessionData) message.obj;
            for (int s = 0; s < HolderMessageSession.getInstance().getSessionListeners().size(); s++) {
                SessionListener listener = HolderMessageSession.getInstance().getSessionListeners().get(s);
                FlappyChatSession chatSession = new FlappyChatSession();
                chatSession.setSession(sessionData);
                listener.sessionCreate(chatSession);
            }
        }
        //会话被更新
        if (message.what == SESSION_UPDATE) {
            SessionData sessionData = (SessionData) message.obj;
            for (int s = 0; s < HolderMessageSession.getInstance().getSessionListeners().size(); s++) {
                SessionListener listener = HolderMessageSession.getInstance().getSessionListeners().get(s);
                FlappyChatSession chatSession = new FlappyChatSession();
                chatSession.setSession(sessionData);
                listener.sessionUpdate(chatSession);
            }
        }
    }

}
