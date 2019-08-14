package com.flappygo.flappyim.Models.Server;

import java.math.BigDecimal;

public class ChatRoute {
    private String routeId;

    private String routeTime;

    private String routeUser;

    private String routeDevice;

    private String routePushId;

    private String routeServerIp;

    private String routeServerPort;

    private String routeServerTopic;

    private String routeServerGroup;

    private BigDecimal routeOnline;

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

    public String getRouteUser() {
        return routeUser;
    }

    public void setRouteUser(String routeUser) {
        this.routeUser = routeUser == null ? null : routeUser.trim();
    }

    public String getRouteDevice() {
        return routeDevice;
    }

    public void setRouteDevice(String routeDevice) {
        this.routeDevice = routeDevice == null ? null : routeDevice.trim();
    }

    public String getRoutePushId() {
        return routePushId;
    }

    public void setRoutePushId(String routePushId) {
        this.routePushId = routePushId == null ? null : routePushId.trim();
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
}