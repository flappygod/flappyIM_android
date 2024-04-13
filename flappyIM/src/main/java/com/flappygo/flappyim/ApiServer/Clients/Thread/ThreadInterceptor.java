package com.flappygo.flappyim.ApiServer.Clients.Thread;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**********
 * 横切线程任务
 */
public class ThreadInterceptor implements InvocationHandler {

    /******
     * 被代理的对象
     */
    private Object target;

    /******
     * 线程的监听
     */
    private ThreadListener mThreadListener;

    /******
     * 线程的监听
     */
    public interface ThreadListener {

        /****
         * 线程死亡
         * @param thread 线程
         */
        void death(Thread thread);

        /**********
         * 线程开始执行
         *
         * @param thread 线程
         */
        void began(Thread thread);
    }

    /******
     * 设置监听
     * @param listener 监听
     */
    public void setThreadListener(ThreadListener listener) {
        this.mThreadListener = listener;
    }

    /******
     * 设置数据
     * @param target 数据
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    /******
     * 线程执行完毕
     * @param method 方法
     * @param args  参数
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
     * @param method  方法
     * @param args    参数
     */
    private void beforeRun(Method method, Object[] args) {
        if (method.getName().equals("run")) {
            if (mThreadListener != null) {
                mThreadListener.began((Thread) target);
            }
        }
    }


    /******
     * Invoke
     * @param proxy  代理对象
     * @param method 方法
     * @param args   参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        beforeRun(method, args);
        Object ob = method.invoke(target, args);
        afterRun(method, args);
        return ob;
    }
}
