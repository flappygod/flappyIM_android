package com.flappygo.flappyim.ApiServer.Callback.Parser;

import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Tools.StringTool;

import org.json.JSONException;

import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;

/******
 * 基础列表解析器
 * @param <T> 对象类型
 */
public class BaseListParser<T> {

    private boolean success;
    private BaseApiModel<List<T>> data;
    private Exception exception;

    /**
     * 基本列表解析对象
     *
     * @param dataStr 数据
     * @param tClass  列表中的对象类型
     */
    public BaseListParser(String dataStr, Class<T> tClass) {
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
        String strData = StringTool.ToNotNullStrWithDefault(
                jsonObject.optString("data"),
                "[]"
        );
        data.setCode(jsonObject.optString("code"));
        data.setMsg(jsonObject.optString("msg"));
        data.setPageCount(jsonObject.optInt("pageCount"));

        JSONArray jsonArray = new JSONArray(strData);
        List<T> arrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            if (tClass == String.class) {
                arrayList.add(tClass.cast(jsonArray.get(i).toString()));
            } else if (tClass == JSONObject.class) {
                arrayList.add(tClass.cast(jsonArray.getJSONObject(i)));
            } else {
                String str = jsonArray.getJSONObject(i).toString();
                arrayList.add(GsonTool.jsonStrToModel(str, tClass));
            }
        }
        this.data.setData(arrayList);
    }

    public BaseApiModel<List<T>> getData() {
        return data;
    }

    public void setData(BaseApiModel<List<T>> data) {
        this.data = data;
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
}