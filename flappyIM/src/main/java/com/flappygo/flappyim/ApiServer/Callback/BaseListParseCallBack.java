package com.flappygo.flappyim.ApiServer.Callback;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_SUCCESS;

import com.flappygo.flappyim.ApiServer.Callback.Parser.BaseListParser;
import com.flappygo.flappyim.ApiServer.Clients.OkHttpAsyncCallback;
import com.flappygo.flappyim.Tools.StringTool;

import java.util.List;

/******
 * 基础回调，返回列表类型的数据
 */
public abstract class BaseListParseCallBack<T> implements OkHttpAsyncCallback {

    /******
     * 解析字符串的class
     */
    private final Class<T> entityClass;

    /******
     * 构造器
     * @param cls class
     */
    public BaseListParseCallBack(Class<T> cls) {
        this.entityClass = cls;
    }


    /******
     * 返回状态false
     * @param message 错误消息
     * @param tag     线程tag
     */
    public abstract void stateFalse(String message, String tag);

    /******
     * 返回数据JSon解析出错
     * @param e 错误
     * @param tag   线程tag
     */
    protected abstract void jsonError(Exception e, String tag);

    /******
     * 解析成功
     * @param t   数据
     * @param tag 线程tag
     */
    protected abstract void stateTrue(List<T> t, String tag);

    /******
     * 网络错误
     * @param e   exception
     * @param tag 线程tag
     */
    protected abstract void netError(Exception e, String tag);


    /******
     * 联网错误的提示
     * @param error 错误
     * @param tag   线程tag
     */
    public void failure(Exception error, String tag) {
        netError(error, tag);
    }

    /******
     * 数据获取成功
     * @param data 数据
     * @param tag  线程tag
     */
    public void success(String data, String tag) {
        //基础解析器为空
        BaseListParser<T> parser = new BaseListParser<T>(data, entityClass);
        //解析成功
        if (parser.isSuccess()) {
            //此处可以对sign进行必要的验证
            if (StringTool.strToInt(parser.getData().getCode(),0)==RESULT_SUCCESS) {
                stateTrue(parser.getData().getData(), tag);
            }
            //状态错误
            else {
                stateFalse(parser.getData().getMsg(), tag);
            }
        } else {
            //json解析出现异常
            jsonError(parser.getException(), tag);
        }

    }
}
