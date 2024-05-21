package com.flappygo.flappyim.Models.Request;

/******
 * 文件消息
 */
public class ChatFile {

    //文件名称
    private String fileName;
    //文件大小
    private String fileSize;
    //文件发送地址
    private String sendPath;
    //文件网络地址
    private String path;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

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
}
