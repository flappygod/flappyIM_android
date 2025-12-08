package com.flappygo.flappyim.Models.Request;

public class ChatReadReceipt {

    String userId;
    String sessionId;
    String readOffset;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getReadOffset() {
        return readOffset;
    }

    public void setReadOffset(String readOffset) {
        this.readOffset = readOffset;
    }
}
