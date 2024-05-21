package com.flappygo.flappyim.Models.Request;

import java.io.Serializable;

/******
 * 定位消息
 */
public class ChatLocation implements Serializable {


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //经纬度
    private String lat;
    private String lng;
    //地址名称
    private String address;



}
