package com.flappygo.flappyim.ApiServer.Base;


import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_SUCCESS;
import com.flappygo.lilin.lxhttpclient.Asynctask.LXAsyncCallback;
import com.flappygo.flappyim.ApiServer.Models.BaseApiModel;
import com.flappygo.flappyim.ApiServer.Parser.BaseParser;


/******
 * 基础解析
 * @param <T> 类型
 */
public abstract class BaseParseCallback<T> implements LXAsyncCallback<String> {

    //实体对象
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
        BaseParser<T> parser = new BaseParser<T>(data, entityClass);
        //解析成功
        if (parser.isParseSuccess()) {
            //此处可以对sign进行必要的验证
            if (parser.getBaseApiModel().getCode().equals(RESULT_SUCCESS)) {
                //假如设置了验证，而且
                stateTrue(parser.getBaseApiModel().getData(), tag);
            }
            //状态错误
            else {
                stateFalse(parser.getBaseApiModel(), tag);
            }
        } else {
            //json解析出现异常
            jsonError(parser.getException(), tag);
        }
    }

}
