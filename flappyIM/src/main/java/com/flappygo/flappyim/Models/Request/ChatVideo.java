package com.flappygo.flappyim.Models.Request;

import java.io.Serializable;

/******
 * 视频消息
 */
public class ChatVideo implements Serializable {

    //网页路径
    private String path;
    //本地路径
    private String sendPath;
    //封面图片
    private String coverPath;
    //封面图片
    private String coverSendPath;
    //时长
    private String duration;
    //宽度
    private String width;
    //高度
    private String height;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSendPath() {
        return sendPath;
    }

    public void setSendPath(String sendPath) {
        this.sendPath = sendPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getCoverSendPath() {
        return coverSendPath;
    }

    public void setCoverSendPath(String coverSendPath) {
        this.coverSendPath = coverSendPath;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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
