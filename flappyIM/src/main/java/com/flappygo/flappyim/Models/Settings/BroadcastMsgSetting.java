package com.flappygo.flappyim.Models.Settings;

public class BroadcastMsgSetting {
    //标题
    String title;
    //系统消息
    String sysMsg;
    //图片消息
    String imgMsg;
    //声音消息
    String voiceMsg;
    //定位消息
    String locateMsg;
    //视频消息
    String videoMsg;
    //文件消息
    String fileMsg;
    //通用消息
    String generalMsg;

    public BroadcastMsgSetting(String title,
                               String sysMsg,
                               String imgMsg,
                               String voiceMsg,
                               String locateMsg,
                               String videoMsg,
                               String fileMsg,
                               String generalMsg
    ) {
        this.title = title;
        this.sysMsg = sysMsg;
        this.imgMsg = imgMsg;
        this.voiceMsg = voiceMsg;
        this.locateMsg = locateMsg;
        this.videoMsg = videoMsg;
        this.fileMsg = fileMsg;
        this.generalMsg = generalMsg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSysMsg() {
        return sysMsg;
    }

    public void setSysMsg(String sysMsg) {
        this.sysMsg = sysMsg;
    }

    public String getImgMsg() {
        return imgMsg;
    }

    public void setImgMsg(String imgMsg) {
        this.imgMsg = imgMsg;
    }

    public String getVoiceMsg() {
        return voiceMsg;
    }

    public void setVoiceMsg(String voiceMsg) {
        this.voiceMsg = voiceMsg;
    }

    public String getLocateMsg() {
        return locateMsg;
    }

    public void setLocateMsg(String locateMsg) {
        this.locateMsg = locateMsg;
    }

    public String getVideoMsg() {
        return videoMsg;
    }

    public void setVideoMsg(String videoMsg) {
        this.videoMsg = videoMsg;
    }

    public String getFileMsg() {
        return fileMsg;
    }

    public void setFileMsg(String fileMsg) {
        this.fileMsg = fileMsg;
    }

    public String getGeneralMsg() {
        return generalMsg;
    }

    public void setGeneralMsg(String generalMsg) {
        this.generalMsg = generalMsg;
    }
}
