package com.flappygo.flappyim.ApiServer.Clients.AsyncTask;


/*************
 * 异步回调接口
 * @param <T>  输出
 */
public interface LXAsyncCallback<T> {

	/*************
	 * 执行异常
	 * 
	 * @param tag
	 *            线程tag
	 * @param e
	 *            错误
	 */
	void failure(Exception e, String tag);

	/****************
	 * 成功
	 * 
	 * @param tag
	 *            线程tag
	 * @param data
	 *            线程需要传递的数据
	 */
	void success(T data, String tag);

}
