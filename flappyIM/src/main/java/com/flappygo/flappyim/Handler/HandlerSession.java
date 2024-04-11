package com.flappygo.flappyim.Handler;

import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Session.FlappySessionData;
import com.flappygo.flappyim.Listener.SessionListener;

import android.os.Handler;
import android.os.Message;
import android.os.Looper;

/******
 * 会话更新的监听
 */
public class HandlerSession extends Handler {

    //Handle message
    public HandlerSession() {
        super(Looper.getMainLooper());
    }

    //消息更新了
    public static final int SESSION_UPDATE = 1;

    //执行消息
    public void handleMessage(Message message) {
        if (message.what == SESSION_UPDATE) {
            FlappySessionData flappySessionData = (FlappySessionData) message.obj;
            for (int s = 0; s < HolderMessageSession.getInstance().getSessionListeners().size(); s++) {
                SessionListener listener = HolderMessageSession.getInstance().getSessionListeners().get(s);
                listener.sessionUpdate(flappySessionData);
            }
        }
    }

}
