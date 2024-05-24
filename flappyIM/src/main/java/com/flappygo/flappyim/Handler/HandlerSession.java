package com.flappygo.flappyim.Handler;

import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.DataBase.Models.SessionModel;
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

    //消息更新了
    public static final int SESSION_DELETE = 2;

    //执行消息
    public void handleMessage(Message message) {
        if(message.obj==null){
            return;
        }
        if (message.what == SESSION_UPDATE) {
            SessionModel sessionModel = (SessionModel) message.obj;
            for (int s = 0; s < HolderMessageSession.getInstance().getSessionListeners().size(); s++) {
                SessionListener listener = HolderMessageSession.getInstance().getSessionListeners().get(s);
                listener.sessionUpdate(sessionModel);
            }
        }
        if (message.what == SESSION_DELETE) {
            SessionModel sessionModel = (SessionModel) message.obj;
            for (int s = 0; s < HolderMessageSession.getInstance().getSessionListeners().size(); s++) {
                SessionListener listener = HolderMessageSession.getInstance().getSessionListeners().get(s);
                listener.sessionDelete(sessionModel);
            }
        }
    }

}
