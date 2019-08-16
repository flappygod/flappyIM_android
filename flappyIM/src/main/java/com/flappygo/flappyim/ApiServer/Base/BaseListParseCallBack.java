package com.flappygo.flappyim.ApiServer.Base;


import com.flappygo.flappyim.ApiServer.Parser.BaseListParser;
import com.flappygo.flappyim.ApiServer.Parser.BaseParser;
import com.flappygo.lilin.lxhttpclient.Asynctask.LXAsyncCallback;

import java.util.List;

import static com.flappygo.flappyim.Datas.FlappyIMCode.RESULT_SUCCESS;

/***************************************
 * 基础回调，返回列表类型的数据
 */
public abstract class BaseListParseCallBack<T> implements LXAsyncCallback {
    //class
    private Class<T> entityClass;

    /*************
     * 构造器
     *
     * @param cls class
     */
    public BaseListParseCallBack(Class<T> cls) {
        this.entityClass = cls;
    }


    /**************
     * 返回状态false
     *
     * @param message 错误消息
     * @param tag     线程tag
     */
    public abstract void stateFalse(String message, String tag);

    /****************
     * 返回数据JSon解析出错
     *
     * @param e 错误
     * @param tag   线程tag
     */
    protected abstract void jsonError(Exception e, String tag);

    /**************
     * 解析成功
     *
     * @param t   数据
     * @param tag 线程tag
     */
    protected abstract void stateTrue(List<T> t, String tag);

    /*****************
     * 网络错误
     *
     * @param e   exception
     * @param tag 线程tag
     */
    protected abstract void netError(Exception e, String tag);

    /******************
     * 秘钥验证失败
     *
     * @param e   错误
     * @param tag 线程tag
     */
    protected abstract void signError(Exception e, String tag);


    //联网错误的提示
    public void failure(Exception error, String tag) {
        netError(error, tag);
    }

    /********************
     * 数据获取成功
     *
     * @param data 数据
     * @param tag  线程tag
     */
    public void success(Object data, String tag) {

        //基础解析器为空
        BaseListParser<T> parser = null;
        //进行解析
        if (data instanceof BaseListParser) {
            parser = (BaseListParser) data;
        } else {
            String dataStr = (String) data;
            parser = new BaseListParser<T>(dataStr, entityClass);
        }
        //解析成功
        if (parser.isParseSuccess()) {
            //解析成功
            //此处可以对sign进行必要的验证
            if (parser.getBaseApiModel().getResultCode().equals(RESULT_SUCCESS)) {
                //假如设置了验证，而且
                stateTrue(parser.getBaseApiModel().getResultData(), tag);
            }
            //状态错误
            else {
                stateFalse(parser.getBaseApiModel().getResultMessage(), tag);
            }
        } else {
            //json解析出现异常
            jsonError(parser.getException(), tag);
        }

    }
}
