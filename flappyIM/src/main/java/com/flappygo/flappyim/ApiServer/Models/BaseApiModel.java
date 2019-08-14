package com.flappygo.flappyim.ApiServer.Models;

import java.io.Serializable;

/**
 * Created by yang on 2016/5/27.
 */
public class BaseApiModel<T> implements Serializable {


    //请求返回的编码
    private String resultCode;
    //加密秘钥
    private String resultMessage;
    //加密秘钥
    private String resultSign;
    //总页码
    private int resultTotalPage;
    //请求返回的数据内容
    private T resultData;



    public int getResultTotalPage() {
        return resultTotalPage;
    }

    public void setResultTotalPage(int resultTotalPage) {
        this.resultTotalPage = resultTotalPage;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getResultSign() {
        return resultSign;
    }

    public void setResultSign(String resultSign) {
        this.resultSign = resultSign;
    }

    public T getResultData() {
        return resultData;
    }

    public void setResultData(T resultData) {
        this.resultData = resultData;
    }
}
