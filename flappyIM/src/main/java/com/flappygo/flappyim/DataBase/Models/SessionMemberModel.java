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

    //会话ID
    private String sessionId;

    //最近阅读
    private String sessionMemberLatestRead;

    //标记名称
    private String sessionMemberMarkName;

    //会话免打扰
    private Integer sessionMemberNoDisturb;

    //用户加入时间
    private Date sessionJoinDate;

    //用户离开时间
    private Date sessionLeaveDate;

    //用户是否离开
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
