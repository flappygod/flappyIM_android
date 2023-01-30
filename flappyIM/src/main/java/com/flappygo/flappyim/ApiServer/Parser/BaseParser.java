package com.flappygo.flappyim.ApiServer.Parser;

import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.ApiServer.Tools.GsonTool;

import org.json.JSONObject;

/**
 * Created by yang on 2016/5/27.
 */
public class BaseParser<T> {

    //基本的解析对象
    private BaseApiModel<T> baseApiModel;
    //判断是否解析成功
    private boolean parseSuccess;
    //解析错误时候的报错代码
    private Exception exception;


    public BaseParser(String dataStr, Class<T> cls) {
        try {
            baseApiModel = new BaseApiModel<T>();
            //创建
            JSONObject jb = new JSONObject(dataStr);
            //返回码
            baseApiModel.setCode(jb.optString("code"));
            //解析code
            baseApiModel.setMsg(jb.optString("msg"));
            //返回的消息
            baseApiModel.setSign(jb.optString("sign"));
            //返回的总页码
            baseApiModel.setPageCount(jb.optInt("pageCount"));
            //获取到Array数据
            String strData = jb.optString("data");

            //假如是String
            if (cls == String.class) {
                baseApiModel.setData((T) strData);
            }
            //假如是JSON
            else if (cls == JSONObject.class) {
                baseApiModel.setData((T) new JSONObject(strData));
            }
            //假如是对象
            else {
                //解析基本数据
                T t = GsonTool.jsonObjectToModel(strData, cls);
                baseApiModel.setData(t);
            }
            //解析成功
            parseSuccess = true;

        } catch (Exception e) {
            exception = e;
        }
    }

    /*********
     * 判断是否解析成功
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
    public BaseApiModel<T> getBaseApiModel() {
        return baseApiModel;
    }

    /**********
     * 设置解析model
     * @param baseApiModel
     */
    public void setBaseApiModel(BaseApiModel<T> baseApiModel) {
        this.baseApiModel = baseApiModel;
    }


}
