package com.flappygo.flappyim.ApiServer.Clients;

/******
 * 请求回调
 */
public interface OkHttpAsyncCallback {

    /******
     * 请求成功
     * @param data 请求成功后的string data
     * @param tag  线程tag
     */
    void success(String data, String tag);

    /******
     * 请求失败
     * @param error 错误
     * @param tag  线程tag
     */
    void failure(Exception error, String tag);

}
