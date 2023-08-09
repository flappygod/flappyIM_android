package com.flappygo.flappyim.Models.Server;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ChatSession implements Serializable {



    //单聊
    public final static int TYPE_SINGLE=1;
    //群聊
    public final static int TYPE_GROUP=2;
    //系统
    public final static int TYPE_SYSTEM=3;


    private String sessionId;

    private String sessionExtendId;

    private BigDecimal sessionType;

    private String sessionInfo;

    private String sessionName;

    private String sessionImage;

    private String sessionOffset;

    private BigDecimal sessionStamp;

    private Date sessionCreateDate;

    private String sessionCreateUser;

    private BigDecimal isDelete;

    private Date deleteDate;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId == null ? null : sessionId.trim();
    }

    public String getSessionExtendId() {
        return sessionExtendId;
    }

    public void setSessionExtendId(String sessionExtendId) {
        this.sessionExtendId = sessionExtendId == null ? null : sessionExtendId.trim();
    }

    public BigDecimal getSessionType() {
        return sessionType;
    }

    public void setSessionType(BigDecimal sessionType) {
        this.sessionType = sessionType;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName == null ? null : sessionName.trim();
    }

    public String getSessionImage() {
        return sessionImage;
    }

    public void setSessionImage(String sessionImage) {
        this.sessionImage = sessionImage == null ? null : sessionImage.trim();
    }

    public String getSessionOffset() {
        return sessionOffset;
    }

    public void setSessionOffset(String sessionOffset) {
        this.sessionOffset = sessionOffset == null ? null : sessionOffset.trim();
    }

    public BigDecimal getSessionStamp() {
        return sessionStamp;
    }

    public void setSessionStamp(BigDecimal sessionStamp) {
        this.sessionStamp = sessionStamp;
    }

    public Date getSessionCreateDate() {
        return sessionCreateDate;
    }

    public void setSessionCreateDate(Date sessionCreateDate) {
        this.sessionCreateDate = sessionCreateDate;
    }

    public String getSessionCreateUser() {
        return sessionCreateUser;
    }

    public void setSessionCreateUser(String sessionCreateUser) {
        this.sessionCreateUser = sessionCreateUser == null ? null : sessionCreateUser.trim();
    }

    public BigDecimal getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(BigDecimal isDelete) {
        this.isDelete = isDelete;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public String getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(String sessionInfo) {
        this.sessionInfo = sessionInfo;
    }
}