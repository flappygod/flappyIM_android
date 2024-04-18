package com.flappygo.flappyim.Callback;

/******
 * 发送消息的返回信息
 * @param <T> 发送回调
 */
public interface FlappySendCallback<T> {

    /******
     * 成功
     * @param data 数据
     */
    void success(T data);

    /******
     * 失败
     * @param data 数据
     * @param ex   异常
     * @param code 代码
     */
    void failure(T data, Exception ex, int code);

}
