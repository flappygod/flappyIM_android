package com.flappygo.flappyim.ApiServer.Parser;

import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.Tools.StringTool;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang on 2016/5/27.
 */
public class BaseListParser<T> {

    //基本列表的解析对象
    private BaseApiModel<List<T>> baseApiModel;
    //判断是否解析成功
    private boolean parseSuccess;
    //解析错误时候的报错代码
    private Exception exception;
    //判断加密是否正确
    private boolean signRight;


    /***********
     * 基本列表解析对象
     *
     * @param dataStr 数据
     * @param cls     列表中的对象类型
     */
    public BaseListParser(String dataStr, Class<T> cls) {
        try {
            baseApiModel = new BaseApiModel<List<T>>();
            //创建
            JSONObject jb = new JSONObject(dataStr);
            //获取到Array数据
            String strData = StringTool.ToNotNullStrWithDefault(jb.optString("data"),"[]") ;
            //返回码
            baseApiModel.setCode(jb.optString("code"));
            //解析code
            baseApiModel.setMsg(jb.optString("msg"));
            //返回的消息
            baseApiModel.setSign(jb.optString("sign"));
            //返回的总页码
            baseApiModel.setPageCount(jb.optInt("pageCount"));

            signRight = true;

            //解析数组
            JSONArray data = new JSONArray(strData);
            //列表解析
            Gson gson = new Gson();
            List<T> rs = new ArrayList<T>();
            for (int s = 0; s < data.length(); s++) {
                if (cls == String.class) {
                    rs.add((T) data.get(s).toString());
                } else if (cls == JSONObject.class) {
                    rs.add((T) data.getJSONObject(s));
                } else {
                    String str=data.getJSONObject(s).toString();
                    T t = gson.fromJson(str, cls);
                    rs.add(t);
                }
            }
            baseApiModel.setData(rs);
            parseSuccess = true;
        } catch (Exception e) {
            parseSuccess = false;
            exception = e;
        }
    }

    /*********
     * 判断是否解析成功
     *
     * @return
     */
    public boolean isParseSuccess() {
        return parseSuccess;
    }

    public void setParseSuccess(boolean parseSuccess) {
        this.parseSuccess = parseSuccess;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    /*********
     * 获取解析后的参数
     *
     * @return
     */
    public BaseApiModel<List<T>> getBaseApiModel() {
        return baseApiModel;
    }

    /***************
     * 设置数据model
     *
     * @param baseApiModel
     */
    public void setBaseApiModel(BaseApiModel<List<T>> baseApiModel) {
        this.baseApiModel = baseApiModel;
    }

    public boolean isSignRight() {
        return signRight;
    }

    public void setSignRight(boolean signRight) {
        this.signRight = signRight;
    }

}
