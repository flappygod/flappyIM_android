package com.flappygo.flappyim.Models.Request;

import java.io.Serializable;

public class ChatImage implements Serializable {
    //发送的本地地址
    private String sendPath;
    //地址
    private String path;
    //宽度
    private String width;
    //高度
    private String height;

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

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
