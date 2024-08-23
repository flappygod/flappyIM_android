package com.flappygo.flappyim.Handler;

import com.flappygo.flappyim.DataBase.Models.SessionModel;
import com.flappygo.flappyim.Holder.HolderMessageSession;
import com.flappygo.flappyim.Listener.SessionListener;

import android.os.Handler;
import android.os.Message;
import android.os.Looper;

import java.util.List;

/******
 * 会话更新的监听
 */
public class HandlerSession extends Handler {

    //会话handler
    public HandlerSession() {
        super(Looper.getMainLooper());
    }

    //会话接收
    public static final int SESSION_RECEIVE = 1;

    //会话接收列表
    public static final int SESSION_RECEIVE_LIST = 2;

    //会话删除
    public static final int SESSION_DELETE = 3;

    //执行消息
    public void handleMessage(Message message) {
        if (message.obj == null) {
            return;
        }
        if (message.what == SESSION_RECEIVE) {
            SessionModel sessionModel = (SessionModel) message.obj;
            for (int s = 0; s < HolderMessageSession.getInstance().getSessionListeners().size(); s++) {
                SessionListener listener = HolderMessageSession.getInstance().getSessionListeners().get(s);
                listener.sessionReceive(sessionModel);
            }
        }
        if (message.what == SESSION_RECEIVE_LIST) {
            List<SessionModel> sessionList = (List<SessionModel>) message.obj;
            for (int s = 0; s < HolderMessageSession.getInstance().getSessionListeners().size(); s++) {
                SessionListener listener = HolderMessageSession.getInstance().getSessionListeners().get(s);
                listener.sessionReceiveList(sessionList);
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
