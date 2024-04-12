package com.flappygo.flappyim.ApiServer.Models;

import java.io.Serializable;

/********
 * 基础api对象
 * @param <T> 类型
 */
public class BaseApiModel<T> implements Serializable {


    //请求返回的编码
    private String code;

    //加密秘钥
    private String msg;

    //总页码
    private int pageCount;

    //请求返回的数据内容
    private T data;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
