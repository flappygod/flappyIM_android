package com.flappygo.flappyim.ApiServer.Clients.AsyncTask;

/**************
 * 异步回调执行接口
 * @param <M>  输入
 * @param <T>  输出
 */
public interface  LXAsyncTask<M,T> extends LXAsyncCallback<T> {



	/******************
	 * 线程执行
	 * @param data    传入数据
	 * @param tag     tag
	 * @return T
	 * @throws Exception  错误
     */
	 T run(M data,String tag) throws Exception;


}
