package com.flappygo.flappyim.Models.Server;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ChatUser implements Serializable {

    private String userId;

    private String userExtendId;

    private String userName;

    private String userAvatar;

    private String userData;

    private Date userCreateDate;

    private Date userLoginDate;

    private String sessionMemberLatestRead;

    private BigDecimal isDelete;

    private Date deleteDate;

    //最后更新的时间戳
    private String  latest;

    //当前的登录信息
    private int login;

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public int isLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getUserExtendId() {
        return userExtendId;
    }

    public void setUserExtendId(String userExtendId) {
        this.userExtendId = userExtendId == null ? null : userExtendId.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar == null ? null : userAvatar.trim();
    }

    public Date getUserCreateDate() {
        return userCreateDate;
    }

    public void setUserCreateDate(Date userCreateDate) {
        this.userCreateDate = userCreateDate;
    }

    public String getSessionMemberLatestRead() {
        return sessionMemberLatestRead;
    }

    public void setSessionMemberLatestRead(String sessionMemberLatestRead) {
        this.sessionMemberLatestRead = sessionMemberLatestRead;
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

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData == null ? null : userData.trim();
    }

    public Date getUserLoginDate() {
        return userLoginDate;
    }

    public void setUserLoginDate(Date userLoginDate) {
        this.userLoginDate = userLoginDate;
    }
}