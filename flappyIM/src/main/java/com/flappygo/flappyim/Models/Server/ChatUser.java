package com.flappygo.flappyim.Models.Server;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/******
 * 会话用户
 */
public class ChatUser implements Serializable {

    //用户ID
    private String userId;

    //用户外部ID
    private String userExtendId;

    //用户名称
    private String userName;

    //用户头像
    private String userAvatar;

    //用户数据
    private String userData;

    //用户创建时间
    private Date userCreateDate;

    //用户登录时间
    private Date userLoginDate;

    /***以下参数进用于当前用户存储***/
    //最后更新的时间戳
    private String latest;

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