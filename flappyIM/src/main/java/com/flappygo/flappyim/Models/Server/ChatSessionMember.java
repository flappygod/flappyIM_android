package com.flappygo.flappyim.Models.Server;

import java.util.Date;

/******
 * 会话用户信息，这是一个聚合类
 * 包含用户信息和用户在当前会话中的附加属性
 */
public class ChatSessionMember extends ChatUser {

    //会话ID
    private String sessionId;

    //最近阅读
    private long sessionMemberLatestRead;

    //最近删除
    private long sessionMemberLatestDelete;

    //标记名称
    private String sessionMemberMarkName;

    //类型
    private int sessionMemberType;

    //会话免打扰
    private int sessionMemberMute;

    //会话置顶
    private int sessionMemberPinned;

    //用户加入时间
    private Date sessionJoinDate;

    //用户离开时间
    private Date sessionLeaveDate;

    //用户是否离开
    private int isLeave;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSessionMemberLatestRead() {
        return sessionMemberLatestRead;
    }

    public void setSessionMemberLatestRead(Long sessionMemberLatestRead) {
        this.sessionMemberLatestRead = (sessionMemberLatestRead != null) ? sessionMemberLatestRead : 0L;
    }

    public Long getSessionMemberLatestDelete() {
        return sessionMemberLatestDelete;
    }

    public void setSessionMemberLatestDelete(Long sessionMemberLatestDelete) {
        this.sessionMemberLatestDelete = (sessionMemberLatestDelete != null) ? sessionMemberLatestDelete : 0;
    }

    public int getSessionMemberType() {
        return sessionMemberType;
    }

    public void setSessionMemberType(int sessionMemberType) {
        this.sessionMemberType = sessionMemberType;
    }

    public String getSessionMemberMarkName() {
        return sessionMemberMarkName;
    }

    public void setSessionMemberMarkName(String sessionMemberMarkName) {
        this.sessionMemberMarkName = sessionMemberMarkName;
    }

    public Integer getSessionMemberMute() {
        return sessionMemberMute;
    }

    public void setSessionMemberMute(Integer sessionMemberMute) {
        this.sessionMemberMute = (sessionMemberMute != null) ? sessionMemberMute : 0;
    }

    public Integer getSessionMemberPinned() {
        return sessionMemberPinned;
    }

    public void setSessionMemberPinned(Integer sessionMemberPinned) {
        this.sessionMemberPinned = (sessionMemberPinned != null) ? sessionMemberPinned : 0;
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
        this.isLeave = (isLeave != null) ? isLeave : 0;
    }
}