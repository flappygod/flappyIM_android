package com.flappygo.flappyim.ApiServer.Base;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_SUCCESS;
import com.flappygo.lilin.lxhttpclient.Asynctask.LXAsyncCallback;
import com.flappygo.flappyim.ApiServer.Parser.BaseListParser;

import java.util.List;

/******
 * 基础回调，返回列表类型的数据
 */
public abstract class BaseListParseCallBack<T> implements LXAsyncCallback<String> {

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
        if (parser.isParseSuccess()) {
            //此处可以对sign进行必要的验证
            if (parser.getBaseApiModel().getCode().equals(RESULT_SUCCESS)) {
                stateTrue(parser.getBaseApiModel().getData(), tag);
            }
            //状态错误
            else {
                stateFalse(parser.getBaseApiModel().getMsg(), tag);
            }
        } else {
            //json解析出现异常
            jsonError(parser.getException(), tag);
        }

    }
}
