package com.flappygo.flappyim.Handler;

import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Models.Response.SessionData;
import com.flappygo.flappyim.Session.FlappyChatSession;
import com.flappygo.flappyim.Listener.SessionListener;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

//会话更新的监听
public class HandlerSession extends Handler {

    //Handle message
    public HandlerSession() {
        super(Looper.getMainLooper());
    }

    //handle message
    public HandlerSession(Looper looper) {
        super(looper);
    }

    //消息更新了
    public static final int SESSION_UPDATE = 1;

    //执行消息
    public void handleMessage(Message message) {
        if (message.what == SESSION_UPDATE) {
            SessionData sessionData = (SessionData) message.obj;
            for (int s = 0; s < HolderMessageSession.getInstance().getSessionListeners().size(); s++) {
                SessionListener listener = HolderMessageSession.getInstance().getSessionListeners().get(s);
                listener.sessionUpdate(sessionData);
            }
        }
    }

}
