package com.flappygo.flappyim.ApiServer.Parser;

import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;
import com.flappygo.flappyim.Tools.StringTool;

import org.json.JSONObject;

import java.util.ArrayList;

import org.json.JSONArray;

import java.util.List;

/******
 * 基础列表解析器
 */
public class BaseListParser<T> {

    //基本列表的解析对象
    private BaseApiModel<List<T>> baseApiModel;

    //判断是否解析成功
    private boolean parseSuccess;

    //解析错误时候的报错代码
    private Exception exception;


    /******
     * 基本列表解析对象
     * @param dataStr 数据
     * @param cls     列表中的对象类型
     */
    @SuppressWarnings("unchecked")
    public BaseListParser(String dataStr, Class<T> cls) {
        try {
            baseApiModel = new BaseApiModel<>();
            //创建
            JSONObject jb = new JSONObject(dataStr);
            //获取到Array数据
            String strData = StringTool.ToNotNullStrWithDefault(jb.optString("data"), "[]");
            //返回码
            baseApiModel.setCode(jb.optString("code"));
            //解析code
            baseApiModel.setMsg(jb.optString("msg"));
            //返回的消息
            baseApiModel.setSign(jb.optString("sign"));
            //返回的总页码
            baseApiModel.setPageCount(jb.optInt("pageCount"));
            //解析数组
            JSONArray data = new JSONArray(strData);
            //列表
            List<T> arrayList = new ArrayList<>();
            //长度
            for (int s = 0; s < data.length(); s++) {
                //字符串
                if (cls == String.class) {
                    arrayList.add((T) data.get(s).toString());
                }
                //json对象
                else if (cls == JSONObject.class) {
                    arrayList.add((T) data.getJSONObject(s));
                }
                //其他对象
                else {
                    String str = data.getJSONObject(s).toString();
                    arrayList.add(GsonTool.jsonStringToModel(str, cls));
                }
            }
            //解析成功
            baseApiModel.setData(arrayList);
            //解析成功
            parseSuccess = true;
        } catch (Exception ex) {
            //解析失败
            parseSuccess = false;
            //错误信息
            exception = ex;
        }
    }


    //获取解析后的参数
    public BaseApiModel<List<T>> getBaseApiModel() {
        return baseApiModel;
    }

    //设置数据model
    public void setBaseApiModel(BaseApiModel<List<T>> baseApiModel) {
        this.baseApiModel = baseApiModel;
    }

    //判断是否解析成功
    public boolean isParseSuccess() {
        return parseSuccess;
    }

    //设置是否解析成功
    public void setParseSuccess(boolean parseSuccess) {
        this.parseSuccess = parseSuccess;
    }

    //获取错误
    public Exception getException() {
        return exception;
    }

    //设置错误
    public void setException(Exception exception) {
        this.exception = exception;
    }


}
