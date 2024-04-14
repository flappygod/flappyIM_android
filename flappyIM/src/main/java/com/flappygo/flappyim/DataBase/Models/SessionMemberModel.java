package com.flappygo.flappyim.DataBase.Models;

import com.flappygo.flappyim.Models.Server.ChatUser;

import java.time.LocalDateTime;
import java.util.Date;

/******
 * 会话用户信息，这是一个聚合类
 * 包含用户信息和用户在当前会话
 * 中的附加属性
 */
public class SessionMemberModel extends ChatUser {


    private String sessionId;

    private String sessionMemberLatestRead;

    private String sessionMemberMarkName;

    private Integer sessionMemberNoDisturb;

    private Date sessionJoinDate;

    private Date sessionLeaveDate;

    private Integer isLeave;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionMemberLatestRead() {
        return sessionMemberLatestRead;
    }

    public void setSessionMemberLatestRead(String sessionMemberLatestRead) {
        this.sessionMemberLatestRead = sessionMemberLatestRead;
    }

    public String getSessionMemberMarkName() {
        return sessionMemberMarkName;
    }

    public void setSessionMemberMarkName(String sessionMemberMarkName) {
        this.sessionMemberMarkName = sessionMemberMarkName;
    }

    public Integer getSessionMemberNoDisturb() {
        return sessionMemberNoDisturb;
    }

    public void setSessionMemberNoDisturb(Integer sessionMemberNoDisturb) {
        this.sessionMemberNoDisturb = sessionMemberNoDisturb;
    }

    public Date getSessionJoinDate() {
        return sessionJoinDate;
    }

    public void setSessionJoinDate(Date sessionJoinDate) {
        this.sessionJoinDate = sessionJoinDate;
    }

    public Date getSessionLeaveDate() {
        return sessionLeaveDate;
    }

    public void setSessionLeaveDate(Date sessionLeaveDate) {
        this.sessionLeaveDate = sessionLeaveDate;
    }

    public Integer getIsLeave() {
        return isLeave;
    }

    public void setIsLeave(Integer isLeave) {
        this.isLeave = isLeave;
    }
}
