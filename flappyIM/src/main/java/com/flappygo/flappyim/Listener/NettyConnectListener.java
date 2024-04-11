package com.flappygo.flappyim.Listener;

/******
 * 监听线程直接死亡的监听
 */
public interface NettyConnectListener {

    //链接断开
    void disconnected();

}
