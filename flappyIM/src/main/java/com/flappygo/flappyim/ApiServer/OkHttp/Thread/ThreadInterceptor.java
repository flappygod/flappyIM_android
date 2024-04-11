package com.flappygo.flappyim.ApiServer.OkHttp.Thread;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**********
 * 
 * 横切
 * @author lijunlin
 */
public class ThreadInterceptor implements InvocationHandler {

	// 被代理的对象
	private Object target;

	// 线程死亡的监听
	private ThreadListener mThreadListener;

	// 线程死亡的监听
	public interface ThreadListener {

		/****
		 * 线程死亡
		 * 
		 * @param thread
		 */
		void death(Thread thread);

		/**********
		 * 线程开始执行
		 * 
		 * @param thread
		 */
		void began(Thread thread);
	}

	// 设置线程死亡的监听
	public void setThreadListener(ThreadListener listener) {
		this.mThreadListener = listener;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	/******
	 * 
	 * 线程执行完毕
	 * 
	 * @param method
	 *            方法
	 * @param args
	 *            参数
	 */
	private void afterRun(Method method, Object[] args) {
		if (method.getName().equals("run")) {
			if (mThreadListener != null) {
				mThreadListener.death((Thread) target);
			}
		}
	}

	/**********
	 * 线程开始执行
	 * 
	 * @param method
	 *            方法
	 * @param args
	 *            参数
	 */
	private void beforeRun(Method method, Object[] args) {
		if (method.getName().equals("run")) {
			if (mThreadListener != null) {
				mThreadListener.began((Thread) target);
			}
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		beforeRun(method, args);
		Object ob = method.invoke(target, args);
		afterRun(method, args);
		return ob;
	}
}
