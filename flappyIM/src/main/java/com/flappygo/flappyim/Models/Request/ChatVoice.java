package com.flappygo.flappyim.Models.Request;

import java.io.Serializable;

/******
 * 语音消息
 */
public class ChatVoice implements Serializable {

    //发送的地址
    private String sendPath;

    //地址
    private String path;

    //声音有多少秒
    private String seconds;


    public String getSendPath() {
        return sendPath;
    }

    public void setSendPath(String sendPath) {
        this.sendPath = sendPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }
}
