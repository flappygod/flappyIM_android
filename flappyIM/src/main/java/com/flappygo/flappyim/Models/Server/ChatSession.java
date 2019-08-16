package com.flappygo.flappyim.Models.Server;

import java.math.BigDecimal;
import java.util.Date;

public class ChatSession {



    //单聊
    public final static int TYPE_SINGLE=1;
    //群聊
    public final static int TYPE_GROUP=2;


    private String sessionId;

    private String sessionExtendId;

    private BigDecimal sessionType;

    private String sessionName;

    private String sessionImage;

    private String sessionOffset;

    private Date sessionCreateDate;

    private String sessionCreateUser;

    private BigDecimal sessionDeleted;

    private Date sessionDeletedDate;

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

    public BigDecimal getSessionDeleted() {
        return sessionDeleted;
    }

    public void setSessionDeleted(BigDecimal sessionDeleted) {
        this.sessionDeleted = sessionDeleted;
    }

    public Date getSessionDeletedDate() {
        return sessionDeletedDate;
    }

    public void setSessionDeletedDate(Date sessionDeletedDate) {
        this.sessionDeletedDate = sessionDeletedDate;
    }
}