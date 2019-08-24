package com.flappygo.flappyim.Models.Response;

//上传的返回信息
public class ResponseUpload {

    //文件的上传地址
    private String filePath;
    //简览
    private String overFilePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOverFilePath() {
        return overFilePath;
    }

    public void setOverFilePath(String overFilePath) {
        this.overFilePath = overFilePath;
    }
}
