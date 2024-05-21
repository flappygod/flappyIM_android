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

    /******
     * 基本的解析对象
     */
    private BaseApiModel<T> data;

    /******
     * 判断是否解析成功
     */
    private boolean success;

    /******
     * 解析错误时候的报错代码
     */
    private Exception exception;

    /******
     * 基础解析器
     * @param dataStr  json字符串
     * @param tClass   对象
     */
    public BaseParser(String dataStr, Class<T> tClass) {
        try {
            parseData(dataStr, tClass);
            //解析成功
            success = true;
        } catch (Exception ex) {
            //解析失败
            success = false;
            //解析失败
            exception = ex;
        }
    }

    /******
     * 解析数据
     * @param dataStr  数据
     * @param tClass      对象
     */
    @SuppressWarnings("unchecked")
    private void parseData(String dataStr, Class<T> tClass) throws JSONException {
        data = new BaseApiModel<T>();
        //创建
        JSONObject jb = new JSONObject(dataStr);
        //返回码
        data.setCode(jb.optString("code"));
        //解析code
        data.setMsg(jb.optString("msg"));
        //返回的总页码
        data.setPageCount(jb.optInt("pageCount"));
        //获取到Array数据
        String strData = jb.optString("data");
        //假如是String
        if (tClass == String.class) {
            data.setData((T) strData);
        }
        //假如是JSON
        else if (tClass == JSONObject.class) {
            data.setData((T) new JSONObject(strData));
        }
        //假如是对象
        else {
            data.setData(GsonTool.jsonStringToModel(strData, tClass));
        }
    }

    //判断是否解析成功
    public boolean isSuccess() {
        return success;
    }

    //设置解析成功
    public void setSuccess(boolean success) {
        this.success = success;
    }

    //获取Exception
    public Exception getException() {
        return exception;
    }

    //设置Exception
    public void setException(Exception exception) {
        this.exception = exception;
    }

    //获取解析后的参数
    public BaseApiModel<T> getData() {
        return data;
    }

    //设置解析model
    public void setData(BaseApiModel<T> data) {
        this.data = data;
    }

}
