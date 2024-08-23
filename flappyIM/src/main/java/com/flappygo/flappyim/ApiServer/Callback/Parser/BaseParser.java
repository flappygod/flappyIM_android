package com.flappygo.flappyim.ApiServer.Callback.Parser;

import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;

import org.json.JSONException;
import org.json.JSONObject;

/******
 * 基础解析器
 * @param <T> 对象类型
 */
public class BaseParser<T> {

    private boolean success;
    private BaseApiModel<T> data;
    private Exception exception;

    /**
     * 基础解析器
     *
     * @param dataStr json字符串
     * @param tClass  对象类型
     */
    public BaseParser(String dataStr, Class<T> tClass) {
        try {
            parseData(dataStr, tClass);
            success = true;
        } catch (Exception ex) {
            success = false;
            exception = ex;
        }
    }

    /**
     * 解析数据
     *
     * @param dataStr 数据
     * @param tClass  对象类型
     */
    private void parseData(String dataStr, Class<T> tClass) throws JSONException {
        data = new BaseApiModel<>();
        JSONObject jsonObject = new JSONObject(dataStr);

        data.setCode(jsonObject.optString("code"));
        data.setMsg(jsonObject.optString("msg"));
        data.setPageCount(jsonObject.optInt("pageCount"));
        String strData = jsonObject.optString("data");

        if (tClass == String.class) {
            data.setData(tClass.cast(strData));
        } else if (tClass == JSONObject.class) {
            data.setData(tClass.cast(new JSONObject(strData)));
        } else {
            data.setData(GsonTool.jsonStrToModel(strData, tClass));
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public BaseApiModel<T> getData() {
        return data;
    }

    public void setData(BaseApiModel<T> data) {
        this.data = data;
    }
}