package com.flappygo.flappyim.Thread;

import com.flappygo.flappyim.Callback.FlappyNettyListener;
import com.flappygo.flappyim.Config.FlappyConfig;

//netty线程被关闭的通知
public abstract class NettyThreadDeadListener implements FlappyNettyListener {

    //只执行一次
    private boolean once = false;

    //是否开启
    private boolean enable = true;

    //重试次数
    private static int retryCount = 0;

    //创建
    public static void reset() {
        retryCount = FlappyConfig.getInstance().autoRetryNetty;
    }

    //重试Http
    abstract protected void threadDeadRetryHttp();

    //重试netty
    abstract protected void threadDeadRetryNetty();

    @Override
    public void disconnected() {
        synchronized (this) {
            if (enable && !once) {
                once = true;
                if (retryCount > 0) {
                    retryCount--;
                    System.out.println("NETTY重连");
                    threadDeadRetryNetty();
                } else {
                    System.out.println("HTTP重连");
                    threadDeadRetryHttp();
                }
            }
        }
    }

    //停止
    public void disable() {
        synchronized (this) {
            enable = false;
        }
    }

}
