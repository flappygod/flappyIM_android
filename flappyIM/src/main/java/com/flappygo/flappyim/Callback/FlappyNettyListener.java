package com.flappygo.flappyim.Callback;

//监听线程直接死亡的监听
public interface FlappyNettyListener {

    //链接断开
    void disconnected();

}
