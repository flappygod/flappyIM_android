package com.flappygo.flappyim.Models.Server;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ChatUser implements Serializable {
    private String userId;

    private String userExtendId;

    private String userName;

    private String userHead;

    private Date userCreateDate;

    private BigDecimal userDeleted;

    private Date userDeletedDate;

    private String userData;

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    //最后更新的时间戳
    private String  latest;
    //当前的登录信息
    private int login;

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

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead == null ? null : userHead.trim();
    }

    public Date getUserCreateDate() {
        return userCreateDate;
    }

    public void setUserCreateDate(Date userCreateDate) {
        this.userCreateDate = userCreateDate;
    }

    public BigDecimal getUserDeleted() {
        return userDeleted;
    }

    public void setUserDeleted(BigDecimal userDeleted) {
        this.userDeleted = userDeleted;
    }

    public Date getUserDeletedDate() {
        return userDeletedDate;
    }

    public void setUserDeletedDate(Date userDeletedDate) {
        this.userDeletedDate = userDeletedDate;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData == null ? null : userData.trim();
    }
}