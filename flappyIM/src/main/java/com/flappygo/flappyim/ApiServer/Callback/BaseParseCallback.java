package com.flappygo.flappyim.ApiServer.Callback;


import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_SUCCESS;

import com.flappygo.flappyim.ApiServer.Clients.OkHttpAsyncCallback;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.ApiServer.Callback.Parser.BaseParser;


/******
 * 基础解析
 * @param <T> 类型
 */
public abstract class BaseParseCallback<T> implements OkHttpAsyncCallback {

    /******
     * 实体对象
     */
    private final Class<T> entityClass;


    /******
     * 构造器
     * @param cls class
     */
    public BaseParseCallback(Class<T> cls) {
        this.entityClass = cls;
    }


    /******
     * 返回状态false
     *
     * @param message 错误消息
     * @param tag     线程tag
     */
    protected abstract void stateFalse(BaseApiModel<T> message, String tag);

    /******
     * 返回数据JSon解析出错
     *
     * @param e   错误
     * @param tag 线程tag
     */
    protected abstract void jsonError(Exception e, String tag);

    /******
     * 解析成功
     *
     * @param t   数据
     * @param tag 线程tag
     */
    public abstract void stateTrue(T t, String tag);

    /******
     * 网络错误
     *
     * @param e   exception
     * @param tag 线程tag
     */
    protected abstract void netError(Exception e, String tag);


    /******
     * 联网错误的提示
     *
     * @param error 网络错误的原因
     * @param tag   线程tag
     */
    public void failure(Exception error, String tag) {
        netError(error, tag);
    }

    /******
     * 请求成功
     *
     * @param data 请求成功后的string data
     * @param tag  线程tag
     */
    public void success(String data, String tag) {
        //基础解析器为空
        BaseParser<T> parser = new BaseParser<>(data, entityClass);
        //解析成功
        if (parser.isSuccess()) {
            //状态成功
            if (parser.getData().getCode().equals(RESULT_SUCCESS)) {
                stateTrue(parser.getData().getData(), tag);
            }
            //状态错误
            else {
                stateFalse(parser.getData(), tag);
            }
        } else {
            //json解析出现异常
            jsonError(parser.getException(), tag);
        }
    }

}
