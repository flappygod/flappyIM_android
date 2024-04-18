package com.flappygo.flappyim.Callback;


/******
 * IM的回调
 * @param <T> 泛型
 */
public interface FlappyIMCallback<T> {

    /******
     * 成功
     * @param data 数据
     */
    void success(T data);

    /******
     * 失败
     * @param ex   异常
     * @param code 代码
     */
    void failure(Exception ex, int code);

}
