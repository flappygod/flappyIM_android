package com.flappygo.flappyim.Models.Server;

import java.math.BigDecimal;

public class ChatMember {
    private String sessionRecordId;

    private String sessionId;

    private BigDecimal sessionType;

    private String sessionMemberId;

    private BigDecimal sessionMemberType;

    private String sessionJoinDate;

    public String getSessionRecordId() {
        return sessionRecordId;
    }

    public void setSessionRecordId(String sessionRecordId) {
        this.sessionRecordId = sessionRecordId == null ? null : sessionRecordId.trim();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId == null ? null : sessionId.trim();
    }

    public BigDecimal getSessionType() {
        return sessionType;
    }

    public void setSessionType(BigDecimal sessionType) {
        this.sessionType = sessionType;
    }

    public String getSessionMemberId() {
        return sessionMemberId;
    }

    public void setSessionMemberId(String sessionMemberId) {
        this.sessionMemberId = sessionMemberId == null ? null : sessionMemberId.trim();
    }

    public BigDecimal getSessionMemberType() {
        return sessionMemberType;
    }

    public void setSessionMemberType(BigDecimal sessionMemberType) {
        this.sessionMemberType = sessionMemberType;
    }

    public String getSessionJoinDate() {
        return sessionJoinDate;
    }

    public void setSessionJoinDate(String sessionJoinDate) {
        this.sessionJoinDate = sessionJoinDate == null ? null : sessionJoinDate.trim();
    }
}