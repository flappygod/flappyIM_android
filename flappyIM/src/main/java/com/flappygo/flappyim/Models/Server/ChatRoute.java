package com.flappygo.flappyim.Models.Server;


import java.io.Serializable;
import java.math.BigDecimal;

public class ChatRoute implements Serializable {

    //默认显示文本
    public final static int PUSH_PRIVACY_TYPE_NORMAL = 0;
    //隐藏文本信息
    public final static int PUSH_PRIVACY_TYPE_HIDE = 1;

    private String routeId;

    private String routeTime;

    private String routeUserId;

    private String routeUserExtendId;

    private String routeDevicePlat;

    private String routeDeviceId;

    private BigDecimal routePushType;

    private String routePushPlat;

    private String routePushId;

    private String routePushLanguage;

    private String routePushPrivacy;

    private String routePushMute;

    private String routeServerIp;

    private String routeServerPort;

    private String routeServerTopic;

    private String routeServerGroup;

    private BigDecimal routeOnline;

    private BigDecimal routeIsLogin;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId == null ? null : routeId.trim();
    }

    public String getRouteTime() {
        return routeTime;
    }

    public void setRouteTime(String routeTime) {
        this.routeTime = routeTime == null ? null : routeTime.trim();
    }

    public String getRouteDeviceId() {
        return routeDeviceId;
    }

    public void setRouteDeviceId(String routeDeviceId) {
        this.routeDeviceId = routeDeviceId;
    }

    public String getRouteUserId() {
        return routeUserId;
    }

    public void setRouteUserId(String routeUserId) {
        this.routeUserId = routeUserId == null ? null : routeUserId.trim();
    }

    public String getRouteUserExtendId() {
        return routeUserExtendId;
    }

    public void setRouteUserExtendId(String routeUserExtendId) {
        this.routeUserExtendId = routeUserExtendId == null ? null : routeUserExtendId.trim();
    }

    public String getRouteDevicePlat() {
        return routeDevicePlat;
    }

    public void setRouteDevicePlat(String routeDevicePlat) {
        this.routeDevicePlat = routeDevicePlat == null ? null : routeDevicePlat.trim();
    }

    public String getRoutePushId() {
        return routePushId;
    }

    public void setRoutePushId(String routePushId) {
        this.routePushId = routePushId == null ? null : routePushId.trim();
    }

    public BigDecimal getRoutePushType() {
        return routePushType;
    }

    public void setRoutePushType(BigDecimal routePushType) {
        this.routePushType = routePushType;
    }

    public String getRouteServerIp() {
        return routeServerIp;
    }

    public void setRouteServerIp(String routeServerIp) {
        this.routeServerIp = routeServerIp == null ? null : routeServerIp.trim();
    }

    public String getRouteServerPort() {
        return routeServerPort;
    }

    public void setRouteServerPort(String routeServerPort) {
        this.routeServerPort = routeServerPort == null ? null : routeServerPort.trim();
    }

    public String getRouteServerTopic() {
        return routeServerTopic;
    }

    public void setRouteServerTopic(String routeServerTopic) {
        this.routeServerTopic = routeServerTopic == null ? null : routeServerTopic.trim();
    }

    public String getRouteServerGroup() {
        return routeServerGroup;
    }

    public void setRouteServerGroup(String routeServerGroup) {
        this.routeServerGroup = routeServerGroup == null ? null : routeServerGroup.trim();
    }

    public BigDecimal getRouteOnline() {
        return routeOnline;
    }

    public void setRouteOnline(BigDecimal routeOnline) {
        this.routeOnline = routeOnline;
    }

    public BigDecimal getRouteIsLogin() {
        return routeIsLogin;
    }

    public void setRouteIsLogin(BigDecimal routeIsLogin) {
        this.routeIsLogin = routeIsLogin;
    }

    public String getRoutePushPlat() {
        return routePushPlat;
    }

    public void setRoutePushPlat(String routePushPlat) {
        this.routePushPlat = routePushPlat;
    }

    public String getRoutePushLanguage() {
        return routePushLanguage;
    }

    public void setRoutePushLanguage(String routePushLanguage) {
        this.routePushLanguage = routePushLanguage;
    }

    public String getRoutePushPrivacy() {
        return routePushPrivacy;
    }

    public void setRoutePushPrivacy(String routePushPrivacy) {
        this.routePushPrivacy = routePushPrivacy;
    }

    public String getRoutePushMute() {
        return routePushMute;
    }

    public void setRoutePushMute(String routePushMute) {
        this.routePushMute = routePushMute;
    }

}