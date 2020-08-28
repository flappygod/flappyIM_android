package com.flappygo.flappyim.Thread;

import com.flappygo.flappyim.Callback.FlappyDeadCallback;

//netty线程被关闭的通知
public abstract class NettyThreadDead implements FlappyDeadCallback {

    //只执行一次
    private boolean onece = false;

    private boolean enable = true;

    @Override
    public void dead() {
        synchronized (this) {
            if (enable == true && onece == false) {
                onece = true;
                threadDead();
            }
        }
    }

    //停止
    public void disable() {
        synchronized (this) {
            enable = false;
        }
    }

    abstract protected void threadDead();
}
